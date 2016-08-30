/*
 *  TestControllers.java
 *
 *  Copyright (c) 1995-2012, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 *  Hamish Cunningham, 16/Mar/00
 *
 *  $Id: TestControllers.java 17530 2014-03-04 15:57:43Z markagreenwood $
 */

package gate.creole;

import java.net.URL;

import junit.framework.*;

import gate.*;
import gate.corpora.TestDocument;
import gate.creole.gazetteer.DefaultGazetteer;
import gate.creole.tokeniser.DefaultTokeniser;
import gate.util.GateException;
import gate.util.Out;

/** Tests for controller classes
  */
public class TestControllers extends TestCase
{
  /** Debug flag */
  private static final boolean DEBUG = false;

  /** The CREOLE register */
  CreoleRegister reg;

  /** Construction */
  public TestControllers(String name) { super(name); }

  /** Fixture set up */
  @Override
  public void setUp() throws GateException {
    // Initialise the GATE library and get the creole register
    Gate.init();
    reg = Gate.getCreoleRegister();

  } // setUp

  /** Put things back as they should be after running tests
    * (reinitialise the CREOLE register).
    */
  @Override
  public void tearDown() throws Exception {
    reg.clear();
    Gate.init();
  } // tearDown

  /** Serial controller test 1 */
  public void testSerial1() throws Exception {
    // a controller
    SerialController c1 = new SerialController();
    assertNotNull("c1 controller is null", c1);

    // set a name for this controller
    c1.setName("SerialController_"+Gate.genSym());
    
    //get a document
    FeatureMap params = Factory.newFeatureMap();
    params.put(Document.DOCUMENT_URL_PARAMETER_NAME, new URL(TestDocument.getTestServerName()+"tests/doc0.html"));
    params.put(Document.DOCUMENT_MARKUP_AWARE_PARAMETER_NAME, "false");
    Document doc = (Document)Factory.createResource("gate.corpora.DocumentImpl",
                                                    params);

    if(DEBUG) {
      ResourceData docRd = reg.get("gate.corpora.DocumentImpl");
      assertNotNull("Couldn't find document res data", docRd);
      Out.prln(docRd.getParameterList().getInitimeParameters());
    }

    //create a default tokeniser
    params = Factory.newFeatureMap();
    params.put(DefaultTokeniser.DEF_TOK_DOCUMENT_PARAMETER_NAME, doc);
    ProcessingResource tokeniser = (ProcessingResource) Factory.createResource(
      "gate.creole.tokeniser.DefaultTokeniser", params
    );

    //create a default gazetteer
    params = Factory.newFeatureMap();
    params.put(DefaultGazetteer.DEF_GAZ_DOCUMENT_PARAMETER_NAME, doc);
    ProcessingResource gaz = (ProcessingResource) Factory.createResource(
      "gate.creole.gazetteer.DefaultGazetteer", params
    );

    // get the controller to encapsulate the tok and gaz
    c1.add(tokeniser);
    c1.add(gaz);
    c1.execute();

    // check the resulting annotations
    if(DEBUG) {
      Out.prln(doc.getAnnotations());
      Out.prln(doc.getContent());
    }
    AnnotationSet annots = doc.getAnnotations();
    assertTrue("no annotations from doc!", !annots.isEmpty());
    Annotation a = annots.get(new Integer(580));
    assertNotNull("couldn't get annot with id 580", a);
//sorry, this is no way to write a test!
//    assert( // check offset - two values depending on whether saved with \r\n
//      "wrong value: " + a.getStartNode().getOffset(),
//      (a.getStartNode().getOffset().equals(new Long(1360)) ||
//      a.getStartNode().getOffset().equals(new Long(1367)))
//    );
//    assert( // check offset - two values depending on whether saved with \r\n
//      "wrong value: " + a.getEndNode().getOffset(),
//      a.getEndNode().getOffset().equals(new Long(1361)) ||
//      a.getEndNode().getOffset().equals(new Long(1442))
//    );
  } // testSerial1()

  /** Serial controller test 2 */
  public void testSerial2() throws Exception {
    // a controller
    Controller c1 = new SerialController();
    assertNotNull("c1 controller is null", c1);
/*
    // a couple of PRs
    ResourceData pr1rd = (ResourceData) reg.get("testpkg.TestPR1");
    ResourceData pr2rd = (ResourceData) reg.get("testpkg.TestPR2");
    assert("couldn't find PR1/PR2 res data", pr1rd != null && pr2rd != null);
    assert("wrong name on PR1", pr1rd.getName().equals("Sheffield Test PR 1"));
    ProcessingResource pr1 = (ProcessingResource)
      Factory.createResource("testpkg.TestPR1", Factory.newFeatureMap());
    ProcessingResource pr2 = (ProcessingResource)
      Factory.createResource("testpkg.TestPR2", Factory.newFeatureMap());

    // add the PRs to the controller and run it
    c1.add(pr1);
    c1.add(pr2);
    c1.run();
*/
  } // testSerial2()

  /** Test suite routine for the test runner */
  public static Test suite() {
    return new TestSuite(TestControllers.class);
  } // suite

} // class TestControllers
