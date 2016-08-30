/*
 *  TestXSchema.java
 *
 *  Copyright (c) 1995-2012, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 *  Cristian URSU, 11/Octomber/2000
 *
 *  $Id: TestXSchema.java 17656 2014-03-14 08:55:23Z markagreenwood $
 */

package gate.creole;

import gate.Factory;
import gate.FeatureMap;
import gate.Gate;
import gate.corpora.TestDocument;
import gate.util.Out;

import java.io.ByteArrayInputStream;
import java.net.URL;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/** Annotation schemas test class.
  */
public class TestXSchema extends TestCase
{
  /** Debug flag */
  private static final boolean DEBUG = false;

  /** Construction */
  public TestXSchema(String name) { super(name); }

  /** Fixture set up */
  @Override
  public void setUp() {
  } // setUp

  /** A test */
  public void testFromAndToXSchema() throws Exception {

    //esourceData resData = Gate.getCreoleRegister().get("gate.creole.AnnotationSchema");

    FeatureMap parameters = Factory.newFeatureMap();
    parameters.put(
      AnnotationSchema.FILE_URL_PARAM_NAME, new URL(TestDocument.getTestServerName()+"tests/xml/POSSchema.xml"));

    AnnotationSchema annotSchema = (AnnotationSchema)
      Factory.createResource("gate.creole.AnnotationSchema", parameters);

    String s = annotSchema.toXSchema();
    // write back the XSchema fom memory
    // File file = Files.writeTempFile(new ByteArrayInputStream(s.getBytes()));
    // load it again.
    //annotSchema.fromXSchema(file.toURI().toURL());
    annotSchema.fromXSchema(new ByteArrayInputStream(s.getBytes()));
  } // testFromAndToXSchema()

  /** Test creation of annotation schemas via gate.Factory */
  public void testFactoryCreation() throws Exception {

    ResourceData resData = Gate.getCreoleRegister().get("gate.creole.AnnotationSchema");

    FeatureMap parameters = Factory.newFeatureMap();
    parameters.put(
      AnnotationSchema.FILE_URL_PARAM_NAME, new URL(TestDocument.getTestServerName()+"tests/xml/POSSchema.xml"));

    AnnotationSchema schema = (AnnotationSchema)
      Factory.createResource("gate.creole.AnnotationSchema", parameters);

    if(DEBUG) {
      Out.prln("schema RD: " + resData);
      Out.prln("schema: " + schema);
    }

  } // testFactoryCreation()

  /** Test suite routine for the test runner */
  public static Test suite() {
    return new TestSuite(TestXSchema.class);
  } // suite

} // class TestXSchema
