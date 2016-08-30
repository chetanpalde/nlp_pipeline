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
 *
 *  $Id: NotWithinPredicate.java 17599 2014-03-08 16:30:42Z markagreenwood $
 */
package gate.jape.constraint;

import gate.Annotation;
import gate.AnnotationSet;
import gate.jape.JapeException;

import java.util.Collection;

/**
 * Returns false if the given annotation is entirely spanned by an annotation
 * of the type set in value.
 */
public class NotWithinPredicate extends WithinPredicate {

  private static final long serialVersionUID = 4478339733567901830L;

  public static final String OPERATOR = "notWithin";

    @Override
    public String getOperator() {
        return OPERATOR;
    }

    /**
     * Sets up environment for concreate class to do the specific matching check
     */
    @Override
    public boolean doMatch(Object annotValue, AnnotationSet context)
            throws JapeException {

      Annotation annot = (Annotation)annotValue;
      AnnotationSet containedSet = doMatch(annot, context);

      Collection<Annotation> filteredSet = filterMatches(containedSet);

      // this is where it differs from WithinPredicate
      return filteredSet.isEmpty();
    }

}