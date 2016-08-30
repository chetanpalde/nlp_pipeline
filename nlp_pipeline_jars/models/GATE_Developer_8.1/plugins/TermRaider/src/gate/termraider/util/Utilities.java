/*
 *  Copyright (c) 2008--2014, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 *  $Id: Utilities.java 17718 2014-03-20 20:40:06Z adamfunk $
 */
package gate.termraider.util;

import gate.Annotation;
import gate.Document;
import gate.FeatureMap;
import gate.creole.ANNIEConstants;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;


public class Utilities implements ANNIEConstants {

  public static final String EXTENSION_CSV = "csv";

  private static double log10of2;
  
  static {
    log10of2 = Math.log10(2.0);
  }

  
  /** This is a little dodgy because it forces the new value 
   * to be Integer; to be used carefully.
   * @param map
   * @param key
   * @param increment
   * @return
   */
  public static int incrementMap(Map<Term, Number> map, Term key, int increment) {
    int count = 0;
    if (map.containsKey(key)) {
      count = map.get(key).intValue();
    }
    count += increment;
    map.put(key, Integer.valueOf(count));
    return count;
  }


  public static double meanDoubleList(List<Double> list) {
    if (list.isEmpty()) {
      return 0.0;
    }
    // implied else
    double total = 0.0;
    for (Double item : list) {
      total += item;
    }
    return total / ((double) list.size());
  }
  

  public static Double convertToDouble(Object x) {
    if (x instanceof Number) {
      return ((Number) x).doubleValue();
    }
    
    return Double.parseDouble(x.toString()) ;
  }


  public static String cleanAndCamelCase(String input) {
    // remove leading & trailing whitespace then camelCase
    return WordUtils.capitalize(StringUtils.trimToEmpty(input)).replaceAll("\\s+", "");
  }
  
  /* The following methods are NOT cruft but are used in some JAPEs,
   * so don't delete them.
   */
  public static void setCanonicalFromLemma(Annotation token, Document doc, String lemmaFeatureName) {
    String canonical = getCanonicalFromLemma(token, doc, lemmaFeatureName);
    token.getFeatures().put("canonical", canonical);
  }

  
  public static String getCanonicalFromLemma(Annotation token, Document doc, String lemmaFeatureName) {
    FeatureMap fm = token.getFeatures();
    String canonical = "";
    if (fm.containsKey(lemmaFeatureName)) {
      canonical = fm.get(lemmaFeatureName).toString().toLowerCase();
    }

    if (canonical.equals("") || canonical.equals("<unknown>")) {
      if (fm.containsKey(TOKEN_STRING_FEATURE_NAME)) {
        canonical = fm.get(TOKEN_STRING_FEATURE_NAME).toString().toLowerCase();
      }
      else {
        canonical = gate.Utils.stringFor(doc, token).toLowerCase();
      }
    }
    
    return canonical;
  }


  public static void setCanonicalFromString(Annotation token, Document doc) {
    String canonical = getCanonicalFromString(token, doc);
    token.getFeatures().put("canonical", canonical);
  }

  
  public static String getCanonicalFromString(Annotation token, Document doc) {
    FeatureMap fm = token.getFeatures();
    String canonical = "";
    if (fm.containsKey(TOKEN_STRING_FEATURE_NAME)) {
      canonical = fm.get(TOKEN_STRING_FEATURE_NAME).toString().toLowerCase();
    }
    else {
      canonical = gate.Utils.stringFor(doc, token).toLowerCase();
    }
    
    return canonical;
  }

  
  public static String sourceOrName(Document document) {
    URL url = document.getSourceUrl();
    if (url == null) {
      return document.getName();
    }
    
    //implied else
    return url.toString();
  }
  

  public static File addExtensionIfNotExtended(File file, String extension) {
    String name = file.getName();
    if (name.contains(".")) {
      return file;
    }

    // implied else: add extension
    File parentDir = file.getParentFile();
    if (extension.startsWith(".")) {
      name = name + extension;
    }
    else {
      name = name + "." + extension;
    }

    return new File(parentDir, name);
  }

  
  public static String integerToString(Integer i) {
    if (i == null) {
      return "<null>";
    }
    // implied else
    return Integer.toString(i);
  }
  
  
  public static double log2(double input) {
    /*  log_a x = log_b x * log_a b
     * 
     *  log_b x = log_a x / log_a b
     */
    return Math.log10(input) / log10of2;
  }

  
  public static void addToMapSet(Map<Term, Set<String>> map, Term key, String value) {
    Set<String> valueSet;
    if (map.containsKey(key)) {
      valueSet = map.get(key);
    }
    else {
      valueSet = new HashSet<String>();
    }
    
    valueSet.add(value);
    map.put(key, valueSet);
  }
  
  
  public static void setScoreTermValue(Map<ScoreType, Map<Term, Number>> map, ScoreType type, Term term, Number value) {
    Map<Term, Number> submap;
    if (map.containsKey(type)) {
      submap = map.get(type);
    }
    else {
      submap = new HashMap<Term, Number>();
    }
    
    submap.put(term, value);
    map.put(type, submap);
  }
  
  
  /**
   * Forces the ultimate value to be Integer. 
   */
  public static void incrementScoreTermValue(Map<ScoreType, Map<Term, Number>> map, 
          ScoreType type, Term term, Integer increment) {
    Map<Term, Number> submap;
    if (map.containsKey(type)) {
      submap = map.get(type);
    }
    else {
      submap = new HashMap<Term, Number>();
    }
    
    int count;
    if (submap.containsKey(term)) {
      count = submap.get(term).intValue();
    }
    else {
      count = 0;
    }
    
    count += increment.intValue();
    submap.put(term, count);
    map.put(type, submap);
  }

  
  public static Set<String> getStringSetFromMap(Map<Term, Set<String>> map, Term key) {
    if (map.containsKey(key)) {
      return map.get(key);
    }
    
    //implied else
    Set<String> valueSet = new HashSet<String>();
    map.put(key, valueSet);
    return valueSet;
  }

}
