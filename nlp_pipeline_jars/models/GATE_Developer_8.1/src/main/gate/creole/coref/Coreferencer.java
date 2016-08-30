/*
 *  Coreferencer.java
 *
 *  Copyright (c) 1995-2012, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 *  Marin Dimitrov, 18/Dec/2001
 *
 *  $Id: Coreferencer.java 17813 2014-04-11 12:54:36Z markagreenwood $
 */

package gate.creole.coref;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Document;
import gate.FeatureMap;
import gate.ProcessingResource;
import gate.Resource;
import gate.creole.AbstractLanguageAnalyser;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;
import gate.creole.metadata.CreoleParameter;
import gate.creole.metadata.CreoleResource;
import gate.creole.metadata.Optional;
import gate.creole.metadata.RunTime;
import gate.util.GateRuntimeException;
import gate.util.SimpleFeatureMapImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

@CreoleResource(name="ANNIE Pronominal Coreferencer", comment="Pronominal Coreference resolution component.", helpURL="http://gate.ac.uk/userguide/sec:annie:pronom-coref", icon="pronominal-coreferencer")
public class Coreferencer extends AbstractLanguageAnalyser implements
                                                          ProcessingResource {

  private static final long serialVersionUID = -2343178168872843239L;

  public static final String COREF_DOCUMENT_PARAMETER_NAME = "document";

  public static final String COREF_ANN_SET_PARAMETER_NAME = "annotationSetName";

  public static final String COREF_TYPE_FEATURE_NAME = "ENTITY_MENTION_TYPE";

  public static final String COREF_ANTECEDENT_FEATURE_NAME = "antecedent_offset";

  /** --- */
  private static final boolean DEBUG = false;

  private static final Logger log = Logger.getLogger(Coreferencer.class);
  
  /** --- */
  private PronominalCoref pronominalModule;

  /** --- */
  public Coreferencer() {
    this.pronominalModule = new PronominalCoref();
  }

  /** Initialise this resource, and return it. */
  @Override
  public Resource init() throws ResourceInstantiationException {

    // load all submodules
    pronominalModule.init();

    return this;
  } // init()

  /**
   * Reinitialises the processing resource. After calling this method
   * the resource should be in the state it is after calling init. If
   * the resource depends on external resources (such as rules files)
   * then the resource will re-read those resources. If the data used to
   * create the resource has changed since the resource has been created
   * then the resource will change too after calling reInit().
   */
  @Override
  public void reInit() throws ResourceInstantiationException {
    init();
  } // reInit()

  /** Set the document to run on. */
  @Override
  public void setDocument(Document newDocument) {

    // Assert.assertNotNull(newDocument);

    this.pronominalModule.setDocument(newDocument);
    super.setDocument(newDocument);
  }
  
  @Override
  public void cleanup() {
    super.cleanup();
    pronominalModule.cleanup();
  }

  /** --- */
  @RunTime
  @Optional
  @CreoleParameter(comment="The annotation set to be used for the generated annotations")
  public void setAnnotationSetName(String annotationSetName) {
    this.pronominalModule.setAnnotationSetName(annotationSetName);
  }

  /** --- */
  public String getAnnotationSetName() {
    return this.pronominalModule.getAnnotationSetName();
  }

  /** --- */
  @RunTime
  @Optional
  @CreoleParameter(comment="Whether or not to resolve it pronouns", defaultValue="false")
  public void setResolveIt(Boolean newValue) {
    this.pronominalModule.setResolveIt(newValue);
  }

  /** --- */
  public Boolean getResolveIt() {
    return this.pronominalModule.getResolveIt();
  }

  /**
   * This method runs the coreferencer. It assumes that all the needed
   * parameters are set. If they are not, an exception will be fired.
   */
  @Override
  public void execute() throws ExecutionException {

    fireStatusChanged("Pronominal Coreferencer processing: "
            + document.getName());
    this.pronominalModule.execute();
    generateCorefChains();
    fireStatusChanged("Pronominal Coreferencer completed");
  }

  /** --- */
  private void generateCorefChains() throws GateRuntimeException {

    // 1. get the resolved corefs
    Map<Annotation,Annotation> ana2ant = this.pronominalModule.getResolvedAnaphora();

    // 2. get the outout annotation set
    String asName = getAnnotationSetName();
    AnnotationSet outputSet = null;

    if(null == asName || asName.equals("")) {
      outputSet = getDocument().getAnnotations();
    }
    else {
      outputSet = getDocument().getAnnotations(asName);
    }

    // 3. generate new annotations
    Iterator<Map.Entry<Annotation, Annotation>> it = ana2ant.entrySet().iterator();
    while(it.hasNext()) {
      Map.Entry<Annotation,Annotation> currLink = it.next();
      Annotation anaphor = currLink.getKey();
      Annotation antecedent = currLink.getValue();

      if(DEBUG) {
        AnnotationSet corefSet = getDocument().getAnnotations("COREF");
        Long antOffset = new Long(0);

        if(null != antecedent) {
          antOffset = antecedent.getStartNode().getOffset();
        }

        FeatureMap features = new SimpleFeatureMapImpl();
        features.put("antecedent", antOffset);
        corefSet.add(anaphor.getStartNode(), anaphor.getEndNode(), "COREF",
                features);
      }

      // do we have antecedent?
      if(null == antecedent) {
        continue;
      }

      // get the ortho-matches of the antecedent
      
      Object matchesObj =
              antecedent.getFeatures().get(ANNOTATION_COREF_FEATURE_NAME);
      @SuppressWarnings("unchecked")
      List<Integer> matches =
              matchesObj instanceof List ? (List<Integer>)matchesObj : null;
      if(matchesObj != null && matches == null) {
        log.warn("Illegal value for " + ANNOTATION_COREF_FEATURE_NAME
                + " feature was ignored.");

      }
        
      if(null == matches) {
        matches = new ArrayList<Integer>();
        matches.add(antecedent.getId());
        antecedent.getFeatures().put(ANNOTATION_COREF_FEATURE_NAME, matches);
        // check if the document has a list of matches
        // if yes, simply add the new list to it
        // if not, create it and add the list of matches to it
        if(document.getFeatures().containsKey(DOCUMENT_COREF_FEATURE_NAME)) {
          @SuppressWarnings("unchecked")
          Map<String,List<List<Integer>>> matchesMap = (Map<String,List<List<Integer>>>)document.getFeatures().get(
                  DOCUMENT_COREF_FEATURE_NAME);
          List<List<Integer>> matchesList = matchesMap.get(getAnnotationSetName());
          if(matchesList == null) {
            matchesList = new ArrayList<List<Integer>>();
            matchesMap.put(getAnnotationSetName(), matchesList);
          }
          matchesList.add(matches);
        }
        else {
          Map<String,List<List<Integer>>> matchesMap = new HashMap<String,List<List<Integer>>>();
          List<List<Integer>> matchesList = new ArrayList<List<Integer>>();
          matchesMap.put(getAnnotationSetName(), matchesList);
          matchesList.add(matches);
          document.getFeatures().put(DOCUMENT_COREF_FEATURE_NAME, matchesMap);
        }// if else
      }// if matches == null

      FeatureMap features = new SimpleFeatureMapImpl();
      features.put(COREF_TYPE_FEATURE_NAME, "PRONOUN");
      features.put(ANNOTATION_COREF_FEATURE_NAME, matches);
      features.put(COREF_ANTECEDENT_FEATURE_NAME, antecedent.getStartNode()
              .getOffset());

      //see if the annotation we want to add already exists
      AnnotationSet existing = outputSet.get(antecedent.getType(), anaphor
              .getStartNode().getOffset(), anaphor.getEndNode().getOffset());

      if(existing.size() > 0) {
        //if it exists simply update the existing annotation
        Annotation annot = existing.iterator().next();
        annot.getFeatures().putAll(features);
        matches.add(annot.getId());
      }
      else {
        //if it doesn't exist create a new annotation
        matches.add(outputSet.add(anaphor.getStartNode(), anaphor.getEndNode(),
                antecedent.getType(), features));
      }
    }
  }

  public String getInanimatedEntityTypes() {
    return this.pronominalModule.getInanimatedEntityTypes();
  }

  @RunTime
  @Optional
  @CreoleParameter(comment="List of annotation types for non animated entities", defaultValue="Organization;Location")
  public void setInanimatedEntityTypes(String inanimatedEntityTypes) {
    this.pronominalModule.setInanimatedEntityTypes(inanimatedEntityTypes);
  }

}
