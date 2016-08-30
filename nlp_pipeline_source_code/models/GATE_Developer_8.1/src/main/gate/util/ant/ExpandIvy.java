/*
 * Copyright (c) 1995-2012, The University of Sheffield. See the file
 * COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 * 
 * This file is part of GATE (see http://gate.ac.uk/), and is free software,
 * licenced under the GNU Library General Public License, Version 2, June 1991
 * (in the distribution as file licence.html, and also available at
 * http://gate.ac.uk/gate/licence.html).
 * 
 * Mark A. Greenwood, 12/01/2012
 * 
 * $Id: ExpandIvy.java 17530 2014-03-04 15:57:43Z markagreenwood $
 */

package gate.util.ant;

import gate.util.persistence.PersistenceManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.ivy.Ivy;
import org.apache.ivy.core.LogOptions;
import org.apache.ivy.core.report.ArtifactDownloadReport;
import org.apache.ivy.core.report.ResolveReport;
import org.apache.ivy.core.resolve.ResolveOptions;
import org.apache.ivy.core.retrieve.RetrieveOptions;
import org.apache.ivy.core.settings.IvySettings;
import org.apache.ivy.util.DefaultMessageLogger;
import org.apache.ivy.util.Message;
import org.apache.ivy.util.filter.Filter;
import org.apache.ivy.util.filter.FilterHelper;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Copy;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

/**
 * An ANT task that takes a CREOLE plugin and adds local copies of Ivy managed
 * dependencies. This involves copying JAR files into the plugin directory as
 * well as updating the creole.xml to substitute the IVY elements with
 * appropriate JAR elements.
 */
public class ExpandIvy extends Task {

  private XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());

  private File dir, settings;

  private boolean verbose = false;

  private boolean fully = false;

  /**
   * Get the CREOLE plugin directory being processed.
   * 
   * @return the CREOLE plugin directory being processed.
   */
  public File getDir() {
    return dir;
  }

  /**
   * Set the CREOLE plugin directory to be processed.
   * 
   * @param dir
   *          the CREOLE plugin directory to be processed.
   */
  public void setDir(File dir) {
    this.dir = dir;
  }

  /**
   * Get the Ivy settings file used to control dependency resolution.
   * 
   * @return the Ivy settings file used to control dependency resolution, or
   *         null if the default settings are being used.
   */
  public File getSettings() {
    return dir;
  }

  /**
   * Specifies the settings file used to control dependency resolution.
   * 
   * @param settings
   *          the settings file used to control dependency resolution, or null
   *          to use the default settings.
   */
  public void setSettings(File settings) {
    this.settings = settings;
  }

  /**
   * If true then Ivy will spit out lots of messages while resolving
   * dependencies.
   * 
   * @return if true then Ivy will spit out lots of messages while resolving
   *         dependencies.
   */
  public boolean getVerbose() {
    return verbose;
  }

  /**
   * Controls the log level of Ivy.
   * 
   * @param verbose
   *          if true then Ivy will spit out lots of messages while resolving
   *          dependencies.
   */
  public void setVerbose(boolean verbose) {
    this.verbose = verbose;
  }

  /**
   * Should we fully remove the link to Ivy by removing the dependency XML
   * files.
   * 
   * @return if true Ivy files referenced in creole.xml will be removed after
   *         they have been processed.
   */
  public boolean getFully() {
    return fully;
  }

  /**
   * If true Ivy files referenced in creole.xml will be removed after they have
   * been processed.
   * 
   * @param fully
   *          if true Ivy files referenced in creole.xml will be removed after
   *          they have been processed.
   */
  public void setFully(boolean fully) {
    this.fully = fully;
  }

  @Override
  public void execute() throws BuildException {
    if(dir == null)
      throw new BuildException("Please specify a directory", getLocation());

    if(!dir.exists() || !dir.isDirectory())
      throw new BuildException("Specified directory doesn't exist",
          getLocation());

    File creoleXml = new File(dir, "creole.xml");

    if(!creoleXml.exists())
      throw new BuildException("Supplied directory isn't a CREOLE plugin");

    try {
      // load the creole.xml into a JDOM structure
      SAXBuilder builder = new SAXBuilder();
      Document creoleDoc = builder.build(creoleXml);

      // get the IVY elements from the creole XML file
      List<Element> ivyElts = getIvyElements(creoleDoc);

      if(ivyElts.size() > 0) {
        // if there are some elements to process then we have work to do...

        // get a configured Ivy instance
        Ivy ivy =
            getIvy(settings != null
                ? settings.toURI().toURL()
                : getSettingsURL(), dir);

        // we only want the binary jars so create a filter for them
        Filter filter = FilterHelper.getArtifactTypeFilter(new String[]{"jar"});

        // set up the options for doing a resolve
        ResolveOptions resolveOptions = new ResolveOptions();
        resolveOptions.setArtifactFilter(filter);
        if(!verbose) resolveOptions.setLog(LogOptions.LOG_QUIET);

        // set up the options for doing a retrieve
        RetrieveOptions retrieveOptions = new RetrieveOptions();
        retrieveOptions.setArtifactFilter(filter);
        if(!verbose) retrieveOptions.setLog(LogOptions.LOG_QUIET);

        // an ANT task to handle all the copying
        Copy copyTask;

        for(Element e : ivyElts) {
          // for each IVY element in the creole.xml file....

          // get the location of the ivy file (assume ivy.xml if not specified)
          File ivyFile = getIvyFile(e, creoleXml);

          if(!ivyFile.exists())
            throw new BuildException("Referenced ivy file does not exist: "
                + ivyFile, getLocation());

          // remove the IVY element from the XML
          Element parent = e.getParentElement();
          parent.removeContent(e);

          // get ivy to resolve the dependencies and generate a report
          ResolveReport report =
              ivy.resolve(ivyFile.toURI().toURL(), resolveOptions);

          if(report.getAllProblemMessages().size() > 0)
            throw new BuildException("Unable to resolve all IVY dependencies",
                getLocation());

          // don't do a retrieve but find out what it would actually do.
          // NOTE: we need to do this as a retrieve just returns the number of
          // jar files copied and not what they were which we need to update the
          // creole.xml file
          @SuppressWarnings("unchecked")
          Map<ArtifactDownloadReport, Set<String>> toCopy =
              ivy.getRetrieveEngine().determineArtifactsToCopy(
                  report.getModuleDescriptor().getModuleRevisionId(),
                  ivy.getSettings().substitute(
                      ivy.getSettings().getVariable("ivy.retrieve.pattern")),
                  retrieveOptions);

          for(Map.Entry<ArtifactDownloadReport, Set<String>> entry : toCopy
              .entrySet()) {
            // for each artifact a retrieve would copy....
            ArtifactDownloadReport dlReport =
                entry.getKey();

            for(String destPath : entry.getValue()) {
              // find out where it should end up
              File destFile = new File(destPath);

              // make sure the dir actually exists
              destFile.getParentFile().mkdirs();

              // set up ANT ready to copy from the cache into the plugin dir
              copyTask = new Copy();
              copyTask.setProject(getProject());
              copyTask.setLocation(getLocation());
              copyTask.setTaskName(getTaskName());
              copyTask.setFile(dlReport.getLocalFile());
              copyTask.setTofile(destFile);
              copyTask.init();

              // do the actual copy
              copyTask.perform();

              // add a new JAR element to creole.xml pointing at the newly added
              // jar
              Element jarElement =
                  new Element("JAR").setText(PersistenceManager
                      .getRelativePath(dir.toURI().toURL(), destFile.toURI()
                          .toURL()));
              parent.addContent(jarElement);
            }

            if(fully && !ivyFile.delete()) ivyFile.deleteOnExit();
          }
        }

        // now we have finished write the new XML back to creole.xml
        outputter.output(creoleDoc, new FileWriter(creoleXml));
      }
    } catch(Exception e) {
      // if anything goes wrong just re-throw the exception
      throw new BuildException(e);
    }
  }

  /**
   * Processes the specified creole.xml file to extract all the &lt;IVY&gt;
   * elements
   * 
   * @param creoleXML
   *          the URL of the creole.xml file to process
   * @return a list of the &lt;IVY&gt; XML elements
   */
  public static List<Element> getIvyElements(URL creoleXML)
      throws JDOMException, IOException {
    // load the creole.xml into a JDOM structure
    SAXBuilder builder = new SAXBuilder();
    Document doc = builder.build(creoleXML);
    return getIvyElements(doc);
  }

  /**
   * Processes the specified XML document file to extract all the &lt;IVY&gt;
   * elements
   * 
   * @param doc
   *          the XML document to process
   * @return a list of the &lt;IVY&gt; XML elements
   */
  @SuppressWarnings("unchecked")
  public static List<Element> getIvyElements(Document doc) throws JDOMException {
    // use XPath to find all the IVY elements
    XPath jarXPath =
        XPath.newInstance("//*[translate(local-name(), 'ivy', 'IVY') = 'IVY']");
    return jarXPath.selectNodes(doc);
  }

  /**
   * Turns an &lt;IVY&gt; XML element into a File instance by resolving relative
   * to the creole.xml file.
   * 
   * @param element
   *          the &lt;IVY&gt; element to convert
   * @param creoleXML
   *          the creole.xml file to resolve relative to
   * @return a File instance pointing to the Ivy file specified by the XML
   *         element
   */
  public static File getIvyFile(Element element, File creoleXML) {
    return new File(creoleXML.getParentFile(), getIvyPath(element));
  }

  /**
   * Retrieve the path to the Ivy file as specified in the XML element. If no
   * path is given use the default of 'ivy.xml'.
   * 
   * @param element
   *          the &lt;IVY&gt; XML element to process
   * @return the path to the Ivy file as specified in the XML element, defaults
   *         to 'ivy.xml'.
   */
  public static String getIvyPath(Element element) {
    String ivyText = element.getTextTrim();
    if(ivyText == null || ivyText.equals("")) ivyText = "ivy.xml";
    return ivyText;
  }

  public static Ivy getIvy() throws ParseException, IOException {
    return getIvy(null, null);
  }

  public static Ivy getIvy(File dir) throws ParseException, IOException {
    return getIvy(null, dir);
  }

  public static Ivy getIvy(URL settings) throws ParseException, IOException {
    return getIvy(settings, null);
  }

  public static Ivy getIvy(URL settings, File dir) throws ParseException,
      IOException {
    IvySettings ivySettings = new IvySettings();

    if(settings != null)
      ivySettings.load(settings);
    else ivySettings.loadDefault();

    if(dir != null) ivySettings.setBaseDir(dir);

    // get an instance of ivy
    return Ivy.newInstance(ivySettings);
  }

  /**
   * Attempts to find a custom Ivy settings file to use instead of the default
   * configuration. This looks first for a system property
   * <code>ivy.settings.file</code> and then <code>ivy.settings.url</code>. If
   * neither exist or can be converted to a valid URL then the method returns
   * null.
   * 
   * @return the URL of the settings file to use or null if one was not
   *         specified or could not be correctly converted.
   */
  public static URL getSettingsURL() {

    String val = System.getProperty("ivy.settings.file");
    if(val != null) {
      try {
        File file = new File(val);
        if(file.exists() && file.isFile() && file.canRead())
          return file.toURI().toURL();
      } catch(Exception e) {
        // ignore this and try the URL
        System.err.println("Ivalid ivy.settings.file will be ignored: " + val);
      }
    }

    val = System.getProperty("ivy.settings.url");
    if(val != null) {
      try {
        return new URL(val);
      } catch(Exception e) {
        // ignore this
        System.err.println("Ivalid ivy.settings.url will be ignored: " + val);
      }
    }

    // neither of the system properties were helpful so return null
    return null;
  }

  static {
    // this seems to be the only way to suppress the loading settings message
    Message.setDefaultLogger(new DefaultMessageLogger(Message.MSG_ERR));
  }
}