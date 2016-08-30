/*
 *  TestGazetteer.java
 *
 *  Copyright (c) 1995-2012, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 *  Valentin Tablan, 25/10/2000
 *
 *  $Id: TestGazetteer.java 18255 2014-08-19 16:51:25Z markagreenwood $
 */

package gate.creole.gazetteer;

import java.net.URL;

import junit.framework.*;

import gate.*;
import gate.corpora.TestDocument;

public class TestGazetteer extends TestCase {

  public TestGazetteer(String name) {
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
  public void testDefaultGazetteer() throws Exception {
    //get a document
    Document doc = Factory.newDocument(
      new URL(TestDocument.getTestServerName() + "tests/doc0.html")
    );

    //create a default gazetteer
    DefaultGazetteer gaz = (DefaultGazetteer) Factory.createResource(
                          "gate.creole.gazetteer.DefaultGazetteer");

    //runtime stuff
    gaz.setDocument(doc);
    gaz.setAnnotationSetName("GazetteerAS");
    //test with default parameters
    gaz.execute();
    AnnotationSet resultAS = doc.getAnnotations("GazetteerAS");
    assertEquals("Wrong number of annotations produced",63, resultAS.size());
    resultAS.clear();
    
    //test with partial words
    gaz.setWholeWordsOnly(false);
    gaz.execute();
    assertEquals("Wrong number of annotations produced", 400, resultAS.size());
    gaz.setWholeWordsOnly(true);
    resultAS.clear();

    //test with prefix matching
    gaz.setLongestMatchOnly(false);
    gaz.execute();
    assertEquals("Wrong number of annotations produced", 78, resultAS.size());
    gaz.setLongestMatchOnly(true);
    resultAS.clear();
    Factory.deleteResource(gaz);
    
    //test with case insensitive
    FeatureMap fm = Factory.newFeatureMap();
    fm.put(DefaultGazetteer.DEF_GAZ_CASE_SENSITIVE_PARAMETER_NAME, false);
    gaz = (DefaultGazetteer) Factory.createResource(
    "gate.creole.gazetteer.DefaultGazetteer", fm);
    gaz.setDocument(doc);
    gaz.setAnnotationSetName("GazetteerAS");
    gaz.execute();
    assertEquals("Wrong number of annotations generated", 104, resultAS.size());
    gaz.setCaseSensitive(true);
    resultAS.clear();
    Factory.deleteResource(gaz);
    Factory.deleteResource(doc);
  }

  /** Test suite routine for the test runner */
  public static Test suite() {
    return new TestSuite(TestGazetteer.class);
  } // suite

  public static void main(String[] args) {
    try{
      Gate.init();
      TestGazetteer testGaz = new TestGazetteer("");
      testGaz.setUp();
      testGaz.testDefaultGazetteer();
      testGaz.tearDown();
    } catch(Exception e) {
      e.printStackTrace();
    }
  } // main

} // TestGazetteer
