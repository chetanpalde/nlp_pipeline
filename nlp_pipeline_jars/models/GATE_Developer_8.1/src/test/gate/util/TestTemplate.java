/*
 *  TestTemplate.java
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
 *  $Id: TestTemplate.java 17656 2014-03-14 08:55:23Z markagreenwood $
 */

package gate.util;

import junit.framework.*;

/** Template test class - to add a new part of the test suite:
  * <UL>
  * <LI>
  * copy this class and change "Template" to the name of the new tests;
  * <LI>
  * add a line to TestGate.java in the suite method referencing your new
  * class;
  * <LI>
  * add test methods to this class.
  * </UL>
  */
public class TestTemplate extends TestCase
{
  /** Construction */
  public TestTemplate(String name) { super(name); }

  /** Fixture set up */
  @Override
  public void setUp() throws Exception {
  } // setUp

  /** Put things back as they should be after running tests.
    */
  @Override
  public void tearDown() throws Exception {
  } // tearDown

  /** A test */
  public void testSomething() throws Exception {
    assertTrue(true);
  } // testSomething()

  /** Test suite routine for the test runner */
  public static Test suite() {
    return new TestSuite(TestTemplate.class);
  } // suite

} // class TestTemplate
