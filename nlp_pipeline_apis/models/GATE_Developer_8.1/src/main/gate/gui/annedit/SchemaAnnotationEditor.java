/*
 * Copyright (c) 1995-2012, The University of Sheffield. See the file
 * COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 * 
 * This file is part of GATE (see http://gate.ac.uk/), and is free software,
 * licenced under the GNU Library General Public License, Version 2, June 1991
 * (in the distribution as file licence.html, and also available at
 * http://gate.ac.uk/gate/licence.html).
 * 
 * AnnotationEditor.java
 * 
 * Valentin Tablan, Sep 10, 2007
 * 
 * $Id: SchemaAnnotationEditor.java 17874 2014-04-18 11:19:47Z markagreenwood $
 */
package gate.gui.annedit;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Factory;
import gate.FeatureMap;
import gate.Gate;
import gate.LanguageResource;
import gate.Resource;
import gate.creole.AbstractVisualResource;
import gate.creole.AnnotationSchema;
import gate.creole.FeatureSchema;
import gate.creole.ResourceInstantiationException;
import gate.event.CreoleEvent;
import gate.event.CreoleListener;
import gate.gui.MainFrame;
import gate.swing.JChoice;
import gate.util.GateException;
import gate.util.GateRuntimeException;
import gate.util.InvalidOffsetException;
import gate.util.LuckyException;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;

/**
 * An annotation editor that enforces the annotation schemas currently loaded in
 * the system. Once the editing of an annotation is started, it cannot be
 * completed until the annotation complies with the schema for that annotation
 * type.
 */
@SuppressWarnings("serial")
public class SchemaAnnotationEditor extends AbstractVisualResource implements
                                                                  OwnedAnnotationEditor {
  
  @Override
  public void editAnnotation(Annotation ann, AnnotationSet set) {
    // the external components we listen to (the text and the list view) can
    // change outside of our control, so we need to update the values frequently
    updateListeners();
    this.annotation = ann;
    this.annSet = set;
    // update the selection in the list view
    // this is necessary because sometimes the call to eidtAnnotaiton is
    // internally received from the search and annotate function.
    // update the editor display
    String annType = annotation == null ? null : annotation.getType();
    // update the border for the types choice
    if(annType == null) {
      // no annotation -> ok
      if(typesChoice.getBorder() != typeDefaultBorder)
        typesChoice.setBorder(typeDefaultBorder);
    } else {
      if(schemasByType.containsKey(annType)) {
        // accepted type
        if(typesChoice.getBorder() != typeDefaultBorder)
          typesChoice.setBorder(typeDefaultBorder);
      } else {
        // wrong type
        if(typesChoice.getBorder() != typeHighlightBorder)
          typesChoice.setBorder(typeHighlightBorder);
      }
    }
    // update the features editor
    SchemaFeaturesEditor newFeaturesEditor = featureEditorsByType.get(annType);
    // if new type, we need to change the features editor and selected type
    // button
    if(newFeaturesEditor != featuresEditor) {
      typesChoice.setSelectedItem(annType);
      if(featuresEditor != null) {
        featuresBox.remove(featuresEditor);
        featuresEditor.editFeatureMap(null);
      }
      featuresEditor = newFeaturesEditor;
      if(featuresEditor != null) {
        featuresBox.add(featuresEditor);
      }
    }
    if(featuresEditor != null) {
      FeatureMap features = ann.getFeatures();
      if(features == null) {
        features = Factory.newFeatureMap();
        ann.setFeatures(features);
      }
      featuresEditor.editFeatureMap(features);
    }
    // enable editing if there is an annotation, disable if not
    setEditingEnabled(annType != null);
    if(dialog != null) {
      if(annotation != null) {
        placeDialog(annotation.getStartNode().getOffset().intValue(),
            annotation.getEndNode().getOffset().intValue());
      } else {
        // this should only occur when the dialog is pinned, so offsets are
        // irrelevant
        placeDialog(0, 0);
      }
    }
  }

  /**
   * This editor implementation is designed to enforce schema compliance. This
   * method will return <tt>false</tt> if the current annotation type does not
   * have a schema or if the features of the current annotation do not comply
   * with the schema.
   * 
   * @see gate.gui.annedit.OwnedAnnotationEditor#editingFinished()
   */
  @Override
  public boolean editingFinished() {
    if(annotation == null) return true;
    // if the dialog is hidden, we've missed the train and we can't force
    // compliance for the old annotation any more. Just give up and
    // allow further editing
    if(!dialog.isVisible()) return true;
    if(!schemasByType.containsKey(annotation.getType())) return false;
    // we need to check that:
    // 1) all required features have values
    // 2) all features known by schema that have values, comply with the schema
    if(annotation == null) return true;
    AnnotationSchema aSchema = schemasByType.get(annotation.getType());
    if(aSchema.getFeatureSchemaSet() == null
        || aSchema.getFeatureSchemaSet().isEmpty()) {
      // known type but no schema restrictions -> OK
      return true;
    }
    FeatureMap annotationFeatures = annotation.getFeatures();
    Map<String, FeatureSchema> featureSchemaByName =
        new HashMap<String, FeatureSchema>();
    // store all the feature schemas, and check the required ones
    for(FeatureSchema aFeatureSchema : aSchema.getFeatureSchemaSet()) {
      featureSchemaByName.put(aFeatureSchema.getFeatureName(), aFeatureSchema);
      Object featureValue =
          annotationFeatures == null ? null : annotationFeatures
              .get(aFeatureSchema.getFeatureName());
      if(aFeatureSchema.isRequired() && featureValue == null) return false;
    }
    // check all the actual values for compliance
    for(Object featureName : annotationFeatures.keySet()) {
      Object featureValue = annotationFeatures.get(featureName);
      FeatureSchema fSchema = featureSchemaByName.get(featureName);
      if(fSchema != null) {
        // this is a schema feature
        if(fSchema.getFeatureValueClass().equals(Boolean.class)
            || fSchema.getFeatureValueClass().equals(Integer.class)
            || fSchema.getFeatureValueClass().equals(Short.class)
            || fSchema.getFeatureValueClass().equals(Byte.class)
            || fSchema.getFeatureValueClass().equals(Float.class)
            || fSchema.getFeatureValueClass().equals(Double.class)) {
          if(featureValue instanceof String) {
            // try to convert numbers
            try {
              if (fSchema.getFeatureValueClass().equals(Integer.class)) {
                featureValue = Integer.valueOf((String) featureValue);
              } else if (fSchema.getFeatureValueClass().equals(Short.class)) {
                featureValue = Short.valueOf((String) featureValue);
              } else if (fSchema.getFeatureValueClass().equals(Byte.class)) {
                featureValue = Byte.valueOf((String) featureValue);
              } else if (fSchema.getFeatureValueClass().equals(Double.class)) {
                featureValue = Double.valueOf((String) featureValue);
              } else if (fSchema.getFeatureValueClass().equals(Float.class)) {
                featureValue = Float.valueOf((String) featureValue);
              }
              annotationFeatures.put(featureName, featureValue);
            } catch (NumberFormatException e) {
              // could not convert
              return false;
            }            
          } else if(!fSchema.getFeatureValueClass().isAssignableFrom(
              featureValue.getClass())) {
            // not a String, nor the exact correct class: invalid value type
            return false;
          }
        } else if(fSchema.getFeatureValueClass().equals(String.class)) {
          if(fSchema.getPermittedValues() != null
              && !fSchema.getPermittedValues().contains(featureValue)) {
            // invalid value
            return false;
          }
        }
      }
    }
    return true;
  }

  /**
   * Does nothing, as this editor does not support cancelling and rollbacks.
   */
  @Override
  public void cancelAction() throws GateException {
  }

  /**
   * Returns <tt>true</tt> always as this editor is generic and can edit any
   * annotation type.
   */
  @Override
  public boolean canDisplayAnnotationType(String annotationType) {
    return true;
  }

  /**
   * Does nothing as this editor works in auto-commit mode (changes are
   * implemented immediately).
   */
  @Override
  public void okAction() throws GateException {
  }

  /**
   * Returns <tt>false</tt>, as this editor does not support cancel operations.
   */
  @Override
  public boolean supportsCancel() {
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see gate.gui.annedit.AnnotationEditor#isActive()
   */
  @Override
  public boolean isActive() {
    return dialog.isVisible();
  }

  /**
   * Finds the best location for the editor dialog for a given span of text
   */
  @Override
  public void placeDialog(int start, int end) {
    if(pinnedButton.isSelected()) {
      // just resize
      Point where = null;
      if(dialog.isVisible()) {
        // where = dialog.getLocationOnScreen();
        where = dialog.getLocation();
      }
      dialog.pack();
      if(where != null) {
        dialogLocation.move(where.x, where.y);
        dialog.setLocation(dialogLocation);
      }
    } else {
      // calculate position
      try {
        Rectangle startRect = owner.getTextComponent().modelToView(start);
        Rectangle endRect = owner.getTextComponent().modelToView(end);
        Point topLeft = owner.getTextComponent().getLocationOnScreen();
        int x = topLeft.x + startRect.x;
        int y = topLeft.y + endRect.y + endRect.height;
        // make sure the window doesn't start lower
        // than the end of the visible rectangle
        Rectangle visRect = owner.getTextComponent().getVisibleRect();
        int maxY = topLeft.y + visRect.y + visRect.height;
        // make sure window doesn't get off-screen
        dialog.pack();
        // responding to changed orientation
        if(currentOrientation == ComponentOrientation.RIGHT_TO_LEFT) {
          x = x - dialog.getSize().width;
          if(x < 0) x = 0;
        }
        // dialog.validate();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        boolean revalidate = false;
        if(dialog.getSize().width > screenSize.width) {
          dialog.setSize(screenSize.width, dialog.getSize().height);
          revalidate = true;
        }
        if(dialog.getSize().height > screenSize.height) {
          dialog.setSize(dialog.getSize().width, screenSize.height);
          revalidate = true;
        }
        if(revalidate) dialog.validate();
        // calculate max X
        int maxX = screenSize.width - dialog.getSize().width;
        // calculate max Y
        if(maxY + dialog.getSize().height > screenSize.height) {
          maxY = screenSize.height - dialog.getSize().height;
        }
        // correct position
        if(y > maxY) y = maxY;
        if(x > maxX) x = maxX;
        dialogLocation.move(x, y);
        dialog.setLocation(dialogLocation);
      } catch(BadLocationException ble) {
        // this should never occur
        throw new GateRuntimeException(ble);
      }
    }
    if(!dialog.isVisible()) dialog.setVisible(true);
  }

  protected static final int HIDE_DELAY = 1500;

  protected static final int SHIFT_INCREMENT = 5;

  protected static final int CTRL_SHIFT_INCREMENT = 10;

  /**
   * The annotation currently being edited.
   */
  protected Annotation annotation;

  /**
   * The annotation set containing the currently edited annotation.
   */
  protected AnnotationSet annSet;

  /**
   * The controlling object for this editor.
   */
  private AnnotationEditorOwner owner;

  /**
   * The text component (obtained from the owner) that this editor listens to.
   */
  private JTextComponent textComponent;

  /**
   * JChoice used for selecting the annotation type.
   */
  protected JChoice<String> typesChoice;

  /**
   * The default border for the types choice
   */
  protected Border typeDefaultBorder;

  /**
   * The highlight border for the types choice
   */
  protected Border typeHighlightBorder;

  /**
   * The dialog used to show this annotation editor.
   */
  protected JDialog dialog;

  protected CreoleListener creoleListener;

  /**
   * Listener used to hide the editing window when the text is hidden.
   */
  protected AncestorListener textAncestorListener;

  /**
   * Stores the Annotation schema objects available in the system. The
   * annotation types are used as keys for the map.
   */
  protected Map<String, AnnotationSchema> schemasByType;

  /**
   * Caches the features editor for each annotation type.
   */
  protected Map<String, SchemaFeaturesEditor> featureEditorsByType;

  /**
   * The box used to host the features editor pane.
   */
  protected Box featuresBox;

  /**
   * Toggle button used to pin down the dialog.
   */
  protected JToggleButton pinnedButton;

  /**
   * The current features editor, one of the ones stored in
   * {@link #featureEditorsByType}.
   */
  protected SchemaFeaturesEditor featuresEditor = null;

  protected MouseEvent pressed;

  public SchemaAnnotationEditor() {
    initData();
  }

  /*
   * (non-Javadoc)
   * 
   * @see gate.creole.AbstractVisualResource#init()
   */
  @Override
  public Resource init() throws ResourceInstantiationException {
    super.init();
    initGui();
    initListeners();
    return this;
  }

  protected void updateListeners() {
    if(owner != null) {
      // we have a new owner
      // if the components that we listen to have changed, we need to update the
      // listeners
      if(textComponent != getOwner().getTextComponent()) {
        // remove old listener
        if(textComponent != null) {
          textComponent.removeAncestorListener(textAncestorListener);
        }
        this.textComponent = owner.getTextComponent();
        // register new listener
        if(textComponent != null) {
          textComponent.addAncestorListener(textAncestorListener);
        }
      }
    } else {
      // no new owner -> just remove old listeners
      if(textComponent != null) {
        textComponent.removeAncestorListener(textAncestorListener);
      }
    }
  }

  protected void initData() {
    schemasByType = new TreeMap<String, AnnotationSchema>();
    for(LanguageResource aSchema : Gate.getCreoleRegister().getLrInstances(
        "gate.creole.AnnotationSchema")) {
      schemasByType.put(((AnnotationSchema)aSchema).getAnnotationName(),
          (AnnotationSchema)aSchema);
    }
    creoleListener = new CreoleListener() {
      @Override
      public void resourceLoaded(CreoleEvent e) {
        Resource newResource = e.getResource();
        if(newResource instanceof AnnotationSchema) {
          AnnotationSchema aSchema = (AnnotationSchema)newResource;
          schemasByType.put(aSchema.getAnnotationName(), aSchema);
        }
      }

      @Override
      public void resourceUnloaded(CreoleEvent e) {
        Resource newResource = e.getResource();
        if(newResource instanceof AnnotationSchema) {
          AnnotationSchema aSchema = (AnnotationSchema)newResource;
          if(schemasByType.containsValue(aSchema)) {
            schemasByType.remove(aSchema.getAnnotationName());
          }
        }
      }

      @Override
      public void datastoreOpened(CreoleEvent e) {
      }

      @Override
      public void datastoreCreated(CreoleEvent e) {
      }

      @Override
      public void datastoreClosed(CreoleEvent e) {
      }

      @Override
      public void resourceRenamed(Resource resource, String oldName,
          String newName) {
      }
    };
    Gate.getCreoleRegister().addCreoleListener(creoleListener);
    textAncestorListener = new AncestorListener() {
      /**
       * A flag used to mark the fact that the dialog is active and was hidden
       * by this listener.
       */
      private boolean dialogActive = false;

      @Override
      public void ancestorAdded(AncestorEvent event) {
        if(dialogActive) {
          if(annotation != null) {
            placeDialog(annotation.getStartNode().getOffset().intValue(),
                annotation.getEndNode().getOffset().intValue());
          }
          dialogActive = false;
        }
      }

      @Override
      public void ancestorMoved(AncestorEvent event) {
        if(dialog.isVisible() && annotation != null) {
          placeDialog(annotation.getStartNode().getOffset().intValue(),
              annotation.getEndNode().getOffset().intValue());
        }
      }

      @Override
      public void ancestorRemoved(AncestorEvent event) {
        if(dialog.isVisible()) {
          dialogActive = true;
          dialog.setVisible(false);
        }
      }
    };
  }

  @Override
  public void cleanup() {
    Gate.getCreoleRegister().removeCreoleListener(creoleListener);
  }

  protected void initGui() {
    // make the dialog
    Window parentWindow =
        SwingUtilities.windowForComponent(owner.getTextComponent());
    if(parentWindow != null) {
      dialog =
          parentWindow instanceof Frame ? new JDialog((Frame)parentWindow,
              "Annotation Editor Dialog", false) : new JDialog(
              (Dialog)parentWindow, "Annotation Editor Dialog", false);
      dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
      MainFrame.getGuiRoots().add(dialog);
    }
    setLayout(new BorderLayout());
    // build the toolbar
    JPanel tBar = new JPanel();
    tBar.setLayout(new GridBagLayout());
    GridBagConstraints constraints = new GridBagConstraints();
    constraints.gridx = GridBagConstraints.RELATIVE;
    constraints.gridy = 0;
    constraints.weightx = 0;
    solButton = new IconOnlyButton(null);
    solButton.setIcon(MainFrame.getIcon("bounds-sol"));
    solButton.setPressedIcon(MainFrame.getIcon("bounds-sol-pressed"));
    tBar.add(solButton, constraints);
    JLabel aLabel = new JLabel(MainFrame.getIcon("bounds-left"));
    aLabel.setBorder(null);
    tBar.add(aLabel, constraints);
    sorButton = new IconOnlyButton(null);
    sorButton.setIcon(MainFrame.getIcon("bounds-sor"));
    sorButton.setPressedIcon(MainFrame.getIcon("bounds-sor-pressed"));
    tBar.add(sorButton, constraints);
    aLabel = new JLabel(MainFrame.getIcon("bounds-span"));
    aLabel.setBorder(null);
    tBar.add(aLabel, constraints);
    eolButton = new IconOnlyButton(null);
    eolButton.setIcon(MainFrame.getIcon("bounds-eol"));
    eolButton.setPressedIcon(MainFrame.getIcon("bounds-eol-pressed"));
    tBar.add(eolButton, constraints);
    aLabel = new JLabel(MainFrame.getIcon("bounds-right"));
    aLabel.setBorder(null);
    tBar.add(aLabel, constraints);
    eorButton = new IconOnlyButton(null);
    eorButton.setIcon(MainFrame.getIcon("bounds-eor"));
    eorButton.setPressedIcon(MainFrame.getIcon("bounds-eor-pressed"));
    tBar.add(eorButton, constraints);
    tBar.add(Box.createHorizontalStrut(15), constraints);
    tBar.add(delButton = new SmallButton(null), constraints);
    constraints.weightx = 1;
    tBar.add(Box.createHorizontalGlue(), constraints);
    constraints.weightx = 0;
    pinnedButton = new JToggleButton(MainFrame.getIcon("pin"));
    pinnedButton.setSelectedIcon(MainFrame.getIcon("pin-in"));
    pinnedButton.setSelected(false);
    pinnedButton.setToolTipText("Press to pin window in place.");
    pinnedButton.setMargin(new Insets(0, 2, 0, 2));
    pinnedButton.setBorderPainted(false);
    pinnedButton.setContentAreaFilled(false);
    tBar.add(pinnedButton);
    add(tBar, BorderLayout.NORTH);
    // build the main pane
    mainPane = new JPanel();
    mainPane.setLayout(new BorderLayout());
    featureEditorsByType = new HashMap<String, SchemaFeaturesEditor>();
    // for each schema we need to create a type button and a features editor
    for(String annType : schemasByType.keySet()) {
      AnnotationSchema annSchema = schemasByType.get(annType);
      SchemaFeaturesEditor aFeaturesEditor =
          new SchemaFeaturesEditor(annSchema);
      featureEditorsByType.put(annType, aFeaturesEditor);
    }
    List<String> typeList = new ArrayList<String>(schemasByType.keySet());
    Collections.sort(typeList);
    String[] typesArray = new String[typeList.size()];
    typeList.toArray(typesArray);
    typesChoice = new JChoice<String>(typesArray);
    typesChoice.setDefaultButtonMargin(new Insets(0, 2, 0, 2));
    typesChoice.setMaximumFastChoices(20);
    typesChoice.setMaximumWidth(300);
    String aTitle = "Type ";
    Border titleBorder = BorderFactory.createTitledBorder(aTitle);
    typeDefaultBorder =
        BorderFactory.createCompoundBorder(titleBorder,
            BorderFactory.createEmptyBorder(2, 2, 2, 2));
    typeHighlightBorder =
        BorderFactory.createCompoundBorder(titleBorder,
            BorderFactory.createLineBorder(Color.RED, 2));
    typesChoice.setBorder(typeDefaultBorder);
    aLabel = new JLabel(aTitle);
    typesChoice
        .setMinimumSize(new Dimension(aLabel.getPreferredSize().width, 0));
    mainPane.add(typesChoice, BorderLayout.NORTH);
    // add the features box
    featuresBox = Box.createVerticalBox();
    aTitle = "Features ";
    featuresBox.setBorder(BorderFactory.createTitledBorder(aTitle));
    aLabel = new JLabel(aTitle);
    mainPane.add(featuresBox, BorderLayout.SOUTH);
    add(mainPane, BorderLayout.CENTER);
    // add the search and annotate GUI at the bottom of the annotator editor
    SearchAndAnnotatePanel searchPanel =
        new SearchAndAnnotatePanel(mainPane.getBackground(), this, dialog);
    add(searchPanel, BorderLayout.SOUTH);
    dialog.add(this);
    dialog.pack();
  }

  protected void initListeners() {
    typesChoice.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        String newType;
        if(typesChoice.getSelectedItem() == null) {
          newType = "";
        } else {
          newType = typesChoice.getSelectedItem().toString();
        }
        if(annotation != null && annSet != null
            && !annotation.getType().equals(newType)) {
          // annotation type change
          Integer oldId = annotation.getId();
          Annotation oldAnn = annotation;
          annSet.remove(oldAnn);
          try {
            annSet.add(oldId, oldAnn.getStartNode().getOffset(), oldAnn
                .getEndNode().getOffset(), newType, oldAnn.getFeatures());
            Annotation newAnn = annSet.get(oldId);
            // update the selection to the new annotation
            getOwner().selectAnnotation(new AnnotationDataImpl(annSet, newAnn));
            editAnnotation(newAnn, annSet);
            owner.annotationChanged(newAnn, annSet, oldAnn.getType());
          } catch(InvalidOffsetException ioe) {
            // this should never happen
            throw new LuckyException(ioe);
          }
        }
      }
    });
    dialog.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        if(editingFinished()) {
          // we can close
          dialog.setVisible(false);
          if(pinnedButton.isSelected()) pinnedButton.setSelected(false);
        } else {
          // let's be really snotty
          getToolkit().beep();
        }
      }
    });
    dialog.getRootPane().addMouseListener(new MouseAdapter() {
      // allow dialog to be dragged with a mouse
      @Override
      public void mousePressed(MouseEvent me) {
        pressed = me;
      }
    });
    dialog.getRootPane().addMouseMotionListener(new MouseMotionAdapter() {
      Point location;

      // allow a dialog to be dragged with a mouse
      @Override
      public void mouseDragged(MouseEvent me) {
        location = dialog.getLocation(location);
        int x = location.x - pressed.getX() + me.getX();
        int y = location.y - pressed.getY() + me.getY();
        dialog.setLocation(x, y);
        pinnedButton.setSelected(true);
      }
    });
    dialog.addComponentListener(new ComponentAdapter() {
      /*
       * (non-Javadoc)
       * 
       * @see java.awt.event.ComponentAdapter#componentMoved(java.awt.event.
       * ComponentEvent)
       */
      @Override
      public void componentMoved(ComponentEvent e) {
        Point newLocation = dialog.getLocation();
        if(!newLocation.equals(dialogLocation)) {
          pinnedButton.setSelected(true);
        }
      }
    });
    InputMap inputMap =
        ((JComponent)dialog.getContentPane())
            .getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    actionMap = ((JComponent)dialog.getContentPane()).getActionMap();
    // add the key-action bindings of this Component to the parent window
    solAction =
        new StartOffsetLeftAction("", MainFrame.getIcon("extend-left"),
            SOL_DESC, KeyEvent.VK_LEFT);
    solButton.setAction(solAction);
    setShortCuts(inputMap, SOL_KEY_STROKES, "solAction");
    actionMap.put("solAction", solAction);
    sorAction =
        new StartOffsetRightAction("", MainFrame.getIcon("extend-right"),
            SOR_DESC, KeyEvent.VK_RIGHT);
    sorButton.setAction(sorAction);
    setShortCuts(inputMap, SOR_KEY_STROKES, "sorAction");
    actionMap.put("sorAction", sorAction);
    delAction =
        new DeleteAnnotationAction("", MainFrame.getIcon("remove-annotation"),
            "Delete the annotation", KeyEvent.VK_DELETE);
    delButton.setAction(delAction);
    inputMap.put(KeyStroke.getKeyStroke("alt DELETE"), "delAction");
    actionMap.put("delAction", delAction);
    eolAction =
        new EndOffsetLeftAction("", MainFrame.getIcon("extend-left"), EOL_DESC,
            KeyEvent.VK_LEFT);
    eolButton.setAction(eolAction);
    setShortCuts(inputMap, EOL_KEY_STROKES, "eolAction");
    actionMap.put("eolAction", eolAction);
    eorAction =
        new EndOffsetRightAction("", MainFrame.getIcon("extend-right"),
            EOR_DESC, KeyEvent.VK_RIGHT);
    eorButton.setAction(eorAction);
    setShortCuts(inputMap, EOR_KEY_STROKES, "eorAction");
    actionMap.put("eorAction", eorAction);
    Action dismissAction = new AbstractAction() {
      private static final long serialVersionUID = 1L;

      @Override
      public void actionPerformed(ActionEvent evt) {
        dialog.setVisible(false);
      }
    };
    inputMap.put(KeyStroke.getKeyStroke("ESCAPE"), "dismissAction");
    actionMap.put("dismissAction", dismissAction);
  }

  /**
   * Stores the currently set dialog location (which is used to identify cases
   * when the dialog was moved by hand, which causes the dialog to be pinned).
   */
  private Point dialogLocation = new Point(0, 0);

  /**
   * @param args
   */
  public static void main(String[] args) {
    try {
      Gate.init();
      JFrame aFrame = new JFrame("New Annotation Editor");
      aFrame.setSize(800, 600);
      aFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      JDialog annDialog =
          new JDialog(aFrame, "Annotation Editor Dialog", false);
      annDialog.setFocusableWindowState(false);
      // annDialog.setResizable(false);
      // annDialog.setUndecorated(true);
      SchemaAnnotationEditor pane = new SchemaAnnotationEditor();
      annDialog.add(pane);
      annDialog.pack();
      // JToolBar tBar = new JToolBar("Annotation Editor", JToolBar.HORIZONTAL);
      // tBar.setLayout(new BorderLayout());
      // tBar.setMinimumSize(tBar.getPreferredSize());
      // tBar.add(pane);
      // aFrame.getContentPane().add(tBar, BorderLayout.NORTH);
      StringBuffer strBuf = new StringBuffer();
      for(int i = 0; i < 100; i++) {
        strBuf.append("The quick brown fox jumped over the lazy dog.\n");
      }
      JTextArea aTextPane = new JTextArea(strBuf.toString());
      JScrollPane scroller = new JScrollPane(aTextPane);
      aFrame.getContentPane().add(scroller, BorderLayout.CENTER);
      // Box aBox = Box.createVerticalBox();
      // aFrame.getContentPane().add(aBox);
      //
      // FeatureEditor aFeatEditor = new FeatureEditor("F-nominal-small",
      // FeatureType.nominal, "val1");
      // aFeatEditor.setValues(new String[]{"val1", "val2", "val3"});
      // aBox.add(aFeatEditor.getGui());
      //
      // aFeatEditor = new FeatureEditor("F-nominal-large",
      // FeatureType.nominal, "val1");
      // aFeatEditor.setValues(new String[]{"val1", "val2", "val3", "val4",
      // "val5",
      // "val6", "val7", "val8", "val9"});
      // aBox.add(aFeatEditor.getGui());
      //
      // aFeatEditor = new FeatureEditor("F-boolean-true",
      // FeatureType.bool, "true");
      // aBox.add(aFeatEditor.getGui());
      //
      // aFeatEditor = new FeatureEditor("F-boolean-false",
      // FeatureType.bool, "false");
      // aBox.add(aFeatEditor.getGui());
      aFrame.setVisible(true);
      System.out.println("Window up");
      annDialog.setVisible(true);
      System.out.println("Dialog up");
    } catch(HeadlessException e) {
      e.printStackTrace();
    } catch(GateException e) {
      e.printStackTrace();
    }
  }

  /**
   * Base class for actions on annotations.
   */
  protected abstract class AnnotationAction extends AbstractAction {
    public AnnotationAction(String text, Icon icon, String desc, int mnemonic) {
      super(text, icon);
      putValue(SHORT_DESCRIPTION, desc);
      putValue(MNEMONIC_KEY, mnemonic);
    }
  }

  protected class StartOffsetLeftAction extends AnnotationAction {
    private static final long serialVersionUID = 1L;

    public StartOffsetLeftAction(String text, Icon icon, String desc,
        int mnemonic) {
      super(text, icon, desc, mnemonic);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
      int increment = 1;
      if((evt.getModifiers() & ActionEvent.SHIFT_MASK) > 0) {
        // CTRL pressed -> use tokens for advancing
        increment = SHIFT_INCREMENT;
        if((evt.getModifiers() & ActionEvent.CTRL_MASK) > 0) {
          increment = CTRL_SHIFT_INCREMENT;
        }
      }
      long newValue =
          annotation.getStartNode().getOffset().longValue() - increment;
      if(newValue < 0) newValue = 0;
      try {
        moveAnnotation(annSet, annotation, new Long(newValue), annotation
            .getEndNode().getOffset());
      } catch(InvalidOffsetException ioe) {
        throw new GateRuntimeException(ioe);
      }
    }
  }

  protected class StartOffsetRightAction extends AnnotationAction {
    private static final long serialVersionUID = 1L;

    public StartOffsetRightAction(String text, Icon icon, String desc,
        int mnemonic) {
      super(text, icon, desc, mnemonic);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
      long endOffset = annotation.getEndNode().getOffset().longValue();
      int increment = 1;
      if((evt.getModifiers() & ActionEvent.SHIFT_MASK) > 0) {
        // CTRL pressed -> use tokens for advancing
        increment = SHIFT_INCREMENT;
        if((evt.getModifiers() & ActionEvent.CTRL_MASK) > 0) {
          increment = CTRL_SHIFT_INCREMENT;
        }
      }
      long newValue =
          annotation.getStartNode().getOffset().longValue() + increment;
      if(newValue > endOffset) newValue = endOffset;
      try {
        moveAnnotation(annSet, annotation, new Long(newValue), annotation
            .getEndNode().getOffset());
      } catch(InvalidOffsetException ioe) {
        throw new GateRuntimeException(ioe);
      }
    }
  }

  protected class EndOffsetLeftAction extends AnnotationAction {
    private static final long serialVersionUID = 1L;

    public EndOffsetLeftAction(String text, Icon icon, String desc, int mnemonic) {
      super(text, icon, desc, mnemonic);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
      long startOffset = annotation.getStartNode().getOffset().longValue();
      int increment = 1;
      if((evt.getModifiers() & ActionEvent.SHIFT_MASK) > 0) {
        // CTRL pressed -> use tokens for advancing
        increment = SHIFT_INCREMENT;
        if((evt.getModifiers() & ActionEvent.CTRL_MASK) > 0) {
          increment = CTRL_SHIFT_INCREMENT;
        }
      }
      long newValue =
          annotation.getEndNode().getOffset().longValue() - increment;
      if(newValue < startOffset) newValue = startOffset;
      try {
        moveAnnotation(annSet, annotation, annotation.getStartNode()
            .getOffset(), new Long(newValue));
      } catch(InvalidOffsetException ioe) {
        throw new GateRuntimeException(ioe);
      }
    }
  }

  protected class EndOffsetRightAction extends AnnotationAction {
    private static final long serialVersionUID = 1L;

    public EndOffsetRightAction(String text, Icon icon, String desc,
        int mnemonic) {
      super(text, icon, desc, mnemonic);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
      long maxOffset = owner.getDocument().getContent().size().longValue() - 1;
      int increment = 1;
      if((evt.getModifiers() & ActionEvent.SHIFT_MASK) > 0) {
        // CTRL pressed -> use tokens for advancing
        increment = SHIFT_INCREMENT;
        if((evt.getModifiers() & ActionEvent.CTRL_MASK) > 0) {
          increment = CTRL_SHIFT_INCREMENT;
        }
      }
      long newValue =
          annotation.getEndNode().getOffset().longValue() + increment;
      if(newValue > maxOffset) newValue = maxOffset;
      try {
        moveAnnotation(annSet, annotation, annotation.getStartNode()
            .getOffset(), new Long(newValue));
      } catch(InvalidOffsetException ioe) {
        throw new GateRuntimeException(ioe);
      }
    }
  }

  protected class DeleteAnnotationAction extends AnnotationAction {
    private static final long serialVersionUID = 1L;

    public DeleteAnnotationAction(String text, Icon icon, String desc,
        int mnemonic) {
      super(text, icon, desc, mnemonic);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
      annSet.remove(annotation);
      // clear the dialog
      editAnnotation(null, annSet);
      if(!pinnedButton.isSelected()) {
        // if not pinned, hide the dialog.
        dialog.setVisible(false);
      } else {
        setEditingEnabled(false);
      }
    }
  }

  /**
   * Changes the span of an existing annotation by creating a new annotation
   * with the same ID, type and features but with the new start and end offsets.
   * 
   * @param set
   *          the annotation set
   * @param oldAnnotation
   *          the annotation to be moved
   * @param newStartOffset
   *          the new start offset
   * @param newEndOffset
   *          the new end offset
   */
  protected void moveAnnotation(AnnotationSet set, Annotation oldAnnotation,
      Long newStartOffset, Long newEndOffset) throws InvalidOffsetException {
    // Moving is done by deleting the old annotation and creating a new one.
    // If this was the last one of one type it would mess up the gui which
    // "forgets" about this type and then it recreates it (with a different
    // colour and not visible.
    // In order to avoid this problem, we'll create a new temporary annotation.
    Annotation tempAnn = null;
    if(set.get(oldAnnotation.getType()).size() == 1) {
      // create a clone of the annotation that will be deleted, to act as a
      // placeholder
      Integer tempAnnId =
          set.add(oldAnnotation.getStartNode(), oldAnnotation.getStartNode(),
              oldAnnotation.getType(), oldAnnotation.getFeatures());
      tempAnn = set.get(tempAnnId);
    }
    Integer oldID = oldAnnotation.getId();
    set.remove(oldAnnotation);
    set.add(oldID, newStartOffset, newEndOffset, oldAnnotation.getType(),
        oldAnnotation.getFeatures());
    Annotation newAnn = set.get(oldID);
    // update the selection to the new annotation
    getOwner().selectAnnotation(new AnnotationDataImpl(set, newAnn));
    editAnnotation(newAnn, set);
    // remove the temporary annotation
    if(tempAnn != null) set.remove(tempAnn);
    owner.annotationChanged(newAnn, set, null);
  }

  /**
   * A JButton with content are not filled and border not painted (in order to
   * save screen real estate)
   */
  protected class SmallButton extends JButton {
    private static final long serialVersionUID = 1L;

    public SmallButton(Action a) {
      super(a);
      // setBorder(null);
      setMargin(new Insets(0, 2, 0, 2));
      // setBorderPainted(false);
      // setContentAreaFilled(false);
    }
  }

  protected class IconOnlyButton extends JButton {
    private static final long serialVersionUID = 1L;

    public IconOnlyButton(Action a) {
      super(a);
      setMargin(new Insets(0, 0, 0, 0));
      setBorder(null);
      setBorderPainted(false);
      setContentAreaFilled(false);
    }
  }

  protected IconOnlyButton solButton;

  protected IconOnlyButton sorButton;

  protected SmallButton delButton;

  protected IconOnlyButton eolButton;

  protected IconOnlyButton eorButton;

  protected JPanel mainPane;

  /**
   * Action bindings for the popup window.
   */
  ActionMap actionMap;

  private StartOffsetLeftAction solAction;

  private StartOffsetRightAction sorAction;

  private DeleteAnnotationAction delAction;

  private EndOffsetLeftAction eolAction;

  private EndOffsetRightAction eorAction;

  /**
   * @return the owner
   */
  @Override
  public AnnotationEditorOwner getOwner() {
    return owner;
  }

  /**
   * @param owner
   *          the owner to set
   */
  @Override
  public void setOwner(AnnotationEditorOwner owner) {
    // if the owner is new, register existing listeners to new owner elements
    if(this.owner != owner) {
      this.owner = owner;
      updateListeners();
    }
  }

  @Override
  public AnnotationSet getAnnotationSetCurrentlyEdited() {
    return annSet;
  }

  @Override
  public Annotation getAnnotationCurrentlyEdited() {
    return annotation;
  }

  @Override
  public void setPinnedMode(boolean pinned) {
    pinnedButton.setSelected(pinned);
  }

  @Override
  public void setEditingEnabled(boolean isEditingEnabled) {
    solButton.setEnabled(isEditingEnabled);
    sorButton.setEnabled(isEditingEnabled);
    delButton.setEnabled(isEditingEnabled);
    eolButton.setEnabled(isEditingEnabled);
    eorButton.setEnabled(isEditingEnabled);
    for(Component c : typesChoice.getComponents()) {
      c.setEnabled(isEditingEnabled);
    }
    // en/disable the components in the featuresBox
    Vector<Component> components = new Vector<Component>();
    Collections.addAll(components, featuresBox.getComponents());
    while(!components.isEmpty()) {
      Component component = components.remove(0);
      if(component instanceof JToggleButton || component instanceof JTextField) {
        component.setEnabled(isEditingEnabled);
      } else if(component instanceof Container) {
        Collections.addAll(components, ((Container)component).getComponents());
      }
    }
    // enable/disable the key binding actions
    if(isEditingEnabled) {
      actionMap.put("solAction", solAction);
      actionMap.put("sorAction", sorAction);
      actionMap.put("delAction", delAction);
      actionMap.put("eolAction", eolAction);
      actionMap.put("eorAction", eorAction);
    } else {
      actionMap.put("solAction", null);
      actionMap.put("sorAction", null);
      actionMap.put("delAction", null);
      actionMap.put("eolAction", null);
      actionMap.put("eorAction", null);
    }
    // reapply the orientation settings after editing is enabled or disabled
    changeOrientation(currentOrientation);
  }

  @Override
  public void changeOrientation(ComponentOrientation orientation) {
    if(orientation == null) return;
    // remember the current orientation
    this.currentOrientation = orientation;
    // input map
    InputMap inputMap =
        ((JComponent)dialog.getContentPane())
            .getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    Action solAction = actionMap.get("solAction");
    Action sorAction = actionMap.get("sorAction");
    Action eolAction = actionMap.get("eolAction");
    Action eorAction = actionMap.get("eorAction");
    if(orientation == ComponentOrientation.RIGHT_TO_LEFT) {
      // in right to left orientation
      // extending start offset is equal to extending end offset
      solButton.setAction(eorAction);
      solButton.setToolTipText(EOR_DESC);
      setShortCuts(inputMap, SOL_KEY_STROKES, "eorAction");
      solButton.setIcon(MainFrame.getIcon("extend-left"));
      // shrinking start offset is equal to shrinking end offset
      sorButton.setAction(eolAction);
      sorButton.setToolTipText(EOL_DESC);
      setShortCuts(inputMap, SOR_KEY_STROKES, "eolAction");
      sorButton.setIcon(MainFrame.getIcon("extend-right"));
      // shrinking end offset is equal to shrinking start offset
      eolButton.setAction(sorAction);
      eolButton.setToolTipText(SOR_DESC);
      setShortCuts(inputMap, EOL_KEY_STROKES, "sorAction");
      eolButton.setIcon(MainFrame.getIcon("extend-left"));
      // extending end offset is extending start offset
      eorButton.setAction(solAction);
      eorButton.setToolTipText(SOL_DESC);
      setShortCuts(inputMap, EOR_KEY_STROKES, "solAction");
      eorButton.setIcon(MainFrame.getIcon("extend-right"));
    } else {
      solButton.setAction(solAction);
      solButton.setToolTipText(SOL_DESC);
      setShortCuts(inputMap, SOL_KEY_STROKES, "solAction");
      solButton.setIcon(MainFrame.getIcon("extend-left"));
      sorButton.setAction(sorAction);
      sorButton.setToolTipText(SOR_DESC);
      setShortCuts(inputMap, SOR_KEY_STROKES, "sorAction");
      sorButton.setIcon(MainFrame.getIcon("extend-right"));
      eolButton.setAction(eolAction);
      eolButton.setToolTipText(EOL_DESC);
      setShortCuts(inputMap, EOL_KEY_STROKES, "eolAction");
      eolButton.setIcon(MainFrame.getIcon("extend-left"));
      eorButton.setAction(eorAction);
      eorButton.setToolTipText(EOR_DESC);
      setShortCuts(inputMap, EOR_KEY_STROKES, "eorAction");
      eorButton.setIcon(MainFrame.getIcon("extend-right"));
    }
  }

  /**
   * Utility method to set short cuts
   * 
   * @param inputMap
   * @param keyStrokes
   * @param action
   */
  private void setShortCuts(InputMap inputMap, String[] keyStrokes,
      String action) {
    for(String aKeyStroke : keyStrokes) {
      inputMap.put(KeyStroke.getKeyStroke(aKeyStroke), action);
    }
  }

  /**
   * current orientation set by the user
   */
  private ComponentOrientation currentOrientation = null;

  /* various tool tips for buttons used for changing offsets */
  private final String SOL_DESC = "<html><b>Extend start</b><small>"
      + "<br>LEFT = 1 character" + "<br> + SHIFT = 5 characters, "
      + "<br> + CTRL + SHIFT = 10 characters</small></html>";

  private final String SOR_DESC = "<html><b>Shrink start</b><small>"
      + "<br>RIGHT = 1 character" + "<br> + SHIFT = 5 characters, "
      + "<br> + CTRL + SHIFT = 10 characters</small></html>";

  private final String EOL_DESC = "<html><b>Shrink end</b><small>"
      + "<br>ALT + LEFT = 1 character" + "<br> + SHIFT = 5 characters, "
      + "<br> + CTRL + SHIFT = 10 characters</small></html>";

  private final String EOR_DESC = "<html><b>Extend end</b><small>"
      + "<br>ALT + RIGHT = 1 character" + "<br> + SHIFT = 5 characters, "
      + "<br> + CTRL + SHIFT = 10 characters</small></html>";

  /* various shortcuts we define */
  private final String[] SOL_KEY_STROKES = new String[]{"LEFT", "shift LEFT",
      "control shift released LEFT"};

  private final String[] SOR_KEY_STROKES = new String[]{"RIGHT", "shift RIGHT",
      "control shift released RIGHT"};

  private final String[] EOL_KEY_STROKES = new String[]{"LEFT", "alt LEFT",
      "control alt released LEFT"};

  private final String[] EOR_KEY_STROKES = new String[]{"RIGHT", "alt RIGHT",
      "control alt released RIGHT"};
}
