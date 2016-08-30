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
 * Accessor that returns the annotation itself
 *
 * @author esword
 */
public class SimpleAnnotationAccessor extends MetaPropertyAccessor {

  private static final long serialVersionUID = 5875035087239642161L;

  @Override
  public Object getValue(Annotation annot, AnnotationSet context) {
    return annot;
  }

  @Override
  public Object getKey() {
    return null;
  }
}
