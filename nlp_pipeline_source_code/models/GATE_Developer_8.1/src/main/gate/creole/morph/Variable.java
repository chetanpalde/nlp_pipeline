/*
 *  Copyright (c) 1995-2012, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 *  Niraj Aswani, 21/10/2003
 *
 *  $Id: Variable.java 15333 2012-02-07 13:18:33Z ian_roberts $
 */

package gate.creole.morph;

/**
 * <p>Description: This is an interface which should be implemented by every
 * new variable type. Variable here is considered to have more than one values.
 * Example of built-in varilables for morpher are StringSet, CharacterRange,
 * CharacterSet.
 * </p>
 */
public abstract class Variable {

  protected int pointer = 0;

  /** name of the variable */
  protected String varName = "";

  /** value of the variable */
  protected String varValue = "";

  /** method tells if next element is available to fetch */
  public abstract boolean hasNext();

  /** should return the next element in the variable */
  public abstract String next();

  /** Sets the variable name and pattern for the variable */
  public abstract boolean set(String varName, String pattern);

  /** should tell variable has one of the values with varValue */
  public abstract boolean contains(String varValue);

  /**
   * this method returns the formatted pattern, which could be recognized
   * by the regular expressions
   */
  public String getPattern() {
    return varValue;
  }

  /**
   * resets the pointer to the begining
   */
  public void resetPointer() {
    pointer = 0;
  }
}