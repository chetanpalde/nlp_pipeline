package gate.creole.morph;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>Title: Storage.java </p>
 * <p>Description: This class is used as the storage in the system, where
 * all the declared variables and their appropriate values are stored </p>
 */
public class Storage {

  /**
   * Stores variable name as the key, and its variable values as values of these
   * keys
   */
  private Map<String, String> variables;

  /**
   * Constructor
   */
  public Storage() {
    variables = new HashMap<String, String>();
  }

  /**
   * Adds the variable name and its value into the hashTable
   * @param varName name of the variable
   * @param varValue value for the variable
   * @return true if variable doesnot exist, false otherwise
   */
  public boolean add(String varName, String varValue) {
    if(variables.containsKey(varName)) {
      return false;
    } else {

      // before storing varValue try to find if it is
      // a Character Range
      // a Character Set
      // a Sting Set

      variables.put(varName,varValue);
      return true;
    }
  }

  /**
   * This method looks into the hashtable and searches for the value of the
   * given variable
   * @param varName
   * @return value of the variable if variable found in the table,null otherwise
   */
  public String get(String varName) {
    return variables.get(varName);
  }

  /**
   * This method checks for the existance of the variable into the hashtable
   * @param varName
   * @return true if variable exists, false otherwise
   */
  public boolean isExist(String varName) {
    return variables.containsKey(varName);
  }

  /**
   * Update the variable with the new value. If variable doesnot exist, add it
   * to the hashtable
   * @param varName name of the variable to be updated, or added
   * @param varValue value of the variable
   */
  public void update(String varName,String varValue) {
    variables.put(varName,varValue);

  }

  /**
   * This method returns names of all the variables available in the hashtable
   * @return array of Strings - names of the variables
   */
  public String [] getVarNames() {
    return variables.keySet().toArray(new String[variables.size()]);
  }
}