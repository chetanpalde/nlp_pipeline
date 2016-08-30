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

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Abstract regular expression based predicate implementation. This
 * class handles parsing of the regexp into a {@link Pattern} object,
 * and at match time it creates a {@link Matcher} for the annotation
 * value. Concrete subclasses define the different criteria for what
 * counts as a "match" in terms of {@link Matcher#find()} and
 * {@link Matcher#matches()}.
 */
public abstract class AbstractRegExpPredicate
                                             extends
                                               AbstractConstraintPredicate {

  private static final long serialVersionUID = 8218973149540251171L;

  @Override
  public String toString() {
    String val = ((Pattern)getValue()).pattern();
    return getAccessor() + getOperator() + "\"" + val + "\"";
  }

  @Override
  public void setValue(Object value) {
    if(value == null) value = "";
    try {
      super.setValue(Pattern.compile(value.toString()));
    }
    catch(PatternSyntaxException pse) {
      throw new IllegalArgumentException("Cannot compile pattern '" + value
              + "'");
    }
  }

  /**
   * Returns true if the given value matches the set pattern. If the
   * value is null it is treated as an empty string. The actual matching
   * logic is defined by {@link #matcherResult}.
   */
  @Override
  public boolean doMatch(Object annotValue, AnnotationSet context)
          throws JapeException {

    if(annotValue == null) annotValue = "";

    if(annotValue instanceof String) {
      String annotValueString = (String)annotValue;
      Pattern constraintPattern = (Pattern)getValue();
      return matcherResult(constraintPattern.matcher(annotValueString));
    }
    else {
      throw new JapeException("Cannot do pattern matching on attribute '"
              + getAccessor() + "'.  Are you sure the value is a string?");
    }
  }

  /**
   * Must be implemented by subclasses to define the matching logic,
   * typically one of {@link Matcher#find()} and
   * {@link Matcher#matches()}, possibly negated.
   * 
   * @param m a {@link Matcher} for the annotation value string,
   *          obtained from the constraint pattern.
   * @return true if this constraint should be considered to match,
   *         false otherwise.
   */
  protected abstract boolean matcherResult(Matcher m);
}
