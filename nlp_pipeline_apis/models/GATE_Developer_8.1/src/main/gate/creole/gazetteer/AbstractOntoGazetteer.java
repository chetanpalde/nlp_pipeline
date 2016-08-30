/*
 * AbstractOntoGazetteer.java
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

/**AbstratOntoGazetteer
 * This class implements the methods common for all ontology-aware gazetteers.*/
public abstract class AbstractOntoGazetteer
extends AbstractGazetteer implements OntoGazetteer {

  private static final long serialVersionUID = 4883216941890639412L;

  /** the url of the mapping definition */
  protected java.net.URL mappingURL;

  /** class name of the linear gazetteer to be called */
  protected String gazetteerName;

  /** reference to the linear gazetteer */
  protected Gazetteer gaz;

  /**
   * Sets the class name of the linear gazetteer to be loaded.
   * @param name class name of a Gazetteer
   */
  @Override
  public void setGazetteerName(String name) {
    gazetteerName = name;
  }

  /**
   * Gets the class name of the linear gazetteer
   * @return the class name of the linear gazetteer
   */
  @Override
  public String getGazetteerName() {
    return gazetteerName;
  }

  /**
   * Sets the URL of the mapping definition
   * @param url the URL of the mapping definition
   */
  @Override
  public void setMappingURL(java.net.URL url) {
    mappingURL = url;
  }

  /**
   * Gets the URL of the mapping definition
   * @return the URL of the mapping definition
   */
  @Override
  public java.net.URL getMappingURL() {
    return mappingURL;
  }

  /**
   * Gets the linear gazetteer
   * @return the linear gazetteer
   */
  @Override
  public Gazetteer getGazetteer(){
    return gaz;
  }

  /**
   * Sets the linear gazetteer
   * @param gaze the linear gazetteer to be associated with this onto gazetteer.
   */
  @Override
  public void setGazetteer(Gazetteer gaze) {
    gaz = gaze;
  }

  /**Overrides {@link gate.creole.gazetteer.Gazetteer}
   * and retrieves the linear definition from the underlying
   * linear gazetteer*/
  @Override
  public LinearDefinition getLinearDefinition() {
    if (null == gaz){
      throw new gate.util.GateRuntimeException(
      "linear gazetteer should be set before \n"+
      "attempting to retrieve the linear definition");
    }
    return gaz.getLinearDefinition();
  }

} // class AbstractOntoGazetteer