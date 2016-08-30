package com.ontotext.russie.morph;

import gate.Factory;
import gate.FeatureMap;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * LemmaImpl.java Implementation of the Lemma inteface that contains the
 * wordforms of a word with their morpho-syntactic types. NOTE: The current
 * implementation considers that the types are distinct in a single lemma.
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
public class LemmaImpl implements Lemma {

  /** the main form in the lemma */
  private String mainForm;

  /** the type of the main form in the lemma */
  private String mainFormType;

  private String annotationType = "";

  private FeatureMap fm = Factory.newFeatureMap();

  public String getAnnotationType() {
    return annotationType;
  }

  public void setAnnotationType(String type) {
    annotationType = type;
  }

  public FeatureMap getFeatureMap() {
    return fm;
  }

  public void setFeatureMap(FeatureMap fm) {
    this.fm = fm;
  }

  /** the so-called root of the lemma */
  private String root;

  /** map of types vs word-form suffixes */
  private Map<String, String> typeVsSuffix;

  /** the nest of suffixes */
  private SuffixNest suffixNest = new SuffixNestImpl();

  public LemmaImpl() {
    typeVsSuffix = new HashMap<String, String>();
  }

  /**
   * Sets the main word-form with its type.
   * 
   * @param wf
   *          word form
   * @param type
   *          the type of the word form
   */
  public void setMainForm(String wf, String type) {
    mainForm = wf;
    type = TypePool.getDistinctType(type);
    mainFormType = type;
    addWordForm(wf, type);
  } // setMainForm(wf,type)

  /**
   * Fetch the main word-form.
   * 
   * @return the main word-form
   */
  public String getMainForm() {
    return mainForm;
  }

  /**
   * Fetch the main word-form type.
   * 
   * @return the main word-form type
   */
  public String getMainFormType() {
    return mainFormType;
  }

  /**
   * Fetch the root of the lemma.
   * 
   * @return the root of the lemma
   */
  public String getRoot() {
    return root;
  }

  /**
   * Adds a word form with its type
   * 
   * @param wf
   *          word-form
   * @param type
   *          the type of the word-form
   */
  public void addWordForm(String wf, String type) {

    // fit the new wf to the root and retain wf suffix.
    String suffix = adjustRoot(wf);

    if(suffixNest == null) {
      suffixNest = new SuffixNestImpl();
    }

    type = TypePool.getDistinctType(type);
    suffixNest.add(suffix, type);

    typeVsSuffix.put(type, suffix);
  }

  /**
   * Get word-form by type.
   * 
   * @param type
   *          the word-form type
   * @return the word-form that has this type
   */
  public String getWordForm(String type) {
    return root + typeVsSuffix.get(type);
  }

  /**
   * Get suffix by type.
   * 
   * @param type
   *          the word-form type
   * @return the suffix that has this type
   */
  public String getSuffix(String type) {
    return typeVsSuffix.get(type);
  }

  /**
   * Get the set of types relevant to a word-form
   * 
   * @param wf
   *          the word-form
   * @return the set of types relevant to the word-form
   */
  public Set<String> getTypeByWF(String wf) {
    return suffixNest.getType(wf.substring(root.length()));
  }

  /**
   * Get the set of types relevant to a word-form suffix
   * 
   * @param suffix
   *          the suffix of the wf
   * @return the set of types relevant to the word-form with this suffix
   */
  public Set<String> getTypeBySuffix(String suffix) {
    return suffixNest.getType(suffix);
  }

  /**
   * Fetch a set of the word-forms in the lemma.
   * 
   * @return the word-forms in this lemma
   */
  public Set<String> getWordForms() {
    Set<String> sufs = suffixNest.getSuffixes();
    Iterator<String> it = sufs.iterator();
    Set<String> wfs = new HashSet<String>();
    while(it.hasNext()) {
      wfs.add(root + it.next());
    }
    return wfs;
  } // / getWordForms()

  /**
   * Fetch a set of the word-form suffixes in the lemma according to the root.
   * 
   * @return the word-form suffixes in this lemma
   */
  public Set<String> getSuffixes() {
    return suffixNest.getSuffixes();
  } // / getSuffixes()

  /**
   * Fetch the set of word-form types in the lemma
   * 
   * @return the set of word-form types in the lemma
   */
  public Set<String> getTypes() {
    return typeVsSuffix.keySet();
  }

  /**
   * It is needed to adjust inflation suffixes while dynamically building the
   * root. adds to the suffixes in the internal representation.
   * 
   * @param prefix
   *          the prefix to be added
   */
  private void add2Suffixes(String prefix) {
    if(prefix == null || prefix.length() == 0) return;

    Iterator<String> ki = typeVsSuffix.keySet().iterator();
    String type;
    String suffix;
    while(ki.hasNext()) {
      type = ki.next();
      suffix = typeVsSuffix.get(type);
      typeVsSuffix.put(type, prefix + suffix);
    } // while keys

    suffixNest.addPrefix2Suffixes(prefix);
  } // add2Suffixes(prefix)

  /**
   * Adjusts the root according to a new word-form and returns the suffix of the
   * wf according to this new root. Accordingly adjusts the other suffixes in
   * the lemma if the root changes.
   * 
   * @param wf
   *          the new word-form
   * @return the suffix according to the new root
   */
  private String adjustRoot(String wf) {
    String suffix = "";

    // the common suffix as a part of the root for the wforms so far.
    String rootSuffix = "";

    if(root == null) {
      root = wf;
      return "";
    }
    if(root.length() < wf.length()) {
      suffix = wf.substring(root.length());
      wf = wf.substring(0, root.length());
    } else {
      if(root.length() > wf.length()) {
        rootSuffix = root.substring(wf.length());
        root = root.substring(0, wf.length());
      }
    } // else

    while(!root.equals(wf)) {
      rootSuffix = root.charAt(root.length() - 1) + rootSuffix;

      root = root.substring(0, root.length() - 1);

      suffix = wf.charAt(wf.length() - 1) + suffix;

      wf = wf.substring(0, wf.length() - 1);

    } // while root is found

    add2Suffixes(rootSuffix);

    return suffix;
  }// adjustRoot(wf)

  /**
   * Compares roots, suffixes and set of types - if all equal - considers the
   * objects are equal
   */
  public boolean equals(Object obj) {
    if(!(obj instanceof Lemma)) return false;
    Lemma l2 = (Lemma)obj;
    if(!l2.getRoot().equals(this.getRoot())) return false;
    if(!l2.getSuffixes().equals(this.getSuffixes())) return false;
    if(!l2.getTypes().equals(this.getTypes())) return false;
    return true;
  } // equals(obj)

  /**
   * Get the suffix nest associated with this lemma.
   * 
   * @return the nest
   */
  public SuffixNest getSuffixNest() {
    return suffixNest;
  }

  /**
   * Synchronizes the current nest with the pool of nests and sets the unique
   * nest to the lemma. To be called after finishing the incremental uploading
   * of suffixes and types to the lemma
   */
  public void synchWithSuffixPool() {
    suffixNest = SuffixPool.getDistinctNestAs(suffixNest);
  }

  /**
   * Calculates the difference between the specified name and this lemma in
   * terms of finding a common root and returns the count of characters
   * different at the suffix of the name compared to the lemma.
   * 
   * @param name
   * @return
   */
  public int difference2(String name) {
    int diff = 0;
    String mf = this.mainForm;
    if(mf.length() < name.length()) {
      diff = name.length() - mf.length();
      name = name.substring(0, mf.length());
    } else {
      if(mf.length() > name.length()) {
        diff = mf.length() - name.length();
        mf = mf.substring(0, name.length());
      }
    } // at this point both mf and name are with equal length
    // and diff has been accumulated
    while(!mf.equals(name)) {
      mf = mf.substring(0, mf.length() - 1);
      name = name.substring(0, name.length() - 1);
      diff++;
    } //

    return diff;
  } // difference2

} // class LemmaImpl