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

import gate.jape.JapeException;

public class LesserEqualPredicate extends ComparablePredicate {

  private static final long serialVersionUID = 4844219899117114197L;

  @Override
  public String getOperator() {
    return LESSER_OR_EQUAL;
  }

  /**
   * Check if passed value is less than or equal to stored value using
   * {@link Comparable} operations. Will attempt to do basic type
   * conversion between the values. Returns false if passed value is
   * null.
   */
  @Override
  protected boolean doMatch(Object annotValue) throws JapeException {
    return compareValue(annotValue) >= 0;
  }

}
