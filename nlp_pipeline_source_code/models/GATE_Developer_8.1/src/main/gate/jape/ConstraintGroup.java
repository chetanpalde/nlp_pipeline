/*
 *  ConstraintGroup.java - transducer class
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
 *  $Id: ConstraintGroup.java 17597 2014-03-08 15:19:43Z markagreenwood $
 */


package gate.jape;

import gate.util.Strings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;


/**
  * A sequence of conjunctions of PatternElements that form a
  * disjunction.
  */
public class ConstraintGroup
extends PatternElement implements JapeConstants, Serializable
{
  private static final long serialVersionUID = -4671488370557996554L;

  /** Anonymous constructor. */
  public ConstraintGroup() {
    patternElementDisjunction1 = new ArrayList<List<PatternElement>>();
    currentConjunction = new ArrayList<PatternElement>();
    patternElementDisjunction1.add(currentConjunction);
  } // Anonymous constructor

  /** Need cloning for processing of macro references. See comments on
    * <CODE>PatternElement.clone()</CODE>
    */
  @Override
  public Object clone() {
    ConstraintGroup newPE = (ConstraintGroup) super.clone();

    // created by createDisjunction
    newPE.currentConjunction = null;

    newPE.patternElementDisjunction1 = new ArrayList<List<PatternElement>>();
    // for each (conjunction) member of the pattern element discjunction
    for(
      Iterator<List<PatternElement>> disjunction = patternElementDisjunction1.iterator();
      disjunction.hasNext();

    ) {

      newPE.createDisjunction();
      // for each pattern element making up this conjunction
      for(
        Iterator<PatternElement> conjunction = disjunction.next().iterator();
        conjunction.hasNext();

      ) {
        PatternElement pat = conjunction.next();

        newPE.addPatternElement((PatternElement)pat.clone());
      } // for each element of the conjunction
    } // for each conjunction (element of the disjunction)

    return newPE;
  } // clone

  /** An array of arrays that represent PatternElement conjunctions
    * during parsing of the .jape. Each conjunction is
    * considered as being disjunct with the next. (I.e. they are
    * or'd, in the same way as expressions around "||" in C and
    * Java.) Set during parsing; replaced by finish().
    */
  private List<List<PatternElement>> patternElementDisjunction1;

  /** The pattern element disjunction for transduction - Java arrays. */
  private PatternElement[][] patternElementDisjunction2;

  /** An array of PatternElements making up a conjunction. It is a member of
    * patternElementDisjunction. This is the one we're adding to
    * at present. Used during parsing, not matching.
    */
  private List<PatternElement> currentConjunction;

  /** Make a new disjunction at this point. */
  public void createDisjunction() {
    currentConjunction = new ArrayList<PatternElement>();
    patternElementDisjunction1.add(currentConjunction);
  } // createDisjunction

  /** Add an element to the current conjunction. */
  public void addPatternElement(PatternElement pe) {
    currentConjunction.add(pe);
  } // addPatternElement

  /** Get an list of CPEs that we contain. */
  public Iterator<ComplexPatternElement> getCPEs() {
    List<ComplexPatternElement> cpes = new ArrayList<ComplexPatternElement>();

    // for each (conjunction) member of the pattern element discjunction
    for(
      Iterator<List<PatternElement>> disjunction = patternElementDisjunction1.iterator();
      disjunction.hasNext();
    ) {
      // for each pattern element making up this conjunction
      for(
        Iterator<PatternElement> conjunction = (disjunction.next()).iterator();
        conjunction.hasNext();
      ) {
        PatternElement pat = conjunction.next();

        Iterator<ComplexPatternElement> i = null;
        if(pat instanceof ComplexPatternElement) {
          cpes.add((ComplexPatternElement)pat);
          i = ((ComplexPatternElement)pat).getCPEs();
        }
        else if(pat instanceof ConstraintGroup)
          i = ((ConstraintGroup)pat).getCPEs();

        if(i != null)
          for( ; i.hasNext(); )
            cpes.add(i.next());
      } // for each element of the conjunction
    } // for each conjunction (element of the disjunction)

    return cpes.iterator();
  } // getCPEs

  /**
   * Populate the HashSet passed as a parameter with all the annotation
   * types that occur in this and recursively contained pattern elements.
   */
  public void getContainedAnnotationTypes(HashSet<String> set) {
    // for each (conjunction) member of the pattern element discjunction
    for(
      Iterator<List<PatternElement>> disjunction = patternElementDisjunction1.iterator();
      disjunction.hasNext();
      ) {
      // for each pattern element making up this conjunction
      for(
        Iterator<PatternElement> conjunction = disjunction.next().iterator();
        conjunction.hasNext();
      ) {
        PatternElement pat = conjunction.next();
        if(pat instanceof BasicPatternElement) {
          List<Constraint> constraints = 
            ((BasicPatternElement)pat).getUnfinishedConstraints();
          for (Constraint c : constraints) {
            set.add(c.getAnnotType());
          }
        } else if(pat instanceof ComplexPatternElement) {
          ((ComplexPatternElement)pat)
            .getConstraintGroup().getContainedAnnotationTypes(set);
        } else if(pat instanceof ConstraintGroup) {
          ((ConstraintGroup)pat)
            .getContainedAnnotationTypes(set);
        }
      } // for each pattern element making up this conjunction
    } // for each (conjunction) member of the pattern element discjunction
  } // method getContainedAnnotationTypes(String<String>)
  
  /** Finish: replace dynamic data structures with Java arrays; called
    * after parsing.
    */
  @Override
  public void finish() {

    // index into patternElementDisjunction2
    int i = 0;

    // index into the conjunctions (second dimension of pED2)
    int j = 0;

    patternElementDisjunction2 =
      new PatternElement[patternElementDisjunction1.size()][];

    // for each (conjunction) member of the pattern element discjunction
    for(
      Iterator<List<PatternElement>> disjuncIter = patternElementDisjunction1.iterator();
      disjuncIter.hasNext();
      i++
    ) {
      List<PatternElement> conjunction = disjuncIter.next();
      patternElementDisjunction2[i] = new PatternElement[conjunction.size()];
      j = 0;

      // for each pattern element making up this conjunction
      for(
        Iterator<PatternElement> conjIter = conjunction.iterator();
        conjIter.hasNext();
        j++
      ) {
        patternElementDisjunction2[i][j] = conjIter.next();
        patternElementDisjunction2[i][j].finish();
      } // loop on conjunction

    } // loop on patternElementDisjunction1

    patternElementDisjunction1 = null;
  } // finish



  /** Create a string representation of the object. */
  @Override
  public String toString() { return toString(""); }

  /** Create a string representation of the object. */
  @Override
  public String toString(String pad) {
    String newline = Strings.getNl();

    StringBuffer buf =
      new StringBuffer(pad + "CG: disjunction(" + newline);
    String newPad = Strings.addPadding(pad, INDENT_PADDING);

    boolean firstTime = true;

    if(patternElementDisjunction1 != null) { // before finish()
      // for each (conjunction) member of the pattern element discjunction
      for(
        Iterator<List<PatternElement>> disjunction = patternElementDisjunction1.iterator();
        disjunction.hasNext();
      ) {
        if(firstTime) firstTime = false;
        else buf.append(newline + pad + "|" + newline);

        // for each pattern element making up this conjunction
        for(
          Iterator<PatternElement> conjunction = disjunction.next().iterator();
          conjunction.hasNext();
        ) {
          buf.append(conjunction.next().toString(newPad) + newline
          );
        } // for each element of the conjunction
      } // for each conjunction (element of the disjunction)

    } else { // after finish
      int pEDLen = patternElementDisjunction2.length;
      if(firstTime) firstTime = false;
      else buf.append(newline + pad + "|" + newline);

      for(int i = 0; i < pEDLen; i++) {
        int conjLen = patternElementDisjunction2[i].length;
        // for each pattern element making up this conjunction
        for(int j = 0; j < conjLen; j++)
          buf.append(
            patternElementDisjunction2[i][j].toString(newPad) + newline
          );
      }
    }

    buf.append(pad + ") CG." + newline);

    return buf.toString();
  } // toString


  //needed by FSM
  public PatternElement[][] getPatternElementDisjunction(){
    return patternElementDisjunction2;
  }

} // class ConstraintGroup


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
// Revision 1.8  2001/09/13 12:09:50  kalina
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
// Revision 1.1  2000/02/23 13:46:06  hamish
// added
//
// Revision 1.1.1.1  1999/02/03 16:23:01  hamish
// added gate2
//
// Revision 1.17  1998/11/24 16:18:29  hamish
// fixed toString for calls after finish
//
// Revision 1.16  1998/11/01 21:21:36  hamish
// use Java arrays in transduction where possible
//
// Revision 1.15  1998/11/01 14:55:54  hamish
// fixed lFP setting in matches
//
// Revision 1.14  1998/10/30 14:06:45  hamish
// added getTransducer
//
// Revision 1.13  1998/10/29 12:07:49  hamish
// toString change
//
// Revision 1.12  1998/10/06 16:16:10  hamish
// negation percolation during constrain add; position advance when none at end
//
// Revision 1.11  1998/10/01 16:06:30  hamish
// new appelt transduction style, replacing buggy version
//
// Revision 1.10  1998/09/26 09:19:16  hamish
// added cloning of PE macros
//
// Revision 1.9  1998/09/17 16:48:31  hamish
// added macro defs and macro refs on LHS
//
// Revision 1.8  1998/08/12 19:05:43  hamish
// fixed multi-part CG bug; set reset to real reset and fixed multi-doc bug
//
// Revision 1.7  1998/08/12 15:39:35  hamish
// added padding toString methods
//
// Revision 1.6  1998/08/05 21:58:06  hamish
// backend works on simple test
//
// Revision 1.5  1998/08/03 19:51:20  hamish
// rollback added
//
// Revision 1.4  1998/07/31 13:12:16  hamish
// done RHS stuff, not tested
//
// Revision 1.3  1998/07/30 11:05:16  hamish
// more jape
//
// Revision 1.2  1998/07/29 11:06:56  hamish
// first compiling version
//
// Revision 1.1.1.1  1998/07/28 16:37:46  hamish
// gate2 lives
