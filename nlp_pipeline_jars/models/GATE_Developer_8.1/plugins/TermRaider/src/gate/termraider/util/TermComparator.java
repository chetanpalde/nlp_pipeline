/*
 *  Copyright (c) 2010--2014, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 *  $Id: TermComparator.java 17718 2014-03-20 20:40:06Z adamfunk $
 */
package gate.termraider.util;

import java.util.Comparator;

/**
 * Comparator used to sort Term data structures.
 */
public class TermComparator implements Comparator<Term> {

  public int compare(Term t0, Term t1) {
    return t0.compareTo(t1);
  }
  
}

