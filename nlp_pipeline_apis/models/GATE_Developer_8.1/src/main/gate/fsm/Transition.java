/*
 *  Transition.java
 *
 *  Copyright (c) 1995-2012, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 *  Valentin Tablan, 11/Apr/2000
 *
 *  $Id: Transition.java 17595 2014-03-08 13:05:32Z markagreenwood $
 */

package gate.fsm;

import gate.Annotation;
import gate.jape.BasicPatternElement;
import gate.jape.Constraint;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
  * This class implements a Finite State Machine transition.
  * A transition is owned by a gate.fsm.State object and contains set of
  * restrictions and a reference to the next state that will be accessed after
  * consuming a set of input symbols according to the restrictions.
  * A transition can also hold information about the label that should be bound
  * to the symbols (annotations) consumed during the state transition.
  */
// >>> DAM
/*
public class Transition implements Serializable {
*/
// >>> DAM, TransArray optimzation, now implements the Comparable interface
public class Transition implements Serializable, Comparable<Transition> {

  private static final long serialVersionUID = 5970884178025357306L;

// >>> DAM, end

  /**
    * Default constructor. Creates a new transition with a new unique index.
    * This constructor should be called by all other constructors.
    */
  public Transition() {
    myIndex = Transition.index++;
  }

  /**
    * Creates a new transition using the given set of constraints and target
    * state.
    * @param constraints the set on constraints associated to this transition
    * @param state the target state of this transition
    */
  public Transition(BasicPatternElement constraints, State state) {
    this(constraints, state, new LinkedList<String>());
  }

  /**
    * Creates a new transition from a set of constraints, a target state and a
    * list of labels to be bound with the recognized input symbols
    * (aka annotations).
    */
  public Transition(BasicPatternElement constraints, State state,
                    List<String> bindings) {
    this();
    this.constraints = constraints;
    target = state;
    this.bindings = bindings;
  }

  /**
    * Creates a new transition to the given State with the same
    * bindings as this one.
    */
  public Transition spawn(State s)
  {
      return new Transition(constraints, s, bindings);
  }

  /**
    * Gets the target state of this transition
    * @return an object of type gate.fsm.State
    */
  public State getTarget(){ return target; }

  /**
    * Gets the constraints associated to this transition
    */
  public BasicPatternElement getConstraints(){ return constraints; }

  /**
    * Returns a boolean value indicating whether this Transition
    * has any constraints on it.
    */
  public boolean hasConstraints()
  {
      return constraints != null;
  }

  /**
    * Returns true if all the constraints on this transition are satisfied
    * by the given Annotations, false otherwise.  The given Annotations
    * should be the set of Annotations beginning at a single point in the
    * document.
    */
  public boolean satisfiedBy(Annotation[] coIncidentAnnos) {
      Constraint[] allConstraints = getConstraints().getConstraints();

      processAllConstraints:
      for (int i = 0; i < allConstraints.length; i++)
      {
          Constraint c = allConstraints[i];
          boolean negated = c.isNegated();

          for (int j = 0; j < coIncidentAnnos.length; j++)
          {
              if (c.matches(coIncidentAnnos[j], null))
              {
                  // One of these puppies being satisfied invalidates the whole transition
                  if (negated) return false;

                  // This constraint is satisfied, go on to the next one
                  continue processAllConstraints;
              }
          }

          // No matching annotations found for this constraint
          if (!negated) return false;
      }

      // All constraints satisfied
      return true;
  }

  /**
    * Returns a boolean value indicating whether this Transition
    * deals with multiple types of annotations.
    */
  public boolean isMultiType() {
      return constraints != null && constraints.isMultiType();
  }

  /**
    * Returns a textual desciption of this transition.
    * @return a String
    */
  @Override
  public String toString(){
    return toString(true);
  }

  public String toString(boolean includeTarget){
    StringBuffer toReturn = new StringBuffer();
    if (includeTarget) toReturn.append("If: ");
    toReturn.append(constraints);
    if (includeTarget) toReturn.append(" then ->: " + target.getIndex());
    return toReturn.toString();
  }

  /**
    * Returns a shorter description that toSting().
    * Actually, it returns the unique index in String form.
    */
  public String shortDesc(){
    String res = "" + myIndex;
    return res;
  }

  /**
    *  Returns the list of bindings associated to this transition
    */
  public List<String> getBindings(){ return bindings; }

  /**
    * The constraints on this transition.
    */
  private BasicPatternElement constraints;

  /**
    * The state this transition leads to
    */
  private State target;

  /**
    * A list with all the labels associated to the annotations recognized by
    * this transition.
    */
  private List<String> bindings;

  /** The unique index of this transition. This value is not used by any of
    * the algorithms. It is only provided as a convenient method of identifying
    * the transitions in textual representations (toString() and GML related
    * methods)
    */
  private int myIndex;

  /** Static member used for generating unique IDs for the objects of type
    * Transition*/
  private static int index = 0;

// >>> DAM, TransArray optimzation, now implements the Comparable interface
  @Override
  public int compareTo(Transition t)
  throws ClassCastException
  {
    return myIndex - t.myIndex;
  }
// >>> DAM, end
} // Transition
