/*
 *  Constraint Predicate implementation
 *
 *  Copyright (c) 1995-2012, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 *  Eric Sword, 03/09/08
 *
 *  $Id$
 */
package gate.jape.constraint;

import gate.AnnotationSet;
import gate.jape.JapeException;

public class EqualPredicate extends AbstractConstraintPredicate {

  private static final long serialVersionUID = 3833632644179230280L;

  @Override
  public String getOperator() {
    return EQUAL;
  }

  @Override
  public boolean doMatch(Object annotValue, AnnotationSet context)
          throws JapeException {

    if(value == null && annotValue != null) return false;

    if(value.equals(annotValue)) return true;

    /*
     * The stored value can be String/Long/Double/Boolean. The passed
     * value must be of the same type, otherwise equals will return
     * false. In that case, let's suppose the annot's attrib. is a
     * String and let's try to convert it to the same type as the
     * constraint.
     */
    if(annotValue instanceof String && !(value instanceof String)) {
      String annotValueString = (String)annotValue;

      try {
        if(value instanceof Long)
          return value.equals(Long.valueOf(annotValueString));

        if(value instanceof Double)
          return value.equals(Double.valueOf(annotValueString));

        if(value instanceof Boolean)
          return value.equals(Boolean.valueOf(annotValueString));

        // if we reach that point, it means constraint has an unexpected
        // type!
        throw new JapeException("Cannot compare values for attribute '"
                + getAccessor() + "' because cannot compare '" + value
                + "' to '" + annotValue + "'.");
      }
      catch(NumberFormatException otherType) {
        // annotValue is a String and cannot be converted to
        // Long/Double/Boolean, so the two cannot be equal
        return false;
      }
    } // if String

    return false;
  }

}
