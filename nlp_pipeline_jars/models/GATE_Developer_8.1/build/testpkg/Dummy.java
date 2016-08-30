/*
	Dummy.java

	Hamish Cunningham, 13/06/00

	$Id: Dummy.java 385 2000-06-23 16:22:30Z hamish $
*/


package testpkg;

import java.io.*;

/** A dummy class, used for testing reloading of classes in 
  * TestJDK.
  */
public class Dummy
{
  public static int i = 0;

  static {
    // System.out.println("initialising dummy class, i = " + i++);
  }

} // class Dummy

