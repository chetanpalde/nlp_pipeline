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
 *  Eric Sword, 09/03/08
 *
 *  $Id$
 */
package gate.jape.constraint;

import java.util.Collection;
import java.util.Collections;

import gate.Annotation;
import gate.AnnotationSet;
import gate.jape.Constraint;
import gate.jape.JapeException;

/**
 * Predicate whose {@link #getValue()} property may be set to a
 * Constraint itself, allowing for recursive evaluations.
 *
 * @author esword
 */
public abstract class EmbeddedConstraintPredicate extends AbstractConstraintPredicate {

  private static final long serialVersionUID = 7472456256804424432L;

  protected Constraint valueConstraint;
  protected String annotType;

  public EmbeddedConstraintPredicate() {
    super();
  }

  public EmbeddedConstraintPredicate(AnnotationAccessor accessor, Object value) {
    super(accessor, value);
  }

  /**
   * Sets up environment for concrete class to do the specific matching check
   */
  @Override
  public boolean doMatch(Object annotValue, AnnotationSet context)
          throws JapeException {

    Annotation annot = (Annotation)annotValue;
    AnnotationSet containedSet = doMatch(annot, context);

    Collection<Annotation> filteredSet = filterMatches(containedSet);

    return !filteredSet.isEmpty();
  }

  protected abstract AnnotationSet doMatch(Annotation annot, AnnotationSet as);

  /**
   * If there are attribute constraints, filter the set.
   */
  protected Collection<Annotation> filterMatches(AnnotationSet containedSet) {
    if (containedSet == null)
      return Collections.emptySet();

    if (valueConstraint == null || containedSet.isEmpty()) {
      return containedSet;
    }
    else {
      return valueConstraint.matches(containedSet, null, containedSet);
    }
  }

  /**
   * If the given value is a {@link Constraint}, then check if there
   * are any additional attribute/feature-checks on the constraint. If
   * so, then store the constraint for use during matching calls. If
   * not, then only the annotation type for the constraint is stored
   * since the full constraint is not needed.
   */
  @Override
  public void setValue(Object v) {
    if(v instanceof Constraint) {
      Constraint c = (Constraint)v;
      annotType = c.getAnnotType();
      if(!c.getAttributeSeq().isEmpty()) {
        // store full constraint for later use. It's stored in the
        // main value object for toString purposes.
        valueConstraint = c;
        value = c;
      }
    }

    // if the given value is not a constraint, then just store it
    // directly as the annotationType
    if(annotType == null && valueConstraint == null) {
      value = v;
      annotType = String.valueOf(v);
    }
  }

  public String getAnnotType() {
    return annotType;
  }

  @Override
  public String toString() {
    StringBuilder str = new StringBuilder();
    str.append(getOperator()).append(" ");
    if(annotType != null)  str.append(annotType);
    if(valueConstraint != null){
      str.append(": ").append(valueConstraint);
    }
    return str.toString();
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((annotType == null) ? 0 : annotType.hashCode());
    result =
        prime * result
            + ((valueConstraint == null) ? 0 : valueConstraint.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if(this == obj) return true;
    if(!super.equals(obj)) return false;
    if(!(obj instanceof EmbeddedConstraintPredicate)) return false;
    EmbeddedConstraintPredicate other = (EmbeddedConstraintPredicate)obj;
    if(annotType == null) {
      if(other.annotType != null) return false;
    } else if(!annotType.equals(other.annotType)) return false;
    if(valueConstraint == null) {
      if(other.valueConstraint != null) return false;
    } else if(!valueConstraint.equals(other.valueConstraint)) return false;
    return true;
  }
}
