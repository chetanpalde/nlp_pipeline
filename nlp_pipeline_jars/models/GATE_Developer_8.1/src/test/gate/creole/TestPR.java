/*
 *  TestPR.java
 *
 *  Copyright (c) 1995-2012, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 *  Oana Hamza,
 *
 *  $Id: TestPR.java 18255 2014-08-19 16:51:25Z markagreenwood $
 */

package gate.creole;

import gate.Annotation;
import gate.AnnotationSet;
import gate.DataStore;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.corpora.TestDocument;
import gate.creole.gazetteer.DefaultGazetteer;
import gate.creole.orthomatcher.OrthoMatcher;
import gate.creole.splitter.SentenceSplitter;
import gate.creole.tokeniser.DefaultTokeniser;
import gate.jape.JapeException;
import gate.jape.constraint.AbstractConstraintPredicate;
import gate.jape.constraint.AnnotationAccessor;
import gate.jape.constraint.ConstraintPredicate;
import gate.jape.constraint.MetaPropertyAccessor;
import gate.util.AnnotationDiffer;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

/** Test the PRs on three documents */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestPR extends TestCase
{
  protected static Document doc1;
  protected static Document doc2;
  protected static Document doc3;
  protected static Document doc4;

  protected static List<String> annotationTypes = new ArrayList<String>(10);

  static{
    annotationTypes.add(ANNIEConstants.SENTENCE_ANNOTATION_TYPE);
    annotationTypes.add(ANNIEConstants.ORGANIZATION_ANNOTATION_TYPE);
    annotationTypes.add(ANNIEConstants.LOCATION_ANNOTATION_TYPE);
    annotationTypes.add(ANNIEConstants.PERSON_ANNOTATION_TYPE);
    annotationTypes.add(ANNIEConstants.DATE_ANNOTATION_TYPE);
    annotationTypes.add(ANNIEConstants.MONEY_ANNOTATION_TYPE);
    annotationTypes.add(ANNIEConstants.LOOKUP_ANNOTATION_TYPE);
    annotationTypes.add(ANNIEConstants.TOKEN_ANNOTATION_TYPE);
    try{
      //get 4 documents
      if (doc1 == null)
        doc1 = Factory.newDocument(
            new URL(TestDocument.getTestServerName() +
                    "tests/ft-bt-03-aug-2001.html"),
            "ISO-8859-1"
            );

      if (doc2 == null)
        doc2 = Factory.newDocument(
            new URL(TestDocument.getTestServerName() +
                    "tests/gu-Am-Brit-4-aug-2001.html"),
            "ISO-8859-1"
            );

      if (doc3 == null)
        doc3 = Factory.newDocument(
            new URL(TestDocument.getTestServerName() +
                    "tests/in-outlook-09-aug-2001.html"),
            "ISO-8859-1"
            );
      if (doc4 == null)
        doc4 = Factory.newDocument(
            new URL(TestDocument.getTestServerName() +
                    "tests/OrthoMatcherTest.txt"),
            "UTF-8"
            );
    }catch(Exception e){
      e.printStackTrace();
    }
  }

  /** Construction */
  public TestPR(String name) { super(name); }

  /** Fixture set up */
  @Override
  public void setUp() throws Exception {
  } // setUp

  /** Put things back as they should be after running tests.
    */
  @Override
  public void tearDown() throws Exception {
  } // tearDown

  public void test001Tokenizer() throws Exception {
    FeatureMap params = Factory.newFeatureMap();
    DefaultTokeniser tokeniser = (DefaultTokeniser) Factory.createResource(
                    "gate.creole.tokeniser.DefaultTokeniser", params);


    //run the tokeniser for doc1
    tokeniser.setDocument(doc1);
    tokeniser.execute();

    //run the tokeniser for doc2
    tokeniser.setDocument(doc2);
    tokeniser.execute();

    //run the tokeniser for doc3
    tokeniser.setDocument(doc3);
    tokeniser.execute();

    tokeniser.setDocument(doc4);
    tokeniser.execute();

    Factory.deleteResource(tokeniser);

    // assertions for doc 1
    assertTrue("Found in "+doc1.getSourceUrl().getFile()+ " "+
      doc1.getAnnotations().size() +
      " Token annotations, instead of the expected 1279.",
      doc1.getAnnotations().size()== 1279);

    // assertions for doc 2
    assertTrue("Found in "+ doc2.getSourceUrl().getFile()+ " "+
      doc2.getAnnotations().size() +
      " Token annotations, instead of the expected 2134.",
      doc2.getAnnotations().size()== 2134);

    // assertions for doc 3
    assertTrue("Found in "+ doc3.getSourceUrl().getFile()+ " "+
      doc3.getAnnotations().size() +
      " Token annotations, instead of the expected 2807.",
      doc3.getAnnotations().size()== 2807);

  }// testTokenizer

  public void test002Gazetteer() throws Exception {
    FeatureMap params = Factory.newFeatureMap();
    DefaultGazetteer gaz = (DefaultGazetteer) Factory.createResource(
                          "gate.creole.gazetteer.DefaultGazetteer", params);

    //run gazetteer for doc1
    gaz.setDocument(doc1);
    gaz.execute();

    //run gazetteer for doc2
    gaz.setDocument(doc2);
    gaz.execute();

    //run gazetteer for doc3
    gaz.setDocument(doc3);
    gaz.execute();

    //run gazetteer for doc3
    gaz.setDocument(doc4);
    gaz.execute();


    Factory.deleteResource(gaz);

//    assertTrue("Found in "+ doc1.getSourceUrl().getFile()+ " "+
//      doc1.getAnnotations().get(ANNIEConstants.LOOKUP_ANNOTATION_TYPE).size() +
//      " Lookup annotations, instead of the expected 60.",
//      doc1.getAnnotations().get(ANNIEConstants.LOOKUP_ANNOTATION_TYPE).size()== 60);
    assertEquals("Wrong number of annotations produced in " +
            doc1.getSourceUrl().getFile(),
            133,
            doc1.getAnnotations().get(ANNIEConstants.LOOKUP_ANNOTATION_TYPE).size());

//    assertTrue("Found in "+ doc2.getSourceUrl().getFile()+ " "+
//      doc2.getAnnotations().get(ANNIEConstants.LOOKUP_ANNOTATION_TYPE).size() +
//      " Lookup annotations, instead of the expected 134.",
//      doc2.getAnnotations().get(ANNIEConstants.LOOKUP_ANNOTATION_TYPE).size()== 134);
    assertEquals("Wrong number of annotations produced in " +
            doc2.getSourceUrl().getFile(),
            232,
            doc2.getAnnotations().get(ANNIEConstants.LOOKUP_ANNOTATION_TYPE).size());

//    assertTrue("Found in "+ doc3.getSourceUrl().getFile()+ " "+
//      doc3.getAnnotations().get(ANNIEConstants.LOOKUP_ANNOTATION_TYPE).size() +
//      " Lookup annotations, instead of the expected 144.",
//      doc3.getAnnotations().get(ANNIEConstants.LOOKUP_ANNOTATION_TYPE).size()== 144);
    assertEquals("Wrong number of annotations produced in " +
            doc3.getSourceUrl().getFile(),
            282,
            doc3.getAnnotations().get(ANNIEConstants.LOOKUP_ANNOTATION_TYPE).size());
  }//testGazetteer

  public void test003Splitter() throws Exception {
    FeatureMap params = Factory.newFeatureMap();
    SentenceSplitter splitter = (SentenceSplitter) Factory.createResource(
                          "gate.creole.splitter.SentenceSplitter", params);

    //run splitter for doc1
    splitter.setDocument(doc1);
    splitter.execute();

    //run splitter for doc2
    splitter.setDocument(doc2);
    splitter.execute();

    //run splitter for doc3
    splitter.setDocument(doc3);
    splitter.execute();

    //run splitter for doc3
    splitter.setDocument(doc4);
    splitter.execute();


    Factory.deleteResource(splitter);

    // assertions for doc 1
    assertTrue("Found in "+ doc1.getSourceUrl().getFile()+ " "+
      doc1.getAnnotations().get(ANNIEConstants.SENTENCE_ANNOTATION_TYPE).size() +
      " Sentence annotations, instead of the expected 25.",
      doc1.getAnnotations().get(ANNIEConstants.SENTENCE_ANNOTATION_TYPE).size()== 25);

    assertTrue("Found in "+ doc1.getSourceUrl().getFile()+ " "+
      doc1.getAnnotations().get("Split").size() +
      " Split annotations, instead of the expected 66.",
      doc1.getAnnotations().get("Split").size()== 66);

    // assertions for doc 2
    assertTrue("Found in "+ doc2.getSourceUrl().getFile()+ " "+
      doc2.getAnnotations().get(ANNIEConstants.SENTENCE_ANNOTATION_TYPE).size() +
      " Sentence annotations, instead of the expected 57.",
      doc2.getAnnotations().get(ANNIEConstants.SENTENCE_ANNOTATION_TYPE).size()== 57);

    assertTrue("Found in "+ doc2.getSourceUrl().getFile()+ " "+
      doc2.getAnnotations().get("Split").size() +
      " Split annotations, instead of the expected 110.",
      doc2.getAnnotations().get("Split").size()== 110);

    // assertions for doc 3
    assertTrue("Found in "+ doc3.getSourceUrl().getFile()+ " "+
      doc3.getAnnotations().get(ANNIEConstants.SENTENCE_ANNOTATION_TYPE).size() +
      " Sentence annotations, instead of the expected 75.",
      doc3.getAnnotations().get(ANNIEConstants.SENTENCE_ANNOTATION_TYPE).size()== 75);

    assertTrue("Found in "+ doc3.getSourceUrl().getFile()+ " "+
      doc3.getAnnotations().get("Split").size() +
      " Split annotations, instead of the expected 122.",
      doc3.getAnnotations().get("Split").size()== 122);
  }//testSplitter

  public void test004Tagger() throws Exception {
    FeatureMap params = Factory.newFeatureMap();
    POSTagger tagger = (POSTagger) Factory.createResource(
                          "gate.creole.POSTagger", params);


    //run the tagger for doc1
    tagger.setDocument(doc1);
    tagger.execute();

    //run the tagger for doc2
    tagger.setDocument(doc2);
    tagger.execute();

    //run the tagger for doc3
    tagger.setDocument(doc3);
    tagger.execute();

    //run the tagger for doc3
    tagger.setDocument(doc4);
    tagger.execute();

    Factory.deleteResource(tagger);

    HashSet<String> fType = new HashSet<String>();
    fType.add(ANNIEConstants.TOKEN_CATEGORY_FEATURE_NAME);

    // assertions for doc 1
    AnnotationSet annots =
      doc1.getAnnotations().get(ANNIEConstants.TOKEN_ANNOTATION_TYPE, fType);

    assertTrue("Found in "+ doc1.getSourceUrl().getFile()+ " "+ annots.size() +
      " Token annotations with category feature, instead of the expected 675.",
      annots.size() == 675);

    // assertions for doc 2
    annots = doc2.getAnnotations().get(ANNIEConstants.TOKEN_ANNOTATION_TYPE, fType);
    assertTrue("Found in "+  doc2.getSourceUrl().getFile()+ " "+annots.size() +
      " Token annotations with category feature, instead of the expected 1131.",
      annots.size() == 1131);

    // assertions for doc 3
    annots = doc3.getAnnotations().get(ANNIEConstants.TOKEN_ANNOTATION_TYPE, fType);
    assertTrue("Found in "+ doc3.getSourceUrl().getFile()+ " "+ annots.size() +
      " Token annotations with category feature, instead of the expected 1447.",
      annots.size() == 1447);
  }//testTagger()

  public void test005Transducer() throws Exception {
    FeatureMap params = Factory.newFeatureMap();
    ANNIETransducer transducer = (ANNIETransducer) Factory.createResource(
                          "gate.creole.ANNIETransducer", params);

    //run the transducer for doc1
    transducer.setDocument(doc1);
    transducer.execute();

    //run the transducer for doc2
    transducer.setDocument(doc2);
    transducer.execute();

    //run the transducer for doc3
    transducer.setDocument(doc3);
    transducer.execute();

    //run the transducer for doc3
    transducer.setDocument(doc4);
    transducer.execute();

    Factory.deleteResource(transducer);

    // assertions for doc 1
    assertTrue("Found in "+ doc1.getSourceUrl().getFile()+ " "+
      doc1.getAnnotations().get(ANNIEConstants.ORGANIZATION_ANNOTATION_TYPE).size() +
      " Organization annotations, instead of the expected 28",
      doc1.getAnnotations().get(ANNIEConstants.ORGANIZATION_ANNOTATION_TYPE).size()== 27);
    assertTrue("Found in "+doc1.getSourceUrl().getFile()+ " "+
      doc1.getAnnotations().get(ANNIEConstants.LOCATION_ANNOTATION_TYPE).size() +
      " Location annotations, instead of the expected 2",
      doc1.getAnnotations().get(ANNIEConstants.LOCATION_ANNOTATION_TYPE).size()== 2);
    assertTrue("Found in "+doc1.getSourceUrl().getFile()+ " "+
      doc1.getAnnotations().get(ANNIEConstants.PERSON_ANNOTATION_TYPE).size() +
      " Person annotations, instead of the expected 2",
      doc1.getAnnotations().get(ANNIEConstants.PERSON_ANNOTATION_TYPE).size()== 2);
    assertTrue("Found in "+doc1.getSourceUrl().getFile()+ " "+
      doc1.getAnnotations().get(ANNIEConstants.DATE_ANNOTATION_TYPE).size() +
      " Date annotations, instead of the expected 7",
      doc1.getAnnotations().get(ANNIEConstants.DATE_ANNOTATION_TYPE).size()== 7);
    assertTrue("Found in "+doc1.getSourceUrl().getFile()+ " "+
      doc1.getAnnotations().get(ANNIEConstants.MONEY_ANNOTATION_TYPE).size() +
      " Money annotations, instead of the expected 1",
      doc1.getAnnotations().get(ANNIEConstants.MONEY_ANNOTATION_TYPE).size()== 1);

    // assertions for doc 2
    assertTrue("Found in "+doc2.getSourceUrl().getFile()+ " "+
      doc2.getAnnotations().get(ANNIEConstants.ORGANIZATION_ANNOTATION_TYPE).size() +
      " Organization annotations, instead of the expected 25",
      doc2.getAnnotations().get(ANNIEConstants.ORGANIZATION_ANNOTATION_TYPE).size()== 25);
    assertTrue("Found in "+doc2.getSourceUrl().getFile()+ " "+
      doc2.getAnnotations().get(ANNIEConstants.LOCATION_ANNOTATION_TYPE).size() +
      " Location annotations, instead of the expected 10",
      doc2.getAnnotations().get(ANNIEConstants.LOCATION_ANNOTATION_TYPE).size()== 10);
    assertTrue("Found in "+doc2.getSourceUrl().getFile()+ " "+
      doc2.getAnnotations().get(ANNIEConstants.PERSON_ANNOTATION_TYPE).size() +
      " Person annotations, instead of the expected 2",
      doc2.getAnnotations().get(ANNIEConstants.PERSON_ANNOTATION_TYPE).size()== 2);
    assertTrue("Found in "+doc2.getSourceUrl().getFile()+ " "+
      doc2.getAnnotations().get(ANNIEConstants.DATE_ANNOTATION_TYPE).size() +
      " Date annotations, instead of the expected 8",
      doc2.getAnnotations().get(ANNIEConstants.DATE_ANNOTATION_TYPE).size()== 8);
    assertTrue("Found in "+doc2.getSourceUrl().getFile()+ " "+
      doc2.getAnnotations().get(ANNIEConstants.MONEY_ANNOTATION_TYPE).size() +
      " Money annotations, instead of the expected 3",
      doc2.getAnnotations().get(ANNIEConstants.MONEY_ANNOTATION_TYPE).size()== 3);

    // assertions for doc 3
    assertEquals("Found in "+doc3.getSourceUrl().getFile()+ " "+
      " wrong number of Organization annotations",29,
      doc3.getAnnotations().get(ANNIEConstants.ORGANIZATION_ANNOTATION_TYPE).size());
    assertTrue("Found in "+doc3.getSourceUrl().getFile()+ " "+
      doc3.getAnnotations().get(ANNIEConstants.LOCATION_ANNOTATION_TYPE).size() +
      " Location annotations, instead of the expected 11",
      doc3.getAnnotations().get(ANNIEConstants.LOCATION_ANNOTATION_TYPE).size()== 11);
    assertTrue("Found in "+doc3.getSourceUrl().getFile()+ " "+
      doc3.getAnnotations().get(ANNIEConstants.PERSON_ANNOTATION_TYPE).size() +
      " Person annotations, instead of the expected 8",
      doc3.getAnnotations().get(ANNIEConstants.PERSON_ANNOTATION_TYPE).size()== 8);
    assertTrue("Found in "+doc3.getSourceUrl().getFile()+ " "+
      doc3.getAnnotations().get(ANNIEConstants.DATE_ANNOTATION_TYPE).size() +
      " Date annotations, instead of the expected 7",
      doc3.getAnnotations().get(ANNIEConstants.DATE_ANNOTATION_TYPE).size()== 7);
    assertTrue("Found in "+doc3.getSourceUrl().getFile()+ " "+
      doc3.getAnnotations().get(ANNIEConstants.MONEY_ANNOTATION_TYPE).size() +
      " Money annotations, instead of the expected 4",
      doc3.getAnnotations().get(ANNIEConstants.MONEY_ANNOTATION_TYPE).size()== 4);

    assertEquals("Wrong number of Person annotations in OrthoMatcher test document",22,
            doc4.getAnnotations().get(ANNIEConstants.PERSON_ANNOTATION_TYPE).size());
  }//testTransducer

  public void test006CustomConstraintDefs() throws Exception {
    FeatureMap params = Factory.newFeatureMap();

    List<String> operators = new ArrayList<String>();
    params.put("operators", operators);
    ConstraintPredicate testPred = new TestConstraintPredicate();
    operators.add(testPred.getClass().getName());

    List<String> accessors = new ArrayList<String>();
    params.put("annotationAccessors", accessors);
    AnnotationAccessor testAccessor = new TestAnnotationAccessor();
    accessors.add(testAccessor.getClass().getName());

    ANNIETransducer transducer = (ANNIETransducer) Factory.createResource(
                          "gate.creole.ANNIETransducer", params);

    assertEquals(accessors, transducer.getAnnotationAccessors());
    assertEquals(operators, transducer.getOperators());

    ConstraintPredicate returnedPred = Factory.getConstraintFactory().createPredicate("fooOp", testAccessor, "fooValue");
    assertNotNull(returnedPred);
    assertEquals("Operator not set", testPred.getClass(), returnedPred.getClass());

    AnnotationAccessor returnAccessor = Factory.getConstraintFactory().createMetaPropertyAccessor("fooProp");
    assertNotNull(returnAccessor);
    assertEquals("Accessor not set", testAccessor.getClass(), returnAccessor.getClass());
  }

  @SuppressWarnings("unchecked")
  public void test007Orthomatcher() throws Exception {
    FeatureMap params = Factory.newFeatureMap();

    OrthoMatcher orthomatcher = (OrthoMatcher) Factory.createResource(
                          "gate.creole.orthomatcher.OrthoMatcher", params);


    // run the orthomatcher for doc1
    orthomatcher.setDocument(doc1);
    orthomatcher.execute();

    //run the orthomatcher for doc2
    orthomatcher.setDocument(doc2);
    orthomatcher.execute();

    //run the orthomatcher for doc3
    orthomatcher.setDocument(doc3);
    orthomatcher.execute();

    //run the orthomatcher for doc3
    orthomatcher.setDocument(doc4);
    orthomatcher.execute();

    Factory.deleteResource(orthomatcher);

    HashSet<String> fType = new HashSet<String>();
    fType.add(ANNIEConstants.ANNOTATION_COREF_FEATURE_NAME);

    //TODO why do we get these annotation sets out without actually checking the sizes?
    
    @SuppressWarnings("unused")
    AnnotationSet annots =
                  doc1.getAnnotations().get(null,fType);

//    assertEquals("Wrong number of annotations with matches feature",
//            17, annots.size());

    annots = doc2.getAnnotations().get(null,fType);
//    assertEquals("Wrong number of annotations with matches feature",
//            31, annots.size());

    annots = doc3.getAnnotations().get(null,fType);
//    assertTrue("Found in "+doc3.getSourceUrl().getFile()+ " "+ annots.size() +
//      " annotations with matches feature, instead of the expected 39.",
//      annots.size() == 39);

    AnnotationSet personAnnots = doc4.getAnnotations().get("Person");
    Annotation sarahAnnot = personAnnots.get(new Long(806), new Long(811)).iterator().next();
    assertEquals("Wrong number of matches for second Sarah in document", 2,
            ((List<Integer>) sarahAnnot.getFeatures().get("matches")).size());

    Annotation robertQJones = personAnnots.get(new Long(300), new Long(315)).iterator().next();
    assertEquals("Wrong number of matches for Robert Q Jones in document", 3,
            ((List<Integer>) robertQJones.getFeatures().get("matches")).size());

    Annotation robertCJones = personAnnots.get(new Long(0), new Long(15)).iterator().next();
    assertEquals("Wrong number of matches for Robert C Jones in document", 3,
            ((List<Integer>) robertCJones.getFeatures().get("matches")).size());

    Annotation robertAnderson = personAnnots.get(new Long(1188), new Long(1203)).iterator().next();
    assertEquals("Found a match for Robert Anderson, but he should not have been matched.", false,
            robertAnderson.getFeatures().containsKey("matches"));



  }//testOrthomatcher

  /** A test for comparing the annotation sets*/
  public void test008AllPR() throws Exception {

    // verify if the saved data store is the same with the just processed file
    // first document

    URL urlBase = new URL(TestDocument.getTestServerName());

    URL storageDir = null;
    storageDir = new URL(urlBase, "tests/ft");

    //open the data store
    DataStore ds = Factory.openDataStore
                    ("gate.persist.SerialDataStore",
                     storageDir.toExternalForm());

    //get LR id
    String lrId = ds.getLrIds("gate.corpora.DocumentImpl").get(0);


    // get the document from data store
    FeatureMap features = Factory.newFeatureMap();
    features.put(DataStore.DATASTORE_FEATURE_NAME, ds);
    features.put(DataStore.LR_ID_FEATURE_NAME, lrId);
    Document document = (Document) Factory.createResource(
                                      "gate.corpora.DocumentImpl",
                                      features);
    compareAnnots(document, doc1);

    // second document
    storageDir = null;
    storageDir = new URL(urlBase, "tests/gu");

    //open the data store
    ds = Factory.openDataStore("gate.persist.SerialDataStore",
                               storageDir.toExternalForm());
    //get LR id
    lrId = ds.getLrIds("gate.corpora.DocumentImpl").get(0);
    // get the document from data store
    features = Factory.newFeatureMap();
    features.put(DataStore.DATASTORE_FEATURE_NAME, ds);
    features.put(DataStore.LR_ID_FEATURE_NAME, lrId);
    document = (Document) Factory.createResource(
                                      "gate.corpora.DocumentImpl",
                                      features);
    compareAnnots(document,doc2);

    // third document
    storageDir = null;
    storageDir = new URL(urlBase, "tests/in");

    //open the data store
    ds = Factory.openDataStore("gate.persist.SerialDataStore",
                               storageDir.toExternalForm());
    //get LR id
    lrId = ds.getLrIds("gate.corpora.DocumentImpl").get(0);
    // get the document from data store
    features = Factory.newFeatureMap();
    features.put(DataStore.DATASTORE_FEATURE_NAME, ds);
    features.put(DataStore.LR_ID_FEATURE_NAME, lrId);
    document = (Document) Factory.createResource(
                                "gate.corpora.DocumentImpl",
                                features);
    compareAnnots(document,doc3);
  } // testAllPR()

//  public void compareAnnots1(Document keyDocument, Document responseDocument)
//              throws Exception{
//    // organization type
//    Iterator iteratorTypes = annotationTypes.iterator();
//    while (iteratorTypes.hasNext()){
//      // get the type of annotation
//      String annotType = (String)iteratorTypes.next();
//      // create annotation schema
//      AnnotationSchema annotationSchema = new AnnotationSchema();
//
//      annotationSchema.setAnnotationName(annotType);
//
//      // create an annotation diff
//      AnnotationDiff annotDiff = new AnnotationDiff();
//      annotDiff.setKeyDocument(keyDocument);
//      annotDiff.setResponseDocument(responseDocument);
//      annotDiff.setAnnotationSchema(annotationSchema);
//      annotDiff.setKeyAnnotationSetName(null);
//      annotDiff.setResponseAnnotationSetName(null);
//
//      Set significantFeatures = new HashSet(Arrays.asList(
//                    new String[]{"NMRule", "kind", "orgType", "rule",
//                                 "rule1", "rule2", "locType", "gender",
//                                 "majorType", "minorType", "category",
//                                 "length", "orth", "string", "subkind",
//                                 "symbolkind"}));
//      annotDiff.setKeyFeatureNamesSet(significantFeatures);
//      annotDiff.setTextMode(new Boolean(true));
//
//      annotDiff.init();
//
//      if (DEBUG){
//        if (annotDiff.getFMeasureAverage() != 1.0) {
//          assertTrue("missing annotations " +
//            annotDiff.getAnnotationsOfType(AnnotationDiff.MISSING_TYPE)
//            + " spurious annotations " +
//            annotDiff.getAnnotationsOfType(AnnotationDiff.SPURIOUS_TYPE)
//            + " partially-correct annotations " +
//            annotDiff.getAnnotationsOfType(
//                            AnnotationDiff.PARTIALLY_CORRECT_TYPE),false);
//        }
//      }//if
//
//      assertTrue(annotType+ " precision average in "+
//        responseDocument.getSourceUrl().getFile()+
//        " is "+ annotDiff.getPrecisionAverage()+ " instead of 1.0 ",
//        annotDiff.getPrecisionAverage()== 1.0);
//      assertTrue(annotType+" recall average in "
//        +responseDocument.getSourceUrl().getFile()+
//        " is " + annotDiff.getRecallAverage()+ " instead of 1.0 ",
//        annotDiff.getRecallAverage()== 1.0);
//      assertTrue(annotType+" f-measure average in "
//        +responseDocument.getSourceUrl().getFile()+
//        " is "+ annotDiff.getFMeasureAverage()+ " instead of 1.0 ",
//        annotDiff.getFMeasureAverage()== 1.0);
//     }//while
//   }// public void compareAnnots
//
   public void compareAnnots(Document keyDocument, Document responseDocument)
                throws Exception{
      // organization type
      //DocumentStaxUtils.writeDocument(responseDocument, new File("/home/mark/"+responseDocument.getName()+".xml"));
      Iterator<String> iteratorTypes = annotationTypes.iterator();
      while (iteratorTypes.hasNext()){
        // get the type of annotation
        String annotType = iteratorTypes.next();

        // create an annotation diff
        AnnotationDiffer annotDiffer = new AnnotationDiffer();
        Set<String> significantFeatures = new HashSet<String>(Arrays.asList(
                      new String[]{"NMRule", "kind", "orgType", "rule",
                                   "rule1", "rule2", "locType", "gender",
                                   "majorType", "minorType", "category",
                                   "length", "orth", "string", "subkind",
                                   "symbolkind"}));
        annotDiffer.setSignificantFeaturesSet(significantFeatures);
        annotDiffer.calculateDiff(keyDocument.getAnnotations().get(annotType),
                                  responseDocument.getAnnotations().get(annotType));
        
        annotDiffer.printMissmatches();       
        
        assertTrue(annotType+ " precision strict in "+
          responseDocument.getSourceUrl().getFile()+
          " is "+ annotDiffer.getPrecisionStrict()+ " instead of 1.0 ",
          annotDiffer.getPrecisionStrict()== 1.0);

        assertTrue(annotType+" recall strict in "
          +responseDocument.getSourceUrl().getFile()+
          " is " + annotDiffer.getRecallStrict()+ " instead of 1.0 ",
          annotDiffer.getRecallStrict()== 1.0);

        assertTrue(annotType+" f-measure strict in "
          +responseDocument.getSourceUrl().getFile()+
          " is "+ annotDiffer.getFMeasureStrict(0.5)+ " instead of 1.0 ",
          annotDiffer.getFMeasureStrict(0.5)== 1.0);
      }//while
     }// public void compareAnnots

   public static class TestConstraintPredicate extends AbstractConstraintPredicate {
    
     private static final long serialVersionUID = -8980180587795897947L;
    
     @Override
     protected boolean doMatch(Object value, AnnotationSet context)
             throws JapeException {
       return false;
     }
     
     @Override
     public String getOperator() {
       return "fooOp";
     }
   };

   public static class TestAnnotationAccessor extends MetaPropertyAccessor {
  
    private static final long serialVersionUID = 6967907751030807600L;

    @Override
    public Object getValue(Annotation annot, AnnotationSet context) {
       return "foo";
     }

    @Override
    public Object getKey() {
      return "fooProp";
    }
   };

  /** Test suite routine for the test runner */
  public static Test suite() {
    return new TestSuite(TestPR.class);
  } // suite

  /*public static void main(String[] args) {
    try{
      Gate.init();
      TestPR testPR = new TestPR("");
      testPR.setUp();
      testPR.testTokenizer();
      testPR.testGazetteer();
      testPR.testSplitter();
      testPR.testTagger();
      testPR.testTransducer();
      testPR.testOrthomatcher();
      testPR.testAllPR();
      testPR.tearDown();
    } catch(Exception e) {
      e.printStackTrace();
    }
  }*/ // main
} // class TestPR
