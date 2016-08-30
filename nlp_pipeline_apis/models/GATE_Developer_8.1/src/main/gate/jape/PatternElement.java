/* 
 *  PatternElement.java - transducer class
 *
 *  Copyright (c) 1995-2012, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 *  Hamish Cunningham, 24/07/98
 *
 *  $Id: PatternElement.java 17599 2014-03-08 16:30:42Z markagreenwood $
 */


package gate.jape;

import java.io.Serializable;
import java.util.Stack;


/**
  * Superclass of the various types of pattern element, and of
  * ConstraintGroup. Inherits from Matcher, providing matches and reset.
  * Provides access to the annotations that are cached by subclasses, and
  * multilevel rollback of those caches. Stores the match history.
  */
abstract public class PatternElement implements Cloneable, 
		      JapeConstants, Serializable
{
  private static final long serialVersionUID = -3721781489731606247L;

  /** Match history stack, for use in rollback. In BasicPatternElements
    * the objects on the stack are Integers giving the number of annots that
    * were cached at that point in the history. In ComplexPatternElements
    * the objects are Integers giving the number of times the component
    * ConstraintGroup was successfully matched. In ConstraintGroups the
    * elements are arrays representing conjunctions of PatternElement that
    * succeeded at that point in the history.
    */
  protected Stack<Object> matchHistory;

  /** Anonymous construction. */
  public PatternElement() {
    matchHistory = new Stack<Object>();
  } // Anonymous constructor.

  /** Cloning for processing of macro references. Note that it doesn't
    * really clone the match history, just set it to a new Stack. This is
    * because a) JGL doesn't have real clone methods and b) we don't
    * actually need it anywhere but during parsing the .jape, where there
    * is no match history yet.
    */
  @Override
  public Object clone() {
    try {
      PatternElement newPE = (PatternElement) super.clone();
      newPE.matchHistory = new Stack<Object>();
      return newPE;
    } catch(CloneNotSupportedException e) {
      throw(new InternalError(e.toString()));
    }
  } // clone


  /**
   * Finishes the JAPE language parsing, building all the in-memory structures 
   * required. 
   */
  public abstract void finish();
  /** Create a string representation of the object with padding. */
  abstract public String toString(String pad);

} // class PatternElement


// $Log$
// Revision 1.7  2005/01/11 13:51:36  ian
// Updating copyrights to 1998-2005 in preparation for v3.0
//
// Revision 1.6  2004/07/21 17:10:08  akshay
// Changed copyright from 1998-2001 to 1998-2004
//
// Revision 1.5  2004/03/25 13:01:14  valyt
// Imports optimisation throughout the Java sources
// (to get rid of annoying warnings in Eclipse)
//
// Revision 1.4  2000/11/08 16:35:03  hamish
// formatting
//
// Revision 1.3  2000/10/16 16:44:34  oana
// Changed the comment of DEBUG variable
//
// Revision 1.2  2000/10/10 15:36:36  oana
// Changed System.out in Out and System.err in Err;
// Added the DEBUG variable seted on false;
// Added in the header the licence;
//
// Revision 1.1  2000/02/23 13:46:09  hamish
// added
//
// Revision 1.1.1.1  1999/02/03 16:23:02  hamish
// added gate2
//
// Revision 1.8  1998/11/03 19:06:49  hamish
// java stack, not jgl stack for matchHistory
//
// Revision 1.7  1998/11/01 23:18:44  hamish
// use new instead of clear on containers
//
// Revision 1.6  1998/09/26 09:19:18  hamish
// added cloning of PE macros
//
// Revision 1.5  1998/08/12 15:39:41  hamish
// added padding toString methods
//
// Revision 1.4  1998/08/03 19:51:24  hamish
// rollback added
//
// Revision 1.3  1998/07/30 11:05:22  hamish
// more jape
//
// Revision 1.2  1998/07/29 11:07:06  hamish
// first compiling version
//
// Revision 1.1.1.1  1998/07/28 16:37:46  hamish
// gate2 lives
