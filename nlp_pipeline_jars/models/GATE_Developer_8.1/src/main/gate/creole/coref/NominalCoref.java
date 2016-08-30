/*
 *  NominalCoref.java
 *
 *  Copyright (c) 1995-2012, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 *  $Id: NominalCoref.java 17616 2014-03-10 16:09:07Z markagreenwood $
 */

package gate.creole.coref;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Document;
import gate.FeatureMap;
import gate.ProcessingResource;
import gate.Resource;
import gate.creole.ANNIEConstants;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;
import gate.creole.metadata.CreoleParameter;
import gate.creole.metadata.CreoleResource;
import gate.creole.metadata.Optional;
import gate.creole.metadata.RunTime;
import gate.util.Err;
import gate.util.OffsetComparator;
import gate.util.SimpleFeatureMapImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@CreoleResource(name="ANNIE Nominal Coreferencer", comment="Nominal Coreference resolution component", helpURL="http://gate.ac.uk/userguide/sec:annie:pronom-coref", icon="nominal-coreferencer")
public class NominalCoref extends AbstractCoreferencer
    implements ProcessingResource, ANNIEConstants {

  private static final long serialVersionUID = 1497388811557744017L;

  public static final String COREF_DOCUMENT_PARAMETER_NAME = "document";

  public static final String COREF_ANN_SET_PARAMETER_NAME = "annotationSetName";

  //annotation features
  private static final String PERSON_CATEGORY = "Person";
  private static final String JOBTITLE_CATEGORY = "JobTitle";
  private static final String ORGANIZATION_CATEGORY = "Organization";
  private static final String LOOKUP_CATEGORY = "Lookup";
  private static final String ORGANIZATION_NOUN_CATEGORY = "organization_noun";
  

  //scope
  /** --- */
  //private static AnnotationOffsetComparator ANNOTATION_OFFSET_COMPARATOR;
  /** --- */
  private String annotationSetName;
  /** --- */
  private AnnotationSet defaultAnnotations;
  /** --- */
  private HashMap<Annotation,Annotation> anaphor2antecedent;

    /*  static {
    ANNOTATION_OFFSET_COMPARATOR = new AnnotationOffsetComparator();
    }*/

  /** --- */
  public NominalCoref() {
    super("NOMINAL");
    this.anaphor2antecedent = new HashMap<Annotation,Annotation>();
  }

  /** Initialise this resource, and return it. */
  @Override
  public Resource init() throws ResourceInstantiationException {
    return super.init();
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
    this.anaphor2antecedent = new HashMap<Annotation,Annotation>();
    init();
  } // reInit()


  /** Set the document to run on. */
  @Override
  public void setDocument(Document newDocument) {

    //0. precondition
//    Assert.assertNotNull(newDocument);

    super.setDocument(newDocument);
  }

  /** --- */
  @Override
  @RunTime
  @Optional
  @CreoleParameter(comment="The annotation set to be used for the generated annotations")
  public void setAnnotationSetName(String annotationSetName) {
    this.annotationSetName = annotationSetName;
  }

  /** --- */
  @Override
  public String getAnnotationSetName() {
    return annotationSetName;
  }

  /**
   * This method runs the coreferencer. It assumes that all the needed parameters
   * are set. If they are not, an exception will be fired.
   *
   * The process goes like this:
   * - Create a sorted list of Person and JobTitle annotations.
   * - Loop through the annotations
   *    If it is a Person, we add it to the top of a stack.
   *    If it is a job title, we subject it to a series of tests. If it 
   *      passes, we associate it with the Person annotation at the top
   *      of the stack
   */
  @Override
  public void execute() throws ExecutionException{

    Annotation[] nominalArray;

    //0. preconditions
    if (null == this.document) {
      throw new ExecutionException("[coreference] Document is not set!");
    }

    //1. preprocess
    preprocess();

    // Out.println("Total annotations: " + defaultAnnotations.size());

    // Get a sorted array of Tokens.
    // The tests for job titles often require getting previous and subsequent
    // tokens, so to save work, we create a single, sorted list of 
    // tokens.
    Annotation[] tokens = defaultAnnotations.get(TOKEN_ANNOTATION_TYPE).
        toArray(new Annotation[0]);
    java.util.Arrays.sort(tokens, new OffsetComparator());

    // The current token is the token at the start of the current annotation.
    int currentToken = 0;

    // get Person entities
    //FeatureMap personConstraint = new SimpleFeatureMapImpl();
    //personConstraint.put(LOOKUP_MAJOR_TYPE_FEATURE_NAME,
    //                          PERSON_CATEGORY);
    Set<String> personConstraint = new HashSet<String>();
    personConstraint.add(PERSON_CATEGORY);
    AnnotationSet people =
      this.defaultAnnotations.get(personConstraint);

    // get all JobTitle entities
    //FeatureMap constraintJobTitle = new SimpleFeatureMapImpl();
    //constraintJobTitle.put(LOOKUP_MAJOR_TYPE_FEATURE_NAME, JOBTITLE_CATEGORY);
    Set<String> jobTitleConstraint = new HashSet<String>();
    jobTitleConstraint.add(JOBTITLE_CATEGORY);
    
    AnnotationSet jobTitles = 
      this.defaultAnnotations.get(jobTitleConstraint);

    FeatureMap orgNounConstraint = new SimpleFeatureMapImpl();
    orgNounConstraint.put(LOOKUP_MAJOR_TYPE_FEATURE_NAME,
                          ORGANIZATION_NOUN_CATEGORY);
    AnnotationSet orgNouns =
      this.defaultAnnotations.get(LOOKUP_CATEGORY, orgNounConstraint);

    Set<String> orgConstraint = new HashSet<String>();
    orgConstraint.add(ORGANIZATION_CATEGORY);

    AnnotationSet organizations =
      this.defaultAnnotations.get(orgConstraint);

    // combine them into a list of nominals
    Set<Annotation> nominals = new HashSet<Annotation>();
    if (people != null) {
      nominals.addAll(people);
    }
    if (jobTitles != null) {
      nominals.addAll(jobTitles);
    }
    if (orgNouns != null) {
      nominals.addAll(orgNouns);
    }
    if (organizations != null) {
      nominals.addAll(organizations);
    }

    //  Out.println("total nominals: " + nominals.size());

    // sort them according to offset
    nominalArray = nominals.toArray(new Annotation[0]);
    java.util.Arrays.sort(nominalArray, new OffsetComparator());
    
    ArrayList<Annotation> previousPeople = new ArrayList<Annotation>();
    ArrayList<Annotation> previousOrgs = new ArrayList<Annotation>();
    
        
    // process all nominals
    for (int i=0; i<nominalArray.length; i++) {
      Annotation nominal = nominalArray[i];
      
      // Find the current place in the tokens array
      currentToken = advanceTokenPosition(nominal, currentToken, tokens);
      
      //Out.print("processing nominal [" + stringValue(nominal) + "] ");
      
      if (nominal.getType().equals(PERSON_CATEGORY)) {
	// Add each Person entity to the beginning of the people list
	// but don't add pronouns
	Object[] personTokens = getSortedTokens(nominal);
	  
	if (personTokens.length == 1) {
	  Annotation personToken = (Annotation) personTokens[0];
	  
	  String personCategory = (String) 
	    personToken.getFeatures().get(TOKEN_CATEGORY_FEATURE_NAME);
	  if (personCategory.equals("PP") ||
	      personCategory.equals("PRP") ||
	      personCategory.equals("PRP$") ||
	      personCategory.equals("PRPR$")) {
	      //Out.println("ignoring personal pronoun");
	      continue;
	  }
	}
	
	previousPeople.add(0, nominal);
	//Out.println("added person");
      }
      else if (nominal.getType().equals(JOBTITLE_CATEGORY)) {
	  
	// Look into the tokens to get some info about POS.
	Object[] jobTitleTokens = getSortedTokens(nominal);
	
	Annotation lastToken = (Annotation)
	  jobTitleTokens[jobTitleTokens.length - 1];
	
	// Don't associate if the job title is not a singular noun
	String tokenCategory = (String) 
	  lastToken.getFeatures().get(TOKEN_CATEGORY_FEATURE_NAME);
	// UNCOMMENT FOR SINGULAR PROPER NOUNS (The President, the Pope)
	//if (! tokenCategory.equals("NN") &&
	//! tokenCategory.equals("NNP")) {
	if (! tokenCategory.equals("NN")) {
	    // Out.println("Not a singular noun");
	  continue;
	}
	
	// Don't associate it if it's part of a Person (eg President Bush)
	if (overlapsAnnotations(nominal, people)) {
	    //Out.println("overlapping annotation");
	  continue;
	}

	Annotation previousToken;
        String previousValue;

	// Don't associate it if it's proceeded by a generic marker
        if (currentToken != 0) {
          previousToken = tokens[currentToken - 1];
          previousValue = (String) 
	    previousToken.getFeatures().get(TOKEN_STRING_FEATURE_NAME);
          if (previousValue.equalsIgnoreCase("a") ||
              previousValue.equalsIgnoreCase("an") ||
              previousValue.equalsIgnoreCase("other") ||
              previousValue.equalsIgnoreCase("another")) {
              //Out.println("indefinite");
	    continue;
          }
        }

	// nominals immediately followed by Person annotations:
	// BAD:
	//   Chairman Bill Gates               (title)
	// GOOD:
	//   secretary of state, Colin Powell  (inverted appositive)
	//   the home secretary David Blunkett (same but no comma, 
	//                                      possible in transcriptions)
	// "the" is a good indicator for apposition
	
	// Luckily we have an array of all Person annotations in order...
	if (i < nominalArray.length - 1) {
	  Annotation nextAnnotation = nominalArray[i+1];
	  if (nextAnnotation.getType().equals(PERSON_CATEGORY)) {
	    // is it preceded by a definite article?
	    previousToken = tokens[currentToken - 1];
	    previousValue = (String) 
	      previousToken.getFeatures().get(TOKEN_STRING_FEATURE_NAME);
	    
	    // Get all tokens between this and the next person
	    int interveningTokens =
	      countInterveningTokens(nominal, nextAnnotation,
				     currentToken, tokens);
	    if (interveningTokens == 0 && 
	      ! previousValue.equalsIgnoreCase("the")) {
			
	      // There is nothing between the job title and the person,
	      // like "Chairman Gates" -- do nothing.
	      //Out.println("immediately followed by Person");
	      continue;
	    }
	    else if (interveningTokens == 1) {
	      String tokenString =
	        (String) getFollowingToken(nominal,
					   currentToken, tokens)
		  .getFeatures().get(TOKEN_STRING_FEATURE_NAME);
	      //Out.print("STRING VALUE [" + tokenString + "] ");
	      if (! tokenString.equals(",") &&
		! tokenString.equals("-")) {
		//Out.println("nominal and person separated by NOT [,-]");
		continue;
	      }
	    }
	    
	    // Did we get through all that? Then we must have an 
	    // apposition.
	    
	    anaphor2antecedent.put(nominal, nextAnnotation);
	    //Out.println("associating with " +
	    //	stringValue(nextAnnotation));
	    continue;
	    
	  }
	}
	
	// If we have no possible antecedents, create a new Person
	// annotation.
	if (previousPeople.size() == 0) {
	  FeatureMap personFeatures = new SimpleFeatureMapImpl();
	  personFeatures.put("ENTITY_MENTION_TYPE", "NOMINAL");
	  this.defaultAnnotations.add(nominal.getStartNode(),
				      nominal.getEndNode(),
				      PERSON_CATEGORY,
				      personFeatures);
	  //Out.println("creating as new Person");
	  continue;
	}

	// Associate this entity with the most recent Person
	int personIndex = 0;
	
	Annotation previousPerson =
	  previousPeople.get(personIndex);
	
	// Don't associate if the two nominals are not the same gender
	String personGender = (String) 
	  previousPerson.getFeatures().get(PERSON_GENDER_FEATURE_NAME);
	String jobTitleGender = (String) 
          nominal.getFeatures().get(PERSON_GENDER_FEATURE_NAME);
	if (personGender != null && jobTitleGender != null) {
          if (! personGender.equals(jobTitleGender)) {
            //Out.println("wrong gender: " + personGender + " " +
            //            jobTitleGender);
	    continue;
	  }
	}
	
	//Out.println("associating with " +
	//	previousPerson.getFeatures()
	//	.get(TOKEN_STRING_FEATURE_NAME));
	
	anaphor2antecedent.put(nominal, previousPerson);
      }
      else if (nominal.getType().equals(ORGANIZATION_CATEGORY)) {
        // Add each organization entity to the beginning of
	// the organization list
	previousOrgs.add(0, nominal);
	//Out.println("added organization");
      }
      else if (nominal.getType().equals(LOOKUP_CATEGORY)) {
	// Don't associate it if we have no organizations
	if (previousOrgs.size() == 0) {
	  //Out.println("no orgs");
	  continue;
	}
	  
	// Look into the tokens to get some info about POS.
	Annotation[] orgNounTokens =
	  this.defaultAnnotations.get(TOKEN_ANNOTATION_TYPE,
				      nominal.getStartNode().getOffset(),
				      nominal.getEndNode().getOffset()).toArray(new Annotation[0]);
	java.util.Arrays.sort(orgNounTokens, new OffsetComparator());
	Annotation lastToken = orgNounTokens[orgNounTokens.length - 1];
	
	// Don't associate if the org noun is not a singular noun
	if (! lastToken.getFeatures().get(TOKEN_CATEGORY_FEATURE_NAME)
	    .equals("NN")) {
	    //Out.println("Not a singular noun");
	    continue;
	}
	
	//Out.println("organization noun");
	// Associate this entity with the most recent Person
	anaphor2antecedent.put(nominal, previousOrgs.get(0));
      }
    }

    // This method does the dirty work of actually adding new annotations and
    // coreferring.
    generateCorefChains(anaphor2antecedent);
  }

  /**
   * This method specifies whether a given annotation overlaps any of a 
   * set of annotations. For instance, JobTitles occasionally are
   * part of Person annotations.
   * 
   */
  private boolean overlapsAnnotations(Annotation a,
                                      AnnotationSet annotations) {
    Iterator<Annotation> iter = annotations.iterator();
    while (iter.hasNext()) {
      Annotation current = iter.next();
      if (a.overlaps(current)) {
        return true;
      }
    }
      
    return false;
  }

  /** Use this method to keep the current token pointer at the right point
   * in the token list */
  private int advanceTokenPosition(Annotation target, int currentPosition,
				   Object[] tokens) {
    long targetOffset = target.getStartNode().getOffset().longValue();
    long currentOffset = ((Annotation) tokens[currentPosition])
      .getStartNode().getOffset().longValue();
    
    if (targetOffset > currentOffset) {
      while (targetOffset > currentOffset) {
	currentPosition++;
	currentOffset = ((Annotation) tokens[currentPosition])
          .getStartNode().getOffset().longValue();
      }
    }
    else if (targetOffset < currentOffset) {
      while (targetOffset < currentOffset) {
	currentPosition--;
	currentOffset = ((Annotation) tokens[currentPosition])
          .getStartNode().getOffset().longValue();
      }
    }
    
    return currentPosition;
  }

  /** Return the number of tokens between the end of annotation 1 and the
   * beginning of annotation 2. Will return 0 if they are not in order */
  private int countInterveningTokens(Annotation first, Annotation second,
				     int currentPosition, Object[] tokens) {
    int interveningTokens = 0;

    long startOffset = first.getEndNode().getOffset().longValue();
    long endOffset = second.getStartNode().getOffset().longValue();
    
    long currentOffset = ((Annotation) tokens[currentPosition])
      .getStartNode().getOffset().longValue();
    
    while (currentOffset < endOffset) {
      if (currentOffset >= startOffset) {
        interveningTokens++;
      }
      currentPosition++;
      currentOffset = ((Annotation) tokens[currentPosition])
	.getStartNode().getOffset().longValue();
    }
    return interveningTokens;
  }

  /** Get the next token after an annotation */
  private Annotation getFollowingToken(Annotation current, int currentPosition,
				       Object[] tokens) {
    long endOffset = current.getEndNode().getOffset().longValue();
    long currentOffset = ((Annotation) tokens[currentPosition])
      .getStartNode().getOffset().longValue();
    while (currentOffset < endOffset) {
      currentPosition++;
      currentOffset = ((Annotation) tokens[currentPosition])
	.getStartNode().getOffset().longValue();
    }
    return (Annotation) tokens[currentPosition];
  }
	
  /** Get the text of an annotation */
  @SuppressWarnings("unused")
  private String stringValue(Annotation ann) {
    Object[] tokens = getSortedTokens(ann);
	
    StringBuffer output = new StringBuffer();
    for (int i=0;i<tokens.length;i++) {
      Annotation token = (Annotation) tokens[i];
      output.append(token.getFeatures().get(TOKEN_STRING_FEATURE_NAME));
      if (i < tokens.length - 1) {
        output.append(" ");
      }
    }
    return output.toString();
  }
    
  /** Get a sorted array of the tokens that make up a given annotation. */
  private Annotation[] getSortedTokens(Annotation a) {
    Annotation[] annotationTokens =
      this.defaultAnnotations.get(TOKEN_ANNOTATION_TYPE,
				  a.getStartNode().getOffset(),
				  a.getEndNode().getOffset()).toArray(new Annotation[0]);
    java.util.Arrays.sort(annotationTokens, new OffsetComparator());
    return annotationTokens;
  }
	
  /** --- */
  public Map<Annotation,Annotation> getResolvedAnaphora() {
    return this.anaphor2antecedent;
  }

  /** --- */
  private void preprocess() throws ExecutionException {

    //0.5 cleanup
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

    /*
    // initialise the quoted text fragments
    AnnotationSet sentQuotes = this.defaultAnnotations.get(QUOTED_TEXT_TYPE);

    //if none then return
    if (null == sentQuotes) {
      this.quotedText = new Quote[0];
    }
    else {
      this.quotedText = new Quote[sentQuotes.size()];

      Object[] quotesArray = sentQuotes.toArray();
      java.util.Arrays.sort(quotesArray,ANNOTATION_OFFSET_COMPARATOR);

      for (int i =0; i < quotesArray.length; i++) {
        this.quotedText[i] = new Quote((Annotation)quotesArray[i],i);
      }
    }
    */
  }

}
