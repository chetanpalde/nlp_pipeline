/*
 * Batch.java - transducer class
 * 
 * Copyright (c) 1995-2012, The University of Sheffield. See the file
 * COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 * 
 * This file is part of GATE (see http://gate.ac.uk/), and is free software,
 * licenced under the GNU Library General Public License, Version 2, June 1991
 * (in the distribution as file licence.html, and also available at
 * http://gate.ac.uk/gate/licence.html).
 * 
 * Hamish Cunningham, 10/08/98
 * 
 * $Id: Batch.java 17768 2014-04-03 14:37:54Z markagreenwood $
 * 
 * DEVELOPER NOTES:
 * 
 * This is one that got away; the relation between constructors, initTransducer
 * and parseTransducer are totally screwy and get worse every time I add
 * something (e.g. support for resource loading). We should probably junk this
 * whole thing and start again....
 */

package gate.jape;

import gate.AnnotationSet;
import gate.Controller;
import gate.Corpus;
import gate.Document;
import gate.Factory;
import gate.Gate;
import gate.creole.ExecutionException;
import gate.creole.ontology.Ontology;
import gate.event.ProgressListener;
import gate.event.StatusListener;
import gate.util.Benchmarkable;
import gate.util.GateClassLoader;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.Vector;

/**
 * Batch processing of JAPE transducers against documents or collections.
 * Construction will parse or deserialise a transducer as required.
 */
public class Batch implements JapeConstants, Benchmarkable {

  private static final long serialVersionUID = -5787362511680231837L;

  /** The URL that points to a .jape file */
  private URL japeURL;

  /** The encoding used for reading the grammar file(s) */
  private String encoding;

  /** The JAPE transducer. */
  private Transducer transducer;

  private ActionContext actionContext;

  private transient GateClassLoader classLoader = null;

  public void setActionContext(ActionContext ac) {
    actionContext = ac;
  }

  @Override
  public void finalize() throws Throwable {
    Gate.getClassLoader().forgetClassLoader(classLoader);
    super.finalize();
  }

  /**
   * Create a fully initialised instance.
   * <P>
   * <CODE>japeFileName</CODE>: the name of a .jape or .ser transducer file.
   * This may be an absolute path, or may a .jar that lives somewhere on the
   * classpath.
   */
  public Batch(URL url, String encoding) throws JapeException {
    this.japeURL = url;
    this.encoding = encoding;
    this.classLoader =
        Gate.getClassLoader().getDisposableClassLoader(
            url.toExternalForm() + System.currentTimeMillis(), true);
    parseJape();
    linkListeners();
  } // full init constructor

  public Batch(URL url, String encoding, StatusListener sListener)
      throws JapeException {

    this.addStatusListener(sListener);
    this.japeURL = url;
    this.encoding = encoding;
    this.classLoader =
        Gate.getClassLoader().getDisposableClassLoader(
            url.toExternalForm() + System.currentTimeMillis(), true);
    parseJape();
    linkListeners();
  } // full init constructor

  private void readObject(java.io.ObjectInputStream in) throws IOException,
      ClassNotFoundException {
    classLoader = Gate.getClassLoader().getDisposableClassLoader(in.toString(),true);
    in.defaultReadObject();
    // now recreate the listeners
    linkListeners();
  }

  /**
   * Creates inner listeners that forward events from the transducer object to
   * our own listeners.
   */
  protected void linkListeners() {
    if(transducer != null) {
      transducer.addStatusListener(new StatusListener() {
        @Override
        public void statusChanged(String text) {
          fireStatusChanged(text);
        }
      });

      transducer.addProgressListener(new ProgressListener() {
        @Override
        public void progressChanged(int value) {
          fireProgressChanged(value);
        }

        @Override
        public void processFinished() {
          fireProcessFinished();
        }
      });
    }
  }

  /**
   * Notifies this PR that it should stop its execution as soon as possible.
   */
  public synchronized void interrupt() {
    transducer.interrupt();
  }

  /** Get the transducer. */
  public Transducer getTransducer() {
    return transducer;
  }

  /** Parse a jape file from {@link #japeURL} and store the transducer. */
  private void parseJape() throws JapeException {
    try {
      gate.jape.parser.ParseCpsl parser =
          Factory.newJapeParser(japeURL, encoding);

      StatusListener listener = null;
      listener = new StatusListener() {
        @Override
        public void statusChanged(String text) {
          fireStatusChanged(text);
        }
      };
      parser.addStatusListener(listener);
      transducer = parser.MultiPhaseTransducer();
      parser.removeStatusListener(listener);
      // the call to finish needs to be handled from here now as it
      // was removed from the .jj file
      transducer.addStatusListener(listener);
      transducer.finish(classLoader);
      transducer.removeStatusListener(listener);

    } catch(gate.jape.parser.ParseException e) {
      throw new JapeException("Batch: error parsing transducer: "
          + e.getMessage());
    } catch(java.io.IOException e) {
      throw new JapeException("Batch: couldn't open JAPE file: "
          + e.getMessage());
    }
  } // parseJape

  /** Process the given collection. */
  public void transduce(Corpus coll) throws JapeException, ExecutionException {
    // for each doc run the transducer
    Iterator<Document> iter = coll.iterator();
    while(iter.hasNext()) {
      Document doc = iter.next();
      transduce(doc, doc.getAnnotations(), doc.getAnnotations());
    }
  } // transduce(coll)

  /** Process a single document. */
  public void transduce(Document doc) throws JapeException, ExecutionException {
    transduce(doc, doc.getAnnotations(), doc.getAnnotations());
  } // transduce(doc)

  /** Process a single document. */
  public void transduce(Document doc, AnnotationSet inputAS,
      AnnotationSet outputAS) throws JapeException, ExecutionException {
    // no need to transduce empty document
    if(inputAS == null || inputAS.isEmpty()) return;
    transducer.setActionContext(actionContext);
    transducer.transduce(doc, inputAS, outputAS);

  } // transduce(doc)

  public void setFeatures(gate.FeatureMap newFeatures) {
    features = newFeatures;
  }

  public gate.FeatureMap getFeatures() {
    return features;
  }

  public synchronized void removeProgressListener(ProgressListener l) {
    if(progressListeners != null && progressListeners.contains(l)) {
      @SuppressWarnings("unchecked")
      Vector<ProgressListener> v =
          (Vector<ProgressListener>)progressListeners.clone();
      v.removeElement(l);
      progressListeners = v;
    }
  }

  public synchronized void addProgressListener(ProgressListener l) {
    @SuppressWarnings("unchecked")
    Vector<ProgressListener> v =
        progressListeners == null
            ? new Vector<ProgressListener>(2)
            : (Vector<ProgressListener>)progressListeners.clone();
    if(!v.contains(l)) {
      v.addElement(l);
      progressListeners = v;
    }
  }

  // ProcessProgressReporter implementation ends here

  private gate.FeatureMap features;

  private transient Vector<ProgressListener> progressListeners;

  private transient Vector<StatusListener> statusListeners;

  private boolean enableDebugging;

  protected void fireProgressChanged(int e) {
    if(progressListeners != null) {
      int count = progressListeners.size();
      for(int i = 0; i < count; i++) {
        progressListeners.elementAt(i).progressChanged(e);
      }
    }
  }

  protected void fireProcessFinished() {
    if(progressListeners != null) {
      int count = progressListeners.size();
      for(int i = 0; i < count; i++) {
        progressListeners.elementAt(i).processFinished();
      }
    }
  }

  public synchronized void removeStatusListener(StatusListener l) {
    if(statusListeners != null && statusListeners.contains(l)) {
      @SuppressWarnings("unchecked")
      Vector<StatusListener> v =
          (Vector<StatusListener>)statusListeners.clone();
      v.removeElement(l);
      statusListeners = v;
    }
  }

  public synchronized void addStatusListener(StatusListener l) {
    @SuppressWarnings("unchecked")
    Vector<StatusListener> v =
        statusListeners == null
            ? new Vector<StatusListener>(2)
            : (Vector<StatusListener>)statusListeners.clone();
    if(!v.contains(l)) {
      v.addElement(l);
      statusListeners = v;
    }
  }

  protected void fireStatusChanged(String e) {
    if(statusListeners != null) {
      int count = statusListeners.size();
      for(int i = 0; i < count; i++) {
        statusListeners.elementAt(i).statusChanged(e);
      }
    }
  }

  /**
   * Sets the ontology to be used by the transducers
   * 
   * @param ontology
   */
  public void setOntology(gate.creole.ontology.Ontology ontology) {
    transducer.setOntology(ontology);
  }

  public boolean isEnableDebugging() {
    return enableDebugging;
  }

  public void setEnableDebugging(boolean enableDebugging) {
    this.enableDebugging = enableDebugging;
    // propagate
    if(transducer != null) transducer.setEnableDebugging(enableDebugging);
  }

  @Override
  public String getBenchmarkId() {
    return transducer.getBenchmarkId();
  }

  @Override
  public void setBenchmarkId(String benchmarkId) {
    transducer.setBenchmarkId(benchmarkId);
  }

  public void runControllerExecutionAbortedBlock(ActionContext ac,
      Controller c, Throwable t, Ontology o) throws ExecutionException {
    transducer.runControllerExecutionAbortedBlock(ac, c, t, o);
  }

  public void runControllerExecutionFinishedBlock(ActionContext ac,
      Controller c, Ontology o) throws ExecutionException {
    transducer.runControllerExecutionFinishedBlock(ac, c, o);
  }

  public void runControllerExecutionStartedBlock(ActionContext ac,
      Controller c, Ontology o) throws ExecutionException {
    transducer.runControllerExecutionStartedBlock(ac, c, o);
  }
} // class Batch

