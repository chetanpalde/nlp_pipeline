/*
 *  PronominalCoref.java
 *
 *  Copyright (c) 1995-2012, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 *  Marin Dimitrov, 30/Dec/2001
 *
 *  $Id: PronominalCoref.java 17829 2014-04-14 15:39:44Z markagreenwood $
 */

package gate.creole.coref;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.Gate;
import gate.Node;
import gate.Resource;
import gate.creole.ANNIEConstants;
import gate.creole.AbstractLanguageAnalyser;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;
import gate.creole.Transducer;
import gate.util.Benchmark;
import gate.util.Benchmarkable;
import gate.util.Err;
import gate.util.Files;
import gate.util.SimpleFeatureMapImpl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PronominalCoref extends AbstractLanguageAnalyser
                              implements ANNIEConstants,
                              Benchmarkable {

  private static final long serialVersionUID = 3860815557386683264L;

  public static final String COREF_DOCUMENT_PARAMETER_NAME = "document";

  public static final String COREF_ANN_SET_PARAMETER_NAME = "annotationSetName";

  /** --- */
  private static final boolean DEBUG = false;

  //JAPE grammars
  private static final String QT_GRAMMAR_URL = Files.getGateResource(
              "/creole/coref/quoted_text.jape").toString();
  private static final String PLEON_GRAMMAR_URL = Files.getGateResource(
          "/creole/coref/pleonasm.jape").toString();

  //annotation types
  private static final String QUOTED_TEXT_TYPE = "QuotedText";
  private static final String PLEONASTIC_TYPE = "PleonasticIt";

  //annotation features
  private static final String PRP_CATEGORY = "PRP";
  private static final String PRP$_CATEGORY = "PRP$";

  //scope
  private static final int SENTENCES_IN_SCOPE = 3;
  /** --- */
  private static AnnotationOffsetComparator ANNOTATION_OFFSET_COMPARATOR;
  /** --- */
  private String annotationSetName;
  /** --- */
  private Transducer qtTransducer;
  /** --- */
  private Transducer pleonTransducer;
  /** --- */
  private AnnotationSet defaultAnnotations;
  /** --- */
  private Sentence[] textSentences;
  /** --- */
  private Quote[] quotedText;
  /** --- */
  private Annotation[] pleonasticIt;
  /** --- */
  private Map<Annotation,String> personGender;
  /** --- */
  private HashMap<Annotation,Annotation> anaphor2antecedent;
  /** --- */
  private static final FeatureMap PRP_RESTRICTION;

  private boolean resolveIt = true;
  
  /** default ORGANIZATIONS,LOCATION**/
  private Set<String> inanimatedSet;
  
  private String inanimatedEntityTypes;
  
  private String benchmarkId;

  /** --- */
  static {
    ANNOTATION_OFFSET_COMPARATOR = new AnnotationOffsetComparator();
    PRP_RESTRICTION = new SimpleFeatureMapImpl();
    PRP_RESTRICTION.put(TOKEN_CATEGORY_FEATURE_NAME,PRP_CATEGORY);
  }

  /** Initialise this resource, and return it. */
  @Override
  public Resource init() throws ResourceInstantiationException {

    personGender = new HashMap<Annotation,String>();
    anaphor2antecedent = new HashMap<Annotation,Annotation>();
    inanimatedSet = new HashSet<String>();
    
    //1. initialise quoted text transducer
    URL qtGrammarURL = null;
    try {
      qtGrammarURL = new URL(QT_GRAMMAR_URL);
    } catch(MalformedURLException mue) {
      throw new ResourceInstantiationException(mue);
    }
    FeatureMap params = Factory.newFeatureMap();
    params.put(Transducer.TRANSD_GRAMMAR_URL_PARAMETER_NAME, qtGrammarURL);
    params.put(Transducer.TRANSD_ENCODING_PARAMETER_NAME, "UTF-8");
    if (qtTransducer == null) {
      features = Factory.newFeatureMap();
      Gate.setHiddenAttribute(features, true);
      qtTransducer = (Transducer)Factory.createResource("gate.creole.Transducer",
              params, features);
      qtTransducer.setName("PronominalCoref-QT " + System.currentTimeMillis());
    }
    else {
      qtTransducer.setParameterValues(params);
      qtTransducer.reInit();
    }
    

    //2. initialise pleonastic transducer
    URL pleonGrammarURL = null;
    try {
      pleonGrammarURL = new URL(PLEON_GRAMMAR_URL);
    }
    catch(MalformedURLException mue) {
      throw new ResourceInstantiationException(mue);
    }
    params = Factory.newFeatureMap();
    params.put(Transducer.TRANSD_GRAMMAR_URL_PARAMETER_NAME, pleonGrammarURL);
    params.put(Transducer.TRANSD_ENCODING_PARAMETER_NAME, "UTF-8");
    if (pleonTransducer == null) {
      features = Factory.newFeatureMap();
      Gate.setHiddenAttribute(features, true);
      pleonTransducer = (Transducer)Factory.createResource("gate.creole.Transducer",
              params, features);
      pleonTransducer.setName("PronominalCoref-Pleon " + System.currentTimeMillis());
    }
    else {
      pleonTransducer.setParameterValues(params);
      pleonTransducer.reInit();
    }
    
    return this;
  } // init()

  @Override
  public void cleanup() {
    super.cleanup();
    Factory.deleteResource(qtTransducer);
    Factory.deleteResource(pleonTransducer);
  }

  /** Set the document to run on. */
  @Override
  public void setDocument(Document newDocument) {

    //0. precondition
//    assert (null != newDocument);

    //1. set doc for aggregated components
    qtTransducer.setDocument(newDocument);
    pleonTransducer.setDocument(newDocument);

    //3. delegate
    super.setDocument(newDocument);
  }

  /** --- */
  public void setAnnotationSetName(String annotationSetName) {
    this.annotationSetName = annotationSetName;
  }


  /** --- */
  public String getAnnotationSetName() {
    return annotationSetName;
  }

  /** --- */
  public void setResolveIt(Boolean newValue) {
    this.resolveIt = newValue.booleanValue();
  }

  /** --- */
  public Boolean getResolveIt() {
    return new Boolean(this.resolveIt);
  }


  /**
   * This method runs the coreferencer. It assumes that all the needed parameters
   * are set. If they are not, an exception will be fired.
   */
  @SuppressWarnings("unchecked")
  @Override
  public void execute() throws ExecutionException{

    //0. preconditions
    if(null == this.document) {
      throw new ExecutionException("[coreference] Document is not set!");
    }

    //1. preprocess
    preprocess();
/*
    //2. remove corefs from previous run
    String annSetName = this.annotationSetName == null ? "COREF"
                                                       : this.annotationSetName;

    AnnotationSet corefSet = this.document.getAnnotations(annSetName);
    if (false == corefSet.isEmpty()) {
      corefSet.clear();
    }
*/
    //3.get personal pronouns
    FeatureMap constraintPRP = new SimpleFeatureMapImpl();
    constraintPRP.put(TOKEN_CATEGORY_FEATURE_NAME,PRP_CATEGORY);
    AnnotationSet personalPronouns = this.defaultAnnotations.get(TOKEN_ANNOTATION_TYPE,constraintPRP);

    //4.get possesive pronouns
    FeatureMap constraintPRP$ = new SimpleFeatureMapImpl();
    constraintPRP$.put(TOKEN_CATEGORY_FEATURE_NAME,PRP$_CATEGORY);
    AnnotationSet possesivePronouns = this.defaultAnnotations.get(TOKEN_ANNOTATION_TYPE,constraintPRP$);

    //5.combine them
    List<Annotation> pronouns = new ArrayList<Annotation>();
    if (personalPronouns != null && !personalPronouns.isEmpty()) {
      pronouns.addAll(personalPronouns);
    }

    if (possesivePronouns != null && !possesivePronouns.isEmpty()) {
      pronouns.addAll(possesivePronouns);
    }

    //6.do we have pronouns at all?
    if (pronouns.isEmpty()) {
      //do nothing
      return;
    }

    //7.sort them according to offset
    Annotation[] arrPronouns = pronouns.toArray(new Annotation[pronouns.size()]);
    Arrays.sort(arrPronouns,ANNOTATION_OFFSET_COMPARATOR);

    //8.cleanup - ease the GC
    pronouns = null;
    personalPronouns = null;
    possesivePronouns = null;

    int prnSentIndex = 0;


    //10. process all pronouns
    for (int i=0; i< arrPronouns.length; i++) {
      Annotation currPronoun = arrPronouns[i];
      while (this.textSentences[prnSentIndex].getEndOffset().longValue() <
                                      currPronoun.getEndNode().getOffset().longValue()) {
        prnSentIndex++;
      }

      Sentence currSentence = this.textSentences[prnSentIndex];
      assert (currSentence.getStartOffset().longValue() <= currPronoun.getStartNode().getOffset().longValue());
      assert (currSentence.getEndOffset().longValue() >= currPronoun.getEndNode().getOffset().longValue());

      //11. find antecedent (if any) for pronoun
      Annotation antc = findAntecedent(currPronoun,prnSentIndex);

      //12. add to the ana2ant hashtable
      this.anaphor2antecedent.put(currPronoun,antc);
    }

    //done
  }


  /** --- */
  public Map<Annotation,Annotation> getResolvedAnaphora() {
    return this.anaphor2antecedent;
  }

  /** --- */
  private Annotation findAntecedent(Annotation currPronoun,int prnSentIndex) {

    //0. preconditions
    assert (null != currPronoun);
    assert (prnSentIndex >= 0);
    assert (currPronoun.getType().equals(TOKEN_ANNOTATION_TYPE));
    assert (currPronoun.getFeatures().get(TOKEN_CATEGORY_FEATURE_NAME).equals(PRP_CATEGORY) ||
                      currPronoun.getFeatures().get(TOKEN_CATEGORY_FEATURE_NAME).equals(PRP$_CATEGORY));

    //1.
    String strPronoun = (String)currPronoun.getFeatures().get(TOKEN_STRING_FEATURE_NAME);

    assert (null != strPronoun);

    //2. delegate processing to the appropriate methods
    if (strPronoun.equalsIgnoreCase("HE") ||
        strPronoun.equalsIgnoreCase("HIM") ||
        strPronoun.equalsIgnoreCase("HIS") ||
        strPronoun.equalsIgnoreCase("HIMSELF")) {
      return _resolve$HE$HIM$HIS$HIMSELF$(currPronoun,prnSentIndex);
    }
    else if (strPronoun.equalsIgnoreCase("SHE") ||
              strPronoun.equalsIgnoreCase("HER") ||
              strPronoun.equalsIgnoreCase("HERS") ||
              strPronoun.equalsIgnoreCase("HERSELF")) {
      return _resolve$SHE$HER$HERS$HERSELF$(currPronoun,prnSentIndex);
    }
    else if (strPronoun.equalsIgnoreCase("IT") ||
              strPronoun.equalsIgnoreCase("ITS") ||
              strPronoun.equalsIgnoreCase("ITSELF")) {
      return _resolve$IT$ITS$ITSELF$(currPronoun,prnSentIndex);
    }
    else if (strPronoun.equalsIgnoreCase("I") ||
              strPronoun.equalsIgnoreCase("ME") ||
              strPronoun.equalsIgnoreCase("MY") ||
              strPronoun.equalsIgnoreCase("MINE") ||
              strPronoun.equalsIgnoreCase("MYSELF")) {
      return _resolve$I$ME$MY$MINE$MYSELF$(currPronoun,prnSentIndex);
    }
    else {
      if (DEBUG) {
        gate.util.Err.println("["+strPronoun+"] is not handled yet...");
      }
      return null;
    }
  }


  boolean isPleonastic(Annotation pronoun) {

    //0. preconditions
    assert (null != pronoun);
    String str = (String)pronoun.getFeatures().get(TOKEN_STRING_FEATURE_NAME);
    assert (str.equalsIgnoreCase("IT"));

    //1. do we have pleonasms in this text?
    if (this.pleonasticIt.length == 0) {
      return false;
    }

    //2. find closest pleonasm index
    @SuppressWarnings("unchecked")
    int closestPleonasmIndex = Arrays.binarySearch(this.pleonasticIt,
                                                             pronoun,
                                                             ANNOTATION_OFFSET_COMPARATOR);
    //normalize index
    if (closestPleonasmIndex < 0) {
      closestPleonasmIndex = -closestPleonasmIndex -1 -1;
    }

    //still not good?
    if (closestPleonasmIndex < 0) {
      closestPleonasmIndex = 0;
    }

    //get closest pleonasm
    Annotation pleonasm = this.pleonasticIt[closestPleonasmIndex];

//System.out.println(pleonasm);
//System.out.println(pronoun);

    //3. return true only if the proboun is contained in pleonastic fragment
    boolean result =  (pleonasm.getStartNode().getOffset().intValue() <= pronoun.getStartNode().getOffset().intValue()
            &&
            pleonasm.getEndNode().getOffset().intValue() >= pronoun.getEndNode().getOffset().intValue());
//System.out.println("is pleon=["+result+"]");
    return result;
  }


  /** --- */
  private Annotation _resolve$HE$HIM$HIS$HIMSELF$(Annotation pronoun, int sentenceIndex) {

    //0. preconditions
    assert (pronoun.getType().equals(TOKEN_ANNOTATION_TYPE));
    assert (pronoun.getFeatures().get(TOKEN_CATEGORY_FEATURE_NAME).equals(PRP_CATEGORY) ||
                      pronoun.getFeatures().get(TOKEN_CATEGORY_FEATURE_NAME).equals(PRP$_CATEGORY));
    String pronounString = (String)pronoun.getFeatures().get(TOKEN_STRING_FEATURE_NAME);
    assert (pronounString.equalsIgnoreCase("HE") ||
                      pronounString.equalsIgnoreCase("HIM") ||
                      pronounString.equalsIgnoreCase("HIS") ||
                      pronounString.equalsIgnoreCase("HIMSELF"));

    //1.
    boolean antecedentFound = false;
    int scopeFirstIndex = sentenceIndex - SENTENCES_IN_SCOPE;
    if (scopeFirstIndex < 0 ) scopeFirstIndex = 0;

    int currSentenceIndex = sentenceIndex;
    Annotation bestAntecedent = null;

    while (currSentenceIndex >= scopeFirstIndex || antecedentFound == false) {
      Sentence currSentence = this.textSentences[currSentenceIndex];
      AnnotationSet persons = currSentence.getPersons();

      Iterator<Annotation> it = persons.iterator();
      while (it.hasNext()) {
        Annotation currPerson = it.next();
        String gender = this.personGender.get(currPerson);

        if (null == gender ||
            gender.equalsIgnoreCase("MALE") ||
            gender.equalsIgnoreCase("UNKNOWN")) {
          //hit
          antecedentFound = true;

          if (null == bestAntecedent) {
            bestAntecedent = currPerson;
          }
          else {
            bestAntecedent = _chooseAntecedent$HE$HIM$HIS$SHE$HER$HERS$HIMSELF$HERSELF$(bestAntecedent,currPerson,pronoun);
          }
        }
      }

      if (0 == currSentenceIndex--)
        break;

    }

    return bestAntecedent;
  }


  /** --- */
  private Annotation _resolve$SHE$HER$HERS$HERSELF$(Annotation pronoun, int sentenceIndex) {

    //0. preconditions
    assert (pronoun.getType().equals(TOKEN_ANNOTATION_TYPE));
    assert (pronoun.getFeatures().get(TOKEN_CATEGORY_FEATURE_NAME).equals(PRP_CATEGORY) ||
                      pronoun.getFeatures().get(TOKEN_CATEGORY_FEATURE_NAME).equals(PRP$_CATEGORY));
    String pronounString = (String)pronoun.getFeatures().get(TOKEN_STRING_FEATURE_NAME);
    assert (pronounString.equalsIgnoreCase("SHE") ||
                      pronounString.equalsIgnoreCase("HER") ||
                      pronounString.equalsIgnoreCase("HERS") ||
                      pronounString.equalsIgnoreCase("HERSELF"));

    //1.
    boolean antecedentFound = false;
    int scopeFirstIndex = sentenceIndex - SENTENCES_IN_SCOPE;
    if (scopeFirstIndex < 0 ) scopeFirstIndex = 0;
    int currSentenceIndex = sentenceIndex;
    Annotation bestAntecedent = null;

    while (currSentenceIndex >= scopeFirstIndex || antecedentFound == false) {
      Sentence currSentence = this.textSentences[currSentenceIndex];
      AnnotationSet persons = currSentence.getPersons();

      Iterator<Annotation> it = persons.iterator();
      while (it.hasNext()) {
        Annotation currPerson = it.next();
        String gender = this.personGender.get(currPerson);

        if (null == gender ||
            gender.equalsIgnoreCase("FEMALE") ||
            gender.equalsIgnoreCase("UNKNOWN")) {
          //hit
          antecedentFound = true;

          if (null == bestAntecedent) {
            bestAntecedent = currPerson;
          }
          else {
            bestAntecedent = _chooseAntecedent$HE$HIM$HIS$SHE$HER$HERS$HIMSELF$HERSELF$(bestAntecedent,currPerson,pronoun);
          }
        }
      }

      if (0 == currSentenceIndex--)
        break;
    }

    return bestAntecedent;
  }


  /** --- */
  private Annotation _resolve$IT$ITS$ITSELF$(Annotation pronoun, int sentenceIndex) {
    //do not resolve it pronouns if disabled by the user
    if (! resolveIt)
      return null;

    //0. preconditions
    assert (pronoun.getType().equals(TOKEN_ANNOTATION_TYPE));
    assert (pronoun.getFeatures().get(TOKEN_CATEGORY_FEATURE_NAME).equals(PRP_CATEGORY) ||
                      pronoun.getFeatures().get(TOKEN_CATEGORY_FEATURE_NAME).equals(PRP$_CATEGORY));
    String pronounString = (String)pronoun.getFeatures().get(TOKEN_STRING_FEATURE_NAME);
    assert (pronounString.equalsIgnoreCase("IT") ||
                      pronounString.equalsIgnoreCase("ITS") ||
                      pronounString.equalsIgnoreCase("ITSELF"));

    //0.5 check if the IT is pleonastic
    if (pronounString.equalsIgnoreCase("IT") &&
        isPleonastic(pronoun)) {
//System.out.println("PLEONASM...");
      return null;
    }

    //1.
    int scopeFirstIndex = sentenceIndex - 1;
    if (scopeFirstIndex < 0 ) scopeFirstIndex = 0;

    int currSentenceIndex = sentenceIndex;
    Annotation bestAntecedent = null;

    while (currSentenceIndex >= scopeFirstIndex) {

      Sentence currSentence = this.textSentences[currSentenceIndex];
      Set<Annotation> org_loc = currSentence.getInanimated();

      Iterator<Annotation> it = org_loc.iterator();
      while (it.hasNext()) {
        Annotation currOrgLoc = it.next();

        if (null == bestAntecedent) {
          //discard cataphoric references
          if (currOrgLoc.getStartNode().getOffset().longValue() <
                                          pronoun.getStartNode().getOffset().longValue()) {
            bestAntecedent = currOrgLoc;
          }
        }
        else {
          bestAntecedent = this._chooseAntecedent$IT$ITS$ITSELF$(bestAntecedent,currOrgLoc,pronoun);
        }
      }

      if (0 == currSentenceIndex--)
        break;
    }

    return bestAntecedent;
  }


  /** --- */
  private Annotation _resolve$I$ME$MY$MINE$MYSELF$(Annotation pronoun, int sentenceIndex) {

    //0. preconditions
    assert (pronoun.getType().equals(TOKEN_ANNOTATION_TYPE));
    assert (pronoun.getFeatures().get(TOKEN_CATEGORY_FEATURE_NAME).equals(PRP_CATEGORY) ||
                      pronoun.getFeatures().get(TOKEN_CATEGORY_FEATURE_NAME).equals(PRP$_CATEGORY));
    String pronounString = (String)pronoun.getFeatures().get(TOKEN_STRING_FEATURE_NAME);
    assert (pronounString.equalsIgnoreCase("I") ||
                      pronounString.equalsIgnoreCase("MY") ||
                      pronounString.equalsIgnoreCase("ME") ||
                      pronounString.equalsIgnoreCase("MINE") ||
                      pronounString.equalsIgnoreCase("MYSELF"));

    //0.5 sanity check
    //if there are not quotes at all in the text then exit
    if (0 == this.quotedText.length) {
//System.out.println("TEXT WITH NO QUOTES ENCOUNTERED...");
      return null;
    }


    //1.
    Annotation bestAntecedent = null;

    @SuppressWarnings("unchecked")
    int closestQuoteIndex = Arrays.binarySearch(this.quotedText,pronoun,ANNOTATION_OFFSET_COMPARATOR);
    //normalize index
    if (closestQuoteIndex < 0) {
      closestQuoteIndex = -closestQuoteIndex -1 -1;
    }

    //still not good?
    if (closestQuoteIndex < 0) {
      closestQuoteIndex = 0;
    }

    //get closest Quote
    Quote quoteContext = this.quotedText[closestQuoteIndex];

    //assure that the pronoun is contained in the quoted text fragment
    //otherwise exit

    if (pronoun.getStartNode().getOffset().intValue() > quoteContext.getEndOffset().intValue() ||
        pronoun.getEndNode().getOffset().intValue() < quoteContext.getStartOffset().intValue()) {
      //oops, probably incorrect text - I/My/Me is not part of quoted text fragment
      //exit
//System.out.println("Oops! ["+pronounString+"] not part of quoted fragment...");
      return null;
    }

    //get the Persons that precede/succeed the quoted fragment
    //the order is:
    //
    //[1]. if there exists a Person or pronoun in {he, she} following the quoted fragment but
    //in the same sentence, then use it
    //i.e.  ["PRN1(x)...", said X ...A, B, C ....]
    //
    //[2]. if there is a Person (NOT a pronoun) in the same sentence,
    // preceding the quote, then use it
    //i.e. . [A, B, C...X ..."PRN1(x) ..."...]
    //

    //try [1]
    //get the succeeding Persons/pronouns
    Set<Annotation> succCandidates = quoteContext.getAntecedentCandidates(Quote.ANTEC_AFTER);
    if (false == succCandidates.isEmpty()) {
      //cool, we have candidates, pick up the one closest to the end quote
      Iterator<Annotation> it = succCandidates.iterator();

      while (it.hasNext()) {
        Annotation currCandidate = it.next();
        if (null == bestAntecedent || ANNOTATION_OFFSET_COMPARATOR.compare(bestAntecedent,currCandidate) > 0) {
          //wow, we have a candidate that is closer to the quote
          bestAntecedent = currCandidate;
        }
      }
    }

    //try [2]
    //get the preceding Persons/pronouns
    if (null == bestAntecedent) {
      Set<Annotation> precCandidates = quoteContext.getAntecedentCandidates(Quote.ANTEC_BEFORE);
      if (false == precCandidates.isEmpty()) {
        //cool, we have candidates, pick up the one closest to the end quote
        Iterator<Annotation> it = precCandidates.iterator();

        while (it.hasNext()) {
          Annotation currCandidate = it.next();
          if (null == bestAntecedent || ANNOTATION_OFFSET_COMPARATOR.compare(bestAntecedent,currCandidate) < 0) {
            //wow, we have a candidate that is closer to the quote
            bestAntecedent = currCandidate;
          }
        }
      }
    }

    //try [3]
    //get the Persons/pronouns back in context
    if (null == bestAntecedent) {
      Set<Annotation> precCandidates = quoteContext.getAntecedentCandidates(Quote.ANTEC_BACK);
      if (false == precCandidates.isEmpty()) {
        //cool, we have candidates, pick up the one closest to the end quote
        Iterator<Annotation> it = precCandidates.iterator();

        while (it.hasNext()) {
          Annotation currCandidate = it.next();
          if (null == bestAntecedent || ANNOTATION_OFFSET_COMPARATOR.compare(bestAntecedent,currCandidate) > 0) {
            //wow, we have a candidate that is closer to the quote
            bestAntecedent = currCandidate;
          }
        }
      }
    }

    return bestAntecedent;
  }


  /** --- */
  @SuppressWarnings("unchecked")
  private void preprocess() throws ExecutionException {

    //0.5 cleanup
    this.personGender.clear();
    this.anaphor2antecedent.clear();

    //1.get all annotation in the input set
    if ( this.annotationSetName == null || this.annotationSetName.equals("")) {
      this.defaultAnnotations = this.document.getAnnotations();
    }
    else {
      this.defaultAnnotations = this.document.getAnnotations(annotationSetName);
    }

    //if none found, print warning and exit
    if (this.defaultAnnotations == null || this.defaultAnnotations.isEmpty()) {
      Err.prln("Coref Warning: No annotations found for processing!");
      return;
    }

    // get the list of inanimated entity types 
    if (inanimatedEntityTypes==null||inanimatedEntityTypes.equals(""))
      inanimatedEntityTypes="Organization;Location";
    
    String[] types = inanimatedEntityTypes.split(";");
    this.inanimatedSet.addAll(Arrays.asList(types));
        
    //2.1 remove QT annotations if left from previous execution
    AnnotationSet qtSet = this.defaultAnnotations.get(QUOTED_TEXT_TYPE);
    if (qtSet != null && !qtSet.isEmpty()) {
      this.defaultAnnotations.removeAll(qtSet);
    }

    //2.2. run quoted text transducer to generate "Quoted Text" annotations
    Benchmark.executeWithBenchmarking(this.qtTransducer,
            Benchmark.createBenchmarkId("qtTransducer",
                    getBenchmarkId()), this, null);

    //3.1 remove pleonastic annotations if left from previous execution
    AnnotationSet pleonSet = this.defaultAnnotations.get(PLEONASTIC_TYPE);
    if (pleonSet != null && !pleonSet.isEmpty()) {
      this.defaultAnnotations.removeAll(pleonSet);
    }

    //3.2 run quoted text transducer to generate "Pleonasm" annotations
    Benchmark.executeWithBenchmarking(pleonTransducer,
            Benchmark.createBenchmarkId("pleonTransducer",
                    getBenchmarkId()), this, null);

    //4.get all SENTENCE annotations
    AnnotationSet sentenceAnnotations = this.defaultAnnotations.get(SENTENCE_ANNOTATION_TYPE);

    this.textSentences = new Sentence[sentenceAnnotations.size()];
    
    Annotation[]  sentenceArray = sentenceAnnotations.toArray(new Annotation[sentenceAnnotations.size()]);
    Arrays.sort(sentenceArray,ANNOTATION_OFFSET_COMPARATOR);

    for (int i=0; i< sentenceArray.length; i++) {

      Annotation currSentence = sentenceArray[i];
      Long sentStartOffset = currSentence.getStartNode().getOffset();
      Long sentEndOffset = currSentence.getEndNode().getOffset();
      
      AnnotationSet tempASOffsets = this.defaultAnnotations.getContained(
              sentStartOffset,sentEndOffset);

      //4.1. get PERSONS in this sentence
      AnnotationSet sentPersons = tempASOffsets.get(PERSON_ANNOTATION_TYPE);

      //4.2. get inanimated entities (ORGANIZATIONS,LOCATION) in this sentence
     
      AnnotationSet sentInans = tempASOffsets.get(this.inanimatedSet);

      //4.5. create a Sentence for the SENTENCE annotation
      this.textSentences[i] = new Sentence(i,
                                            0,
                                            sentStartOffset,
                                            sentEndOffset,
                                            sentPersons,
                                            sentInans
                                  );

      //4.6. for all PERSONs in the sentence - find their gender using the
      //orthographic coreferences if the gender of some entity is unknown
      Iterator<Annotation> itPersons = sentPersons.iterator();
      while (itPersons.hasNext()) {
        Annotation currPerson = itPersons.next();
        String gender = this.findPersonGender(currPerson);
        this.personGender.put(currPerson,gender);
      }
    }

    //5. initialise the quoted text fragments
    AnnotationSet sentQuotes = this.defaultAnnotations.get(QUOTED_TEXT_TYPE);

    //if none then return
    if (null == sentQuotes) {
      this.quotedText = new Quote[0];
    }
    else {
      this.quotedText = new Quote[sentQuotes.size()];

      Annotation[] quotesArray = sentQuotes.toArray(new Annotation[sentQuotes.size()]);
      Arrays.sort(quotesArray,ANNOTATION_OFFSET_COMPARATOR);

      for (int i =0; i < quotesArray.length; i++) {
        this.quotedText[i] = new Quote(quotesArray[i],i);
      }
    }

    //6. initialuse the plonastic It annotations
    AnnotationSet plaonasticSet = this.defaultAnnotations.get(PLEONASTIC_TYPE);

    if (null == plaonasticSet) {
      this.pleonasticIt = new Annotation[0];
    }
    else {
      this.pleonasticIt = new Annotation[plaonasticSet.size()];

      Annotation[] quotesArray = plaonasticSet.toArray(new Annotation[plaonasticSet.size()]);
      Arrays.sort(quotesArray,ANNOTATION_OFFSET_COMPARATOR);

      for (int i=0; i< this.pleonasticIt.length; i++) {
        this.pleonasticIt[i] = quotesArray[i];
      }
    }

  }


  /** --- */
  private String findPersonGender(Annotation person) {

    String result = (String)person.getFeatures().get(PERSON_GENDER_FEATURE_NAME);

    if (null==result) {
      //gender is unknown - try to find it from the ortho coreferences
      @SuppressWarnings("unchecked")
      List<Integer> orthoMatches  = (List<Integer>)person.getFeatures().get(ANNOTATION_COREF_FEATURE_NAME);

      if (null != orthoMatches) {
        Iterator<Integer> itMatches = orthoMatches.iterator();

        while (itMatches.hasNext()) {
          Integer correferringID = itMatches.next();
          Annotation coreferringEntity = this.defaultAnnotations.get(correferringID);
          if (coreferringEntity != null) {
            assert (coreferringEntity.getType().equalsIgnoreCase(PERSON_ANNOTATION_TYPE));
            String correferringGender = (String)coreferringEntity.getFeatures().get(PERSON_GENDER_FEATURE_NAME);

            if (null != correferringGender) {
              result = correferringGender;
              break;
            }
          }
        }
      }
    }

    return result;
  }

  @SuppressWarnings("rawtypes")
  private static class AnnotationOffsetComparator implements Comparator {

    private int _getOffset(Object o) {

      if (o instanceof Annotation) {
        return ((Annotation)o).getEndNode().getOffset().intValue();
      }
      else if (o instanceof Sentence) {
        return ((Sentence)o).getStartOffset().intValue();
      }
      else if (o instanceof Quote) {
        return ((Quote)o).getStartOffset().intValue();
      }
      else if (o instanceof Node) {
        return ((Node)o).getOffset().intValue();
      }
      else {
        throw new IllegalArgumentException();
      }
    }

    @Override
    public int compare(Object o1,Object o2) {

      //0. preconditions
      assert (null != o1);
      assert (null != o2);
      assert (o1 instanceof Annotation ||
                        o1 instanceof Sentence ||
                        o1 instanceof Quote ||
                        o1 instanceof Node);
      assert (o2 instanceof Annotation ||
                        o2 instanceof Sentence ||
                        o2 instanceof Quote ||
                        o2 instanceof Node);

      int offset1 = _getOffset(o1);
      int offset2 = _getOffset(o2);

      return offset1 - offset2;
    }
  }


  /** --- */
  private Annotation _chooseAntecedent$HE$HIM$HIS$SHE$HER$HERS$HIMSELF$HERSELF$(Annotation ant1, Annotation ant2, Annotation pronoun) {

    //0. preconditions
    assert (null != ant1);
    assert (null != ant2);
    assert (null != pronoun);
    assert (pronoun.getFeatures().get(TOKEN_CATEGORY_FEATURE_NAME).equals(PRP_CATEGORY) ||
                      pronoun.getFeatures().get(TOKEN_CATEGORY_FEATURE_NAME).equals(PRP$_CATEGORY));
    String pronounString = (String)pronoun.getFeatures().get(TOKEN_STRING_FEATURE_NAME);
    assert (pronounString.equalsIgnoreCase("SHE") ||
                      pronounString.equalsIgnoreCase("HER") ||
                      pronounString.equalsIgnoreCase("HERS") ||
                      pronounString.equalsIgnoreCase("HERSELF") ||
                      pronounString.equalsIgnoreCase("HE") ||
                      pronounString.equalsIgnoreCase("HIM") ||
                      pronounString.equalsIgnoreCase("HIS") ||
                      pronounString.equalsIgnoreCase("HIMSELF"));

    Long offset1 = ant1.getStartNode().getOffset();
    Long offset2 = ant2.getStartNode().getOffset();
    Long offsetPrn = pronoun.getStartNode().getOffset();

    long diff1 = offsetPrn.longValue() - offset1.longValue();
    long diff2 = offsetPrn.longValue() - offset2.longValue();
//    assert (diff1 != 0 && diff2 != 0);
    //reject candidates that overlap with the pronoun
    if (diff1 == 0) {
      return ant2;
    }
    else if (diff2 == 0) {
      return ant1;
    }

    //get the one CLOSEST AND PRECEDING the pronoun
    if (diff1 > 0 && diff2 > 0) {
      //we have [...antecedentA...AntecedentB....pronoun...] ==> choose B
      if (diff1 < diff2)
        return ant1;
      else
        return ant2;
    }
    else if (diff1 < 0 && diff2 < 0) {
      //we have [...pronoun ...antecedentA...AntecedentB.......] ==> choose A
      if (Math.abs(diff1) < Math.abs(diff2))
        return ant1;
      else
          return ant2;
    }
    else {
      assert (Math.abs(diff1 + diff2) < Math.abs(diff1) + Math.abs(diff2));
      //we have [antecedentA...pronoun...AntecedentB] ==> choose A
      if (diff1 > 0)
        return ant1;
      else
        return ant2;
    }
  }

  /** --- */
  private Annotation _chooseAntecedent$IT$ITS$ITSELF$(Annotation ant1, Annotation ant2, Annotation pronoun) {

    //0. preconditions
    assert (null != ant1);
    assert (null != ant2);
    assert (null != pronoun);
    assert (pronoun.getFeatures().get(TOKEN_CATEGORY_FEATURE_NAME).equals(PRP_CATEGORY) ||
                      pronoun.getFeatures().get(TOKEN_CATEGORY_FEATURE_NAME).equals(PRP$_CATEGORY));
    String pronounString = (String)pronoun.getFeatures().get(TOKEN_STRING_FEATURE_NAME);

    assert (pronounString.equalsIgnoreCase("IT") ||
                      pronounString.equalsIgnoreCase("ITS") ||
                      pronounString.equalsIgnoreCase("ITSELF"));

    Long offset1 = ant1.getStartNode().getOffset();
    Long offset2 = ant2.getStartNode().getOffset();
    Long offsetPrn = pronoun.getStartNode().getOffset();
    long diff1 = offsetPrn.longValue() - offset1.longValue();
    long diff2 = offsetPrn.longValue() - offset2.longValue();
//    assert (diff1 != 0 && diff2 != 0);
    //reject candidates that overlap with the pronoun
    if (diff1 == 0) {
      return ant2;
    }
    else if (diff2 == 0) {
      return ant1;
    }


    //get the one CLOSEST AND PRECEDING the pronoun
    if (diff1 > 0 && diff2 > 0) {
      //we have [...antecedentA...AntecedentB....pronoun...] ==> choose B
      if (diff1 < diff2)
        return ant1;
      else
        return ant2;
    }
    else if (diff1 > 0){
      assert (Math.abs(diff1 + diff2) < Math.abs(diff1) + Math.abs(diff2));
      //we have [antecedentA...pronoun...AntecedentB] ==> choose A
      return ant1;
    }
    else if (diff2 > 0){
      assert (Math.abs(diff1 + diff2) < Math.abs(diff1) + Math.abs(diff2));
      //we have [antecedentA...pronoun...AntecedentB] ==> choose A
      return ant2;
    }
    else {
      //both possible antecedents are BEHIND the anaophoric pronoun - i.e. we have either
      //cataphora, or nominal antecedent, or an antecedent that is further back in scope
      //in any case - discard the antecedents
      return null;
    }
  }


  /** --- */
  private class Quote {

    /** --- */
    public static final int ANTEC_AFTER = 1;
    /** --- */
    public static final int ANTEC_BEFORE = 2;
    /** --- */
    public static final int ANTEC_BACK = 3;
    /** --- */
    private Set<Annotation> antecedentsBefore;
    /** --- */
    private Set<Annotation> antecedentsAfter;
    /** --- */
    private Set<Annotation> antecedentsBackInContext;
    /** --- */
    private Annotation quoteAnnotation;
    /** --- */
    private int quoteIndex;

    /** --- */
    public Quote(Annotation quoteAnnotation, int index) {

      this.quoteAnnotation = quoteAnnotation;
      this.quoteIndex = index;
      init();
    }

    /** --- */
    private void init() {

      //0.preconditions
      assert (null != textSentences);

      //0.5 create a restriction for PRP pos tokens
      FeatureMap prpTokenRestriction = new SimpleFeatureMapImpl();
      prpTokenRestriction.put(TOKEN_CATEGORY_FEATURE_NAME,PRP_CATEGORY);

      //1. generate the precPersons set

      //1.1 locate the sentece containing the opening quote marks
      @SuppressWarnings("unchecked")
      int quoteStartPos = Arrays.binarySearch(textSentences,
                                                        this.quoteAnnotation.getStartNode(),
                                                        ANNOTATION_OFFSET_COMPARATOR);

      //normalize index
      int startSentenceIndex = quoteStartPos >= 0 ? quoteStartPos
                                                  : -quoteStartPos -1 -1; // blame Sun, not me
      //still not good?
      if (startSentenceIndex < 0) {
        startSentenceIndex = 0;
      }

      //1.2. get the persons and restrict to these that precede the quote (i.e. not contained
      //in the quote)
      this.antecedentsBefore = generateAntecedentCandidates(startSentenceIndex,
                                                            this.quoteIndex,
                                                            ANTEC_BEFORE);


      //2. generate the precPersonsInCOntext set
      //2.1. get the persons from the sentence precedeing the sentence containing the quote start
      if (startSentenceIndex > 0) {
        this.antecedentsBackInContext = generateAntecedentCandidates(startSentenceIndex -1,
                                                                    this.quoteIndex,
                                                                    ANTEC_BACK);
      }

      //2. generate the succ  Persons set
      //2.1 locate the sentece containing the closing quote marks
      @SuppressWarnings("unchecked")
      int quoteEndPos = Arrays.binarySearch(textSentences,
                                                        this.quoteAnnotation.getEndNode(),
                                                        ANNOTATION_OFFSET_COMPARATOR);

      //normalize it
      int endSentenceIndex = quoteEndPos >= 0 ? quoteEndPos
                                              : -quoteEndPos -1 -1; // blame Sun, not me
      //still not good?
      if (endSentenceIndex < 0) {
        endSentenceIndex = 0;
      }

      this.antecedentsAfter = generateAntecedentCandidates(endSentenceIndex,
                                                            this.quoteIndex,
                                                            ANTEC_AFTER);
      //generate t
    }


    /** --- */
    private Set<Annotation> generateAntecedentCandidates(int sentenceNumber,
                                                        int quoteNumber ,
                                                        int mode) {

      //0. preconditions
      assert (sentenceNumber >=0);
      assert (quoteNumber >=0);
      assert (mode == Quote.ANTEC_AFTER ||
                        mode == Quote.ANTEC_BEFORE ||
                        mode == Quote.ANTEC_BACK);

      //1. get sentence
     Sentence sentence = textSentences[sentenceNumber];

      //2. get the persons
      Set<Annotation> antecedents = new HashSet<Annotation>(sentence.getPersons());

      //4. now get the he/she pronouns in the relevant context
      AnnotationSet annotations = null;

      switch(mode) {

        case ANTEC_BEFORE:
          annotations = defaultAnnotations.getContained(sentence.getStartOffset(),
                                                      this.getStartOffset());
          break;

        case ANTEC_AFTER:
          annotations = defaultAnnotations.getContained(this.getEndOffset(),
                                                     sentence.getEndOffset());
          break;

        case ANTEC_BACK:
          annotations = defaultAnnotations.getContained(sentence.getStartOffset(),
                                                     sentence.getEndOffset());
          break;
      }

      //4. get the pronouns
      //restrict to he/she pronouns
      if (null != annotations) {
        AnnotationSet pronouns = annotations.get(TOKEN_ANNOTATION_TYPE,PRP_RESTRICTION);

        if (null != pronouns) {

          Iterator<Annotation> it = pronouns.iterator();
          while (it.hasNext()) {
            Annotation currPronoun = it.next();
            //add to succPersons only if HE/SHE
            String pronounString = (String)currPronoun.getFeatures().get(TOKEN_STRING_FEATURE_NAME);

            if (null != pronounString &&
                (pronounString.equalsIgnoreCase("he") || pronounString.equalsIgnoreCase("she"))
                )
              antecedents.add(currPronoun);
          }//while
        }//if
      }//if


      //3. depending on the mode, may have to restrict persons to these that precede/succeed
      //the quoted fragment
      //
      //for ANTEC_BEFORE, get the ones #preceding# the quote, contained in the sentence where
      //the quote *starts*
      //
      //for ANTEC_AFTER, get the ones #succeeding# the quote, contained in the sentence where
      //the quote *ends*
      //
      //for ANTEC_BACK, we are operating in the context of the sentence previous to the
      //sentence where the quote starts. I.e. we're resolbinf a case like
      // [sss "q1q1q1q1" s1s1s1s1]["q2q2q2q2"]
      //...and we want to get the entities from the s1s1 part - they *succeed* the #previous# quote
      //Note that the cirrent sentence is the first one, not the second
      //
      Iterator<Annotation> itPersons = antecedents.iterator();

      while (itPersons.hasNext()) {
        Annotation currPerson = itPersons.next();

        //cut
        if (Quote.ANTEC_BEFORE == mode &&
            currPerson.getStartNode().getOffset().intValue() > getStartOffset().intValue()) {
          //restrict only to persosn preceding
          itPersons.remove();
        }
        else if (Quote.ANTEC_AFTER == mode &&
                currPerson.getStartNode().getOffset().intValue() < getEndOffset().intValue()) {
          //restrict only to persons succeeding the quote
          itPersons.remove();
        }
        else if (Quote.ANTEC_BACK == mode) {
          //this one is tricky
          //locate the quote previous to the one we're resolving
          //(since we're operating in the sentence previous to the quote being resolved
          //wew try to find if any quote (prevQuote) exist in this sentence and get the
          //persons succeeding it)

          //get prev quote
          //is the curr quote the first one?
          if (quoteNumber >0) {
            Quote prevQuote = PronominalCoref.this.quotedText[quoteNumber-1];

            //restrict to the succeeding persons
            if (currPerson.getStartNode().getOffset().longValue() < prevQuote.getEndOffset().longValue()) {
              itPersons.remove();
            }
          }
        }
      }

      return antecedents;
    }

    /** --- */
    public Long getStartOffset() {
      return this.quoteAnnotation.getStartNode().getOffset();
    }

    /** --- */
    public Long getEndOffset() {
      return this.quoteAnnotation.getEndNode().getOffset();
    }

    /** --- */
    public Set<Annotation> getAntecedentCandidates(int type) {

      switch(type) {

        case ANTEC_AFTER:
          return null != this.antecedentsAfter ? 
                         this.antecedentsAfter : 
                         new HashSet<Annotation>();

        case ANTEC_BEFORE:
          return null != this.antecedentsBefore ? 
                         this.antecedentsBefore : 
                         new HashSet<Annotation>();

        case ANTEC_BACK:
          return null != this.antecedentsBackInContext ? 
                  this.antecedentsBackInContext : 
                  new HashSet<Annotation>();

        default:
          throw new IllegalArgumentException();
      }
    }

  }


  /** --- */
  private class Sentence {

    /** --- */
    @SuppressWarnings("unused")
    private int sentNumber;
    /** --- */
    @SuppressWarnings("unused")
    private int paraNumber;
    /** --- */
    private Long startOffset;
    /** --- */
    private Long endOffset;
    /** --- */
    private AnnotationSet persons;
    /** --- */
    private AnnotationSet inanimated;

    /** --- */
    public Sentence(int sentNumber,
                    int paraNumber,
                    Long startOffset,
                    Long endOffset,
                    AnnotationSet persons,
                    AnnotationSet inanimated) {

      this.sentNumber = sentNumber;
      this.paraNumber = paraNumber;
      this.startOffset = startOffset;
      this.endOffset = endOffset;
      this.persons = persons;
      this.inanimated = inanimated;
    }

    /** --- */
    public Long getStartOffset() {
      return this.startOffset;
    }

    /** --- */
    public Long getEndOffset() {
      return this.endOffset;
    }

    /** --- */
    public AnnotationSet getPersons() {
      return this.persons;
    }

    public AnnotationSet getInanimated() {
      return this.inanimated;
    }
    
  }


  public String getInanimatedEntityTypes() {
    return inanimatedEntityTypes;
  }

  public void setInanimatedEntityTypes(String inanimatedEntityTypes) {
    this.inanimatedEntityTypes = inanimatedEntityTypes;
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

}
