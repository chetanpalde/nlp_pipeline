package gate.creole.morph;

import java.util.Arrays;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class CharacterSet extends Variable {

  private char [] varChars;
  /**
   * Constructor
   */
  public CharacterSet() {

  }

  /**
   * Tells if any value available which can be retrieved
   * @return true if value available, false otherwise
   */
  @Override
  public boolean hasNext() {
    if(pointer<varChars.length) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * Returns the next available value for this variable
   * @return value of the variable in the String format
   */
  @Override
  public String next() {
    if(pointer<varChars.length) {
      pointer++;
      return ""+varChars[pointer-1];
    } else {
      return null;
    }
  }

  /**
   * Process the provided value and stores in the underlying data structure
   * @param varName name of the variable
   * @param varValue String that contains possible different values
   * @return true if successfully stored, false otherwise
   */
  @Override
  public boolean set(String varName, String varValue) {
    this.varName = varName;
    this.varValue = varValue;
    // here the varValue would be in the following format
    // [abcdefg] // we need to sort it, so that while searching it will be
    // easier and faster to perform the binary search
    varValue = varValue.substring(1,varValue.length()-1);
    this.varChars = varValue.toCharArray();
    Arrays.sort(this.varChars);
    return true;
  }

  /**
   * A method that tells if the characters of the provided value are
   * from the characterSet only
   * @param value String of which the characters to be searched in the
   * characterSet
   * @return true if all characters of value string are from the
   * specified characterSet, false otherwise
   */
  @Override
  public boolean contains(String value) {
    for(int i=0;i<value.length();i++) {
      if(Arrays.binarySearch(this.varChars,value.charAt(i))<0) {
        return false;
      }
    }
    return true;
  }
}