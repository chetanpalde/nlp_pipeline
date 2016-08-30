/*
 *  LeftHandSide.java - transducer class
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
 *  $Id: LeftHandSide.java 17597 2014-03-08 15:19:43Z markagreenwood $
 */


package gate.jape;

import java.io.Serializable;
import java.util.*;

import gate.util.Strings;


/**
  * The LHS of a CPSL rule. The pattern part. Has a ConstraintGroup and
  * binding information that associates labels with ComplexPatternElements.
  * Provides the Matcher interface.
  */
public class LeftHandSide implements JapeConstants, Serializable
{
  private static final long serialVersionUID = -188265607327929863L;

  /** The constraint group making up this LHS. */
  private ConstraintGroup constraintGroup;

  /** Mapping of binding names to ComplexPatternElements */
  private Map<String, ComplexPatternElement> bindingTable;

  /** Construction from a ConstraintGroup */
  public LeftHandSide(ConstraintGroup constraintGroup) {
    this.constraintGroup = constraintGroup;
    bindingTable = new HashMap<String,ComplexPatternElement>();
  } // construction from ConstraintGroup

  /** Add a binding record. */
  public void addBinding(
    String bindingName,
    ComplexPatternElement binding,
    Set<String> bindingNameSet
  ) throws JapeException {
    if(bindingTable.get(bindingName) != null)
      throw new JapeException(
        "LeftHandSide.addBinding: " + bindingName + " already bound"
      );
    bindingTable.put(bindingName, binding);
    bindingNameSet.add(bindingName);
  } // addBinding

  /** Finish: replace dynamic data structures with Java arrays; called
    * after parsing.
    */
  public void finish() {
    constraintGroup.finish();
  } // finish

  /** Create a string representation of the object. */
  @Override
  public String toString() { return toString(""); }

  /** Create a string representation of the object. */
  public String toString(String pad) {
    String newline = Strings.getNl();
    String newPad = Strings.addPadding(pad, INDENT_PADDING);

    StringBuffer buf = new StringBuffer(pad +
      /*"LHS: hasMatched(" + hasMatched + ")*/ 
      "; constraintGroup(" + newline +
      constraintGroup.toString(newPad) + newline + pad +
      "); bindingTable(" + newline + pad
    );

    for(Iterator<String> i = bindingTable.keySet().iterator(); i.hasNext(); ) {
      String bName = i.next();
      ComplexPatternElement cpe = (bindingTable.get(bName));
      buf.append(
        pad + "bT.bn(" + bName + "), cpe.bn(" + cpe.getBindingName() + ")"
      );
    }

    buf.append(newline + pad + ") LHS." + newline);

    return buf.toString();
  } // toString

  /** Get the constraint group */
  public ConstraintGroup getConstraintGroup(){
    return constraintGroup;
  }

} // class LeftHandSide


// $Log$
// Revision 1.10  2005/01/11 13:51:36  ian
// Updating copyrights to 1998-2005 in preparation for v3.0
//
// Revision 1.9  2004/07/21 17:10:08  akshay
// Changed copyright from 1998-2001 to 1998-2004
//
// Revision 1.8  2004/03/25 13:01:13  valyt
// Imports optimisation throughout the Java sources
// (to get rid of annoying warnings in Eclipse)
//
// Revision 1.7  2001/09/12 11:59:33  kalina
// Changed the old JAPE stuff to use the new Collections API,
// instead of com.objectspace stuff. Will eliminate that library
// completely very soon! Just one class left to re-implement,
//
// ParseCPSL.jj changed accordingly. All tested and no smoke.
//
// Revision 1.6  2000/11/08 16:35:03  hamish
// formatting
//
// Revision 1.5  2000/10/16 16:44:33  oana
// Changed the comment of DEBUG variable
//
// Revision 1.4  2000/10/10 15:36:36  oana
// Changed System.out in Out and System.err in Err;
// Added the DEBUG variable seted on false;
// Added in the header the licence;
//
// Revision 1.3  2000/05/02 16:54:26  hamish
// comment
//
// Revision 1.2  2000/04/14 18:02:46  valyt
// Added some gate.fsm classes
// added some accessor function in old jape classes
//
// Revision 1.1  2000/02/23 13:46:08  hamish
// added
//
// Revision 1.1.1.1  1999/02/03 16:23:01  hamish
// added gate2
//
// Revision 1.14  1998/11/01 21:21:37  hamish
// use Java arrays in transduction where possible
//
// Revision 1.13  1998/10/30 15:31:07  kalina
// Made small changes to make compile under 1.2 and 1.1.x
//
// Revision 1.12  1998/10/01 16:06:32  hamish
// new appelt transduction style, replacing buggy version
//
// Revision 1.11  1998/09/21 16:19:49  hamish
// cope with CPEs with no binding
//
// Revision 1.10  1998/09/17 16:48:32  hamish
// added macro defs and macro refs on LHS
//
// Revision 1.9  1998/08/19 20:21:39  hamish
// new RHS assignment expression stuff added
//
// Revision 1.8  1998/08/18 12:43:07  hamish
// fixed SPT bug, not advancing newPosition
//
// Revision 1.7  1998/08/12 19:05:45  hamish
// fixed multi-part CG bug; set reset to real reset and fixed multi-doc bug
//
// Revision 1.6  1998/08/12 15:39:37  hamish
// added padding toString methods
//
// Revision 1.5  1998/08/03 19:51:22  hamish
// rollback added
//
// Revision 1.4  1998/07/31 13:12:20  mks
// done RHS stuff, not tested
//
// Revision 1.3  1998/07/30 11:05:19  mks
// more jape
//
// Revision 1.2  1998/07/29 11:06:59  hamish
// first compiling version
//
// Revision 1.1.1.1  1998/07/28 16:37:46  hamish
// gate2 lives
