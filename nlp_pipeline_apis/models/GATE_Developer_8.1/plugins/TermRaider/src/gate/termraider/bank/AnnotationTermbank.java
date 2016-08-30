/*
 *  Copyright (c) 2008--2014, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 *  $Id: AnnotationTermbank.java 17967 2014-05-11 16:35:51Z ian_roberts $
 */
package gate.termraider.bank;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Document;
import gate.FeatureMap;
import gate.creole.metadata.CreoleParameter;
import gate.creole.metadata.CreoleResource;
import gate.gui.ActionsPublisher;
import gate.termraider.modes.MergingMode;
import gate.termraider.modes.Normalization;
import gate.termraider.util.ScoreType;
import gate.termraider.util.Term;
import gate.termraider.util.Utilities;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


@CreoleResource(name = "AnnotationTermbank",
    icon = "termbank-lr.png",
    comment = "TermRaider Termbank derived from document annotations",
    helpURL = "http://gate.ac.uk/userguide/sec:creole:termraider:annotation")
public class AnnotationTermbank extends AbstractTermbank
    implements ActionsPublisher  {

  private static final long serialVersionUID = -775090785732060049L;
  
  /* EXTRA CREOLE PARAMETERS */
  protected String inputScoreFeature;
  private MergingMode mergingMode;
  private Normalization normalization;

  /* EXTRA DATA FOR ANALYSIS */
  private Map<Term, List<Double>>  termIndividualScores;
  private ScoreType rawScoreST, termFrequencyST, localDocFrequencyST;
  
  
  protected void processDocument(Document document) {
    documentCount++;
    String documentSource = Utilities.sourceOrName(document);
    AnnotationSet candidates = document.getAnnotations(inputASName).get(inputAnnotationTypes);

    for (Annotation candidate : candidates) {
      Term term = makeTerm(candidate, document);
      FeatureMap fm = candidate.getFeatures();
      if (fm.containsKey(inputScoreFeature)) {
        Utilities.incrementScoreTermValue(scores, termFrequencyST, term, 1);
        
        double score = ((Number) fm.get(inputScoreFeature)).doubleValue();
        Utilities.addToMapSet(termDocuments, term, documentSource);
        
        if (termIndividualScores.containsKey(term)) {
          List<Double> scoreList = termIndividualScores.get(term);
          scoreList.add(score);
        }
        else {
          List<Double> scoreList = new ArrayList<Double>();
          scoreList.add(score);
          termIndividualScores.put(term, scoreList);
        }
      }
    }
  }


  public void calculateScores() {
    for (Term term : termDocuments.keySet()) {
      languages.add(term.getLanguageCode());
      types.add(term.getType());
      
      Double rawScore = MergingMode.calculate(mergingMode, termIndividualScores.get(term));
      Utilities.setScoreTermValue(scores, rawScoreST, term, rawScore);
      int localDF = termDocuments.get(term).size();
      Utilities.setScoreTermValue(scores, localDocFrequencyST, term, localDF);
      double normalized = Normalization.calculate(normalization, rawScore);
      Utilities.setScoreTermValue(scores, getDefaultScoreType(), term, normalized);
    }
    
    if (debugMode) {
      System.out.println("Termbank: nbr of terms = " + termDocuments.size());
    }
  }

  
  protected void resetScores() {
    scores = new HashMap<ScoreType, Map<Term,Number>>();
    for (ScoreType st : scoreTypes) {
      scores.put(st, new HashMap<Term, Number>());
    }
    termIndividualScores = new HashMap<Term, List<Double>>();
    termDocuments        = new HashMap<Term, Set<String>>();
    languages = new HashSet<String>();
    types = new HashSet<String>();
  }

  
  protected void initializeScoreTypes() {
    this.scoreTypes = new ArrayList<ScoreType>();
    this.scoreTypes.add(new ScoreType(scoreProperty));
    this.rawScoreST = new ScoreType(scoreProperty + AbstractTermbank.RAW_SUFFIX);
    this.scoreTypes.add(rawScoreST);
    this.termFrequencyST = new ScoreType("termFrequency");
    this.scoreTypes.add(termFrequencyST);
    this.localDocFrequencyST = new ScoreType("localDocFrequency");
    this.scoreTypes.add(localDocFrequencyST);
  }

  
  /***** CREOLE PARAMETERS *****/

  @CreoleParameter(comment = "annotation feature containing the score to index",
          defaultValue = "localAugTfIdf")
  public void setInputScoreFeature(String annScoreFeature) {
    this.inputScoreFeature = annScoreFeature;    
  }
  
  public String getInputScoreFeature() {
    return this.inputScoreFeature;
  }
  
  @CreoleParameter(comment = "method for aggregating local scores",
          defaultValue = "MAXIMUM")
  public void setMergingMode(MergingMode mode) {
    this.mergingMode = mode;
  }
  
  public MergingMode getMergingMode() {
    return this.mergingMode;
  }
  
  @CreoleParameter(comment = "score normalization",
          defaultValue = "Sigmoid")
  public void setNormalization(Normalization mode) {
    this.normalization = mode;
  }
  
  public Normalization getNormalization() {
    return this.normalization;
  }
  
  /* override default value from AbstractTermbank   */
  @CreoleParameter(defaultValue = "tfIdfAug")
  public void setScoreProperty(String name) {
    super.setScoreProperty(name);
  }


  @Override
  public Map<String, String> getMiscDataForGui() {
    Map<String, String> result = new HashMap<String, String>();
    result.put("nbr of local documents", String.valueOf(this.documentCount));
    result.put("nbr of terms", String.valueOf(this.getDefaultScores().size()));
    return result;
  }

}
