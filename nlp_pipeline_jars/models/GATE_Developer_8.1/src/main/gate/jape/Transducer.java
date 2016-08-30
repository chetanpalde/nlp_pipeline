/*
 *  Transducer.java - transducer class
 *
 *  Copyright (c) 1995-2012, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 *  Hamish Cunningham, 24/07/98
 *
 *  $Id: Transducer.java 17616 2014-03-10 16:09:07Z markagreenwood $
 */


package gate.jape;

import gate.AnnotationSet;
import gate.Controller;
import gate.Document;
import gate.creole.ExecutionException;
import gate.creole.ontology.Ontology;
import gate.event.ProgressListener;
import gate.event.StatusListener;
import gate.util.Benchmarkable;
import gate.util.GateClassLoader;

import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;


/**
  * Represents a single or multiphase transducer.
  */
public abstract class Transducer implements Serializable, Benchmarkable
{
  private static final long serialVersionUID = -5607520609825899179L;

  /** Name of this transducer. */
  protected String name;

  protected Ontology ontology = null;
  
  /**
   * Shared featureMap
   */
  protected Map<Object,Object> benchmarkFeatures = new HashMap<Object,Object>();

  /**
   * Benchmark ID of this transducer.
   */
  protected String benchmarkID;

  /** Get the phase name of this transducer */
  public String getName() { return name; }

  /**
   * Gets the benchmark ID of this transducer.
   */
  @Override
  public String getBenchmarkId() {
    if(benchmarkID == null) {
      return getName();
    }
    else {
      return benchmarkID;
    }
  }

  /**
   * Set the benchmark ID for this transducer.
   */
  @Override
  public void setBenchmarkId(String benchmarkId) {
    this.benchmarkID = benchmarkId;
  }

  /** Transduce a document.  */
  public abstract void transduce(Document doc, AnnotationSet inputAS,
                                 AnnotationSet outputAS)
                                 throws JapeException, ExecutionException;

  /**
   * Finish: parsing is complete so now we need to do some small amount of final
   * processing before loading the grammars into the specified classloader.
   */
  public abstract void finish(GateClassLoader classLoader);
  
  /** Clean up (delete action class files, for e.g.). */
  public abstract void cleanUp();

  /** Create a string representation of the object with padding. */
  public abstract String toString(String pad);


  /**
   * Checks whether this PR has been interrupted since the last time its
   * {@link #transduce(Document, AnnotationSet, AnnotationSet)} method was called.
   */
  public synchronized boolean isInterrupted(){
    return interrupted;
  }

  /**
   * Notifies this PR that it should stop its execution as soon as possible.
   */
  public synchronized void interrupt(){
    interrupted = true;
  }

  protected boolean interrupted = false;


  public void setBaseURL(java.net.URL newBaseURL) {
    baseURL = newBaseURL;
  }
  public java.net.URL getBaseURL() {
    return baseURL;
  }
  public synchronized void removeProgressListener(ProgressListener l) {
    if (progressListeners != null && progressListeners.contains(l)) {
      @SuppressWarnings("unchecked")
      Vector<ProgressListener> v = (Vector<ProgressListener>) progressListeners.clone();
      v.removeElement(l);
      progressListeners = v;
    }
  }
  public synchronized void addProgressListener(ProgressListener l) {
    @SuppressWarnings("unchecked")
    Vector<ProgressListener> v = progressListeners == null ? new Vector<ProgressListener>(2) : (Vector<ProgressListener>)progressListeners.clone();
    if (!v.contains(l)) {
      v.addElement(l);
      progressListeners = v;
    }
  }

  public void setDebugMode(boolean debugMode) {
    this.debugMode = debugMode;
  }
  public boolean isDebugMode() {
    return debugMode;
  }

  /**
   * Switch used to enable printing debug messages
   */
  private boolean debugMode = false;

  public void setMatchGroupMode(boolean mode) {
    matchGroupMode = mode;
  }
  
  public boolean isMatchGroupMode() {
    return matchGroupMode;
  }

  /** Switch used to enable multiple LHS matching  in case of pattern coverage
   * over one and same span with different annotation groups */
  private boolean matchGroupMode = false;

  /**
   * Switch used to use pre 7.0 style negation, where multiple negative 
   * predicates are not grouped into a conjunction.
   */
  private boolean negationCompatMode;
  
  
  private URL baseURL;

  private transient Vector<ProgressListener> progressListeners;
  private transient Vector<StatusListener> statusListeners;

  /**
   * Switch used to activate the JAPE debugger
   */
  protected boolean enableDebugging;

  /**
   * This property affects the Appelt style of rules application.
   * If true then the longest match will be fired otherwise the shortest will
   * be used. By default it is true.
   */
  protected void fireProgressChanged(int e) {
    if (progressListeners != null  && !progressListeners.isEmpty()) {
      Vector<ProgressListener> listeners = progressListeners;
      int count = listeners.size();
      for (int i = 0; i < count; i++) {
        listeners.elementAt(i).progressChanged(e);
      }
    }
  }
  protected void fireProcessFinished() {
    if (progressListeners != null) {
      Vector<ProgressListener> listeners = progressListeners;
      int count = listeners.size();
      for (int i = 0; i < count; i++) {
        listeners.elementAt(i).processFinished();
      }
    }
  }
  public synchronized void removeStatusListener(StatusListener l) {
    if (statusListeners != null && statusListeners.contains(l)) {
      @SuppressWarnings("unchecked")
      Vector<StatusListener> v = (Vector<StatusListener>)statusListeners.clone();
      v.removeElement(l);
      statusListeners = v;
    }
  }
  public synchronized void addStatusListener(StatusListener l) {
    @SuppressWarnings("unchecked")
    Vector<StatusListener> v = statusListeners == null ? new Vector<StatusListener>(2) : (Vector<StatusListener>) statusListeners.clone();
    if (!v.contains(l)) {
      v.addElement(l);
      statusListeners = v;
    }
  }
  protected void fireStatusChanged(String e) {
    if (statusListeners != null) {
      Vector<StatusListener> listeners = statusListeners;
      int count = listeners.size();
      for (int i = 0; i < count; i++) {
        listeners.elementAt(i).statusChanged(e);
      }
    }
  }

  /**
   * Gets the ontology used by this transducer;
   * @return an {@link gate.creole.ontology.Ontology} value;
   */
  public Ontology getOntology() {
    return ontology;
  }

  /**
   * Sets the ontology used by this transducer;
   * @param ontology an {@link gate.creole.ontology.Ontology} value;
   */
  public void setOntology(Ontology ontology) {
    this.ontology = ontology;
  }

  public boolean isEnableDebugging() {
    return enableDebugging;
  }

  public void setEnableDebugging(boolean enableDebugging) {
    this.enableDebugging = enableDebugging;
  }

  
  
  //ProcessProgressReporter implementation ends here
  
  public boolean isNegationCompatMode() {
    return negationCompatMode;
  }

  public void setNegationCompatMode(boolean negationCompatMode) {
    this.negationCompatMode = negationCompatMode;
  }

  protected ActionContext actionContext;
  public void setActionContext(ActionContext ac) {
    actionContext = ac;
  }

  void runControllerExecutionStartedBlock(ActionContext ac, Controller c, Ontology o) throws ExecutionException { }
  void runControllerExecutionFinishedBlock(ActionContext ac, Controller c, Ontology o) throws ExecutionException { }
  void runControllerExecutionAbortedBlock(ActionContext ac, Controller c, Throwable t, Ontology o) throws ExecutionException { }
 


} // class Transducer



