/*
 * Copyright (c) 1995-2012, The University of Sheffield. See the file
 * COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 * 
 * This file is part of GATE (see http://gate.ac.uk/), and is free software,
 * licenced under the GNU Library General Public License, Version 2, June 1991
 * (in the distribution as file licence.html, and also available at
 * http://gate.ac.uk/gate/licence.html).
 * 
 * Mark A. Greenwood, 21/01/2012
 * 
 * $Id: SyncEclipse.java 15185 2012-01-22 16:10:41Z markagreenwood $
 */

package gate.util.ant;

import gate.util.ExtensionFileFilter;
import gate.util.Files;
import gate.util.persistence.PersistenceManager;

import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

/**
 * An ANT task that syncs an Eclipse classpath file against a folder of jar
 * files. Once the task has completed all jar files in the specified folder will
 * be mentioned within the Eclipse classpath file and any jars that were
 * mentioned in the classpath file as being with the specified folder and which
 * no longer exist will have been removed.
 */
public class SyncEclipse extends Task {

  private XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat()
      .setIndent("\t"));

  private File dir, classpath;

  private boolean recursive = true;
  
  private boolean verbose = false;

  public boolean isRecursive() {
    return recursive;
  }

  public void setRecursive(boolean recursive) {
    this.recursive = recursive;
  }
  
  public boolean isVerbose() {
    return verbose;
  }

  public void setVerbose(boolean verbose) {
    this.verbose = verbose;
  }

  public File getDir() {
    return dir;
  }

  public void setDir(File dir) {
    this.dir = dir;
  }

  public File getClasspathFile() {
    return classpath;
  }

  public void setClasspathFile(File classpath) {
    this.classpath = classpath;
  }

  @Override
  public void execute() throws BuildException {
    if(dir == null)
      throw new BuildException("Please specify a directory", getLocation());

    if(!dir.exists() || !dir.isDirectory())
      throw new BuildException("Specified directory doesn't exist",
          getLocation());

    FileFilter ff = new ExtensionFileFilter("JAR Files", "jar");

    if(classpath == null)
      classpath = new File(getProject().getBaseDir(), ".classpath");

    if(!classpath.exists() || !classpath.isFile())
      throw new BuildException("Eclipse classpath file can't be located",
          getLocation());

    Set<File> jars = new HashSet<File>();
    for(File f : Arrays.asList(recursive
        ? Files.listFilesRecursively(dir, ff)
        : dir.listFiles(ff))) {
      if(!f.isDirectory()) jars.add(f);
    }

    boolean dirty = false;

    try {
      SAXBuilder builder = new SAXBuilder();
      Document classpathDoc = builder.build(classpath);

      @SuppressWarnings("unchecked")
      List<Element> libEntries =
          XPath.newInstance("/classpath/classpathentry[@kind='lib']")
              .selectNodes(classpathDoc);

      String relative = PersistenceManager.getRelativePath(classpath.toURI().toURL(), dir.toURI().toURL());
            
      for(Element e : libEntries) {
        String path = e.getAttributeValue("path");
        if(path.startsWith(relative)) {
          File f = new File(classpath.getParentFile(), path);
          if(jars.contains(f)) {
            if (verbose) System.out.println("KEEPING: " + path);
            jars.remove(f);
          } else {
            dirty = true;
            if (verbose) System.out.println("REMOVED: " + path);
            Element parent = e.getParentElement();
            parent.removeContent(e);
          }
        }
      }

      if(!jars.isEmpty()) {
        dirty = true;
        for(File f : jars) {
          String path =
              PersistenceManager.getRelativePath(classpath.toURI().toURL(), f
                  .toURI().toURL());
          if (verbose) System.out.println("ADDED: " + path);
          Element libElement =
              new Element("classpathentry").setAttribute("kind", "lib")
                  .setAttribute("exported", "true").setAttribute("path", path);
          classpathDoc.getRootElement().addContent(libElement);
        }
      }

      if(dirty) {
        outputter.output(classpathDoc, new FileWriter(classpath));
      }

    } catch(Exception e) {
      throw new BuildException(e, getLocation());
    }
  }
}
