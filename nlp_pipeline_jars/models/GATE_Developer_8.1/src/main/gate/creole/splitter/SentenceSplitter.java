/*
 *  Copyright (c) 1995-2011, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 *  Valentin Tablan, 01 Feb 2000
 *
 *  $Id: SentenceSplitter.java 18633 2015-04-13 12:23:47Z markagreenwood $
 */

package gate.creole.splitter;

import gate.AnnotationSet;
import gate.Factory;
import gate.FeatureMap;
import gate.Gate;
import gate.Resource;
import gate.creole.AbstractLanguageAnalyser;
import gate.creole.ExecutionException;
import gate.creole.ExecutionInterruptedException;
import gate.creole.ResourceInstantiationException;
import gate.creole.Transducer;
import gate.creole.gazetteer.DefaultGazetteer;
import gate.creole.metadata.CreoleParameter;
import gate.creole.metadata.CreoleResource;
import gate.creole.metadata.Optional;
import gate.creole.metadata.RunTime;
import gate.event.ProgressListener;
import gate.event.StatusListener;
import gate.util.Benchmark;
import gate.util.Benchmarkable;
import gate.util.GateRuntimeException;
import gate.util.InvalidOffsetException;

/**
 * A sentence splitter. This is module contains a tokeniser, a
 * gazetteer and a Jape grammar. This class is used so we can have a different
 * entry in the creole.xml file describing the default resources and to add
 * some minor processing after running the components in order to extract the
 * results in a usable form.
 */
@CreoleResource(name="ANNIE Sentence Splitter", comment="ANNIE sentence splitter.", helpURL="http://gate.ac.uk/userguide/sec:annie:splitter", icon="sentence-splitter")
public class SentenceSplitter extends AbstractLanguageAnalyser implements Benchmarkable{

  private static final long serialVersionUID = -5335682060379173111L;

  public static final String
    SPLIT_DOCUMENT_PARAMETER_NAME = "document";

  public static final String
    SPLIT_INPUT_AS_PARAMETER_NAME = "inputASName";

  public static final String
    SPLIT_OUTPUT_AS_PARAMETER_NAME = "outputASName";

  public static final String
    SPLIT_ENCODING_PARAMETER_NAME = "encoding";

  public static final String
    SPLIT_GAZ_URL_PARAMETER_NAME = "gazetteerListsURL";

  public static final String
    SPLIT_TRANSD_URL_PARAMETER_NAME = "transducerURL";
  
  
  private String benchmarkId;

  @Override
  public Resource init()throws ResourceInstantiationException{
    //create all the componets
    FeatureMap params;
    FeatureMap features;

    params = Factory.newFeatureMap();
    if(gazetteerListsURL != null)
      params.put(DefaultGazetteer.DEF_GAZ_LISTS_URL_PARAMETER_NAME,
              gazetteerListsURL);
    params.put(DefaultGazetteer.DEF_GAZ_ENCODING_PARAMETER_NAME, encoding);

    if (gazetteer == null) {
      //gazetteer
      fireStatusChanged("Creating the gazetteer");
      features = Factory.newFeatureMap();
      Gate.setHiddenAttribute(features, true);

      gazetteer = (DefaultGazetteer)Factory.createResource(
              "gate.creole.gazetteer.DefaultGazetteer",
              params, features);
      gazetteer.setName("Gazetteer " + System.currentTimeMillis());
    }
    else {
      gazetteer.setParameterValues(params);
      gazetteer.reInit();
    }
    
    fireProgressChanged(10);

    params = Factory.newFeatureMap();
    if(transducerURL != null)
      params.put(Transducer.TRANSD_GRAMMAR_URL_PARAMETER_NAME, transducerURL);
    params.put(Transducer.TRANSD_ENCODING_PARAMETER_NAME, encoding);

    if (transducer == null) {
      //transducer
      fireStatusChanged("Creating the JAPE transducer");
      features = Factory.newFeatureMap();
      Gate.setHiddenAttribute(features, true);

      transducer = (Transducer)Factory.createResource(
              "gate.creole.Transducer",
              params, features);
      transducer.setName("Transducer " + System.currentTimeMillis());
    }
    else {
      transducer.setParameterValues(params);
      transducer.reInit();
    }
    
    fireProgressChanged(100);
    fireProcessFinished();

    return this;
  }
  
  @Override
  public void cleanup() {
    Factory.deleteResource(gazetteer);
    Factory.deleteResource(transducer);
  }

  @Override
  public void execute() throws ExecutionException{
    interrupted = false;
    //set the runtime parameters
    FeatureMap params;
    if(inputASName != null && inputASName.equals("")) inputASName = null;
    if(outputASName != null && outputASName.equals("")) outputASName = null;
    
    ProgressListener pListener = null;
    StatusListener sListener = null;

    fireProgressChanged(5);
    pListener = new IntervalProgressListener(5, 10);
    sListener = new StatusListener() {
      @Override
      public void statusChanged(String text) {
        fireStatusChanged(text);
      }
    };
    try {
      // run the gazetteer
      params = Factory.newFeatureMap();
      params.put(DefaultGazetteer.DEF_GAZ_DOCUMENT_PARAMETER_NAME, document);
      params.put(DefaultGazetteer.DEF_GAZ_ANNOT_SET_PARAMETER_NAME, inputASName);
      gazetteer.setParameterValues(params);

      gazetteer.addProgressListener(pListener);
      gazetteer.addStatusListener(sListener);
      gazetteer.execute();

    } catch(Exception e) {
      throw new ExecutionException(e);
    } finally {
      gazetteer.setDocument(null);
      gazetteer.removeProgressListener(pListener);
      gazetteer.removeStatusListener(sListener);
    }

    if(isInterrupted())
      throw new ExecutionInterruptedException("The execution of the \""
              + getName()
              + "\" sentence splitter has been abruptly interrupted!");

    pListener = new IntervalProgressListener(11, 90);

    try {
      params = Factory.newFeatureMap();
      params.put(Transducer.TRANSD_DOCUMENT_PARAMETER_NAME, document);
      params.put(Transducer.TRANSD_INPUT_AS_PARAMETER_NAME, inputASName);
      params.put(Transducer.TRANSD_OUTPUT_AS_PARAMETER_NAME, inputASName);
      transducer.setParameterValues(params);

      transducer.addProgressListener(pListener);
      transducer.addStatusListener(sListener);
      Benchmark.executeWithBenchmarking(transducer,
              Benchmark.createBenchmarkId("SentenceSplitterTransducer",
                      getBenchmarkId()), this, null);

    } catch(Exception e) {
      throw new ExecutionException(e);
    } finally {
      transducer.setDocument(null);
      transducer.removeProgressListener(pListener);
      transducer.removeStatusListener(sListener);
    }

    //get pointers to the annotation sets
    AnnotationSet inputAS = (inputASName == null) ?
                            document.getAnnotations() :
                            document.getAnnotations(inputASName);

    AnnotationSet outputAS = (outputASName == null) ?
                             document.getAnnotations() :
                             document.getAnnotations(outputASName);

    //copy the results to the output set if they are different
    if(inputAS != outputAS){
      outputAS.addAll(inputAS.get(SENTENCE_ANNOTATION_TYPE));
    }

    //create one big sentence if none were found
    AnnotationSet sentences = outputAS.get(SENTENCE_ANNOTATION_TYPE);
    if(sentences == null || sentences.isEmpty()){
      //create an annotation covering the entire content
      try{
        outputAS.add(new Long(0), document.getContent().size(), 
                SENTENCE_ANNOTATION_TYPE, Factory.newFeatureMap());
      }catch(InvalidOffsetException ioe){
        throw new GateRuntimeException(ioe);
      }
    }else{
      //add a sentence covering all the tokens after the last sentence
      Long endSentences = sentences.lastNode().getOffset();
      AnnotationSet remainingTokens = inputAS.get(TOKEN_ANNOTATION_TYPE, endSentences,
                                                  inputAS.lastNode().getOffset());
      if(remainingTokens != null && !remainingTokens.isEmpty()){
        try{
          outputAS.add(remainingTokens.firstNode().getOffset(),
                       remainingTokens.lastNode().getOffset(),
                       SENTENCE_ANNOTATION_TYPE,
                       Factory.newFeatureMap());
        }catch(InvalidOffsetException ioe){
          throw new ExecutionException(ioe);
        }
      }
    }
    fireProcessFinished();
  }//execute()

  /**
   * Notifies all the PRs in this controller that they should stop their
   * execution as soon as possible.
   */
  @Override
  public synchronized void interrupt(){
    interrupted = true;
    gazetteer.interrupt();
    transducer.interrupt();
  }

  @Optional
  @CreoleParameter(defaultValue="resources/sentenceSplitter/grammar/main-single-nl.jape", comment="The URL to the custom Jape grammar file", suffixes="jape")
  public void setTransducerURL(java.net.URL newTransducerURL) {
    transducerURL = newTransducerURL;
  }
  public java.net.URL getTransducerURL() {
    return transducerURL;
  }
  DefaultGazetteer gazetteer;
  Transducer transducer;
  private java.net.URL transducerURL;
  private String encoding;
  private java.net.URL gazetteerListsURL;


  @CreoleParameter(comment="The encoding used for reading the definition files", defaultValue="UTF-8")
  public void setEncoding(String newEncoding) {
    encoding = newEncoding;
  }
  public String getEncoding() {
    return encoding;
  }
  
  @Optional
  @CreoleParameter(defaultValue="resources/sentenceSplitter/gazetteer/lists.def", comment="The URL to the custom list lookup definition file", suffixes="def")
  public void setGazetteerListsURL(java.net.URL newGazetteerListsURL) {
    gazetteerListsURL = newGazetteerListsURL;
  }
  public java.net.URL getGazetteerListsURL() {
    return gazetteerListsURL;
  }
  
  @RunTime
  @Optional
  @CreoleParameter(comment="The annotation set to be used as input that must contain 'Token' annotations")
  public void setInputASName(String newInputASName) {
    inputASName = newInputASName;
  }

  public String getInputASName() {
    return inputASName;
  }
  
  @RunTime
  @Optional
  @CreoleParameter(comment="The annotation set to be used as output for 'Sentence' and 'Split' annotations")
  public void setOutputASName(String newOutputASName) {
    outputASName = newOutputASName;
  }
  public String getOutputASName() {
    return outputASName;
  }
  
  /* (non-Javadoc)
   * @see gate.util.Benchmarkable#getBenchmarkId()
   */
  @Override
  public String getBenchmarkId() {
    if(benchmarkId == null) {
      return getName();
    }
    else {
      return benchmarkId;
    }
  }

  /* (non-Javadoc)
   * @see gate.util.Benchmarkable#setBenchmarkId(java.lang.String)
   */
  @Override
  public void setBenchmarkId(String benchmarkId) {
    this.benchmarkId = benchmarkId;
  }

  private String inputASName;
  private String outputASName;
}//public class SentenceSplitter extends Nerc
