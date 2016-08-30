package com.ontotext.russie.morph;

import java.util.HashMap;
import java.util.Map;

/**SuffixPool.java
 *
 * A pool of suffix nests that removes the duplication of equivalent nests.
 *
 * <p>Title: RussIE</p>
 * <p>Description: Russian Information Extraction based on GATE</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Ontotext Lab.</p>
 * @author unascribed
 * @version 1.0
 */
public class SuffixPool {

  private static Map<SuffixNest, SuffixNest> pool =
    new HashMap<SuffixNest, SuffixNest>();

  public SuffixPool() {
  }

  /**
   * Retrieves a distinct/unique SuffixNest instance that equals the new nest. If there
   * is no equivalent the new nest is returned and also added to the pool.
   * @param newNest
   * @return unique nest
   */
  public static SuffixNest getDistinctNestAs(SuffixNest newNest) {
    if (!pool.containsKey(newNest)) {
      pool.put(newNest,newNest);
      return newNest;
    }

    SuffixNest nest;

    nest = pool.get(newNest);
    return nest;
  } // getUniqueNestAs(newNest)

  public static String getString(){
    return pool.keySet().toString();
  }

  public static int size(){
    return pool.size();
  }

} // class SuffixPool

