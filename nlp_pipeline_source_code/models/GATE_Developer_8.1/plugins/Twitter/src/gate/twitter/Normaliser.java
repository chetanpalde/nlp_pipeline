/*
 * Normaliser.java
 *
 * Copyright (c) 2011-2013, The University of Sheffield. See the file COPYRIGHT.txt
 * in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 * This file is part of GATE (see http://gate.ac.uk/), and is free software,
 * licenced under the GNU Library General Public License, Version 2, June 1991
 * (in the distribution as file licence.html, and also available at
 * http://gate.ac.uk/gate/licence.html).
 *
 */

package gate.twitter;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import gate.*;
import gate.creole.*;
import gate.creole.metadata.*;
import gate.util.Files;
import gate.util.GateRuntimeException;

import pt.tumba.spell.*;

@CreoleResource(name = "Tweet Normaliser", comment = "Normalise texts in tweets (convert into standard English spelling mistakes, colloquialisms, typing variations and so on)", helpURL = "http://gate.ac.uk/userguide/sec:social:twitter:prs")
public class Normaliser extends AbstractLanguageAnalyser {

  private static final long serialVersionUID = -4139489923193104429L;

  @Override
  public Resource init() throws ResourceInstantiationException {

    if(this.orthURL == null)
      throw new ResourceInstantiationException("orth norm file not set");

    if(this.dictURL == null)
      throw new ResourceInstantiationException("dict file not set");

    checker = new SpellChecker();
    BufferedReader dict_reader;
    BufferedReader orth_reader;
    wordlist = new HashSet<String>();
    orthmappings = new HashMap<String, String>();

    // start up dict reader & checker
    try {
      String filepath = Files.fileFromURL(dictURL).getAbsolutePath();
      dict_reader = new BufferedReader(new FileReader(filepath));
      checker.initialize(filepath);
    } catch(Exception e) {
      throw new ResourceInstantiationException(e);
    }

    // populate the wordlist
    String entry;
    try {
      while((entry = dict_reader.readLine()) != null) {
        String[] tokens = entry.split(" : ");
        wordlist.add(tokens[0]);
      }
      dict_reader.close();
    } catch(IOException e) {
      throw new ResourceInstantiationException(e);
    }

    // populate the common norm. lookup list
    try {
      String filepath = Files.fileFromURL(orthURL).getAbsolutePath();
      orth_reader = new BufferedReader(new FileReader(filepath));

      while((entry = orth_reader.readLine()) != null) {
        String[] tokens = entry.split(",");
        orthmappings.put(tokens[0], tokens[1]);
      }
      orth_reader.close();
    } catch(Exception e) {
      throw new ResourceInstantiationException(e);
    }

    dist = new LevenshteinDistance();

    return this;
  }

  @Override
  public void execute() throws ExecutionException {

    /*
     * The goal is to correct in-vocab (IV) words to their normal English form,
     * and skip over out-of-vocab (OOV) terms
     * 
     * General process: Take the Input AS; per Sentence, per Token, read the
     * String feature look to see if it has a direct conversion - if so, do
     * that, done look to see if it has a spelling correcting candidate within
     * edit dist up to 2 - if so, convert, done look to see if it has a
     * doublemetaphone candidate within edit dist up to 2 - if so, convert, done
     * otherwise, assume OOV and not a mangled IV term; ignore
     */

    if(document == null)
      throw new ExecutionException("No document to process!");

    fireStatusChanged("Normalising " + document.getName());

    AnnotationSet inputAS = document.getAnnotations(inputASName);

    AnnotationSet tokensAS = inputAS.get(TOKEN_ANNOTATION_TYPE);
    List<Annotation> tokenList = new ArrayList<Annotation>(tokensAS);

    // if there are any annotations
    if(tokensAS != null && tokensAS.size() > 0) {
      Iterator<Annotation> tokensIter = tokenList.iterator();

      while(tokensIter.hasNext()) {

        Annotation ann = tokensIter.next();
        // System.out.println("--");
        // System.out.println(ann.toString());

        String kind = (String)ann.getFeatures().get("kind");

        // skip if kind != word - don't both correcting known NEs, punctuation,
        // and so on
        if(kind.equals("word")) {

          String initialText =
            (String)ann.getFeatures().get(initialTextFeature);

          // first: is it in our lookup list?
          // TODO: only trigger this if the tweet has a noise level above a
          // threshold, or based on language modelling
          String initialLower = initialText.toLowerCase();
          if(orthmappings.containsKey(initialLower)) {
            addFeatures(ann, origTextFeature, initialLower);
            addFeatures(ann, normTextFeature, orthmappings.get(initialLower));
            continue;
          }

          // skip words in the dictionary
          if(wordlist.contains(initialText)) {
            continue;
          }

          // check orthography as a quick proper noun filter
          String orth = "invalid";
          try {
            orth = (String)ann.getFeatures().get("orth");
          } catch(Exception e) {
            continue;
          }

          if(orth == null) {
            orth = "invalid";
          }

          // is it a proper noun (capitalised) or should be skipped?
          if((orth.equals("upperInitial")) || (orth.equals("invalid"))) {
            continue;
          }

          // System.out.println("kind: '" + kind + "'" + " orth: '" + orth +
          // "'");

          // we're going to try and do a full replacement
          // save original text
          addFeatures(ann, origTextFeature, initialText);

          // do mangling
          String normalisedText = initialText;

          // replacement checking starts here
          // second param true means take keyboard distances into account
          String mostSimilar = checker.findMostSimilar(initialText);

          // System.out.println(initialText + ":" + mostSimilar);

          if(mostSimilar != null) {

            // if most similar distance is below threshold, make the
            // substitution
            if(dist.modifiedLevenshteinDistance(initialText, mostSimilar) < maxDistance) {
              // don't bother just changing case - leave case as it is
              if(!initialText.toLowerCase()
                .equals(mostSimilar.toLowerCase())) {
                normalisedText = mostSimilar;
              }
            }

            // save normalised text
            addFeatures(ann, normTextFeature, normalisedText);
          }

        }

      }

    }

    fireProcessFinished();

  }

  protected void addFeatures(Annotation annot, String featureName,
                             String featureValue) throws GateRuntimeException {

    String tempIASN = inputASName == null ? "" : inputASName;
    String tempOASN = outputASName == null ? "" : outputASName;
    if(tempIASN.equals(tempOASN)) {
      annot.getFeatures().put(featureName, featureValue);
      return;
    } else {
      int start = annot.getStartNode().getOffset().intValue();
      int end = annot.getEndNode().getOffset().intValue();

      // get the annotations of type outputAnnotationType
      AnnotationSet outputAS =
        (outputASName == null) ? document.getAnnotations() : document
          .getAnnotations(outputASName);
      AnnotationSet annotations = outputAS.get(TOKEN_ANNOTATION_TYPE);
      if(annotations == null || annotations.size() == 0) {
        // add new annotation
        FeatureMap features = Factory.newFeatureMap();
        features.put(featureName, featureValue);
        try {
          outputAS.add(new Long(start), new Long(end), TOKEN_ANNOTATION_TYPE,
            features);
        } catch(Exception e) {
          throw new GateRuntimeException("Invalid Offsets");
        }
      } else {
        // search for the annotation if there is one with the same start and end
        // offsets
        ArrayList<Annotation> tempList =
          new ArrayList<Annotation>(annotations.get());
        boolean found = false;
        for(int i = 0; i < tempList.size(); i++) {
          Annotation annotation = tempList.get(i);
          if(annotation.getStartNode().getOffset().intValue() == start &&
            annotation.getEndNode().getOffset().intValue() == end) {
            // this is the one
            annotation.getFeatures().put(featureName, featureValue);
            found = true;
            break;
          }
        }

        if(!found) {
          // add new annotation
          FeatureMap features = Factory.newFeatureMap();
          features.put(featureName, featureValue);
          try {
            outputAS.add(new Long(start), new Long(end), TOKEN_ANNOTATION_TYPE,
              features);
          } catch(Exception e) {
            throw new GateRuntimeException("Invalid Offsets");
          }
        }
      }
    }
  }

  @Override
  public void reInit() throws ResourceInstantiationException {
    // reinitialization code
    init();
  }

  // getter and setter methods

  @RunTime
  @Optional
  @CreoleParameter(comment = "Input annotation set name", defaultValue = "")
  public void setInputASName(String inputASName) {
    this.inputASName = inputASName;
  }

  public String getInputASName() {
    return this.inputASName;
  }

  @RunTime
  @Optional
  @CreoleParameter(comment = "Output annotation set name", defaultValue = "")
  public void setOutputASName(String outputASName) {
    this.outputASName = outputASName;
  }

  public String getOutputASName() {
    return this.outputASName;
  }

  @RunTime
  @Optional
  @CreoleParameter(comment = "Feature on Token annotations in the input AS that contains the token string", defaultValue = TOKEN_STRING_FEATURE_NAME)
  public void setInitialTextFeature(String f) {
    this.initialTextFeature = f;
  }

  public String getInitialTextFeature() {
    return this.initialTextFeature;
  }

  @RunTime
  @Optional
  @CreoleParameter(comment = "Feature to which the normalised text should be saved", defaultValue = TOKEN_STRING_FEATURE_NAME)
  public void setNormTextFeature(String f) {
    this.normTextFeature = f;
  }

  public String getNormTextFeature() {
    return this.normTextFeature;
  }

  @RunTime
  @Optional
  @CreoleParameter(comment = "Feature to which the original text should be saved", defaultValue = "origString")
  public void setOrigTextFeature(String f) {
    this.origTextFeature = f;
  }

  public String getOrigTextFeature() {
    return this.origTextFeature;
  }

  @CreoleParameter(comment = "Path to JaSpell dictionary", defaultValue = "resources/normaliser/english.jaspell")
  public void setDictURL(URL dictURL) {
    this.dictURL = dictURL;
  }

  public URL getDictURL() {
    return this.dictURL;
  }

  @CreoleParameter(comment = "Path to common normalisation terms list (for orthographic mappings, e.g. 'b4' to 'before')", defaultValue = "resources/normaliser/orth.en.csv")
  public void setOrthURL(URL orthURL) {
    this.orthURL = orthURL;
  }

  public URL getOrthURL() {
    return this.orthURL;
  }

  @RunTime
  @Optional
  @CreoleParameter(comment = "Maximum distance to consider (this determines OOV/IV threshold).\nBased on Levenshtein edit dist (with a case change downweighted to 0.5) and double-metaphone.", defaultValue = "2.0")
  public void setMaxDistance(String maxDistance) {
    this.maxDistance = new Float(maxDistance).doubleValue();
  }

  public String getMaxDistance() {
    return new Float(this.maxDistance).toString();
  }

  protected HashMap<String, String> orthmappings;

  protected HashSet<String> wordlist;

  protected SpellChecker checker;

  protected LevenshteinDistance dist;

  private String outputASName;

  private String inputASName;

  private String initialTextFeature;

  private String normTextFeature;

  private String origTextFeature;

  private URL dictURL;

  private URL orthURL;

  private double maxDistance;

}
