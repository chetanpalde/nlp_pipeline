/*
 *  Rule.java
 *
 *  Copyright (c) 1995-2012, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 *  HepTag was originally written by Mark Hepple, this version contains
 *  modifications by Valentin Tablan and Niraj Aswani.
 *
 *  $Id: Rule.java 17402 2014-02-22 14:44:43Z markagreenwood $
 */

/**
 * Title:        HepTag
 * Description:  Mark Hepple's POS tagger
 * Copyright:    Copyright (c) 2001
 * Company:      University of Sheffield
 * @author Mark Hepple
 * @version 1.0
 */

package hepple.postag;

import java.util.List;

public abstract class Rule {

  protected String from;
  protected String to;
  protected String ruleId;
  protected String[] context;

  public void initialise(List<String> ruleParts) {
    from = ruleParts.get(0);
    to = ruleParts.get(1);
    ruleId = ruleParts.get(2);
    int contextSize = ruleParts.size() - 3;
    context = new String[contextSize];
    for (int i=0 ; i<contextSize ; i++) context[i] = ruleParts.get(i+3);
  }

  abstract public boolean checkContext(POSTagger tagger);

  public boolean hasToTag(POSTagger tagger) {
    for (int i=0 ; i<tagger.lexBuff[3].length ; i++)
      if (to.equals(tagger.lexBuff[3][i])) return true;
    return false;
  }//public boolean hasToTag(Tagger tagger)

  public boolean apply(POSTagger tagger) {
    if (hasToTag(tagger) && checkContext(tagger)) {
      tagger.tagBuff[3] = to;
      return true;
    }else return false;
  }//public boolean apply(Tagger tagger)

}//public abstract class Rule
