/*
 *  Copyright (c) 1995-2014, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *  
 *  $Id: PopulationConfig.java 18422 2014-10-31 17:22:29Z ian_roberts $
 */
package gate.corpora.twitter;


import gate.Gate;
import gate.gui.MainFrame;
import gate.swing.XJFileChooser;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import org.apache.log4j.Logger;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.StaxDriver;


public class PopulationConfig   {
  public static final String RESOURCE_CODE = "twitter.population.config";

  private String encoding;
  private List<String> featureKeys, contentKeys;
  private int tweetsPerDoc;
  private boolean processEntities = true;
  
  
  public boolean getOneDocCheckbox() {
    return this.tweetsPerDoc == 1;
  }
  
  public int getTweetsPerDoc() {
    return this.tweetsPerDoc;
  }

  public void setTweetsPerDoc(int tpd) {
    this.tweetsPerDoc = tpd;
  }
  
  public boolean isProcessEntities() {
    return processEntities;
  }
  
  public void setProcessEntities(boolean entities) {
    this.processEntities = entities;
  }
  
  public String getEncoding() {
    return this.encoding;
  }
  
  public void setEncoding(String encoding) {
    this.encoding = encoding;
  }
  
  public List<String> getFeatureKeys() {
    return this.featureKeys;
  }
  
  public void setFeatureKeys(List<String> keys) {
    this.featureKeys = keys;
  }

  public List<String> getContentKeys() {
    return this.contentKeys;
  }
  
  public void setContentKeys(List<String> keys) {
    this.contentKeys = keys;
  }

  /** 
   * Constructor with defaults.
   */
  public PopulationConfig() {
    this.tweetsPerDoc = 1;
    // Default changed from 0 (all in one doc) to 1 as agreed.
    this.encoding = TweetUtils.DEFAULT_ENCODING;
    this.contentKeys = Arrays.asList(TweetUtils.DEFAULT_CONTENT_KEYS);
    this.featureKeys = Arrays.asList(TweetUtils.DEFAULT_FEATURE_KEYS);
  }
  

  /**
   * Constructor with all options.
   * @param tpd
   * @param encoding
   * @param cks
   * @param fks
   */
  public PopulationConfig(int tpd, boolean entities, String encoding, List<String> cks, List<String> fks) {
    this.tweetsPerDoc = tpd;
    this.processEntities = entities;
    this.encoding = encoding;
    this.contentKeys = cks;
    this.featureKeys = fks;
  }
  
  
  public void reload(File file) {
    PopulationConfig source = PopulationConfig.load(file);
    this.tweetsPerDoc = source.tweetsPerDoc;
    this.processEntities = source.processEntities;
    this.encoding = source.encoding;
    this.contentKeys = source.contentKeys;
    this.featureKeys = source.featureKeys;
  }
  
  public void reload(URL url) {
    PopulationConfig source = PopulationConfig.load(url);
    this.tweetsPerDoc = source.tweetsPerDoc;
    this.processEntities = source.processEntities;
    this.encoding = source.encoding;
    this.contentKeys = source.contentKeys;
    this.featureKeys = source.featureKeys;
  }
  
  
  public static PopulationConfig load(File file) {
    XStream xstream = new XStream(new StaxDriver());
    // setClassLoader needed so XStream finds plugin classes
    xstream.setClassLoader(Gate.getClassLoader());
    return (PopulationConfig) xstream.fromXML(file);
  }

  public static PopulationConfig load(URL url) {
    XStream xstream = new XStream(new StaxDriver());
    // setClassLoader needed so XStream finds plugin classes
    xstream.setClassLoader(Gate.getClassLoader());
    return (PopulationConfig) xstream.fromXML(url);
  }

  
  public void saveXML(File file) throws IOException {
    XStream xstream = new XStream(new StaxDriver());
    //TODO slap an XML prolog on the front
    PrettyPrintWriter ppw = new PrettyPrintWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
    xstream.marshal(this, ppw);
    ppw.close();
  }
  
}


class LoadConfigListener implements ActionListener {
  PopulationDialogWrapper wrapper;
  
  public LoadConfigListener(PopulationDialogWrapper wrapper) {
    this.wrapper = wrapper;
  }

  @Override
  public void actionPerformed(ActionEvent arg0) {
    XJFileChooser chooser = new XJFileChooser();
    chooser.setResource(PopulationConfig.RESOURCE_CODE);
    chooser.setDialogTitle("Load XML configuration");
    chooser.setFileSelectionMode(XJFileChooser.FILES_ONLY);
    int chosen = chooser.showOpenDialog(this.wrapper.dialog);
    if (chosen == XJFileChooser.APPROVE_OPTION) {
      wrapper.setNewConfig(PopulationConfig.load(chooser.getSelectedFile()));
    }
  }
}


class SaveConfigListener implements ActionListener {
  PopulationDialogWrapper wrapper;
  
  private static final Logger logger = Logger.getLogger(SaveConfigListener.class.getName());
  
  public SaveConfigListener(PopulationDialogWrapper wrapper) {
    this.wrapper = wrapper;
  }

  @Override
  public void actionPerformed(ActionEvent event) {
    XJFileChooser chooser = new XJFileChooser();
    chooser.setResource(PopulationConfig.RESOURCE_CODE);
    chooser.setDialogTitle("Save configuration as XML");
    chooser.setFileSelectionMode(XJFileChooser.FILES_ONLY);
    int chosen = chooser.showSaveDialog(this.wrapper.dialog);
    if (chosen == XJFileChooser.APPROVE_OPTION) {
      try {
        wrapper.updateConfig();
        wrapper.config.saveXML(chooser.getSelectedFile());
      } 
      catch(IOException exception) {
        logger.warn("Error saving population config", exception);
      }
    }
  }
  
  
}


