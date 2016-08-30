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

import org.apache.log4j.Logger;

/**
 * Accessor which returns a particular property or meta-property of an
 * annotation, such as length or string.
 *
 * @author esword
 */
public abstract class MetaPropertyAccessor implements AnnotationAccessor {

  private static final long serialVersionUID = 2736859945392088458L;

  protected static final Logger log = Logger.getLogger(MetaPropertyAccessor.class);

  public MetaPropertyAccessor() {
    super();
  }

  @Override
  public int hashCode() {
    return this.getClass().hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if(obj == null) return false;
    if(obj == this) return true;
    if(!(this.getClass().equals(obj.getClass()))) return false;

    return true;
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName();
  }

  @Override
  public void setKey(Object key) {
    if(key != null && !key.equals(""))
      log.warn(this.getClass().getName() + " doesn't use key values.  Key was: " + key);
  }

  /**
   * Sub-classes should return the name of the meta-property which they implement.
   */
  @Override
  public abstract Object getKey();
}
