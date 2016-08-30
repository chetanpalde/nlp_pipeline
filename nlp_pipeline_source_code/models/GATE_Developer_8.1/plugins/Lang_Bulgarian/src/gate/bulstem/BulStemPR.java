/*
 * BulStemPR.java
 * 
 * Copyright (c) 2013 The University of Sheffield.
 * 
 * This file is part of GATE (see http://gate.ac.uk/), and is free software,
 * licenced under the GNU Library General Public License, Version 2, June1991.
 * 
 * A copy of this licence is included in the distribution in the file
 * licence.html, and is also available at http://gate.ac.uk/gate/licence.html.
 * 
 * Ivelina Nikolova, 05/12/2013
 */
package gate.bulstem;

import gate.Annotation;
import gate.AnnotationSet;
import gate.ProcessingResource;
import gate.Resource;
import gate.Utils;
import gate.creole.AbstractLanguageAnalyser;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;
import gate.creole.metadata.CreoleParameter;
import gate.creole.metadata.CreoleResource;
import gate.creole.metadata.Optional;
import gate.creole.metadata.RunTime;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URL;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * Stemming algorithm by Preslav Nakov.
 * 
 * @author Alexander Alexandrov, e-mail: sencko@mail.bg, provided the JAVA
 *         implementation of the algorithm
 * @author Ivelina Nikolova, e-mail:iva@lml.bas.bg, wrapped the stemmer for GATE
 */
@CreoleResource(name = "BulStem", helpURL = "http://lml.bas.bg/~nakov/bulstem/", comment = "This plugin is an implementation of the BulStem stemmer algorithm for Bulgarian developed by Preslav Nakov.")
public class BulStemPR extends AbstractLanguageAnalyser implements
  ProcessingResource, Serializable {

  private static final long serialVersionUID = 257778017962925274L;

  protected Logger logger = Logger.getLogger(this.getClass());

  private URL rulesURL;

  private String annotationSetName;

  private String annotationType;

  private Map<String, String> stemmingRules;;

  // should we make this an init param?
  // at the moment this always excludes 8556 entries from the default rules file
  private static final int STEM_BOUNDARY = 1;

  private Boolean failOnMissingInputAnnotations = true;

  private static final Pattern vocals = Pattern
    .compile("[^аъоуеиюя]*[аъоуеиюя]");

  public static final Pattern p = Pattern
    .compile("([а-я]+)\\s==>\\s([а-я]+)\\s([0-9]+)");

  @Override
  public Resource init() throws ResourceInstantiationException {

    // check required parameters are set
    if(rulesURL == null) { throw new ResourceInstantiationException(
      "rulesURL param must be set"); }

    stemmingRules = new HashMap<String, String>();

    BufferedReader br = null;
    try {
      br = new BufferedReader(new InputStreamReader(rulesURL.openStream()));
      String s = null;
      while((s = br.readLine()) != null) {
        Matcher m = p.matcher(s);
        if(m.matches()) {
          if(Integer.parseInt(m.group(3)) > STEM_BOUNDARY) {
            stemmingRules.put(m.group(1), m.group(2));
          }
        }
      }
    } catch(Exception e) {
      throw new ResourceInstantiationException(e);
    } finally {
      if(br != null) IOUtils.closeQuietly(br);
    }

    return this;
  }

  @Override
  public void execute() throws ExecutionException {

    // get all the tokens from the specified annotation set
    AnnotationSet allTokens =
      document.getAnnotations(annotationSetName).get(annotationType);

    if(allTokens.size() > 0) {

      // sort out the status reporting stuff
      long startTime = System.currentTimeMillis();
      fireStatusChanged("Running BulStem over " + document.getName());
      fireProgressChanged(0);
      int tokenCount = 0;

      for(Annotation token : allTokens) {
        // for each Token annotation...

        // get the string feature
        String tokenString = token.getFeatures().get("string").toString();

        // stem the string feature and change it to lowercase
        String stem = stem(tokenString).toLowerCase();

        // store the new feature
        token.getFeatures().put("stem", stem);

        // report our progress
        fireProgressChanged(tokenCount++ * 100 / allTokens.size());
      }

      // we've finished so report this
      fireProcessFinished();
      fireStatusChanged(document.getName() +
        " stemmed in " +
        NumberFormat.getInstance().format(
          (double)(System.currentTimeMillis() - startTime) / 1000) +
        " seconds!");
    } else {
      if(failOnMissingInputAnnotations) {
        throw new ExecutionException("No tokens to process in document " +
          document.getName() + "\n" + "Please run a tokeniser first!");
      } else {
        Utils
          .logOnce(logger, Level.INFO,
            "BulStem: no token annotations in input document - see debug log for details.");
        logger.debug("No input annotations in document " + document.getName());
      }
    }
  }

  private String stem(String word) {
    Matcher m = vocals.matcher(word);
    if(!m.lookingAt()) { return word; }

    for(int i = m.end() + 1; i < word.length(); i++) {
      String suffix = word.substring(i);
      if((suffix = stemmingRules.get(suffix)) != null) {
        // get the new stem by cutting up the word and adding the right suffix
        // from the rules
        return word.substring(0, i) + suffix;
      }
    }
    return word;
  }

  // PR parameters
  @CreoleParameter(comment = "Stemming Rules File", defaultValue = "resources/stem_rules_context_2_UTF-8.txt")
  public void setPathToRules(URL rulesURL) {
    this.rulesURL = rulesURL;
  }

  public URL getPathToRules() {
    return rulesURL;
  }

  @Optional
  @RunTime
  @CreoleParameter(comment = "The annotation set to use as input")
  public void setAnnotationSetName(String annotationSetName) {
    this.annotationSetName = annotationSetName;
  }

  public String getAnnotationSetName() {
    return annotationSetName;
  }

  @RunTime
  @CreoleParameter(comment = "The name of the base 'Token' annotation type", defaultValue = "Token")
  public void setAnnotationType(String annotationType) {
    this.annotationType = annotationType;
  }

  public String getAnnotationType() {
    return annotationType;
  }

  @RunTime
  @Optional
  @CreoleParameter(comment = "Throw an exception when there are none of the required input annotations", defaultValue = "true")
  public void setFailOnMissingInputAnnotations(Boolean fail) {
    failOnMissingInputAnnotations = fail;
  }

  public Boolean getFailOnMissingInputAnnotations() {
    return failOnMissingInputAnnotations;
  }
}
