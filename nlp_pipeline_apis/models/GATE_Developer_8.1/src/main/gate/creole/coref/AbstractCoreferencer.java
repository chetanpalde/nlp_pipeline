/*
 *  AbstractCoreferencer.java
 *
 *  Copyright (c) 1995-2012, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 *  $Id: AbstractCoreferencer.java 17588 2014-03-08 07:50:36Z markagreenwood $
 */

package gate.creole.coref;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Document;
import gate.FeatureMap;
import gate.ProcessingResource;
import gate.Resource;
import gate.creole.AbstractLanguageAnalyser;
import gate.creole.ResourceInstantiationException;
import gate.util.GateRuntimeException;
import gate.util.SimpleFeatureMapImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class AbstractCoreferencer extends AbstractLanguageAnalyser
    implements ProcessingResource{

  private static final long serialVersionUID = 7077378848676784207L;

  public static final String COREF_DOCUMENT_PARAMETER_NAME = "document";

  public static final String COREF_ANN_SET_PARAMETER_NAME = "annotationSetName";

  public static final String COREF_TYPE_FEATURE_NAME = "ENTITY_MENTION_TYPE";
  public static final String COREF_ANTECEDENT_FEATURE_NAME = "antecedent_offset";

  /** --- */
  private static final boolean DEBUG = false;

  public String coreferenceType;

  /** --- */
  public AbstractCoreferencer(String type) {
    this.coreferenceType = type;
  }


  /** Initialise this resource, and return it. */
  @Override
  public Resource init() throws ResourceInstantiationException {

    Resource result = super.init();

    return result;
  } // init()


  /**
   * Reinitialises the processing resource. After calling this method the
   * resource should be in the state it is after calling init.
   * If the resource depends on external resources (such as rules files) then
   * the resource will re-read those resources. If the data used to create
   * the resource has changed since the resource has been created then the
   * resource will change too after calling reInit().
  */
  @Override
  public void reInit() throws ResourceInstantiationException {
    init();
  } // reInit()

  /** Set the document to run on. */
  @Override
  public void setDocument(Document newDocument) {
    super.setDocument(newDocument);
  }

  /** --- */
  public abstract void setAnnotationSetName(String annotationSetName);

  /** --- */
  public abstract String getAnnotationSetName();

  /** --- */
  protected void generateCorefChains(Map<Annotation, Annotation> ana2ant)
      throws GateRuntimeException{

    String asName = getAnnotationSetName();
    AnnotationSet outputSet = null;

    if (null == asName || asName.equals("")) {
      outputSet = getDocument().getAnnotations();
    }
    else {
      outputSet = getDocument().getAnnotations(asName);
    }

    //3. generate new annotations
    Iterator<Map.Entry<Annotation, Annotation>> it = ana2ant.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry<Annotation, Annotation> currLink = it.next();
      Annotation anaphor = currLink.getKey();
      Annotation antecedent = currLink.getValue();

      if (DEBUG) {
        AnnotationSet corefSet = getDocument().getAnnotations("COREF");
        Long antOffset = new Long(0);

        if (null != antecedent) {
          antOffset = antecedent.getStartNode().getOffset();
        }

        FeatureMap features = new SimpleFeatureMapImpl();
        features.put("antecedent",antOffset);
        corefSet.add(anaphor.getStartNode(),anaphor.getEndNode(),"COREF",features);
      }

      //do we have antecedent?
      if (null == antecedent) {
        continue;
      }

      //get the ortho-matches of the antecedent
      @SuppressWarnings("unchecked")
      List<Integer> matches = (List<Integer>)antecedent.getFeatures().
        get(ANNOTATION_COREF_FEATURE_NAME);
      if (null == matches) {
        matches = new ArrayList<Integer>();
        matches.add(antecedent.getId());
        antecedent.getFeatures().
          put(ANNOTATION_COREF_FEATURE_NAME,matches);
        //check if the document has a list of matches
        //if yes, simply add the new list to it
        //if not, create it and add the list of matches to it
        if (document.getFeatures().containsKey(
            DOCUMENT_COREF_FEATURE_NAME)) {
          @SuppressWarnings("unchecked")
          Map<String,List<List<Integer>>> matchesMap = (Map<String,List<List<Integer>>>)document.getFeatures().get(
                                DOCUMENT_COREF_FEATURE_NAME);
          List<List<Integer>> matchesList = matchesMap.get(getAnnotationSetName());
          if (matchesList == null) {
            matchesList = new ArrayList<List<Integer>>();
            matchesMap.put(getAnnotationSetName(), matchesList);
          }
          matchesList.add(matches);
        } else {
          Map<String,List<List<Integer>>> matchesMap = new HashMap<String,List<List<Integer>>>();
            List<List<Integer>> matchesList = new ArrayList<List<Integer>>();
            matchesMap.put(getAnnotationSetName(), matchesList);
            matchesList.add(matches);
        }//if else
      }//if matches == null

      FeatureMap features = new SimpleFeatureMapImpl();
      features.put(COREF_TYPE_FEATURE_NAME, coreferenceType);
      features.put(ANNOTATION_COREF_FEATURE_NAME, matches);
      features.put(COREF_ANTECEDENT_FEATURE_NAME,
                   antecedent.getStartNode().getOffset());

      Integer annID = outputSet.add(anaphor.getStartNode(),
                                    anaphor.getEndNode(),
                                    antecedent.getType(),
                                    features);
      matches.add(annID);
    }
  }

}
