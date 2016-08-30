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
 *  $Id: NotRegExpFindPredicate.java 17599 2014-03-08 16:30:42Z markagreenwood $
 */
package gate.jape.constraint;

import java.util.regex.Matcher;

/**
 * Implementation of the =~ predicate, which fails if any part of the
 * annotation value matches the given regular expression, and succeeds
 * otherwise.
 */
public class NotRegExpFindPredicate extends AbstractRegExpPredicate {

  private static final long serialVersionUID = -2958686314966008138L;

  @Override
  public String getOperator() {
    return NOT_REGEXP_FIND;
  }

  @Override
  protected boolean matcherResult(Matcher m) {
    return !m.find();
  }

}
