/*
 * AdjectiveImpl.java
 * 
 * Copyright (c) 1995-2012, The University of Sheffield. See the file
 * COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 * 
 * This file is part of GATE (see http://gate.ac.uk/), and is free software,
 * licenced under the GNU Library General Public License, Version 2, June 1991
 * (in the distribution as file licence.html, and also available at
 * http://gate.ac.uk/gate/licence.html).
 * 
 * Marin Dimitrov, 20/May/2002
 * 
 * $Id: AdjectiveImpl.java 17496 2014-03-01 14:20:35Z markagreenwood $
 */

package gate.wordnet;

import net.didion.jwnl.dictionary.Dictionary;

/**
 * Represents WordNet adjective implements interface Adjective
 */
public class AdjectiveImpl extends WordSenseImpl implements Adjective {

  private int adjPosition;

  public AdjectiveImpl(WordImpl _word, SynsetImpl _synset, int _senseNumber,
                       int _orderInSynset, boolean _isSemcor, int _adjPosition,
                       Dictionary _wnDict) {

    super(_word, _synset, _senseNumber, _orderInSynset, _isSemcor, _wnDict);
    this.adjPosition = _adjPosition;
  }

  /**
   * returns the syntactic position of the adjective in relation to noun that it
   * modifies
   */
  public int getAdjectivePosition() {
    return this.adjPosition;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + adjPosition;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if(this == obj) return true;
    if(!super.equals(obj)) return false;
    if(getClass() != obj.getClass()) return false;
    AdjectiveImpl other = (AdjectiveImpl)obj;
    if(adjPosition != other.adjPosition) return false;
    return true;
  }
}