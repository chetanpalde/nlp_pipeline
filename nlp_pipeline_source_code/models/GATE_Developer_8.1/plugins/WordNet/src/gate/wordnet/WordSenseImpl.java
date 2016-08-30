/*
 *  WordImpl.java
 *
 *  Copyright (c) 1995-2012, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 *  Marin Dimitrov, 17/May/2002
 *
 *  $Id: WordSenseImpl.java 17496 2014-03-01 14:20:35Z markagreenwood $
 */

package gate.wordnet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.Assert;
import net.didion.jwnl.JWNLException;
import net.didion.jwnl.data.IndexWord;
import net.didion.jwnl.data.POS;
import net.didion.jwnl.data.Pointer;
import net.didion.jwnl.data.PointerTarget;
import net.didion.jwnl.data.PointerType;
import net.didion.jwnl.dictionary.Dictionary;


public class WordSenseImpl implements WordSense {

  private WordImpl word;
  private SynsetImpl  synset;
  private int senseNumber;
  private int orderInSynset;
  private boolean isSemcor;
  private List<LexicalRelation> lexRelations;
  private Dictionary wnDictionary;

  public WordSenseImpl(WordImpl _word,
                      SynsetImpl _synset,
                      int _senseNumber,
                      int _orderInSynset,
                      boolean _isSemcor,
                      Dictionary _wnDict) {

    //0.
    Assert.assertNotNull(_word);
    Assert.assertNotNull(_synset);
    Assert.assertNotNull(_wnDict);

    this.word = _word;
    this.synset = _synset;
    this.senseNumber = _senseNumber;
    this.orderInSynset = _orderInSynset;
    this.isSemcor = _isSemcor;
    this.wnDictionary = _wnDict;
  }

  /** returns the Word of this WordSense */
  public Word getWord() {
    return this.word;
  }

  /** part-of-speech for this sense (inherited from the containing synset) */
  public int getPOS() {
    return this.synset.getPOS();
  }

  /** synset of this sense */
  public Synset getSynset() {
    return this.synset;
  }

  /** order of this sense relative to the word - i.e. most important senses of the same word come first */
  public int getSenseNumber() {
    return this.senseNumber;
  }

  /** order of this sense relative to the synset- i.e. most important senses of the same synset come first */
  public int getOrderInSynset() {
    return this.orderInSynset;
  }


  /** appears in SEMCOR? */
  public boolean isSemcor() {
    return this.isSemcor;
  }


  /** return the Lex relations this sense participates in */
  public List<LexicalRelation> getLexicalRelations() throws WordNetException {

    if (null == this.lexRelations) {
      _loadLexicalRelations();
    }

    return this.lexRelations;
  }


  /** return the Lex relations (of the specified type) this sense participates in */
  public List<LexicalRelation> getLexicalRelations(int type) throws WordNetException {

    List<LexicalRelation> result = new ArrayList<LexicalRelation>(1);

    if (null == this.lexRelations) {
      _loadLexicalRelations();
    }

    Iterator<LexicalRelation> it = this.lexRelations.iterator();
    while (it.hasNext()) {
      LexicalRelation lRel = (LexicalRelation)it.next();
      Assert.assertNotNull(lRel);
      if (type == lRel.getType()) {
        result.add(lRel);
      }
    }

    return result;
  }


  private void _loadLexicalRelations() throws WordNetException{

    POS jwPOS = null;
    jwPOS = WNHelper.int2POS(this.getPOS());

    try {
      net.didion.jwnl.data.Synset jwSynset = this.wnDictionary.getSynsetAt(jwPOS,this.synset.getOffset());
      Assert.assertNotNull(jwSynset);

      Pointer[] jwPointers = null;

      net.didion.jwnl.data.Word[] jwWords = jwSynset.getWords();
      for (int i=0; i< jwWords.length; i++) {
        net.didion.jwnl.data.Word currJwWord = jwWords[i];
        if (currJwWord.getLemma().equalsIgnoreCase(this.getWord().getLemma())) {
          jwPointers = currJwWord.getPointers();
          break;
        }
      }

      this.lexRelations = new ArrayList<LexicalRelation>(jwPointers.length);

      for (int i= 0; i< jwPointers.length; i++) {

        Pointer currPointer = jwPointers[i];
        //skip semantic relations
        if (false == currPointer.isLexical()) {
          continue;
        }

        PointerType currType = currPointer.getType();
        try{
//        PointerTarget ptrSource = currPointer.getSource();
        PointerTarget ptrTarget = currPointer.getTarget();
        Assert.assertTrue(ptrTarget instanceof net.didion.jwnl.data.Word);

        net.didion.jwnl.data.Word jwTargetWord = (net.didion.jwnl.data.Word)ptrTarget;
        net.didion.jwnl.data.Synset jwTargetSynset = jwTargetWord.getSynset();
        IndexWord jwTargetIndexWord = this.wnDictionary.lookupIndexWord(jwTargetWord.getPOS(),
                                                                      jwTargetWord.getLemma());

        SynsetImpl gateSynset = new SynsetImpl(jwTargetSynset,this.wnDictionary);

        WordImpl gateWord = new WordImpl(jwTargetWord.getLemma(),
                                      jwTargetIndexWord.getSenseCount(),
                                      this.wnDictionary);

        WordSenseImpl gateTargetWordSense = new WordSenseImpl(gateWord,
                                                          gateSynset,
                                                          0,
                                                          jwTargetWord.getIndex(),
                                                          false,
                                                          this.wnDictionary);

        LexicalRelation gateLexRel = new LexicalRelationImpl(WNHelper.PointerType2int(currType),
                                                            this,
                                                            gateTargetWordSense);
        //add to list of sem relations for this synset
        this.lexRelations.add(gateLexRel);
        }
        catch (IllegalArgumentException e) {
          //System.err.println("Unknown PointerType: " + currType);
        }
      }
    }
    catch(JWNLException e) {
      throw new WordNetException(e);
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((synset == null) ? 0 : synset.hashCode());
    result = prime * result + ((word == null) ? 0 : word.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if(this == obj) return true;
    if(obj == null) return false;
    if(getClass() != obj.getClass()) return false;
    WordSenseImpl other = (WordSenseImpl)obj;
    if(synset == null) {
      if(other.synset != null) return false;
    } else if(!synset.equals(other.synset)) return false;
    if(word == null) {
      if(other.word != null) return false;
    } else if(!word.equals(other.word)) return false;
    return true;
  }
}