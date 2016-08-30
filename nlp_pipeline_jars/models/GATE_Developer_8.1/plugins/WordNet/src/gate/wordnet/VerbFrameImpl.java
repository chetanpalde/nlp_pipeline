/*
 * VerbFrameImpl.java
 * 
 * Copyright (c) 1995-2012, The University of Sheffield. See the file
 * COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 * 
 * This file is part of GATE (see http://gate.ac.uk/), and is free software,
 * licenced under the GNU Library General Public License, Version 2, June 1991
 * (in the distribution as file licence.html, and also available at
 * http://gate.ac.uk/gate/licence.html).
 * 
 * Marin Dimitrov, 16/May/2002
 * 
 * $Id: VerbFrameImpl.java 17496 2014-03-01 14:20:35Z markagreenwood $
 */

package gate.wordnet;

import junit.framework.Assert;

public class VerbFrameImpl implements VerbFrame {

  private String frame;

  public VerbFrameImpl(String _frame) {

    Assert.assertNotNull(_frame);
    this.frame = _frame;
  }

  public String getFrame() {
    return this.frame;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((frame == null) ? 0 : frame.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if(this == obj) return true;
    if(obj == null) return false;
    if(getClass() != obj.getClass()) return false;
    VerbFrameImpl other = (VerbFrameImpl)obj;
    if(frame == null) {
      if(other.frame != null) return false;
    } else if(!frame.equals(other.frame)) return false;
    return true;
  }
}