/*
 *
 *  Copyright (c) 1995-2012, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 *  Valentin Tablan, 26/Feb/2002
 *
 *  $Id: TestJavac.java 17656 2014-03-14 08:55:23Z markagreenwood $
 */

package gate.util;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import junit.framework.*;

import gate.Gate;

public class TestJavac extends TestCase{
  /** Construction */
  public TestJavac(String name) { super(name); }

 /** Fixture set up */
  @Override
  public void setUp() {
  } // setUp

  /** Test suite routine for the test runner */
  public static Test suite() {
    return new TestSuite(TestJavac.class);
  } // suite

 /** Jdk compiler */
  public void testCompiler() throws Exception {

    String nl = Strings.getNl();
    String javaSource =
      "package foo.bar;" + nl +
      "public class Outer {" + nl +
      "//let's make an inner class " + nl +
      " class Adder{" + nl +
      " public int inc(int i){" + nl +
      "   return i + 1;" + nl +
      " }//inc(int)" + nl +
      " }//class Adder" + nl +
      " //let's make another inner class" + nl +
      " class Deccer{" + nl +
      " public int dec(int i){" + nl +
      "   return i - 1;" + nl +
      " }//dec(int)" + nl +
      " }//clas Deccer" + nl +
      " //some public methods" + nl +
      " public int inc(int i){" + nl +
      "   return new Adder().inc(i);" + nl +
      " }" + nl +
      " public int dec(int i){" + nl +
      "   return new Deccer().dec(i);" + nl +
      " }" + nl +
      " }//class Outer" + nl;

      //load the class
      Map<String,String> sources = new HashMap<String,String>();
      sources.put("foo.bar.Outer", javaSource);
      Javac.loadClasses(sources, Gate.getClassLoader());
      //try to access the class
      Class<?> testClass = Gate.getClassLoader().loadClass("foo.bar.Outer");
      assertNotNull("Could not find decalred class", testClass);
      Object testInstance = testClass.newInstance();
      assertNotNull("Could not instantiate declared class", testInstance);
      Method testMethod =  testClass.getDeclaredMethod(
                                          "inc",
                                          new Class[]{int.class});
      assertNotNull("Could not find declared method", testMethod);
      Object result = testMethod.invoke(testInstance,
                                        new Object[]{new Integer(1)});
      assertEquals("Invalid result", result, new Integer(2));

      testMethod =  testClass.getDeclaredMethod(
                                          "dec",
                                          new Class[]{int.class});
      assertNotNull("Could not find declared method", testMethod);
      result = testMethod.invoke(testInstance, new Object[]{new Integer(2)});
      assertEquals("Invalid result", result, new Integer(1));
  }

  public void testCompileError() throws Exception {
    System.err.println("Testing for a compile error:");
    String nl = Strings.getNl();
    String javaSource =
        "package foo.bar;" + nl +
        "public class X {" + nl +
        " //some public methods" + nl +
        " public void foo(){" + nl +
        " String nullStr = null;" + nl +
        "// This should cause a compile error:" + nl +
        " nullStr = 123;" + nl +
        "} " + nl +
        " " + nl +
        " " + nl +
        " }//class Outer" + nl;

    //load the class
    Map<String,String> sources = new HashMap<String,String>();
    sources.put("foo.bar.X", javaSource);
    boolean gotException = false;
    try {
      Javac.loadClasses(sources,Gate.getClassLoader());
    }
    catch (GateException ge) {
      gotException = true;
    }
    finally {
//      newSyserr.flush();
//      // re-enable System.out
//      System.setErr(syserr);
//      newSyserr.close();
    }
    assertTrue("Garbage java code did not raise an exception!",
               gotException);
  }
}