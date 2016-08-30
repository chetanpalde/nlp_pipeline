/*
 *  FSM.java
 *
 *  Copyright (c) 1995-2012, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 *  Valentin Tablan, 29/Mar/2000
 *
 *  $Id: FSM.java 17595 2014-03-08 13:05:32Z markagreenwood $
 */

package gate.fsm;

import java.util.*;

import gate.jape.*;
import gate.util.Benchmark;
import gate.util.SimpleArraySet;
import static gate.jape.KleeneOperator.Type.*;

/**
  * This class implements a standard Finite State Machine.
  * It is used for both deterministic and non-deterministic machines.
  */
public class FSM implements JapeConstants {
  
  private static final long serialVersionUID = -7088856970776558801L;
  
  private List<RuleTime> ruleTimes = new ArrayList<RuleTime>();
  
  public List<RuleTime> getRuleTimes() {
    return ruleTimes;
  }

  private void decorateStates() {
    HashMap<String,Integer>  temporaryRuleNameToIndexMap = new HashMap<String,Integer>();
    ruleTimes.add(new RuleTime(0,State.INITIAL_RULE));
    ruleTimes.add(new RuleTime(0,State.UNKNOWN_RULE));
    ruleTimes.add(new RuleTime(0,State.UNVISITED_RULE));
    int ruleIndex = State.INITIAL_INDEX;
    for (Transition t : this.getInitialState().getTransitions()) {
      ruleIndex = t.getTarget().getRuleForState(temporaryRuleNameToIndexMap, ruleTimes);
      assert (ruleIndex != State.UNVISITED_INDEX) && (ruleIndex != State.UNKNOWN_INDEX);
    }
    this.getInitialState().setIndexInRuleList(State.INITIAL_INDEX);
  }


  /** Debug flag */
  private static final boolean DEBUG = false;

  /**
   * The constructor that all the other constructors should call.
   */
  protected FSM() {
    initialState = new State();
  }

  /**
    * Builds a standalone FSM starting from a single phase transducer.
    * @param spt the single phase transducer to be used for building this FSM.
    */
  public FSM(SinglePhaseTransducer spt){
    this();
    addRules(spt.getRules());
    if (Benchmark.isBenchmarkingEnabled()) {
      this.decorateStates();
    }
  }

  /**
   * Do the work involved in creating an FSM from a PrioritisedRuleList.
   */
  protected void addRules(PrioritisedRuleList rules) {
    Iterator<Rule> rulesEnum = rules.iterator();

    while(rulesEnum.hasNext()){
      FSM ruleFSM = spawn(rulesEnum.next());

      //added by Karter start -> JapeDebugger
      ruleHash.putAll(ruleFSM.ruleHash);
      //added by Karter end

      initialState.addTransition(new Transition(null,
                                                ruleFSM.getInitialState()));
    }

    eliminateVoidTransitions();
  }

  /**
    * Builds a FSM starting from a rule. This FSM is actually a part of a larger
    * one (usually the one that is built based on the single phase transducer
    * that contains the rule).
    * built by this constructor.
    * @param rule the rule to be used for the building process.
    */
  public FSM(Rule rule) {
    this();
    setRule(rule);
  }


  /**
   * Do the work involved in creating an FSM from a Rule.
   */
  protected void setRule(Rule rule) {

    LeftHandSide lhs = rule.getLHS();

    //added by Karter start -> JapeDebugger
    LinkedList<String> ll = new LinkedList<String>();
    String label = currentLHSBinding(lhs);
    ll.add(label);
    ruleHash.put(rule.getName(), label);
    //added by Karter end


    PatternElement[][] constraints =
                       lhs.getConstraintGroup().getPatternElementDisjunction();
    // the rectangular array constraints is a disjunction of sequences of
    // constraints = [[PE]:[PE]...[PE] ||
    //                [PE]:[PE]...[PE] ||
    //                ...
    //                [PE]:[PE]...[PE] ]

    //The current and the next state for the current ROW.
    State currentRowState, nextRowState;
    State finalState = new State();
    PatternElement currentPattern;

    for(int i = 0; i < constraints.length; i++){
      // for each row we have to create a sequence of states that will accept
      // the sequence of annotations described by the restrictions on that row.
      // The final state of such a sequence will always be a final state which
      // will have associated the right hand side of the rule used for this
      // constructor.

      // For each row we will start from the initial state.
      currentRowState = initialState;
      for(int j=0; j < constraints[i].length; j++) {

        // parse the sequence of constraints:
        // For each basic pattern element add a new state and link it to the
        // currentRowState.
        // The case of kleene operators has to be considered!
        currentPattern = constraints[i][j];
        State insulator = new State();
        currentRowState.addTransition(new Transition(null,insulator));
        currentRowState = insulator;
        if(currentPattern instanceof BasicPatternElement) {
          //the easy case
          nextRowState = new State();

          //added by Karter start -> JapeDebugger
          LinkedList<String> sll = new LinkedList<String>();
          sll.add(currentBasicBinding( (BasicPatternElement) currentPattern));
          currentRowState.addTransition(
              new Transition( (BasicPatternElement) currentPattern,
                             nextRowState
                             /*added by Karter*/, sll));
          //added by Karter end

          currentRowState = nextRowState;
        } else if(currentPattern instanceof ComplexPatternElement) {

          // the current pattern is a complex pattern element
          // ..it will probaly be converted into a sequence of states itself.

          //  -> JapeDebugger
          currentRowState = convertComplexPE(
              currentRowState,
              (ComplexPatternElement) currentPattern,
              /*changed by Karter "new LinkedList()"*/ll);

        } else {
          // we got an unknown kind of pattern
          throw new RuntimeException("Strange looking pattern: " +
                                     currentPattern);
        }

      } // for j

      //link the end of the current row to the final state using
      //an empty transition.
      currentRowState.addTransition(new Transition(null,finalState));
      finalState.setAction(rule.getRHS());
      finalState.setFileIndex(rule.getPosition());
      finalState.setPriority(rule.getPriority());
    } // for i
  }

  /**
   * Builds a FSM starting from a ComplexPatternElement. This FSM is usually
   * part of a larger FSM based on the Rule that contains the
   * ComplexPatternElement.
   *
   * @param cpe
   *            the ComplexPatternElement to be used for the building process.
   */
  protected FSM(ComplexPatternElement cpe) {
      this();
      finalState = convertComplexPE(initialState, cpe, new LinkedList<String>());
      finalState.isFinal = true;
  }

  /**
   * A factory method for new FSMs like this one, given a Rule object.
   */
  protected FSM spawn(Rule r) {
    return new FSM(r);
  }

  /**
   * A factory method for new FSMs like this one, given a ComplexPatternElement
   * object.
   */
  protected FSM spawn(ComplexPatternElement currentPattern) {
      return new FSM(currentPattern);
  }

  /**
    * Gets the initial state of this FSM
    * @return an object of type gate.fsm.State representing the initial state.
    */
  public State getInitialState() {
    return initialState;
  } // getInitialState

  /**
    * Receives a state to start from and a complex pattern element.
    * Parses the complex pattern element and creates all the necessary states
    * and transitions for accepting annotations described by the given PE.
    * @param startState the state to start from
    * @param cpe the pattern to be recognized
    * @param labels the bindings name for all the annotation accepted along
    * the way. This is actually a list of Strings. It is necessary to use
    * a list because of the recursive definition of ComplexPatternElement.
    * @return the final state reached after accepting a sequence of annotations
    * as described in the pattern
    */
  private State convertComplexPE(State startState,
          ComplexPatternElement cpe, LinkedList<String> labels){

    State endState = generateStates(startState, cpe, labels);

    // now take care of the kleene operator
    KleeneOperator kleeneOp = cpe.getKleeneOp();
    KleeneOperator.Type type = kleeneOp.getType();
    if (type == OPTIONAL) {
      //allow to skip everything via a null transition
      startState.addTransition(new Transition(null,endState));
    }
    else if (type == PLUS) {
      // allow to return to startState
      endState.addTransition(new Transition(null,startState));
    }
    else if (type == STAR) {
      // allow to skip everything via a null transition
      startState.addTransition(new Transition(null,endState));

      // allow to return to startState
      endState.addTransition(new Transition(null,startState));
    }
    else if (type == RANGE) {
      Integer min = kleeneOp.getMin();
      Integer max = kleeneOp.getMax();

      // keep a list of the start states for each possible optional sets so can make
      // direct transitions from them to the final end state
      List<State> startStateList = new ArrayList<State>();

      if (min == null || min == 0) {
        //if min is empty or 0, allow to skip everything via a null transition
        startStateList.add(startState);
      }
      else if (min > 1) {
        // add min-1 copies of the set of states for the CPE.  It's -1 because
        // one set was already added by the first generateStates call
        int numCopies = min - 1;
        for(int i = 1; i <= numCopies; i++) {
          // the end state of the previous set always moves up to be the
          // start state of the next set.
          startState = endState;
          endState = generateStates(startState, cpe, labels);
        }
      }

      if (max == null) {
        // if there is no defined max, allow to return to startState any
        // number of times.  Start state may be the original start or, if
        // min > 1, then it's the start of the last set of states added.
        // Example: A range with min 3 and max = unbounded will look like
        // this:
        //                                  v------|
        // start1...end1->start2...end2->start3...end3->...
        //
        endState.addTransition(new Transition(null,startState));
      }
      else if (max > min) {
        // there are some optional state sets.  Make a copy of the state
        // set for each.
        int numCopies = max-min;

        //if min == 0 then reduce numCopies by one since we already added
        //one set of states that are optional
        if (min == 0)
          numCopies--;

        for(int i = 1; i <= numCopies; i++) {
          startState = endState;
          startStateList.add(startState);
          endState = generateStates(startState, cpe, labels);
        }
      }

      //each of the optional stages can transition directly to the final end
      for(State state : startStateList) {
        state.addTransition(new Transition(null,endState));
      }

    } //end if type == RANGE

    return endState;
  } // convertComplexPE

  /**
   * Receives a state to start from and a complex pattern element.
   * Parses the complex pattern element and creates all the necessary states
   * and transitions for traversing the annotations described by the given PE
   * exactly once.  Does not add any transitions for kleene operators.
   * @param startState the state to start from
   * @param cpe the pattern to be recognized
   * @param labels the bindings name for all the annotation accepted along
   * the way. This is actually a list of Strings. It is necessary to use
   * a list because of the recursive definition of ComplexPatternElement.
   * @return the final state reached after accepting a sequence of annotations
   * as described in the pattern
   */
  private State generateStates(State startState, ComplexPatternElement cpe,
          LinkedList<String> labels) {
    //create a copy
    @SuppressWarnings("unchecked")
    LinkedList<String> newBindings = (LinkedList<String>)labels.clone();
    String localLabel = cpe.getBindingName ();

    if(localLabel != null)newBindings.add(localLabel);

    ConstraintGroup constraintGroup = cpe.getConstraintGroup();
    PatternElement[][] constraints =
                       constraintGroup.getPatternElementDisjunction();

    // the rectangular array constraints is a disjunction of sequences of
    // constraints = [[PE]:[PE]...[PE] ||
    //                [PE]:[PE]...[PE] ||
    //                ...
    //                [PE]:[PE]...[PE] ]

    //The current and the next state for the current ROW.
    State currentRowState, nextRowState, endState = new State();
    PatternElement currentPattern;

    for(int i = 0; i < constraints.length; i++) {
      // for each row we have to create a sequence of states that will accept
      // the sequence of annotations described by the restrictions on that row.
      // The final state of such a sequence will always be a finale state which
      // will have associated the right hand side of the rule used for this
      // constructor.

      //For each row we will start from the initial state.
      currentRowState = startState;
      for(int j=0; j < (constraints[i]).length; j++) {

        //parse the sequence of constraints:
        //For each basic pattern element add a new state and link it to the
        //currentRowState.
        //The case of kleene operators has to be considered!
        State insulator = new State();
        currentRowState.addTransition(new Transition(null,insulator));
        currentRowState = insulator;
        currentPattern = constraints[i][j];
        if(currentPattern instanceof BasicPatternElement) {

          //the easy case
          nextRowState = new State();

          //added by Karter start -> JapeDebugger
          newBindings.add(currentBasicBinding( (BasicPatternElement)
                                              currentPattern));
          //added by Karter end


          currentRowState.addTransition(
            new Transition((BasicPatternElement)currentPattern,
                            nextRowState,newBindings));
          currentRowState = nextRowState;
        } else if(currentPattern instanceof ComplexPatternElement) {

          // the current pattern is a complex pattern element
          // ..it will probaly be converted into a sequence of states itself.
          currentRowState =  convertComplexPE(
                              currentRowState,
                              (ComplexPatternElement)currentPattern,
                              newBindings);
        } else {

          //we got an unknown kind of pattern
          throw new RuntimeException("Strange looking pattern:"+currentPattern);
        }

      } // for j
        // link the end of the current row to the general end state using
        // an empty transition.
        currentRowState.addTransition(new Transition(null,endState));
    } // for i
    return endState;
  }

  /**
    * Converts this FSM from a non-deterministic to a deterministic one by
    * eliminating all the unrestricted transitions.
    */
  public void eliminateVoidTransitions() {

    dStates.clear(); //kalina: replaced from new HashSet()
    LinkedList<Set<State>> unmarkedDStates = new LinkedList<Set<State>>();
    Set<State> currentDState = new HashSet<State>();
    //kalina: prefer clear coz faster than init()
    newStates.clear();

    currentDState.add(initialState);
    currentDState = lambdaClosure(currentDState);
    dStates.add(currentDState);
    unmarkedDStates.add(currentDState);

    // create a new state that will take the place the set of states
    // in currentDState
    initialState = new State();
    newStates.put(currentDState, initialState);

    // find out if the new state is a final one
    Iterator<State> innerStatesIter = currentDState.iterator();
    RightHandSide action = null;

    while(innerStatesIter.hasNext()){
      State currentInnerState = innerStatesIter.next();
      if(currentInnerState.isFinal()){
        action = currentInnerState.getAction();
        initialState.setAction(action);
        initialState.setFileIndex(currentInnerState.getFileIndex());
        initialState.setPriority(currentInnerState.getPriority());
        break;
      }
    }

    while(!unmarkedDStates.isEmpty()) {
      currentDState = unmarkedDStates.removeFirst();
      Iterator<State> insideStatesIter = currentDState.iterator();

      while(insideStatesIter.hasNext()) {
        State innerState = insideStatesIter.next();
        Iterator<Transition> transIter = innerState.getTransitions().iterator();

        while(transIter.hasNext()) {
          Transition currentTrans = transIter.next();

          if(currentTrans.getConstraints() !=null) {
            State target = currentTrans.getTarget();
            Set<State> newDState = new HashSet<State>();
            newDState.add(target);
            newDState = lambdaClosure(newDState);

            if(!dStates.contains(newDState)) {
              dStates.add(newDState);
              unmarkedDStates.add(newDState);
              State newState = new State();
              newStates.put(newDState, newState);

              //find out if the new state is a final one
              innerStatesIter = newDState.iterator();
              while(innerStatesIter.hasNext()) {
                State currentInnerState = innerStatesIter.next();

                if(currentInnerState.isFinal()) {
                  newState.setAction(
                          currentInnerState.getAction());
                  newState.setFileIndex(currentInnerState.getFileIndex());
                  newState.setPriority(currentInnerState.getPriority());
                  break;
                }
              }
            }// if(!dStates.contains(newDState))

            State currentState = newStates.get(currentDState);
            State newState = newStates.get(newDState);
            currentState.addTransition(new Transition(
                                        currentTrans.getConstraints(),
                                        newState,
                                        currentTrans.getBindings()));
          }// if(currentTrans.getConstraints() !=null)

        }// while(transIter.hasNext())

      }// while(insideStatesIter.hasNext())

    }// while(!unmarkedDstates.isEmpty())

    /*
    //find final states
    Iterator allDStatesIter = dStates.iterator();
    while(allDStatesIter.hasNext()){
      currentDState = (AbstractSet) allDStatesIter.next();
      Iterator innerStatesIter = currentDState.iterator();
      while(innerStatesIter.hasNext()){
        State currentInnerState = (State) innerStatesIter.next();
        if(currentInnerState.isFinal()){
          State newState = (State)newStates.get(currentDState);

          newState.setAction(currentInnerState.getAction());
          break;
        }
      }

    }
    */
    allStates = newStates.values();
  }//eliminateVoidTransitions

  /*
    * Computes the lambda-closure (aka epsilon closure) of the given set of
    * states, that is the set of states that are accessible from any of the
    * states in the given set using only unrestricted transitions.
    * @return a set containing all the states accessible from this state via
    * transitions that bear no restrictions.
    */
  private Set<State> lambdaClosure(Set<State> s) {
    // the stack/queue used by the algorithm
    LinkedList<State> list = new LinkedList<State>(s);

    // the set to be returned
    Set<State> lambdaClosure = new HashSet<State>(s);
    State top;
    Iterator<Transition> transIter;
    Transition currentTransition;
    State currentState;
    while(!list.isEmpty()){
      top = list.removeFirst();
      transIter = top.getTransitions().iterator();

      while(transIter.hasNext()){
        currentTransition = transIter.next();

        if(currentTransition.getConstraints() == null){
          currentState = currentTransition.getTarget();
          if(!lambdaClosure.contains(currentState)){
            lambdaClosure.add(currentState);
            list.addFirst(currentState);
          }// if(!lambdaClosure.contains(currentState))

        }// if(currentTransition.getConstraints() == null)

      }
    }
    return lambdaClosure;
  } // lambdaClosure


  /**
   * Two members used by forEachState().
   */
  protected State currentState;
  protected Transition currentTransition;

  /**
   * Iterates over all the states in this FSM, setting currentState and
   * currentTransition, then calling the given Runnable callback.
   */
  protected void forEachState (java.lang.Runnable r) {
    Set<State> stackToProcess = new HashSet<State>();
    Set<State> processed = new HashSet<State>();

    stackToProcess.add(initialState);
    while (!stackToProcess.isEmpty()) {
      currentState = stackToProcess.iterator().next();
      stackToProcess.remove(currentState);
      processed.add(currentState);

      for(Transition t : currentState.getTransitions()) {
        currentTransition = t;
        State target = currentTransition.getTarget();
        if (processed.contains(target) || stackToProcess.contains(target)) continue;
        stackToProcess.add(target);

        r.run();
      }
    }
  }

  /**
   * @return a Map whose keys contain the states of this FSM, and whose values
   *         contain their corresponding transitions. This method actually walks
   *         the FSM, so it may be called before the FSM is finalized with
   *         compactTransitions().
   */
  public Map<State,SimpleArraySet<Transition>> getAllStates() {
    /*
     * This method can't use the allStates data member, since it's sometimes
     * called before allStates is populated.
     */

    Map<State,SimpleArraySet<Transition>> statesToReturn = new HashMap<State,SimpleArraySet<Transition>>();
    Set<State> stackToProcess = new HashSet<State>();
    Set<State> processed = new HashSet<State>();

    stackToProcess.add(initialState);
    while (!stackToProcess.isEmpty()) {
      currentState = stackToProcess.iterator().next();
      stackToProcess.remove(currentState);
      processed.add(currentState);


      SimpleArraySet<Transition> transitions = currentState.getTransitions();
      statesToReturn.put(currentState, transitions);
      for (Iterator<Transition> iter = transitions.iterator(); iter.hasNext();) {
        currentTransition = iter.next();
        State target = currentTransition.getTarget();
        if (processed.contains(target) || stackToProcess.contains(target)) continue;
        stackToProcess.add(target);
      }
    }

    return statesToReturn;
  }

  /**
   * Returns a representation of this FSM in the GraphViz graph-visualization
   * language. We use the "digraph" (directed graph) format. Nodes are labeled
   * by their numerical indexes. A node's shape is a diamond if it's the initial
   * state, and round otherwise. A node is green if it's an initial state, red
   * if it's a final state, and black otherwise. Final states are also marked
   * with a double-line outline.
   * 
   * @see <a href="http://www.graphviz.org">GraphViz for visulization</a>
   * @param includeConstraints
   *          whether to include a stringified representation of each transition
   *          object as part of its label. The default is false.
   */
  public String asGraphViz(boolean includeConstraints) {
    StringBuffer result = new StringBuffer();

    result.append("digraph G {\n");

    for(State currentState : getAllStates().keySet()) {
      int stateIndex = currentState.getIndex();
      Map<String,String> opts = new HashMap<String,String>();
      opts.put("shape", currentState == initialState ? "diamond" : "circle");
      opts.put("color", currentState == initialState ? "green" : currentState.isFinal() ? "red" : "black");
      if (currentState.isFinal()) {
        opts.put("peripheries", "2");
        if (DEBUG) {
          opts.put("shape", "rectangle");
          opts.put("label", "" + stateIndex + "-" + currentState.getAction());
        }
      }

      result.append("  " + stateIndex + " [" + encodeForGraphViz(opts) + "]" + ";\n");

      for(Transition t : currentState.getTransitions()) {
        String extraText = includeConstraints
        ? " [label=\"" + t.toString(false) + "\"]"
                : "";
        result.append("  " + stateIndex + " -> " + t.getTarget().getIndex()
                + extraText + ";\n");
      }
    }

    result.append("}\n");

    return result.toString();
  }

  /**
   * Given a Map, encodes its keys and values as strings suitable for use as a
   * GraphViz label. Embedded "\r\n" sequences are replaced by "\\l" to create
   * line feeds, and embedded backslashes are escaped. The returned String takes
   * the form "key1=value1, key2=value2, ...".
   */
  String encodeForGraphViz (Map<String,String> m) {
    ArrayList<String> temp = new ArrayList<String>(m.size());
    for(String k : m.keySet()) {
      String v = m.get(k);
      v = v.replaceAll("\r\n", "\\\\l");
      v = v.replaceAll("\"", "\\\\\"");
      temp.add(k + "=\"" + v + "\"");
    }

    StringBuffer toReturn = new StringBuffer();
    for (int i = 0; i < temp.size(); i++)
    {
      if (i != 0) toReturn.append(",");
      toReturn.append(temp.get(i));
    }
    return toReturn.toString();
  }


  /**
    * Returns a GML (Graph Modelling Language) representation of the transition
    * graph of this FSM.
    */
  public String getGML() {

    String res = "graph[ \ndirected 1\n";

    StringBuffer nodes = new StringBuffer(gate.Gate.STRINGBUFFER_SIZE),
                 edges = new StringBuffer(gate.Gate.STRINGBUFFER_SIZE);

    Iterator<State> stateIter = allStates.iterator();
    while (stateIter.hasNext()){
      State currentState = stateIter.next();
      int stateIndex = currentState.getIndex();
        nodes.append("node[ id ");
        nodes.append(stateIndex);
        nodes.append(" label \"");
        nodes.append(stateIndex);

             if(currentState.isFinal()){
              nodes.append(",F\\n" + currentState.getAction().shortDesc());
             }
             nodes.append("\"  ]\n");
      edges.append(currentState.getEdgesGML());
    }
    res += nodes.toString() + edges.toString() + "]\n";
    return res;
  } // getGML

  /**
    * Returns a textual description of this FSM.
    */
  @Override
  public String toString(){
    String res = "Starting from:" + initialState.getIndex() + "\n";
    Iterator<State> stateIter = allStates.iterator();
    while (stateIter.hasNext()){
      res += "\n\n" + stateIter.next();
    }
    return res;
  } // toString

  /**
    * The initial state of this FSM.
    */
  private State initialState;

 /**
   * The final state of this FSM (usually only valid during construction).
   */
  protected State finalState;

  /**
    * The set of states for this FSM
    */
  private transient Collection<State> allStates =  new HashSet<State>();

  //kalina: added this member here to minimise HashMap allocation
  private transient Map<Set<State>,State> newStates = new HashMap<Set<State>,State>();
  private transient Set<Set<State>> dStates = new HashSet<Set<State>>();


  //added by Karter start
  private String currentBinding(ComplexPatternElement cpe, int indent) {
    if (indent == 0)
      bpeId = 0;
    String ind = "";
    for (int i = 0; i < indent; i++) {
      ind += "   ";
    }
    String binds = ind + "(\n";
    PatternElement[][] pe = cpe.getConstraintGroup().
        getPatternElementDisjunction();
    for (int i = 0; i < pe.length; i++) {
      PatternElement[] patternElements = pe[i];
      for (int j = 0; j < patternElements.length; j++) {
        PatternElement patternElement = patternElements[j];
        if (patternElement instanceof ComplexPatternElement) {
          ComplexPatternElement complexPatternElement = (ComplexPatternElement)
              patternElement;
          binds += currentBinding(complexPatternElement, indent + 1);

        }
        else {
          binds += ind + "   ";
          binds += currentBasicBinding((BasicPatternElement) patternElement);
          binds += "\n";
        }
      }
      binds += ind + "   |\n";
    }
    binds = binds.substring(0, binds.length() - 5);
    binds += ")" + cpe.getKleeneOp().toString() + "\n";
    if (indent == 0)
      bpeId = 0;
    return binds;
  }

  private String currentBasicBinding(BasicPatternElement bpe) {
    StringBuilder sb = new StringBuilder("{");
    Constraint[] cons = bpe.getConstraints();
    for (int k = 0; k < cons.length; k++) {
      sb.append(cons[k].getDisplayString(""));
      if (k < cons.length - 1)
        sb.append(",");
    }
    sb.append("}").append(" *").append(bpeId++).append("*");
    return sb.toString();
  }

  private String currentLHSBinding(LeftHandSide lhs) {
    String binds = "(\n";
    PatternElement[][] pe = lhs.getConstraintGroup().
        getPatternElementDisjunction();
    for (int i = 0; i < pe.length; i++) {
      PatternElement[] patternElements = pe[i];
      for (int j = 0; j < patternElements.length; j++) {
        PatternElement patternElement = patternElements[j];
        if (patternElement instanceof ComplexPatternElement) {
          ComplexPatternElement complexPatternElement = (ComplexPatternElement)
              patternElement;
          binds += currentBinding(complexPatternElement, 1);

        }
        else {
          binds += "   ";
          binds += currentBasicBinding((BasicPatternElement) patternElement);
          binds += "\n";
        }
      }
      binds += "   |\n";
    }
    binds = binds.substring(0, binds.length() - 5);
    binds += ")\n";
    bpeId = 0;
    return binds;
  }

  int bpeId = 0;
  public HashMap<String,String> ruleHash = new HashMap<String,String>();
  //added by Karter end
} // FSM
