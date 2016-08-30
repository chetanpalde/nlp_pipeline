/*
 * Gazetteer.java
 *
 * Copyright (c) 2002, The University of Sheffield.
 *
 * This file is part of GATE (see http://gate.ac.uk/), and is free
 * software, licenced under the GNU Library General Public License,
 * Version 2, June1991.
 *
 * A copy of this licence is included in the distribution in the file
 * licence.html, and is also available at http://gate.ac.uk/gate/licence.html.
 *
 * borislav popov 02/2002
 *
 */
 package gate.creole.gazetteer;

import java.util.Set;

/**The Gazetteer interface defines the mandatory methods of a gazetteer PR. */
public interface Gazetteer extends gate.LanguageAnalyser,gate.ProcessingResource {

  /** Looks-up a string
   * @param singleItem the string
   * @return set of Lookups
   */
  public Set<Lookup> lookup(String singleItem);

  /**
   * Sets the AnnotationSet that will be used at the next run for the newly
   * produced annotations.
   * @param newAnnotationSetName the annotation set name for
   * the annotations that are going to be produced
   */
  public void setAnnotationSetName(String newAnnotationSetName);

  /**
   * Gets the AnnotationSet that will be used at the next run for the newly
   * produced annotations.
   * @return the current AnnotationSet name
   */
  public String getAnnotationSetName() ;

  public void setEncoding(String newEncoding);

  public String getEncoding() ;

  /**Gets the url of the lists.def file
   * @return the url of the lists.def file  */
  public java.net.URL getListsURL() ;

  /**Sets the url of the lists.def file
   * @param newListsURL the url of the lists.def file to be set  */
  public void setListsURL(java.net.URL newListsURL) ;

  /**Triggers case sensitive
   * @param newCaseSensitive turn on or off case sensitivity */
  public void setCaseSensitive(Boolean newCaseSensitive) ;

  /**Gets the current case sensitivity
   * @return the current case sensitivity */
  public Boolean getCaseSensitive();

  /**Sets the mapping definition if such to this gazetteer
   * @param mapping a mapping definition */
  public void setMappingDefinition(MappingDefinition mapping);

  /**Gets the mapping definition of this gazetteer,if such
   * @return the mapping definition of this gazetteer,if such otherwise null   */
  public MappingDefinition getMappingDefinition();

  /**Gets the linear definition of this gazetteer. There is no parallel
   * set method because the definition is laoded through the listsUrl
   * on init().
   * @return the linear definition of the gazetteer */
  public LinearDefinition getLinearDefinition();

  /**
   * Fires a Gazetteer Event
   * @param ge Gazetteer Event to be fired
   */
  public void fireGazetteerEvent(GazetteerEvent ge) ;

  /**
   * Registers a Gazetteer Listener
   * @param gl Gazetteer Listener to be registered
   */
  public void addGazetteerListener(GazetteerListener gl);

  /**
   * Adds a new string to the gazetteer
   * @param singleItem
   * @param lookup the lookup to be associated with the new string
   * @return true if the operation was successful
   */
  boolean add(String singleItem, Lookup lookup);

  /**
   * Removes a string from the gazetteer
   * @param singleItem
   * @return true if the operation was successful
   */
  boolean remove(String singleItem);
}//interface Gazetteer