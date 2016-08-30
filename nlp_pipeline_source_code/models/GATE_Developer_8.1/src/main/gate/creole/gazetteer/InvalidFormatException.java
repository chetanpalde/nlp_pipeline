/*
 * InvalidFormatException.java
 *
 * Copyright (c) 2002, The University of Sheffield.
 *
 * This file is part of GATE (see http://gate.ac.uk/), and is free
 * software, licenced under the GNU Library General Public License,
 * Version 2, June1991.
 *
 * A copy of this licence is included in the distribution in the file
 * licence.html, and is also available at http://gate.ac.uk/gate/licence.html.
 *
 * borislav popov 16/04/2002
 *
 * $Id: InvalidFormatException.java 17593 2014-03-08 10:03:19Z markagreenwood $
 */
package gate.creole.gazetteer;

import java.net.URL;

import gate.util.GateException;

/** exception thrown when an invalid format of a file is detected */
public class InvalidFormatException extends GateException{

  private static final long serialVersionUID = 5229935133798314714L;

  /**
   * the associated file
   */
  private String file;

  /** the associated URL */
  private URL url;

  /** the basic exception message */
  private final static String MSG = "Invalid format of file is detected; file: ";

  /**
   * Constructs the exception given a file and a comment
   * @param file the file to be associated
   * @param comment to be added to the basic excpetion message
   */
  public InvalidFormatException(String file,String comment) {
    super(MSG+file+"\n"+(null==comment ? "" : comment));
  }


  /**
   * Constructs the exception given an URL and a comment
   * @param url  the url to be associated
   * @param comment to be added to the basic excpetion message
   */
  public InvalidFormatException(URL url,String comment) {
    super(MSG+url.toString()+"\n"+(null==comment ? "" : comment));
  }

  public InvalidFormatException() {
    super(MSG);
  }

  /**
   * Gets the associated file
   * @return the associated file
   */
  public String getFile(){
    return file;
  }

  /**
   * Gets the asssociated URL
   * @return the associated URL
   */
  public URL getURL() {
    return url;
  }
} // class InvalidFormatException