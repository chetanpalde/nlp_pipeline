/*
 *  TestGate.java
 *
 *  Copyright (c) 1995-2012, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 *  Hamish Cunningham, 21/Jan/00
 *
 *  $Id: TestGate.java 18602 2015-03-20 18:14:18Z johann_p $
 */

package gate;

import gate.annotation.TestAnnotation;
import gate.config.TestConfig;
import gate.corpora.TestCorpus;
import gate.corpora.TestDocument;
import gate.corpora.TestDocumentStaxUtils;
import gate.corpora.TestSerialCorpus;
import gate.corpora.TestTikaFormats;
import gate.creole.TestControllers;
import gate.creole.TestCreole;
import gate.creole.TestCreoleAnnotationHandler;
import gate.creole.TestPR;
import gate.creole.TestXSchema;
import gate.creole.annic.test.TestAnnic;
import gate.creole.gazetteer.TestFlexibleGazetteer;
import gate.creole.gazetteer.TestGazetteer;
import gate.creole.morph.TestMorph;
import gate.creole.test.DynamicRegistrationTest;
import gate.email.TestEmail;
import gate.html.TestHtml;
import gate.jape.functest.TestConstraints;
import gate.jape.functest.TestJape;
import gate.persist.TestPersist;
import gate.sgml.TestSgml;
import gate.util.Err;
import gate.util.GateException;
import gate.util.Out;
import gate.util.TestAnnotationMerging;
import gate.util.TestClassificationMeasures;
import gate.util.TestDiffer;
import gate.util.TestFeatureMap;
import gate.util.TestFiles;
import gate.util.TestJavac;
import gate.util.TestRBTreeMap;
import gate.util.TestTemplate;
import gate.util.TestTools;
import gate.xml.TestRepositioningInfo;
import gate.xml.TestXml;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.ontotext.gate.gazetteer.TestHashGazetteer;

/** Top-level entry point for GATE test suite;
  * "main" will run the JUnit test runner interface.
  * <P>
  * Many tests require access to files; generally these files are located
  * on Web servers. In cases where there is no net connection, or the
  * Web servers are down, the test files are searched for in the file system
  * or Jar code base that the system has been loaded from. The search
  * order for test files is like this:
  * <UL>
  * <LI>
  * <A HREF=http://derwent.dcs.shef.ac.uk:80/gate.ac.uk/>
  * http://derwent.dcs.shef.ac.uk:80/gate.ac.uk/</A>
  * <LI>
  * <A HREF=http://gate.ac.uk:80/>http://gate.ac.uk:80/</A>
  * <LI>
  * <A HREF=http://localhost:80/gate.ac.uk/>http://localhost:80/gate.ac.uk/</A>
  * <LI>
  * the file system location that the classes came from, e.g.
  * <TT>z:\gate\classes</TT>, or <TT>jar:....gate.jar</TT>.
  * </UL>
  * This search order can be modified by parameters to the main
  * function (see below).
  */

public class TestGate {

  /** Status flag for normal exit. */
  @SuppressWarnings("unused")
  private static final int STATUS_NORMAL = 0;

  /** Status flag for error exit. */
  private static final int STATUS_ERROR = 1;

  /** GATE test suite. Every test case class has to be
    * registered here.
    */
  public static Test suite() throws Exception {
    // inialise the library. we re-throw any exceptions thrown by
    // init, after printing them out, because the junit gui doesn't
    // say anything more informative than "can't invoke suite" if there's
    // an exception here...

    try {
      //get the config if set through a property
      String configFile = System.getProperty("gate.config");
      if(configFile != null && configFile.length() > 0){
        File f = new File(configFile);
        try {
          @SuppressWarnings("unused")
          URL u = f.toURI().toURL();
        } catch(MalformedURLException e) {
          Err.prln("Bad initialisation file: " + configFile);
          Err.prln(e);
          System.exit(STATUS_ERROR);
        }
        Gate.setSiteConfigFile(f);
      }
      Gate.init();
    } catch(GateException e) {
      Out.prln("can't initialise GATE library! exception = " + e);
      throw(e);
    }

    TestSuite suite = new TestSuite();

    try {
      ////////////////////////////////////////////////
      // Test bench
      ////////////////////////////////////////////////
      // set this true to run all tests; false to run the just one below
      String testName = System.getProperty("gate.testcase");
      if(testName != null) {
        // single test class specified in a system property, so run just
        // that test
        Class<?> testClass = Class.forName(testName);
        Method suiteMethod = testClass.getMethod("suite");
        Test theSuite = (Test)suiteMethod.invoke(null);
        suite.addTest(theSuite);
      } else {
        // no test name specified, so run them all
        suite.addTest(TestUtils.suite());
        suite.addTest(TestAnnic.suite());

        //WordNet has been moved into a plugin along with the test
        //suite.addTest(TestWordNet.suite());

        //The IR impls are now in a plugin along with the tests
        //suite.addTest(TestIndex.suite());
        suite.addTest(TestPersist.suite());
        suite.addTest(TestControllers.suite());

        // the db isn't usually available so this will always fail
        //suite.addTest(TestSecurity.suite());
        suite.addTest(TestConfig.suite());
        suite.addTest(TestAnnotation.suite());
        suite.addTest(TestEmail.suite());

        suite.addTest(TestXml.suite());
        suite.addTest(TestHtml.suite());
        suite.addTest(TestSgml.suite());
        suite.addTest(TestXSchema.suite());

        suite.addTest(TestCreole.suite());
        suite.addTest(TestFiles.suite());
        suite.addTest(TestJavac.suite());
        suite.addTest(TestJape.suite());
        
        //TODO fix these tests so that they work properly
        suite.addTest(TestConstraints.suite());
        
        suite.addTest(TestTemplate.suite());
        suite.addTest(TestDocument.suite());
        suite.addTest(TestDocumentStaxUtils.suite());
        suite.addTest(TestTikaFormats.suite());
        suite.addTest(TestRBTreeMap.suite());
        suite.addTest(TestCorpus.suite());
        suite.addTest(TestSerialCorpus.suite());
        suite.addTest(TestDiffer.suite());
        suite.addTest(TestAnnotationMerging.suite());
        suite.addTest(TestClassificationMeasures.suite());
        suite.addTest(TestGazetteer.suite());
        suite.addTest(TestFeatureMap.suite());
        suite.addTest(TestTools.suite());
        suite.addTest(TestCreoleAnnotationHandler.suite());
        suite.addTest(TestPR.suite());
        suite.addTest(TestMorph.suite());

        //test ontotext gazetteer
        suite.addTest(TestHashGazetteer.suite());
        suite.addTest(TestRepositioningInfo.suite());
        suite.addTest(TestFlexibleGazetteer.suite());

        // Dynamic creole registration
        suite.addTest(DynamicRegistrationTest.suite());

      } // if(allTests)

    } catch(Exception e) {
      Out.prln("can't add tests! exception = " + e);
      throw(e);
    }

    return suite;
  } // suite

} // class TestGate
