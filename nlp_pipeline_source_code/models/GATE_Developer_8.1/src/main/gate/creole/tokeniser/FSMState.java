/*
 *  FSMState.java
 *
 *  Copyright (c) 1995-2012, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 *  Valentin Tablan, 27/06/2000
 *
 *  $Id: FSMState.java 17871 2014-04-18 10:38:57Z markagreenwood $
 */

package gate.creole.tokeniser;

import java.util.*;

  /** A state of the finite state machine that is the kernel tokeniser
    */
class FSMState implements java.io.Serializable {

  private static final long serialVersionUID = -8044319707799787043L;

  /** Creates a new FSMState belonging to a specified tokeniser
    * @param owner the tokeniser that contains this new state
    */
  public FSMState(SimpleTokeniser owner) {
    myIndex = index++;
    owner.fsmStates.add(this);
  }

  /** Returns the value of the transition function of this state for a given
    * Unicode type.
    * As this state can belong to a non-deterministic automaton, the result
    * will be a set.
    */
  Set<FSMState> nextSet(UnicodeType type) {
    if(null == type) return transitionFunction[SimpleTokeniser.maxTypeId];
    else return transitionFunction[type.type];
  } // nextSet(UnicodeType type)

  /** Returns the value of the transition function of this state for a given
    * Unicode type specified using the internal ids used by the tokeniser.
    * As this state can belong to a non-deterministic automaton, the result
    * will be a set.
    */
  Set<FSMState> nextSet(int type) {
    return transitionFunction[type];
  } // nextSet(int type)

  /** Adds a new transition to the transition function of this state
    * @param type the restriction for the new transition; if <code>null</code>
    * this transition will be unrestricted.
    * @param state the vaule of the transition function for the given type
    */
  void put(UnicodeType type, FSMState state) {
    if(null == type) put(SimpleTokeniser.maxTypeId, state);
    else put(type.type, state);
  } // put(UnicodeType type, FSMState state)

  /** Adds a new transition to the transition function of this state
    * @param index the internal index of the Unicode type representing the
    * restriction for the new transition;
    * @param state the vaule of the transition function for the given type
    */
  void put(int index, FSMState state) {
    if(null == transitionFunction[index])
      transitionFunction[index] = new HashSet<FSMState>();
    transitionFunction[index].add(state);
  } // put(int index, FSMState state)

  /** Sets the RHS string value */
  void setRhs(String rhs) { this.rhs = rhs; }

  /** Gets the RHS string value */
  String getRhs() { return rhs; }

  /** Checks whether this state is a final one */
  boolean isFinal() { return (null != rhs); }

  /** Gets the unique id of this state */
  int getIndex() { return myIndex; }

  /** Returns a GML representation of all the edges emerging
    * from this state */
  String getEdgesGML() {
///    String res = "";
    StringBuffer res = new StringBuffer(gate.Gate.STRINGBUFFER_SIZE);
    Set<FSMState> nextSet;
    Iterator<FSMState> nextSetIter;
    FSMState nextState;

    for(int i = 0; i <= SimpleTokeniser.maxTypeId; i++){
      nextSet = transitionFunction[i];
      if(null != nextSet){
        nextSetIter = nextSet.iterator();
        while(nextSetIter.hasNext()){
          nextState = nextSetIter.next();
/*          res += "edge [ source " + myIndex +
          " target " + nextState.getIndex() +
          " label \"";
*/
            res.append("edge [ source ");
            res.append(myIndex);
            res.append(" target ");
            res.append(nextState.getIndex());
            res.append(" label \"");

          if(i == SimpleTokeniser.maxTypeId) ///res += "[]";
                res.append("[]");
          else ///res += SimpleTokeniser.typeMnemonics[i];
                res.append(SimpleTokeniser.typeMnemonics[i]);

          ///res += "\" ]\n";
          res.append("\" ]\n");
        }//while(nextSetIter.hasNext())
      }
    };
    return res.toString();
  } // getIndex

  /** The transition function of this state. It's an array mapping from int
    * (the ids used internally by the tokeniser for the Unicode types) to sets
    * of states.
    */
  @SuppressWarnings({"unchecked","rawtypes"})
  Set<FSMState>[] transitionFunction = new Set[SimpleTokeniser.maxTypeId + 1];

  /** The RHS string value from which the annotation associated to
    * final states is constructed.
    */
  String rhs;

  /**the unique index of this state*/
  int myIndex;

  /**used for generating unique ids*/
  static int index;

  static{
    index = 0;
  }

} // class FSMState
