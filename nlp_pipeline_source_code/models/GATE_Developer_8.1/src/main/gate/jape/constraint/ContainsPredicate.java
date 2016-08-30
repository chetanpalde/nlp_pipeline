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

import gate.Annotation;
import gate.AnnotationSet;


/**
 * Returns true if there is an annotation of the type set in value that is entirely
 * spanned by the given annotation
 */
public class ContainsPredicate extends EmbeddedConstraintPredicate {

  private static final long serialVersionUID = 2129108867232668824L;

  public static final String OPERATOR = "contains";

  @Override
  public String getOperator() {
    return OPERATOR;
  }

  /**
   * Get all the annots of the right type that are within the span of
   * this annot.
   */
  @Override
  public AnnotationSet doMatch(Annotation annot, AnnotationSet as) {
    return as.getContained(annot.getStartNode().getOffset(),
            annot.getEndNode().getOffset()).get(annotType);
  }

}
