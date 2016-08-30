/*
 *  Copyright (c) 1995-2012, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 *  Eric Sword, 09/19/08
 *
 *  $Id$
 */
package gate.jape;

import org.apache.log4j.Logger;
import gate.jape.JapeConstants;

import java.io.Serializable;

/**
 * Representation of Kleene operators on expressions. Kleene operators
 * indicate the number of repetitions over an expression that are legal.
 * The most common are star (0 or more), plus (1 or more), and optional
 * (0 or 1, also referred to as "kleene query"). The class can also
 * represent a range with a fixed minimum, maximum, or both. Finally, a
 * default type of single (exactly 1) is also defined.
 *
 * @version $Revision$
 * @author esword
 */
public class KleeneOperator implements Serializable {

  private static final long serialVersionUID = 5590283708947918804L;

  protected static final Logger log = Logger.getLogger(KleeneOperator.class);

  /**
   * Enum containing the defined types of operators.
   *
   * @version $Revision$
   * @author esword
   */
  public enum Type
  {
      SINGLE(""),
      OPTIONAL("?"),
      STAR("*"),
      PLUS("+"),
      RANGE("[x,y]");

      private String symbol;

      Type( )
      {
      }

      Type( String symbol )
      {
          this.symbol = symbol;
      }

      public String getSymbol()
      {
          return( symbol );
      }

      /**
       * Conversion routine from the old JapeConstants values to
       * the type class.
       */
      public static KleeneOperator.Type getFromJapeConstant(int op) {
        switch(op) {
          case JapeConstants.NO_KLEENE_OP:
            return KleeneOperator.Type.SINGLE;
          case JapeConstants.KLEENE_PLUS:
            return KleeneOperator.Type.PLUS;
          case JapeConstants.KLEENE_STAR:
            return KleeneOperator.Type.PLUS;
          case JapeConstants.KLEENE_QUERY:
            return KleeneOperator.Type.OPTIONAL;
          default:
            throw new IllegalArgumentException("Unknown op code: " + op);
        }
      }

      /**
       * Conversion routine from the string symbol for a type to the type
       * Enum.
       */
      public static KleeneOperator.Type getFromSymbol(String symbol) {
        for(Type t : Type.values()) {
          if (t.getSymbol().equals(symbol))
            return t;
        }

        return null;
      }
  }

  private Type type;
  private Integer min;
  private Integer max;
  private String displayString;

  /**
   * Create an operator with the given type, setting the
   * appropriate min for each (and max when defined).  This constructor
   * should not be used for {@link Type#RANGE} operators.  Use one of
   * the other range-defining constructors in that case.
   * @param type
   */
  public KleeneOperator(Type type) {
    this.type = type;

    //setup min and max in case needed
    if (type == Type.SINGLE) {
      min = 1;
      max = 1;
    }
    else if (type == Type.OPTIONAL) {
      min = 0;
      max = 1;
    }
    else if (type == Type.STAR) {
      min = 0;
      //no set max
    }
    else if (type == Type.PLUS) {
      min = 1;
      //no set max
    }
    else if (type == Type.RANGE) {
      //default to 1 and 1.  Really, the other constructor should be used.
      min = 1;
      max = 1;
    }
  }

  /**
   * Create an operator with type RANGE and min and max both set to val.
   * @param val
   */
  public KleeneOperator(Integer val) {
    this(val, val);
  }

  /**
   * Create an operator with type RANGE and the given min and max.
   * @param min
   * @param max
   */
  public KleeneOperator(Integer min, Integer max) {
    this.type = Type.RANGE;

    if (min != null && max != null && min > max)
      throw new IllegalArgumentException("min cannot be greater than max: " + min + ", " + max);

    this.min = min;
    this.max = max;
  }

  /**
   * The string representation for most operators is the operator symbol itself.  For ranges, the
   * min and max are shown.  If min == max, only one is shown.
   */
  @Override
  public String toString() {
    if (type != Type.RANGE) return type.getSymbol();
        
    if(displayString != null) return displayString;

    StringBuilder sb = new StringBuilder("[");
    sb.append(getMin());
    if(!min.equals(max)) {
      sb.append(",");
      if(max != null) sb.append(max);
    }
    sb.append("]");

    displayString = sb.toString();
    
    return displayString;
  }

  /*
   * =================================================================================================
   * Getters and Setters
   * =================================================================================================
   */
  public Type getType() {
    return type;
  }

  public void setType(Type type) {
    this.type = type;
  }

  public Integer getMin() {
    return min;
  }

  public Integer getMax() {
    return max;
  }
}
