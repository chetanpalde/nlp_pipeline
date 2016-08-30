/*
 * NodePosition.java
 *
 * Copyright (c) 2004--2011, The University of Sheffield.
 *
 * This file is part of GATE (see http://gate.ac.uk/), and is free
 * software, licenced under the GNU Library General Public License,
 * Version 2, June1991.
 *
 * A copy of this licence is included in the distribution in the file
 * licence.html, and is also available at http://gate.ac.uk/gate/licence.html.
 *
 * Niraj Aswani 02/2002
 * $Id: NodePosition.java 17530 2014-03-04 15:57:43Z markagreenwood $
 *
 * 2011-11-18: AF made this immutable.
 */

package gate.creole.gazetteer;

import java.util.Comparator;

/**
 * <p>Title: NodePosition.java </p>
 * <p>Description: This class is used to store the information about the
 * changes in the text and the addition or the subtraction of the spaces.
 * It is used by FlexibleGazetteer. </p>
 * @author Niraj Aswani
 */

public class NodePosition {

  /** The original start offset before changes */
  private long originalStartOffset;

  /** The original end offset before changes */
  private long originalEndOffset;

  /** The new start offset after the changes */
  private long tempStartOffset;

  /** The new end offset after the changes */
  private long tempEndOffset;

  /**
   * constructor
   * @param osn - old start offset
   * @param oen - old end offset
   * @param nsn - new start offset
   * @param nen - new end offset
   */
  public NodePosition(long osn, long oen, long nsn, long nen) {
    originalStartOffset = osn;
    originalEndOffset = oen;
    tempStartOffset = nsn;
    tempEndOffset = nen;
  }

  /**
   * Returns the old start offset
   * @return a <tt>long</tt> value.
   */
  public long getOriginalStartOffset() {
    return originalStartOffset;
  }

  /**
   * Returns the old end offset
   * @return a <tt>long</tt> value.
   */
  public long getOriginalEndOffset() {
    return originalEndOffset;
  }

  /**
   * Returns new start offset
   * @return  a <tt>long</tt> value.
   */
  public long getTempStartOffset() {
    return tempStartOffset;
  }

  /**
   * Returns the new end offset
   * @return a <tt>long</tt> value.
   */
  public long getTempEndOffset() {
    return tempEndOffset;
  }

}


class NodePositionComparator implements Comparator<NodePosition> {

  @Override
  public int compare(NodePosition arg0, NodePosition arg1) {
    long diff = arg0.getTempStartOffset() - arg1.getTempStartOffset();
    if (diff != 0L) {
      return Long.signum(diff);
    }
    // implied else
    diff = arg0.getTempEndOffset() - arg1.getTempEndOffset();
    if (diff != 0L) {
      return Long.signum(diff);
    }
    // implied else
    diff = arg0.getOriginalStartOffset() - arg1.getOriginalStartOffset();
    if (diff != 0L) {
      return Long.signum(diff);
    }
    // implied else
    diff = arg0.getOriginalEndOffset() - arg1.getOriginalEndOffset();
    return Long.signum(diff);
  }
  
  
  
}
