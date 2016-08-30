package com.ontotext.russie.morph;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import com.ontotext.russie.RussIEConstants;
import com.ontotext.russie.WrongFormatException;

/**
 * This class reads in the morphology with the word forms and creates lists of
 * Lemmas that present wordforms and varios other morhological and syntactic
 * information for them. It needs the path to the morphology to be set before
 * load-time, otherwise the default is used form RussIEConstants.
 * <p>
 * Title: RussIE
 * </p>
 * <p>
 * Description: Russian Information Extraction based on GATE
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company: Ontotext Lab.
 * </p>
 * 
 * @author borislav popov
 * @version 1.0
 */
public class MorphologyReader implements RussIEConstants {

  protected String encoding;

  private boolean caseSensitive;

  private final static String LINE_PREFIX = "le(";

  private final static String NO_LINE_PREFIX = "There is no prefix [" +
    LINE_PREFIX + "] in the current morphology line.";

  /** the set of lemmas */
  private Set<Lemma> lemmas;

  public MorphologyReader(boolean caseSensitive) {
    lemmas = new HashSet<Lemma>();
    this.caseSensitive = caseSensitive;
  }

  /** Loads the morphology files */
  public void load(URL url) throws IOException {

    BufferedReader mReader =
      new BufferedReader(new InputStreamReader(url.openStream(), encoding));
    try {
      String line;
      Lemma lemma;
      while((line = mReader.readLine()) != null) {
        try {
          lemma = constructLemmaByMorphologyLine(line);
          lemmas.add(lemma);
        } catch(WrongFormatException wfe) {
          System.out.println("WrongFormatException");
          System.out.println(wfe.getMessage());
          System.out.println("line :\n" + line);
        }
      } // while lines
    } finally {
      mReader.close();
    }
  } // load()

  /**
   * Gets the set of lemmas built from the morphology file.
   * 
   * @return the set of lemmas built from the morphology file.
   */
  public Set<Lemma> getLemmas() {
    return lemmas;
  }

  /**
   * Constructs a Lemma given a line from the Morphology.
   * 
   * @param line
   *          a line from the Morphology
   * @return the constructed Lemma
   * @throws WrongFormatException
   */
  private Lemma constructLemmaByMorphologyLine(String line)
    throws WrongFormatException {
    // example line : it is one line but has been idented for convinience.
    // le(
    // \u00E0\u00E1\u00E0\u00E6\u00F3\u00F0,
    // 'Nmisn',
    // [
    // \u00E0\u00E1\u00E0\u00E6\u00F3\u00F0\u00E0:'Nmisg',
    // \u00E0\u00E1\u00E0\u00E6\u00F3\u00F0\u00F3:'Nmisd',
    // \u00E0\u00E1\u00E0\u00E6\u00F3\u00F0:'Nmisa',
    // \u00E0\u00E1\u00E0\u00E6\u00F3\u00F0\u00EE\u00EC:'Nmisi',
    // \u00E0\u00E1\u00E0\u00E6\u00F3\u00F0\u00E5:'Nmisl',
    // \u00E0\u00E1\u00E0\u00E6\u00F3\u00F0\u00FB:'Nmipn',
    // \u00E0\u00E1\u00E0\u00E6\u00F3\u00F0\u00EE\u00E2:'Nmipg',
    // \u00E0\u00E1\u00E0\u00E6\u00F3\u00F0\u00E0\u00EC:'Nmipd',
    // \u00E0\u00E1\u00E0\u00E6\u00F3\u00F0\u00FB:'Nmipa',
    // \u00E0\u00E1\u00E0\u00E6\u00F3\u00F0\u00E0\u00EC\u00E8:'Nmipi',
    // \u00E0\u00E1\u00E0\u00E6\u00F3\u00F0\u00E0\u00F5:'Nmipl'
    // ]).
    // check el prefix
    if(!line.substring(0, 3).equals(LINE_PREFIX)) { throw new WrongFormatException(
      NO_LINE_PREFIX); }

    // find the commas after the main wf and its type
    int commaIndex = line.indexOf(',', 3);
    int nextCommaIndex = line.indexOf(',', commaIndex + 1);

    // check main wf commas
    if(commaIndex < 0 || nextCommaIndex < 0) { throw new WrongFormatException(); }

    Lemma lemma = new LemmaImpl();

    String type = line.substring(commaIndex + 1, nextCommaIndex);
    type = removeQuote(type);
    lemma.setMainForm(removeQuote(line.substring(3, commaIndex)), type);

    if(!line.substring(nextCommaIndex + 1, nextCommaIndex + 2).equals("[")) { throw new WrongFormatException(); }

    // indicates whether there are more alternative word-forms to read
    boolean moreWf = true;

    if(line.substring(nextCommaIndex + 2, nextCommaIndex + 3).equals("]")) {
      // empty list of wfs
      moreWf = false;
    }

    int startOfWfCouple = nextCommaIndex + 2;
    String wf;
    while(moreWf) {
      commaIndex = line.indexOf(":", nextCommaIndex + 1);
      nextCommaIndex = line.indexOf(",", commaIndex + 1);
      if(commaIndex < 0) throw new WrongFormatException();
      if(nextCommaIndex < 0) {
        moreWf = false;
        nextCommaIndex = line.indexOf("]", commaIndex);
        if(nextCommaIndex < 0) throw new WrongFormatException();
      }

      type = line.substring(commaIndex + 1, nextCommaIndex);
      type = removeQuote(type);
      wf = removeQuote(line.substring(startOfWfCouple, commaIndex));
      if(!caseSensitive) {
        wf = wf.toLowerCase();
      }
      lemma.addWordForm(wf, type);

      startOfWfCouple = nextCommaIndex + 1;

    } // while there are WF

    lemma.synchWithSuffixPool();
    lemma.getSuffixNest().setMainFormSuffix(
      lemma.getMainForm().substring(lemma.getRoot().length()));
    return lemma;
  }// constructLemmaByMorphologyLine(line)

  public void setEncoding(String newEncoding) {
    encoding = newEncoding;
  }

  public String getEncoding() {
    return encoding;
  }

  /**
   * Removes the single quotes embracing a phrase. Presumes that if there is a
   * quote at the start - there is also a quote at the end.
   * 
   * @param phrase
   * @return the phrase without quotes
   */
  private String removeQuote(String phrase) {
    if(phrase == null || phrase.length() == 0) return phrase;
    if(phrase.substring(0, 1).equals("'")) {
      phrase = phrase.substring(1, phrase.length() - 1);
    }
    return phrase;
  }
} // class MorphologyReader
