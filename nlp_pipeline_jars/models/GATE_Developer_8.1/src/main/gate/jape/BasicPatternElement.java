/*
 *  BasicPatternElement.java - transducer class
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
 *  $Id: BasicPatternElement.java 17597 2014-03-08 15:19:43Z markagreenwood $
 */


package gate.jape;

import gate.util.Pair;
import gate.util.Strings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
  * A pattern element within curly braces. Has a set of Constraint,
  * which all must be satisfied at whatever position the element is being
  * matched at.
  */
public class BasicPatternElement
extends PatternElement implements JapeConstants, Serializable
{
  private static final long serialVersionUID = -6515011025898779462L;

  /** A set of Constraint. Used during parsing. */
  private List<Constraint> constraints1;

  /** A set of Constraint. Used during matching. */
  private Constraint[] constraints2;

  /** A map of constraint annot type to constraint. Used during parsing. */
  private Map<Object, Constraint> constraintsMap;

  /** Cache of the last position we failed at (-1 when none). */
  private int lastFailurePoint = -1;

  /** The position of the next available annotation of the type required
    * by the first constraint.
    */
  //private MutableInteger nextAvailable = new MutableInteger();

  private final boolean negationCompatMode;
  
  /** Construction. */
  public BasicPatternElement(SinglePhaseTransducer spt) {
    constraintsMap = new HashMap<Object, Constraint>();
    constraints1 = new ArrayList<Constraint>();
    lastFailurePoint = -1;
    negationCompatMode = spt.isNegationCompatMode();
  } // construction

  /** Need cloning for processing of macro references. See comments on
    * <CODE>PatternElement.clone()</CODE>
    */
  @Override
  public Object clone() {
    BasicPatternElement newPE = (BasicPatternElement) super.clone();
    newPE.constraintsMap = new HashMap<Object, Constraint>(constraintsMap);
    newPE.constraints1 = new ArrayList<Constraint>();
    int consLen = constraints1.size();
    for(int i = 0; i < consLen; i++)
      newPE.constraints1.add(
        (Constraint)constraints1.get(i).clone()
      );
//    newPE.matchedAnnots = new AnnotationSetImpl((Document) null);
//    newPE.matchedAnnots.addAll(matchedAnnots);
    return newPE;
  } // clone

  /** Add a constraint. Ensures that only one constraint of any given
    * annotation type and negation state exists.
    */
  public void addConstraint(Constraint newConstraint) {
    // find if we need to merge this new constraint with an existing one
    Constraint existingConstraint = null;
    String annotType = newConstraint.getAnnotType();
    Pair typeNegKey = new Pair(annotType, newConstraint.isNegated());
    if(negationCompatMode && newConstraint.isNegated()) {
      // compatibility mode (pre GATE 7.0) where multiple negative constraints
      // on the same annotation type do NOT get grouped together and AND'ed 
      // before the negation is applied
      existingConstraint = null;
    } else {
      // positive constraint OR negated, but in default mode:
      // if a constraint with the same negation state as this constraint is
      // already mapped, put its attributes on the existing constraint, else
      // add it
      existingConstraint = constraintsMap.get(typeNegKey);
    }      

    if(existingConstraint != null) {
      existingConstraint.addAttributes(newConstraint.getAttributeSeq());
    } else {
      constraintsMap.put(typeNegKey, newConstraint);
      constraints1.add(newConstraint);      
    }
  } // addConstraint


  /**
   * Indicates whether this constraint deals with only one type of annotation or
   * multiple types.
   */
  public boolean isMultiType() {
      return constraints2 != null ? constraints2.length > 1 :
             constraints1 != null ? constraints1.size() > 1 :
             false;
  }

  /** Finish: replace dynamic data structures with Java arrays; called
    * after parsing.
    */
  @Override
  public void finish() {
    int j=0;
    constraints2 = new Constraint[constraints1.size()];
    for(Constraint c : constraints1 ) {
      constraints2[j] = c;
      constraints2[j++].finish();
    }
    constraints1 = null;
  } // finish



  /** Create a string representation of the object. */
  @Override
  public String toString() {
    StringBuffer result = new StringBuffer("{");
    Constraint[] constraints = getConstraints();
    for(int i = 0; i<constraints.length; i++){
      result.append(constraints[i].shortDesc() + ",");
    }
    result.setCharAt(result.length() -1, '}');
    return result.toString();
  }

  /** Create a string representation of the object. */
  @Override
  public String toString(String pad) {
    String newline = Strings.getNl();
    String newPad = Strings.addPadding(pad, INDENT_PADDING);

    StringBuffer buf = new StringBuffer(pad +
      "BPE: lastFailurePoint(" + lastFailurePoint + "); constraints("
    );

    // constraints
    if(constraints1 != null) {
      for(int len = constraints1.size(), i = 0; i < len; i++)
        buf.append(
          newline + constraints1.get(i).getDisplayString(newPad)
        );
    } else {
      for(int len = constraints2.length, i = 0; i < len; i++)
        buf.append(newline + constraints2[i].getDisplayString(newPad));
    }

    // matched annots
//    buf.append(
//      newline + pad + "matchedAnnots: " + matchedAnnots +
//      newline + pad + ") BPE."
//    );

    return buf.toString();
  } // toString

  /**
    * Returns a short description.
    */
  public String shortDesc() {
    String res = "";
    if(constraints1 != null) {
      for(int len = constraints1.size(), i = 0; i < len; i++)
        res += constraints1.get(i).toString();
    } else {
      for(int len = constraints2.length, i = 0; i < len; i++)
        res += constraints2[i].shortDesc();
    }
    return res;
  }

  /**
   * Get the current list of unfinished Constraint objects. This
   * can only be used before the finish() method is used.
   * @return the array list of constraint objects. Will be null after
   * the finish() method has been used.
   */
  public List<Constraint> getUnfinishedConstraints() {
    return constraints1;
  }

  /**
   * Get the finished Constraint objects. Can only be used after the
   * finish() method has been used.
   * @return an array of constraint objects. Will be null before the
   * finish() method has been used.
   */
  public Constraint[] getConstraints(){
    return constraints2;
  }
} // class BasicPatternElement

