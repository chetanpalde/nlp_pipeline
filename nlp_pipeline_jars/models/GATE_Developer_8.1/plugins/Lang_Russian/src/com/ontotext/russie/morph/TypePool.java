package com.ontotext.russie.morph;

import java.util.HashMap;
import java.util.Map;

/**
 * TypePool.java Represents a pool of distinct morpho-syntactic types.
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
public class TypePool {

  private static Map<String, String> pool = new HashMap<String, String>();

  /**
   * Retrieves a distinct/unique morpho-syntactic type instance that equals the
   * new type. If there is no equivalent the new type is returned and also added
   * to the pool.
   * 
   * @param newType
   * @return distinct type
   */
  public static String getDistinctType(String newType) {
    if(!pool.containsKey(newType)) {
      pool.put(newType, newType);
      return newType;
    }

    return pool.get(newType);
  }

  public static String getString() {
    return pool.keySet().toString();
  }

  public static int size() {
    return pool.size();
  }

}