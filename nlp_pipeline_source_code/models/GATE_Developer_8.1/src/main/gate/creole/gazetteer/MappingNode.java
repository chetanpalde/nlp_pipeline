/*
 * MappingNode.java
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

import java.io.Serializable;



/**Represents a single node from the mapping definition*/
public class MappingNode implements Serializable {

  private static final long serialVersionUID = -4410243081697344856L;

  /** the gazetteer list filename */
  private String list;
  /** the class associated with the list */
  private String classID;
  /** the ontology to which the class belongs */
  private String ontologyID;

  /**Creates a new mapping node given a string representation.
   * @param node a node from the mapping definition
   * @throws InvalidFormatException if the node is misformatted
   */
  public MappingNode(String node) throws InvalidFormatException {
    int firstColumn = node.indexOf(':');
    int lastColumn = node.lastIndexOf(':');
    if (-1 == firstColumn || -1 == lastColumn ) {
      throw new InvalidFormatException();
    }
    list = node.substring(0,firstColumn);
    ontologyID = node.substring(firstColumn+1,lastColumn);
    classID = node.substring(lastColumn+1);
  }// MappingNode construct

  /**Creates a new mapping node given its members
   * @param aList the gaz list file name
   * @param anOntologyID the ontology
   * @param aClassID the class
   */
  public MappingNode(String aList, String anOntologyID,String aClassID) {
    list = aList;
    classID = aClassID;
    ontologyID = anOntologyID;
  }

  /**Sets gaz list for the node
   * @param aList a gazetteer list file name */
  public void setList(String aList) {
    list = aList;
  }

  /** Gets the list of the node
   *  @return the gazetteer list file name*/
  public String getList(){
    return list;
  }

  /** Sets the class ID
   * @param theClassID  the class id */
  public void setClassID(String theClassID) {
    classID = theClassID;
  }

  /** Gets the class id
   *  @return the class id  */
  public String getClassID(){
    return classID;
  }

  /** Sets the ontology id
   *  @param id the ontology id */
  public void setOntologyID(String id) {
    ontologyID = id;
  }

  /** Gets the ontology id
   *  @return the ontology id  */
  public String getOntologyID(){
    return ontologyID;
  }

  /**
   * Gets the string representation of the node
   * @return the string representation of the node
   */
  @Override
  public String toString() {
    return list + ":" + ontologyID + ":" + classID;
  }
} // class MappingNode