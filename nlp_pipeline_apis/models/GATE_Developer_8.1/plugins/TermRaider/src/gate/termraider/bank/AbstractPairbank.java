/*
 *  Copyright (c) 2008--2014, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 *  $Id: AbstractPairbank.java 17718 2014-03-20 20:40:06Z adamfunk $
 */
package gate.termraider.bank;

import gate.Corpus;
import gate.Document;
import gate.Factory;
import gate.Resource;
import gate.creole.ResourceInstantiationException;
import gate.gui.ActionsPublisher;
import gate.termraider.gui.ActionSaveCsv;
import gate.termraider.output.PairCsvGenerator;
import gate.termraider.util.UnorderedTermPair;
import gate.util.GateException;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.Action;



public abstract class AbstractPairbank extends AbstractBank
    implements ActionsPublisher {

  private static final long serialVersionUID = 3544077331310241919L;

  protected transient List<Action> actionsList;
  
  protected Map<UnorderedTermPair, Double> scores;
  protected Map<UnorderedTermPair, Set<String>> documents;
  protected Map<UnorderedTermPair, Integer> pairCount;



  public Resource init() throws ResourceInstantiationException {
    prepare();
    resetScores();
    processCorpora();
    calculateScores();
    return this;
  }
  

  public void cleanup() {
    super.cleanup();
  }

  public Set<UnorderedTermPair> getPairs() {
    return this.pairCount.keySet();
  }
  
  public Map<UnorderedTermPair, Double> getScores() {
    return this.scores;
  }
  
  public Map<UnorderedTermPair, Set<String>> getDocuments() {
    return this.documents;
  }
  
  public int getDocumentCount(UnorderedTermPair pair) {
    if (this.documents.containsKey(pair)) {
      return this.documents.get(pair).size();
    }
    
    return 0;
  }
  
  public int getPairCount(UnorderedTermPair pair) {
    if (this.pairCount.containsKey(pair)) {
      return this.pairCount.get(pair);
    }
    // implied else
    return 0;
  }
  
  
  public Double getMinScore() {
    if (this.scores.isEmpty()) {
      return 0.0;
    }
    // implied else
    return Collections.min(this.scores.values());
  }
  
  public Double getMaxScore() {
    if (this.scores.isEmpty()) {
      return 0.0;
    }
    // implied else
    return Collections.max(this.scores.values());
  }
  
  
  protected void prepare() throws ResourceInstantiationException {
    if ( (corpora == null) || (corpora.size() == 0) ) {
      throw new ResourceInstantiationException("No corpora given");
    }
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
    scanTypesAndLanguages();
  }
  
  
  private void scanTypesAndLanguages() {
    this.types = new TreeSet<String>();
    this.languages = new TreeSet<String>();
    for (UnorderedTermPair pair : this.pairCount.keySet()) {
      this.languages.add(pair.getTerm0().getLanguageCode());
      this.languages.add(pair.getTerm1().getLanguageCode());
      this.types.add(pair.getTerm0().getType());
      this.types.add(pair.getTerm1().getType());
    }
  }

  
  protected void processCorpus(Corpus corpus) {
    for (int i=0 ; i < corpus.size() ; i++) {
      boolean wasLoaded = corpus.isDocumentLoaded(i);
      Document document = (Document) corpus.get(i);
      
      addData(document);

      // datastore safety
      if (! wasLoaded) {
        corpus.unloadDocument(document);
        Factory.deleteResource(document);
      }
    }
  }
  
  
  protected void resetScores() {
    this.documents = new HashMap<UnorderedTermPair, Set<String>>();
    this.scores = new HashMap<UnorderedTermPair, Double>();
    this.pairCount = new HashMap<UnorderedTermPair, Integer>();
    resetImplScores();
  }


  /* BEHOLD THE GUBBINS to distinguish the various (potential) types of Pairbanks*/

  protected abstract void addData(Document document);
  
  protected abstract void calculateScores(); 
  
  protected abstract void resetImplScores();

  
  
  public Double getScore(UnorderedTermPair pair) {
    if (scores.containsKey(pair)) {
      return scores.get(pair);
    }
    
    // error code
    return null;
  }
  
  
  
  
  /* Methods for saving as CSV */
  
  public void saveAsCsv(Number threshold, File outputFile) throws GateException {
    PairCsvGenerator generator = new PairCsvGenerator();
    generator.generateAndSaveCsv(this, threshold, outputFile);
  }

  /**
   * Convenience method to save everything in the termbank.
   * @param outputFile
   * @throws GateException
   */
  public void saveAsCsv(File outputFile) throws GateException {
    PairCsvGenerator generator = new PairCsvGenerator();
    generator.generateAndSaveCsv(this, -100.0F, outputFile);
  }
  
  
  
  @Override
  public List<Action> getActions() {
    // lazy instantiation because it's transient
    if (this.actionsList == null) {
      createActions();
    }
    
    return this.actionsList;
  }

  
  /***** CREOLE PARAMETERS *****/

  

}



