/*
 *  RuleTime.java
 *
 *  Copyright (c) 2009, Intelius, Inc.
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 *  Andrew Borthwick, 7/22/2009
 *
 */
package gate.fsm;

import java.io.Serializable;

/**
 * @author andrew
 *
 */
public class RuleTime implements Serializable {

  private static final long serialVersionUID = 1005996578872528959L;
  
  private long timeSpent;
  final private String ruleName;
  RuleTime(long my_timeSpent, String my_ruleName) {
    timeSpent = my_timeSpent;
    ruleName = my_ruleName;
  }
  public long getTimeSpent() {
    return timeSpent;
  }
  public void setTimeSpent(long timeSpent) {
    this.timeSpent = timeSpent;
  }
  public long addTime(long additionalTime) {
    this.timeSpent += additionalTime;
    return timeSpent;
  }
  public String getRuleName() {
    return ruleName;
  }

}
