/*
 * DefaultGazeteer.java
 *
 * Copyright (c) 1998-2005, The University of Sheffield.
 *
 * This file is part of GATE (see http://gate.ac.uk/), and is free
 * software, licenced under the GNU Library General Public License,
 * Version 2, June1991.
 *
 * A copy of this licence is included in the distribution in the file
 * licence.html, and is also available at http://gate.ac.uk/gate/licence.html.
 *
 * Valentin Tablan, 03/07/2000
 * borislav popov 24/03/2002
 *
 * $Id: DefaultGazetteer.java 17806 2014-04-11 09:10:02Z markagreenwood $
 */
package gate.creole.gazetteer;

import gate.AnnotationSet;
import gate.Factory;
import gate.FeatureMap;
import gate.Resource;
import gate.Utils;
import gate.creole.CustomDuplication;
import gate.creole.ExecutionException;
import gate.creole.ExecutionInterruptedException;
import gate.creole.ResourceInstantiationException;
import gate.creole.metadata.CreoleParameter;
import gate.creole.metadata.CreoleResource;
import gate.creole.metadata.Optional;
import gate.util.GateRuntimeException;
import gate.util.InvalidOffsetException;
import gate.util.Strings;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/** This component is responsible for doing lists lookup. The implementation is
 * based on finite state machines.
 * The phrases to be recognised should be listed in a set of files, one for
 * each type of occurrences.
 * The gazetteer is build with the information from a file that contains the set
 * of lists (which are files as well) and the associated type for each list.
 * The file defining the set of lists should have the following syntax:
 * each list definition should be written on its own line and should contain:
 * <ol>
 * <li>the file name (required) </li>
 * <li>the major type (required) </li>
 * <li>the minor type (optional)</li>
 * <li>the language(s) (optional) </li>
 * </ol>
 * The elements of each definition are separated by &quot;:&quot;.
 * The following is an example of a valid definition: <br>
 * <code>personmale.lst:person:male:english</code>
 * Each list file named in the lists definition file is just a list containing
 * one entry per line.
 * When this gazetteer will be run over some input text (a Gate document) it
 * will generate annotations of type Lookup having the attributes specified in
 * the definition file.
 */
@CreoleResource(name="ANNIE Gazetteer", comment="A list lookup component.", icon="gazetteer", helpURL="http://gate.ac.uk/userguide/sec:annie:gazetteer")
public class DefaultGazetteer extends AbstractGazetteer
                              implements CustomDuplication {

  private static final long serialVersionUID = -8976141132455436099L;

  public static final String
    DEF_GAZ_DOCUMENT_PARAMETER_NAME = "document";

  public static final String
    DEF_GAZ_ANNOT_SET_PARAMETER_NAME = "annotationSetName";

  public static final String
    DEF_GAZ_LISTS_URL_PARAMETER_NAME = "listsURL";

  public static final String
    DEF_GAZ_ENCODING_PARAMETER_NAME = "encoding";

  public static final String
    DEF_GAZ_CASE_SENSITIVE_PARAMETER_NAME = "caseSensitive";
  
  public static final String
    DEF_GAZ_LONGEST_MATCH_ONLY_PARAMETER_NAME = "longestMatchOnly";
  
  public static final String
    DEF_GAZ_FEATURE_SEPARATOR_PARAMETER_NAME = "gazetteerFeatureSeparator";

  /** The separator used for gazetteer entry features */
  protected String gazetteerFeatureSeparator;
  
  /** a map of nodes vs gaz lists */
  protected Map<LinearNode,GazetteerList> listsByNode;

  /** 
   * Build a gazetteer using the default lists from the gate resources
   */
  public DefaultGazetteer(){
  }
  
  /** Does the actual loading and parsing of the lists. This method must be
   * called before the gazetteer can be used
   */
  @Override
  public Resource init()throws ResourceInstantiationException{
    fsmStates = new HashSet<FSMState>();
    initialState = new FSMState(this);
    if(listsURL == null){
      throw new ResourceInstantiationException (
            "No URL provided for gazetteer creation!");
    }
    definition = new LinearDefinition();
    definition.setSeparator(Strings.unescape(gazetteerFeatureSeparator));
    definition.setURL(listsURL);
    definition.load();
    int linesCnt = definition.size();
    listsByNode = definition.loadLists();
    Iterator<LinearNode> inodes = definition.iterator();

    int nodeIdx = 0;
    LinearNode node;
    while (inodes.hasNext()) {
      node = inodes.next();
      fireStatusChanged("Reading " + node.toString());
      fireProgressChanged(++nodeIdx * 100 / linesCnt);
      readList(node,true);
    } // while iline
    fireProcessFinished();
    return this;
  }


  /** Reads one lists (one file) of phrases
   *
   * @param node the node
   * @param add if <b>true</b> will add the phrases found in the list to the ones
   *     recognised by this gazetteer, if <b>false</b> the phrases found in the
   *     list will be removed from the list of phrases recognised by this
   *     gazetteer.
   */
   protected void readList(LinearNode node, boolean add) 
       throws ResourceInstantiationException{
    String listName, majorType, minorType, languages,annotationType;
    if ( null == node ) {
      throw new ResourceInstantiationException(" LinearNode node is null ");
    }

    listName = node.getList();
    majorType = node.getMajorType();
    minorType = node.getMinorType();
    languages = node.getLanguage();
    annotationType = node.getAnnotationType();
    GazetteerList gazList = listsByNode.get(node);
    if (null == gazList) {
      throw new ResourceInstantiationException("gazetteer list not found by node");
    }

    Iterator<GazetteerNode> iline = gazList.iterator();
    
    // create default lookup for entries with no arbitrary features
    Lookup defaultLookup = new Lookup(listName,majorType, minorType, languages,annotationType);
    defaultLookup.list = node.getList();
    if ( null != mappingDefinition){
      MappingNode mnode = mappingDefinition.getNodeByList(defaultLookup.list);
      if (null!=mnode){
        defaultLookup.oClass = mnode.getClassID();
        defaultLookup.ontology = mnode.getOntologyID();
      }
    }//if mapping def
    
    Lookup lookup;
    String entry; // the actual gazetteer entry text
    while(iline.hasNext()){
      GazetteerNode gazNode = iline.next();
      entry = gazNode.getEntry();
      
      Map<String,Object> features = gazNode.getFeatureMap();
      if (features == null) {
        lookup = defaultLookup;
      } else {
        // create a new Lookup object with features
        lookup = new Lookup(listName, majorType, minorType, languages,annotationType);
        lookup.list = node.getList();
        if(null != mappingDefinition) {
          MappingNode mnode = mappingDefinition.getNodeByList(lookup.list);
          if(null != mnode) {
            lookup.oClass = mnode.getClassID();
            lookup.ontology = mnode.getOntologyID();
          }
        }// if mapping def
        lookup.features = features;
      } 
      
      if(add)addLookup(entry, lookup);
      else removeLookup(entry, lookup);
    }
  } // void readList(String listDesc)

  /** Adds one phrase to the list of phrases recognised by this gazetteer
   *
   * @param text the phrase to be added
   * @param lookup the description of the annotation to be added when this
   *     phrase is recognised
   */
  public void addLookup(String text, Lookup lookup) {
    char currentChar;
    FSMState currentState = initialState;
    FSMState nextState;
    boolean isSpace;

    for(int i = 0; i< text.length(); i++) {
        currentChar = text.charAt(i);
        isSpace = Character.isSpaceChar(currentChar) || Character.isWhitespace(currentChar);
        if(isSpace) currentChar = ' ';
        else currentChar = (caseSensitive.booleanValue()) ?
                          currentChar :
                          Character.toUpperCase(currentChar) ;
      nextState = currentState.next(currentChar);
      if(nextState == null){
        nextState = new FSMState(this);
        currentState.put(currentChar, nextState);
        if(isSpace) nextState.put(' ',nextState);
      }
      currentState = nextState;
    } //for(int i = 0; i< text.length(); i++)

    currentState.addLookup(lookup);
    //Out.println(text + "|" + lookup.majorType + "|" + lookup.minorType);

  } // addLookup

  /** Removes one phrase to the list of phrases recognised by this gazetteer
   *
   * @param text the phrase to be removed
   * @param lookup the description of the annotation associated to this phrase
   */
  public void removeLookup(String text, Lookup lookup) {
    char currentChar;
    FSMState currentState = initialState;
    FSMState nextState;

    for(int i = 0; i< text.length(); i++) {
        currentChar = text.charAt(i);
        if ( Character.isSpaceChar(currentChar) || Character.isWhitespace(currentChar) ) currentChar = ' ';
        nextState = currentState.next(currentChar);
        if(nextState == null) return;//nothing to remove
        currentState = nextState;
    } //for(int i = 0; i< text.length(); i++)
    currentState.removeLookup(lookup);
  } // removeLookup

  /** Returns a string representation of the deterministic FSM graph using
   * GML.
   */
  public String getFSMgml() {
    String res = "graph[ \ndirected 1\n";
    StringBuffer nodes = new StringBuffer(gate.Gate.STRINGBUFFER_SIZE),
                edges = new StringBuffer(gate.Gate.STRINGBUFFER_SIZE);
    Iterator<FSMState> fsmStatesIter = fsmStates.iterator();
    while (fsmStatesIter.hasNext()){
      FSMState currentState = fsmStatesIter.next();
      int stateIndex = currentState.getIndex();
      nodes.append("node[ id ");
      nodes.append(stateIndex);
      nodes.append(" label \"");
      nodes.append(stateIndex);

             if(currentState.isFinal()){
              nodes.append(",F\\n");
              nodes.append(currentState.getLookupSet());
             }
             nodes.append("\"  ]\n");
      edges.append(currentState.getEdgesGML());
    }
    res += nodes.toString() + edges.toString() + "]\n";
    return res;
  } // getFSMgml


  /**
   * Tests whether a character is internal to a word (i.e. if it's a letter or
   * a combining mark (spacing or not)).
   * @param ch the character to be tested
   * @return a boolean value
   */
  public static boolean isWordInternal(char ch){
    return Character.isLetter(ch) ||
           Character.getType(ch) == Character.COMBINING_SPACING_MARK ||
           Character.getType(ch) == Character.NON_SPACING_MARK;
  }

  /**
   * This method runs the gazetteer. It assumes that all the needed parameters
   * are set. If they are not, an exception will be fired.
   */
  @Override
  public void execute() throws ExecutionException{
    interrupted = false;
    AnnotationSet annotationSet;
    //check the input
    if(document == null) {
      throw new ExecutionException(
        "No document to process!"
      );
    }

    if(annotationSetName == null ||
       annotationSetName.equals("")) annotationSet = document.getAnnotations();
    else annotationSet = document.getAnnotations(annotationSetName);

    fireStatusChanged("Performing look-up in " + document.getName() + "...");
    String content = document.getContent().toString();
    int length = content.length();
    char currentChar;
    FSMState currentState = initialState;
    FSMState nextState;
    FSMState lastMatchingState = null;
    int matchedRegionEnd = 0;
    int matchedRegionStart = 0;
    int charIdx = 0;
    int oldCharIdx = 0;

    while(charIdx < length) {
      currentChar = content.charAt(charIdx);
      if( Character.isSpaceChar(currentChar) || Character.isWhitespace(currentChar) ) currentChar = ' ';
      else currentChar = caseSensitive.booleanValue() ?
                          currentChar :
                          Character.toUpperCase(currentChar);
      nextState = currentState.next(currentChar);
      if(nextState == null) {
        //the matching stopped
        //if we had a successful match then act on it;
        if(lastMatchingState != null){
          createLookups(lastMatchingState, matchedRegionStart, matchedRegionEnd, 
                  annotationSet);
          lastMatchingState = null;
        }
        //reset the FSM
        charIdx = matchedRegionStart + 1;
        matchedRegionStart = charIdx;
        currentState = initialState;
      } else{//go on with the matching
        currentState = nextState;
        //if we have a successful state then store it
        if(currentState.isFinal() &&
           (
            (!wholeWordsOnly.booleanValue())
             ||
            ((matchedRegionStart == 0 ||
             !isWordInternal(content.charAt(matchedRegionStart - 1)))
             &&
             (charIdx + 1 >= content.length()   ||
             !isWordInternal(content.charAt(charIdx + 1)))
            )
           )
          ){
          //we have a new match
          //if we had an existing match and we need to annotate prefixes, then 
          //apply it
          if(!longestMatchOnly && lastMatchingState != null){
            createLookups(lastMatchingState, matchedRegionStart, 
                    matchedRegionEnd, annotationSet);
          }
          matchedRegionEnd = charIdx;
          lastMatchingState = currentState;
        }
        charIdx ++;
        if(charIdx == content.length()){
          //we can't go on, use the last matching state and restart matching
          //from the next char
          if(lastMatchingState != null){
            //let's add the new annotation(s)
            createLookups(lastMatchingState, matchedRegionStart, 
                    matchedRegionEnd, annotationSet);
            lastMatchingState = null;
          }
          //reset the FSM
          charIdx = matchedRegionStart + 1;
          matchedRegionStart = charIdx;
          currentState = initialState;
        }
      }
      //fire the progress event
      if(charIdx - oldCharIdx > 256) {
        fireProgressChanged((100 * charIdx )/ length );
        oldCharIdx = charIdx;
        if(isInterrupted()) throw new ExecutionInterruptedException(
            "The execution of the " + getName() +
            " gazetteer has been abruptly interrupted!");
      }
    } // while(charIdx < length)
    //we've finished. If we had a stored match, then apply it.
    if(lastMatchingState != null) {
      createLookups(lastMatchingState, matchedRegionStart, 
              matchedRegionEnd, annotationSet);
    }
    fireProcessFinished();
    fireStatusChanged("Look-up complete!");
  } // execute


  /**
   * Creates the Lookup annotations according to a gazetteer match.
   * @param matchingState the final FSMState that was reached while matching. 
   * @param matchedRegionStart the start of the matched text region.
   * @param matchedRegionEnd the end of the matched text region.
   * @param annotationSet the annotation set where the new annotations should 
   * be added.
   */
  protected void createLookups(FSMState matchingState, long matchedRegionStart, 
          long matchedRegionEnd, AnnotationSet annotationSet){
    Iterator<Lookup> lookupIter = matchingState.getLookupSet().iterator();
    while(lookupIter.hasNext()) {
      Lookup currentLookup = lookupIter.next();
      FeatureMap fm = Factory.newFeatureMap();
      fm.put(LOOKUP_MAJOR_TYPE_FEATURE_NAME, currentLookup.majorType);
      if (null!= currentLookup.oClass && null!=currentLookup.ontology){
        fm.put(LOOKUP_CLASS_FEATURE_NAME,currentLookup.oClass);
        fm.put(LOOKUP_ONTOLOGY_FEATURE_NAME,currentLookup.ontology);
      }

      if(null != currentLookup.minorType)
        fm.put(LOOKUP_MINOR_TYPE_FEATURE_NAME, currentLookup.minorType);
      if(null != currentLookup.languages)
        fm.put(LOOKUP_LANGUAGE_FEATURE_NAME, currentLookup.languages);      
      if(null != currentLookup.features) {
        fm.putAll(currentLookup.features);
      }
      try{
//        if(currentLookup.annotationType==null || "".equals(currentLookup.annotationType)){
//          annotationSet.add(new Long(matchedRegionStart),
//                          new Long(matchedRegionEnd + 1),
//                          LOOKUP_ANNOTATION_TYPE,
//                          fm);
//        }else{
          annotationSet.add(new Long(matchedRegionStart),
                          new Long(matchedRegionEnd + 1),
                          currentLookup.annotationType, //this pojo attribute will have Lookup as a default tag.
                          fm);
       // }
      } catch(InvalidOffsetException ioe) {
        throw new GateRuntimeException(ioe.toString());
      }
    }//while(lookupIter.hasNext())
  }
  
  /** The initial state of the FSM that backs this gazetteer
   */
  protected FSMState initialState;

  /** A set containing all the states of the FSM backing the gazetteer
   */
  protected Set<FSMState> fsmStates;

  /**lookup <br>
   * @param singleItem a single string to be looked up by the gazetteer
   * @return set of the Lookups associated with the parameter*/
  @Override
  public Set<Lookup> lookup(String singleItem) {
    char currentChar;
    Set<Lookup> set = new HashSet<Lookup>();
    FSMState currentState = initialState;
    FSMState nextState;

    for(int i = 0; i< singleItem.length(); i++) {
        currentChar = singleItem.charAt(i);
        if ( Character.isSpaceChar(currentChar) || Character.isWhitespace(currentChar) ) currentChar = ' ';
        nextState = currentState.next(currentChar);
        if(nextState == null) {
          return set;
        }
        currentState = nextState;
    } //for(int i = 0; i< text.length(); i++)
    set = currentState.getLookupSet();
    return set;
  }

  @Override
  public boolean remove(String singleItem) {
    char currentChar;
    FSMState currentState = initialState;
    FSMState nextState;

    for(int i = 0; i< singleItem.length(); i++) {
        currentChar = singleItem.charAt(i);
        if ( Character.isSpaceChar(currentChar) || Character.isWhitespace(currentChar) ) currentChar = ' ';
        nextState = currentState.next(currentChar);
        if(nextState == null) {
          return false;
        }//nothing to remove
        currentState = nextState;
    } //for(int i = 0; i< text.length(); i++)
    currentState.lookupSet = new HashSet<Lookup>();
    return true;
  }

  @Override
  public boolean add(String singleItem, Lookup lookup) {
    addLookup(singleItem,lookup);
    return true;
  }
  
  /**
   * Use a {@link SharedDefaultGazetteer} to duplicate this gazetteer
   * by sharing the internal FSM rather than re-loading the lists.
   */
  @Override
  public Resource duplicate(Factory.DuplicationContext ctx)
          throws ResourceInstantiationException {
    return Factory.createResource(SharedDefaultGazetteer.class.getName(),
            Utils.featureMap(
                    SharedDefaultGazetteer.SDEF_GAZ_BOOTSTRAP_GAZETTEER_PROPERTY_NAME,
                    this),
            Factory.duplicate(this.getFeatures(), ctx),
            this.getName());
  }


  public static interface Iter
  {
      public boolean hasNext();
      public char next();
  } // iter class

  /**
   * class implementing the map using binary search by char as key
   * to retrieve the corresponding object.
   */
  public static class CharMap implements Serializable
  {
    private static final long serialVersionUID = 4192829422957074447L;

    char[] itemsKeys = null;
      Object[] itemsObjs = null;

      /**
       * resize the containers by one, leaving empty element at position 'index'
       */
      void resize(int index)
      {
          int newsz = itemsKeys.length + 1;
          char[] tempKeys = new char[newsz];
          Object[] tempObjs = new Object[newsz];
          System.arraycopy(itemsKeys, 0, tempKeys, 0, index);
          System.arraycopy(itemsObjs, 0, tempObjs, 0, index);
          System.arraycopy(itemsKeys, index, tempKeys, index + 1, newsz - index - 1);
          System.arraycopy(itemsObjs, index, tempObjs, index + 1, newsz - index - 1);

          itemsKeys = tempKeys;
          itemsObjs = tempObjs;
      } // resize

  /**
   * get the object from the map using the char key
   */
      Object get(char key)
      {
          if (itemsKeys == null) return null;
          int index = Arrays.binarySearch(itemsKeys, key);
          if (index<0)
              return null;
          return itemsObjs[index];
      }
  /**
   * put the object into the char map using the char as the key
   */
      Object put(char key, Object value)
      {
          if (itemsKeys == null)
          {
              itemsKeys = new char[1];
              itemsKeys[0] = key;
              itemsObjs = new Object[1];
              itemsObjs[0] = value;
              return value;
          }// if first time
          int index = Arrays.binarySearch(itemsKeys, key);
          if (index<0)
          {
              index = ~index;
              resize(index);
              itemsKeys[index] = key;
              itemsObjs[index] = value;
          }
          return itemsObjs[index];
      } // put

  }// class CharMap

  /**
   * @return the gazetteerFeatureSeparator
   */
  public String getGazetteerFeatureSeparator() {
    return gazetteerFeatureSeparator;
  }

  /**
   * @param gazetteerFeatureSeparator the gazetteerFeatureSeparator to set
   */
  @Optional
  @CreoleParameter(comment="The character used to separate features for entries in gazetteer lists. Accepts strings like &quot;\t&quot; and will unescape it to the relevant character. If not specified, this gazetteer does not support extra features.",defaultValue=":")
  public void setGazetteerFeatureSeparator(String gazetteerFeatureSeparator) {
    this.gazetteerFeatureSeparator = gazetteerFeatureSeparator;
  }
   
} // DefaultGazetteer
