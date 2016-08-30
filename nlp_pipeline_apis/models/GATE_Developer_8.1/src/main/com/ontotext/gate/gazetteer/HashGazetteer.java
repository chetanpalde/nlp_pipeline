/**
 * (c) Copyright Ontotext Lab, Sirma Group Corp 2004
 */

package com.ontotext.gate.gazetteer;

import gate.AnnotationSet;
import gate.Factory;
import gate.FeatureMap;
import gate.Resource;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;
import gate.creole.gazetteer.AbstractGazetteer;
import gate.creole.gazetteer.GazetteerException;
import gate.creole.gazetteer.GazetteerList;
import gate.creole.gazetteer.GazetteerNode;
import gate.creole.gazetteer.LinearDefinition;
import gate.creole.gazetteer.LinearNode;
import gate.creole.gazetteer.Lookup;
import gate.creole.gazetteer.MappingNode;
import gate.creole.metadata.CreoleResource;
import gate.creole.metadata.HiddenCreoleParameter;
import gate.creole.metadata.Sharable;
import gate.util.InvalidOffsetException;
import gate.util.LuckyException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

@CreoleResource(name="Hash Gazetteer", icon="gazetteer", comment="A list lookup component implemented by OntoText Lab. The licence information is also available in licence.ontotext.html in the lib folder of GATE", helpURL="http://www.ontotext.com/downloads/index.html#gazetteer")
public class HashGazetteer extends AbstractGazetteer {
  private static final long serialVersionUID = -4603155688378104052L;

  private ArrayList<Lookup> categoryList;

  private Map<LinearNode, GazetteerList> listsByNode;

  private Map<String, List<Lookup>> mapsList[];

  private AnnotationSet annotationSet = null;

  @Override
  @SuppressWarnings({"unchecked","rawtypes"})
  public Resource init() throws ResourceInstantiationException {
    if(mapsList != null) {
      // this is a duplicate - nothing to do
    } else {
      if(listsURL == null)
        throw new ResourceInstantiationException(
                "No URL provided for gazetteer creation!");

      try {
        mapsList = new HashMap[1000];
        definition = new LinearDefinition();
        definition.setURL(listsURL);
        definition.load();
        int i = definition.size();
        listsByNode = definition.loadLists();
        categoryList = new ArrayList<Lookup>(i + 1);
        Iterator<LinearNode> iterator = definition.iterator();
        int j = 0;
        LinearNode linearnode;
        for(; iterator.hasNext(); readList(linearnode)) {
          linearnode = iterator.next();
          fireStatusChanged("Reading " + linearnode.toString());
          fireProgressChanged((++j * 100) / i);
        }

        fireProcessFinished();
      }
      catch(Exception exception) {
        throw new ResourceInstantiationException(exception);
      }
    }
    return this;
  }

  /**
   * Re-initialize this gazetteer by re-loading the configuration.
   */
  @Override
  public void reInit() throws ResourceInstantiationException {
    mapsList = null;
    categoryList = null;
    init();
  }

  @Override
  public void execute() throws ExecutionException {
    if(document == null) throw new ExecutionException("Document is null!");
    annotationSet = document.getAnnotations(annotationSetName);

    String s = document.getContent().toString() + " ";
    if(!super.caseSensitive.booleanValue()) {
      s = s.toUpperCase();
    }

    int documentLength = s.length();
    int j = 0;
    int k = 0;

    StringBuffer stringbuffer = new StringBuffer();
    boolean prevIsSymbol = false;
    boolean prevIsDigit = false;
    boolean prevIsLetter = false;

    // TODO what does this do, as it is only ever set to false
    boolean flag11 = false;

    String s3 = "";
    int i1 = 0;
    int j1 = 0;

    for(int position = 0; position < documentLength; position++) {
      char c = s.charAt(position);
      boolean currIsWhitespace = Character.isWhitespace(c);
      if(currIsWhitespace && stringbuffer.length() == 0) {
        j++;
        prevIsLetter = prevIsDigit = prevIsSymbol = flag11 = false;
        continue;
      }
      if(currIsWhitespace && prevIsSymbol && stringbuffer.length() == 1) {
        j += 2;
        prevIsLetter = prevIsDigit = prevIsSymbol = flag11 = false;
        stringbuffer.delete(0, stringbuffer.length());
        continue;
      }
      boolean currIsLetter = Character.isLetter(c);
      boolean currIsDigit = Character.isDigit(c);
      boolean currIsSymbol = !currIsWhitespace && !currIsLetter && !currIsDigit;
      boolean currIsLowerCase = Character.isLowerCase(c);
      if(k <= j
              && (currIsWhitespace || currIsSymbol || flag11
                      && !currIsLowerCase || !prevIsLetter && currIsLetter))
        k = position;
      boolean flag13 = prevIsLetter
              && (currIsDigit || currIsSymbol || currIsWhitespace)
              || prevIsLetter && currIsLetter && flag11 && !currIsLowerCase
              || prevIsDigit
              && (currIsLetter || currIsSymbol || currIsWhitespace)
              || prevIsSymbol;
      if(position == documentLength - 1) flag13 = true;
      if(flag13) {
        boolean flag16 = !currIsSymbol && !currIsDigit;
        if(position == documentLength - 1) flag16 = true;
        String word = normalizeWhitespace(stringbuffer.toString());
        int k1 = word.length();
        flag16 &= k1 - j1 > 1;
        j1 = k1;
        if(i1 != j || !word.equals(s3)) {
          int wordLength = word.length();
          if(wordLength > 0) {
            boolean flag14 = annotate(word, j, position, wordLength);
            if(flag14) {
              s3 = word;
              i1 = j;
            }
            if(!flag14 && flag16 || documentLength - 1 == position) {
              if(k <= j) k = position;
              j = k;
              position = k - 1;
              stringbuffer.delete(0, stringbuffer.length());
              continue;
            }
          }
        }
      }
      stringbuffer.append(c);
      prevIsDigit = currIsDigit;
      prevIsLetter = currIsLetter;
      prevIsSymbol = currIsSymbol;
    }

    fireProcessFinished();
    fireStatusChanged("Hash Gazetteer processing finished!");
  }

  @Override
  public boolean add(String word, Lookup lookup1) {
    if(!super.caseSensitive.booleanValue()) {
      word = word.toUpperCase();
    }
    
    String s2 = removeTrailingSymbols(word);
    if(!s2.equals(word)) add(s2, lookup1);
    String s3 = word + " ";

    List<Lookup> arraylist = null;
    int j = s3.length();

    boolean prevIsLetter = false;
    boolean prevIsDigit = false;
    boolean prevIsLowercase = false;

    String s4 = "";
    Map<String, List<Lookup>> hashmap = null;
    for(int k = 0; k < j; k++) {
      char c = s3.charAt(k);
      boolean currIsWhitespace = Character.isWhitespace(c);
      boolean currIsDigit = Character.isDigit(c);
      boolean currIsLetter = Character.isLetter(c);
      boolean currIsSymbol = !currIsWhitespace && !currIsDigit && !currIsLetter;
      boolean currIsLowercase = Character.isLowerCase(c);
      boolean flag18 = prevIsLetter
              && (currIsDigit || currIsSymbol || currIsWhitespace)
              || prevIsLetter && currIsLetter && prevIsLowercase
              && !currIsLowercase || prevIsDigit
              && (currIsLetter || currIsSymbol || currIsWhitespace);

      //if we are on the last character
      if(k + 1 == j) flag18 = true;
      
      if(flag18) {
        s4 = normalizeWhitespace(s3.substring(0, k));
        int i = s4.length();
        if(mapsList[i] == null) {
          hashmap = new HashMap<String, List<Lookup>>();
          mapsList[i] = hashmap;
        }
        else {
          hashmap = mapsList[i];
        }
        if(!hashmap.containsKey(s4)) hashmap.put(s4, null);
      }
      prevIsDigit = currIsDigit;
      prevIsLetter = currIsLetter;

      prevIsLowercase = currIsLowercase;

    }

    arraylist = hashmap.get(s4);
    if(null == arraylist) {
      arraylist = new ArrayList<Lookup>(1);
      arraylist.add(lookup1);
    }
    else if(!arraylist.contains(lookup1)) arraylist.add(lookup1);
    hashmap.put(s4, arraylist);
    return true;
  }

  @Override
  public Set<Lookup> lookup(String s) {
    Set<Lookup> set = null;
    String s1 = normalizeWhitespace(s);
    int i = s1.length();
    if(mapsList.length < i) return set;
    Map<String, List<Lookup>> hashmap = mapsList[i];
    if(hashmap == null) {
      return set;
    }
    else {
      Set<Lookup> hashset = new HashSet<Lookup>(hashmap.get(s1));
      return hashset;
    }
  }

  private boolean annotate(String word, int i, int documentPosition, int wordLength) {
    if(wordLength >= mapsList.length) return false;
    Map<String, List<Lookup>> hashmap = mapsList[wordLength];
    if(hashmap == null) return false;
    if(!hashmap.containsKey(word)) return false;
    List<Lookup> arraylist = hashmap.get(word);

    // TODO shouldn't this return false if arraylist is null?

    if(null != arraylist) {
      for(Iterator<Lookup> iterator = arraylist.iterator(); iterator.hasNext();) {
        Lookup lookup1 = iterator.next();
        FeatureMap featuremap = Factory.newFeatureMap();
        featuremap.put("majorType", lookup1.majorType);
        if(null != lookup1.oClass && null != lookup1.ontology) {
          featuremap.put("class", lookup1.oClass);
          featuremap.put("ontology", lookup1.ontology);
        }
        if(null != lookup1.minorType) {
          featuremap.put("minorType", lookup1.minorType);
          if(null != lookup1.languages)
            featuremap.put("language", lookup1.languages);
        }
        try {
          annotationSet.add(new Long(i), new Long(documentPosition), "Lookup", featuremap);
        }
        catch(InvalidOffsetException invalidoffsetexception) {
          throw new LuckyException(invalidoffsetexception.toString());
        }
      }

    }

    return true;
  }

  /**
   * Removes a string from the gazetteer
   *
   * @param s the item to remove
   * @return true if the operation was successful
   */
  @Override
  public boolean remove(String s) {

    String s1 = a(s);
    int i = s1.length();
    if(i > mapsList.length) return false;
    Map<String, List<Lookup>> hashmap = mapsList[i];
    if(hashmap == null) return false;
    if(hashmap.containsKey(s1)) {
      hashmap.remove(s1);
      return true;
    }
    return false;
  }

  /**
   * Works backwards through the String parameter removing each
   * character until it encounters a letter, digit, or whitespace at
   * which point it returns the truncated string.
   *
   * @param s the String you wish to remove trailing symbols from
   * @return the truncated String that now ends in a letter, digit, or
   *         whitespace character
   */
  private String removeTrailingSymbols(String s) {
    for(int i = s.length() - 1; i >= 0; i--) {
      char c = s.charAt(i);
      if(!Character.isLetter(c) && !Character.isDigit(c)
              && !Character.isWhitespace(c))
        s = s.substring(0, i);
      else return s;
    }

    return s;
  }

  /**
   * Normalizes the whitespace within the String instance by replacing
   * any sequence of one or more whitespace characters with a single
   * space. Not that any leading/trailing whitespace is also removed.
   *
   * @param s the String to normalize
   * @return the normalized String
   */
  private String normalizeWhitespace(String s) {

    // this seems to be the same as String.replaceAll("\\s+", " ")

    StringBuffer stringbuffer = new StringBuffer();
    s = s.trim();
    char ac[] = s.toCharArray();
    int i = s.length();
    boolean prevWasWhitespace = false;
    for(int j = 0; j < i; j++) {
      char c = ac[j];

      boolean currIsWhitespace = Character.isWhitespace(c);

      if(currIsWhitespace && !prevWasWhitespace)
        stringbuffer.append(' ');
      else if(!currIsWhitespace) stringbuffer.append(c);

      prevWasWhitespace = currIsWhitespace;
    }

    return stringbuffer.toString();
  }

  private String a(String s) {
    StringBuffer stringbuffer = new StringBuffer();
    boolean allLettersUppercase = true;
    s = s.trim();
    char ac[] = s.toCharArray();
    int i = s.length();
    if(i <= 1) return s;
    
    char firstCharacter = ac[0];
    stringbuffer.append(firstCharacter);
    boolean flag2 = true;
    boolean prevIsLetter = Character.isLetter(firstCharacter);
    boolean prevNotLetterOrDigit = !Character.isLetterOrDigit(firstCharacter);

    boolean flag10 = true;
    char c2 = 'p';

    for(int j = 1; j < i; j++) {
      char currentCharacter = ac[j];
      boolean currNotLetterOrDigit = !Character.isLetterOrDigit(currentCharacter);
      boolean currIsWhitespace = Character.isWhitespace(currentCharacter);
      boolean currIsLetter = Character.isLetter(currentCharacter);
      boolean currIsDigit = Character.isDigit(currentCharacter);
      
      if(flag2) {
        if(prevNotLetterOrDigit && currIsWhitespace) continue;
        flag2 = prevIsLetter && currNotLetterOrDigit || prevNotLetterOrDigit
                && currIsLetter;
        if(currNotLetterOrDigit) {
          if(c2 == 'p') c2 = currentCharacter;
          flag2 = flag10 = c2 == currentCharacter;
        }
        if(j > 2 && !flag2 && stringbuffer.length() > 0) {
          char c3 = stringbuffer.charAt(stringbuffer.length() - 1);
          stringbuffer.deleteCharAt(stringbuffer.length() - 1);
          stringbuffer.append(Character.toLowerCase(c3));
        }
      }
      
      if(currIsLetter || currIsDigit) {
        if(currIsLetter) allLettersUppercase &= Character.isUpperCase(currentCharacter);
        if(!flag10) currentCharacter = Character.toLowerCase(currentCharacter);
        stringbuffer.append(currentCharacter);
      }
      else if(!flag2) flag10 = false;
      
      prevIsLetter = currIsLetter;
      prevNotLetterOrDigit = currNotLetterOrDigit;
    }

    String s1 = stringbuffer.toString();
    if(allLettersUppercase) s1 = s1.toUpperCase();
    return s1;
  }

  private void readList(LinearNode linearnode) throws GazetteerException {

    if(linearnode == null)
      throw new GazetteerException("LinearNode node is null");

    GazetteerList gazetteerlist = listsByNode.get(linearnode);
    if(gazetteerlist == null)
      throw new GazetteerException("gazetteer list not found by node");

    String s = linearnode.getList();
    String majorType = linearnode.getMajorType();
    String minorType = linearnode.getMinorType();
    String language = linearnode.getLanguage();

    Lookup lookup1 = new Lookup(s, majorType, minorType, language);

    if(mappingDefinition != null) {
      MappingNode mappingnode = mappingDefinition.getNodeByList(s);
      if(null != mappingnode) {
        lookup1.oClass = mappingnode.getClassID();
        lookup1.ontology = mappingnode.getOntologyID();
      }
    }

    lookup1.list = s;
    categoryList.add(lookup1);

    Iterator<GazetteerNode> iterator = gazetteerlist.iterator();
    String normalisedWord = null;

    for(; iterator.hasNext(); add(normalisedWord, lookup1)) {
      String word = iterator.next().toString();
      int wordLength = word.length();
      for(int j = 0; j < wordLength; j++) {
        if(j + 1 != wordLength && !Character.isWhitespace(word.charAt(j))) continue;
        if(j + 1 == wordLength) j = wordLength;
        normalisedWord = word.substring(0, j).trim();
      }
    }
  }
  
  @Override
  @HiddenCreoleParameter
  public void setWholeWordsOnly(Boolean wholeWordsOnly) {
    super.setWholeWordsOnly(wholeWordsOnly);
  }
  
  @Override
  @HiddenCreoleParameter
  public void setLongestMatchOnly(Boolean longestMatchOnly) {
    super.setLongestMatchOnly(longestMatchOnly);
  }

  /**
   * For internal use by the duplication mechanism.
   */
  @Sharable
  public void setMapsList(Map<String, List<Lookup>> mapsList[]) {
    this.mapsList = mapsList;
  }

  /**
   * For internal use by the duplication mechanism.
   */
  public Map<String, List<Lookup>>[] getMapsList() {
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
}
