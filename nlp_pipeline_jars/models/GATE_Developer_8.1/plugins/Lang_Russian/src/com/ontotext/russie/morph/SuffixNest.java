package com.ontotext.russie.morph;

import gate.FeatureMap;

import java.util.Set;

/**
 * SuffixNest.java The suffit nest keeps the inflection suffixes of a lemma with
 * their respective morpho-syntactic type.
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
 * @author borislav popov
 * @version 1.0
 */
public interface SuffixNest {

  /**
   * Sets the main form suffix in order to allow easy construction of the main
   * form in the time of generation of MSD annotations
   * 
   * @param suf
   *          the main-form suffix according to the common root.
   */
  void setMainFormSuffix(String suf);

  String getMainFormSuffix();

  /**
   * Adds a suffix with its morpho-syntactic type
   * 
   * @param suffix
   *          the suffix
   * @param type
   *          the type
   */
  void add(String suffix, String type);

  /**
   * Adds a suffix with its morpho-syntactic type set
   * 
   * @param suffix
   *          the suffix
   * @param typeSet
   *          the set of types
   */
  void add(String suffix, Set<String> typeSet);

  /**
   * Get all suffixes in the nest.
   * 
   * @return the suffixes in the nest
   */
  Set<String> getSuffixes();

  /**
   * Get all morpho-syntactic types.
   * 
   * @return all morpho-syntactic types
   */
  Set<String> getTypes();

  /**
   * Gets the set of types associated with a suffix
   * 
   * @param suffix
   * @return the set of types associated with the suffix
   */
  Set<String> getType(String suffix);

  /**
   * Adds a prefix to all suffixes in the nest
   * 
   * @param prefix
   */
  void addPrefix2Suffixes(String prefix);

  FeatureMap getFeatureMap();

  void setFeatureMap(FeatureMap fm);

} // class SuffixNest