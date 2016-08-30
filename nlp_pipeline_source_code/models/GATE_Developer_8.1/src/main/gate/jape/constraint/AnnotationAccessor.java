/*
 *  AnnotationAccessor - transducer class
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
import gate.Document;

import java.io.Serializable;

/**
 * Provides way to access some property of an {@link Annotation} or
 * otherwise get a value associated with an annotation (such as the
 * length of text it spans or the text itself if the associated document
 * is available).
 *
 * @author esword
 */
public interface AnnotationAccessor extends Serializable {

  /**
   * Store a key or name for the accessor to reference when it attempts
   * to obtain the value of an object. Different implementors will have
   * different uses for the key. For example, it could be the name of
   * the feature that should be returned. It could also be the name of a
   * property to invoke on the Annotation object.
   *
   * @param key
   */
  public void setKey(Object key);

  /**
   * Return the key for this accessor.
   */
  public Object getKey();

  /**
   * Obtain the value of some part of the given annotation
   *
   * @param annot
   * @param context optional parameter with information about the
   *          context in which the annotation has meaning. Normally this
   *          would be a {@link Document}. Not all accessors will
   *          require the context information so it may be null. Some
   *          accessor implementations may throw an exception without
   *          it.
   */
  public Object getValue(Annotation annot, AnnotationSet context);
}
