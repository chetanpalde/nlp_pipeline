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

import gate.Annotation;
import gate.AnnotationSet;
import gate.jape.JapeException;

/**
 * Base class for most {@link ConstraintPredicate}s. Contains standard
 * getters/setters and other routines.
 *
 * @author esword
 */
public abstract class AbstractConstraintPredicate implements
                                                 ConstraintPredicate {

  private static final long serialVersionUID = -4564142595796620686L;

  protected AnnotationAccessor accessor;
  protected Object value;

  public AbstractConstraintPredicate() {
  }

  public AbstractConstraintPredicate(AnnotationAccessor accessor, Object value) {
    setAccessor(accessor);
    setValue(value);
  }

  @Override
  public String toString() {
    // If value is a String, quote it. Otherwise (for things like
    // Numbers), don't.
    Object val = getValue();
    if(val instanceof String) val = "\"" + val + "\"";
    return accessor + " " + getOperator() + " " + val;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((accessor == null) ? 0 : accessor.hashCode());
    result = prime * result + ((value == null) ? 0 : value.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if(this == obj) return true;
    if(obj == null) return false;
    if(!(obj instanceof AbstractConstraintPredicate)) return false;
    AbstractConstraintPredicate other = (AbstractConstraintPredicate)obj;
    if(accessor == null) {
      if(other.accessor != null) return false;
    } else if(!accessor.equals(other.accessor)) return false;
    if(value == null) {
      if(other.value != null) return false;
    } else if(!value.equals(other.value)) return false;
    return true;
  }

  @Override
  public boolean matches(Annotation annot, AnnotationSet context) throws JapeException {
    //get the appropriate value using the accessor and then have
    //concrete subclasses do the eval
    return doMatch(accessor.getValue(annot, context), context);
  }

  protected abstract boolean doMatch(Object value, AnnotationSet context)
          throws JapeException;



  @Override
  public void setAccessor(AnnotationAccessor accessor) {
    this.accessor = accessor;
  }

  @Override
  public AnnotationAccessor getAccessor() {
    return accessor;
  }

  @Override
  public void setValue(Object value) {
    this.value = value;
  }

  @Override
  public Object getValue() {
    return value;
  }
}
