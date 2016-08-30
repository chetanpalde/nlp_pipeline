/*
 *  GazeteerException.java
 *
 *  Copyright (c) 1995-2012, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 *  Valentin Tablan, 11/07/2000
 *
 *  $Id: GazetteerException.java 17593 2014-03-08 10:03:19Z markagreenwood $
 */

package gate.creole.gazetteer;

import gate.util.GateException;

/** Used to signal Gazetteer specific exceptions */
public class GazetteerException extends GateException {

  private static final long serialVersionUID = -8985924931103162693L;

  public GazetteerException(String s) {
    super(s);
  }
  
  public GazetteerException(String s, Throwable cause) {
    super(s,cause);
  }

} // GazetteerException
