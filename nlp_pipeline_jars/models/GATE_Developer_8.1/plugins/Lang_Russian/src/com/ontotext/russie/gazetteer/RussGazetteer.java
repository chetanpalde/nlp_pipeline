/**
 * RussGazetteer.java This one is based on the HashGazetteer with additional
 * features : ALL-CAPS recognition; multiple overlapping lookups generation.
 * <p>
 * Title: RussIE
 * </p>
 * <p>
 * Description: Russian Information Extraction based on GATE
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company: Ontotext Lab.
 * </p>
 * 
 * @author unascribed
 * @version 1.0 This file is a part of the processing resources provided by
 *          OntoText Lab. a part of Sirma Artifical Intelligence Labs. the
 *          software and this file are licenced. A copy of the licence is
 *          included in the distribution in the file licence.ontotext.html, and
 *          is also available at
 *          http://www.ontotext.com/gate/licence.ontotext.html borislav popov,
 *          08/11/2001 $Id: RussGazetteer.java 16342 2013-03-13 17:11:12Z ian $
 */

package com.ontotext.russie.gazetteer;

import gate.AnnotationSet;
import gate.Factory;
import gate.FeatureMap;
import gate.Resource;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;
import gate.creole.gazetteer.AbstractGazetteer;
import gate.creole.gazetteer.GazetteerException;
import gate.creole.gazetteer.GazetteerList;
import gate.creole.gazetteer.LinearDefinition;
import gate.creole.gazetteer.LinearNode;
import gate.creole.gazetteer.Lookup;
import gate.creole.gazetteer.MappingNode;
import gate.creole.metadata.CreoleParameter;
import gate.creole.metadata.CreoleResource;
import gate.creole.metadata.HiddenCreoleParameter;
import gate.creole.metadata.RunTime;
import gate.creole.metadata.Sharable;
import gate.util.InvalidOffsetException;
import gate.util.LuckyException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ontotext.russie.RussIEConstants;

/**
 * RussGazetteer.java
 * 
 * @author borislav popov
 * @version 1.0
 */
@CreoleResource(name = "Russian Gazetteer", icon = "shefGazetteer",
    comment = "Customised version of the hash gazetteer",
    helpURL = "http://gate.ac.uk/userguide/sec:misc-creole:language-plugins:russian")
@SuppressWarnings({"rawtypes","unchecked"})
public class RussGazetteer extends AbstractGazetteer implements RussIEConstants {

  private static final long serialVersionUID = -5174914553200046785L;

  /**
   * Debug flag
   */
  protected static final boolean DEBUG = false;

  /**
   * majorType feature static representation
   */
  protected static final String MAJOR_TYPE_STR = "majorType";

  /**
   * minorType feature static representation
   */
  protected static final String MINOR_TYPE_STR = "minorType";

  /**
   * language feature static representation
   */
  protected static final String LANGUAGE = "language";

  /**
   * "Lookup" static
   */
  protected static final String LOOKUP = "Lookup";

  /**
   * fireStatusDoingLookupStr
   */
  protected static final String DOING_LOOKUP_IN = "Doing lookup in ";

  /**
   * empty string
   */
  protected static final String EMPTY_STR = "";

  /**
   * dots ... string
   */
  protected static final String DOTS = "...";

  /**
   * \\ string
   */
  protected static final String SLASH_SLASH = "\\";

  /**
   * "Reading " string
   */
  protected static final String READING = "Reading ";

  /**
   * A dot string = "."
   */
  protected static final String DOT = ".";

  /** a map of nodes vs gaz lists */
  protected Map listsByNode;

  /**
   * a list of all the maps representing the first words, first_secind phrases,
   * etc. each map's value might be an ArrayList of Lookup objects specifying
   * categories tied to this word/phrase.
   */
  protected List<Map> mapsList;

  /**
   * size of the mapsList
   */
  protected int mapsListSize = 0;

  /**
   * a list of references to Lookup objs representing the categories.
   */
  protected ArrayList<Lookup> categoryList = null;

  /**
   * Builds a gazetter using the default lists from the GATE resources {see
   * init()}
   */
  public RussGazetteer() {
  }

  /**
   * Does the actual loading and parsing of the lists. This method must be
   * called before the gazetteer can be used.
   * 
   * @throws ResourceInstantiationException
   * @return returns this resource
   */
  public Resource init() throws ResourceInstantiationException {
    if(mapsList != null) {
      // this is a duplicate
      mapsListSize = mapsList.size();
    } else {
      mapsList = new ArrayList<Map>(10);
      // check if there's a list URL
      if(listsURL == null) { throw new ResourceInstantiationException(
        "No URL provided for gazetteer creation!"); } // if

      try {
        definition = new LinearDefinition();
        definition.setURL(listsURL);
        definition.load();
        int linesCnt = definition.size();
        listsByNode = definition.loadLists();

        // allocate the hashmap for the first words from the phrases
        mapsList.add(new HashMap(linesCnt * 10));
        mapsListSize = mapsList.size();

        // allocate the category Map with optimal initial capacity & load factor
        categoryList = new ArrayList<Lookup>(linesCnt + 1);

        Iterator<LinearNode> inodes = definition.iterator();
        LinearNode node;
        int nodeIdx = 0;
        while(inodes.hasNext()) {
          node = inodes.next();
          fireStatusChanged(READING + node.toString());
          fireProgressChanged(++nodeIdx * 100 / linesCnt);
          readList(node, true);
        } // while
        fireProcessFinished();
      } catch(Exception x) {
        throw new ResourceInstantiationException(x);
      } // catch
    }
    return this;
  } // Resource init()throws ResourceInstantiationException

  /**
   * Re-initialize this gazetteer by re-loading the configuration.
   */
  public void reInit() throws ResourceInstantiationException {
    mapsList = null;
    categoryList = null;
    init();
  }


  /**
   * gets the phrases/lines of a gazetteer list stores them in the maps opposed
   * to a lookup.
   * 
   * @param node
   *          a linear node(line from the linear definition)
   * @param add
   * @add if <b>true</b> will add the phrases found in the list to the ones
   *      recognised by this gazetter, if <b>false</b> the phrases found in the
   *      list will be removed from the list of phrases recognised by this
   *      gazetteer.
   * @throws FileNotFoundException
   * @throws IOException
   * @throws GazetteerException
   * @return void
   */
  void readList(LinearNode node, boolean add) throws GazetteerException {

    String listName, majorType, minorType, languages;
    if(null == node) { throw new GazetteerException(" LinearNode node is null "); }

    listName = node.getList();
    majorType = node.getMajorType();
    minorType = node.getMinorType();
    languages = node.getLanguage();

    GazetteerList gazList = (GazetteerList)listsByNode.get(node);
    if(null == gazList) { throw new GazetteerException(
      "gazetteer list not found by node"); }

    // create a lookup object for the current category
    Lookup lookup = new Lookup(listName, majorType, minorType, languages);
    if(null != mappingDefinition) {
      MappingNode mnode = mappingDefinition.getNodeByList(listName);
      if(null != mnode) {
        lookup.oClass = mnode.getClassID();
        lookup.ontology = mnode.getOntologyID();
      }
    }// if mapping def
    lookup.list = listName;

    Iterator iline = gazList.iterator();
    String line;

    // add the following lines to the gazetteer
    if(add) {
      while(iline.hasNext()) {
        line = iline.next().toString();
        this.add(line, lookup);

      } // while there are lines to be processed

      // remove the following lines from the gazetteer
    } else {
      while(iline.hasNext()) {
        // currently no implementation of remove
        line = iline.next().toString();
      } // while there are lines to be processed
    } // else remove

  } // void readList(String listDesc)

  /**
   * This method runs the gazetteer. It parses the document and looks-up the
   * parsed phrases from the maps, in which the phrases vs. annotations are set,
   * in order to generate an annotation set. It assumes that all the needed
   * parameters are set. If they are not, an exception will be fired.
   */
  public void execute() throws ExecutionException {
    AnnotationSet annotationSet;
    // check the input
    if(document == null) { throw new ExecutionException(
      "No document to process!"); } // if document is null

    if(annotationSetName == null || annotationSetName.equals(EMPTY_STR))
      annotationSet = document.getAnnotations();
    else annotationSet = document.getAnnotations(annotationSetName);

    fireStatusChanged(DOING_LOOKUP_IN + document.getSourceUrl().getFile() +
      DOTS);

    // get the content of the document and its length
    String content = document.getContent().toString();

    // init some params
    int length = content.length();
    int matchedRegionEnd = 0;
    int matchedRegionStart = 0;
    // word start
    int iwordStart = 0;

    int iend = 0;
    int secondWordStart = 0;
    String phrase = EMPTY_STR;
    int mapIndex = 0;
    FeatureMap fm;
    Map currentMap = new HashMap();
    List currentLookup = null;
    // whether the current word is the first in the phrase
    boolean firstWord = true;
    boolean punctuationZone = false;
    // letter to number or number to letter transition zone
    boolean l2nORn2lZone = false;
    char currentChar = 0;
    int typeWeight = 0;

    // note that the code within the next cycle is overwhelmed by complexity
    // this said status was reached because what actually this cycle does
    // is ad hoc tokenization of ... guess what ... phrases
    // if you're still not sure this is a complex task-here are some examples :
    // Cable & Wireless
    // Moody's
    // C.V.
    // C.de R.L.
    // C.por A.
    // AG &AMP; Co KG
    // G.S.C.
    // S.A.R.L.
    // etc...
    // so in this cycle we monitor several flags and several indexes
    // they all should speak for themsleves e.g. boolean firstWord
    // boolean punctuationZone tells us whether we're within the context of a
    // punctuation sign (e.g. '.','&',';')
    // length is the length of the doc.
    // iwordStart is the starting index within the doc of the current 'word'
    // being processed
    // iend is the end of the current word/phrase
    // content is the content of the doc
    // mapIndex is the index of the current map, this is also the index of the
    // current 'part' of the phrase(parts are groups of characters separated by
    // whitespaces,and in some cases by punctuation)
    // matchedRegion(Start/End) denote the start/emd of an already looked-up
    // phrase( annotated )
    // secondWordStart is the index of the second word in the currently
    // processed phrase
    // l2nORn2lZone indicates whether we're in a zone where
    // transition from letter to number or number to letter is present
    // e.g. GBP100 the "P1" is such a zone
    // typeWeight is the summed weight of the types of the last two chars
    while(iwordStart < length) {

      if(firstWord)
        // ultimately : starts the new lookup operation from
        // the second word of the previous phrase
        iwordStart = secondWordStart;
      else iwordStart = iend;

      // additional check
      if(iwordStart >= length) break;

      // get the beginning of the word
      while(iwordStart < length &&
        (Character.isWhitespace(content.charAt(iwordStart)) || isWhiteSpacePunctuation(content
          .charAt(iwordStart)))) {
        iwordStart++;
      } // while find start of word
      // get the end of the word
      iend = iwordStart + 1;
      // tired of all these checks ?
      // bro why you bother to look @ this code @ all ???
      if(iend >= length) break;

      // do while punctuation
      do {
        int currCharInt;
        if(punctuationZone) {
          currentChar = content.charAt(iend);
        } else while(iend < length &&
          (Character.isLetterOrDigit(currentChar = content.charAt(iend)) ||
          // handling for ch and etc. cyrillic letters that fail the above check
          ((215 == (currCharInt = currentChar)) || (currCharInt == 168) ||
            (currCharInt == 247) || (currCharInt == 184))) ||
          ((isDashOrQuotePunctuation(currentChar)) && (Character
            .isWhitespace(content.charAt(iend - 1)) || isWhiteSpacePunctuation(content
            .charAt(iend - 1))))) {

          // check whether the neighbouring chars are letter number
          // or number letter
          typeWeight =
            Character.getType(currentChar) +
              Character.getType(content.charAt(iend - 1));
          if(l2nORn2lZone = (typeWeight == 10) || (typeWeight == 11)) {
            break;
          } // if l2nORn2lZone

          iend++;
        } // while find end of word

        // build phrase
        if(firstWord) {
          phrase = content.substring(iwordStart, iend);
          // maintain the case of a token without whitespaces
          // but beginning with punctuation e.g. : "The or &BT
          // in this case we should set the start of the next word to
          // iwordStart + 1;
          // it is the same when when l2nORn2lZone = true

          if((isDashOrQuotePunctuation(content.charAt(iwordStart))) ||
            l2nORn2lZone) {
            secondWordStart = iwordStart + 1;
          } else {
            secondWordStart = iend;
          } // else

          matchedRegionStart = iwordStart;
          mapIndex = 0;
          firstWord = false;

        } else {
          if(punctuationZone || l2nORn2lZone) {
            // close a punctuation zone or a l2nORn2lZone ?
            if(Character.isWhitespace(currentChar) ||
              isWhiteSpacePunctuation(currentChar)) {
              punctuationZone = false;
              l2nORn2lZone = false;

              mapIndex++;

              break;
            } // if it's whitespace
            else {
              phrase = phrase + currentChar;
              iend++;
            } // still in punctuation zone
          } else {

            phrase = phrase + ' ' + content.substring(iwordStart, iend);
          } // not a punctuation zone neither l2nORn2lZone
        } // not a first word

        // determine punctuatuion zone
        if(isDashOrQuotePunctuation(currentChar) &&
          !Character.isWhitespace(content.charAt(iend - 1))) {
          firstWord = false;
        } // if punctuation

        // determine l2nORn2lZone
        typeWeight =
          Character.getType(currentChar) +
            Character.getType(content.charAt(iend - 1));
        if((typeWeight == 10) || (typeWeight == 11)) {
          l2nORn2lZone = true;
          firstWord = false;
        } // if typeWeight ...

        // check mapindex's validity
        if(mapIndex >= mapsListSize) {
          firstWord = true;
          punctuationZone = false;

          l2nORn2lZone = false;

          continue;
        } // if mapindex out of bounds

        // try to find it in the dark cave ...
        currentMap = mapsList.get(mapIndex);
        // if found in current map then set matchedRegion
        phrase = trunxSuffixVowelsFromPhrase(phrase);
        if(currentMap.containsKey(phrase)) {
          currentLookup = (ArrayList)currentMap.get(phrase);
          if(null != currentLookup) {
            matchedRegionEnd = iend;
            // generate lookups for the phrase so far
            {
              Iterator lookupIter = currentLookup.iterator();
              Lookup lookup;
              while(lookupIter.hasNext()) {
                lookup = (Lookup)lookupIter.next();
                fm = Factory.newFeatureMap();

                fm.put(MAJOR_TYPE_STR, lookup.majorType);
                if(null != lookup.minorType)
                  fm.put(MINOR_TYPE_STR, lookup.minorType);
                if(null != lookup.languages)
                  fm.put(LANGUAGE, lookup.languages);

                try {
                  annotationSet.add(new Long(matchedRegionStart), new Long(
                    matchedRegionEnd), LOOKUP_ANNOTATION_TYPE, fm);
                } catch(InvalidOffsetException ioe) {
                  throw new LuckyException(ioe.toString());
                } // catchx

              }// while(lookupIter.hasNext())
            } // generate lookups for the phraseso far
          } // if not null lookup
        } else {

          if(!punctuationZone && !l2nORn2lZone) {

            // the map doesn't contain the key
            iend = secondWordStart;
            firstWord = true;
            continue;
          } // if critical iteration
        } // else

        // jump to the next map only if not within a punctuation context
        // neither in a l2nORn2l Zone
        if(!punctuationZone && !l2nORn2lZone) ++mapIndex;

        // if the current map index reached the size of the map list
        if(mapIndex >= mapsListSize) {
          firstWord = true;
          punctuationZone = false;
          continue;
        } // if mapIndex out of bounds

      } while((punctuationZone || l2nORn2lZone) && iend < length);

      // if end of boundaries for iend reached then set exclusively firstWord
      // this will cause the program to start from secondWordStart or
      // matchedRegionEnd indexes.
      if(iend >= length || iwordStart >= length) {
        iend = secondWordStart;
        firstWord = true;
        // last change
        punctuationZone = false;
      } // if iend out of boundaries

    } // while within content

    fireProcessFinished();
    fireStatusChanged("Gazetteer processing finished!");

  } // execute ()

  public Set lookup(String singleItem) {
    Set result = null;
    for(int li = 0; li < mapsListSize; li++) {
      Map list = mapsList.get(li);
      if(list.containsKey(singleItem)) {
        ArrayList lookupList = (ArrayList)list.get(singleItem);
        if(lookupList != null && lookupList.size() > 0) {
          result = new HashSet(lookupList);
          break;
        }
      }
    } // for lists
    return result;
  }

  public boolean remove(String singleItem) {
    boolean isRemoved = false;
    for(int i = 0; i < mapsListSize; i++) {
      Map map = mapsList.get(i);
      if(map.containsKey(singleItem)) {
        map.remove(singleItem);
        isRemoved = true;
        break;
      }
    } // for lists
    return isRemoved;
  }

  public boolean add(String singleItem, Lookup lookup) {

    // ALL-UPPER-CASE SUPPORT
    String upper = singleItem.toUpperCase();
    if(!upper.equals(singleItem)) {
      this.add(upper, lookup);
    } // avoid endless recursion

    // if the item is not with first capital - make it with first capital and
    // add it
    if(singleItem.length() > 1) {
      String firstLetter = singleItem.substring(0, 1);
      if(!firstLetter.equals(firstLetter.toUpperCase())) {
        this.add(firstLetter.toUpperCase() + singleItem.substring(1), lookup);
      }
    }

    // stems the word (or words in phrase) and adds it as a new gaz entry if it
    // differs from the currently being added phrase/word
    {
      String stem = trunxSuffixVowelsFromPhrase(singleItem);
      if(!stem.equals(singleItem)) {
        this.add(stem, lookup);
      }
    }

    // category key
    ArrayList<Lookup> key = new ArrayList<Lookup>(1);

    // add the lookup to the current key
    key.add(lookup);

    // add the lookup to the category list
    categoryList.add(lookup);

    // init some params

    String line = singleItem;
    int mapIndex = -1;
    String word = null;
    List<Lookup> oldKey = null;
    Map<String, List<Lookup>> currentMap = new HashMap<String, List<Lookup>>();
    int length = 0;

    line = singleItem;
    mapIndex = -1;
    line = line.trim();
    length = line.length();
    for(int lineIndex = 0; lineIndex < length; lineIndex++) {
      if((lineIndex + 1 == length) ||
        (Character.isWhitespace(line.charAt(lineIndex)))) {
        // if not whitespace but end of line then the index should equal the
        // length
        if(lineIndex + 1 == length) lineIndex = length;
        // get the word

        word = line.substring(0, lineIndex).trim();

        // if the map doesn't exist : create it
        ++mapIndex;
        if(mapsListSize <= mapIndex) {
          mapsList.add(new HashMap());
          mapsListSize++;
        } // if the map doesn't exist

        // get the map and add the word to the map
        currentMap = (mapsList.get(mapIndex));
        // try to get the current word
        // if there isn't such a word : add it with null key.
        if(!currentMap.containsKey(word)) {
          currentMap.put(word, null);

        } // add the word

      } // if whitespace

    } // for line iterate

    // !!! put the category key in the last map
    oldKey = currentMap.get(word);
    if(null == oldKey) {
      currentMap.put(word, key);
    } else {
      // merge the two arraylists
      // and check to avoid duplicity of lookups
      // note that key's length is 1
      ArrayList<Lookup> mergedKey = new ArrayList<Lookup>(oldKey);
      boolean duplicity = false;
      for(int i = 0; i < oldKey.size(); i++) {
        duplicity = mergedKey.get(i).equals(key.get(0));
      } // for i
      if(!duplicity) mergedKey.add(key.get(0));
      // put the merged key in the map
      currentMap.put(word, mergedKey);
    } // else

    return true;

  } // add

  private boolean isDashOrQuotePunctuation(char ch) {
    int type = Character.getType(ch);
    if(Character.DASH_PUNCTUATION == type ||
      Character.INITIAL_QUOTE_PUNCTUATION == type ||
      Character.FINAL_QUOTE_PUNCTUATION == type || ch == '.') { return true; }
    return false;
  } // isDashOrQuotePunctuation(ch)

  private boolean isWhiteSpacePunctuation(char ch) {
    int type = Character.getType(ch);
    if((Character.OTHER_PUNCTUATION == type ||
      Character.CONNECTOR_PUNCTUATION == type ||
      Character.START_PUNCTUATION == type || Character.END_PUNCTUATION == type) &&
      (ch != '.')) { return true; }
    return false;
  } // isWhiteSpacePunctuation(ch)

  public String trunxSuffixVowelsFromPhrase(String phrase) {
    String line = phrase;
    int length = phrase.length();
    //String word;
    StringBuffer stem = new StringBuffer();
    int lastWordEnd = 0;
    String justWord;
    for(int lineIndex = 0; lineIndex < length; lineIndex++) {
      if((lineIndex + 1 == length) ||
        (Character.isWhitespace(line.charAt(lineIndex)))) {
        // if not whitespace but end of line then the index should equal the
        // length
        if(lineIndex + 1 == length) lineIndex = length;
        // get the word

        //word = line.substring(0, lineIndex).trim();
        justWord = line.substring(lastWordEnd, lineIndex).trim();
        stem.append(trunxSuffixVowelsFromWord(justWord)).append(" ");
        lastWordEnd = lineIndex;
      } // if whitespace

    } // for line iterate

    // if (phrase.length() != stem.toString().trim().length() ) {
    // System.out.println(phrase+" -> "+stem.toString());
    // }
    return stem.toString().trim();
  } // trunxSuffixVowelsFromPhrase()

  public String trunxSuffixVowelsFromWord(String word) {
    int len = word.length();
    String lastCh;
    
    int trunxCount = 0;
    while(len > minWordLength && trunxCount < maxTruncatedVowels) {

      lastCh = word.substring(len - 1);

      if(SET_OF_VOWELS.contains(lastCh)) {
        word = word.substring(0, len - 1);
        trunxCount++;
      } else {
        // sufix2l = word.substring(len-2);
        // if (SET_OF_CONSONANT_SUFFIXES.contains(sufix2l)){
        // word = word.substring(0,len-2);
        // }
        return word;
      } // not a vowel

      len--;

    }

    return word;
  } // trunxSuffixVowelsFromWord()
  
  @HiddenCreoleParameter
  @CreoleParameter(comment="not supported by this gazetteer", defaultValue="true")
  public void setCaseSensitive(Boolean newCaseSensitive) {
    caseSensitive = newCaseSensitive;
  }
  
  @HiddenCreoleParameter
  @RunTime
  @CreoleParameter(comment="not supported by this gazetteer", defaultValue="true")
  public void setLongestMatchOnly(Boolean longestMatchOnly) {
    this.longestMatchOnly = longestMatchOnly;
  }
  
  @HiddenCreoleParameter
  @RunTime
  @CreoleParameter(comment="not supported by this gazetteer", defaultValue="true")
  public void setWholeWordsOnly(Boolean wholeWordsOnly) {
    this.wholeWordsOnly = wholeWordsOnly;
  }

  /**
   * For internal use by the duplication mechanism.
   */
  @Sharable
  public void setMapsList(List<Map> mapsList) {
    this.mapsList = mapsList;
  }

  /**
   * For internal use by the duplication mechanism.
   */
  public List<Map> getMapsList() {
    return mapsList;
  }

  /**
   * For internal use by the duplication mechanism.
   */
  @Sharable
  public void setCategoryList(ArrayList<Lookup> categoryList) {
    this.categoryList = categoryList;
  }

  /**
   * For internal use by the duplication mechanism.
   */
  public ArrayList<Lookup> getCategoryList() {
    return categoryList;
  }
} // class Russ Gazetteer
