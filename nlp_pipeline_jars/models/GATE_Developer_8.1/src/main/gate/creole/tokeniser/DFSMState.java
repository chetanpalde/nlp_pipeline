/*
 *  DFSMState.java
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
 *  $Id: DFSMState.java 17595 2014-03-08 13:05:32Z markagreenwood $
 */

 /*
    modified by OntoText, Aug 29

 */

package gate.creole.tokeniser;

import java.util.*;

/** Implements a state of the deterministic finite state machine of the
  * tokeniser.
  * It differs from {@link FSMState FSMState} by the definition of the
  * transition function which in this case maps character types to other states
  * as oposed to the transition function from FSMState which maps character
  * types to sets of states, hence the nondeterministic character.
  * @see FSMState
  */
class DFSMState implements java.io.Serializable { //extends FSMState{

  private static final long serialVersionUID = 7584872407097617987L;

  /** Constructs a new DFSMState object and adds it to the list of deterministic
    * states of the {@link DefaultTokeniser DefaultTokeniser} provided as owner.
    * @param owner a {@link DefaultTokeniser DefaultTokeniser} object
    */
  public DFSMState(SimpleTokeniser owner){
    myIndex = index++;
    owner.dfsmStates.add(this);
  }

  /** Adds a new mapping in the transition function of this state
    * @param type the UnicodeType for this mapping
    * @param state the next state of the FSM Machine when a character of type type
    * is read from the input.
    */
  void put(UnicodeType type, DFSMState state){
    put(type.type, state);
  } // put(UnicodeType type, DFSMState state)

  /** Adds a new mapping using the actual index in the internal array.
    * This method is for internal use only. Use
    * {@link #put(gate.creole.tokeniser.UnicodeType,
    *             gate.creole.tokeniser.DFSMState)} instead.
    */
  void put(int index, DFSMState state){
    transitionFunction[index] = state;
  } // put(int index, DFSMState state)

  /** This method is used to access the transition function of this state.
    * @param type the Unicode type identifier as the corresponding static value
    * on {@link java.lang.Character}
    */
  DFSMState next(int type){//UnicodeType type){
    return transitionFunction[type];
  } // next

  /** Returns a GML (Graph Modelling Language) representation of the edges
    * emerging from this state
    */
  String getEdgesGML(){
    ///String res = "";
    //OT
    StringBuffer res = new StringBuffer(gate.Gate.STRINGBUFFER_SIZE);

    DFSMState nextState;

    for(int i = 0; i< transitionFunction.length; i++){
      nextState = transitionFunction[i];
      if(null != nextState){
        /*
        res += "edge [ source " + myIndex +
        " target " + nextState.getIndex() +
        " label \"";
        res += SimpleTokeniser.typeMnemonics[i];
        res += "\" ]\n";
        */
        //OT
        res.append("edge [ source ");
        res.append(myIndex);
        res.append(" target ");
        res.append(nextState.getIndex());
        res.append(" label \"");
        res.append(SimpleTokeniser.typeMnemonics[i]);
        res.append("\" ]\n");
      }
    };
    return res.toString();
  } // getEdgesGML

  /** Builds the token description for the token that will be generated when
    * this <b>final</b> state will be reached and the action associated with it
    * will be fired.
    * See also {@link #setRhs(String)}.
    */
  void buildTokenDesc() throws TokeniserException{
    String ignorables = " \t\f";
    String token = null,
           type = null,
           attribute = null,
           value = null
           ///prefix = null,
           ///read =""
           ;
    //OT
    StringBuffer prefix = new StringBuffer(gate.Gate.STRINGBUFFER_SIZE);
    StringBuffer read = new StringBuffer(gate.Gate.STRINGBUFFER_SIZE);

    LinkedList<String> attributes = new LinkedList<String>(),
               values = new LinkedList<String>();
    StringTokenizer mainSt =
      new StringTokenizer(rhs, ignorables + "\\\";=", true);

    //phase means:
    //0 == looking for type;
    //1 == looking for attribute;
    //2 == looking for value;
    //3 == write the attr/value pair
    int phase = 0;

    while(mainSt.hasMoreTokens()) {
      token = SimpleTokeniser.skipIgnoreTokens(mainSt);

      if(token.equals("\\")){
        if(null == prefix)
            ///prefix = mainSt.nextToken();
        //OT
            prefix = new StringBuffer(mainSt.nextToken());
        else ///prefix += mainSt.nextToken();
        //OT
            prefix.append(mainSt.nextToken());
        continue;
      } else if(null != prefix) {
        ///read += prefix;
        //OT
        read.append(prefix.toString());
        prefix = null;
      }

      if(token.equals("\"")){
        ///read = mainSt.nextToken("\"");
        //OT
        read = new StringBuffer(mainSt.nextToken("\""));
        if(read.toString().equals("\"")) ///read = "";
            read = new StringBuffer(gate.Gate.STRINGBUFFER_SIZE);
        else {
          //delete the remaining enclosing quote and restore the delimiters
          mainSt.nextToken(ignorables + "\\\";=");
        }

      } else if(token.equals("=")) {

        if(phase == 1){
          ///attribute = read;
          //OT
          attribute = read.toString();
          ///read = "";
          //OT
          read = new StringBuffer(gate.Gate.STRINGBUFFER_SIZE);
          phase = 2;
        }else throw new TokeniserException("Invalid attribute format: " +
                                           read);
      } else if(token.equals(";")) {
        if(phase == 0){
          ///type = read;
          type = read.toString();
          ///read = "";
          read = new StringBuffer(gate.Gate.STRINGBUFFER_SIZE);
          //Out.print("Type: " + type);
          attributes.addLast(type);
          values.addLast("");
          phase = 1;
        } else if(phase == 2) {
          ///value = read;
          value = read.toString();
          ///read = "";
          read = new StringBuffer(gate.Gate.STRINGBUFFER_SIZE);
          phase = 3;
        } else throw new TokeniserException("Invalid value format: " +
                                           read);
      } else ///read += token;
            read.append(token);

      if(phase == 3) {
        // Out.print("; " + attribute + "=" + value);
        attributes.addLast(attribute);
        values.addLast(value);
        phase = 1;
      }
    }
    //Out.println();
    if(attributes.size() < 1)
      throw new InvalidRuleException("Invalid right hand side " + rhs);
    tokenDesc = new String[attributes.size()][2];

    for(int i = 0; i < attributes.size(); i++) {
      tokenDesc[i][0] = attributes.get(i);
      tokenDesc[i][1] = values.get(i);
    }

    // for(int i = 0; i < attributes.size(); i++){
    //    Out.println(tokenDesc[i][0] + "=" +
    //                  tokenDesc[i][1]);
    // }
  } // buildTokenDesc

  /** Sets the right hand side associated with this state. The RHS is
    * represented as a string value that will be parsed by the
    * {@link #buildTokenDesc()} method being converted in a table of strings
    * with 2 columns and as many lines as necessary.
    * @param rhs the RHS string
    */
  void setRhs(String rhs) { this.rhs = rhs; }

  /** Returns the RHS string*/
  String getRhs(){return rhs;}

  /** Checks whether this state is a final one*/
  boolean isFinal() { return (null != rhs); }

  /** Returns the unique ID of this state.*/
  int getIndex() { return myIndex; }

  /** Returns the token description associated with this state. This description
    * is built by {@link #buildTokenDesc()} method and consists of a table of
    * strings having two columns.
    * The first line of the table contains the annotation type on the first
    * position and nothing on the second.
    * Each line after the first one contains a attribute on the first position
    * and its associated value on the second.
    */
  String[][] getTokenDesc() {
    return tokenDesc;
  }

  /** A table of strings describing an annotation.
    * The first line of the table contains the annotation type on the first
    * position and nothing on the second.
    * Each line after the first one contains a attribute on the first position
    * and its associated value on the second.
    */
  String[][] tokenDesc;

  /** The transition function of this state.
    */
  DFSMState[] transitionFunction = new DFSMState[SimpleTokeniser.maxTypeId];

  /** The string of the RHS of the rule from which the token
    * description is built
    */
  String rhs;

  /** The unique index of this state*/
  int myIndex;

  /** Used to generate unique indices for all the objects of this class*/
  static int index;

  static {
    index = 0;
  }

} // class DFSMState
