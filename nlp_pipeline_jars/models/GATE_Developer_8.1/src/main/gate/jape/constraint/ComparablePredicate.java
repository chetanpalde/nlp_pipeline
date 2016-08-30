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

/**
 * Base class for those predicates which use <code>compareTo</code> to
 * compare values.
 *
 * @author esword
 */
@SuppressWarnings({"unchecked","rawtypes"})
public abstract class ComparablePredicate extends AbstractConstraintPredicate {

  private static final long serialVersionUID = -5667456294601338967L;
  
  protected Comparable comparableValue;

  /**
   * Value must be a Comparable
   */
  @Override
  public void setValue(Object value) {
    if(!(value instanceof Comparable)) {
      String classString = (value == null) ? "null" : value.getClass()
              .toString();
      throw new IllegalArgumentException("Value for attribute '"
              + getAccessor() + "' must be a Comparable type, not a "
              + classString);
    }
    comparableValue = (Comparable)value;

    super.setValue(value);
  }

  @Override
  public boolean doMatch(Object value, AnnotationSet context) throws JapeException {
    if(value == null) return false;

    return doMatch(value);
  }

  protected abstract boolean doMatch(Object featureValue) throws JapeException;

  /**
   * Use <code>compareTo</code> to compare set value with the given
   * object, doing basic type conversion to get the two objects to the
   * same class.
   * @throws JapeException if the provided object is not a Comparable or
   *           the classes cannot be compared.
   */
  protected int compareValue(Object obj) throws JapeException {

    if(!(obj instanceof Comparable)) {
      String classString = (obj == null) ? "null" : obj.getClass().toString();
      throw new JapeException("Value passed to compare to attribute '"
              + getAccessor() + "' must be a Comparable type, not a "
              + classString);
    }
    Comparable passedValue = (Comparable)obj;

    try {
      return comparableValue.compareTo(passedValue);
    }
    catch(ClassCastException notSameType) {
      try {
        // try to compare as Longs
        if(comparableValue instanceof Long) {
          return comparableValue.compareTo(Long.valueOf(passedValue.toString()));
        }

        // try to compare as Double
        if(comparableValue instanceof Double) {
          return comparableValue.compareTo(Double.valueOf(passedValue.toString()));
        }

        // can't compare
        throw new JapeException("Cannot compare values for attribute '"
                + getAccessor() + "' because cannot compare '"
                + comparableValue + "' to '" + passedValue + "'.");
      } // try
      catch(NumberFormatException nfe) {
        // stored value is a Long/Double, but annot is not: cannot
        // compare
        throw new JapeException("Cannot compare values for attribute '"
                + getAccessor() + "' because cannot compare '"
                + comparableValue + "' to '" + passedValue + "'.");
      }

    } // catch notSameType

  }

}
