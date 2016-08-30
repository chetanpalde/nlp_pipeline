/*
 * Copyright (c) 2009-2013, The University of Sheffield.
 * 
 * This file is part of GATE (see http://gate.ac.uk/), and is free software,
 * Licensed under the GNU Library General Public License, Version 3, June 2007
 * (in the distribution as file licence.html, and also available at
 * http://gate.ac.uk/gate/licence.html).
 */

package gate.alchemyAPI;

import gate.Factory;
import gate.FeatureMap;

public class TextSpan implements Comparable<TextSpan> {
  public String text;

  public FeatureMap featureMap = Factory.newFeatureMap();

  public String toString() {
    return text + " " + featureMap;
  }

  @Override
  public int compareTo(TextSpan o) {
    return o.text.length() - text.length();
  }
}