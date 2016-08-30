/*
 *  LaxErrorHandler.java
 *
 *  Copyright (c) 1995-2012, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *  
 *  Cristian URSU,  7/July/2000
 *
 *  $Id: LaxErrorHandler.java 17600 2014-03-08 18:47:11Z markagreenwood $
 */
package gate.util;

/**
 * LaxErrorHandler
 */
import org.xml.sax.*;

public abstract class LaxErrorHandler implements ErrorHandler {

  /**
   * LaxErrorHandler constructor comment.
   */
  public LaxErrorHandler() {super();}

  /**
   * error method comment.
   */
  @Override
  public abstract void error(SAXParseException ex) throws SAXException;

  /**
   * fatalError method comment.
   */
  @Override
  public abstract void fatalError(SAXParseException ex) throws SAXException ;

  /**
   * warning method comment.
   */
  @Override
  public abstract void warning(SAXParseException ex) throws SAXException ;

} // class LaxErrorHandler
