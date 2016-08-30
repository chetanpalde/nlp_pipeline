/*
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
 * Accessor that returns the length of the characters spanned by the annotation
 *
 * @version $Revision$
 * @author esword
 */
public class LengthAccessor extends MetaPropertyAccessor {

  private static final long serialVersionUID = -7632284854801892163L;

  /**
   * Return the length of the span of the annotation.
   */
  @Override
  public Object getValue(Annotation annot, AnnotationSet context) {
    if(annot == null) return 0;
    Long retVal = annot.getEndNode().getOffset()
            - annot.getStartNode().getOffset();
    return retVal;
  }

  /**
   * Always returns "length", the name of the meta-property which this
   * accessor provides.
   */
  @Override
  public Object getKey() {
    return "length";
  }
}
