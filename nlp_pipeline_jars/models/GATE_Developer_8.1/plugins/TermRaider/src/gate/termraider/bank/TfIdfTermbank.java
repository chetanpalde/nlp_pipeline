/*
 *  Copyright (c) 2008-2014, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 *  $Id: TfIdfTermbank.java 17967 2014-05-11 16:35:51Z ian_roberts $
 */
package gate.termraider.bank;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.creole.ResourceInstantiationException;
import gate.creole.metadata.CreoleParameter;
import gate.creole.metadata.CreoleResource;
import gate.creole.metadata.Optional;
import gate.gui.ActionsPublisher;
import gate.termraider.modes.IdfCalculation;
import gate.termraider.modes.Normalization;
import gate.termraider.modes.TfCalculation;
import gate.termraider.util.ScoreType;
import gate.termraider.util.Term;
import gate.termraider.util.Utilities;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


@CreoleResource(name = "TfIdfTermbank",
        icon = "termbank-lr.png",
        comment = "TermRaider Termbank derived from vectors in document features",
        helpURL = "http://gate.ac.uk/userguide/sec:creole:termraider:tfidf")

public class TfIdfTermbank extends AbstractTermbank
    implements ActionsPublisher  {

  private static final long serialVersionUID = 2256964300070167978L;
  
  /* EXTRA CREOLE PARAMETERS */
  private TfCalculation tfCalculation;
  private IdfCalculation idfCalculation;
  private Normalization normalization;
  private DocumentFrequencyBank docFreqSource;
  
  /* EXTRA DATA */
  private ScoreType rawScoreST, termFrequencyST, localDocFrequencyST, refDocFrequencyST;
  
  
  
  protected void processDocument(Document document) {
    documentCount++;
    String documentSource = Utilities.sourceOrName(document);
    AnnotationSet candidates = document.getAnnotations(inputASName).get(inputAnnotationTypes);

    for (Annotation candidate : candidates) {
      Term term = makeTerm(candidate, document);
      Utilities.incrementScoreTermValue(scores, termFrequencyST, term, 1);
      Utilities.addToMapSet(termDocuments, term, documentSource);
    }
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
    this.refDocFrequencyST = new ScoreType("refDocFrequency");
    this.scoreTypes.add(refDocFrequencyST);
  }

  
  protected void calculateScores() {
    for (Term term : scores.get(termFrequencyST).keySet()) {
      this.languages.add(term.getLanguageCode());
      this.types.add(term.getType());
      
      int tf = scores.get(termFrequencyST).get(term).intValue();
      int df = docFreqSource.getFrequencyLax(term);
      Utilities.setScoreTermValue(scores, refDocFrequencyST, term, df);
      int localDF = this.termDocuments.get(term).size();
      Utilities.setScoreTermValue(scores, localDocFrequencyST, term, localDF);
      int n = docFreqSource.getDocumentCount();
      double rawScore = TfCalculation.calculate(tfCalculation, tf) * IdfCalculation.calculate(idfCalculation, df, n);
      Utilities.setScoreTermValue(scores, rawScoreST, term, rawScore);
      double normalized = Normalization.calculate(normalization, rawScore);
      Utilities.setScoreTermValue(scores, getDefaultScoreType(), term, normalized);
    }

    if (debugMode) {
      System.out.println("Termbank: nbr of terms = " + this.getTerms().size());
    }
  }
  
  
  protected void resetScores() {
    termDocuments = new HashMap<Term, Set<String>>();
    documentCount = 0;
    scores = new HashMap<ScoreType, Map<Term,Number>>();
    for (ScoreType st : scoreTypes) {
      scores.put(st, new HashMap<Term, Number>());
    }
    types = new HashSet<String>();
    languages = new HashSet<String>();
  }


  public int getDocCount() {
    return this.documentCount;
  }
  
  /***** CREOLE PARAMETERS *****/
  @Optional
  @CreoleParameter(comment = "document frequency bank (unset = create from these corpora)")
  public void setDocFreqSource(DocumentFrequencyBank dfb) {
    this.docFreqSource = dfb;
  }
  
  public DocumentFrequencyBank getDocFreqSource() {
    return this.docFreqSource;
  }
  
  @CreoleParameter(comment = "score normalization",
          defaultValue = "Sigmoid")
  public void setNormalization(Normalization mode) {
    this.normalization = mode;
  }
  
  public Normalization getNormalization() {
    return this.normalization;
  }
  

  @CreoleParameter(comment = "term frequency calculation",
          defaultValue = "Logarithmic")
  public void setTfCalculation(TfCalculation mode) {
    this.tfCalculation = mode;
  }
  
  public TfCalculation getTfCalculation() {
    return this.tfCalculation;
  }
          

          
  @CreoleParameter(comment = "inverted document frequency calculation",
          defaultValue = "LogarithmicScaled")
  public void setIdfCalculation(IdfCalculation mode) {
    this.idfCalculation = mode;
  }
  
  public IdfCalculation getIdfCalculation() {
    return this.idfCalculation;
  }

  
  /* override default value from AbstractTermbank   */
  @CreoleParameter(defaultValue = "tfIdf")
  public void setScoreProperty(String name) {
    super.setScoreProperty(name);
  }


  protected void prepare() throws ResourceInstantiationException {
    if ( (corpora == null) || (corpora.size() == 0) ) {
      throw new ResourceInstantiationException("No corpora given");
    }
    
    // If no DFB is specified, we create one from the given corpora
    if (this.docFreqSource == null) {
      FeatureMap dfbParameters = Factory.newFeatureMap();
      dfbParameters.put("inputASName", this.inputASName);
      dfbParameters.put("languageFeature", this.languageFeature);
      dfbParameters.put("inputAnnotationFeature", this.inputAnnotationFeature);
      dfbParameters.put("corpora", this.corpora);
      dfbParameters.put("debugMode", this.debugMode);

      DocumentFrequencyBank dfb = (DocumentFrequencyBank) Factory.createResource(DocumentFrequencyBank.class.getName(), dfbParameters);
      this.setDocFreqSource(dfb);
    }
  }


  @Override
  public Map<String, String> getMiscDataForGui() {
    Map<String, String> result = new HashMap<String, String>();
    result.put("nbr of local documents", String.valueOf(this.documentCount));
    result.put("nbr of reference documents", String.valueOf(this.docFreqSource.getDocumentCount()));
    result.put("nbr of terms", String.valueOf(this.getDefaultScores().size()));
    return result;
  }

}
