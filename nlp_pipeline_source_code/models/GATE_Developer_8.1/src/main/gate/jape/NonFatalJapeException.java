/*
 *  NonFatalJapeException.java
 *
 *  Copyright (c) 1995-2012, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 *  Mark A. Greenwood, 19/10/2009
 *
 */
package gate.jape;

public class NonFatalJapeException extends JapeException {

  private static final long serialVersionUID = -4395817316344064153L;

  public NonFatalJapeException() {
    super();
  }
  
  public NonFatalJapeException(String message) {
    super(message);
  }
  
  public NonFatalJapeException(Throwable cause) {
    super(cause);
  }
  
  public NonFatalJapeException(String message, Throwable cause) {
    super(message, cause);
  }
}
