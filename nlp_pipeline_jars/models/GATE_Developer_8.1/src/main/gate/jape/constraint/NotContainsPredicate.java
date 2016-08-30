/*
 *  NotWithinPredicate.java
 *
 *  Copyright (c) 1995-2012, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 *  Julien Nioche, 26 Jan 2012
 */
package gate.jape.constraint;

import gate.Annotation;
import gate.AnnotationSet;
import gate.jape.JapeException;

import java.util.Collection;

/**
 * Returns false if there is an annotation of the type set in value that is
 * entirely spanned by the given annotation
 */
public class NotContainsPredicate extends ContainsPredicate {

  private static final long serialVersionUID = -6988665289497332784L;

  public static final String OPERATOR = "notContains";

  @Override
  public String getOperator() {
    return OPERATOR;
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

    // this is where it differs from ContainsPredicate
    return filteredSet.isEmpty();
  }

}