package com.ontotext.russie.morph;

import gate.FeatureMap;

import java.util.Set;

/**
 * Lemma.java
 * 
 * @todo : make it allow not distinct types and wfs The Lemma consists of
 *       alternative word-forms and syntactic and morphological information and
 *       allows its access.
 *       <p>
 *       Title: RussIE
 *       </p>
 *       <p>
 *       Description: Russian Information Extraction based on GATE
 *       </p>
 *       <p>
 *       Copyright: Copyright (c) 2003
 *       </p>
 *       <p>
 *       Company: Ontotext Lab.
 *       </p>
 * @author borislav popov
 * @version 1.0
 */
public interface Lemma {

  String getAnnotationType();

  void setAnnotationType(String type);

  FeatureMap getFeatureMap();

  void setFeatureMap(FeatureMap fm);

  /**
   * Sets the main word-form with its type.
   * 
   * @param wf
   *          word form
   * @param type
   *          the type of the word form
   */
  void setMainForm(String wf, String type);

  /**
   * Fetch the main word-form.
   * 
   * @return the main word-form
   */
  String getMainForm();

  /**
   * Fetch the main word-form type.
   * 
   * @return the main word-form type
   */
  String getMainFormType();

  /**
   * Fetch the root of the lemma.
   * 
   * @return the root of the lemma
   */
  String getRoot();

  /**
   * Adds a word form with its type
   * 
   * @param wf
   *          word-form
   * @param type
   *          the type of the word-form
   */
  void addWordForm(String wf, String type);

  /**
   * Get the word form by type.
   * 
   * @param type
   *          the word-form type
   * @return the word-form that has this type
   */
  String getWordForm(String type);

  /**
   * Get suffix by type.
   * 
   * @param type
   *          the word-form type
   * @return the suffix that has this type
   */
  String getSuffix(String type);

  /**
   * Fetch a set of the word-form suffixes in the lemma according to the root.
   * 
   * @return the word-form suffixes in this lemma
   */
  public Set<String> getSuffixes();

  /**
   * Get the set of types relevant to a word-form
   * 
   * @param wf
   *          the word-form
   * @return the set of types relevant to the word-form
   */
  Set<String> getTypeByWF(String wf);

  /**
   * Get the set of types relevant to a word-form suffix
   * 
   * @param suffix
   *          the suffix of the wf
   * @return the set of types relevant to the word-form with this suffix
   */
  Set<String> getTypeBySuffix(String suffix);

  /**
   * Fetch a set of the word-forms in the lemma.
   * 
   * @return the word-forms in this lemma
   */
  Set<String> getWordForms();

  /**
   * Fetch the set of word-form types in the lemma
   * 
   * @return the set of word-form types in the lemma
   */
  Set<String> getTypes();

  /**
   * Get the suffix nest associated with this lemma.
   * 
   * @return the nest
   */
  SuffixNest getSuffixNest();

  /**
   * Synchronizes the current nest with the pool of nests and sets the unique
   * nest to the lemma. To be called after finishing the incremental uploading
   * of suffixes and types to the lemma
   */
  void synchWithSuffixPool();

  /**
   * Calculates the difference between the specified name and this lemma in
   * terms of finding a common root and returns the count of characters
   * different at the suffix of the name compared to the lemma.
   * 
   * @param name
   * @return
   */
  int difference2(String name);

} // interface Lemma