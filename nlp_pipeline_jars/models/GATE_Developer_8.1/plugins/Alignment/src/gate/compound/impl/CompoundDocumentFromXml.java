package gate.compound.impl;

import gate.Factory;
import gate.FeatureMap;
import gate.Gate;
import gate.Resource;
import gate.alignment.Alignment;
import gate.creole.ResourceInstantiationException;
import gate.util.BomStrippingInputStreamReader;
import gate.util.GateRuntimeException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;

/**
 * Those compound documents saved in a single xml document can be
 * reloaded in GATE using this LR.
 *
 * @author niraj
 */
public class CompoundDocumentFromXml extends CompoundDocumentImpl {

  private static final long serialVersionUID = 8114328411647768889L;

  // files that need to be deleted at the end
  private List<File> filesToDelete = new ArrayList<File>();

  /** Initialise this resource, and return it. */
  public Resource init() throws ResourceInstantiationException {
    // set up the source URL and create the content
    if(compoundDocumentUrl == null) {
      throw new ResourceInstantiationException(
              "The compoundDocumentUrl is null.");
    }

    BufferedReader br = null;
    try {
      StringBuilder xmlString = new StringBuilder();
       br = new BomStrippingInputStreamReader(compoundDocumentUrl
              .openStream(), getEncoding());
      String line = br.readLine();
      while(line != null) {
        xmlString.append("\n").append(line);
        line = br.readLine();
      }

      StringReader reader = new StringReader(xmlString.toString());
      com.thoughtworks.xstream.XStream xstream = new com.thoughtworks.xstream.XStream(
              new com.thoughtworks.xstream.io.xml.StaxDriver());

      // asking the xstream library to use gate class loader
      xstream.setClassLoader(Gate.getClassLoader());

      // reading the xml object
      @SuppressWarnings("unchecked")
      Map<String, Object> globalMap = (HashMap<String, Object>)xstream
              .fromXML(reader);

      // now we read individual information
      @SuppressWarnings("unchecked")
      Map<String, String> docXmls = (HashMap<String, String>)globalMap
              .get("docXmls");
      
      @SuppressWarnings("unchecked")
      Map<String, Object> features = (Map<String, Object>)globalMap
              .get("feats");

      String encoding = (String)features.get("encoding");
      super.setEncoding(encoding);

      File tempFile = File.createTempFile("example", ".xml");
      File tempFolder = new File(tempFile.getParentFile(), "temp"
              + Gate.genSym());

      if(!tempFolder.exists() && !tempFolder.mkdirs()) {
        throw new GateRuntimeException("Temporary folder "
                + tempFolder.getAbsolutePath() + " could not be created");
      }

      tempFile.deleteOnExit();
      tempFolder.deleteOnExit();

      URL sourceUrl = null;
      List<String> docIDs = new ArrayList<String>();
      for(String id : docXmls.keySet()) {
        docIDs.add(id);
        File newFile = new File(tempFolder, "X." + id + ".xml");
        filesToDelete.add(newFile);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(newFile), encoding));
        bw.write(docXmls.get(id));
        bw.flush();
        bw.close();
        sourceUrl = newFile.toURI().toURL();
      }
      super.setDocumentIDs(docIDs);
      super.setSourceUrl(sourceUrl);

      String name = (String)features.get("name");
      features.remove("name");
      FeatureMap fets = Factory.newFeatureMap();
      for(String s : features.keySet()) {
        fets.put(s, features.get(s));
      }

      this.setName(name);
      super.init();

      FeatureMap docFeatures = (FeatureMap)globalMap.get("docFeats");
      for(Object key : docFeatures.keySet()) {
        Object value = docFeatures.get(key);
        if(value instanceof Alignment) {
          ((Alignment)value).setSourceDocument(this);
        }
      }

      setFeatures(docFeatures);
      br.close();
    }
    catch(UnsupportedEncodingException uee) {
      throw new ResourceInstantiationException(uee);
    }
    catch(IOException ioe) {
      throw new ResourceInstantiationException(ioe);
    }
    finally {
      IOUtils.closeQuietly(br);
    }
    return this;
  } // init()

  /**
   * Cleanup the document
   */
  @Override
  public void cleanup() {
    super.cleanup();

    // delete all the temporarily created files
    for(File fileToDelete : filesToDelete) {
      fileToDelete.delete();
    }
  }

  /**
   * Url of the compound document
   */
  private URL compoundDocumentUrl;

  public URL getCompoundDocumentUrl() {
    return compoundDocumentUrl;
  }

  public void setCompoundDocumentUrl(URL compoundDocumentUrl) {
    this.compoundDocumentUrl = compoundDocumentUrl;
  }
}