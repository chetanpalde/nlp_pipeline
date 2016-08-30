/*
 * SortedFeatureMap
 * 
 * Copyright (c) 2011-2014, The University of Sheffield.
 * 
 * This file is part of GATE (see http://gate.ac.uk/), and is free software,
 * licenced under the GNU Library General Public License, Version 3, June 2007
 * (in the distribution as file licence.html, and also available at
 * http://gate.ac.uk/gate/licence.html).
 * 
 * Mark A. Greenwood, 11/06/2011
 */

package gate.creole.microdata;

import gate.util.SimpleFeatureMapImpl;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * A FeatureMap implementation that provides a way of ensuring that the keys
 * (when accessed with the keySet) are sorted into a given order.
 * 
 * @author Mark A. Greenwood
 */
public class SortedFeatureMap extends SimpleFeatureMapImpl {

  private static final long serialVersionUID = 8673641740013486436L;

  private Comparator<Object> comparator = null;

  public SortedFeatureMap(final String... keyOrder) {
    comparator = createComparator(keyOrder);
  }

  public SortedFeatureMap(Comparator<Object> comparator) {
    this.comparator = comparator;
  }

  @Override
  public Set<Object> keySet() {
    SortedSet<Object> keys = new TreeSet<Object>(comparator);
    keys.addAll(super.keySet());
    return keys;
  }

  public static Comparator<Object> createComparator(final String[] keyOrder) {
    return new Comparator<Object>() {

      List<String> order = Arrays.asList(keyOrder);

      @Override
      public int compare(Object o1, Object o2) {

        Integer i1 = order.indexOf(o1);
        Integer i2 = order.indexOf(o2);

        int a = i1.compareTo(i2);

        if(a != 0) return a;

        return o1.toString().compareTo(o2.toString());
      }
    };
  }
}
