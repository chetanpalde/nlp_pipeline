/*
 * Simplifier.java
 *
 * Copyright (c) 2004-2014, The University of Sheffield.
 * 
 * This file is part of GATE (see http://gate.ac.uk/), and is free software,
 * Licensed under the GNU Library General Public License, Version 3, June 2007
 * (in the distribution as file licence.html, and also available at
 * http://gate.ac.uk/gate/licence.html).
 * 
 * Mark A. Greenwood, 2013
 */

package gate.creole.summarization.linguistic;

import gate.Annotation;
import gate.Factory;
import gate.FeatureMap;
import gate.Gate;
import gate.LanguageAnalyser;
import gate.Resource;
import gate.creole.AbstractLanguageAnalyser;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;
import gate.creole.metadata.CreoleParameter;
import gate.creole.metadata.CreoleResource;
import gate.creole.metadata.Optional;
import gate.creole.metadata.RunTime;
import gate.wordnet.Relation;
import gate.wordnet.SemanticRelation;
import gate.wordnet.Synset;
import gate.wordnet.WordNet;
import gate.wordnet.WordSense;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@CreoleResource(name = "Linguistic Simplifier", icon = "LinguisticSimplifier")
public class Simplifier extends AbstractLanguageAnalyser {

  private static final long serialVersionUID = 1L;

  private LanguageAnalyser gaz, jape;

  private URL gazURL, japeURL, nvURL;

  private String encoding, annotationSetName;

  private Map<String, String> noun2verb = new HashMap<String, String>();

  private WordNet wordnet;

  public URL getNounVerbMapURL() {
    return nvURL;
  }

  @CreoleParameter(defaultValue = "resources/noun_verb.csv")
  public void setNounVerbMapURL(URL nvURL) {
    this.nvURL = nvURL;
  }

  public URL getJapeURL() {
    return japeURL;
  }

  @CreoleParameter(defaultValue = "resources/gazetteer/lists.def")
  public void setGazetteerURL(URL gazURL) {
    this.gazURL = gazURL;
  }

  public URL getGazetteerURL() {
    return gazURL;
  }

  @CreoleParameter(defaultValue = "resources/jape/main.jape")
  public void setJapeURL(URL japeURL) {
    this.japeURL = japeURL;
  }

  public String getEncoding() {
    return encoding;
  }

  @CreoleParameter(comment = "The encoding used for all the configuration files", defaultValue = "UTF-8")
  public void setEncoding(String encoding) {
    this.encoding = encoding;
  }

  public String getAnnotationSetName() {
    return annotationSetName;
  }

  @RunTime
  @Optional
  @CreoleParameter
  public void setAnnotationSetName(String annotationSetName) {
    this.annotationSetName = annotationSetName;
  }

  public WordNet getWordNet() {
    return wordnet;
  }

  @RunTime
  @Optional
  @CreoleParameter(comment = "optional WordNet that enables further simplifications to be made")
  public void setWordNet(WordNet wordnet) {
    this.wordnet = wordnet;
  }

  @Override
  public Resource init() throws ResourceInstantiationException {

    // read in the noun -> verb mapping into a map
    try {
      BufferedReader in =
        new BufferedReader(new InputStreamReader(nvURL.openStream()));
      String line = in.readLine();
      while(line != null) {
        String[] data = line.split(",", 2);

        noun2verb.put(data[0], data[1]);

        line = in.readLine();
      }
    } catch(IOException e) {
      throw new ResourceInstantiationException(
        "error reading noun to verb mapping file", e);
    }

    // we want to make sure the gazetteer and jape transducer are hidden
    FeatureMap hidden = Factory.newFeatureMap();
    Gate.setHiddenAttribute(hidden, true);

    // create the gazetteer
    FeatureMap params = Factory.newFeatureMap();
    params.put("listsURL", gazURL);
    params.put("caseSensitive", Boolean.FALSE);
    params.put("encoding", encoding);

    if(gaz == null) {
      gaz =
        (LanguageAnalyser)Factory.createResource(
          "gate.creole.gazetteer.DefaultGazetteer", params, hidden);
    } else {
      gaz.setParameterValues(params);
      gaz.reInit();
    }

    // create the jape transducer
    params = Factory.newFeatureMap();
    params.put("grammarURL", japeURL);
    params.put("encoding", encoding);

    if(jape == null) {
      jape =
        (LanguageAnalyser)Factory.createResource("gate.creole.Transducer",
          params, hidden);
    } else {
      jape.setParameterValues(params);
      jape.reInit();
    }

    // return ourself
    return this;
  }

  @Override
  public void execute() throws ExecutionException {

    // run the gazetteer and then clean up properly
    try {
      gaz.setDocument(getDocument());
      gaz.setParameterValue("annotationSetName", annotationSetName);
      gaz.execute();
    } catch(ResourceInstantiationException rie) {
      throw new ExecutionException(rie);
    } finally {
      gaz.setDocument(null);
    }

    // run the JAPE and then clean up properly
    try {
      jape.setDocument(getDocument());
      jape.getFeatures().put("simplifier", this);
      jape.getFeatures().put("noun2verb", noun2verb);
      jape.setParameterValue("inputASName", annotationSetName);
      jape.setParameterValue("outputASName", annotationSetName);
      jape.execute();
    } catch(ResourceInstantiationException rie) {
      throw new ExecutionException(rie);
    } finally {
      jape.setDocument(null);
    }
    
    //List<Annotation> redundant = Utils.inDocumentOrder(getDocument().getAnnotations(annotationSetName).get("Redundant"));
    //System.out.println(redundant.size());
  }

  @Override
  public void cleanup() {
    // delete the gazetteer and jape transducer when we are deleted to avoid
    // leaking memory
    Factory.deleteResource(gaz);
    Factory.deleteResource(jape);
  }

  /**
   * Checks to see if the word X is a type of Y (i.e. red is a type of colour).
   * This relies on WordNet and will always return false if the optional runtime
   * parameter has not been set.
   * 
   * @param X
   *          the Token annotation spanning word X
   * @param Y
   *          the Token annotation spanning word Y
   * @return true if X is a type of Y given WordNet, false otherwise
   */
  public boolean typeof(Annotation X, Annotation Y) {

    if(wordnet == null) return false;

    if(!X.getType().equals("Token")) return false;
    if(!Y.getType().equals("Token")) return false;

    try {
      List<WordSense> iwX =
        wordnet.lookupWord((String)X.getFeatures().get("string"),
          WordNet.POS_NOUN);
      if(iwX == null || iwX.isEmpty())
        iwX =
          wordnet.lookupWord((String)X.getFeatures().get("root"),
            WordNet.POS_NOUN);
      if(iwX == null || iwX.isEmpty()) return false;

      List<WordSense> iwY =
        wordnet.lookupWord((String)Y.getFeatures().get("string"),
          WordNet.POS_NOUN);
      if(iwY == null || iwY.isEmpty())
        iwY =
          wordnet.lookupWord((String)Y.getFeatures().get("root"),
            WordNet.POS_NOUN);
      if(iwY == null || iwY.isEmpty()) return false;

      int length = findPath(iwX, iwY, Relation.REL_HYPERNYM);
      if(length > 0) return true;

      length = findPath(iwX, iwY, Relation.REL_ATTRIBUTE);
      if(length > 0) return true;

      List<WordSense> iwY2 =
        wordnet.lookupWord((String)Y.getFeatures().get("root"),
          WordNet.POS_NOUN);
      if(iwY2 != null) {
        length = findPath(iwX, iwY2, Relation.REL_HYPERNYM);
        if(length > 0) return true;

        length = findPath(iwX, iwY2, Relation.REL_ATTRIBUTE);
        if(length > 0) return true;
      }

      iwX =
        wordnet.lookupWord((String)X.getFeatures().get("string"),
          WordNet.POS_NOUN);
      if(iwX == null) return false;

      length = findPath(iwX, iwY, Relation.REL_HYPERNYM);
      if(length > 0) return true;

      length = findPath(iwX, iwY, Relation.REL_ATTRIBUTE);
      if(length > 0) return true;

      if(iwY2 != null) {
        length = findPath(iwX, iwY2, Relation.REL_HYPERNYM);
        if(length > 0) return true;

        length = findPath(iwX, iwY2, Relation.REL_ATTRIBUTE);
        if(length > 0) return true;
      }
    } catch(Exception e) {
      e.printStackTrace();
      return false;
    }

    return false;
  }

  /**
   * Find the length of a path between any sense of the two words by following
   * the given relation from the first word outwards. Note that if multiple
   * paths exists the length of the first to be found will be returned which is
   * not guaranteed to be the shortest path.
   * 
   * @param iw1
   *          the senses of the first word
   * @param iw2
   *          the sense of the second word
   * @param relation
   *          the relation type to follow
   * @return the length of the path found, or -1 if no path is found
   */
  private int findPath(List<WordSense> iw1, List<WordSense> iw2, int relation)
    throws Exception {

    for(WordSense ws : iw1) {
      // for each sense of word 1....

      // if this sense is also a sense of word 2 then the path length is 0
      if(iw2.contains(ws)) { return 0; }

      // otherwise see if there is a path from this sense to any sense of word 2
      int tLength =
        findPath(ws.getSynset(), iw2, relation, 0, new HashSet<Synset>());

      // if there was a path return it's length
      if(tLength != -1) return tLength;
    }

    // there is no path between the two words so return a lenght of -1
    return -1;
  }

  /**
   * Finds the length of the path (if one exists) from the given synset to any
   * sense of a second word by following the specific relation. Note that if
   * multiple paths exists the length of the first to be found will be returned
   * which is not guaranteed to be the shortest path.
   * 
   * @param s1
   *          the sysnet to start searching from
   * @param s2
   *          the senses we are aiming to find
   * @param relation
   *          the relation to follow
   * @param length
   *          the length of the path seen so far
   * @param seen
   *          the set of synsets seen so far to enable us to avoid cyclic paths
   * @return
   */
  private int findPath(Synset s1, List<WordSense> s2, int relation, int length,
                       Set<Synset> seen) throws Exception {

    for(WordSense ws : s2) {
      // this is convoluted because Synset doesn't implement equals and the
      // default impl seems to create new instances each time it is requested,
      // which as well as being inefficient means that the default java equals
      // always fails. I should probably do something about that but for now...
      if(s1.getPOS() == ws.getSynset().getPOS() &&
        s1.getOffset() == ws.getSynset().getOffset()) return length;
    }

    // if we've ended up in a cyclic path then stop before we run out of stack
    // space and crash the JVM
    if(seen.contains(s1)) return -1;

    // record the synset we are processing so we can check if we end up going
    // into a cyclic path
    seen.add(s1);

    for(SemanticRelation sr : s1
      .getSemanticRelations(relation)) {
      // for each of the possible semantic relations from this sysnet that we
      // need to process...

      // find the length of the path
      int tLength = findPath(sr.getTarget(), s2, relation, length + 1, seen);

      // if there is a path then return its length
      if(tLength != -1) return tLength;

    }

    // there are no paths for the given relation so return -1
    return -1;
  }
}
