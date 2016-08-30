/*
 *  TemplateLaxErrorHandler.java
 *
 *  Copyright (c) 1995-2012, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 *  Cristian URSU, 07/July/2000
 *
 *  $Id: TemplateLaxErrorHandler.java 17600 2014-03-08 18:47:11Z markagreenwood $
 */

// modify this according with your package
package gate.util;

/**
 * TemplateLaxErrorHandler
 */
import java.io.File;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

// this import is for the abstract class LaxErrorHandler located in gate.util


// modify the class name the way you want
public class TemplateLaxErrorHandler extends LaxErrorHandler {

  /**
    * TemplateLaxErrorHandler constructor comment.
    */
  public TemplateLaxErrorHandler() {super();}

  /**
    * error method comment.
    */
  @Override
  public void error(SAXParseException ex) throws SAXException{
    // do something with the error
    File fInput = new File (ex.getSystemId());
    Err.println("e: " + fInput.getPath() + ": line " +
      ex.getLineNumber() + ": " + ex);
  } // error

  /**
    * fatalError method comment.
    */
  @Override
  public void fatalError(SAXParseException ex) throws SAXException{
    // do something with the fatalError
    File fInput = new File(ex.getSystemId());
    Err.println("E: " + fInput.getName() + ": line " +
      ex.getLineNumber() + ": " + ex);
  } // fatalError

  /**
    * warning method comment.
    */
  @Override
  public void warning(SAXParseException ex) throws SAXException {
    // do something with the warning.
    File fInput = new File(ex.getSystemId());
    Err.println("w: " + fInput.getName() + ": line " +
      ex.getLineNumber() + ": " + ex);
  } // warning

} // TemplateLaxErrorHandler
