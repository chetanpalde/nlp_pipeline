/*
 *  ConstraintPredicate - transducer class
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

import gate.Annotation;
import gate.AnnotationSet;
import gate.jape.JapeException;

import java.io.Serializable;

/**
 * A predicate defines a single boolean operation on an
 * {@link gate.Annotation} or some property of an annotation. These are
 * also referred to as attributes of a constraint.
 * <p>
 * Implementors will determine if a provided annotation matches the
 * predicate based on the intent of the operator (equals, not equals,
 * greater than, etc).
 *
 * @version $Revision$
 * @author esword
 */
public interface ConstraintPredicate extends Serializable {

  // Standard operators. Note that this was purposefully not done as an
  // enum so that additional operators could be added dynamically for other
  // parsers
  public String EQUAL = "==";

  public String NOT_EQUAL = "!=";
  
  public String GREATER = ">";

  public String LESSER = "<";

  public String GREATER_OR_EQUAL = ">=";

  public String LESSER_OR_EQUAL = "<=";

  public String REGEXP_FIND = "=~";
  
  public String NOT_REGEXP_FIND = "!~";
  
  public String REGEXP_MATCH = "==~";

  public String NOT_REGEXP_MATCH = "!=~";

  /**
   * The accessor associated with this predicate.
   */
  public AnnotationAccessor getAccessor();

  /**
   * Set the accessor associated with this predicate.
   */
  public void setAccessor(AnnotationAccessor accessor);

  /**
   * The value used in comparisons against passed in data in
   * {@link #matches(Annotation, AnnotationSet)}.
   */
  public Object getValue();

  /**
   * Set the value used in comparisons against passed in data in
   * {@link #matches(Annotation, AnnotationSet)}.
   */
  public void setValue(Object value);

  /**
   * String representation of the logic operator that the predicate
   * implements.
   */
  public String getOperator();

  /**
   * Evaluates if the provided annotation meets the requirements of the
   * predicate.
   * @throws JapeException
   */
  public boolean matches(Annotation annot, AnnotationSet context) throws JapeException;
}
