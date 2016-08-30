package com.ontotext.gate.gazetteer;
/*
 *  HashGazetteer.java
 *
 *  OntoText Lab.
 *
 *  borislav popov , 09/11/2001
 *
 *  $Id: TestHashGazetteer.java 18253 2014-08-19 16:10:55Z markagreenwood $
 */

import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.Gate;
import gate.corpora.TestDocument;
import java.net.URL;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Tests the HashGazetteer.
 */
public class TestHashGazetteer extends TestCase {

  private static final String GAZ_AS = "GazetteerAS";
  public TestHashGazetteer(String name) {
    super(name);
  }

  /** Fixture set up */
  @Override
  public void setUp() throws Exception {
  }

  @Override
  public void tearDown() throws Exception {
  } // tearDown

  /** Test the default tokeniser */
  public void testHashGazetteer() throws Exception {
    //get a document
    Document doc = Factory.newDocument(
      new URL(TestDocument.getTestServerName() + "tests/doc0.html")
    );
    
    System.out.println(doc.getFeatures().get("gate.SourceURL"));

    //create a default gazetteer
    FeatureMap params = Factory.newFeatureMap();
    HashGazetteer gaz = (HashGazetteer) Factory.createResource(
                          "com.ontotext.gate.gazetteer.HashGazetteer", params);

    //runtime stuff
    gaz.setDocument(doc);
    gaz.setAnnotationSetName(GAZ_AS);
    gaz.execute();

    assertTrue("the Annotation set resulting of the execution of the OntoText "
            +"Natural Gazetteer is empty."
            ,!doc.getAnnotations(GAZ_AS).isEmpty());

    //check whether the annotations are as expected
    assertEquals("wrong number of lookup annotations found",76,
      doc.getAnnotations(GAZ_AS).size());

  } // testHashGazetteer();

  /** Test suite routine for the test runner */
  public static Test suite() {
    return new TestSuite(TestHashGazetteer.class);
  } // suite

  public static void main(String[] args) {
    try{
      Gate.init();
      TestHashGazetteer testGaz = new TestHashGazetteer("");
      testGaz.setUp();
      testGaz.testHashGazetteer();
      testGaz.tearDown();
    } catch(Exception e) {
      e.printStackTrace();
    }
  } // main
} // TestHashGazetteer
