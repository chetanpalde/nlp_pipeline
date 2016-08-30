/* 
 *  MutableInteger.java - A mutable wrapper for int, so you can return
 *                       integer values via a method parameter
 *
 *  Copyright (c) 1995-2012, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 *  Hamish Cunningham, 24/07/98
 *
 *  $Id: MutableInteger.java 17599 2014-03-08 16:30:42Z markagreenwood $
 */


package gate.jape;

import java.io.Serializable;

/**
  * A mutable wrapper for int, so you can return
  * integer values via a method parameter. If public data members bother you
  * I suggest you get a hobby, or have more sex or something.
  */
public class MutableInteger implements Serializable
{
  private static final long serialVersionUID = -5619478724698259637L;

  public int value = 0;
} // class MutableInteger

