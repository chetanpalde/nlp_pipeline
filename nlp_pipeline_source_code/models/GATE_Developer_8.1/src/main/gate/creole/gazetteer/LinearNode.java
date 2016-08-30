/*
 * LinearNode.java
 * 
 * Copyright (c) 2002, The University of Sheffield.
 * 
 * This file is part of GATE (see http://gate.ac.uk/), and is free software,
 * licenced under the GNU Library General Public License, Version 2, June1991.
 * 
 * A copy of this licence is included in the distribution in the file
 * licence.html, and is also available at http://gate.ac.uk/gate/licence.html.
 * 
 * borislav popov 02/2002
 */
package gate.creole.gazetteer;

import gate.creole.ANNIEConstants;

/**
 * Linear node specifies an entry of the type : list:major:minor:language:annotationType
 */
public class LinearNode {
  /** the gazetteer list from the node */
  private String list;

  /** the minor type from the node */
  private String minor;

  /** the major type from the node */
  private String major;

  /** the languages member from the node */
  private String language;

  /** the annotationType member from the node **/
  private String annotationType;

  /**
   * Constructor
   * 
   * @param list
   *          name of the list
   * @param minor
   *          minor type
   * @param major
   *          major type
   * @param language
   *          language feature
   * @param annotationType
   *          the annotation type that should be used for annotating mentions of
   *          entries from the list
   */
  public LinearNode(String list, String minor, String major, String language,
      String annotationType) {
    this.list = list;
    this.minor = minor;
    this.major = major;
    this.language = language;
    this.annotationType = annotationType;
  }

  /**
   * Constructs a linear node given its elements
   * 
   * @param aList
   *          the gazetteer list file name
   * @param aMajor
   *          the major type
   * @param aMinor
   *          the minor type
   * @param aLanguage
   *          the language(s)
   */
  public LinearNode(String aList, String aMajor, String aMinor, String aLanguage) {
    this(aLanguage, aMajor, aMinor, aLanguage, null);
  } // LinearNode construct

  /**
   * Parses and create a linear node from a string
   * 
   * @param node
   *          the linear node to be parsed
   * @throws InvalidFormatException
   */
  public LinearNode(String node) throws InvalidFormatException {
    int firstColon = node.indexOf(':');
    int secondColon = node.indexOf(':', firstColon + 1);
    int thirdColon = node.indexOf(':', secondColon + 1);
    int fourthColon = node.indexOf(':', thirdColon + 1);
    annotationType = ANNIEConstants.LOOKUP_ANNOTATION_TYPE; // default value
                                                            // must be lookup
                                                            // for backword
                                                            // compatibility
    if(firstColon == -1) { throw new InvalidFormatException("", "Line: " + node); }
    list = node.substring(0, firstColon);
    if(secondColon == -1) {
      major = node.substring(firstColon + 1);
      minor = null;
      language = null;
    } else {
      major = node.substring(firstColon + 1, secondColon);
      if(thirdColon == -1) {
        minor = node.substring(secondColon + 1);
        language = null;
      } else {
        minor = node.substring(secondColon + 1, thirdColon);
        if(fourthColon == -1) {
          language = node.substring(thirdColon + 1);
          annotationType = ANNIEConstants.LOOKUP_ANNOTATION_TYPE;
        } else {
          language = node.substring(thirdColon + 1, fourthColon);
          annotationType = node.substring(fourthColon + 1);
        }
      }
    } // else
  } // LinearNode concstruct

  /**
   * Get the gazetteer list filename from the node
   * 
   * @return the gazetteer list filename
   */
  public String getList() {
    return list;
  }

  /**
   * Sets the gazetteer list filename for the node
   * 
   * @param aList
   *          the gazetteer list filename
   */
  public void setList(String aList) {
    list = aList;
  }

  /**
   * Gets the language of the node (the language is optional)
   * 
   * @return the language of the node
   */
  public String getLanguage() {
    return language;
  }

  /**
   * Sets the language of the node
   * 
   * @param aLanguage
   *          the language of the node
   */
  public void setLanguage(String aLanguage) {
    language = aLanguage;
  }

  /**
   * Gets the minor type
   * 
   * @return the minor type
   */
  public String getMinorType() {
    return minor;
  }

  /**
   * Sets the minor type
   */
  public void setMinorType(String minorType) {
    minor = minorType;
  }

  /**
   * Gets the major type
   * 
   * @return the major type
   */
  public String getMajorType() {
    return major;
  }

  /**
   * Sets the major type
   * 
   * @param majorType
   *          the major type
   */
  public void setMajorType(String majorType) {
    major = majorType;
  }

  /**
   * Gets the annotationType
   * 
   * @return the annotationType
   */
  public String getAnnotationType() {
    return annotationType;
  }

  /**
   * Sets the annotaionType
   * 
   * @param annotationType
   */
  public void setAnnotationType(String annotationType) {
    this.annotationType = annotationType;
  }

  /**
   * Gets the string representation of this node
   * 
   * @return the string representation of this node
   */
  @Override
  public String toString() {
    String result = list + ':' + major;
    if((null != minor) && (0 != minor.length())) result += ':' + minor;
    if((null != language) && (0 != language.length())) {
      if((null == minor) || (0 == minor.length())) result += ':';
      result += ':' + language;
    }
    // if the annotation type is Lookup we don't really need to add
    // it to the definition file
    if((null != annotationType) && (0 != annotationType.length())
        && !annotationType.equals(ANNIEConstants.LOOKUP_ANNOTATION_TYPE)) {
      if((null == minor) || (0 == minor.length())) result += ':';
      if(language == null || (0 == language.length())) result += ':';
      result += ':' + annotationType;
    }
    return result;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result =
        prime * result
            + ((annotationType == null) ? 0 : annotationType.hashCode());
    result = prime * result + ((language == null) ? 0 : language.hashCode());
    result = prime * result + ((list == null) ? 0 : list.hashCode());
    result = prime * result + ((major == null) ? 0 : major.hashCode());
    result = prime * result + ((minor == null) ? 0 : minor.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if(this == obj) return true;
    if(obj == null) return false;
    if(getClass() != obj.getClass()) return false;
    LinearNode other = (LinearNode)obj;
    if(annotationType == null) {
      if(other.annotationType != null) return false;
    } else if(!annotationType.equals(other.annotationType)) return false;
    if(language == null) {
      if(other.language != null) return false;
    } else if(!language.equals(other.language)) return false;
    if(list == null) {
      if(other.list != null) return false;
    } else if(!list.equals(other.list)) return false;
    if(major == null) {
      if(other.major != null) return false;
    } else if(!major.equals(other.major)) return false;
    if(minor == null) {
      if(other.minor != null) return false;
    } else if(!minor.equals(other.minor)) return false;
    return true;
  }
} // class LinearNode