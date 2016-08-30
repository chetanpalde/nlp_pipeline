/*
 *  Copyright (c) 1995-2012, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 *  SchemaFeaturesEditor.java
 *
 *  Valentin Tablan, Sep 11, 2007
 *
 *  $Id: SchemaFeaturesEditor.java 17874 2014-04-18 11:19:47Z markagreenwood $
 */
package gate.gui.annedit;

import gate.FeatureMap;
import gate.creole.AnnotationSchema;
import gate.creole.FeatureSchema;
import gate.swing.JChoice;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * A GUI component for editing a feature map based on a feature schema object.
 */
@SuppressWarnings("serial")
public class SchemaFeaturesEditor extends JPanel{

  protected static enum FeatureType{
    /**
     * Type for features that have a range of possible values 
     */
    nominal, 
    
    /**
     * Type for boolean features.
     */
    bool, 
    
    /**
     * Type for free text features.
     */
    text};

  protected class FeatureEditor{
    
    /**
     * Constructor for nominal features
     * @param featureName
     * @param values
     * @param defaultValue
     */
    public FeatureEditor(String featureName, String[] values, 
            String defaultValue){
      this.featureName = featureName;
      this.type = FeatureType.nominal;
      this.values = values;
      this.defaultValue = defaultValue;
      buildGui();
    }
    
    /**
     * Constructor for boolean features
     * @param featureName
     * @param defaultValue
     */
    public FeatureEditor(String featureName, Boolean defaultValue){
      this.featureName = featureName;
      this.type = FeatureType.bool;
      if (defaultValue != null )
          this.defaultValue = defaultValue.booleanValue() ? BOOLEAN_TRUE : BOOLEAN_FALSE;
      else
          this.defaultValue = null;
      this.values = new String[]{BOOLEAN_FALSE, BOOLEAN_TRUE};
      buildGui();
    }
    
    /**
     * Constructor for plain text features
     * @param featureName
     * @param defaultValue
     */
    public FeatureEditor(String featureName, String defaultValue){
      this.featureName = featureName;
      this.type = FeatureType.text;
      this.defaultValue = defaultValue;
      this.values = null;
      buildGui();
    }
    
    /**
     * Builds the GUI according to the internally stored values.
     */
    protected void buildGui(){
      //prepare the action listener
      sharedActionListener = new ActionListener(){
        @Override
        public void actionPerformed(ActionEvent e) {          
          Object newValue = null;
          if(e.getSource() == checkbox){
            newValue = new Boolean(checkbox.isSelected());
          }else if(e.getSource() == textField){
            newValue = textField.getText();
          }else if(e.getSource() == jchoice){
            newValue = jchoice.getSelectedItem();
            if(newValue != null && type == FeatureType.bool){
              //convert eh new value to Boolean
              newValue = new Boolean(BOOLEAN_TRUE == newValue);
            }
          }else if(e.getSource() == SchemaFeaturesEditor.this){
            //synthetic event
            newValue = getValue();
          }
          
          if(featureMap != null && e.getSource() != SchemaFeaturesEditor.this){
            if(newValue != null){
              if(newValue != featureMap.get(featureName)){ 
                featureMap.put(featureName, newValue);
              }
            }else{
              featureMap.remove(featureName);
            }
          }
          
          
          //if the change makes this feature map non schema-compliant,
          //highlight this feature editor
          if(required && newValue == null){
            if(getGui().getBorder() != highlightBorder){ 
              getGui().setBorder(highlightBorder);
            }
          }else{
            if(getGui().getBorder() != defaultBorder){
              getGui().setBorder(defaultBorder);
            }
          }
        }
      };
      
      //build the empty shell
      gui = new JPanel();
      gui.setAlignmentX(Component.LEFT_ALIGNMENT);
      gui.setLayout(new BoxLayout(gui, BoxLayout.Y_AXIS));
      switch(type) {
        case nominal:
          //use JChoice
          jchoice = new JChoice<String>(values);
          jchoice.setDefaultButtonMargin(new Insets(0, 2, 0, 2));
          jchoice.setMaximumFastChoices(20);
          jchoice.setMaximumWidth(300);
          jchoice.setSelectedItem(value);
          jchoice.addActionListener(sharedActionListener);
          gui.add(jchoice);
          break;
        case bool:
          //new implementation -> use JChoice instead of JCheckBox in order
          //to allow "unset" value (i.e. null)
          jchoice = new JChoice<String>(values);
          jchoice.setDefaultButtonMargin(new Insets(0, 2, 0, 2));
          jchoice.setMaximumFastChoices(20);
          jchoice.setMaximumWidth(300);
          if (BOOLEAN_TRUE.equals(value))
            jchoice.setSelectedItem(BOOLEAN_TRUE);
          else if (BOOLEAN_FALSE.equals(value))
            jchoice.setSelectedItem(BOOLEAN_FALSE);
          else
            jchoice.setSelectedItem(null);
          jchoice.addActionListener(sharedActionListener);
          gui.add(jchoice);
          break;
          
//        case bool:
//          gui.setLayout(new BoxLayout(gui, BoxLayout.LINE_AXIS));
//          checkbox = new JCheckBox();
//          checkbox.addActionListener(sharedActionListener);
//          if(defaultValue != null){ 
//            checkbox.setSelected(Boolean.parseBoolean(defaultValue));
//          }
//          gui.add(checkbox);
//          break;
        case text:
          gui.setLayout(new BoxLayout(gui, BoxLayout.LINE_AXIS));
          textField = new JNullableTextField();
          textField.setColumns(20);
          if(value != null){
            textField.setText(value);
          }else if(defaultValue != null){
            textField.setText(defaultValue);
          }
          textField.addDocumentListener(new DocumentListener(){
            @Override
            public void changedUpdate(DocumentEvent e) {
              sharedActionListener.actionPerformed(
                      new ActionEvent(textField, ActionEvent.ACTION_PERFORMED, 
                              null));
            }
            @Override
            public void insertUpdate(DocumentEvent e) {
              sharedActionListener.actionPerformed(
                      new ActionEvent(textField, ActionEvent.ACTION_PERFORMED, 
                              null));
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
              sharedActionListener.actionPerformed(
                      new ActionEvent(textField, ActionEvent.ACTION_PERFORMED, 
                              null));
            }
          });
          gui.add(textField);          
          break;
      }
      
      defaultBorder = BorderFactory.createEmptyBorder(2, 2, 2, 2);
      highlightBorder = BorderFactory.createLineBorder(Color.RED, 2);
      gui.setBorder(defaultBorder);
    }
    
    protected JNullableTextField textField;
    protected JCheckBox checkbox;
    protected JChoice<String> jchoice;
    
    protected Border defaultBorder;
    
    protected Border highlightBorder;
    
    
    /**
     * The type of the feature.
     */
    protected FeatureType type;
    
    /**
     * The name of the feature
     */
    protected String featureName;
    
    /**
     * 
     * The GUI used for editing the feature.
     */
    protected JComponent gui;
    
    /**
     * Permitted values for nominal features. 
     */
    protected String[] values;
    
    /**
     * Is this feature required
     */
    protected boolean required;
    
    /**
     * The action listener that acts upon UI actions on nay of the widgets.
     */
    protected ActionListener sharedActionListener;
    
    /**
     * Default value as string.
     */
    protected String defaultValue;
    
    /**
     * The value of the feature
     */
    protected String value;
    
    /**
     * @return the type
     */
    public FeatureType getType() {
      return type;
    }
    /**
     * @param type the type to set
     */
    public void setType(FeatureType type) {
      this.type = type;
    }
    /**
     * @return the values
     */
    public String[] getValues() {
      return values;
    }
    /**
     * @param values the values to set
     */
    public void setValues(String[] values) {
      this.values = values;
    }
    /**
     * @return the defaultValue
     */
    public String getDefaultValue() {
      return defaultValue;
    }
    
    /**
     * @param defaultValue the defaultValue to set
     */
    public void setDefaultValue(String defaultValue) {
      this.defaultValue = defaultValue;
    }
    
    /**
     * Sets the value for this feature
     * @param value
     */
    /**
     * @param value
     */
    public void setValue(String value) {
      // cache the actually provided value: if the value is null, we need to 
      // know, as the text editor would return "" when asked rather than null
      this.value = value;
      switch(type){
        case nominal:
          jchoice.setSelectedItem(value);
          break;
        case bool:
          if (BOOLEAN_TRUE.equals(value))
            jchoice.setSelectedItem(BOOLEAN_TRUE);
          else if (BOOLEAN_FALSE.equals(value))
            jchoice.setSelectedItem(BOOLEAN_FALSE);
          else
            jchoice.setSelectedItem(null);
          break;          
//        case bool:
//          checkbox.setSelected(value != null && Boolean.parseBoolean(value));
//          break;
        case text:
          textField.setText(value);
          break;
      }
      //call the action listener to update the border
      sharedActionListener.actionPerformed(
              new ActionEvent(SchemaFeaturesEditor.this, 
              ActionEvent.ACTION_PERFORMED, ""));
    }

    public Object getValue(){
      switch(type){
        case nominal:
          return jchoice.getSelectedItem();
        case bool:
          Object choiceValue = jchoice.getSelectedItem();        
          return choiceValue == null ? null : 
            new Boolean(choiceValue == BOOLEAN_TRUE);
//        case bool:
//          return new Boolean(checkbox.isSelected());
        case text:
          return textField.getText();
        default:
          return null;
      }
    }
    /**
     * @return the featureName
     */
    public String getFeatureName() {
      return featureName;
    }
    /**
     * @param featureName the featureName to set
     */
    public void setFeatureName(String featureName) {
      this.featureName = featureName;
    }
    
    /**
     * @return the gui
     */
    public JComponent getGui() {
      if(gui == null) buildGui();
      return gui;
    }

    /**
     * @return the required
     */
    public boolean isRequired() {
      return required;
    }

    /**
     * @param required the required to set
     */
    public void setRequired(boolean required) {
      this.required = required;
    }

  }
  
  public SchemaFeaturesEditor(AnnotationSchema schema){
    this.schema = schema;
    featureSchemas = new LinkedHashMap<String, FeatureSchema>();
    if(schema != null && schema.getFeatureSchemaSet() != null){
      for(FeatureSchema aFSchema : schema.getFeatureSchemaSet()){
        featureSchemas.put(aFSchema.getFeatureName(), aFSchema);
      }
    }
    initGui();
  }
    
  protected void initGui(){
    setLayout(new GridBagLayout());   
    GridBagConstraints constraints = new GridBagConstraints();
    constraints.anchor = GridBagConstraints.WEST;
    constraints.fill = GridBagConstraints.BOTH;
    constraints.insets = new Insets(2,2,2,2);
    constraints.weightx = 0;
    constraints.weighty = 0;
    int gridy = 0;
    constraints.gridx = GridBagConstraints.RELATIVE;

    
    //build the feature editors
    featureEditors = new LinkedHashMap<String, FeatureEditor>();
    Set<FeatureSchema> fsSet = schema.getFeatureSchemaSet();
    if(fsSet != null){
      for(FeatureSchema aFeatureSchema : fsSet){
        String aFeatureName = aFeatureSchema.getFeatureName();
        String defaultValue = aFeatureSchema.getFeatureValue();
        if(defaultValue != null && defaultValue.length() == 0) 
          defaultValue = null;
        String[] valuesArray = null;
        Set <Object>values = aFeatureSchema.getPermittedValues();
        if(values != null && values.size() > 0){
          valuesArray = new String[values.size()];
          int i = 0;
          for(Object aValue : values){
            valuesArray[i++] = aValue.toString();
          }
          Arrays.sort(valuesArray);
        }
        //build the right editor for the current feature
        FeatureEditor anEditor;
        if(valuesArray != null && valuesArray.length > 0){
          //we have a set of allowed values -> nominal feature
          anEditor = new FeatureEditor(aFeatureName, valuesArray, 
                  defaultValue);
        }else{
          //we don't have any permitted set of values specified
          if(aFeatureSchema.getFeatureValueClass().equals(Boolean.class)){
            //boolean value
            Boolean tValue = null;
            if (BOOLEAN_FALSE.equals(defaultValue))
              tValue = false;
            else if (BOOLEAN_TRUE.equals(defaultValue))
              tValue = true;
                         
            anEditor = new FeatureEditor(aFeatureName, tValue);
          }else{
            //plain text value
            anEditor = new FeatureEditor(aFeatureName, defaultValue);
          }
        }
        anEditor.setRequired(aFeatureSchema.isRequired());
        featureEditors.put(aFeatureName, anEditor);
      }
    }
    //add the feature editors in the alphabetical order
    for(String featureName : featureEditors.keySet()){
      FeatureEditor featureEditor = featureEditors.get(featureName);
      constraints.gridy = gridy++;
      JLabel nameLabel = new JLabel(
              "<html>" + featureName + 
              (featureEditor.isRequired() ? "<b><font color='red'>*</font></b>: " : ": ") +
              "</html>");
      add(nameLabel, constraints);
      constraints.weightx = 1;
      add(featureEditor.getGui(), constraints);
      constraints.weightx = 0;
//      //add a horizontal spacer
//      constraints.weightx = 1;
//      add(Box.createHorizontalGlue(), constraints);
//      constraints.weightx = 0;
    }
    //add a vertical spacer
    constraints.weighty = 1;
    constraints.gridy = gridy++;
    constraints.gridx = GridBagConstraints.LINE_START;
    add(Box.createVerticalGlue(), constraints);
  }
  
  /**
   * Method called to initiate editing of a new feature map.
   * @param featureMap
   */
  public void editFeatureMap(FeatureMap featureMap){
    this.featureMap = featureMap;
    featureMapUpdated();
  }
  
  /* (non-Javadoc)
   * @see gate.event.FeatureMapListener#featureMapUpdated()
   */
  public void featureMapUpdated() {
    //the underlying F-map was changed
    // 1) validate that known features are schema-compliant
    if(featureMap != null){
      for(Object aFeatureName : new HashSet<Object>(featureMap.keySet())){
        //first check if the feature is allowed
        if(featureSchemas.keySet().contains(aFeatureName)){
          FeatureSchema fSchema = featureSchemas.get(aFeatureName);
          Object aFeatureValue = featureMap.get(aFeatureName);
          //check if the value is permitted
          Class<?> featureValueClass = fSchema.getFeatureValueClass(); 
          if(featureValueClass.equals(Boolean.class) ||
             featureValueClass.equals(Integer.class) ||
             featureValueClass.equals(Short.class) ||
             featureValueClass.equals(Byte.class) ||
             featureValueClass.equals(Float.class) ||
             featureValueClass.equals(Double.class)){
            //just check the right type
            if(!featureValueClass.isAssignableFrom(aFeatureValue.getClass())){
              //invalid value type
              featureMap.remove(aFeatureName);
            }
          }else if(featureValueClass.equals(String.class)){
            if(fSchema.getPermittedValues() != null &&
                    !fSchema.getPermittedValues().contains(aFeatureValue)){
                   //invalid value
                   featureMap.remove(aFeatureName);
                 }
          }
        }else{
          //feature not permitted -> ignore
//          featureMap.remove(aFeatureName);
        }
      }
    }
    // 2) then update all the displays
    for(String featureName : featureEditors.keySet()){
//      FeatureSchema fSchema = featureSchemas.get(featureName);
      FeatureEditor aFeatureEditor = featureEditors.get(featureName);
      Object featureValue = featureMap == null ? 
              null : featureMap.get(featureName);
      if(featureValue == null){
        //we don't have a value from the featureMap
        //use the default
        featureValue = aFeatureEditor.getDefaultValue();
        //if we still don't have a value, use the last used value
//        if(featureValue == null ||
//           ( featureValue instanceof String && 
//             ((String)featureValue).length() == 0 
//           ) ){
//          featureValue = aFeatureEditor.getValue();
//        }
        if(featureValue != null && featureMap != null){
          //we managed to find a relevant value -> save it in the feature map
          featureMap.put(featureName, featureValue);
        }
      }else{
        
        //Some values need converting to String
        FeatureSchema fSchema = featureSchemas.get(featureName);
        Class<?> featureValueClass = fSchema.getFeatureValueClass();
        if(featureValueClass.equals(Boolean.class)){
            featureValue = ((Boolean)featureValue).booleanValue() ?
                    BOOLEAN_TRUE : BOOLEAN_FALSE;
        }else if(featureValueClass.equals(String.class)){
          //already a String - nothing to do
        }else{
          //some other type
          featureValue = featureValue.toString();
        }
      }
      aFeatureEditor.setValue((String)featureValue);
    }
  }
  
  
  /**
   * Label for the <tt>true</tt> boolean value.
   */
  private static final String BOOLEAN_TRUE = "True";

  /**
   * Label for the <tt>false</tt> boolean value.
   */
  private static final String BOOLEAN_FALSE = "False";

  
  /**
   * The feature schema for this editor
   */
  protected AnnotationSchema schema;
  
  /**
   * Stored the individual feature schemas, indexed by name. 
   */
  protected Map<String, FeatureSchema> featureSchemas;
  
  /**
   * The feature map currently being edited.
   */
  protected FeatureMap featureMap;
  

  /**
   * A Map storing the editor for each feature.
   */
  protected Map<String, FeatureEditor> featureEditors;
}
