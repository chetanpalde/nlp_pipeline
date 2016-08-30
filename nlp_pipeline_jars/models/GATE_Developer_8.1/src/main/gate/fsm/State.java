/*
 *  State.java
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
 *  $Id: State.java 17699 2014-03-19 09:11:55Z markagreenwood $
 */

package gate.fsm;

import gate.jape.BasicPatternElement;
import gate.jape.JapeConstants;
import gate.jape.RightHandSide;
import gate.util.SimpleArraySet;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This class implements a Finite State Machine state.
 *
 */
public class State implements JapeConstants {

  private static final long serialVersionUID = 1733852753275942428L;

  public static final int UNKNOWN_INDEX = 1;
  public static final int VISITED_INDEX = -2;
  public static final int UNVISITED_INDEX = 2;
  public static final int INITIAL_INDEX = 0;
  public static final String INITIAL_RULE = "_____Initial_State_for_all_rules";
  public static final String UNKNOWN_RULE = "___UNKNOWN_RULES_TYPE_1";
  public static final String UNVISITED_RULE = "___UNKNOWN_RULES_TYPE_2";
  
  // Points to the rule in the FSM which created this state
  private int indexInRuleList = UNVISITED_INDEX;
  
  /**
   * 
   * @return  The index of the rule in the ruleTimes ArrayList held in the FSM
   */
  public int getIndexInRuleList() {
    return indexInRuleList;
  }

  /**
   * This should only need to be called by getRuleForState when the state is being initialized
   * @param indexInRuleList
   */
  void setIndexInRuleList(int indexInRuleList) {
    this.indexInRuleList = indexInRuleList;
  }
  
  /**
   * Sets the index of the rule for this state.
   * Determines the appropriate rule by recursively searching this state's outbound transitions until
   * we reach a final state.  Record this state in the ruleTimes and ruleNameToIndexMap structures
   */
  public int getRuleForState(Map<String,Integer> ruleNameToIndexMap, List<RuleTime>ruleTimes) {
    if (this.getIndexInRuleList() != UNVISITED_INDEX) {
      return this.getIndexInRuleList();  
    }
    if (this.isFinal()) {
      String ruleNameOfThisState = this.getAction().getRuleName();
      int returnVal;
      if (ruleNameToIndexMap.containsKey(ruleNameOfThisState)) {
        returnVal =  ruleNameToIndexMap.get(ruleNameOfThisState);
      }
      else {
        ruleTimes.add(new RuleTime(0,ruleNameOfThisState));
        ruleNameToIndexMap.put(ruleNameOfThisState, ruleTimes.size() - 1);
        returnVal =  ruleTimes.size() - 1;
      }
      this.setIndexInRuleList(returnVal);
      return returnVal;
    }
    else {
      this.setIndexInRuleList(VISITED_INDEX);
      int returnVal = UNKNOWN_INDEX;
      // Note that returnVal will always need to be the same for all returned elements
      // (because a state is currently associated with only one rule), but
      // we need to call it repeateadly to set the indexInRuleList for all states in 
      // the tree
      for (Transition t :this.getTransitions()) {
        int tempReturn = t.getTarget().getRuleForState(ruleNameToIndexMap,ruleTimes);
        if (tempReturn != UNKNOWN_INDEX && tempReturn != VISITED_INDEX) {
          returnVal = tempReturn;
        }
      }
      if (returnVal == UNKNOWN_INDEX) {
        this.setIndexInRuleList(returnVal);
      }
      else {
        this.propogateRuleForward(returnVal);
      }
      return returnVal;
    }
  }


  /**
   * This sets the rule index for every descendant of the current state
   * Note that we only need to set the state for states whose rule is Unknown
   * Rules whose state is "VISITED_INDEX" are my ancestors.  Their states will be set
   * when the recursion backs out.  Rules whose index is something other than VISITED_INDEX or
   * UNKNOWN_RULE are finished and we know that all of their descendants have been set, by
   * the properties of this algorithm 
   * @param ruleForThisState   The rule to be associated with this state
   */
  private void propogateRuleForward(int ruleForThisState) {
    this.setIndexInRuleList(ruleForThisState);
    for (Transition t: this.getTransitions()) {
      if (t.getTarget().getIndexInRuleList() == UNKNOWN_INDEX) {
        t.getTarget().propogateRuleForward(ruleForThisState);
      }
    }
  }

  /**
   * Build a new state.
   */
  public State() {
    myIndex = State.index++;
    isFinal = false;
  }

  /**
   * Reports if this state is a final one.
   * Note: A state has an associated action if and only if it is final.
   */
  public boolean isFinal() {
    return isFinal;
  }

  /**
   * Gets the set of transitions for this state.
   *
   * @return a Set contining objects of type gate.fsm.Transition
   */
// >>> DAM, was Set
/*
  public Set getTransitions() {
    return transitions;
  }
*/
// >>> DAM, TransArray optimization
  public SimpleArraySet<Transition> getTransitions() {
    return transitions;
  }
// >>> DAM, end
  /** Sets the action associated to this FINAL state. An action is actually
   * a gate.jape.RightHandSide object.
   * NOTE: only a final state has an associated action so after a call to this
   * method this state will be a final one.
   */
  protected void setAction(RightHandSide rhs) {
    action = rhs;
    isFinal = (action != null);
  }

  /** Sets the value for fileIndex. File index is the index in the jape
   * definition file of the rule that contains as right hand side the action
   * associated to this state. This value is only intended for final states.
   */
  protected void setFileIndex(int i) { fileIndex = i; }

  /** Sets the value for priority. Priority is the priority in the jape
   * definition file of the rule that contains as right hand side the action
   * associated to this state. This value is only intended for final states.
   */
  protected void setPriority(int i) { priority = i; }

  /**
   * Gets the action associated to this state.
   *
   * @return a RightHandSide object
   */
  public RightHandSide getAction() {
    return action;
  }

  /**
   * Returns the index in the definition file of the rule that generated this
   * state.
   * The value for fileIndex is correct only on final states!
   */
  protected int getFileIndex() { return fileIndex; }

  /**
   * Returns the priority in the definition file of the rule that generated
   * this state.
   * This value is correct only on final states!
   */
  protected int getPriority() { return priority; }

  /**
   * Adds a new transition to the list of outgoing transitions for this state.
   *
   * @param transition the transition to be added
   */
  public void addTransition(Transition transition) {
    transitions.add(transition);
  } // addTransition

  /**
   * Gets the index of this state. Each state has a unique index (a int value).
   * This value is not actually used by any of the algorithms. It is useful only
   * as a way of refering to states in string representations so it is used by
   * toString and GML related methods.
   *
   * @return the index associated to this state
   */
  public int getIndex() {
    return myIndex;
  }// getIndex

  /**
   * Returns a GML (graph modelling language) representation for the edges
   * corresponding to transitions departing from this state in the
   * transition graph of the FSM to which this state belongs
   *
   * @return a string value contining the GML text
   */
  public String getEdgesGML() {
///    String res = "";
    StringBuffer res = new StringBuffer(gate.Gate.STRINGBUFFER_SIZE);

    Iterator<Transition> transIter = transitions.iterator();
    BasicPatternElement bpe;

    while(transIter.hasNext()) {
      Transition currentTrans = transIter.next();
/*      res += "edge [ source " + myIndex +
             " target " + currentTrans.getTarget().getIndex() +
             " label \"" + currentTrans.shortDesc() + ":";
*/
        res.append("edge [ source ");
        res.append(myIndex);
        res.append(" target ");
        res.append(currentTrans.getTarget().getIndex());
        res.append(" label \"");
        res.append(currentTrans.shortDesc());
        res.append(":");

             bpe = currentTrans.getConstraints();
             if(bpe == null) ///res += "null";
                res.append("null");
             else ///res += bpe.shortDesc();
                res.append(bpe.shortDesc());
///             res += " :" + currentTrans.getBindings() +              "\" ]\n";
             res.append(" :");
             res.append(currentTrans.getBindings());
             res.append("\" ]\n");
    }
    return res.toString();
  } // getEdgesGML

  /**
   * Returns a textual description of this state
   *
   * @return a String value.
   */
  @Override
  public String toString() {
///    String res = "State " + myIndex;
    StringBuffer res = new StringBuffer(gate.Gate.STRINGBUFFER_SIZE);

    if(isFinal()) ///res += "\nFinal!";
        res.append("\nFinal!");

    ///res += "\nTransitions:\n";
    res.append("\nTransitions:\n");

    Iterator<Transition> transIter = transitions.iterator();
    while(transIter.hasNext()){
      ///res += transIter.next().toString();
      res.append(transIter.next().toString());
    }
    return res.toString();
  }


  /**
   * A set of objects of type gata.fsm.Transition representing the outgoing
   * transitions.
   */
// >>> DAM was
/*
  private Set transitions = new HashSet();
*/
// >>> DAM, TransArray optimization
  private SimpleArraySet<Transition> transitions = new SimpleArraySet<Transition>();
// >>> DAM, end

  /**
   * Is this state a final one?
   */
  protected boolean isFinal = false;

  /**
   * The right hand side associated to the rule for which this state recognizes
   * the lhs.
   */
  protected RightHandSide action = null;

  /**
   * The unique index of this state.
   */
  protected int myIndex;

  /**
   * The class data member used for generating unique indices for State
   * instances.
   */
  protected static int index = 0;

  /**
   * The index in the definition file of the rule that was used for creating
   * this state.
   * NOTE: this member is consistent only for FINAL STATES!
   */
  protected int fileIndex = 0;

  /**
   * The priority of the rule from which this state derived.
   *
   */
  protected int priority = -1;

} // State
