/*
 *  Constraint Factory - transducer class
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

import java.util.*;

import gate.jape.Constraint;

/**
 * Creates Jape {@link Constraint}s and associated
 * {@link ConstraintPredicate}s.
 *
 * @version $Revision$
 * @author esword
 */
public class ConstraintFactory {

  protected Map<String, Class<? extends ConstraintPredicate>> operatorImplMap =
    new HashMap<String, Class<? extends ConstraintPredicate>>();

  protected Map<String, Class<? extends AnnotationAccessor>> metaPropertyMap =
    new HashMap<String, Class<? extends AnnotationAccessor>>();

  public ConstraintFactory() {
    initOperatorMap();
    initMetaPropertyMap();
  }

  protected void initOperatorMap() {
    addOperator(ConstraintPredicate.EQUAL, EqualPredicate.class);
    addOperator(ConstraintPredicate.NOT_EQUAL, NotEqualPredicate.class);
    addOperator(ConstraintPredicate.GREATER, GreaterPredicate.class);
    addOperator(ConstraintPredicate.LESSER, LesserPredicate.class);
    addOperator(ConstraintPredicate.GREATER_OR_EQUAL,
            GreaterEqualPredicate.class);
    addOperator(ConstraintPredicate.LESSER_OR_EQUAL, LesserEqualPredicate.class);
    addOperator(ConstraintPredicate.REGEXP_FIND, RegExpFindPredicate.class);
    addOperator(ConstraintPredicate.NOT_REGEXP_FIND, NotRegExpFindPredicate.class);
    addOperator(ConstraintPredicate.REGEXP_MATCH, RegExpMatchPredicate.class);
    addOperator(ConstraintPredicate.NOT_REGEXP_MATCH, NotRegExpMatchPredicate.class);
    addOperator(ContainsPredicate.OPERATOR, ContainsPredicate.class);
    addOperator(NotContainsPredicate.OPERATOR, NotContainsPredicate.class);
    addOperator(WithinPredicate.OPERATOR, WithinPredicate.class);
    addOperator(NotWithinPredicate.OPERATOR, NotWithinPredicate.class);
  }

  protected void initMetaPropertyMap() {
    addMetaProperty("string", StringAccessor.class);
    addMetaProperty("cleanString", CleanStringAccessor.class);
    addMetaProperty("length", LengthAccessor.class);
  }

  public void addOperator(String operator,
          Class<? extends ConstraintPredicate> clazz) {
    operatorImplMap.put(operator, clazz);
  }

  public void addMetaProperty(String metaProperty,
          Class<? extends AnnotationAccessor> clazz) {
    metaPropertyMap.put(metaProperty, clazz);
  }

  /**
   * Create a new constraint for the given annotation type
   */
  public Constraint createConstraint(String annotType) {
    return new Constraint(annotType);
  }

  public AnnotationAccessor createDefaultAccessor(Object key) {
    return new AnnotationFeatureAccessor(key);
  }

  public AnnotationAccessor createMetaPropertyAccessor(String propName) {
    AnnotationAccessor retVal = null;
    Class<?> clazz = metaPropertyMap.get(propName);
    if(clazz == null)
      throw new IllegalArgumentException(
              "No meta property associated with name: " + propName);

    try {
      retVal = (AnnotationAccessor)clazz.newInstance();
    }
    catch(Exception e) {
      throw new RuntimeException("Could not create accessor for name '"
              + propName + "'", e);
    }

    return retVal;
  }

  /**
   * Create a constraint predicate using the default equals predicate.
   *
   * @param name feature name associated with the predicate
   * @param value value associated with the predicate
   */
  public ConstraintPredicate createPredicate(String name, Object value) {
    return createPredicate(createDefaultAccessor(name), value);
  }

  public ConstraintPredicate createPredicate(AnnotationAccessor accessor,
          Object value) {
    return createPredicate(ConstraintPredicate.EQUAL, accessor, value);
  }

  public ConstraintPredicate createPredicate(String operator,
          AnnotationAccessor accessor, Object value) {
    ConstraintPredicate retVal = null;
    Class<?> clazz = operatorImplMap.get(operator);
    if(clazz == null)
      throw new IllegalArgumentException(
              "No predicate associated with operator: " + operator);

    try {
      retVal = (ConstraintPredicate)clazz.newInstance();
      retVal.setAccessor(accessor);
      retVal.setValue(value);
    }
    catch(Exception e) {
      throw new RuntimeException("Could not create predicate for operator '"
              + operator + "' with accessor '" + accessor + "' and value '"
              + value + "'", e);
    }

    return retVal;
  }

}
