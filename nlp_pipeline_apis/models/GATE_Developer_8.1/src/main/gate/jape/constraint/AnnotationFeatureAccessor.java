/*
 *  AnnotationFeatureAccessor - transducer class
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

/**
 * Accessor that returns a named feature value.
 *
 * @author esword
 */
public class AnnotationFeatureAccessor implements AnnotationAccessor {

  private static final long serialVersionUID = 9118181222280392932L;

  protected String featureName;

  public AnnotationFeatureAccessor() {
  }

  public AnnotationFeatureAccessor(Object key) {
    setKey(key);
  }

  /**
   * Obtain a named feature
   */
  @Override
  public Object getValue(Annotation annot, AnnotationSet context) {

    if(featureName == null || featureName.length() == 00)
      throw new IllegalStateException("setKey has not been called with "
              + "the featureName or key was empty");

    if(annot == null || annot.getFeatures() == null) return null;

    return annot.getFeatures().get(featureName);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result =
            prime * result
                    + ((featureName == null) ? 0 : featureName.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if(this == obj) return true;
    if(obj == null) return false;
    if(getClass() != obj.getClass()) return false;
    AnnotationFeatureAccessor other = (AnnotationFeatureAccessor)obj;
    if(featureName == null) {
      if(other.featureName != null) return false;
    } else if(!featureName.equals(other.featureName)) return false;
    return true;
  }

  @Override
  public String toString() {
    return featureName;
  }

  @Override
  public void setKey(Object key) {
    if(key != null) featureName = key.toString();
  }

  @Override
  public Object getKey() {
    return featureName;
  }
}
