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
 *  Ian Roberts, 03/09/08
 *
 *  $Id: NotEqualPredicate.java 17599 2014-03-08 16:30:42Z markagreenwood $
 */
package gate.jape.constraint;

import gate.AnnotationSet;
import gate.jape.JapeException;

public class NotEqualPredicate extends EqualPredicate {

  private static final long serialVersionUID = 4065258361559210239L;

  @Override
  public String getOperator() {
    return NOT_EQUAL;
  }

  @Override
  public boolean doMatch(Object annotValue, AnnotationSet context) throws JapeException {
    return !super.doMatch(annotValue, context);
  }

}
