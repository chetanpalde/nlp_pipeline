package gate.creole.morph;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Title: StringSet </p>
 * <p>Description: This is one of the variable types that is allowed to define.
 * It stores different possible strings for this variable
 * The format of the value of this variable should be </p>
 * <p> "string1" OR "string2" OR "string3" ... </p>
 */

public class StringSet extends Variable {

  private String varName;
  private List<String> variables;

  /**
   * Constructor
   */
  public StringSet() {
    variables = new ArrayList<String>();
  }

  /**
   * Tells if any value available which can be retrieved
   * @return true if value available, false otherwise
   */
  @Override
  public boolean hasNext() {
    if(pointer<variables.size()) {
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
    if(pointer<variables.size()) {
      pointer++;
      return variables.get(pointer-1);
    } else {
      return null;
    }
  }

  /**
   * Process the provided value and stores in the underlying data structure
   * @param varName name of the variable
   * @param varValue String that contains possible different values
   * @return true if successfully stored, false otherwise (means some syntax error)
   */
  @Override
  public boolean set(String varName, String varValue) {
    this.varName = varName;
    this.varValue = "";
    // now we need to process the varValue
    // lets first split between the | sign

    String [] values = varValue.split(" OR ");
    //check for its syntax
    for(int i=0;i<values.length;i++) {

      // remove all extra spaces
      values[i] = values[i].trim();

      // now check if it has been qouted properly
      if(values[i].length()<3 || !(values[i].charAt(0)=='\"') || !(values[i].charAt(values[i].length()-1)=='\"')) {
        return false;
      } else {
        values[i] = values[i].substring(1,values[i].length()-1);
      }
    }

    // store each value in the arrayList
    for(int i=0;i<values.length;i++) {
      variables.add(values[i]);
      this.varValue = this.varValue + "("+values[i]+")";
      if ((i+1) != values.length) {
        this.varValue = this.varValue + "|";
      }
    }
    return true;
  }

  /**
   * A method that tells if value is available in the StringSet
   * @param value String that is to be searched in the String set
   * @return true if value found in the StringSet, false otherwise
   */
  @Override
  public boolean contains(String value) {
    return variables.contains(value);
  }
  
  public String getVariableName() {
    return varName;
  }
}