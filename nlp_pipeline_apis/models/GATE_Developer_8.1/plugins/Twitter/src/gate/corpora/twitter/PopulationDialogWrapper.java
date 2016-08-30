/*
 *  Copyright (c) 1995-2014, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *  
 *  $Id: PopulationDialogWrapper.java 18481 2014-12-02 20:35:45Z ian_roberts $
 */
package gate.corpora.twitter;

import gate.gui.ListEditorDialog;
import gate.gui.MainFrame;
import gate.swing.XJFileChooser;
import gate.util.ExtensionFileFilter;
import gate.util.Strings;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import org.apache.log4j.Logger;


public class PopulationDialogWrapper  {
  protected JDialog dialog;
  protected PopulationConfig config;
  private JTextField encodingField;
  private JCheckBox oneDocPerTweetCheckbox;
  private JCheckBox entitiesCheckbox;
  private XJFileChooser chooser;
  private List<URL> fileUrls;
  private ListEditor featureKeysEditor, contentKeysEditor;

  public static final String RESOURCE_CODE = "twitter.population";
  private static final Logger logger = Logger.getLogger(PopulationDialogWrapper.class.getName());

  
  public PopulationDialogWrapper() {
    config = new PopulationConfig();
    
    dialog = new JDialog(MainFrame.getInstance(), "Populate from Twitter JSON", true);
    MainFrame.getGuiRoots().add(dialog);
    dialog.getContentPane().setLayout(new BoxLayout(dialog.getContentPane(), BoxLayout.Y_AXIS));
    
    GridBagLayout formLayout = new GridBagLayout();
    JPanel formPanel = new JPanel(formLayout);
    GridBagConstraints labelConstraints = new GridBagConstraints();
    labelConstraints.gridx = 0;
    labelConstraints.insets = new Insets(3, 3, 0, 3);
    labelConstraints.anchor = GridBagConstraints.LINE_END;
    
    GridBagConstraints componentConstraints = new GridBagConstraints();
    componentConstraints.gridx = 1;
    componentConstraints.gridwidth = GridBagConstraints.REMAINDER;
    componentConstraints.insets = new Insets(3, 3, 0, 3);
    componentConstraints.anchor = GridBagConstraints.LINE_START;
    componentConstraints.weightx = 1.0;
    componentConstraints.fill = GridBagConstraints.HORIZONTAL;
    
    
    JLabel encodingLabel = new JLabel("Encoding");
    encodingField = new JTextField(config.getEncoding());
    formLayout.setConstraints(encodingLabel, labelConstraints);
    formPanel.add(encodingLabel);
    formLayout.setConstraints(encodingField, componentConstraints);
    formPanel.add(encodingField);

    // don't need horizontal fill for checkboxes
    componentConstraints.fill = GridBagConstraints.NONE;
    
    JLabel odptCheckboxLabel = new JLabel("One document per tweet");
    odptCheckboxLabel.setToolTipText("If unchecked, one document per file");
    oneDocPerTweetCheckbox = new JCheckBox();
    oneDocPerTweetCheckbox.setToolTipText("If unchecked, one document per file");
    oneDocPerTweetCheckbox.setSelected(config.getOneDocCheckbox());
    formLayout.setConstraints(odptCheckboxLabel, labelConstraints);
    formPanel.add(odptCheckboxLabel);
    
    formLayout.setConstraints(oneDocPerTweetCheckbox, componentConstraints);
    formPanel.add(oneDocPerTweetCheckbox);
    
    JLabel entitiesCheckboxLabel = new JLabel("Annotations for \"entities\"");
    entitiesCheckboxLabel.setToolTipText("Create annotations based on the \"entities\" property of the JSON");
    entitiesCheckbox = new JCheckBox();
    entitiesCheckbox.setToolTipText("Create annotations based on the \"entities\" property of the JSON");
    entitiesCheckbox.setSelected(config.isProcessEntities());
    formLayout.setConstraints(entitiesCheckboxLabel, labelConstraints);
    formPanel.add(entitiesCheckboxLabel);
    
    formLayout.setConstraints(entitiesCheckbox, componentConstraints);
    formPanel.add(entitiesCheckbox);

    // restore horizontal fill
    componentConstraints.fill = GridBagConstraints.HORIZONTAL;

    JLabel contentKeysLabel = new JLabel("Content keys");
    contentKeysLabel.setToolTipText("JSON key paths to be turned into DocumentContent");    
    contentKeysEditor = new ListEditor(config.getContentKeys());
    contentKeysEditor.setToolTipText("JSON key paths to be turned into DocumentContent");
    formLayout.setConstraints(contentKeysLabel, labelConstraints);
    formPanel.add(contentKeysLabel);
    formLayout.setConstraints(contentKeysEditor, componentConstraints);
    formPanel.add(contentKeysEditor);
    
    
    JLabel featureKeysLabel = new JLabel("Feature keys");
    featureKeysLabel.setToolTipText("JSON key paths to be turned into Tweet annotation features");    
    featureKeysEditor = new ListEditor(config.getFeatureKeys());
    featureKeysEditor.setToolTipText("JSON key paths to be turned into Tweet annotation features");
    formLayout.setConstraints(featureKeysLabel, labelConstraints);
    formPanel.add(featureKeysLabel);
    formLayout.setConstraints(featureKeysEditor, componentConstraints);
    formPanel.add(featureKeysEditor);
    
    dialog.add(formPanel);
    dialog.add(Box.createVerticalStrut(4));

    Box configPersistenceBox = Box.createHorizontalBox();
    configPersistenceBox.add(Box.createHorizontalGlue());
    JButton loadConfigButton = new JButton("Load configuration");
    loadConfigButton.setToolTipText("Replace the configuration above with a previously saved one");
    loadConfigButton.addActionListener(new LoadConfigListener(this));
    configPersistenceBox.add(loadConfigButton);
    configPersistenceBox.add(Box.createHorizontalGlue());
    JButton saveConfigButton = new JButton("Save configuration");
    saveConfigButton.setToolTipText("Save the configuration above for re-use");
    saveConfigButton.addActionListener(new SaveConfigListener(this));
    configPersistenceBox.add(saveConfigButton);
    configPersistenceBox.add(Box.createHorizontalGlue());
    
    dialog.add(configPersistenceBox);
    dialog.add(Box.createVerticalStrut(5));
    
    dialog.add(new JSeparator(SwingConstants.HORIZONTAL));
    dialog.add(Box.createVerticalStrut(2));
    
    chooser = MainFrame.getFileChooser();
    chooser.setResource(RESOURCE_CODE);
    chooser.setFileSelectionMode(XJFileChooser.FILES_ONLY);
    chooser.setMultiSelectionEnabled(true);
    chooser.setDialogTitle("Select a Twitter JSON file");
    chooser.resetChoosableFileFilters();
    chooser.setAcceptAllFileFilterUsed(false);
    ExtensionFileFilter filter = new ExtensionFileFilter("Twitter JSON files (*.json)", "json");
    chooser.addChoosableFileFilter(filter);
    chooser.setFileFilter(filter);
    chooser.setApproveButtonText("Populate");
    chooser.addActionListener(new PopulationDialogListener(this));

    dialog.add(chooser);
    dialog.pack();
    dialog.setLocationRelativeTo(dialog.getOwner());
    dialog.setVisible(true);
  }
  
  
  public List<URL> getFileUrls() throws MalformedURLException {
    return this.fileUrls;
  }

  public PopulationConfig getConfig() {
    return this.config;
  }
  
  protected void setNewConfig(PopulationConfig newConfig) {
    this.config = newConfig;
    this.updateGui();
  }
  
  protected void updateConfig() {
    this.config.setTweetsPerDoc(this.oneDocPerTweetCheckbox.isSelected() ? 1 : 0);
    this.config.setProcessEntities(this.entitiesCheckbox.isSelected());
    this.config.setContentKeys(this.contentKeysEditor.getValues());
    this.config.setFeatureKeys(this.featureKeysEditor.getValues());
    this.config.setEncoding(this.encodingField.getText());
  }
  
  
  protected void updateGui() {
    this.encodingField.setText(config.getEncoding());
    this.contentKeysEditor.setValues(config.getContentKeys());
    this.featureKeysEditor.setValues(config.getFeatureKeys());
    this.oneDocPerTweetCheckbox.setSelected(config.getOneDocCheckbox());
    this.entitiesCheckbox.setSelected(config.isProcessEntities());
  }
  
  
  protected void loadFile()  {
    updateConfig();

    try {
      this.fileUrls = new ArrayList<URL>();
      for (File file : this.chooser.getSelectedFiles()) {
        this.fileUrls.add(file.toURI().toURL());
      }
    }
    catch (MalformedURLException e) {
      logger.warn("Error loading file", e);
    }
    finally {
      this.dialog.dispose();
    }
  }

  
  protected void cancel() {
    this.dialog.dispose();
  }
  
}


class PopulationDialogListener implements ActionListener {

  private PopulationDialogWrapper dialog;
  
  public PopulationDialogListener(PopulationDialogWrapper dialog) {
    this.dialog = dialog;
  }

  
  @Override
  public void actionPerformed(ActionEvent event) {
    if (event.getActionCommand().equals(XJFileChooser.APPROVE_SELECTION)){
      this.dialog.loadFile();
    }
    else {
      this.dialog.cancel();
    }
  }
  
}


class ListEditor extends JPanel {
  private static final long serialVersionUID = -1578463259277343578L;

  private JButton listButton;
  private ListEditorDialog listEditor;
  private List<String> values;
  private JTextField field;
  
  @Override
  public void setToolTipText(String text) {
    super.setToolTipText(text);
    field.setToolTipText(text);
  }
  
  
  public ListEditor(List<String> initialValues) {
    field = new JTextField();
    values = initialValues;
    field.setText(Strings.toString(initialValues));
    field.setEditable(false);
        
    listEditor = new ListEditorDialog(SwingUtilities.getAncestorOfClass(
        Window.class, this), values, List.class, String.class.getName());

    listButton = new JButton(MainFrame.getIcon("edit-list"));
    listButton.setToolTipText("Edit the list");
    
    listButton.addActionListener(new ActionListener() {
      @SuppressWarnings("unchecked")
      public void actionPerformed(ActionEvent e) {
        List<?> returnedList = listEditor.showDialog();
        if(returnedList != null) {
          values = (List<String>) returnedList;
          field.setText(Strings.toString(returnedList));
        }
      }
    });
    
    this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
    this.add(field);
    this.add(listButton);
  }
  
  
  public List<String> getValues() {
    return this.values;
  }
  
  public void setValues(List<String> values) {
    this.values = values;
    // re-create the list editor with new values
    if(listEditor != null) {
      listEditor.dispose();
    }
    listEditor = new ListEditorDialog(SwingUtilities.getAncestorOfClass(
            Window.class, this), values, List.class, String.class.getName());
    this.field.setText(Strings.toString(values));
  }
  
}
