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

public class CharacterRange extends Variable {

  private char [] varChars;

  /**
   * Constructor
   */
  public CharacterRange() {

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
    this.varValue = varValue.trim();
    // lets process the varValue
    // remove the [- and ] from the varValue
    varValue = varValue.substring(2,varValue.length()-1);
    // we need to find the sets
    String characters = "";
    for(int i=0; i<varValue.length();i = i + 3) {
      String set = varValue.substring(i,i+3);
      char startWith = set.charAt(0);
      char endWith = set.charAt(2);
      if(startWith>endWith) {
        char temp = startWith;
        startWith = endWith;
        endWith = temp;
      }
      for(int j=startWith;j<=endWith;j++) {
        // if it is already present no need to add it
        if(characters.indexOf((char)j)<0) {
          characters = characters + (char)(j);
        }
      }
    }
    // convert it into the character array and sort it
    this.varChars = characters.toCharArray();
    Arrays.sort(this.varChars);

    // now we need to convert the varValue into the proper pattern
    // simply remove the second character (i.e. '-')
    this.varValue = new String(new StringBuffer(this.varValue).deleteCharAt(1));
    return true;
  }

  /**
   * A method that tells if the characters of the provided value are
   * from the character range only
   * @param value String of which the characters to be searched in the
   * character range
   * @return true if all characters of value string are from the
   * specified character range, false otherwise
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