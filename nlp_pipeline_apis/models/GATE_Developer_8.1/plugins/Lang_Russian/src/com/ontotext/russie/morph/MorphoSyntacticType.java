package com.ontotext.russie.morph;

/**
 * MorphoSyntacticType.java This class represents the morpho-syntactic type
 * associated with a word form in the morphology.
 * <p>
 * Title: RussIE
 * </p>
 * <p>
 * Description: Russian Information Extraction based on GATE
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company: Ontotext Lab.
 * </p>
 * 
 * @author borislav popov
 * @version 1.0
 */
public class MorphoSyntacticType {

  private String type;

  public MorphoSyntacticType(String type) {
    this.type = type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getType() {
    return type;
  }

} // class MorphoSyntacticType

