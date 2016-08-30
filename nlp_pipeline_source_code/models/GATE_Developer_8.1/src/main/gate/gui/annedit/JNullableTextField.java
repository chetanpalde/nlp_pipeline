/*
 *  Copyright (c) 1995-2011, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).

 *  Valentin Tablan, 15 Apr 2011
 *
 *  $Id: JNullableTextField.java 17530 2014-03-04 15:57:43Z markagreenwood $
 */
package gate.gui.annedit;

import gate.gui.MainFrame;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * An encapsulation of {@link JTextField} and a {@link JButton} that allows 
 * the text value to be set to null by pressing the button. Provides the minimal
 * API required for the needs of {@link SchemaFeaturesEditor}. 
 */
public class JNullableTextField extends JPanel {
  private static final long serialVersionUID = -1530694436281692216L;

  protected class NullifyTextAction extends AbstractAction {
    private static final long serialVersionUID = -7807829141939910776L;

    public NullifyTextAction() {
      super(null, MainFrame.getIcon("delete"));
      putValue(SHORT_DESCRIPTION, "Removes this feature completely");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      textField.setText(null);
      text = null;
      fireRemoveUpdate(null);
    }
  }
  
  /**
   * The button used to clear (nullify) the textual value.
   */
  protected JButton nullifyButton;
  
  /**
   * The text field used for editing the textual value.
   */
  protected JTextField textField;
  
  /**
   * The normal background colour for the text field.
   */
  protected Color normalBgColor;
  
  /**
   * The colour used for the text field's background when the value is null.
   */
  protected Color nullBgColor = new Color(200, 250, 255);

  /**
   * My document listeners.
   */
  protected Set<DocumentListener> documentListeners;
  
  /**
   * The text value, which can be null
   */
  protected String text = null;
  
  /**
   * Creates a new {@link JNullableTextField} widget.
   */
  public JNullableTextField() {
    initGui();
    initListeners();
  }

  /**
   * Sets the value edited by this component. Will cause an insertUpdate
   * notification to all {@link DocumentListener}s associated with this
   * component (see {@link #addDocumentListener(DocumentListener)}.
   * @param text
   */
  public void setText(String text) {
    textField.setText(text);
    this.text = text;
    fireInsertUpdate(null);
  }
  
  /**
   * Gets the value currently being edited. Unlike {@link JTextField}, this 
   * value may be null (if {@link #setText(String)} was called previously with 
   * a <code>null</code> value, of the delete button was pressed by the user). 
   */
  public String getText() {
    return text;
  }

  /**
   * Sets the number of columns for the included {@link JTextField}, see 
   * {@link JTextField#setColumns(int)}. 
   */
  public void setColumns(int cols) {
    textField.setColumns(cols);
  }
  
  protected void initGui() {
    setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
    
    textField = new JTextField();
    add(textField);
    add(Box.createHorizontalStrut(2));
    nullifyButton = new JButton(new NullifyTextAction());
    add(nullifyButton);

    normalBgColor = textField.getBackground();
  }
  
  protected void initListeners() {
    documentListeners = Collections.synchronizedSet(
            new HashSet<DocumentListener>());
    
    final DocumentListener tfDocumentListener = new DocumentListener() {
      @Override
      public void removeUpdate(DocumentEvent e) {
        text = textField.getText();
        fireRemoveUpdate(e);
      }
      
      @Override
      public void insertUpdate(DocumentEvent e) {
        text = textField.getText();
        fireInsertUpdate(e);
      }
      
      @Override
      public void changedUpdate(DocumentEvent e) {
        fireChangedUpdate(e);
      }
    };
    
    textField.getDocument().addDocumentListener(tfDocumentListener);
    
    textField.addPropertyChangeListener("document", new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        textField.getDocument().addDocumentListener(tfDocumentListener);
      }
    });
    
    // listen to our own events, and highlight null value
    addDocumentListener(new DocumentListener() {
      @Override
      public void removeUpdate(DocumentEvent e) {
        valueChanged();
      }
      @Override
      public void insertUpdate(DocumentEvent e) {
        valueChanged();
      }
      
      @Override
      public void changedUpdate(DocumentEvent e) { }
      
      private void valueChanged() {
        if(getText() == null) {
          textField.setBackground(nullBgColor);
        } else {
          textField.setBackground(normalBgColor);
        }
      }
    });
    
  }

  /**
   * Registers a new {@link DocumentListener} with this component. The provided
   * listener will be forwarded all the events generated by the encapsulated 
   * {@link JTextField}. An event will also be generated when the user presses 
   * the delete button, causing the text value to be nullified.  
   * @param listener
   */
  public void addDocumentListener(DocumentListener listener) {
    documentListeners.add(listener);
  }

  /**
   * Removes a previously registered listener (see 
   * {@link #addDocumentListener(DocumentListener)}).
   * @param listener
   */
  public void removeDocumentListener(DocumentListener listener) {
    documentListeners.remove(listener);
  }
  
  
  protected void fireChangedUpdate(DocumentEvent e) {
    for(DocumentListener aListener : documentListeners) 
      aListener.changedUpdate(e);
  }
  
  protected void fireInsertUpdate(DocumentEvent e) {
    for(DocumentListener aListener : documentListeners) 
      aListener.insertUpdate(e);
  }
  
  protected void fireRemoveUpdate(DocumentEvent e) {
    for(DocumentListener aListener : documentListeners) 
      aListener.removeUpdate(e);
  }
}
