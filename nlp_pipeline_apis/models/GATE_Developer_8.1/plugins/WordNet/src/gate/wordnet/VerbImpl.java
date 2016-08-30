/*
 * VerbImpl.java
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
 * $Id: VerbImpl.java 17496 2014-03-01 14:20:35Z markagreenwood $
 */

package gate.wordnet;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import net.didion.jwnl.dictionary.Dictionary;

/**
 * Represents WordNet verb.
 */
public class VerbImpl extends WordSenseImpl implements Verb {

  private List<VerbFrame> verbFrames;

  public VerbImpl(WordImpl _word, SynsetImpl _synset, int _senseNumber,
                  int _orderInSynset, boolean _isSemcor,
                  net.didion.jwnl.data.Verb _jwVerb, Dictionary _wnDict) {

    super(_word, _synset, _senseNumber, _orderInSynset, _isSemcor, _wnDict);

    Assert.assertNotNull(_jwVerb);

    String[] jwFrames = _jwVerb.getVerbFrames();
    this.verbFrames = new ArrayList<VerbFrame>(jwFrames.length);

    for(int i = 0; i < jwFrames.length; i++) {
      this.verbFrames.add(new VerbFrameImpl(jwFrames[i]));
    }
  }

  /** returns the verb frames associated with this synset */
  public List<VerbFrame> getVerbFrames() {
    return this.verbFrames;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result =
      prime * result + ((verbFrames == null) ? 0 : verbFrames.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if(this == obj) return true;
    if(!super.equals(obj)) return false;
    if(getClass() != obj.getClass()) return false;
    VerbImpl other = (VerbImpl)obj;
    if(verbFrames == null) {
      if(other.verbFrames != null) return false;
    } else if(!verbFrames.equals(other.verbFrames)) return false;
    return true;
  }

}