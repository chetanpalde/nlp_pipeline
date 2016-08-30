/*
 *  DefaultTokeniser.java
 *
 *  Copyright (c) 1995-2012, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 *  Valentin Tablan, 2000
 *
 *  $Id: SimpleTokeniser.java 18317 2014-09-11 18:21:24Z ian_roberts $
 */

package gate.creole.tokeniser;

import gate.AnnotationSet;
import gate.Factory;
import gate.FeatureMap;
import gate.Gate;
import gate.Resource;
import gate.creole.AbstractLanguageAnalyser;
import gate.creole.ExecutionException;
import gate.creole.ExecutionInterruptedException;
import gate.creole.ResourceInstantiationException;
import gate.creole.metadata.CreoleParameter;
import gate.creole.metadata.CreoleResource;
import gate.creole.metadata.Optional;
import gate.creole.metadata.RunTime;
import gate.util.BomStrippingInputStreamReader;
import gate.util.Err;
import gate.util.GateRuntimeException;
import gate.util.InvalidOffsetException;
import gate.util.LuckyException;

import java.io.BufferedReader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.io.IOUtils;

/** Implementation of a Unicode rule based tokeniser.
 * The tokeniser gets its rules from a file an {@link java.io.InputStream
 * InputStream} or a {@link java.io.Reader Reader} which should be sent to one
 * of the constructors.
 * The implementations is based on a finite state machine that is built based
 * on the set of rules.
 * A rule has two sides, the left hand side (LHS)and the right hand side (RHS)
 * that are separated by the &quot;&gt;&quot; character. The LHS represents a
 * regular expression that will be matched against the input while the RHS
 * describes a Gate2 annotation in terms of annotation type and attribute-value
 * pairs.
 * The matching is done using Unicode enumarated types as defined by the {@link
 * java.lang.Character Character} class. At the time of writing this class the
 * suported Unicode categories were:
 * <ul>
 * <li>UNASSIGNED
 * <li>UPPERCASE_LETTER
 * <li>LOWERCASE_LETTER
 * <li>TITLECASE_LETTER
 * <li>MODIFIER_LETTER
 * <li>OTHER_LETTER
 * <li>NON_SPACING_MARK
 * <li>ENCLOSING_MARK
 * <li>COMBINING_SPACING_MARK
 * <li>DECIMAL_DIGIT_NUMBER
 * <li>LETTER_NUMBER
 * <li>OTHER_NUMBER
 * <li>SPACE_SEPARATOR
 * <li>LINE_SEPARATOR
 * <li>PARAGRAPH_SEPARATOR
 * <li>CONTROL
 * <li>FORMAT
 * <li>PRIVATE_USE
 * <li>SURROGATE
 * <li>DASH_PUNCTUATION
 * <li>START_PUNCTUATION
 * <li>END_PUNCTUATION
 * <li>CONNECTOR_PUNCTUATION
 * <li>OTHER_PUNCTUATION
 * <li>MATH_SYMBOL
 * <li>CURRENCY_SYMBOL
 * <li>MODIFIER_SYMBOL
 * <li>OTHER_SYMBOL
 * </ul>
 * The accepted operators for the LHS are "+", "*" and "|" having the usual
 * interpretations of "1 to n occurences", "0 to n occurences" and
 * "boolean OR".
 * For instance this is a valid LHS:
 * <br>"UPPERCASE_LETTER" "LOWERCASE_LETTER"+
 * <br>meaning an uppercase letter followed by one or more lowercase letters.
 *
 * The RHS describes an annotation that is to be created and inserted in the
 * annotation set provided in case of a match. The new annotation will span the
 * text that has been recognised. The RHS consists in the annotation type
 * followed by pairs of attributes and associated values.
 * E.g. for the LHS above a possible RHS can be:<br>
 * Token;kind=upperInitial;<br>
 * representing an annotation of type &quot;Token&quot; having one attribute
 * named &quot;kind&quot; with the value &quot;upperInitial&quot;<br>
 * The entire rule willbe:<br>
 * <pre>"UPPERCASE_LETTER" "LOWERCASE_LETTER"+ > Token;kind=upperInitial;</pre>
 * <br>
 * The tokeniser ignores all the empty lines or the ones that start with # or
 * //.
 *
 */
@CreoleResource(name="GATE Unicode Tokeniser", comment="A customisable Unicode tokeniser.", helpURL="http://gate.ac.uk/userguide/sec:annie:tokeniser", icon="tokeniser")
public class SimpleTokeniser extends AbstractLanguageAnalyser{

  private static final long serialVersionUID = 1411111968361716069L;

  public static final String
    SIMP_TOK_DOCUMENT_PARAMETER_NAME = "document";

  public static final String
    SIMP_TOK_ANNOT_SET_PARAMETER_NAME = "annotationSetName";

  public static final String
    SIMP_TOK_RULES_URL_PARAMETER_NAME = "rulesURL";

  public static final String
    SIMP_TOK_ENCODING_PARAMETER_NAME = "encoding";

  /**
   * Creates a tokeniser
   */
  public SimpleTokeniser(){
  }

  /**
   * Initialises this tokeniser by reading the rules from an external source (provided through an URL) and building
   * the finite state machine at the core of the tokeniser.
   *
   * @exception ResourceInstantiationException
   */
  @Override
  public Resource init() throws ResourceInstantiationException{
    BufferedReader bRulesReader = null;
    try{
      if(rulesURL != null){
        bRulesReader = new BufferedReader(new BomStrippingInputStreamReader(rulesURL.openStream(), encoding));
      }else{
        //no init data, Scream!
        throw new ResourceInstantiationException(
          "No URL provided for the rules!");
      }
      initialState = new FSMState(this);
      String line = bRulesReader.readLine();
      ///String toParse = "";
      StringBuffer toParse = new StringBuffer(Gate.STRINGBUFFER_SIZE);

      while (line != null){
        if(line.endsWith("\\")){
          ///toParse += line.substring(0,line.length()-1);
          toParse.append(line.substring(0,line.length()-1));
        }else{
          /*toParse += line;
          parseRule(toParse);
          toParse = "";
          */
          toParse.append(line);
          parseRule(toParse.toString());
          toParse.delete(0,toParse.length());
        }
        line = bRulesReader.readLine();
      }
      eliminateVoidTransitions();
    }catch(java.io.IOException ioe){
      throw new ResourceInstantiationException(ioe);
    }catch(TokeniserException te){
      throw new ResourceInstantiationException(te);
    }
    finally {
      IOUtils.closeQuietly(bRulesReader);
    }
    return this;
  }

  /**
   * Prepares this Processing resource for a new run.
   */
  public void reset(){
    document = null;
  }

  /** Parses one input line containing a tokeniser rule.
   * This will create the necessary FSMState objects and the links
   * between them.
   *
   * @param line the string containing the rule
   */
  void parseRule(String line)throws TokeniserException{
    //ignore comments
    if(line.startsWith("#")) return;

    if(line.startsWith("//")) return;

    StringTokenizer st = new StringTokenizer(line, "()+*|\" \t\f>", true);
    FSMState newState = new FSMState(this);

    initialState.put(null, newState);
    FSMState finalState = parseLHS(newState, st, LHStoRHS);
    String rhs = "";

    if(st.hasMoreTokens()) rhs = st.nextToken("\f");

    if(rhs.length() > 0)finalState.setRhs(rhs);
  } // parseRule

  /** Parses a part or the entire LHS.
   *
   * @param startState a FSMState object representing the initial state for
   *     the small FSM that will recognise the (part of) the rule parsed by this
   *     method.
   * @param st a {@link java.util.StringTokenizer StringTokenizer} that
   *     provides the input
   * @param until the string that marks the end of the section to be
   *     recognised. This method will first be called by {@link
   *     #parseRule(String)} with &quot; &gt;&quot; in order to parse the entire
   *     LHS. when necessary it will make itself another call to {@link #parseLHS
   *     parseLHS} to parse a region of the LHS (e.g. a
   *     &quot;(&quot;,&quot;)&quot; enclosed part.
   */
  FSMState parseLHS(FSMState startState, StringTokenizer st, String until)
       throws TokeniserException{

    FSMState currentState = startState;
    boolean orFound = false;
    List<FSMState> orList = new LinkedList<FSMState>();
    String token;
    token = skipIgnoreTokens(st);

    if(null == token) return currentState;

    FSMState newState;
    Integer typeId;
    UnicodeType uType;

    bigwhile: while(!token.equals(until)){
      if(token.equals("(")){//(..)
        newState = parseLHS(currentState, st,")");
      } else if(token.equals("\"")){//"unicode_type"
        String sType = parseQuotedString(st, "\"");
        newState = new FSMState(this);
        typeId = stringTypeIds.get(sType);

        if(null == typeId)
          throw new InvalidRuleException("Invalid type: \"" + sType + "\"");
        else uType = new UnicodeType(typeId.intValue());

        currentState.put(uType ,newState);
      } else {// a type with no quotes
        String sType = token;
        newState = new FSMState(this);
        typeId = stringTypeIds.get(sType);

        if(null == typeId)
          throw new InvalidRuleException("Invalid type: \"" + sType + "\"");
        else uType = new UnicodeType(typeId.intValue());

        currentState.put(uType ,newState);
      }
      //treat the operators
      token = skipIgnoreTokens(st);
      if(null == token) throw
        new InvalidRuleException("Tokeniser rule ended too soon!");

      if(token.equals("|")) {

        orFound = true;
        orList.add(newState);
        token = skipIgnoreTokens(st);
        if(null == token) throw
          new InvalidRuleException("Tokeniser rule ended too soon!");

        continue bigwhile;
      } else if(orFound) {//done parsing the "|"
        orFound = false;
        orList.add(newState);
        newState = new FSMState(this);
        Iterator<FSMState> orListIter = orList.iterator();

        while(orListIter.hasNext())
          orListIter.next().put(null, newState);
        orList.clear();
      }

      if(token.equals("+")) {

        newState.put(null,currentState);
        currentState = newState;
        newState = new FSMState(this);
        currentState.put(null,newState);
        token = skipIgnoreTokens(st);

        if(null == token) throw
          new InvalidRuleException("Tokeniser rule ended too soon!");
      } else if(token.equals("*")) {

        currentState.put(null,newState);
        newState.put(null,currentState);
        currentState = newState;
        newState = new FSMState(this);
        currentState.put(null,newState);
        token = skipIgnoreTokens(st);

        if(null == token) throw
          new InvalidRuleException("Tokeniser rule ended too soon!");
      }
      currentState = newState;
    }
    return currentState;
  } // parseLHS

  /** Parses from the given string tokeniser until it finds a specific
   * delimiter.
   * One use for this method is to read everything until the first quote.
   *
   * @param st a {@link java.util.StringTokenizer StringTokenizer} that
   *     provides the input
   * @param until a String representing the end delimiter.
   */
  String parseQuotedString(StringTokenizer st, String until)
    throws TokeniserException {

    String token;

    if(st.hasMoreElements()) token = st.nextToken();
    else return null;

    ///String type = "";
    StringBuffer type = new StringBuffer(Gate.STRINGBUFFER_SIZE);

    while(!token.equals(until)){
      //type += token;
      type.append(token);
      if(st.hasMoreElements())token = st.nextToken();
      else throw new InvalidRuleException("Tokeniser rule ended too soon!");
    }
    return type.toString();
  } // parseQuotedString

  /** Skips the ignorable tokens from the input returning the first significant
   * token.
   * The ignorable tokens are defined by {@link #ignoreTokens a set}
   */
  protected static String skipIgnoreTokens(StringTokenizer st){
    Iterator<String> ignorables;
    boolean ignorableFound = false;
    String currentToken;

    while(true){
      if(st.hasMoreTokens()){
        currentToken = st.nextToken();
        ignorables = ignoreTokens.iterator();
        ignorableFound = false;

        while(!ignorableFound && ignorables.hasNext()){
          if(currentToken.equals(ignorables.next()))
            ignorableFound = true;
        }

        if(!ignorableFound) return currentToken;
      } else return null;
    }
  }//skipIgnoreTokens

  /* Computes the lambda-closure (aka epsilon closure) of the given set of
   * states, that is the set of states that are accessible from any of the
   * states in the given set using only unrestricted transitions.
   * @return a set containing all the states accessible from this state via
   * transitions that bear no restrictions.
   */
  /**
   * Converts the finite state machine to a deterministic one.
   *
   * @param s
   */
  private AbstractSet<FSMState> lambdaClosure(Set<FSMState> s){

    //the stack/queue used by the algorithm
    LinkedList<FSMState> list = new LinkedList<FSMState>(s);

    //the set to be returned
    AbstractSet<FSMState> lambdaClosure = new HashSet<FSMState>(s);

    FSMState top;
    FSMState currentState;
    Set<FSMState> nextStates;
    Iterator<FSMState> statesIter;

    while(!list.isEmpty()) {
      top = list.removeFirst();
      nextStates = top.nextSet(null);

      if(null != nextStates){
        statesIter = nextStates.iterator();

        while(statesIter.hasNext()) {
          currentState = statesIter.next();
          if(!lambdaClosure.contains(currentState)){
            lambdaClosure.add(currentState);
            list.addFirst(currentState);
          }//if(!lambdaClosure.contains(currentState))
        }//while(statesIter.hasNext())

      }//if(null != nextStates)
    }
    return lambdaClosure;
  } // lambdaClosure

  /** Converts the FSM from a non-deterministic to a deterministic one by
   * eliminating all the unrestricted transitions.
   */
  void eliminateVoidTransitions() throws TokeniserException {

    //kalina:clear() faster than init() which is called with init()
    newStates.clear();
    Set<Set<FSMState>> sdStates = new HashSet<Set<FSMState>>();
    LinkedList<Set<FSMState>> unmarkedDStates = new LinkedList<Set<FSMState>>();
    DFSMState dCurrentState = new DFSMState(this);
    Set<FSMState> sdCurrentState = new HashSet<FSMState>();

    sdCurrentState.add(initialState);
    sdCurrentState = lambdaClosure(sdCurrentState);
    newStates.put(sdCurrentState, dCurrentState);
    sdStates.add(sdCurrentState);

    //find out if the new state is a final one
    Iterator<FSMState> innerStatesIter = sdCurrentState.iterator();
    String rhs;
    FSMState currentInnerState;
    Set<String> rhsClashSet = new HashSet<String>();
    boolean newRhs = false;

    while(innerStatesIter.hasNext()){
      currentInnerState = innerStatesIter.next();
      if(currentInnerState.isFinal()){
        rhs = currentInnerState.getRhs();
        rhsClashSet.add(rhs);
        dCurrentState.rhs = rhs;
        newRhs = true;
      }
    }

    if(rhsClashSet.size() > 1){
      Err.println("Warning, rule clash: " +  rhsClashSet +
                         "\nSelected last definition: " + dCurrentState.rhs);
    }

    if(newRhs)dCurrentState.buildTokenDesc();
    rhsClashSet.clear();
    unmarkedDStates.addFirst(sdCurrentState);
    dInitialState = dCurrentState;
    Set<FSMState> nextSet;

    while(!unmarkedDStates.isEmpty()){
      //Out.println("\n\n=====================" + unmarkedDStates.size());
      sdCurrentState = unmarkedDStates.removeFirst();
      for(int type = 0; type < maxTypeId; type++){
      //Out.print(type);
        nextSet = new HashSet<FSMState>();
        innerStatesIter = sdCurrentState.iterator();

        while(innerStatesIter.hasNext()){
          currentInnerState = innerStatesIter.next();
          Set<FSMState> tempSet = currentInnerState.nextSet(type);
          if(null != tempSet) nextSet.addAll(tempSet);
        }//while(innerStatesIter.hasNext())

        if(!nextSet.isEmpty()){
          nextSet = lambdaClosure(nextSet);
          dCurrentState = newStates.get(nextSet);

          if(dCurrentState == null){

            //we have a new DFSMState
            dCurrentState = new DFSMState(this);
            sdStates.add(nextSet);
            unmarkedDStates.add(nextSet);

            //check to see whether the new state is a final one
            innerStatesIter = nextSet.iterator();
            newRhs =false;

            while(innerStatesIter.hasNext()){
              currentInnerState = innerStatesIter.next();
              if(currentInnerState.isFinal()){
                rhs = currentInnerState.getRhs();
                rhsClashSet.add(rhs);
                dCurrentState.rhs = rhs;
                newRhs = true;
              }
            }

            if(rhsClashSet.size() > 1){
              Err.println("Warning, rule clash: " +  rhsClashSet +
                            "\nSelected last definition: " + dCurrentState.rhs);
            }

            if(newRhs)dCurrentState.buildTokenDesc();
            rhsClashSet.clear();
            newStates.put(nextSet, dCurrentState);
          }
          newStates.get(sdCurrentState).put(type,dCurrentState);
        } // if(!nextSet.isEmpty())

      } // for(byte type = 0; type < 256; type++)

    } // while(!unmarkedDStates.isEmpty())

  } // eliminateVoidTransitions

  /** Returns a string representation of the non-deterministic FSM graph using
   * GML (Graph modelling language).
   */
  public String getFSMgml(){
    String res = "graph[ \ndirected 1\n";
    ///String nodes = "", edges = "";
    StringBuffer nodes = new StringBuffer(Gate.STRINGBUFFER_SIZE),
                 edges = new StringBuffer(Gate.STRINGBUFFER_SIZE);

    Iterator<FSMState> fsmStatesIter = fsmStates.iterator();
    while (fsmStatesIter.hasNext()){
      FSMState currentState = fsmStatesIter.next();
      int stateIndex = currentState.getIndex();
      /*nodes += "node[ id " + stateIndex +
               " label \"" + stateIndex;
        */
        nodes.append("node[ id ");
        nodes.append(stateIndex);
        nodes.append(" label \"");
        nodes.append(stateIndex);

             if(currentState.isFinal()){
              ///nodes += ",F\\n" + currentState.getRhs();
              nodes.append(",F\\n" + currentState.getRhs());
             }
             ///nodes +=  "\"  ]\n";
             nodes.append("\"  ]\n");
      ///edges += currentState.getEdgesGML();
      edges.append(currentState.getEdgesGML());
    }
    res += nodes.toString() + edges.toString() + "]\n";
    return res;
  } // getFSMgml

  /** Returns a string representation of the deterministic FSM graph using
   * GML.
   */
  public String getDFSMgml() {
    String res = "graph[ \ndirected 1\n";
    ///String nodes = "", edges = "";
    StringBuffer nodes = new StringBuffer(Gate.STRINGBUFFER_SIZE),
                 edges = new StringBuffer(Gate.STRINGBUFFER_SIZE);

    Iterator<DFSMState> dfsmStatesIter = dfsmStates.iterator();
    while (dfsmStatesIter.hasNext()) {
      DFSMState currentState = dfsmStatesIter.next();
      int stateIndex = currentState.getIndex();
/*      nodes += "node[ id " + stateIndex +
               " label \"" + stateIndex;
*/
        nodes.append("node[ id ");
        nodes.append(stateIndex);
        nodes.append(" label \"");
        nodes.append(stateIndex);

             if(currentState.isFinal()){
///              nodes += ",F\\n" + currentState.getRhs();
              nodes.append(",F\\n" + currentState.getRhs());
             }
///             nodes +=  "\"  ]\n";
             nodes.append("\"  ]\n");
///      edges += currentState.getEdgesGML();
        edges.append(currentState.getEdgesGML());
    }
    res += nodes.toString() + edges.toString() + "]\n";
    return res;
  } // getDFSMgml

  /**
   * The method that does the actual tokenisation.
   */
  @Override
  public void execute() throws ExecutionException {
    interrupted = false;
    AnnotationSet annotationSet;
    //check the input
    if(document == null) {
      throw new ExecutionException(
        "No document to tokenise!"
      );
    }

    if(annotationSetName == null ||
       annotationSetName.equals("")) annotationSet = document.getAnnotations();
    else annotationSet = document.getAnnotations(annotationSetName);

    fireStatusChanged(
        "Tokenising " + document.getName() + "...");

    String content = document.getContent().toString();
    int length = content.length();
    int currentChar;
    int charsInCurrentCP = 1;

    DFSMState graphPosition = dInitialState;

    //the index of the first character of the token trying to be recognised
    int tokenStart = 0;

    DFSMState lastMatchingState = null;
    DFSMState nextState;
    String tokenString;
    int charIdx = 0;
    int oldCharIdx = 0;
    FeatureMap newTokenFm;

    while(charIdx < length){
      currentChar = content.codePointAt(charIdx);
      // number of chars we have to advance after processing this code point.
      // 1 in the vast majority of cases, but 2 where the code point is a
      // supplementary character represented as a surrogate pair.
      charsInCurrentCP = Character.isSupplementaryCodePoint(currentChar) ? 2 : 1;
      
//      Out.println(
//      currentChar + typesMnemonics[Character.getType(currentChar)+128]);
      nextState = graphPosition.next(typeIds.get(
                  new Integer(Character.getType(currentChar))).intValue());

      if( null != nextState ) {
        graphPosition = nextState;
        if(graphPosition.isFinal()) {
          lastMatchingState = graphPosition;
        }
        charIdx += charsInCurrentCP;
      } else {//we have a match!
        newTokenFm = Factory.newFeatureMap();

        if (null == lastMatchingState) {
          // no rule matches this character, so create a single-char
          // DEFAULT_TOKEN annotation covering it and start again after it
          charIdx  = tokenStart + charsInCurrentCP;
          tokenString = content.substring(tokenStart, charIdx);
          newTokenFm.put("type","UNKNOWN");
          newTokenFm.put(TOKEN_STRING_FEATURE_NAME, tokenString);
          newTokenFm.put(TOKEN_LENGTH_FEATURE_NAME,
                         Integer.toString(tokenString.length()));

          try {
            annotationSet.add(new Long(tokenStart),
                              new Long(charIdx),
                              "DEFAULT_TOKEN", newTokenFm);
          } catch (InvalidOffsetException ioe) {
            //This REALLY shouldn't happen!
            ioe.printStackTrace(Err.getPrintWriter());
          }
          // Out.println("Default token: " + tokenStart +
          //             "->" + tokenStart + " :" + tokenString + ";");
        } else {
          // we've reached the end of a string that the FSM recognised
          tokenString = content.substring(tokenStart, charIdx);
          newTokenFm.put(TOKEN_STRING_FEATURE_NAME, tokenString);
          newTokenFm.put(TOKEN_LENGTH_FEATURE_NAME,
                         Integer.toString(tokenString.length()));

          for(int i = 1; i < lastMatchingState.getTokenDesc().length; i++){
            newTokenFm.put(lastMatchingState.getTokenDesc()[i][0],
                           lastMatchingState.getTokenDesc()[i][1]);
          //Out.println(lastMatchingState.getTokenDesc()[i][0] + "=" +
          //                       lastMatchingState.getTokenDesc()[i][1]);
          }


          try {
            annotationSet.add(new Long(tokenStart),
                            new Long(charIdx),
                            lastMatchingState.getTokenDesc()[0][0], newTokenFm);
          } catch(InvalidOffsetException ioe) {
            //This REALLY shouldn't happen!
            throw new GateRuntimeException(ioe.toString());
          }

          // Out.println(lastMatchingState.getTokenDesc()[0][0] +
          //              ": " + tokenStart + "->" + lastMatch +
          //              " :" + tokenString + ";");
          //charIdx = lastMatch + 1;
        }

        // reset to initial state and start looking again from here
        lastMatchingState = null;
        graphPosition = dInitialState;
        tokenStart = charIdx;
      }

      if((charIdx - oldCharIdx > 256)){
        fireProgressChanged((100 * charIdx )/ length );
        oldCharIdx = charIdx;
        if(isInterrupted()) throw new ExecutionInterruptedException();
      }

    } // while(charIdx < length)

    if (null != lastMatchingState) {
      // we dropped off the end having found a match, annotate it
      tokenString = content.substring(tokenStart, charIdx);
      newTokenFm = Factory.newFeatureMap();
      newTokenFm.put(TOKEN_STRING_FEATURE_NAME, tokenString);
      newTokenFm.put(TOKEN_LENGTH_FEATURE_NAME,
                     Integer.toString(tokenString.length()));

      for(int i = 1; i < lastMatchingState.getTokenDesc().length; i++){
        newTokenFm.put(lastMatchingState.getTokenDesc()[i][0],
                       lastMatchingState.getTokenDesc()[i][1]);
      }


      try {
        annotationSet.add(new Long(tokenStart),
                          new Long(charIdx),
                          lastMatchingState.getTokenDesc()[0][0], newTokenFm);
      } catch(InvalidOffsetException ioe) {
        //This REALLY shouldn't happen!
        throw new GateRuntimeException(ioe.toString());
      }

    }

    reset();
    fireProcessFinished();
    fireStatusChanged("Tokenisation complete!");
  } // run

  /**
   * Sets the value of the <code>rulesURL</code> property which holds an URL
   * to the file containing the rules for this tokeniser.
   * @param newRulesURL
   */
  @CreoleParameter(defaultValue="resources/tokeniser/DefaultTokeniser.rules", comment="The URL to the rules file", suffixes="rules")
  public void setRulesURL(java.net.URL newRulesURL) {
    rulesURL = newRulesURL;
  }
  /**
   * Gets the value of the <code>rulesURL</code> property hich holds an
   * URL to the file containing the rules for this tokeniser.
   */
  public java.net.URL getRulesURL() {
    return rulesURL;
  }
    
  @RunTime
  @Optional
  @CreoleParameter(comment="The annotation set to be used for the generated annotations")
  public void setAnnotationSetName(String newAnnotationSetName) {
    annotationSetName = newAnnotationSetName;
  }
  /**    */
  public String getAnnotationSetName() {
    return annotationSetName;
  }
  public void setRulesResourceName(String newRulesResourceName) {
    rulesResourceName = newRulesResourceName;
  }
  public String getRulesResourceName() {
    return rulesResourceName;
  }
  
  @CreoleParameter(defaultValue="UTF-8", comment="The encoding used for reading the definitions")
  public void setEncoding(String newEncoding) {
    encoding = newEncoding;
  }
  public String getEncoding() {
    return encoding;
  }

  /** the annotations et where the new annotations will be adde
   */
  protected String annotationSetName;

  /** The initial state of the non deterministic machin
   */
  protected FSMState initialState;

  /** A set containng all the states of the non deterministic machine
   */
  protected Set<FSMState> fsmStates = new HashSet<FSMState>();

  /** The initial state of the deterministic machine
   */
  protected DFSMState dInitialState;

  /** A set containng all the states of the deterministic machine
   */
  protected Set<DFSMState> dfsmStates = new HashSet<DFSMState>();

  /** The separator from LHS to RH
   */
  static String LHStoRHS = ">";

  /** A set of string representing tokens to be ignored (e.g. blanks
   */
  static Set<String> ignoreTokens;

  /** maps from int (the static value on {@link java.lang.Character} to int
   * the internal value used by the tokeniser. The ins values used by the
   * tokeniser are consecutive values, starting from 0 and going as high as
   * necessary.
   * They map all the public static int members on{@link java.lang.Character}
   */
  public static final Map<Integer, Integer> typeIds;

  /** The maximum int value used internally as a type i
   */
  public static int maxTypeId;

  /** Maps the internal type ids to the type name
   */
  public static String[] typeMnemonics;

  /** Maps from type names to type internal id
   */
  public static final Map<String, Integer> stringTypeIds;

  /**
   * This property holds an URL to the file containing the rules for this tokeniser
   *
   */

  /**    */
  static protected String defaultResourceName =
                            "creole/tokeniser/DefaultTokeniser.rules";

  private String rulesResourceName;
  private java.net.URL rulesURL;
  private String encoding;

  //kalina: added this as method to minimise too many init() calls
  protected transient Map<Set<FSMState>, DFSMState> newStates = new HashMap<Set<FSMState>, DFSMState>();


  /** The static initialiser will inspect the class {@link java.lang.Character}
    * using reflection to find all the public static members and will map them
    * to ids starting from 0.
    * After that it will build all the static data: {@link #typeIds}, {@link
    * #maxTypeId}, {@link #typeMnemonics}, {@link #stringTypeIds}
    */
  static{
    Field[] characterClassFields;

    try{
      characterClassFields = Class.forName("java.lang.Character").getFields();
    }catch(ClassNotFoundException cnfe){
      throw new LuckyException("Could not find the java.lang.Character class!");
    }

    Collection<Field> staticFields = new LinkedList<Field>();
    // JDK 1.4 introduced directionality constants that have the same values as
    //character types; we need to skip those as well
    for(int i = 0; i< characterClassFields.length; i++)
      if(Modifier.isStatic(characterClassFields[i].getModifiers()) &&
         characterClassFields[i].getName().indexOf("DIRECTIONALITY") == -1)
        staticFields.add(characterClassFields[i]);

    Map<Integer, Integer> tempTypeIds = new HashMap<Integer, Integer>();
    maxTypeId = staticFields.size() -1;
    typeMnemonics = new String[maxTypeId + 1];
    Map<String, Integer> tempStringTypeIds = new HashMap<String, Integer>();

    
    
    Iterator<Field> staticFieldsIter = staticFields.iterator();
    Field currentField;
    int currentId = 0;
    String fieldName;

    try {
      while(staticFieldsIter.hasNext()){
        currentField = staticFieldsIter.next();
        if(currentField.getType().toString().equals("byte")){
          fieldName = currentField.getName();
          tempTypeIds.put(new Integer(currentField.getInt(null)),
                                    new Integer(currentId));
          typeMnemonics[currentId] = fieldName;
          tempStringTypeIds.put(fieldName, new Integer(currentId));
          currentId++;
        }
      }
    } catch(Exception e) {
      throw new LuckyException(e.toString());
    }
    
    typeIds = Collections.unmodifiableMap(tempTypeIds);
    stringTypeIds = Collections.unmodifiableMap(tempStringTypeIds);

    ignoreTokens = new HashSet<String>();
    ignoreTokens.add(" ");
    ignoreTokens.add("\t");
    ignoreTokens.add("\f");
  }

} // class DefaultTokeniser