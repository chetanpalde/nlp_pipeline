/*
 *  PrioritisedRuleList.java - transducer class
 *
 *  Copyright (c) 1995-2012, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 *  Hamish Cunningham, 27/07/98
 *
 *  $Id: PrioritisedRuleList.java 17595 2014-03-08 13:05:32Z markagreenwood $
 */


package gate.jape;

import java.util.ArrayList;
import java.util.Iterator;


/**
  * A list of rules ordered according to priority. May be used for ordering
  * non-matched rules (in which case the order is based on
  * priority/position), or matched rules (in which case the order is based
  * on matched lenght/priority/position). Note that position represents
  * the idea of order within an input file; it is assumed that this is the
  * same as the order of addition of the rules to the list, i.e. a rule
  * added 5th is assumed to occupy 5th place in the file (or other rule
  * source). This class is based on JGL's DList, which allows for fast
  * insertion of elements at any point. The highest priority rule is the
  * first in the list, which may be accessed by <CODE>front()</CODE>.
  */
public class PrioritisedRuleList extends ArrayList<Rule> implements java.io.Serializable
{
  private static final long serialVersionUID = 1603854971047597460L;

  /** Adds a rule in order. Used for non-matched rules. Implements the
    * ordering based on priority/position.
    */
  public synchronized boolean add(Rule newRule) {
    /* for each rule,
     *   if it is higher priority, continue;
     *   else if it is same priority
     *     if it is higher position, continue;
     *     else break
     *   else (it is lower priority) break
     * insert newRule before current position (which may be finish)
     */
    Iterator<Rule> iterator = this.iterator();
    int i = 0;
    for(  ; iterator.hasNext(); i++) {
      Rule rule	=	iterator.next();
      int rulePriority =	rule.getPriority();
      int newRulePriority =	newRule.getPriority();
      int rulePosition =	rule.getPosition();
      int newRulePosition =	newRule.getPosition();

      if(rulePriority > newRulePriority)
        continue;
      else if(rulePriority == newRulePriority) {
        if(rulePosition < newRulePosition)
          continue;
        else
          break;
      } else {
        break;
      }

    } // while not hit the end of the rules


    this.add(i, newRule);
    
    return true;
  } // add(Rule)


} // class PrioritisedRuleList


// $Log$
// Revision 1.8  2005/01/11 13:51:36  ian
// Updating copyrights to 1998-2005 in preparation for v3.0
//
// Revision 1.7  2004/07/21 17:10:08  akshay
// Changed copyright from 1998-2001 to 1998-2004
//
// Revision 1.6  2004/03/25 13:01:13  valyt
// Imports optimisation throughout the Java sources
// (to get rid of annoying warnings in Eclipse)
//
// Revision 1.5  2001/09/13 12:09:50  kalina
// Removed completely the use of jgl.objectspace.Array and such.
// Instead all sources now use the new Collections, typically ArrayList.
// I ran the tests and I ran some documents and compared with keys.
// JAPE seems to work well (that's where it all was). If there are problems
// maybe look at those new structures first.
//
// Revision 1.4  2000/11/08 16:35:03  hamish
// formatting
//
// Revision 1.3  2000/10/16 16:44:34  oana
// Changed the comment of DEBUG variable
//
// Revision 1.2  2000/10/10 15:36:36  oana
// Changed System.out in Out and System.err in Err;
// Added the DEBUG variable seted on false;
// Added in the header the licence;
//
// Revision 1.1  2000/02/23 13:46:10  hamish
// added
//
// Revision 1.1.1.1  1999/02/03 16:23:02  hamish
// added gate2
//
// Revision 1.6  1998/10/01 16:06:34  hamish
// new appelt transduction style, replacing buggy version
//
// Revision 1.5  1998/09/17 10:24:02  hamish
// added options support, and Appelt-style rule application
//
// Revision 1.4  1998/08/12 19:05:47  hamish
// fixed multi-part CG bug; set reset to real reset and fixed multi-doc bug
//
// Revision 1.3  1998/07/30 11:05:24  mks
// more jape
//
// Revision 1.2  1998/07/29 11:07:08  hamish
// first compiling version
//
// Revision 1.1.1.1  1998/07/28 16:37:46  hamish
// gate2 lives
