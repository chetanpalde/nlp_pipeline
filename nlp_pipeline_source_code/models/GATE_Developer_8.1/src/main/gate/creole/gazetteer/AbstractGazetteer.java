/*
 * AbstractGazetteer.java
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

import gate.creole.AbstractLanguageAnalyser;
import gate.creole.ResourceInstantiationException;
import gate.creole.metadata.CreoleParameter;
import gate.creole.metadata.Optional;
import gate.creole.metadata.RunTime;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**AbstractGazetteer
 * This class implements the common-for-all methods of the Gazetteer interface*/
public abstract class AbstractGazetteer
  extends AbstractLanguageAnalyser implements Gazetteer {

  private static final long serialVersionUID = 223125105523762358L;

  /** the set of gazetteer listeners */
  protected Set<GazetteerListener> listeners = new HashSet<GazetteerListener>();

  /** Used to store the annotation set currently being used for the newly
   * generated annotations*/
  protected String annotationSetName;

  /** the encoding of the gazetteer */
  protected String encoding = "UTF-8";

  /**
   * The value of this property is the URL that will be used for reading the
   * lists that define this Gazetteer
   */
  protected java.net.URL listsURL;

  /**
   * Should this gazetteer be case sensitive. The default value is true.
   */
  protected Boolean caseSensitive = new Boolean(true);

  /**
   * Should this gazetteer only match whole words. The default value is
   * <tt>true</tt>.
   */
  protected Boolean wholeWordsOnly = new Boolean(true);

  /**
   * Should this gazetteer only match the longest string starting from any 
   * offset? This parameter is only relevant when the list of lookups contains
   * proper prefixes of other entries (e.g when both &quot;Dell&quot; and 
   * &quot;Dell Europe&quot; are in the lists). The default behaviour (when this
   * parameter is set to <tt>true</tt>) is to only match the longest entry, 
   * &quot;Dell Europe&quot; in this example. This is the default GATE gazetteer
   * behaviour since version 2.0. Setting this parameter to <tt>false</tt> will 
   * cause the gazetteer to match all possible prefixes.
   */
  protected Boolean longestMatchOnly = new Boolean(true);
  
  /** the linear definition of the gazetteer */
  protected LinearDefinition definition;

  /** reference to mapping definition info
   *  allows filling of Lookup.ontologyClass according to a list*/
  protected MappingDefinition mappingDefinition;


  /**
   * Sets the AnnotationSet that will be used at the next run for the newly
   * produced annotations.
   */
  @Override
  @RunTime
  @Optional
  @CreoleParameter(comment="The annotation set to be used for the generated annotations")
  public void setAnnotationSetName(String newAnnotationSetName) {
    annotationSetName = newAnnotationSetName;
  }

  /**
   * Gets the AnnotationSet that will be used at the next run for the newly
   * produced annotations.
   */
  @Override
  public String getAnnotationSetName() {
    return annotationSetName;
  }

  @Override
  @CreoleParameter(comment="The encoding used for reading the definitions", defaultValue="UTF-8")
  public void setEncoding(String newEncoding) {
    encoding = newEncoding;
  }

  @Override
  public String getEncoding() {
    return encoding;
  }

  @Override
  public java.net.URL getListsURL() {
    return listsURL;
  }

  @Override
  @CreoleParameter(comment="The URL to the file with list of lists", suffixes="def", defaultValue="resources/gazetteer/lists.def")
  public void setListsURL(java.net.URL newListsURL) {
    listsURL = newListsURL;
  }

  @Override
  @CreoleParameter(comment="Should this gazetteer diferentiate on case?", defaultValue="true")
  public void setCaseSensitive(Boolean newCaseSensitive) {
    caseSensitive = newCaseSensitive;
  }

  @Override
  public Boolean getCaseSensitive() {
    return caseSensitive;
  }

  @Override
  public void setMappingDefinition(MappingDefinition mapping) {
    mappingDefinition = mapping;
  }

  @Override
  public MappingDefinition getMappingDefinition(){
    return mappingDefinition;
  }
  
  /**
   * @return the longestMatchOnly
   */
  public Boolean getLongestMatchOnly() {
    return longestMatchOnly;
  }

  /**
   * @param longestMatchOnly the longestMatchOnly to set
   */
  @RunTime
  @CreoleParameter(comment="Should this gazetteer only match the longest string starting from any offset?", defaultValue="true")
  public void setLongestMatchOnly(Boolean longestMatchOnly) {
    this.longestMatchOnly = longestMatchOnly;
  }

  /**Gets the linear definition of this gazetteer. there is no parallel
   * set method because the definition is loaded through the listsUrl
   * on init().
   * @return the linear definition of the gazetteer */
  @Override
  public LinearDefinition getLinearDefinition() {
    return definition;
  }

  @Override
  public void reInit() throws ResourceInstantiationException {
    super.reInit();
    fireGazetteerEvent(new GazetteerEvent(this,GazetteerEvent.REINIT));
  }//reInit()

  /**
   * fires a Gazetteer Event
   * @param ge Gazetteer Event to be fired
   */
  @Override
  public void fireGazetteerEvent(GazetteerEvent ge) {
    Iterator<GazetteerListener> li = listeners.iterator();
    while ( li.hasNext()) {
      GazetteerListener gl = li.next();
      gl.processGazetteerEvent(ge);
    }
  }

  /**
   * Registers a Gazetteer Listener
   * @param gl Gazetteer Listener to be registered
   */
  @Override
  public void addGazetteerListener(GazetteerListener gl){
    if ( null!=gl )
      listeners.add(gl);
  }

  /**
   * Gets the value for the {@link #wholeWordsOnly} parameter.
   * @return a Boolean value.
   */
  public Boolean getWholeWordsOnly() {
    return wholeWordsOnly;
  }

  /**
   * Sets the value for the {@link #wholeWordsOnly} parameter.
   * @param wholeWordsOnly a Boolean value.
   */
  @RunTime
  @CreoleParameter(comment="Should this gazetteer only match whole words?", defaultValue="true")
  public void setWholeWordsOnly(Boolean wholeWordsOnly) {
    this.wholeWordsOnly = wholeWordsOnly;
  }

}//class AbstractGazetteer