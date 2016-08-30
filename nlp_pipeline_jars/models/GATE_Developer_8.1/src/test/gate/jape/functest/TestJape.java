/*
 *  TestJape.java
 *
 *  Copyright (c) 1995-2012, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 *  Hamish Cunningham, 23/Feb/00
 *
 *  $Id: TestJape.java 17530 2014-03-04 15:57:43Z markagreenwood $
 */

package gate.jape.functest;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Corpus;
import gate.CorpusController;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;
import gate.persist.PersistenceException;
import gate.util.AnnotationDiffer;
import gate.util.Files;
import gate.util.GateException;
import gate.util.InvalidOffsetException;
import gate.util.Out;
import gate.util.persistence.PersistenceManager;

import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.util.Set;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.io.output.NullOutputStream;
import org.apache.log4j.Logger;

/** 
 * Tests for the Corpus classes
  */
public class TestJape extends BaseJapeTests {
    private static final Logger logger = Logger.getLogger(TestJape.class);

    public TestJape(String name) {
	super(name);
    }

    /** Batch run */
    public void testSimple() throws Exception {
	AnnotationCreator ac = new BaseAnnotationCreator() {

	    @Override
      public AnnotationSet createAnnots(Document doc) throws InvalidOffsetException {
		// defaultAS.add(new Long( 0), new Long( 2), "A",feat);
		add(2, 4, "A");
		// defaultAS.add(new Long( 4), new Long( 6), "A",feat);
		// defaultAS.add(new Long( 6), new Long( 8), "A",feat);
		add(4, 6, "B");
		// defaultAS.add(new Long(10), new Long(12), "B",feat);
		// defaultAS.add(new Long(12), new Long(14), "B",feat);
		// defaultAS.add(new Long(14), new Long(16), "B",feat);
		// defaultAS.add(new Long(16), new Long(18), "B",feat);
		add(6, 8, "C");
		add(8, 10, "C");
		// defaultAS.add(new Long(22), new Long(24), "C",feat);
		// defaultAS.add(new Long(24), new Long(26), "C",feat);
		return doc.getAnnotations();
	    }
	};
	Set<Annotation> res = doTest("texts/doc0.html", "/jape/TestABC.jape", ac);
	Out.println(res);
	assertEquals(res.toString(), 3, res.size());
	compareStartOffsets(res, 2, 2, 2);
	compareEndOffsets(res, 6, 8, 10);
    } // testBatch()


    /**
     * This test loads a saved application which runs several JAPE grammars
     * using different application modes on a specially prepared document.
     * The resulting annotations are checked against gold-standard versions
     * saved in the test document.
     * @throws IOException
     * @throws ResourceInstantiationException
     * @throws PersistenceException
     * @throws ExecutionException
     */
    public void testApplicationModes() throws PersistenceException, ResourceInstantiationException,
	    IOException, ExecutionException {
	//load the application
	URL applicationURL = Files.getGateResource("gate.ac.uk/tests/jape/jape-test.xgapp");
	CorpusController application = (CorpusController) PersistenceManager
		.loadObjectFromUrl(applicationURL);
	//load the test file
	Document testDoc = Factory.newDocument(Files.getGateResource("gate.ac.uk/tests/jape/test-doc.xml"),
		"UTF-8");
	Corpus testCorpus = Factory.newCorpus("JAPE Test Corpus");
	testCorpus.add(testDoc);
	//run the application
	application.setCorpus(testCorpus);
	application.execute();
	//check the results
	AnnotationDiffer annDiff = new AnnotationDiffer();
	annDiff.setSignificantFeaturesSet(null);
	for (String testName : new String[] { "appelt", "brill", "all", "once" }) {
	    AnnotationSet keySet = testDoc.getAnnotations(testName);
	    AnnotationSet responseSet = testDoc.getAnnotations(testName + "-test");
	    annDiff.calculateDiff(keySet, responseSet);
	    double fMeasure = annDiff.getFMeasureStrict(1);
	    assertEquals("Incorrect F-measure for test " + testName, (double) 1, fMeasure);
	}
	//cleanup
	application.setCorpus(null);
	Factory.deleteResource(application);
	testCorpus.remove(0);
	Factory.deleteResource(testDoc);
	Factory.deleteResource(testCorpus);
    }

    /**
     * This test sets up a JAPE transducer based on a grammar
     * (RhsError.jape) that will throw a null pointer exception.
     * The test succeeds so long as we get that exception.
     */
    public void disabled_testRhsErrorMessages() {
	try {

	    // a document with an annotation
	    Document doc = Factory.newDocument("This is a Small Document.");
	    FeatureMap features = Factory.newFeatureMap();
	    features.put("orth", "upperInitial");
	    doc.getAnnotations().add(new Long(0), new Long(8), "Token", features);

	    doTest(doc, "tests/RhsError.jape", null, null);
	    fail("Bad JAPE grammar didn't throw an exception");
	} catch (Exception e) {
	    if (logger.isDebugEnabled())
		logger.info("Exception in Jape rule: " + e);
	}

    } // testRhsErrorMessages

    public void testBrill() throws IOException, GateException, Exception {
	String japeFile = "/gate.ac.uk/tests/jape/control_mode_tests/brill_test.jape";
	String[] expectedResults = { "Find_A", "Find_A", "Find_A_B", "Find_A_B", "Find_A_B_C" };

	AnnotationCreator annotCreator = new BaseAnnotationCreator() {
	    @Override
      public AnnotationSet createAnnots(Document doc) throws InvalidOffsetException {
		add(2, 4, "A");
		add(2, 5, "A");
		add(3, 5, "A");
		add(4, 6, "B");
		add(5, 7, "B");
		add(6, 8, "C");
		add(8, 10, "D");

		return as;
	    }
	};

	Set<Annotation> actualResults = doTest(DEFAULT_DATA_FILE, japeFile, annotCreator);
	Out.println(actualResults);
	compareResults(expectedResults, actualResults);

    } // testBrill()

    public void testAppeltMode() throws IOException, GateException, Exception {
	String japeFile = "/gate.ac.uk/tests/jape/control_mode_tests/appelt_test.jape";
	String[] expectedResults = { "Find_A_B_C" };

	AnnotationCreator annotCreator = new BaseAnnotationCreator() {
	    @Override
      public AnnotationSet createAnnots(Document doc) throws InvalidOffsetException {
		add(2, 4, "A");
		add(4, 6, "B");
		add(2, 3, "C");
		add(3, 8, "D");
		add(2, 3, "A");
		add(3, 4, "B");
		add(4, 9, "C");
		return as;
	    }
	};

	Set<Annotation> actualResults = doTest(DEFAULT_DATA_FILE, japeFile, annotCreator);
	Out.println(actualResults);
	compareResults(expectedResults, actualResults);

    } // testAppelt()

    public void testAllMode() throws IOException, GateException, Exception {
	String japeFile = "/gate.ac.uk/tests/jape/control_mode_tests/all_mode_test.jape";
	String[] expectedResults = { "Find_A", "Find_A", "Find_A_B", "Find_A_B", "Find_A_B_C", "Find_A",
		"Find_A_B", "Find_B_C" };

	AnnotationCreator annotCreator = new BaseAnnotationCreator() {
	    @Override
      public AnnotationSet createAnnots(Document doc) throws InvalidOffsetException {
		add(2, 4, "A");
		add(2, 5, "A");
		add(3, 5, "A");
		add(4, 6, "B");
		add(5, 7, "B");
		add(6, 8, "C");
		add(8, 10, "D");
		return as;
	    }
	};

	Set<Annotation> actualResults = doTest(DEFAULT_DATA_FILE, japeFile, annotCreator);
	Out.println(actualResults);
	compareResults(expectedResults, actualResults);

    } // testAppelt()

    /**
     * This test sets up a JAPE transducer based on a grammar
     * (RhsError2.jape) that will throw a compiler error.
     * The test succeeds so long as we get that exception.
     */
    public void disabled_testRhsErrorMessages2() {

	PrintStream sysout = System.out;
	System.setOut(new PrintStream(new NullOutputStream()));

	// run a JAPE batch on the faulty grammar
	try {
	    Document doc = Factory.newDocument("This is a Small Document.");
	    FeatureMap features = Factory.newFeatureMap();
	    features.put("orth", "upperInitial");
	    doc.getAnnotations().add(new Long(0), new Long(8), "Token", features);
	    doTest(doc, "tests/RhsError2.jape", null, null);
	    fail("Bad JAPE grammar (2) didn't throw an exception");
	} catch (Exception e) {
	    // success
	} finally {
	    System.setOut(sysout);
	}

    } // testRhsErrorMessages2

    public static Test suite() {
	Test suite = new TestSetup(new TestSuite(TestJape.class)) {
	    @Override
      protected void setUp() {
		setUpGate();
		logger.info("GATE initialized and fixure set up.");
	    }
	};
	return suite;
    }

    public static void main(String... args) {
	junit.textui.TestRunner.run(TestJape.suite());
    }
} // class TestJape
