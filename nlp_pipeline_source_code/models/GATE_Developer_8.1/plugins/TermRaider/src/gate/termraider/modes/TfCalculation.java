/*
 *  Copyright (c) 2012--2014, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 *  $Id: TfCalculation.java 17458 2014-02-26 20:51:10Z adamfunk $
 */
package gate.termraider.modes;

import gate.termraider.util.Utilities;

public enum TfCalculation {
  Natural,
  Sqrt,
  Logarithmic;
  
  
  public static double calculate(TfCalculation mode, int rawTF) {
    double tf = (double) rawTF;
    
    if (mode == Logarithmic) {
      return 1.0 + Utilities.log2(tf);
    }
    
    else if (mode == Sqrt) {
      return Math.sqrt(tf);
    }
    
    // must be Natural
    return tf;
  }
}
