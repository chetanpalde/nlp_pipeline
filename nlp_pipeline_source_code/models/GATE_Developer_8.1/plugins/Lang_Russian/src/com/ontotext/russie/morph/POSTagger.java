/**
 * POSTagger.java
 * 
 * @todo: all-upper-handling
 * @todo: ch and e-oborotnoe handling or file conversion
 * @todo: list of all types and of all nests, toString of lemma and nest. This
 *        one is based on the RussGazetteer and performs POS Tagging without
 *        disambiguation.
 *        <p>
 *        Title: RussIE
 *        </p>
 *        <p>
 *        Description: Russian Information Extraction based on GATE
 *        </p>
 *        <p>
 *        Copyright: Copyright (c) 2003
 *        </p>
 *        <p>
 *        Company: Ontotext Lab.
 *        </p>
 * @author borislav popov
 * @version 1.0
 */
package com.ontotext.russie.morph;

import gate.AnnotationSet;
import gate.Factory;
import gate.FeatureMap;
import gate.Resource;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;
import gate.creole.metadata.CreoleParameter;
import gate.creole.metadata.CreoleResource;
import gate.creole.metadata.Optional;
import gate.creole.metadata.RunTime;
import gate.creole.metadata.Sharable;
import gate.util.GateRuntimeException;
import gate.util.InvalidOffsetException;
import gate.util.LuckyException;
import gate.util.profile.Profiler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ontotext.russie.RussIEConstants;

@CreoleResource(name = "Russian POS Tagger", icon = "pos-tagger",
    comment = "Part-of-speech tagger for Russian",
    helpURL = "http://gate.ac.uk/userguide/sec:misc-creole:language-plugins:russian")
@SuppressWarnings({"rawtypes","unchecked"})
public class POSTagger extends gate.creole.AbstractLanguageAnalyser implements
  RussIEConstants {

  private static final long serialVersionUID = -1392655786706987563L;

  /**
   * Debug flag
   */
  protected static final boolean DEBUG = false;

  /**
   * Detailed Debug flag
   */
  protected static final boolean DETAILED_DEBUG = false;

  public Boolean caseSensitive;

  /**
   * Used to store the annotation set currently being used for the newly
   * generated annotations
   */
  protected String annotationSetName;

  /** the encoding of the gazetteer */
  protected String encoding;

  /**
   * fireStatusDoingLookupStr
   */
  protected static final String DOING_LOOKUP_IN =
    "Doing Morpho-Syntactic Type Lookup";

  /**
   * "Reading " string
   */
  protected static final String READING = "Reading morphology file :";

  /**
   * a list of all the maps representing the first words, first_secind phrases,
   * etc. each map's value might be an ArrayList of TYpe objects specifying
   * categories tied to this word/phrase.
   */
  protected List<Map> mapsList;

  /**
   * size of the mapsList
   */
  protected int mapsListSize = 0;

  /**
   * a list of distinct references to morpho syntactic set.
   */
  protected Set<SuffixNest> msTypeSet = null;

  /** The location of the main configuration file */
  private URL config;

  public POSTagger() {
  }

  /**
   * Gets the morph reader.
   * 
   * @param file
   *          the morhology file
   * @return the morh reader
   */
  private MorphologyReader getMorphReader(URL url) {
    MorphologyReader mReader =
      new MorphologyReader(caseSensitive.booleanValue());
    mReader.setEncoding(encoding);

    try {
      mReader.load(url);
    } catch(IOException x) {
      x.printStackTrace();
      throw new GateRuntimeException("Loading of the morphology failed.");
    }
    return mReader;
  } // getMorphReader(url)

  /**
   * Initializes the POS Tagger using the morphology.
   * 
   * @throws ResourceInstantiationException
   * @return returns this resource
   */
  public Resource init() throws ResourceInstantiationException {
    if(mapsList != null) {
      // this is a duplicate
      mapsListSize = mapsList.size();
    } else {
      Profiler profiler = null;
      if(DEBUG) {
        profiler = new Profiler();
        profiler.enableGCCalling(false);
        profiler.initRun("POS Tagger init()");
        profiler.checkPoint("reset");
      }
      // check if there's a list URL
      if(config == null) { throw new ResourceInstantiationException(
        "No config provided!"); } // if

      mapsList = new ArrayList<Map>(10);

      try {
        BufferedReader configReader =
          new BufferedReader(new InputStreamReader(config.openStream(), encoding));
        int lemmaIdx = 0;
        try {
          // each line in config is a relative path to a morph file

          String configLine = null;
          while((configLine = configReader.readLine()) != null) {
            configLine = configLine.trim();
            // ignore blank lines and comments
            if("".equals(configLine) || configLine.startsWith("#")) continue;

            URL morphURL = new URL(config, configLine);
            MorphologyReader morphRdr = getMorphReader(morphURL);
            Set<Lemma> lemmaSet = morphRdr.getLemmas();
            Iterator<Lemma> lemmaIter = lemmaSet.iterator();

            int linesCnt = lemmaSet.size();
            // allocate the hashmap for the first words from the phrases
            mapsList.add(new HashMap(linesCnt));
            mapsListSize = mapsList.size();

            // allocate the category Map with optimal initial capacity & load
            // factor
            // IR - no idea what this is for, it gets overwritten every iteration
            // and is written to but not read within add()...
            msTypeSet = new HashSet<SuffixNest>();

            Lemma lemma;
            fireStatusChanged(READING + configLine);
            while(lemmaIter.hasNext()) {
              lemma = lemmaIter.next();
              fireProgressChanged(++lemmaIdx * 100 / linesCnt);
              this.add(lemma);
            } // while
          }

        } finally {
          configReader.close();
        }

        fireProcessFinished();

        if(DEBUG) {
          System.out.println("POS Tagger Is GC Enabled ? = " +
            profiler.isGCCallingEnabled());
          profiler.checkPoint("POS Tagger init completed.");
        } // DEBUG

        if(DEBUG) {
          System.out.println("Starting Garbage Collection ...");
          System.gc();
          profiler.checkPoint("Garbage Collection finished");
        }

        if(DEBUG) {
          System.out.println("\nLEMMAs COUNT -> ");
          System.out.println(lemmaIdx);
          System.out.println("\nTYPE POOL SIZE -> ");
          System.out.println(TypePool.size());
          System.out.println("\nSUFFIX POOL SIZE -> ");
          System.out.println(SuffixPool.size());
        }
        if(DETAILED_DEBUG) {
          // dump distinct types
          System.out.println("\nTYPE POOL ->");
          System.out.println(TypePool.getString());
          System.out.println("\nSUFFIX POOL ->");
          System.out.println(SuffixPool.getString());

        }

      } catch(Exception x) {
        throw new ResourceInstantiationException(x);
      } // catch
    }

    return this;
  } // Resource init() throws ResourceInstantiationException

  /**
   * Re-initialize this POS tagger by re-loading the configuration.
   */
  public void reInit() throws ResourceInstantiationException {
    mapsList = null;
    msTypeSet = null;
    init();
  }

  /**
   * This method runs the gazetteer. It parses the document and looks-up the
   * parsed phrases from the maps, in which the phrases vs. annotations are set,
   * in order to generate an annotation set. It assumes that all the needed
   * parameters are set. If they are not, an exception will be fired.
   */
  public void execute() throws ExecutionException {
    Profiler profiler = null;
    if(DEBUG) {
      profiler = new Profiler();
      profiler.enableGCCalling(false);
      profiler
        .initRun("POS Tagger execute over document " + document.getName());
      profiler.checkPoint("reset");

    }

    AnnotationSet annotationSet;
    // check the input
    if(document == null) { throw new ExecutionException(
      "No document to process!"); } // if document is null

    if(annotationSetName == null || annotationSetName.equals(""))
      annotationSet = document.getAnnotations();
    else annotationSet = document.getAnnotations(annotationSetName);

    fireStatusChanged(DOING_LOOKUP_IN);

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
    String phrase = "";
    int mapIndex = 0;
    FeatureMap fm;
    Map currentMap = new HashMap();
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
      int currCharInt;
      do {
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
          punctuationZone = true;
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

        Map<String, Set> lemmaVsTypes = this.lookup(phrase, currentMap);
        if(lemmaVsTypes != null && lemmaVsTypes.size() > 0) {
          matchedRegionEnd = iend;
          // generate lookups for the phrase so far
          {
            Iterator<String> lemmaIter = lemmaVsTypes.keySet().iterator();
            String lemma;
            Set typeSet;
            String type;
            Iterator typeIter;
            while(lemmaIter.hasNext()) {
              lemma = lemmaIter.next();
              typeSet = lemmaVsTypes.get(lemma);
              typeIter = typeSet.iterator();
              while(typeIter.hasNext()) {
                type = typeIter.next().toString();
                fm = Factory.newFeatureMap();

                fm.put(FEATURE_MSD_TYPE, type);
                fm.put(FEATURE_LEMMA, lemma);

                try {
                  annotationSet.add(new Long(matchedRegionStart), new Long(
                    matchedRegionEnd), TYPE_MSD, fm);
                } catch(InvalidOffsetException ioe) {
                  throw new LuckyException(ioe.toString());
                } // catchx
              } // while types
            }// while(lemmaIter.hasNext())
          } // generate MSD for the phraseso far
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

    if(DEBUG) {
      profiler.checkPoint("POS Tagger init completed.");
    } // DEBUG

    fireProcessFinished();
    fireStatusChanged("POS Tagger processing finished!");

  } // execute ()

  public Set lookup(String wordForm) {
    Set result = null;
    for(int li = 0; li < mapsListSize; li++) {
      Map list = mapsList.get(li);
      if(list.containsKey(wordForm)) {
        ArrayList lookupList = (ArrayList)list.get(wordForm);
        if(lookupList != null && lookupList.size() > 0) {
          result = new HashSet(lookupList);
          break;
        }
      }
    } // for lists
    return result;
  }

  public boolean remove(String wordForm) {
    boolean isRemoved = false;
    for(int i = 0; i < mapsListSize; i++) {
      Map map = mapsList.get(i);
      if(map.containsKey(wordForm)) {
        map.remove(wordForm);
        isRemoved = true;
        break;
      }
    } // for lists
    return isRemoved;
  }

  /**
   * Adds to the POS Tagger model another wordForm with its type.
   * 
   * @param wordForm
   *          the word-form
   * @param lemma
   *          the lemma to be added
   * @return true if successful
   */
  public boolean add(Lemma lemma) {
    boolean isAdded = false;

    SuffixNest nest = lemma.getSuffixNest();
    String root = lemma.getRoot();
    @SuppressWarnings("unused")
    String mainFormSuffix = lemma.getSuffix(lemma.getMainFormType());

    if(!caseSensitive.booleanValue()) {
      root = root.toLowerCase();
    }

    // ALL-UPPER-CASE SUPPORT
    // String upper = root.toUpperCase();
    // if (!upper.equals(root)) {
    // this.add(upper,type);
    // } // avoid endless recursion

    // category key
    Set<SuffixNest> key = new HashSet<SuffixNest>(1);

    // add the lookup to the current key
    key.add(nest);

    // add the lookup to the category list
    msTypeSet.add(nest);

    // init some params

    String line = root;
    int mapIndex = -1;
    String word = null;
    Set<SuffixNest> oldKey = null;
    Map<String, Set> currentMap = null;
    int length = 0;

    mapIndex = -1;
    line = line.trim();
    length = line.length();
    for(int lineIndex = 0; lineIndex < length; lineIndex++) {
      if((lineIndex + 1 == length) ||
        Character.isWhitespace(line.charAt(lineIndex)) ||
        isWhiteSpacePunctuation(line.charAt(lineIndex))) {
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

    if(word == null) {
      word = root;
      // if the map doesn't exist : create it
      ++mapIndex;
      if(mapsListSize <= mapIndex) {
        mapsList.add(new HashMap());
        mapsListSize++;
      } // if the map doesn't exist
      currentMap = (mapsList.get(mapIndex));
    }

    try {
      // !!! put the category key in the last map
      oldKey = currentMap.get(word);
    } catch(Exception x) {
      x.printStackTrace();
      System.out.println("root = " + word);
    }

    if(null == oldKey) {
      oldKey = new HashSet<SuffixNest>(1);
      oldKey.add(nest);
      currentMap.put(word, oldKey);
      isAdded = true;
    } else {
      if(!oldKey.contains(nest)) {
        oldKey.add(nest);
      }
      currentMap.put(word, oldKey);
      isAdded = true;
    } // else
    return isAdded;

  } // add

  @CreoleParameter(comment = "Location of configuration file listing "
    + "morphology files to be loaded", defaultValue = "resources/morphology/main.conf")
  public void setConfig(URL config) {
    this.config = config;
  }

  public URL getConfig() {
    return config;
  } // getConfig

  /**
   * Sets the AnnotationSet that will be used at the next run for the newly
   * produced annotations.
   */
  @RunTime
  @Optional
  @CreoleParameter(comment = "The annotation set name to use")
  public void setAnnotationSetName(String newAnnotationSetName) {
    annotationSetName = newAnnotationSetName;
  }

  /**
   * Gets the AnnotationSet that will be used at the next run for the newly
   * produced annotations.
   */
  public String getAnnotationSetName() {
    return annotationSetName;
  }

  @CreoleParameter(comment = "The encoding used for reading the config and "
    + "morphology files", defaultValue = "UTF-8")
  public void setEncoding(String newEncoding) {
    encoding = newEncoding;
  }

  public String getEncoding() {
    return encoding;
  }

  /**
   * Find the list of mathcing types and mainforms for the current phrase and
   * map
   * 
   * @param phrase
   * @param map
   * @return the map of main form vs matching types for the current phrase and
   *         map
   */
  private Map<String, Set> lookup(String phrase, Map map) {
    if(DETAILED_DEBUG) {
      System.out.println("phrase -> " + phrase);
    }

    if(!caseSensitive.booleanValue()) {
      phrase = phrase.toLowerCase();
    }

    Map<String, Set> lemmaVsTypes = new HashMap<String, Set>();
    Set newTypes;
    Set nests;
    Iterator ni;
    String suffix = "";
    SuffixNest nest;
    String lemma;
    // temporary mediator
    Set types;
    while(phrase.length() > 0) {
      if(map.containsKey(phrase)) {
        nests = (Set)map.get(phrase);
        ni = nests.iterator();
        while(ni.hasNext()) {
          nest = (SuffixNest)ni.next();
          if(nest != null) {
            newTypes = nest.getType(suffix);
            lemma = phrase + nest.getMainFormSuffix();
            if(newTypes != null) {
              types = lemmaVsTypes.get(lemma);
              if(types == null) {
                lemmaVsTypes.put(lemma, newTypes);
              } else {
                types.addAll(types);
              }
            } else {
              if(DETAILED_DEBUG) {
                System.out.println("\nUNRECOGNIZED PHRASE - > " + phrase +
                  suffix);
                System.out.println("SUFFIX - > " + suffix);
                System.out.println("NEST - > " + nest);
              } // detailed debug
            } // dump the unrecognized phrase with the nest content
          } // not null nest
        } // while nests
      } // if phrase contained
      suffix = phrase.substring(phrase.length() - 1) + suffix;
      phrase = phrase.substring(0, phrase.length() - 1);
    } // while phrase - try lookups

    return lemmaVsTypes;
  } // lookup (phrase,map)

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

  @CreoleParameter(comment = "Should this POS Tagger differentiate on case", defaultValue = "true")
  public void setCaseSensitive(Boolean newCaseSensitive) {
    caseSensitive = newCaseSensitive;
  }

  public Boolean getCaseSensitive() {
    return caseSensitive;
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
  public void setMsTypeSet(Set<SuffixNest> msTypeSet) {
    this.msTypeSet = msTypeSet;
  }

  /**
   * For internal use by the duplication mechanism.
   */
  public Set<SuffixNest> getMsTypeSet() {
    return msTypeSet;
  }
} // class POSTagger

