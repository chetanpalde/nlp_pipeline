/*
 *  Copyright (c) 1995-2012, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 *  Valentin Tablan, 01 Feb 2000
 *
 *  $Id: Transducer.java 17821 2014-04-11 19:19:46Z markagreenwood $
 */
package gate.creole;

import gate.*;
import gate.creole.metadata.CreoleParameter;
import gate.creole.metadata.CreoleResource;
import gate.creole.metadata.Optional;
import gate.creole.metadata.RunTime;
import gate.gui.MainFrame;
import gate.jape.Batch;
import gate.jape.DefaultActionContext;
import gate.jape.JapeException;
import gate.jape.constraint.AnnotationAccessor;
import gate.jape.constraint.ConstraintPredicate;
import gate.util.Benchmarkable;
import gate.util.Err;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;

import javax.swing.*;

/**
 * A cascaded multi-phase transducer using the Jape language which is a variant
 * of the CPSL language.
 */
@CreoleResource(name = "JAPE Transducer",
  comment = "A module for executing Jape grammars.",
  helpURL = "http://gate.ac.uk/userguide/chap:jape",
  icon = "jape"
  )
public class Transducer
  extends AbstractLanguageAnalyser
  implements gate.gui.ActionsPublisher, Benchmarkable, ControllerAwarePR
{
  private static final long serialVersionUID = -8789395272116846595L;

  public static final String TRANSD_DOCUMENT_PARAMETER_NAME = "document";

  public static final String TRANSD_INPUT_AS_PARAMETER_NAME = "inputASName";

  public static final String TRANSD_OUTPUT_AS_PARAMETER_NAME = "outputASName";

  public static final String TRANSD_ENCODING_PARAMETER_NAME = "encoding";

  public static final String TRANSD_GRAMMAR_URL_PARAMETER_NAME = "grammarURL";

  public static final String TRANSD_BINARY_GRAMMAR_URL_PARAMETER_NAME = "binaryGrammarURL";

  public static final String TRANSD_OPERATORS_PARAMETER_NAME = "operators";

  public static final String TRANSD_ANNOTATION_ACCESSORS_PARAMETER_NAME = "annotationAccessors";


  protected List<Action> actionList;
  protected DefaultActionContext actionContext;

  /**
   * Default constructor. Does nothing apart from calling the default
   * constructor from the super class. The actual object initialisation is done
   * via the {@link #init} method.
   */
  public Transducer() {
    actionList = new ArrayList<Action>();
    actionList.add(null);
    actionList.add(new SerializeTransducerAction());
  }

  /*
   * private void writeObject(ObjectOutputStream oos) throws IOException {
   * Out.prln("writing transducer"); oos.defaultWriteObject();
   * Out.prln("finished writing transducer"); } // writeObject
   */
  /**
   * This method is the one responsible for initialising the transducer. It
   * assumes that all the needed parameters have been already set using the
   * appropiate setXXX() methods.
   *
   * @return a reference to <b>this</b>
   */
  @Override
  public Resource init() throws ResourceInstantiationException {
    try {
      fireProgressChanged(0);

      initCustomConstraints();

      if(binaryGrammarURL != null) {
        ObjectInputStream s = new ObjectInputStream(binaryGrammarURL
                .openStream());
        batch = (gate.jape.Batch)s.readObject();
      } else if(grammarURL != null) {
        if(encoding != null) {
          batch = new Batch(grammarURL, encoding, new InternalStatusListener());
          if(enableDebugging != null) {
            batch.setEnableDebugging(enableDebugging.booleanValue());
          } else {
            batch.setEnableDebugging(false);
          }
          batch.setOntology(ontology);
        } else {
          throw new ResourceInstantiationException("encoding is not set!");
        }
      } else {
        throw new ResourceInstantiationException(
                "Neither grammarURL or binaryGrammarURL parameters are set!");
      }
    } catch(Exception e) {
      String message = "Error while parsing the grammar ";
      if(grammarURL != null) message += "(" + grammarURL.toExternalForm() + ")";
      message += ":";
      throw new ResourceInstantiationException(message, e);
    } finally {
      fireProcessFinished();
    }
    actionContext = initActionContext();
    batch.setActionContext(actionContext);
    batch.addProgressListener(new IntervalProgressListener(0, 100));
    return this;
  }

  /**
   * Method that initialises the ActionContext. This method can be overridden
   * if somebody wants to extend the Transducer PR class and provide their own
   * subclass of DefaultActionContext to add some functionality.
   * 
   * @return a DefaultActionContext object
   */
  protected DefaultActionContext initActionContext() {
    return new DefaultActionContext();
  }
  
  
  
  /**
   * Implementation of the run() method from {@link java.lang.Runnable}. This
   * method is responsible for doing all the processing of the input document.
   */
  @Override
  public void execute() throws ExecutionException {
    interrupted = false;
    if(document == null) throw new ExecutionException("No document provided!");
    if(inputASName != null && inputASName.equals("")) inputASName = null;
    if(outputASName != null && outputASName.equals("")) outputASName = null;
    // the action context always reflects, for each document executed,
    // the current PR features and the corpus, if present
    actionContext.setCorpus(corpus);
    actionContext.setPRFeatures(features);
    actionContext.setPR(this);
    try {
      batch.transduce(document, inputASName == null
              ? document.getAnnotations()
              : document.getAnnotations(inputASName), outputASName == null
              ? document.getAnnotations()
              : document.getAnnotations(outputASName));
    } catch(JapeException je) {
      throw new ExecutionException(je);
    }
  }

  /**
   * Gets the list of actions that can be performed on this resource.
   *
   * @return a List of Action objects (or null values)
   */
  @Override
  public List<Action> getActions() {
    List<Action> result = new ArrayList<Action>();
    result.addAll(actionList);
    return result;
  }

  /**
   * Loads any custom operators and annotation accessors into the ConstraintFactory.
   * @throws ResourceInstantiationException
   */
  protected void initCustomConstraints() throws ResourceInstantiationException {
    //Load operators
    if (operators != null) {
      for(String opName : operators) {
        Class<? extends ConstraintPredicate> clazz = null;
        try {
          clazz = Class.forName(opName, true, Gate.getClassLoader())
                        .asSubclass(ConstraintPredicate.class);
        }
        catch(ClassNotFoundException e) {
          //if couldn't find it that way, try with current thread class loader
          try {
            clazz = Class.forName(opName, true,
                Thread.currentThread().getContextClassLoader())
                  .asSubclass(ConstraintPredicate.class);
          }
          catch(ClassNotFoundException e1) {
            throw new ResourceInstantiationException("Cannot load class for operator: " + opName, e1);
          }
        }
        catch(ClassCastException cce) {
          throw new ResourceInstantiationException("Operator class '" + opName + "' must implement ConstraintPredicate");
        }

        //instantiate an instance of the class so can get the operator string
        try {
          ConstraintPredicate predicate = clazz.newInstance();
          String opSymbol = predicate.getOperator();
          //now store it in ConstraintFactory
          Factory.getConstraintFactory().addOperator(opSymbol, clazz);
        }
        catch(Exception e) {
          throw new ResourceInstantiationException("Cannot instantiate class for operator: " + opName, e);
        }
      }
    }

    //Load annotationAccessors
    if (annotationAccessors != null) {
      for(String accessorName : annotationAccessors) {
        Class<? extends AnnotationAccessor> clazz = null;
        try {
          clazz = Class.forName(accessorName, true, Gate.getClassLoader())
                     .asSubclass(AnnotationAccessor.class);
        }
        catch(ClassNotFoundException e) {
          //if couldn't find it that way, try with current thread class loader
          try {
            clazz = Class.forName(accessorName, true,
                Thread.currentThread().getContextClassLoader())
                   .asSubclass(AnnotationAccessor.class);
          }
          catch(ClassNotFoundException e1) {
            throw new ResourceInstantiationException("Cannot load class for accessor: " + accessorName, e1);
          }
        }
        catch(ClassCastException cce) {
          throw new ResourceInstantiationException("Operator class '" + accessorName + "' must implement AnnotationAccessor");
        }

        //instantiate an instance of the class so can get the meta-property name string
        try {
          AnnotationAccessor aa = clazz.newInstance();
          String accSymbol = (String)aa.getKey();
          //now store it in ConstraintFactory
          Factory.getConstraintFactory().addMetaProperty(accSymbol, clazz);
        }
        catch(Exception e) {
          throw new ResourceInstantiationException("Cannot instantiate class for accessor: " + accessorName, e);
        }

      }
    }
  }

  /**
   * Sends a serialized (binary) copy of this transducer to the specified output stream.
   * Note that this is the same function used by the "Serialize Transducer" menu item
   * allowing the same functionality to be accessed via code as well as the GUI.
   **/
  public void serialize(ObjectOutputStream out) throws IOException {
    out.writeObject(batch);
    out.flush();
  }

  /**
   * Saves the Jape Transuder to the binary file.
   *
   * @author niraj
   */
  protected class SerializeTransducerAction extends javax.swing.AbstractAction {

    private static final long serialVersionUID = 4248612378452393237L;

    public SerializeTransducerAction() {
      super("Serialize Transducer");
      putValue(SHORT_DESCRIPTION, "Serializes the Transducer as binary file");
    }

    @Override
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      Runnable runnable = new Runnable() {
        @Override
        public void run() {
          JFileChooser fileChooser = MainFrame.getFileChooser();
          fileChooser.setFileFilter(fileChooser.getAcceptAllFileFilter());
          fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
          fileChooser.setMultiSelectionEnabled(false);
          if(fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
              MainFrame.lockGUI("Serializing JAPE Transducer...");
              FileOutputStream out = new FileOutputStream(file);
              ObjectOutputStream s = new ObjectOutputStream(out);
     		  serialize(s);
              s.close();
              out.close();
            } catch(IOException ioe) {
              JOptionPane.showMessageDialog(MainFrame.getInstance(), "Error!\n" + ioe.toString(),
                      "GATE", JOptionPane.ERROR_MESSAGE);
              ioe.printStackTrace(Err.getPrintWriter());
            } finally {
              MainFrame.unlockGUI();
            }
          }
        }
      };
      Thread thread = new Thread(runnable, "Transduer Serialization");
      thread.setPriority(Thread.MIN_PRIORITY);
      thread.start();
    }
  }

  /**
   * Notifies all the PRs in this controller that they should stop their
   * execution as soon as possible.
   */
  @Override
  public synchronized void interrupt() {
    interrupted = true;
    batch.interrupt();
  }

  /**
   * Sets the grammar to be used for building this transducer.
   *
   * @param newGrammarURL
   *          an URL to a file containing a Jape grammar.
   */
  @CreoleParameter(
    comment = "The URL to the grammar file.",
    suffixes = "jape",
    disjunction = "grammar",
    priority = 1
  )
  public void setGrammarURL(java.net.URL newGrammarURL) {
    grammarURL = newGrammarURL;
  }

  /**
   * Gets the URL to the grammar used to build this transducer.
   *
   * @return a {@link java.net.URL} pointing to the grammar file.
   */
  public java.net.URL getGrammarURL() {
    return grammarURL;
  }

  /**
   *
   * Sets the encoding to be used for reding the input file(s) forming the Jape
   * grammar. Note that if the input grammar is a multi-file one than the same
   * encoding will be used for reding all the files. Multi file grammars with
   * different encoding across the composing files are not supported!
   *
   * @param newEncoding
   *          a {link String} representing the encoding.
   */
  @CreoleParameter(
    comment = "The encoding used for reading the grammar",
    defaultValue = "UTF-8"
  )
  public void setEncoding(String newEncoding) {
    encoding = newEncoding;
  }

  /**
   * Gets the encoding used for reding the grammar file(s).
   */
  public String getEncoding() {
    return encoding;
  }

  /**
   * Sets the {@link gate.AnnotationSet} to be used as input for the transducer.
   *
   * @param newInputASName
   *          a {@link gate.AnnotationSet}
   */
  @RunTime
  @Optional
  @CreoleParameter(
    comment = "The annotation set to be used as input for the transducer"
  )
  public void setInputASName(String newInputASName) {
    inputASName = newInputASName;
  }

  /**
   * Gets the {@link gate.AnnotationSet} used as input by this transducer.
   *
   * @return a {@link gate.AnnotationSet}
   */
  public String getInputASName() {
    return inputASName;
  }

  /**
   * Sets the {@link gate.AnnotationSet} to be used as output by the transducer.
   *
   * @param newOutputASName
   *          a {@link gate.AnnotationSet}
   */
  @RunTime
  @Optional
  @CreoleParameter(
    comment = "The annotation set to be used as output for the transducer"
  )
  public void setOutputASName(String newOutputASName) {
    outputASName = newOutputASName;
  }

  /**
   * Gets the {@link gate.AnnotationSet} used as output by this transducer.
   *
   * @return a {@link gate.AnnotationSet}
   */
  public String getOutputASName() {
    return outputASName;
  }

  public Boolean getEnableDebugging() {
    return enableDebugging;
  }

  @RunTime
  @CreoleParameter(defaultValue = "false")
  public void setEnableDebugging(Boolean enableDebugging) {
    this.enableDebugging = enableDebugging;
  }

  /**
   * Gets the list of class names for any custom boolean operators.
   * Classes must implement {@link gate.jape.constraint.ConstraintPredicate}.
   */
  public List<String> getOperators() {
    return operators;
  }

  /**
   * Sets the list of class names for any custom boolean operators.
   * Classes must implement {@link gate.jape.constraint.ConstraintPredicate}.
   */
  @Optional
  @CreoleParameter(
    comment = "Class names that implement gate.jape.constraint.ConstraintPredicate."
  )
  public void setOperators(List<String> operators) {
    this.operators = operators;
  }

  /**
   * Gets the list of class names for any custom
   * {@link gate.jape.constraint.AnnotationAccessor}s.
   */
  public List<String> getAnnotationAccessors() {
    return annotationAccessors;
  }

  /**
   * Sets the list of class names for any custom
   * {@link gate.jape.constraint.AnnotationAccessor}s.
   */
  @Optional
  @CreoleParameter(
    comment = "Class names that implement gate.jape.constraint.AnnotationAccessor."
  )
  public void setAnnotationAccessors(List<String> annotationAccessors) {
    this.annotationAccessors = annotationAccessors;
  }

  /**
   * Get the benchmark ID of this Transducers batch.
   */
  @Override
  public String getBenchmarkId() {
    return batch.getBenchmarkId();
  }

  /**
   * Set the benchmark ID of this PR.
   */
  @Override
  public void setBenchmarkId(String benchmarkId) {
    batch.setBenchmarkId(benchmarkId);
  }

  /**
   * The URL to the jape file used as grammar by this transducer.
   */
  protected java.net.URL grammarURL;

  /**
   * The URL to the serialized jape file used as grammar by this transducer.
   */
  protected java.net.URL binaryGrammarURL;

  /**
   * The actual JapeTransducer used for processing the document(s).
   */
  protected Batch batch;

  /**
   * The encoding used for reding the grammar file(s).
   */
  protected String encoding;

  /**
   * The {@link gate.AnnotationSet} used as input for the transducer.
   */
  protected String inputASName;

  /**
   * The {@link gate.AnnotationSet} used as output by the transducer.
   */
  protected String outputASName;

  /**
   * The ontology that will be available on the RHS of JAPE rules.
   */
  protected gate.creole.ontology.Ontology ontology;

  /**
   * List of class names for any custom
   * {@link gate.jape.constraint.ConstraintPredicate}.
   */
  protected List<String> operators = null;

  /**
   * List of class names for any custom
   * {@link gate.jape.constraint.AnnotationAccessor}s.
   */
  protected List<String> annotationAccessors = null;

  /**
   * Gets the ontology used by this transducer.
   *
   * @return an {@link gate.creole.ontology.Ontology} value.
   */
  public gate.creole.ontology.Ontology getOntology() {
    return ontology;
  }

  /**
   * Sets the ontology used by this transducer.
   *
   * @param ontology
   *          an {@link gate.creole.ontology.Ontology} value.
   */
  @RunTime
  @Optional
  @CreoleParameter(
    comment = "The ontology to be used by this transducer"
  )
  public void setOntology(gate.creole.ontology.Ontology ontology) {
    this.ontology = ontology;
    //ontology is now a run-time param so we need to propagate it down to the
    //actual SPTs included in this transducer.
    if(batch!= null) batch.setOntology(ontology);
  }

  /**
   * A switch used to activate the JAPE debugger.
   */
  protected Boolean enableDebugging = Boolean.FALSE;


  public java.net.URL getBinaryGrammarURL() {
    return binaryGrammarURL;
  }

  @CreoleParameter(
    comment = "The URL to the binary grammar file.",
    suffixes = "jape",
    disjunction = "grammar",
    priority = 100
  )
  public void setBinaryGrammarURL(java.net.URL binaryGrammarURL) {
    this.binaryGrammarURL = binaryGrammarURL;
  }

  // methods implemeting ControllerAwarePR
  @Override
  public void controllerExecutionStarted(Controller c)
    throws ExecutionException {
    actionContext.setController(c);
    actionContext.setCorpus(corpus);
    actionContext.setPRFeatures(features);
    actionContext.setPRName(this.getName());
    actionContext.setPR(this);
    actionContext.setDebuggingEnabled(enableDebugging);
    batch.runControllerExecutionStartedBlock(actionContext,c,ontology);
  }

  @Override
  public void controllerExecutionFinished(Controller c)
    throws ExecutionException {
    batch.runControllerExecutionFinishedBlock(actionContext,c,ontology);
    actionContext.setCorpus(null);
    actionContext.setController(null);
    actionContext.setPR(null);
  }

  @Override
  public void controllerExecutionAborted(Controller c, Throwable t)
    throws ExecutionException {
    batch.runControllerExecutionAbortedBlock(actionContext,c,t,ontology);
    actionContext.setCorpus(null);
    actionContext.setController(null);
    actionContext.setPR(null);
  }


}
