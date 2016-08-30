/*
 *  Copyright (c) 2012--2014, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 *  $Id: IdfCalculation.java 17538 2014-03-04 21:48:49Z adamfunk $
 */
package gate.termraider.modes;

import gate.termraider.util.Utilities;

public enum IdfCalculation {
  Logarithmic,
  LogarithmicScaled,
  Scaled,
  Natural;
  
  /* These calculations are partly based on Manning & Schütze, 
   * Foundations of Statistical NLP, section 15.2 (p.544).
   */
  
  public static double calculate(IdfCalculation mode, int rawDF, int corpusSize) {
    double df = (double) rawDF;
    double n = (double) corpusSize;
    
    if (mode == LogarithmicScaled) {
      return 1.0 + Utilities.log2(n / (df + 1.0));
    }

    if (mode == Logarithmic) {
      return 1.0 + Utilities.log2(1.0 / (df + 1.0));
    }

    if (mode == Scaled) {
      return (1.0 + n )/ (df + 1.0);
    }
    
    // must be Natural
    return 1.0 / (df + 1.0);
  }

}
