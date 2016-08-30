/*
 *  Copyright (c) 2014, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 *  $Id: ScoreType.java 17481 2014-02-27 17:02:59Z adamfunk $
 */
package gate.termraider.util;

import java.io.Serializable;


public class ScoreType 
implements Comparable<ScoreType>, Serializable {

  private static final long serialVersionUID = 2475508487229559804L;
  
  private String string;
  private String normalizedString;
  
  @Override
  public String toString() {
    return this.string;
  }
  
  public String toNormalizedString() {
    return this.normalizedString;
  }
  
  
  public ScoreType(String string) {
    if (string == null) {
      throw new IllegalArgumentException("ScoreType must not be null.");
    }
    
    this.string = string;
    
    // Normalize = remove leading & trailing whitespace then camelCase
    this.normalizedString = Utilities.cleanAndCamelCase(string);
    
    if (this.normalizedString.length() == 0) {
      throw new IllegalArgumentException("ScoreType must contain some non-whitespace characters.");
    }
  }
  
  
  @Override
  public int compareTo(ScoreType x) {
    return this.string.compareTo(x.string);
  }
  
  @Override
  public boolean equals(Object x) {
    if (x instanceof ScoreType) {
      return this.string.equals(((ScoreType)x).string);
    }

    // implied else: x is another class
    return false;
  }
  
  @Override
  public int hashCode() {
    return this.string.hashCode();
  }
  
}
