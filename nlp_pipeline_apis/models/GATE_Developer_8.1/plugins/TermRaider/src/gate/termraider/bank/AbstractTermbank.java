/*
 *  Copyright (c) 2008--2014, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 *  $Id: AbstractTermbank.java 17718 2014-03-20 20:40:06Z adamfunk $
 */
package gate.termraider.bank;

import gate.Corpus;
import gate.Document;
import gate.Factory;
import gate.Resource;
import gate.creole.ResourceInstantiationException;
import gate.creole.metadata.CreoleParameter;
import gate.gui.ActionsPublisher;
import gate.termraider.gui.ActionSaveCsv;
import gate.termraider.output.CsvGenerator;
import gate.termraider.util.ScoreType;
import gate.termraider.util.Term;
import gate.termraider.util.TermComparatorByDescendingScore;
import gate.util.GateException;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.Action;
import org.apache.commons.lang.StringEscapeUtils;



public abstract class AbstractTermbank extends AbstractBank 
    implements ActionsPublisher  {

  private static final long serialVersionUID = -1044054380153036770L;
  
  // additional CREOLE init parameters
  protected Set<String> inputAnnotationTypes;

  // transient to allow serialization
  protected transient List<Action> actionsList;
  
  protected Map<ScoreType, Map<Term, Number>> scores;
  protected Map<Term, Set<String>>  termDocuments;
  public static final String RAW_SUFFIX = ".raw";
  
  private List<Term> termsByDescendingScore;
  protected boolean  descendingScoresDone = false;
  
  protected List<ScoreType> scoreTypes;
  private Number minDefaultScore, maxDefaultScore;


  public Resource init() throws ResourceInstantiationException {
    prepare();
    initializeScoreTypes();
    // Above method must be set in each subclass;
    // now we check it has been done.
    if (this.scoreTypes.size() == 0) {
      throw new ResourceInstantiationException("No score types found in " + this.toString());
    }
    resetScores();
    processCorpora();
    calculateScores();
    return this;
  }
  

  public void cleanup() {
    super.cleanup();
  }
  
  
  public List<ScoreType> getScoreTypes() {
    return this.scoreTypes;
  }
  

  public Number getScore(ScoreType type, Term term) {
    Map<Term, Number> ss = this.getScores(type);
    if (ss.containsKey(term)) {
      return ss.get(term);
    }
    
    // implied else
    return 0;
  }


  public Collection<Term> getTerms() {
    return this.getDefaultScores().keySet();
  }
  
  
  public ScoreType getDefaultScoreType() {
    return this.scoreTypes.get(0);
  }
  
  
  public Map<Term, Number> getDefaultScores() {
    return this.scores.get(getDefaultScoreType());
  }
  
  
  protected abstract void initializeScoreTypes();
  
  
  public List<Term> getTermsByDescendingScore() {
    // lazy computation
    if (! descendingScoresDone) {
      termsByDescendingScore = new ArrayList<Term>(this.getTerms());
      Collections.sort(termsByDescendingScore, new TermComparatorByDescendingScore(scores.get(this.getDefaultScoreType())));
      descendingScoresDone = true;
    }
    return this.termsByDescendingScore;
  }
  

  public Map<Term, Set<String>> getTermDocuments() {
    return this.termDocuments;
  }
  
  
  public Map<ScoreType, Number> getScoreMap(Term term) {
    Map<ScoreType, Number> result = new HashMap<ScoreType, Number>();
    for (ScoreType st : this.scoreTypes) {
      result.put(st, this.scores.get(st).get(term));
    }
    return result;
  }
  
  
  public Map<Term, Number> getScores(ScoreType st) {
    return this.scores.get(st);
  }
  
  
  public Set<String> getDocumentsForTerm(Term term) {
    if (this.termDocuments.containsKey(term)) {
      return this.termDocuments.get(term);
    }
    
    // implied else: empty set
    return new HashSet<String>();
  }
  
  
  private void findMinAndMaxDefaultScores() {
    Collection<Number> values = this.getDefaultScores().values();
    if (values.isEmpty()) {
      minDefaultScore = new Integer(0);
      maxDefaultScore = new Integer(1);
    }
    else {
      minDefaultScore = values.iterator().next();
      maxDefaultScore = values.iterator().next();
      for (Number n : values) {
        if (n.doubleValue() < minDefaultScore.doubleValue()) {
          minDefaultScore = n;
        }
        
        if (n.doubleValue() > maxDefaultScore.doubleValue()) {
          maxDefaultScore = n;
        }
      }
    }
  }
  
  
  public Number getMinScore() {
    // lazy calculation
    if (minDefaultScore == null) {
      findMinAndMaxDefaultScores();
    }
    return minDefaultScore;
  }

  
  public Number getMaxScore() {
    // lazy calculation
    if (maxDefaultScore == null) {
      findMinAndMaxDefaultScores();
    }
    return maxDefaultScore;
  }
  
  
  protected void prepare() throws ResourceInstantiationException {
    if ( (corpora == null) || (corpora.size() == 0) ) {
      throw new ResourceInstantiationException("No corpora given");
    }
    
    this.types = new TreeSet<String>();
    this.languages = new TreeSet<String>();
  }
  
  protected void createActions() {
    actionsList = new ArrayList<Action>();
    actionsList.add(new ActionSaveCsv("Save as CSV...", this));
  }
  
  
  protected void processCorpora() {
    for (Corpus corpus : corpora) {
      processCorpus(corpus);
      if (debugMode) {
        System.out.println("Termbank: added corpus " + corpus.getName() + " with " + corpus.size() + " documents");
      }
    }
  }
  
  
  protected void processCorpus(Corpus corpus) {
    for (int i=0 ; i < corpus.size() ; i++) {
      boolean wasLoaded = corpus.isDocumentLoaded(i);
      Document document = (Document) corpus.get(i);
      
      processDocument(document);

      // datastore safety
      if (! wasLoaded) {
        corpus.unloadDocument(document);
        Factory.deleteResource(document);
      }
    }
  }
  
  

  /* BEHOLD THE GUBBINS to distinguish the various types of Termbanks */

  protected abstract void resetScores();

  protected abstract void processDocument(Document document);
  
  /**
   * This also needs to fill types and languages
   */
  protected abstract void calculateScores();
  
  public abstract Map<String, String> getMiscDataForGui();
  
  
  /* Methods for saving as CSV */
  
  public void saveAsCsv(Number threshold, File outputFile) throws GateException {
    CsvGenerator.generateAndSaveCsv(this, threshold, outputFile);
  }

  /**
   * Convenience method to save everything in the termbank.
   * @param outputFile
   * @throws GateException
   */
  public void saveAsCsv(File outputFile) throws GateException {
    saveAsCsv(this.getMinScore(), outputFile);
  }
  
  
  @Override
  public List<Action> getActions() {
    // lazy instantiation because actionsList is transient
    if (actionsList == null) {
      createActions();
    }
    
    return this.actionsList;
  }

  
  public String getCsvHeader() {
    StringBuilder sb = new StringBuilder();
    sb.append(StringEscapeUtils.escapeCsv("Term"));
    sb.append(',').append(StringEscapeUtils.escapeCsv("Lang"));
    sb.append(',').append(StringEscapeUtils.escapeCsv("Type"));
    for (ScoreType type : this.scoreTypes) {
      sb.append(',').append(StringEscapeUtils.escapeCsv(type.toString()));
    }
    sb.append(getCsvSubheader());
    return sb.toString();
  }
  
  
  /**
   * TODO: This is not right (columns).
   * Should be overridden as necessary, for totals etc.
   * Must start with a newline.
   * @return
   */
  protected String getCsvSubheader() {
    StringBuilder sb = new StringBuilder();
    sb.append('\n');
    sb.append(',').append(StringEscapeUtils.escapeCsv("_TOTAL_DOCS_"));
    sb.append(',').append(StringEscapeUtils.escapeCsv(""));
    sb.append(',').append(StringEscapeUtils.escapeCsv(""));
    sb.append(',').append(StringEscapeUtils.escapeCsv(Integer.toString(this.getDocumentCount())));
    return sb.toString();
  }


  public String getCsvLine(Term term) {
      StringBuilder sb = new StringBuilder();
      sb.append(StringEscapeUtils.escapeCsv(term.getTermString()));
      sb.append(',').append(StringEscapeUtils.escapeCsv(term.getLanguageCode()));
      sb.append(',').append(StringEscapeUtils.escapeCsv(term.getType()));
      for (ScoreType type : this.scoreTypes) {
        sb.append(',').append(StringEscapeUtils.escapeCsv(this.getScore(type, term).toString()));
      }
      return sb.toString();
  }
  

  /***** CREOLE PARAMETERS *****/

  @CreoleParameter(comment = "input annotation types",
          defaultValue = "SingleWord;MultiWord")
  public void setInputAnnotationTypes(Set<String> names) {
    this.inputAnnotationTypes = names;
  }
  
  public Set<String> getInputAnnotationTypes() {
    return this.inputAnnotationTypes;
  }
  
}
