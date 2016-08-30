package com.ontotext.russie;

/**An exception indicating erroneous formatting of resources.
 * <p>Title: RussIE</p>
 * <p>Description: Russian Information Extraction based on GATE</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Ontotext Lab.</p>
 * @author unascribed
 * @version 1.0
 */
public class WrongFormatException extends Exception {

  private static final long serialVersionUID = -6149480951422115298L;

  public WrongFormatException() {
      super();
  }

  public WrongFormatException(String message) {
      super(message);
  }

  public WrongFormatException(String message, Throwable cause) {
      super(message, cause);
  }

  public WrongFormatException(Throwable cause) {
      super(cause);
  }

} // class WrongFormatException