/*
 *  Copyright (c) 2008--2014, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 *  $Id: HyponymyTermbank.java 17967 2014-05-11 16:35:51Z ian_roberts $
 */
package gate.termraider.bank;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Document;
import gate.FeatureMap;
import gate.Utils;
import gate.creole.metadata.CreoleParameter;
import gate.creole.metadata.CreoleResource;
import gate.gui.ActionsPublisher;
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



@CreoleResource(name = "HyponymyTermbank",
        icon = "termbank-lr.png",
        comment = "TermRaider Termbank derived from head/string hyponymy",
        helpURL = "http://gate.ac.uk/userguide/sec:creole:termraider:hyponymy")

public class HyponymyTermbank extends AbstractTermbank
    implements ActionsPublisher  {

  private static final long serialVersionUID = -2382834479385875682L;

  
  /* EXTRA CREOLE PARAMETERS */
  protected List<String> inputHeadFeatures;
  private Normalization normalization;

  
  /* EXTRA DATA FOR ANALYSIS */
  private Map<Term, Set<String>> termHeads;
  private Map<Term, Set<String>> termHyponyms;
  private ScoreType termFrequencyST, hyponymsST, localDocFrequencyST, rawScoreST;

  
  /* Methods for the debugging GUI to get the data   */
  public Map<Term, Set<String>> getTermHeads() {
    return this.termHeads;
  }

  public Map<Term, Set<String>> getTermHyponyms() {
    return this.termHyponyms;
  }

  
  
  private double calculateOneRawScore(Term term) {
    Integer hyponyms = Utilities.getStringSetFromMap(termHyponyms, term).size();
    Integer docFreq = Utilities.getStringSetFromMap(termDocuments, term).size();
    Utilities.setScoreTermValue(scores, hyponymsST, term, hyponyms);
    return docFreq.doubleValue() * (1.0F + hyponyms.doubleValue());
  }

  
  protected void processDocument(Document document) {
    documentCount++;
    String documentSource = Utilities.sourceOrName(document);
    AnnotationSet candidates = document.getAnnotations(inputASName).get(inputAnnotationTypes);
    
    for (Annotation candidate : candidates) {
      Term term = makeTerm(candidate, document);

      FeatureMap features = candidate.getFeatures();
      String head = Utils.stringFor(document, candidate);
      
      for (String key : inputHeadFeatures) {
        if (features.containsKey(key)) {
          head = features.get(key).toString();
          break;
        }
      }
      
      Utilities.addToMapSet(termDocuments, term, documentSource);
      Utilities.addToMapSet(termHeads, term, head);
      Utilities.incrementScoreTermValue(scores, termFrequencyST, term, 1);
    }
  }

  
  
  public void calculateScores() {
    Set<Term> terms = termHeads.keySet();
    Set<String> headsI, headsJ;
    
    for (Term termI : terms) {
      headsI = termHeads.get(termI);
      
      for (Term termJ : terms) {
        if (termJ.getTermString().contains(termI.getTermString())
                && (! termI.equals(termJ))) {
          headsJ = termHeads.get(termJ);
          
          hyponymLoop:
            for (String headI : headsI) {
              for (String headJ : headsJ) {
                if (headI.endsWith(headJ)) {
                  Utilities.addToMapSet(termHyponyms, termI, termJ.getTermString());
                  break hyponymLoop;
                }
              }
            }
        }
      }
    }
    
    for (Term term : terms) {
      this.languages.add(term.getLanguageCode());
      this.types.add(term.getType());
      
      double rawScore = calculateOneRawScore(term);
      double normalized = Normalization.calculate(normalization, rawScore);
      Utilities.setScoreTermValue(scores, rawScoreST, term, rawScore);
      Utilities.setScoreTermValue(scores, getDefaultScoreType(), term, normalized);
      int localDF = this.termDocuments.get(term).size();
      Utilities.setScoreTermValue(scores, localDocFrequencyST, term, localDF);
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
    termHeads       = new HashMap<Term, Set<String>>();
    termHyponyms    = new HashMap<Term, Set<String>>();
    termDocuments   = new HashMap<Term, Set<String>>();
    languages = new HashSet<String>();
    types = new HashSet<String>();
  }

  
  protected void initializeScoreTypes() {
    this.scoreTypes = new ArrayList<ScoreType>();
    this.scoreTypes.add(new ScoreType(scoreProperty));
    this.rawScoreST = new ScoreType(scoreProperty + RAW_SUFFIX);
    this.scoreTypes.add(rawScoreST);
    this.termFrequencyST = new ScoreType("termFrequency");
    this.scoreTypes.add(termFrequencyST);
    this.hyponymsST = new ScoreType("hyponymCount");
    this.scoreTypes.add(hyponymsST);
    this.localDocFrequencyST = new ScoreType("localDocFrequency");
    this.scoreTypes.add(localDocFrequencyST);
  }

  
  
  /***** CREOLE PARAMETERS *****/

  @CreoleParameter(comment = "Annotation features (in order) to be scanned as terms' heads")
  public void setInputHeadFeatures(List<String> list) {
    this.inputHeadFeatures = list;
  }
  
  public List<String> getInputHeadFeatures() {
    return this.inputHeadFeatures;
  }
  
  
  /* override default value from AbstractTermbank   */
  @CreoleParameter(defaultValue = "kyotoDomainRelevance")
  public void setScoreProperty(String name) {
    super.setScoreProperty(name);
  }

  
  @CreoleParameter(comment = "score normalization",
          defaultValue = "Sigmoid")
  public void setNormalization(Normalization mode) {
    this.normalization = mode;
  }
  
  public Normalization getNormalization() {
    return this.normalization;
  }


  @Override
  public Map<String, String> getMiscDataForGui() {
    Map<String, String> result = new HashMap<String, String>();
    result.put("nbr of local documents", String.valueOf(this.documentCount));
    result.put("nbr of terms", String.valueOf(this.getDefaultScores().size()));
    return result;
  }

}
