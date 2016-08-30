/*
 *  OrthoMatcher.java
 *
 *  Copyright (c) 1995-2012, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 *  Kalina Bontcheva, 24/August/2001
 *
 *  Major update by Andrew Borthwick of Spock Networks, 11/13/2007 - 8/3/2008:
 *    1.  matchWithPrevious now searches for matching annotations in order, starting from current and working backwards
 *    until it finds a match.  This compares with the previous behavior, which searched randomly among previous annotations
 *    for a match (because it used an iterator across an AnnotationSet, whereas now we iterate across an ArrayList<Annotation>)
 *    2.  We no longer require that identical strings always refer to the same entity.  We can correctly match
 *    the sequence "David Jones ... David ... David Smith ... David" as referring to two people, tying the first
 *    David to "David Jones" and the second David to "David Smith".  Ditto with David Jones .. Mr. Jones ..
 *    Richard Jones .. Mr. Jones
 *    3.  We now allow for nickname matches for Persons (David = Dave) via the "fuzzyMatch" method which is referenced
 *    in some of the matching rules.
 *    4.  Optional parameter highPrecisionOrgs only allows high precision matches for organizations and
 *    turns off the riskier rules.  Under this option, need to match on something like IBM = IBM Corp.
 *    5.  Various fixes to a number of rules
 *
 *  $Id: OrthoMatcher.java 8929 2007-07-12 16:49:55Z ian_roberts $
 */

package gate.creole.orthomatcher;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Resource;
import gate.creole.AbstractLanguageAnalyser;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;
import gate.creole.metadata.CreoleParameter;
import gate.creole.metadata.CreoleResource;
import gate.creole.metadata.Optional;
import gate.creole.metadata.RunTime;
import gate.util.BomStrippingInputStreamReader;
import gate.util.GateRuntimeException;
import gate.util.InvalidOffsetException;
import gate.util.OffsetComparator;
import gate.util.Out;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
@CreoleResource(name="ANNIE OrthoMatcher", comment="ANNIE orthographical coreference component.", helpURL="http://gate.ac.uk/userguide/sec:annie:orthomatcher", icon="ortho-matcher")
public class OrthoMatcher extends AbstractLanguageAnalyser {

  private static final long serialVersionUID = -6258229350677707465L;

  protected static final Logger log = Logger.getLogger(OrthoMatcher.class);

  public static final boolean DEBUG = false;

  public static final String
  OM_DOCUMENT_PARAMETER_NAME = "document";

  public static final String
  OM_ANN_SET_PARAMETER_NAME = "annotationSetName";

  public static final String
  OM_CASE_SENSITIVE_PARAMETER_NAME = "caseSensitive";

  public static final String
  OM_ANN_TYPES_PARAMETER_NAME = "annotationTypes";

  public static final String
  OM_ORG_TYPE_PARAMETER_NAME = "organizationType";

  public static final String
  OM_PERSON_TYPE_PARAMETER_NAME = "personType";

  public static final String
  OM_EXT_LISTS_PARAMETER_NAME = "extLists";

  protected static final String CDGLISTNAME = "cdg";
  protected static final String ALIASLISTNAME = "alias";
  protected static final String ARTLISTNAME = "def_art";
  protected static final String PREPLISTNAME = "prepos";
  protected static final String CONNECTORLISTNAME = "connector";
  protected static final String SPURLISTNAME = "spur_match";

  protected static final String PUNCTUATION_VALUE = "punctuation";
  protected static final String THE_VALUE = "The";


  /**the name of the annotation set*/
  protected String annotationSetName;

  /** the types of the annotation */
  protected List<String> annotationTypes = new ArrayList<String>(10);

  /** the organization type*/
  protected String organizationType = ORGANIZATION_ANNOTATION_TYPE;

  /** the person type*/
  protected String personType = PERSON_ANNOTATION_TYPE;

  protected String unknownType = "Unknown";

  /** internal or external list */
  protected boolean extLists = true;

  /** Use only high precision rules for Organizations */
  protected Boolean highPrecisionOrgs = false;

  /** matching unknowns or not*/
  protected boolean matchingUnknowns = true;

  /** This is an internal variable to indicate whether
   *  we matched using a rule that requires that
   *  the newly matched annotation matches all the others
   *  This is needed, because organizations can share
   *  first/last tokens like News and be different
   */
  protected boolean allMatchingNeeded = false;

  //** Orthomatching is not case-sensitive by default*/
  protected boolean caseSensitive = false;

  //protected FeatureMap queryFM = Factory.newFeatureMap();

  // name lookup tables (used for namematch)
  //gave them bigger default size, coz rehash is expensive
  protected HashMap<String, String> alias = new HashMap<String, String>(100);
  protected Set<String> cdg = new HashSet<String>();
  protected HashMap<String, String> spur_match = new HashMap<String, String>(100);
  protected HashMap<String, String> def_art = new HashMap<String, String>(20);
  protected HashMap<String, String> connector = new HashMap<String, String>(20);
  protected HashMap<String, String> prepos = new HashMap<String, String>(30);


  protected AnnotationSet nameAllAnnots = null;

  protected HashMap<Integer, String> processedAnnots = new HashMap<Integer, String>(150);
  protected HashMap<Integer, String> annots2Remove = new HashMap<Integer, String>(75);
  protected List<List<Integer>> matchesDocFeature = new ArrayList<List<Integer>>();
  //maps annotation ids to array lists of tokens
  protected HashMap<Integer, List<Annotation>> tokensMap = new HashMap<Integer, List<Annotation>>(150);
  public Map<Integer, List<Annotation>> getTokensMap() {
    return tokensMap;
  }

  protected Map<Integer, List<Annotation>> normalizedTokensMap = new HashMap<Integer, List<Annotation>>(150);

  protected Annotation shortAnnot;
  protected Annotation longAnnot;

  protected ArrayList<Annotation> tokensLongAnnot;
  protected ArrayList<Annotation> tokensShortAnnot;

  protected ArrayList<Annotation> normalizedTokensLongAnnot, normalizedTokensShortAnnot;

  /**
   * URL to the file containing the definition for this orthomatcher
   */
  private java.net.URL definitionFileURL;

  private Double minimumNicknameLikelihood;

  /** The encoding used for the definition file and associated lists.*/
  private String encoding;

  private Map<Integer,OrthoMatcherRule> rules=new HashMap<Integer,OrthoMatcherRule>();

  /** to be initialized in init() */
  private AnnotationOrthography orthoAnnotation;

  /** @link dependency */
  /*#OrthoMatcher lnkOrthoMatcher;*/

  public OrthoMatcher () {
    annotationTypes.add(organizationType);
    annotationTypes.add(personType);
    annotationTypes.add("Location");
    annotationTypes.add("Date");
  }

  /** Initialise the rules. The orthomatcher loads its build-in rules. */
  private void initRules(){
    //this line should be executed after spur_match is loaded
    rules.put(0,  new MatchRule0(this));
    rules.put(1,  new MatchRule1(this));
    rules.put(2,  new MatchRule2(this));
    rules.put(3,  new MatchRule3(this));
    rules.put(4,  new MatchRule4(this));
    rules.put(5,  new MatchRule5(this));
    rules.put(6,  new MatchRule6(this));
    rules.put(7,  new MatchRule7(this));
    rules.put(8,  new MatchRule8(this));
    rules.put(9,  new MatchRule9(this));
    rules.put(10, new MatchRule10(this));
    rules.put(11, new MatchRule11(this));
    rules.put(12, new MatchRule12(this));
    rules.put(13, new MatchRule13(this));
    rules.put(14, new MatchRule14(this));
    rules.put(15, new MatchRule15(this));
    rules.put(16, new MatchRule16(this));
    rules.put(17, new MatchRule17(this));

  }

  /** Override this method to add, replace, remove rules */
  protected void modifyRules(Map<Integer,OrthoMatcherRule> rules) {

  }

  /** Initialise this resource, and return it. */
  @SuppressWarnings("resource")
  @Override
  public Resource init() throws ResourceInstantiationException {
    //initialise the list of annotations which we will match
    if(definitionFileURL == null){
      throw new ResourceInstantiationException(
      "No URL provided for the definition file!");
    }
    String nicknameFile = null;
    BufferedReader reader = null;
    //at this point we have the definition file
    try{
      reader = new BomStrippingInputStreamReader(
          definitionFileURL.openStream(), encoding);
      String lineRead = null;
      //boolean foundANickname = false;
      while ((lineRead = reader.readLine()) != null){
        int index = lineRead.indexOf(":");
        if (index != -1){
          String nameFile = lineRead.substring(0,index);
          String nameList = lineRead.substring(index+1,lineRead.length());
          if (nameList.equals("nickname")) {
            if (minimumNicknameLikelihood == null) {
              throw new ResourceInstantiationException(
                  "No value for the required parameter " +
                  "minimumNicknameLikelihood!");
            }
            nicknameFile = nameFile;
          } else {
            createAnnotList(nameFile,nameList);
          }
        }// if
      }//while
      reader.close();

      URL nicknameURL = null;
      if (nicknameFile != null)
        nicknameURL = new URL(definitionFileURL, nicknameFile);
      this.orthoAnnotation = new BasicAnnotationOrthography(
              personType,extLists,unknownType,nicknameURL,
              minimumNicknameLikelihood, encoding);
      initRules();
      modifyRules(rules);

    }catch(IOException ioe){
      throw new ResourceInstantiationException(ioe);
    }
    finally {
      IOUtils.closeQuietly(reader);
    }


    return this;
  } // init()


  /**  Run the resource. It doesn't make sense not to override
   *  this in subclasses so the default implementation signals an
   *  exception.
   */
  @Override
  public void execute() throws ExecutionException{
    try{
      //check the input
      if(document == null) {
        throw new ExecutionException(
                "No document for namematch!"
        );
      }
      fireStatusChanged("OrthoMatcher processing: " +  document.getName());

      // get the annotations from document
      if ((annotationSetName == null)|| (annotationSetName.equals("")))
        nameAllAnnots = document.getAnnotations();
      else
        nameAllAnnots = document.getAnnotations(annotationSetName);

      //if none found, print warning and exit
      if ((nameAllAnnots == null) || nameAllAnnots.isEmpty()) {
        Out.prln("OrthoMatcher Warning: No annotations found for processing");
        return;
      }

      //check if we've been run on this document before
      //and clean the doc if needed
      docCleanup();
      @SuppressWarnings("unchecked")
      Map<String, List<List<Integer>>> matchesMap = (Map<String, List<List<Integer>>>)document.getFeatures().
      get(DOCUMENT_COREF_FEATURE_NAME);


      // creates the cdg list from the document
      //no need to create otherwise, coz already done in init()
      if (!extLists)
        cdg=orthoAnnotation.buildTables(nameAllAnnots);


      //Match all name annotations and unknown annotations
      matchNameAnnotations();

      //used to check if the Orthomatcher works properly
      //OrthoMatcherHelper.setMatchesPositions(nameAllAnnots);

      // set the matches of the document
      //    determineMatchesDocument();
      if (! matchesDocFeature.isEmpty()) {
        if(matchesMap == null){
          matchesMap = new HashMap<String, List<List<Integer>>>();
        }
        matchesMap.put(nameAllAnnots.getName(), matchesDocFeature);
        // System.out.println("matchesMap is: " + matchesMap);
        //we need to put it even if it was already present in order to triger
        //the update events
        document.getFeatures().put(DOCUMENT_COREF_FEATURE_NAME, matchesMap);

        //cannot do clear() as this has already been put on the document
        //so I need a new one for the next run of matcher
        matchesDocFeature = new ArrayList<List<Integer>>();


        fireStatusChanged("OrthoMatcher completed");
      }
    }finally{
      //make sure the cleanup happens even if there are errors.
      //    Out.prln("Processed strings" + processedAnnots.values());
      //clean-up the internal data structures for next run
      nameAllAnnots = null;
      processedAnnots.clear();
      annots2Remove.clear();
      tokensMap.clear();
      normalizedTokensMap.clear();
      matchesDocFeature = new ArrayList<List<Integer>>();
      longAnnot = null;
      shortAnnot = null;
      tokensLongAnnot = null;
      tokensShortAnnot = null;

      //if (log.isDebugEnabled()) OrthoMatcherHelper.saveUsedTable();
    }
  } // run()

  protected void matchNameAnnotations() throws ExecutionException{
    // go through all the annotation types
    Iterator<String> iterAnnotationTypes = annotationTypes.iterator();
    while (iterAnnotationTypes.hasNext()) {
      String annotationType = iterAnnotationTypes.next();

      AnnotationSet nameAnnots = nameAllAnnots.get(annotationType);

      // continue if no such annotations exist
      if (nameAnnots.isEmpty()) continue;

      AnnotationSet tokensNameAS = nameAllAnnots.get(TOKEN_ANNOTATION_TYPE);
      if (tokensNameAS.isEmpty()) continue;

      ArrayList<Annotation> sortedNameAnnots = new ArrayList<Annotation>(nameAnnots);
      Collections.<Annotation>sort(sortedNameAnnots,new OffsetComparator());
      for (int snaIndex = 0;snaIndex < sortedNameAnnots.size();snaIndex++) {
        Annotation tempAnnot = sortedNameAnnots.get(snaIndex);
        Annotation nameAnnot = nameAllAnnots.get(tempAnnot.getId()); // Not sure if this matters

        // get string and value
        String annotString = orthoAnnotation.getStringForAnnotation(nameAnnot, document);

        //convert to lower case if we are not doing a case sensitive match
        if (!caseSensitive)
          annotString = annotString.toLowerCase();

        if (DEBUG) {
          if (log.isDebugEnabled()) {
            log.debug("Now processing the annotation:  "
                    + orthoAnnotation.getStringForAnnotation(nameAnnot, document) + " Id: " + nameAnnot.getId()
                    + " Type: " + nameAnnot.getType() + " Offset: " + nameAnnot.getStartNode().getOffset());
          }
        }

        // get the tokens
        List<Annotation> tokens = new ArrayList<Annotation>(tokensNameAS.getContained(nameAnnot.getStartNode().getOffset(),
                nameAnnot.getEndNode().getOffset()));

        //if no tokens to match, do nothing
        if (tokens.isEmpty()) {
          if (log.isDebugEnabled()) {
            log.debug("Didn't find any tokens for the following annotation.  We will be unable to perform coref on this annotation.  \n String:  "
                    + orthoAnnotation.getStringForAnnotation(nameAnnot, document) + " Id: " + nameAnnot.getId() + " Type: " + nameAnnot.getType());
          }
          continue;
        }
        Collections.sort(tokens, new gate.util.OffsetComparator());
        //check if these actually do not end after the name
        //needed coz new tokeniser conflates
        //strings with dashes. So British Gas-style is two tokens
        //instead of three. So cannot match properly British Gas
        //      tokens = checkTokens(tokens);
        tokensMap.put(nameAnnot.getId(), tokens);
        normalizedTokensMap.put(nameAnnot.getId(), new ArrayList<Annotation>(tokens));

        //first check whether we have not matched such a string already
        //if so, just consider it matched, don't bother calling the rules
        // Exception:  AB, Spock:
        // Note that we require one-token Person annotations to be matched even if an identical string
        // has been matched earlier because there could be multiple people named "David", for instance,
        // on a page.
        if (processedAnnots.containsValue(annotString) &&
                (! (nameAnnot.getType().equals(personType) && (tokens.size() == 1)))) {
          Annotation returnAnnot = orthoAnnotation.updateMatches(nameAnnot, annotString,processedAnnots,nameAllAnnots,matchesDocFeature);
          if (returnAnnot != null) {
            if (DEBUG) {
              if (log.isDebugEnabled()) {
                log.debug("Exact match criteria matched " + annotString + " from (id: " + nameAnnot.getId() + ", offset: " + nameAnnot.getStartNode().getOffset() + ") to " +
                        "(id: " + returnAnnot.getId() + ", offset: " + returnAnnot.getStartNode().getOffset() + ")");
              }
            }
            processedAnnots.put(nameAnnot.getId(), annotString);
            continue;
          }
        } else if (processedAnnots.isEmpty()) {
          // System.out.println("First item put in processedAnnots: " + annotString);
          processedAnnots.put(nameAnnot.getId(), annotString);
          continue;
        }

        //if a person, then remove their title before matching
        if (nameAnnot.getType().equals(personType)) {
          annotString = orthoAnnotation.stripPersonTitle(annotString, nameAnnot,document,tokensMap,normalizedTokensMap,nameAllAnnots);
          normalizePersonName(nameAnnot);
        }
        else if (nameAnnot.getType().equals(organizationType))
          annotString = normalizeOrganizationName(annotString, nameAnnot);

        if(null == annotString || "".equals(annotString) || tokens.isEmpty()) {
          if (log.isDebugEnabled()) {
            log.debug("Annotation ID " + nameAnnot.getId() + " of type" + nameAnnot.getType() +
            " refers to a null or empty string or one with no tokens after normalization.  Unable to process further.");
          }
          continue;
        }
        //otherwise try matching with previous annotations
        matchWithPrevious(nameAnnot, annotString,sortedNameAnnots,snaIndex);

        // Out.prln("Putting in previous " + nameAnnot + ": string " + annotString);
        //finally add the current annotations to the processed map
        processedAnnots.put(nameAnnot.getId(), annotString);
      }//while through name annotations
      if (matchingUnknowns) {
        matchUnknown(sortedNameAnnots);
      }
    }//while through annotation types

  }

  protected void matchUnknown(ArrayList<Annotation> sortedAnnotationsForAType) throws ExecutionException {
    //get all Unknown annotations
    AnnotationSet unknownAnnots = nameAllAnnots.get(unknownType);
    annots2Remove.clear();
    if (unknownAnnots.isEmpty()) return;

    AnnotationSet nameAllTokens = nameAllAnnots.get(TOKEN_ANNOTATION_TYPE);
    if (nameAllTokens.isEmpty()) return;

    Iterator<Annotation> iter = unknownAnnots.iterator();
    //loop through the unknown annots
    while (iter.hasNext()) {
      Annotation unknown = iter.next();

      // get string and value
      String unknownString = orthoAnnotation.getStringForAnnotation(unknown, document);
      //convert to lower case if we are not doing a case sensitive match
      if (!caseSensitive)
        unknownString = unknownString.toLowerCase();

      // System.out.println("Now trying to match the unknown string: " + unknownString);
      //get the tokens
      List<Annotation> tokens = new ArrayList<Annotation>(nameAllTokens.getContained(
              unknown.getStartNode().getOffset(),
              unknown.getEndNode().getOffset()
      ));
      if (tokens.isEmpty())
        continue;
      Collections.sort(tokens, new gate.util.OffsetComparator());
      tokensMap.put(unknown.getId(), tokens);
      normalizedTokensMap.put(unknown.getId(), tokens);


      //first check whether we have not matched such a string already
      //if so, just consider it matched, don't bother calling the rules
      if (processedAnnots.containsValue(unknownString)) {
        Annotation matchedAnnot = orthoAnnotation.updateMatches(unknown, unknownString,processedAnnots,nameAllAnnots,matchesDocFeature);
        if (matchedAnnot == null) {
          log.debug("Orthomatcher: Unable to find the annotation: " +
                  orthoAnnotation.getStringForAnnotation(unknown, document) +
          " in matchUnknown");
        }
        else {
          if (matchedAnnot.getType().equals(unknownType)) {
            annots2Remove.put(unknown.getId(),
                    annots2Remove.get(matchedAnnot.getId()));
          }
          else
            annots2Remove.put(unknown.getId(), matchedAnnot.getType());
          processedAnnots.put(unknown.getId(), unknownString);
          unknown.getFeatures().put("NMRule", unknownType);
          continue;
        }
      }

      //check if we should do sub-string matching in case it's hyphenated
      //for example US-led
      if (tokens.size() == 1
              && "hyphen".equals(unknown.getFeatures().get(TOKEN_KIND_FEATURE_NAME))) {
        if (matchHyphenatedUnknowns(unknown, unknownString, iter))
          continue;
      }//if

      // TODO:  The below results in a assigning the unknown's to the last annotation that it matches in a document.
      // It would probably be better to first start with things which precede the current unknown and then do
      // annotations after
      matchWithPrevious(unknown, unknownString,sortedAnnotationsForAType,sortedAnnotationsForAType.size() - 1);

    } //while though unknowns

    if (! annots2Remove.isEmpty()) {
      Iterator<Integer> unknownIter = annots2Remove.keySet().iterator();
      while (unknownIter.hasNext()) {
        Integer unknId = unknownIter.next();
        Annotation unknown = nameAllAnnots.get(unknId);
        Integer newID = nameAllAnnots.add(
                unknown.getStartNode(),
                unknown.getEndNode(),
                annots2Remove.get(unknId),
                unknown.getFeatures()
        );
        nameAllAnnots.remove(unknown);

        //change the id in the matches list
        @SuppressWarnings("unchecked")
        List<Integer> mList = (List<Integer>)unknown.getFeatures().
        get(ANNOTATION_COREF_FEATURE_NAME);
        mList.remove(unknId);
        mList.add(newID);
      }//while
    }//if
  }

  private boolean matchHyphenatedUnknowns(Annotation unknown, String unknownString,
          Iterator<Annotation> iter){
    boolean matched = false;

    //only take the substring before the hyphen
    int stringEnd = unknownString.indexOf("-");
    unknownString = unknownString.substring(0, stringEnd);
    //check if we've already matched this string
    //because only exact match of the substring are considered
    if (processedAnnots.containsValue(unknownString)) {
      matched = true;
      Annotation matchedAnnot = orthoAnnotation.updateMatches(unknown, unknownString,processedAnnots,nameAllAnnots,matchesDocFeature);
      //only do the matching if not a person, because we do not match
      //those on sub-strings
      iter.remove();
      String newType;
      if (matchedAnnot.getType().equals(unknownType))
        newType = annots2Remove.get(matchedAnnot.getId());
      else
        newType = matchedAnnot.getType();

      Integer newID = new Integer(-1);
      try {
        newID = nameAllAnnots.add(
                unknown.getStartNode().getOffset(),
                new Long(unknown.getStartNode().getOffset().longValue()
                        + stringEnd),
                        newType,
                        unknown.getFeatures()
        );
      } catch (InvalidOffsetException ex) {
        throw new GateRuntimeException(ex.getMessage());
      }
      nameAllAnnots.remove(unknown);

      //change the id in the matches list
      @SuppressWarnings("unchecked")
      List<Integer> mList = (List<Integer>)unknown.getFeatures().
      get(ANNOTATION_COREF_FEATURE_NAME);
      mList.remove(unknown.getId());
      mList.add(newID);

    }
    return matched;
  }

  /**
   * Attempt to match nameAnnot against all previous annotations of the same type, which are passed down
   * in listOfThisType.  Matches are tested in order from most recent to oldest.
   * @param nameAnnot    Annotation we are trying to match
   * @param annotString  Normalized string representation of annotation
   * @param listOfThisType  ArrayList of Annotations of the same type as nameAnnot
   * @param startIndex   Index in listOfThisType that we will start from in matching the current annotation
   */
  protected void matchWithPrevious(Annotation nameAnnot, String annotString,
          ArrayList<Annotation> listOfThisType,
          int startIndex) {
    boolean matchedUnknown = false;
    // Out.prln("matchWithPrevious now processing: " + annotString);

    for (int curIndex = startIndex - 1;curIndex >= 0;curIndex--) {
      Integer prevId = listOfThisType.get(curIndex).getId();
      Annotation prevAnnot = nameAllAnnots.get(prevId);  // Note that this line probably isn't necessary anymore

      //check if the two are from the same type or the new one is unknown
      if (prevAnnot == null || (! prevAnnot.getType().equals(nameAnnot.getType())
              && ! nameAnnot.getType().equals(unknownType))
      )
        continue;
      //do not compare two unknown annotations either
      //they are only matched to those of known types
      if (  nameAnnot.getType().equals(unknownType)
              && prevAnnot.getType().equals(unknownType))
        continue;

      //check if we have already matched this annotation to the new one
      if (orthoAnnotation.matchedAlready(nameAnnot, prevAnnot,matchesDocFeature,nameAllAnnots) )
        continue;

      //now changed to a rule, here we just match by gender
      if (prevAnnot.getType().equals(personType)) {
        String prevGender =
          (String) prevAnnot.getFeatures().get(PERSON_GENDER_FEATURE_NAME);
        String nameGender =
          (String) nameAnnot.getFeatures().get(PERSON_GENDER_FEATURE_NAME);
        if (   prevGender != null
                && nameGender != null
                && ( (nameGender.equalsIgnoreCase("female")
                        &&
                        prevGender.equalsIgnoreCase("male")
                )
                ||
                (prevGender.equalsIgnoreCase("female")
                        && nameGender.equalsIgnoreCase("male")
                )
                )
        ) //if condition
          continue; //we don't have a match if the two genders are different

      }//if

      //if the two annotations match
      //
      // A. Borthwick, Spock:  If the earlier annotation is shorter than the current annotation and it
      // has already been matched with a longer annotations, then don't match it with the current annotation.
      // Reasoning is that with the sequence David Jones . . . David  . . . David Smith, we don't want to match
      // David Smith with David.  However, with the sequence, David  . . . David Jones, it's okay to match the
      // shorter version with the longer, because it hasn't already been matched with a longer.
      boolean prevAnnotUsedToMatchWithLonger = prevAnnot.getFeatures().containsKey("matchedWithLonger");
      if (matchAnnotations(nameAnnot, annotString,  prevAnnot)) {
        orthoAnnotation.updateMatches(nameAnnot, prevAnnot,matchesDocFeature,nameAllAnnots);
        if (DEBUG) {
          log.debug("Just matched nameAnnot " + nameAnnot.getId() + " with prevAnnot " + prevAnnot.getId());
        }

        if (!prevAnnotUsedToMatchWithLonger && prevAnnot.getFeatures().containsKey("matchedWithLonger")) {
          // We have just matched the previous annotation with a longer annotation for the first time.  We need
          // to propagate the matchedWithLonger property to all other annotations which coreffed with the previous annotation
          // so that we don't match them with a longer annotation
          propagatePropertyToExactMatchingMatches(prevAnnot,"matchedWithLonger",true);
        }
        //if unknown annotation, we need to change to the new type
        if (nameAnnot.getType().equals(unknownType)) {
          matchedUnknown = true;
          if (prevAnnot.getType().equals(unknownType))
            annots2Remove.put(nameAnnot.getId(),
                    annots2Remove.get(prevAnnot.getId()));
          else
            annots2Remove.put(nameAnnot.getId(), prevAnnot.getType());
          //also put an attribute to indicate that
          nameAnnot.getFeatures().put("NMRule", unknownType);
        }//if unknown
        break; //no need to match further
      }//if annotations matched

    }//while through previous annotations

    if (matchedUnknown)
      processedAnnots.put(nameAnnot.getId(), annotString);


  }//matchWithPrevious

  protected void propagatePropertyToExactMatchingMatches(Annotation updateAnnot,String featureName,Object value) {
    try {
      @SuppressWarnings("unchecked")
      List<Integer> matchesList = (List<Integer>) updateAnnot.getFeatures().get(ANNOTATION_COREF_FEATURE_NAME);
      if ((matchesList == null) || matchesList.isEmpty()) {
        return;
      }
      else {
        String updateAnnotString = orthoAnnotation.getStringForAnnotation(updateAnnot, document).toLowerCase();
        for (Integer nextId : matchesList) {
          Annotation a = nameAllAnnots.get(nextId);

          if (orthoAnnotation.fuzzyMatch(orthoAnnotation.getStringForAnnotation(a, document),updateAnnotString)) {
            if (DEBUG) {
              log.debug("propogateProperty: " + featureName + " " + value + " from: " + updateAnnot.getId() + " to: " + a.getId());
            }
            a.getFeatures().put(featureName, value);
          }
        }
      }
    }
    catch (Exception e) {
      log.error("Error in propogatePropertyToExactMatchingMatches", e);
    }
  }

  protected boolean matchAnnotations(Annotation newAnnot, String annotString,
          Annotation prevAnnot) {
    //do not match two annotations that overlap
    if (newAnnot.overlaps(prevAnnot))
      return false;

    // find which annotation string of the two is longer
    //  this is useful for some of the matching rules
    String prevAnnotString = processedAnnots.get(prevAnnot.getId());
    // Out.prln("matchAnnotations processing " + annotString + " and " + prevAnnotString);
    if (prevAnnotString == null) {
      //    Out.prln("We discovered that the following string is null!:  " + prevAnnot.getId() +
      //    " For the previous annotation " + getStringForAnnotation(prevAnnot, document) +
      //    " which has annotation type " + prevAnnot.getType() +
      //    " Tried to compared it to the annotation string " + annotString);
      return false;
    }

    String longName = prevAnnotString;
    String shortName = annotString;
    longAnnot = prevAnnot;
    shortAnnot = newAnnot;
    boolean longerPrevious = true;

    if (shortName.length()>longName.length()) {
      String temp = longName;
      longName = shortName;
      shortName = temp;
      Annotation tempAnn = longAnnot;
      longAnnot = shortAnnot;
      shortAnnot = tempAnn;
      longerPrevious = false;
    }//if

    tokensLongAnnot = (ArrayList<Annotation>) tokensMap.get(longAnnot.getId());
    normalizedTokensLongAnnot = (ArrayList<Annotation>) normalizedTokensMap.get(longAnnot.getId());
    tokensShortAnnot = (ArrayList<Annotation>) tokensMap.get(shortAnnot.getId());
    normalizedTokensShortAnnot = (ArrayList<Annotation>) normalizedTokensMap.get(shortAnnot.getId());

    @SuppressWarnings("unchecked")
    List<Integer> matchesList = (List<Integer>) prevAnnot.getFeatures().
    get(ANNOTATION_COREF_FEATURE_NAME);
    if (matchesList == null || matchesList.isEmpty())
      return apply_rules_namematch(prevAnnot.getType(), shortName,longName,
              prevAnnot,newAnnot,longerPrevious);

    //if these two match, then let's see if all the other matching one will too
    //that's needed, because sometimes names can share a token (e.g., first or
    //last but not be the same
    if (apply_rules_namematch(prevAnnot.getType(), shortName,longName,prevAnnot,newAnnot,
            longerPrevious)) {
      /**
       * Check whether we need to ensure that there is a match with the rest
       * of the matching annotations, because the rule requires that
       * transtivity is not assummed.
       */
      if (allMatchingNeeded) {
        allMatchingNeeded = false;

        List<Integer> toMatchList = new ArrayList<Integer>(matchesList);
        //      if (newAnnot.getType().equals(unknownType))
        //        Out.prln("Matching new " + annotString + " with annots " + toMatchList);
        toMatchList.remove(prevAnnot.getId());

        return matchOtherAnnots(toMatchList, newAnnot, annotString);
      } else
        return true;
    }
    return false;
  }

  /** This method checkes whether the new annotation matches
   *  all annotations given in the toMatchList (it contains ids)
   *  The idea is that the new annotation needs to match all those,
   *  because assuming transitivity does not always work, when
   *  two different entities share a common token: e.g., BT Cellnet
   *  and BT and British Telecom.
   */
  protected boolean matchOtherAnnots( List<Integer> toMatchList, Annotation newAnnot,
          String annotString) {

    //if the list is empty, then we're matching all right :-)
    if (toMatchList.isEmpty())
      return true;

    boolean matchedAll = true;
    int i = 0;

    while (matchedAll && i < toMatchList.size()) {
      Annotation prevAnnot = nameAllAnnots.get(toMatchList.get(i));

      // find which annotation string of the two is longer
      //  this is useful for some of the matching rules
      String prevAnnotString = processedAnnots.get(prevAnnot.getId());
      if (prevAnnotString == null)
        try {
          prevAnnotString = document.getContent().getContent(
                  prevAnnot.getStartNode().getOffset(),
                  prevAnnot.getEndNode().getOffset()
          ).toString();
        } catch (InvalidOffsetException ioe) {
          return false;
        }//try


        String longName = prevAnnotString;
        String shortName = annotString;
        longAnnot = prevAnnot;
        shortAnnot = newAnnot;
        boolean longerPrevious = true;
        if (shortName.length()>=longName.length()) {
          String temp = longName;
          longName = shortName;
          shortName = temp;
          Annotation tempAnn = longAnnot;
          longAnnot = shortAnnot;
          shortAnnot = tempAnn;
          longerPrevious = false;
        }//if

        tokensLongAnnot = (ArrayList<Annotation>) tokensMap.get(longAnnot.getId());
        normalizedTokensLongAnnot = (ArrayList<Annotation>) normalizedTokensMap.get(longAnnot.getId());
        tokensShortAnnot = (ArrayList<Annotation>) tokensMap.get(shortAnnot.getId());
        normalizedTokensShortAnnot = (ArrayList<Annotation>) normalizedTokensMap.get(shortAnnot.getId());

        matchedAll = apply_rules_namematch(prevAnnot.getType(), shortName,longName,prevAnnot,newAnnot,
                longerPrevious);
        //      if (newAnnot.getType().equals(unknownType))
        //      Out.prln("Loop: " + shortName + " and " + longName + ": result: " + matchedAll);

        i++;
    }//while
    return matchedAll;
  }

  @SuppressWarnings("unchecked")
  protected void docCleanup() {
    Object matchesValue = document.getFeatures().get(DOCUMENT_COREF_FEATURE_NAME);
    if (matchesValue != null && (matchesValue instanceof Map))
      ((Map<String,List<List<Integer>>>)matchesValue).remove(nameAllAnnots.getName());
    else if (matchesValue != null) {
      document.getFeatures().put(DOCUMENT_COREF_FEATURE_NAME, new HashMap<String,List<List<Integer>>>());
    }

    //get all annotations that have a matches feature
    HashSet<String> fNames = new HashSet<String>();
    fNames.add(ANNOTATION_COREF_FEATURE_NAME);
    AnnotationSet annots =
      nameAllAnnots.get(null, fNames);

    //  Out.prln("Annots to cleanup" + annots);

    if (annots == null || annots.isEmpty())
      return;

    Iterator<Annotation> iter = annots.iterator();
    while (iter.hasNext()) {
      while (iter.hasNext())
        iter.next().getFeatures().remove(ANNOTATION_COREF_FEATURE_NAME);
    } //while
  }//cleanup


  static Pattern periodPat = Pattern.compile("[\\.]+");

  protected void normalizePersonName (Annotation annot) throws ExecutionException {
    ArrayList<Annotation> tokens = (ArrayList<Annotation>) normalizedTokensMap.get(annot.getId());
    for (int i = tokens.size() - 1; i >= 0; i--) {
      String tokenString = ((String) tokens.get(i).getFeatures().get(TOKEN_STRING_FEATURE_NAME));
      String kind = (String) tokens.get(i).getFeatures().get(TOKEN_KIND_FEATURE_NAME);
      //String category = (String) tokens.get(i).getFeatures().get(TOKEN_CATEGORY_FEATURE_NAME);
      if (!caseSensitive)  {
        tokenString = tokenString.toLowerCase();
      }
      // log.debug("tokenString: " + tokenString + " kind: " + kind + " category: " + category);
      if (kind.equals(PUNCTUATION_VALUE) ) {
        // log.debug("Now tagging it!");
        tokens.get(i).getFeatures().put("ortho_stop", true);
      }
    }

    ArrayList<Annotation> normalizedTokens = new ArrayList<Annotation>(tokens);
    for (int j = normalizedTokens.size() - 1; j >=  0;j--) {
      if (normalizedTokens.get(j).getFeatures().containsKey("ortho_stop")) {
        // log.debug("Now removing " + normalizedTokens.get(j).getFeatures().get(TOKEN_STRING_FEATURE_NAME));
        normalizedTokens.remove(j);
      }
    }
    // log.debug("normalizedTokens size is: " + normalizedTokens.size());
    normalizedTokensMap.put(annot.getId(), normalizedTokens);
  }

  /** return an organization  without a designator and starting The*/
  protected String normalizeOrganizationName (String annotString, Annotation annot){

    ArrayList<Annotation> tokens = (ArrayList<Annotation>) tokensMap.get(annot.getId());

    //strip starting The first
    if ( ((String) tokens.get(0).getFeatures().get(TOKEN_STRING_FEATURE_NAME))
    .equalsIgnoreCase(THE_VALUE))
      tokens.remove(0);

    if (tokens.size() > 0) {

      // New code by A. Borthwick of Spock Networks
      // June 13, 2008
      // Strip everything on the cdg list, which now encompasses not just cdg's, but also other stopwords
      // Start from the right side so we don't mess up the arraylist
      for (int i = tokens.size() - 1; i >= 0; i--) {
        String tokenString = ((String) tokens.get(i).getFeatures().get(TOKEN_STRING_FEATURE_NAME));
        String kind = (String) tokens.get(i).getFeatures().get(TOKEN_KIND_FEATURE_NAME);
        String category = (String) tokens.get(i).getFeatures().get(TOKEN_CATEGORY_FEATURE_NAME);
        if (!caseSensitive)  {
          tokenString = tokenString.toLowerCase();
        }
        // Out.prln("tokenString: " + tokenString + " kind: " + kind + " category: " + category);
        if (kind.equals(PUNCTUATION_VALUE) ||
	    ( (category != null) && (category.equals("DT") || category.equals("IN")) )
	    || cdg.contains(tokenString)) {
          // Out.prln("Now tagging it!");
          tokens.get(i).getFeatures().put("ortho_stop", true);
        }
      }

      // AB, Spock:  Need to check for CDG even for 1 token so we don't automatically match
      // a one-token annotation called "Company", for instance
      String compareString = (String) tokens.get(tokens.size()-1).getFeatures().get(TOKEN_STRING_FEATURE_NAME);
      if (!caseSensitive) {
        compareString = compareString.toLowerCase();
      }
      if (cdg.contains(compareString)) {
        tokens.remove(tokens.size()-1);
      }

    }

    ArrayList<Annotation> normalizedTokens = new ArrayList<Annotation>(tokens);
    for (int j = normalizedTokens.size() - 1; j >=  0;j--) {
      if (normalizedTokens.get(j).getFeatures().containsKey("ortho_stop")) {
        normalizedTokens.remove(j);
      }
    }

    normalizedTokensMap.put(annot.getId(), normalizedTokens);

    StringBuffer newString = new StringBuffer(50);
    for (int i = 0; i < tokens.size(); i++){
      newString.append((String) tokens.get(i).getFeatures().get(TOKEN_STRING_FEATURE_NAME) );
      if (i != tokens.size()-1)
        newString.append(" ");
    }
    // Out.prln("Strip CDG returned: " + newString + "for string " + annotString);

    if (caseSensitive)
      return newString.toString();

    return newString.toString().toLowerCase();
  }

  /** creates the lookup tables */
  protected void createAnnotList(String nameFile, String nameList)
          throws IOException {
    // create the relative URL
    URL fileURL = new URL(definitionFileURL, nameFile);
    BufferedReader bufferedReader = null;
    try {
      bufferedReader =
              new BomStrippingInputStreamReader(fileURL.openStream(), encoding);

      String lineRead = null;
      while((lineRead = bufferedReader.readLine()) != null) {
        if(nameList.compareTo(CDGLISTNAME) == 0) {
          Matcher matcher = punctPat.matcher(lineRead.toLowerCase().trim());
          lineRead = matcher.replaceAll(" ").trim();
          if(caseSensitive)
            cdg.add(lineRead);
          else cdg.add(lineRead.toLowerCase());
        }// if
        else {
          int index = lineRead.indexOf("£");
          if(index != -1) {
            String expr = lineRead.substring(0, index);
            // if not case-sensitive, we need to downcase all strings
            if(!caseSensitive) expr = expr.toLowerCase();
            String code = lineRead.substring(index + 1, lineRead.length());
            if(nameList.equals(ALIASLISTNAME)) {
              alias.put(expr, code);
            } else if(nameList.equals(ARTLISTNAME)) {
              def_art.put(expr, code);
            } else if(nameList.equals(PREPLISTNAME)) {
              prepos.put(expr, code);
            } else if(nameList.equals(CONNECTORLISTNAME)) {
              connector.put(expr, code);
            } else if(nameList.equals(SPURLISTNAME)) {
              spur_match.put(expr, code);
            }
          }// if
        }// else

      }// while
    } finally {
      IOUtils.closeQuietly(bufferedReader);
    }
  }// createAnnotList


  /**
   * This is the skeleton of a function which should be available in OrthoMatcher to allow a pairwise comparison of two name strings
   * It should eventually be made public.  It is private here (and thus non-functional) because OrthoMatcher is currently reliant
   * on the tokenization of the names, which are held in the global variables tokensShortAnnot and tokensLongAnnot
   *
   * @param name1
   * @param name2
   * @return  true if the two names indicate the same person
   */
  @SuppressWarnings("unused")
  private boolean pairwise_person_name_match(String name1, String name2) {
    String shortName,longName;
    if (name1.length() > name2.length()) {
      longName = name1;
      shortName = name2;
    }
    else {
      longName = name2;
      shortName = name1;
    }
    if (rules.get(0).value(longName,shortName)) {//matchRule0(longName,shortName)
      return false;
    }
    else {
      if (longName.equals(shortName) || rules.get(2).value(longName, shortName) ||
              rules.get(3).value(longName, shortName)) {
        return true;
      }
      else {
        return (rules.get(0).value(longName, shortName));
        // boolean throwAway[] = new boolean[17];
        // return basic_person_match_criteria(shortName,longName,throwAway);
        // The above doesn't work because basic_person_match_criteria is reliant on the global
        // variables tokensShortAnnot and tokensLongAnnot so I just call what I can directly
      }
    }
  }

  /**
   * basic_person_match_criteria
   * Note that this function relies on various global variables in some other match rules.
   */
  private boolean basic_person_match_criteria(String shortName,
          String longName, boolean mr[]) {

    if ( // For 4, 5, 14, and 15, need to mark shorter annot
            //kalina: added 16, so it matches names when contain more than one first and one last name
            OrthoMatcherHelper.executeDisjunction(rules, new int[] {1,5,6,13,15,16},longName,shortName,mr)
    ) {
      return true;
    }
    return false;
  }


  /** apply_rules_namematch: apply rules similarly to lasie1.5's namematch */
  private boolean apply_rules_namematch(String annotationType, String shortName,
          String longName,Annotation prevAnnot,
          Annotation followAnnot,
          boolean longerPrevious) {
    boolean mr[] = new boolean[rules.size()];
    // first apply rule for spurious matches i.e. rule0
    if (DEBUG) {
      log.debug("Now matching " + longName + "(id: " + longAnnot.getId() + ") to "
              + shortName + "(id: " + shortAnnot.getId() + ")");
    }

    if (rules.get(0).value(longName,shortName))
      return false;
    if (
            (// rules for all annotations
                    //no longer use rule1, coz I do the check for same string via the hash table
                    OrthoMatcherHelper.executeDisjunction(rules, new int[] {2,3},longName,shortName,mr)

            ) // rules for all annotations
            ||
            (// rules for organisation annotations
                    (annotationType.equals(organizationType)
                            //ACE addition
                            || annotationType.equals("Facility")
                    )
                    &&
                    // Should basically only match when you have a match of all tokens other than
                    // CDG's and function words
                    (
                            (!highPrecisionOrgs && OrthoMatcherHelper.executeDisjunction(rules,new int[] {4,6,7,8,9,10,11,12,14},longName,shortName,mr))
                            ||
                            (highPrecisionOrgs && OrthoMatcherHelper.executeDisjunction(rules,new int[] {7,8,10,11,17},longName,shortName,mr))
                    )
            )
    ) {// rules for organisation annotations
      return true;
    }

    if  (// rules for person annotations
            (    annotationType.equals(personType))) {
      if (noMatchRule1(longName, shortName,prevAnnot, longerPrevious) ||
              noMatchRule2(longName, shortName)) {
        // Out.prln("noMatchRule1 rejected match between " + longName + " and " + shortName);
        return false;
      }
      else {
        if (  basic_person_match_criteria(shortName,longName,mr))
        {
          if ((longName.length() != shortName.length()) && (mr[4] || mr[5] || mr[14] || mr[15])) {
            if (longerPrevious) {
              followAnnot.getFeatures().put("matchedWithLonger", true);
            }
            else {
              prevAnnot.getFeatures().put("matchedWithLonger", true);
            }
          }
          else if ((longName.length() == shortName.length()) && (mr[1])) {
            if (prevAnnot.getFeatures().containsKey("matchedWithLonger")) {
              followAnnot.getFeatures().put("matchedWithLonger", true);
            }
          }
          return true;
        }
        return false;
      }
    }
    return false;
  }//apply_rules


  /** set the extLists flag */
  @Optional
  @CreoleParameter(comment="External lists otherwise internal", defaultValue="true")
  public void setExtLists(Boolean newExtLists) {
    extLists = newExtLists.booleanValue();
  }//setextLists

  /** set the caseSensitive flag */
  @Optional
  @CreoleParameter(comment="Should this resource diferentiate on case?",defaultValue="false")
  public void setCaseSensitive(Boolean newCase) {
    caseSensitive = newCase.booleanValue();
  }//setextLists

  /** set the annotation set name*/
  @RunTime
  @Optional
  @CreoleParameter(comment="Annotation set name where are the annotation types (annotationTypes)")
  public void setAnnotationSetName(String newAnnotationSetName) {
    annotationSetName = newAnnotationSetName;
  }//setAnnotationSetName

  /** set the types of the annotations*/
  @RunTime
  @Optional
  @CreoleParameter(comment="Name of the annotation types to use", defaultValue="Organization;Person;Location;Date")
  public void setAnnotationTypes(List<String> newType) {
    annotationTypes = newType;
  }//setAnnotationTypes

  /** set whether to process the Unknown annotations*/
  @Optional
  @CreoleParameter(comment="Should we process 'Unknown' annotations?", defaultValue="true")
  public void setProcessUnknown(Boolean processOrNot) {
    this.matchingUnknowns = processOrNot.booleanValue();
  }//setAnnotationTypes

  @Optional
  @CreoleParameter(comment="Annotation name for the organizations", defaultValue="Organization")
  public void setOrganizationType(String newOrganizationType) {
    organizationType = newOrganizationType;
  }//setOrganizationType

  @Optional
  @CreoleParameter(comment="Annotation name for the persons", defaultValue="Person")
  public void setPersonType(String newPersonType) {
    personType = newPersonType;
  }//setPersonType

  /**get the name of the annotation set*/
  public String getAnnotationSetName() {
    return annotationSetName;
  }//getAnnotationSetName

  /** get the types of the annotation*/
  public List<String> getAnnotationTypes() {
    return annotationTypes;
  }//getAnnotationTypes

  public String getOrganizationType() {
    return organizationType;
  }

  public String getPersonType() {
    return personType;
  }

  public Boolean getExtLists() {
    return new Boolean(extLists);
  }

  /** Are we running in a case-sensitive mode?*/
  public Boolean getCaseSensitive() {
    return new Boolean(caseSensitive);
  }

  /** Return whether or not we're processing the Unknown annots*/
  public Boolean getProcessUnknown() {
    return new Boolean(matchingUnknowns);
  }



  /**
  No Match Rule 1:
  Avoids the problem of matching
  David Jones ...
  David ...
  David Smith
  Since "David" was matched with David Jones, we don't match David with David Smith.
   */
  public boolean noMatchRule1(String s1,
          String s2,Annotation previousAnnot, boolean longerPrevious) {
    //    if (DEBUG) {
    //      try {
    //        String annotString = getStringForAnnotation(previousAnnot, document );

    //        log.debug("Previous annotation was " + annotString +  "(id: " + previousAnnot.getId() + ")" + " features are " + previousAnnot.getFeatures());
    //      }
    //      catch (ExecutionException e) {}
    //    }

    if (longerPrevious || !previousAnnot.getFeatures().containsKey("matchedWithLonger")) {
      return false;
    }
    else {
      return true;
    }
  }//noMatchRule1

  /***
   * returns true if it detects a middle name which indicates that the name string contains a nickname or a
   * compound last name
   */
  private boolean detectBadMiddleTokens(ArrayList<Annotation> tokArray) {
    for (int j = 1;j < tokArray.size() - 1;j++) {
      String currentToken = (String) tokArray.get(j).getFeatures().get(TOKEN_STRING_FEATURE_NAME);
      Matcher matcher = badMiddleTokens.matcher(currentToken.toLowerCase().trim());
      if (matcher.find()) {
        // We have found a case of a ", ',
        return true;
      }
    }
    return false;
  }

  /**
   * NoMatch Rule #2: Do we have a mismatch of middle initial?
   * Condition(s):  Only applies to person names with more than two tokens in the name
   *
   * Want George W. Bush != George H. W. Bush and George Walker Bush != George Herbert Walker Bush
   * and
   * John T. Smith != John Q. Smith
   * however
   * John T. Smith == John Thomas Smith
   * be careful about
   * Hillary Rodham Clinton == Hillary Rodham-Clinton
   * be careful about
   * Carlos Bueno de Lopez == Bueno de Lopez
   * and
   * Cynthia Morgan de Rothschild == Cynthia de Rothschild
   */
  @SuppressWarnings("unused")
  public boolean noMatchRule2(String s1,String s2) {
    if (normalizedTokensLongAnnot.size()>2 && normalizedTokensShortAnnot.size()>2) {
      boolean retval = false;
      if (normalizedTokensLongAnnot.size() != normalizedTokensShortAnnot.size()) {
        String firstNameLong = (String) normalizedTokensLongAnnot.get(0).getFeatures().get(TOKEN_STRING_FEATURE_NAME);
        String firstNameShort = (String) normalizedTokensShortAnnot.get(0).getFeatures().get(TOKEN_STRING_FEATURE_NAME);
        String lastNameLong = (String) normalizedTokensLongAnnot.get(normalizedTokensLongAnnot.size() - 1).
        getFeatures().get(TOKEN_STRING_FEATURE_NAME);
        String lastNameShort = (String) normalizedTokensShortAnnot.get(normalizedTokensShortAnnot.size() - 1).
        getFeatures().get(TOKEN_STRING_FEATURE_NAME);
        if (rules.get(1).value(firstNameLong,firstNameShort) &&
                (rules.get(1).value(lastNameLong,lastNameShort))) {
          // Must have a match on first and last name for this non-match rule to take effect when the number of tokens differs
          if (detectBadMiddleTokens(tokensLongAnnot) || detectBadMiddleTokens(tokensShortAnnot)) {
            // Exclude the William (Bill) H. Gates vs. William H. Gates case and the
            // Cynthia Morgan de Rothschild vs. Cynthia de Rothschild case
            if (DEBUG && log.isDebugEnabled()) {
              log.debug("noMatchRule2Name did not non-match because of bad middle tokens " + s1 + "(id: " + longAnnot.getId() + ") to "
                      + s2+ "(id: " + shortAnnot.getId() + ")");
            }
            return false;
          }
          else {
            // Covers the George W. Bush vs George H. W. Bush and George Walker Bush vs. George Herbert Walker Bush cases
            retval = true;
          }
        }
      }
      else {
        for (int i = 1; i < normalizedTokensLongAnnot.size() - 1;i++) {
          String s1_middle = (String) normalizedTokensLongAnnot.get(i).getFeatures().get(TOKEN_STRING_FEATURE_NAME);
          String s2_middle = (String) normalizedTokensShortAnnot.get(i).getFeatures().get(TOKEN_STRING_FEATURE_NAME);
          if (!caseSensitive) {
            s1_middle = s1_middle.toLowerCase();
            s2_middle = s2_middle.toLowerCase();
          }
          //          log.debug("noMatchRule2 comparing substring " + s1_middle + " to " + s2_middle);
          if (!(rules.get(1).value(s1_middle,s2_middle) ||
                  OrthoMatcherHelper.initialMatch(s1_middle, s2_middle))) {
            // We found a mismatching middle name
            retval = true;
            break;
          }
        }
      }
      if (retval && log.isDebugEnabled() && DEBUG)  {
        log.debug("noMatchRule2Name non-matched  " + s1 + "(id: " + longAnnot.getId() + ") to "
                + s2+ "(id: " + shortAnnot.getId() + ")");
      }
      return retval;
    } // if (normalizedTokensLongAnnot.size()>2 && normalizedTokensShortAnnot.size()>2)
    return false;
  }//noMatchRule2

  @CreoleParameter(comment="The URL to the definition file", defaultValue="resources/othomatcher/listsNM.def", suffixes="def")
  public void setDefinitionFileURL(java.net.URL definitionFileURL) {
    this.definitionFileURL = definitionFileURL;
  }

  public java.net.URL getDefinitionFileURL() {
    return definitionFileURL;
  }
  
  @CreoleParameter(comment="The encoding used for reading the definition file", defaultValue="UTF-8")
  public void setEncoding(String encoding) {
    this.encoding = encoding;
  }
  public String getEncoding() {
    return encoding;
  }


  public Double getMinimumNicknameLikelihood() {
    return minimumNicknameLikelihood;
  }

  @CreoleParameter(comment="Minimum likelihood that a name is a nickname", defaultValue="0.50")
  public void setMinimumNicknameLikelihood(Double minimumNicknameLikelihood) {
    this.minimumNicknameLikelihood = minimumNicknameLikelihood;
  }

  /**
   * @return the highPrecisionOrgs
   */
  public Boolean getHighPrecisionOrgs() {
    return highPrecisionOrgs;
  }

  /**
   * @param highPrecisionOrgs the highPrecisionOrgs to set
   */
  @Optional
  @CreoleParameter(comment="Use very safe features for matching orgs, such as ACME = ACME, Inc.", defaultValue="false")  
  public void setHighPrecisionOrgs(Boolean highPrecisionOrgs) {
    this.highPrecisionOrgs = highPrecisionOrgs;
  }

  public void setOrthography(AnnotationOrthography orthography) {
    this.orthoAnnotation = orthography;
  }

  public AnnotationOrthography getOrthography() {
    return orthoAnnotation;
  }

  static Pattern punctPat = Pattern.compile("[\\p{Punct}]+");
  // The UTF characters are right and left double and single curly quotes
  static Pattern badMiddleTokens = Pattern.compile("[\u201c\u201d\u2018\u2019\'\\(\\)\"]+|^de$|^von$");
}