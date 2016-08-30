/*
 *  TextualDocumentFormat.java
 *
 *  Copyright (c) 1995-2012, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 *  Valentin Tablan 27 Aug 2003
 *
 *  $Id: MimeType.java 17530 2014-03-04 15:57:43Z markagreenwood $
 */

package gate.corpora;

import java.util.HashMap;
import java.util.Map;

/**
 * A very basic implementation for a MIME Type.
 */
public class MimeType {
  /**
   * Constructor from type and subtype.
   * @param type
   * @param subType
   */
  public MimeType(String type, String subType){
    this.type = type;
    this.subtype = subType;
    parameters = new HashMap<String,String>();
  }

  /**
   * Two MIME Types are equal if their types and subtypes coincide.
   * @param other the othe MIME Type to be compared with this one.
   * @return true if the two MIME Types are the same.
   */
  @Override
  public boolean equals(Object other){
    return other != null && type.equals(((MimeType)other).getType()) &&
           subtype.equals(((MimeType)other).getSubtype());
  }

  /**
   * The hashcode is composed (by addition) from the hashcodes for the type and
   * subtype.
   * @return and integer.
   */
  @Override
  public int hashCode(){
    return (type == null ? 0 : type.hashCode()) +
            (subtype == null ? 0 : subtype.hashCode());
  }

  /**
   * Returns the type component of this MIME Type.
   * @return a String value.
   */
  public String getType() {
    return type;
  }

  /**
   * Sets the type component of this MIME type.
   * @param type a String value.
   */
  public void setType(String type) {
    this.type = type;
  }

  /**
   * Returns the subtype component of this MIME Type.
   * @return a String value.
   */
  public String getSubtype() {
    return subtype;
  }

  /**
   * Sets the subtype component of this MIME type.
   * @param subtype a String value.
   */
  public void setSubtype(String subtype) {
    this.subtype = subtype;
  }

  /**
   * Adds (and replaces if necessary) a parameter to this MIME type.
   * @param param the name of the parameter.
   * @param value the value of the parameter.
   */
  public void addParameter(String param, String value){
    parameters.put(param, value);
  }

  /**
   * Gets the value for a particular parameter.
   * @param name the name of the parameter.
   * @return a {@link java.lang.String} value.
   */
  public String getParameterValue(String name){
    return parameters.get(name);
  }

  /**
   * Checks to see if this MIME type has a particular parameter.
   * @param name the name of the parameter.
   * @return a boolean value.
   */
  public boolean hasParameter(String name){
    return parameters.containsKey(name);
  }

  /**
   * The type component
   */
  protected String type;

  /**
   * The subtype component
   */
  protected String subtype;

  /**
   * The parameters map.
   */
  protected Map<String,String> parameters;
}