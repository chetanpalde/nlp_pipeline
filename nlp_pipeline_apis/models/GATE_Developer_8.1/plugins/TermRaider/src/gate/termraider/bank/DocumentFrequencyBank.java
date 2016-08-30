/*
 *  Copyright (c) 2008-2014, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 *  $Id: DocumentFrequencyBank.java 17967 2014-05-11 16:35:51Z ian_roberts $
 */
package gate.termraider.bank;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Corpus;
import gate.Document;
import gate.Resource;
import gate.creole.ResourceInstantiationException;
import gate.creole.metadata.CreoleParameter;
import gate.creole.metadata.CreoleResource;
import gate.gui.ActionsPublisher;
import gate.termraider.gui.ActionSaveCsv;
import gate.termraider.util.ScoreType;
import gate.termraider.util.Term;
import gate.termraider.util.Utilities;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.Action;
import org.apache.commons.lang.StringEscapeUtils;


@CreoleResource(name = "DocumentFrequencyBank",
icon = "termbank-lr.png",
comment = "Document frequency counter derived from corpora and other DFBs",
helpURL = "http://gate.ac.uk/userguide/sec:creole:termraider:docfrequency")
public class DocumentFrequencyBank extends AbstractTermbank
implements ActionsPublisher{
  
  private static final long serialVersionUID = 8486379203429800194L;

  // Note: corpora parameter inherited from AbstractBank
  private Set<DocumentFrequencyBank> inputBanks;
  
  private Map<String, Set<Term>> stringLookupTable;

  // transient to allow serialization
  protected transient List<Action> actionsList;


  public Resource init() throws ResourceInstantiationException {
    prepare();
    initializeScoreTypes();
    resetScores();
    processInputBanks();
    processCorpora();
    calculateScores();
    return this;
  }
  

  public void cleanup() {
    super.cleanup();
  }
  
  
  
  protected void prepare() throws ResourceInstantiationException {
    if (corpora == null) {
      corpora = new HashSet<Corpus>();
    }
    if (inputBanks == null) {
      inputBanks = new HashSet<DocumentFrequencyBank>();
    }
  }

  
  protected void resetScores() {
    scores = new HashMap<ScoreType, Map<Term,Number>>();
    for (ScoreType st : scoreTypes) {
      scores.put(st, new HashMap<Term, Number>());
    }
    
    documentCount = 0;
    languages = new HashSet<String>();
    types = new HashSet<String>();
    stringLookupTable = new HashMap<String, Set<Term>>();
    termDocuments = new HashMap<Term, Set<String>>();
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
  
  
  protected void processInputBanks() {
    for (DocumentFrequencyBank bank : inputBanks) {
      this.documentCount += bank.documentCount;
      for (Term term : bank.getTerms()) {
        Utilities.incrementMap(getDefaultScores(), term, bank.getFrequencyStrict(term));
      }
    }
  }
  
  
  protected void processDocument(Document document) {
    documentCount++;
    String documentSource = Utilities.sourceOrName(document);
    AnnotationSet candidates = document.getAnnotations(inputASName).get(inputAnnotationTypes);

    Set<Term> documentTerms = new HashSet<Term>();
    for (Annotation candidate : candidates) {
      documentTerms.add(makeTerm(candidate, document));
    }
    
    for (Term term : documentTerms) {
      Utilities.addToMapSet(termDocuments, term, documentSource);
    }
  }

  
  protected void calculateScores() {
    for (Term term : termDocuments.keySet()) {
      this.types.add(term.getType());
      this.languages.add(term.getLanguageCode());
      int df = termDocuments.get(term).size();
      Utilities.setScoreTermValue(scores, getDefaultScoreType(), term, df);
      storeStringLookup(term);
    }

    if (debugMode) {
      System.out.println("Termbank: nbr of terms = " + this.getTerms().size());
    }
  }
  
  
  public int getFrequencyStrict(Term term) {
    if (getDefaultScores().containsKey(term)) {
      return getDefaultScores().get(term).intValue();
    }
    
    return 0;
  }
  
  
  public int getFrequencyLax(Term term) {
    // Try for an exact match first
    if (getDefaultScores().containsKey(term)) {
      return getDefaultScores().get(term).intValue();
    }
    
    // Now see if there's one with a blank language code
    String termString = term.getTermString();
    if (stringLookupTable.containsKey(termString)) {
      for (Term testTerm : stringLookupTable.get(termString)) {
        if (testTerm.closeMatch(term)) {
          return getDefaultScores().get(testTerm).intValue();
        }
      }
    }
    
    return 0;
  }
  
  
  @CreoleParameter(comment = "Other DFBs to compile into the new one")
  public void setInputBanks(Set<DocumentFrequencyBank> inputBanks) {
    this.inputBanks = inputBanks;
  }
  
  public Set<DocumentFrequencyBank> getInputBanks() {
    return this.inputBanks;
  }


  @Override
  public List<Action> getActions() {
    // lazy instantiation because actionsList is transient
    if (actionsList == null) {
      createActions();
    }
    
    return this.actionsList;
  }


  private void storeStringLookup(Term term) {
    String termString = term.getTermString();
    Set<Term> terms;
    if (stringLookupTable.containsKey(termString)) {
      terms = stringLookupTable.get(termString);
    }
    else {
      terms = new HashSet<Term>();
    }
    terms.add(term);
    stringLookupTable.put(termString, terms);
  }
  

  protected void initializeScoreTypes() {
    // Whatever this is called, it must be the reference
    // document frequency, so we will only need
    // to use getDefaultScoreType() later
    this.scoreTypes = new ArrayList<ScoreType>();
    this.scoreTypes.add(new ScoreType(scoreProperty));
  }

  
  @CreoleParameter(comment = "name of main score",
          defaultValue = "documentFrequency")
  public void setScoreProperty(String name) {
    this.scoreProperty = name;
  }

  @Override
  public Map<String, String> getMiscDataForGui() {
    Map<String, String> result = new HashMap<String, String>();
    result.put("nbr of documents", String.valueOf(this.documentCount));
    result.put("nbr of terms", String.valueOf(this.getDefaultScores().size()));
    result.put("nbr of distinct term strings", String.valueOf(this.stringLookupTable.size()));
    return result;
  }


  public String getCsvSubheader() {
    StringBuilder sb = new StringBuilder();
    sb.append('\n');
    sb.append(',').append(StringEscapeUtils.escapeCsv("_TOTAL_DOCS_"));
    sb.append(',').append(StringEscapeUtils.escapeCsv(""));
    sb.append(',').append(StringEscapeUtils.escapeCsv(""));
    sb.append(',').append(StringEscapeUtils.escapeCsv(Integer.toString(this.getDocumentCount())));
    return sb.toString();
  }
}
