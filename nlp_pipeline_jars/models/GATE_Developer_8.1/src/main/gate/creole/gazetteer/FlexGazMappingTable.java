/*
 * Copyright (c) 2012, The University of Sheffield.
 * 
 * This file is part of GATE (see http://gate.ac.uk/), and is free software,
 * licenced under the GNU Library General Public License, Version 2, June1991.
 * 
 * A copy of this licence is included in the distribution in the file
 * licence.html, and is also available at http://gate.ac.uk/gate/licence.html.
 * $Id: FlexGazMappingTable.java 16102 2012-10-01 11:35:43Z adamfunk $
 */
package gate.creole.gazetteer;

import java.util.*;

public class FlexGazMappingTable {
  
  private Map<Long, NodePosition> startMap;
  private Map<Long, NodePosition> endMap;
  private long[] tempStartOffsets;
  private long[] tempEndOffsets;
  private boolean updated;
  private int size;
  
  
  public FlexGazMappingTable() {
    startMap = new HashMap<Long, NodePosition>();
    endMap = new HashMap<Long, NodePosition>();
    tempStartOffsets = null;
    tempEndOffsets = null;
    size = 0;
    updated = false;
  }
  
  
  private void add(NodePosition mapping) {
    startMap.put(mapping.getTempStartOffset(), mapping);
    endMap.put(mapping.getTempEndOffset(), mapping);
    size++;
    updated = false;
  }
  
  
  public Collection<NodePosition> getMappings() {
    return startMap.values();
  }
  
  
  public void add(long originalStart, long originalEnd, long tempStart, long tempEnd) {
    NodePosition mapping = new NodePosition(originalStart, originalEnd, tempStart, tempEnd);
    add(mapping);
  }
  
  
  public int size() {
    return this.size;
  }
  
  
  public boolean isEmpty() {
    return this.size == 0;
  }
  
  
  private void update() {
    if (updated) {
      return;
    }

    tempStartOffsets = new long[size];
    tempEndOffsets = new long[size];
    
    int i = 0;
    for (Long key : startMap.keySet()) {
      tempStartOffsets[i] = key.longValue();
      tempEndOffsets[i] = startMap.get(key).getTempEndOffset();
      i++;
    }
    
    Arrays.sort(tempStartOffsets);
    Arrays.sort(tempEndOffsets);
    updated = true;
  }
  

  /** Find the start offset of the latest original annotation
   *  that starts at or before this temporary annotation.
   *  This method MUST return a valid original annotation
   *  start offset or -1.
   * @param tempStartOffset
   * @return -1 is the error code, sorry
   */

  public long getBestOriginalStart(long tempStartOffset) {
    update();
    int i = Arrays.binarySearch(tempStartOffsets, tempStartOffset);

    // According to the binarySearch API, i = - insPt - 1

    if (i == -1) {
      // This means we've undershot the first original annotation
      return -1L;
    }
    
    if (i >= 0) {
      return startMap.get(tempStartOffsets[i]).getOriginalStartOffset();
    }
    
    /* Now we want the position before the insertion point 
     * (we've already tested for undershooting the first 
     * original annotation)   */
    i = - i - 2;
    return startMap.get(tempStartOffsets[i]).getOriginalStartOffset();
  }
  

  /** Find the end offset of the first original annotation
   *  that ends at or after this temporary annotation.  This method
   *  MUST return a valid original annotation end offset or -1. 
   * 
   * @param tempEndOffset
   * @return -1 is the error code, sorry
   */
  public long getBestOriginalEnd(long tempEndOffset) {
    update();
    int i = Arrays.binarySearch(tempEndOffsets, tempEndOffset);
    
    // Exact key is found in the array:
    if (i >= 0) {
      return endMap.get(tempEndOffsets[i]).getOriginalEndOffset();
    }
    
    /* Exact key is not in the array; according
     * to the binarySearch API, i = - insPt - 1
     * We want the insertion point, but if that is past the 
     * existing end of the array, then 
     * we have overshot the first input annotation    */
    i = - i - 1;
    if (i >= size) {
      return -1L;
    }
    
    return endMap.get(tempEndOffsets[i]).getOriginalEndOffset();
  }

  
  public void dump() {
    update();
    for (int i = 0 ; i < size ; i++) {
      long start = tempStartOffsets[i];
      long end = tempEndOffsets[i];
      NodePosition m = startMap.get(start);
      System.out.format("FGMT: %d, %d : o(%d, %d) t(%d, %d)\n", start, end,
          m.getOriginalStartOffset(), m.getOriginalEndOffset(),
          m.getTempStartOffset(), m.getTempEndOffset() );
    }
    
    
  }
   
  
}
