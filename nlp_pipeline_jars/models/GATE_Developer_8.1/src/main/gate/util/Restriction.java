/*
 *  Restriction.java
 *
 *  Copyright (c) 1995-2012, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 *  Rosen Marinov, 10/Dec/2001
 *
 *  $Id: Restriction.java 17600 2014-03-08 18:47:11Z markagreenwood $
 */

package gate.util;

import java.io.Serializable;

public class Restriction implements Serializable{

  private static final long serialVersionUID = -1266771900567902681L;

  /* Type of operator for comparision in query*/
  public static final int OPERATOR_EQUATION = 100;
  public static final int OPERATOR_LESS = 101;
  public static final int OPERATOR_BIGGER = 102;
  public static final int OPERATOR_EQUATION_OR_BIGGER = 103;
  public static final int OPERATOR_EQUATION_OR_LESS = 104;
  public static final int OPERATOR_LIKE = 105;

  private Object value;
  private String key;
  private int    operator_;

  /** Constructor.
   *
   * @param key string value of a feature key in document.
   * @param value value of a feature with this key
   * @param operator_  type of operator for comparison in query
   *
   */
  public Restriction(String key, Object value, int operator_){
    this.key = key;
    this.value = value;
    this.operator_ = operator_;
  }

  /**
   * @return Object value of feature
   */
  public Object getValue(){
    return value;
  }

  /** @return String string value og feature */
  public String getStringValue(){
    return value.toString();
  }

  /** @return String string value of the feature key  */
  public String getKey(){
    return key;
  }

  /** @return int type of operator */
  public int getOperator(){
    return operator_;
  }
}