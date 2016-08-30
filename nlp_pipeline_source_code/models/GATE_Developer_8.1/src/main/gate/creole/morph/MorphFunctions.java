package gate.creole.morph;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class MorphFunctions {

  /** The word for which the program should find the root and the affix */
  private String input;
  /** Affix to the root word */
  private String affix;
  /** Length of the word provided to the program */
  private int len;

  /**
   * Default Constructor
   */
  public MorphFunctions() {

  }

  /**
   * Method returns the found affix of the word provided to the program, for
   * which the root and the affix has to be found
   * @return affix if found, "  " otherwise
   */
  public String getAffix() {
    if(affix==null) {
      return " ";
    } else {
      return affix;
    }
  }

  /**
   * Sets the input for which the roor entry has to be found in the program
   * @param input
   */
  public void setInput(String input) {
    this.input = input;
    this.len = input.length();
    this.affix = null;
  }

  /**
   * Deletes the "del" given number of characters from right,
   * <BR> appends the "add" given string at the end and
   * <BR> sets the affix as "affix"
   * <BR> and returns this new string
   */
  public String stem(int del, String add, String affix) {
    int stem_length = len - del;
    String result = this.input.substring(0,stem_length)+add;
    this.affix = affix;
    return result;
  } // method stem()


  /**
   * Deletes the "del" given number of characters from right,
   * <BR> appends the "add" given string at the end
   * <BR> and returns this new string
   */
  public String semi_reg_stem(int del, String add) {
    int stem_length = len - del;
    int inputLength = len;

    /* look for -es, -ed, -ing; cannot be anything else */
    if(input.charAt(inputLength-1) == 's' || input.charAt(inputLength-1) == 'S') {
      stem_length-=2;
      this.affix = "s";
    }


    if(input.charAt(inputLength-1) == 'd' || input.charAt(inputLength-1) == 'D') {
      stem_length-=2;
      this.affix = "ed";
    }


    if(input.charAt(inputLength-1) == 'g' || input.charAt(inputLength-1) == 'G') {
      stem_length-=3;
      this.affix = "ing";
    }

    String result = input.substring(0,stem_length)+add;
    return result;
  } // method semi_reg_stem()


  /**
   * returns the "root" as result and sets "affix" as affix
   */
  public String irreg_stem(String root, String affix) {
    String result = root;
    this.affix = affix;
    return result;
  } // method irreg_stem()


  /**
   * returns the input as the root word
   */
  public String null_stem() {
    String result = input;
    return result;
  } // method null_stem()
}