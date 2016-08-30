/*
 *  JapeException.java
 *
 *  Copyright (c) 1995-2012, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 *  Hamish Cunningham, 23/02/2000
 *
 *  $Id: JapeException.java 17597 2014-03-08 15:19:43Z markagreenwood $
 */

package gate.jape;

import gate.util.GateException;

/** Superclass of all JAPE exceptions. */
public class JapeException extends GateException {

  private static final long serialVersionUID = -1036038431324812585L;

  public JapeException(Throwable cause) {
    super(cause);
  }
  
  public JapeException(String message) {
    super(message);
  }
  
  public JapeException(String message, Throwable cause) {
    super(message, cause);
  }

  public JapeException() {
    super();
  }
  
  /**
   * The location of the error.
   */
  String location = null;
  
  void setLocation(String location) {
    this.location = location;
  }
  
  @Override
  public String getMessage() {
    if(location != null) {
      return super.getMessage() + " (at " + location + ")";
    }
    else {
      return super.getMessage();
    }
  }

} // class JapeException
