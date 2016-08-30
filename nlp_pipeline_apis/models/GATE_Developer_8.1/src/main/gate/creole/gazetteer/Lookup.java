/*
 * Lookup.java
 * 
 * Copyright (c) 1995-2012, The University of Sheffield. See the file
 * COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 * 
 * This file is part of GATE (see http://gate.ac.uk/), and is free software,
 * licenced under the GNU Library General Public License, Version 2, June 1991
 * (in the distribution as file licence.html, and also available at
 * http://gate.ac.uk/gate/licence.html).
 * 
 * Valentin Tablan, 11/07/2000 borislav popov, 05/02/2002
 * 
 * $Id: Lookup.java 17593 2014-03-08 10:03:19Z markagreenwood $
 */
package gate.creole.gazetteer;

import gate.creole.ANNIEConstants;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Used to describe a type of lookup annotations. A lookup is described by a
 * major type a minor type and a list of languages. Added members are :
 * ontologyClass and list. All these values are strings (the list of languages
 * is a string and it is intended to represesnt a comma separated list). An
 * optional features field stores arbitary features as part of the lookup
 * annotation. This can be used to set meta-data for a gazetteer entry.
 */
public class Lookup implements Serializable {

  private static final long serialVersionUID = 4107354748136747541L;

  /** a map of arbitary features */
  public Map<String,Object> features = null;

  /**
   * Creates a new Lookup value with the given major and minor types and
   * languages.
   * 
   * @param major
   *          major type
   * @param minor
   *          minor type
   * @param theLanguages
   *          the languages
   * @param annotationType
   *          the annotation type to use for annotating this particular lookup.
   */
  public Lookup(String theList, String major, String minor,
      String theLanguages, String annotationType) {
    majorType = major;
    minorType = minor;
    languages = theLanguages;
    list = theList;
    this.annotationType = annotationType;
  }

  /**
   * Creates a new Lookup value with the given major and minor types and
   * languages.
   * 
   * @param major
   *          major type
   * @param minor
   *          minor type
   * @param theLanguages
   *          the languages
   */
  public Lookup(String theList, String major, String minor, String theLanguages) {
    this(theList, major, minor, theLanguages,
        ANNIEConstants.LOOKUP_ANNOTATION_TYPE);
  }

  /** Tha major type for this lookup, e.g. "Organisation" */
  public String majorType;

  /** The minor type for this lookup, e.g. "Company" */
  public String minorType;

  /** The languages for this lookup, e.g. "English, French" */
  public String languages;

  /**
   * the ontology class of this lookup according to the mapping between list and
   * ontology
   */
  public String oClass;

  /** the ontology ID */
  public String ontology;

  /** the list represented by this lookup */
  public String list;

  /** annotation type that should be used to create a lookup */
  public String annotationType;

  /**
   * Returns a string representation of this lookup in the format This method is
   * used in equals() that caused this method to implement dualistic behaviour :
   * i.e. whenever class and ontology are filled then use the long version,incl.
   * list, ontology and class; else return just majorType.minorType
   */
  @Override
  public String toString() {
    StringBuffer b = new StringBuffer();
    boolean longVersion = false;
    boolean hasArbitaryFeatures = false;
    if(null != ontology && null != oClass) {
      longVersion = true;
    }
    if(null != features) {
      hasArbitaryFeatures = true;
    }
    if(longVersion) {
      b.append(list);
      b.append(".");
    }
    b.append(majorType);
    b.append(".");
    if(null != minorType) {
      b.append(minorType);
      if(null != languages) {
        b.append(".");
        b.append(languages);
      }// if
    }// if
    if(longVersion) {
      b.append("|");
      b.append(ontology);
      b.append(":");
      b.append(oClass);
    }
    if(hasArbitaryFeatures) {
      // as the ordering of the featureMap is undefined, create a new list of
      // keys and sort it to ensure the string returned is always the same
      List<String> sortedKeys = new ArrayList<String>(features.keySet());
      Collections.sort(sortedKeys);
      for(Iterator<String> it = sortedKeys.iterator(); it.hasNext();) {
        String key = it.next();
        b.append("|");
        b.append(key);
        b.append(":");
        b.append(features.get(key));
      }
    }
    return b.toString();
  }

  /**
   * Two lookups are equal if they have the same string representation (major
   * type and minor type).
   * 
   * @param obj
   */
  @Override
  public boolean equals(Object obj) {
    if(obj instanceof Lookup)
      return obj.toString().equals(toString());
    else return false;
  } // equals

  /**
   * *
   */
  @Override
  public int hashCode() {
    return toString().hashCode();
  }
} // Lookup
