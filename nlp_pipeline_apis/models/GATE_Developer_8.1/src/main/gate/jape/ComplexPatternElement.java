/*
 *  ComplexPatternElement.java - transducer class
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
 *  $Id: ComplexPatternElement.java 17597 2014-03-08 15:19:43Z markagreenwood $
 */


package gate.jape;

import gate.util.Strings;

import java.io.Serializable;
import java.util.Iterator;


/**
  * A pattern element enclosed in round brackets. Has a
  * ConstraintGroup, optional Kleene operator and optional binding name.
  */
public class ComplexPatternElement extends PatternElement
implements JapeConstants, Serializable
{
  private static final long serialVersionUID = -1660335210282070151L;

  /** Kleene operator (defaults to none). Other values: KLEENE_STAR (*);
    * KLEENE_PLUS (+); KLEENE_QUERY (?) */
  private KleeneOperator kleeneOp = null;

  /** Binding name (may be null). */
  private String bindingName = null;

  /** Get binding name. */
  public String getBindingName() { return bindingName; }

  /** Get a list of CPEs that we contain. */
  protected Iterator<ComplexPatternElement> getCPEs() {
    return constraintGroup.getCPEs();
  } // getCPEs

  /** The recursive definition of what pattern elements make up this one. */
  private ConstraintGroup constraintGroup;

  public ComplexPatternElement(
    ConstraintGroup constraintGroup,
    KleeneOperator.Type kleeneType,
    String bindingName
  ) {
    this(constraintGroup, new KleeneOperator(kleeneType), bindingName);
  }

  public ComplexPatternElement(
          ConstraintGroup constraintGroup,
          KleeneOperator kleeneOp,
          String bindingName
        ) {
          if (kleeneOp == null)
            kleeneOp = new KleeneOperator(KleeneOperator.Type.SINGLE);
          this.constraintGroup = constraintGroup;
          this.kleeneOp = kleeneOp;
          this.bindingName = bindingName;
        }

  /** Construction from ConstraintGroup, min and max legal occurance limits,
   * and binding name.
   */
  public ComplexPatternElement(
    ConstraintGroup constraintGroup,
    int minOccurance, int maxOccurance,
    String bindingName
  ) {
    this.constraintGroup = constraintGroup;
    this.kleeneOp = new KleeneOperator(minOccurance, maxOccurance);
    this.bindingName = bindingName;
  }

  /** Need cloning for processing of macro references. See comments on
    * <CODE>PatternElement.clone()</CODE>
    */
  @Override
  public Object clone() {
    ComplexPatternElement newPE = (ComplexPatternElement) super.clone();
    newPE.constraintGroup = (ConstraintGroup) constraintGroup.clone();
    return newPE;
  } // clone

  /** Finish: replace dynamic data structures with Java arrays; called
    * after parsing.
    */
  @Override
  public void finish() {
    constraintGroup.finish();
  } // finish

  /** Create a string representation of the object. */
  @Override
  public String toString() { return toString(""); }

  /** Create a string representation of the object. */
  @Override
  public String toString(String pad) {
    String newline = Strings.getNl();

    StringBuffer buf = new StringBuffer(
      pad + "CPE: bindingName(" + bindingName + "); kleeneOp("
    );

    if (kleeneOp != null)
      buf.append(kleeneOp);

    buf.append(
      "); constraintGroup(" + newline +
      constraintGroup.toString(Strings.addPadding(pad, INDENT_PADDING)) +
      newline + pad + ") CPE." + newline
    );

    return buf.toString();
  } // toString
  //needed by FSM

  public KleeneOperator getKleeneOp(){ return kleeneOp; };

  public ConstraintGroup getConstraintGroup(){ return constraintGroup; }

} // class ComplexPatternElement


// $Log$
// Revision 1.11  2005/01/11 13:51:36  ian
// Updating copyrights to 1998-2005 in preparation for v3.0
//
// Revision 1.10  2004/07/21 17:10:07  akshay
// Changed copyright from 1998-2001 to 1998-2004
//
// Revision 1.9  2004/03/25 13:01:15  valyt
// Imports optimisation throughout the Java sources
// (to get rid of annoying warnings in Eclipse)
//
// Revision 1.8  2001/09/13 12:09:49  kalina
// Removed completely the use of jgl.objectspace.Array and such.
// Instead all sources now use the new Collections, typically ArrayList.
// I ran the tests and I ran some documents and compared with keys.
// JAPE seems to work well (that's where it all was). If there are problems
// maybe look at those new structures first.
//
// Revision 1.7  2001/09/12 11:59:33  kalina
// Changed the old JAPE stuff to use the new Collections API,
// instead of com.objectspace stuff. Will eliminate that library
// completely very soon! Just one class left to re-implement,
//
// ParseCPSL.jj changed accordingly. All tested and no smoke.
//
// Revision 1.6  2000/11/08 16:35:02  hamish
// formatting
//
// Revision 1.5  2000/10/26 10:45:30  oana
// Modified in the code style
//
// Revision 1.4  2000/10/16 16:44:33  oana
// Changed the comment of DEBUG variable
//
// Revision 1.3  2000/10/10 15:36:35  oana
// Changed System.out in Out and System.err in Err;
// Added the DEBUG variable seted on false;
// Added in the header the licence;
//
// Revision 1.2  2000/04/14 18:02:46  valyt
// Added some gate.fsm classes
// added some accessor function in old jape classes
//
// Revision 1.1  2000/02/23 13:46:05  hamish
// added
//
// Revision 1.1.1.1  1999/02/03 16:23:01  hamish
// added gate2
//
// Revision 1.14  1998/11/13 13:17:16  hamish
// merged in the doc length bug fix
//
// Revision 1.13  1998/11/12 17:47:27  kalina
// A bug fixed, wasn't restoring the document length
//
// Revision 1.12  1998/11/05 13:36:29  kalina
// moved to use array of JdmAttributes for selectNextAnnotation instead
// of a sequence
//
// Revision 1.11  1998/11/01 21:21:35  hamish
// use Java arrays in transduction where possible
//
// Revision 1.10  1998/10/06 16:16:09  hamish
// negation percolation during constrain add; position advance when none at end
//
// Revision 1.9  1998/10/01 16:06:29  hamish
// new appelt transduction style, replacing buggy version
//
// Revision 1.8  1998/09/26 09:19:14  hamish
// added cloning of PE macros
//
// Revision 1.7  1998/09/17 16:48:29  hamish
// added macro defs and macro refs on LHS
//
// Revision 1.6  1998/08/12 15:39:32  hamish
// added padding toString methods
//
// Revision 1.5  1998/08/05 21:58:04  hamish
// backend works on simple test
//
// Revision 1.4  1998/08/03 19:51:19  hamish
// rollback added
//
// Revision 1.3  1998/07/30 11:05:14  hamish
// more jape
//
// Revision 1.2  1998/07/29 11:06:54  hamish
// first compiling version
//
// Revision 1.1.1.1  1998/07/28 16:37:46  hamish
// gate2 lives
