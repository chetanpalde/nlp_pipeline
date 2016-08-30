/*
 *  WordNetViewer.java
 *
 *  Copyright (c) 1995-2012, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 *  Rosen Marinov, 29/05/2002
 *
 */
package gate.gui.wordnet;

import gate.creole.AbstractVisualResource;
import gate.wordnet.LexicalRelation;
import gate.wordnet.Relation;
import gate.wordnet.SemanticRelation;
import gate.wordnet.Synset;
import gate.wordnet.Verb;
import gate.wordnet.VerbFrame;
import gate.wordnet.WordNet;
import gate.wordnet.WordNetException;
import gate.wordnet.WordSense;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;

public class WordNetViewer extends AbstractVisualResource
                           implements ActionListener{

  private static final long serialVersionUID = -3934904786556278822L;
  
  protected JLabel searchLabel = new JLabel();
  protected JTextField searchWordTextField = new JTextField();
  protected JButton searchButton = new JButton();
  protected JTextPane resultPane = new JTextPane();
  protected JLabel searchLabel2 = new JLabel();
  protected JButton nounButton = new JButton();
  protected JButton verbButton = new JButton();
  protected JButton adjectiveButton = new JButton();
  protected JButton adverbButton = new JButton();
  protected JScrollPane scrollPane = new JScrollPane();
  protected GridBagLayout gridBagLayout1 = new GridBagLayout();

  protected JPopupMenu nounPopup;
  protected JPopupMenu verbPopup;
  protected JPopupMenu adjectivePopup;
  protected JPopupMenu adverbPopup;

  private WordNet wnMain = null;

  private boolean sentenceFrames = false;
  public final static int SENTENCE_FRAMES = 33001;

  public WordNetViewer(){
     searchLabel.setText("Search Word:");
        this.setLayout(gridBagLayout1);
        searchButton.setText("Search");
        searchButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchButton_actionPerformed(e);
            }
        });
        searchWordTextField.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchWordTextField_actionPerformed(e);
            }
        });
        searchLabel2.setText("Searches for ... :");
        nounButton.setText("Noun");
        verbButton.setText("Verb");
        adjectiveButton.setText("Adjective");
        adverbButton.setText("Adverb");

        nounButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nounButton_actionPerformed(e);
            }
        });
        verbButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                verbButton_actionPerformed(e);
            }
        });
        adjectiveButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                adjectiveButton_actionPerformed(e);
            }
        });
        adverbButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                adverbButton_actionPerformed(e);
            }
        });
        nounButton.setEnabled(false);
        verbButton.setEnabled(false);
        adjectiveButton.setEnabled(false);
        adverbButton.setEnabled(false);

        resultPane.setEditable(false);
        scrollPane.getViewport().add(resultPane);

        this.add(searchLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        this.add(searchWordTextField, new GridBagConstraints(1, 0, 5, 1, 1.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        this.add(scrollPane, new GridBagConstraints(0, 2, 7, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
        this.add(searchLabel2, new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        this.add(searchButton, new GridBagConstraints(6, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        this.add(adjectiveButton, new GridBagConstraints(4, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        this.add(verbButton, new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        this.add(nounButton, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        this.add(adverbButton, new GridBagConstraints(5, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
  }

  private void searchButton_actionPerformed(ActionEvent e) {
    actionSearch();
  }

  private void searchWordTextField_actionPerformed(ActionEvent e) {
    actionSearch();
  }

  private void actionSearch(){
    String text = searchWordTextField.getText().trim();
    text = text.replace(' ','_');
    searchLabel2.setText("Searches for " + text + ":");

    nounButton.setEnabled(false);
    verbButton.setEnabled(false);
    adjectiveButton.setEnabled(false);
    adverbButton.setEnabled(false);

    nounPopup = new JPopupMenu();
    verbPopup = new JPopupMenu();
    adjectivePopup = new JPopupMenu();
    adverbPopup = new JPopupMenu();

    StringBuffer display = new StringBuffer("");

    addToResult(display, text, WordNet.POS_NOUN);
    addToResult(display, text, WordNet.POS_VERB);
    addToResult(display, text, WordNet.POS_ADJECTIVE);
    addToResult(display, text, WordNet.POS_ADVERB);

    resultPane.setText(display.toString());

  }

  private void addToResult(StringBuffer display,  String text, int wordType) {
    List<WordSense> senses = null;
    try {
      wnMain.cleanup();
      senses = wnMain.lookupWord(text, wordType);
    } catch (WordNetException wne) {
      wne.printStackTrace();
    }

    if ( senses!=null && senses.size()>0){

      String wordIdentifier = "";
      switch (wordType){
        case WordNet.POS_NOUN:
          wordIdentifier = "noun";
          nounButton.setEnabled(true);
          break;
        case WordNet.POS_VERB:
          wordIdentifier = "verb";
          verbButton.setEnabled(true);
          break;
        case WordNet.POS_ADJECTIVE:
          wordIdentifier = "adjective";
          adjectiveButton.setEnabled(true);
          break;
        case WordNet.POS_ADVERB:
          wordIdentifier = "adverb";
          adverbButton.setEnabled(true);
          break;
      }

      display.append("\n");
      display.append("The ");
      display.append(wordIdentifier);
      display.append(" ");
      display.append(text);
      display.append(" has ");
      display.append(senses.size());
      display.append(" senses:");
      display.append("\n\n");
      for (int i=0; i< senses.size(); i++) {
        WordSense currSense = senses.get(i);
        Synset currSynset = currSense.getSynset();
        addToPopupMenu(currSense, currSynset, wordType, senses);
        List<WordSense> words = currSynset.getWordSenses();
        String wordsString = getWords(words);

        display.append(" " + (i+1) + ". " + wordsString + " -- " + currSynset.getGloss());
        display.append("\n");
      }
    }
  }

  private void addToPopupMenu(WordSense wordSense, Synset synset, int wordType, List<WordSense> senses){
    List<SemanticRelation> semRelations = null;
    try {
      semRelations = synset.getSemanticRelations();
    } catch (Exception e){
      e.printStackTrace();
    }

    List<LexicalRelation> lexRelations = null;
    try {
      lexRelations = wordSense.getLexicalRelations();
    } catch (Exception e){
      e.printStackTrace();
    }

    for (int i=0; i<(semRelations.size()+lexRelations.size()); i++) {
      Relation relation;
      if (i<semRelations.size()){
        relation = semRelations.get(i);
      } else {
        relation = lexRelations.get(i-semRelations.size());
      }

      switch (wordType) {
        case WordNet.POS_NOUN:
          if (false == existInPopup(nounPopup, getLabel(relation)) ){
            nounPopup.add(new RelationItem(getLabel(relation), relation.getType(), senses));
          }
          break;
        case WordNet.POS_VERB:
          //commented because problem with WN API and Sentence Frames
          if (!sentenceFrames){
            verbPopup.add(new RelationItem("Sentence Frames", SENTENCE_FRAMES, senses));
            sentenceFrames = true;
          }
          if (false == existInPopup(verbPopup, getLabel(relation)) ){
            verbPopup.add(new RelationItem(getLabel(relation), relation.getType(), senses));
          }
          break;
        case WordNet.POS_ADJECTIVE:
          if (false == existInPopup(adjectivePopup, getLabel(relation)) ){
            adjectivePopup.add(new RelationItem(getLabel(relation), relation.getType(), senses));
          }
          break;
        case WordNet.POS_ADVERB:
          if (false == existInPopup(adverbPopup, getLabel(relation)) ){
            adverbPopup.add(new RelationItem(getLabel(relation), relation.getType(), senses));
          }
          break;
      }
    }
  }

  private boolean existInPopup(JPopupMenu menu, String name){
    boolean result = false;
    for (int i=0; i<menu.getComponents().length; i++){
      if ( menu.getComponents()[i].getName().equals(name)){
        result = true;
        break;
      }
    }
    return result;
  }

  void nounButton_actionPerformed(ActionEvent e) {
    nounPopup.show(nounButton, 0, nounButton.getHeight());
  }

  void verbButton_actionPerformed(ActionEvent e) {
    verbPopup.show(verbButton, 0, verbButton.getHeight());
  }

  void adjectiveButton_actionPerformed(ActionEvent e) {
    adjectivePopup.show(adjectiveButton, 0, adjectiveButton.getHeight());
  }

  void adverbButton_actionPerformed(ActionEvent e) {
    if (adverbPopup.getComponentCount()>0){
      adverbPopup.show(adverbButton, 0, adverbButton.getHeight());
    }
  }

  @Override
  public void actionPerformed(ActionEvent e){
    RelationItem ri = (RelationItem) e.getSource();
    switch (ri.getRelationType()){
      case Relation.REL_ANTONYM:
        relAntonymSeeAlso(ri.getSenses(), Relation.REL_ANTONYM,"=> ");
        break;
      case Relation.REL_ATTRIBUTE:
        relAtributeSimilarTo(ri.getSenses(), Relation.REL_ATTRIBUTE, "=> ");
        break;
      case Relation.REL_CAUSE:
        relHoloMeroHypo(ri.getSenses(), Relation.REL_CAUSE, "=> ");
        break;
      case Relation.REL_DERIVED_FROM_ADJECTIVE:
        relAntonymSeeAlso(ri.getSenses(), Relation.REL_DERIVED_FROM_ADJECTIVE,"=> ");
        break;
      case Relation.REL_ENTAILMENT:
        relHoloMeroHypo(ri.getSenses(), Relation.REL_ENTAILMENT, "=> ");
        break;
      case Relation.REL_HYPERNYM:
        relHypernym(ri.getSenses());
        break;
      case Relation.REL_HYPONYM:
        relHoloMeroHypo(ri.getSenses(), Relation.REL_HYPONYM, "=> ");
        break;
      case Relation.REL_MEMBER_HOLONYM:
        relHoloMeroHypo(ri.getSenses(), Relation.REL_MEMBER_HOLONYM,"MEMBER OF: ");
        break;
      case Relation.REL_MEMBER_MERONYM:
        relHoloMeroHypo(ri.getSenses(), Relation.REL_MEMBER_MERONYM, "HAS MEMBER: ");
        break;
      case Relation.REL_PARTICIPLE_OF_VERB:
        relAntonymSeeAlso(ri.getSenses(), Relation.REL_PARTICIPLE_OF_VERB,"=> ");
        break;
      case Relation.REL_PART_HOLONYM:
        relHoloMeroHypo(ri.getSenses(), Relation.REL_PART_HOLONYM, "PART OF: ");
        break;
      case Relation.REL_PART_MERONYM:
        relHoloMeroHypo(ri.getSenses(), Relation.REL_PART_MERONYM, "HAS PART: ");
        break;
      case Relation.REL_PERTAINYM:
        break;
      case Relation.REL_SEE_ALSO:
        relAntonymSeeAlso(ri.getSenses(), Relation.REL_SEE_ALSO,"=> ");
        break;
      case Relation.REL_SIMILAR_TO:
        relAtributeSimilarTo(ri.getSenses(), Relation.REL_SIMILAR_TO, "=> ");
        break;
      case Relation.REL_SUBSTANCE_HOLONYM:
        relHoloMeroHypo(ri.getSenses(), Relation.REL_SUBSTANCE_HOLONYM,
                             " SUBSTANCE OF: ");
        break;
      case Relation.REL_SUBSTANCE_MERONYM:
        relHoloMeroHypo(ri.getSenses(), Relation.REL_SUBSTANCE_MERONYM, "HAS SUBSTANCE: ");
        break;
      case Relation.REL_VERB_GROUP:
        relAtributeSimilarTo(ri.getSenses(), Relation.REL_VERB_GROUP, "=> ");
        break;
      case SENTENCE_FRAMES:
        sentenceFrames(ri.getSenses());
        break;
    }
  }

  private void relHypernym(List<WordSense> senses){
    StringBuffer display = new StringBuffer("");
    for (int i = 0; i<senses.size(); i++){
      //display.append(getDescription(Relation.REL_HYPERNYM));
      display.append("\n");
      display.append("Sense ");
      display.append(i+1);
      display.append("\n");

      WordSense currSense = senses.get(i);
      Synset currSynset = currSense.getSynset();
      recursiveHypernym(currSynset, display, "  =>");
    }

    resultPane.setText(display.toString());
  }

  private void recursiveHypernym(Synset synset, StringBuffer display, String prefix){
    List<WordSense> words = synset.getWordSenses();
    String wordsString = getWords(words);

    display.append(prefix);
    display.append(" ");
    display.append(wordsString);
    display.append(" -- ");
    display.append(synset.getGloss());
    display.append("\n");

    List<SemanticRelation>  hList = null;
    try {
      hList = synset.getSemanticRelations(Relation.REL_HYPERNYM);
    } catch (Exception e){
      e.printStackTrace();
    }
    if (hList!=null && hList.size()>0){
      SemanticRelation rel = hList.get(0);
      prefix = "    " + prefix;
      recursiveHypernym(rel.getTarget(), display, prefix);
    }
  }


  private void relHoloMeroHypo(List<WordSense> senses, int relationType,
                               String relRefString){
    StringBuffer display = new StringBuffer("");
    for (int i = 0; i<senses.size(); i++){
      WordSense currSense = senses.get(i);
      Synset currSynset = currSense.getSynset();
      try {
        if (currSynset.getSemanticRelations(relationType).size()>0){
          //display.append(getDescription(relationType));
          display.append("\n");
          display.append("Sense ");
          display.append(i+1);
          display.append("\n");
          recursiveHoloMeroHypo(currSynset, display, "  ", false,
                                relationType, relRefString);
        }
      } catch (Exception e){
        e.printStackTrace();
      }
    }

    resultPane.setText(display.toString());
  }

  private void recursiveHoloMeroHypo(Synset synset, StringBuffer display,
                                String prefix, boolean symbPrefix,
                                int relationType, String relRefString){

    List<WordSense> words = synset.getWordSenses();
    String wordsString = getWords(words);

    display.append(prefix);
    if (symbPrefix) {
      display.append(relRefString);
    }
    display.append(wordsString);
    display.append(" -- ");
    display.append(synset.getGloss());
    display.append("\n");

    List<SemanticRelation>  holoList = null;
    try {
      holoList = synset.getSemanticRelations(relationType);
    } catch (Exception e){
      e.printStackTrace();
    }
    if (holoList!=null && holoList.size()>0){
      for (int i = 0; i<holoList.size(); i++){
        SemanticRelation rel = holoList.get(i);
        prefix = "    " + prefix;
        recursiveHoloMeroHypo(rel.getTarget(), display, prefix, true,
                              relationType, relRefString);
        prefix = prefix.substring(4, prefix.length());
      }
    }
  }

  private void relAntonymSeeAlso(List<WordSense> senses,
                                 int relType, String relRefString){
    StringBuffer display = new StringBuffer("");
    boolean semantic_see_also = true;
    for (int i = 0; i<senses.size(); i++){
      WordSense currSense = senses.get(i);
      Synset currSynset = currSense.getSynset();
      try {
        List<LexicalRelation> antonyms = currSense.getLexicalRelations(relType);
        if (antonyms!=null && antonyms.size()>0){
          semantic_see_also = false;
          //display.append(getDescription(relType));
          display.append("\n");
          display.append("Sense ");
          display.append(i+1);
          display.append("\n  ");
          display.append(getWords(currSynset.getWordSenses()));
          display.append(" -- ");
          display.append(currSynset.getGloss());
          display.append("\n");
          for (int j=0; j<antonyms.size(); j++){
            LexicalRelation rel = antonyms.get(j);
            WordSense word = rel.getTarget();
            display.append("      ");
            display.append(relRefString);
            display.append(word.getWord().getLemma());
            display.append(" -- ");
            display.append(word.getSynset().getGloss());
            display.append("\n");
          }
          display.append("\n");
        }
      } catch (Exception e){
        e.printStackTrace();
      }
    }

    resultPane.setText(display.toString());
    if (semantic_see_also){
        relAtributeSimilarTo(senses, Relation.REL_SEE_ALSO,"=> ");
    }
  }

  private void relAtributeSimilarTo(List<WordSense> senses, int releationType,
                                     String relRefString){
    StringBuffer display = new StringBuffer("");
    for (int i = 0; i<senses.size(); i++){
      WordSense currSense = senses.get(i);
      Synset currSynset = currSense.getSynset();
      try {
        List<SemanticRelation> atributes = currSynset.getSemanticRelations(releationType);
        if (atributes!=null && atributes.size()>0){
          //display.append(getDescription(releationType));
          display.append("\n");
          display.append("Sense ");
          display.append(i+1);
          display.append("\n  ");
          display.append(getWords(currSynset.getWordSenses()));
          display.append(" -- ");
          display.append(currSynset.getGloss());
          display.append("\n");
          for (int j=0; j<atributes.size(); j++){
            SemanticRelation rel = atributes.get(j);
            Synset synset = rel.getTarget();
            display.append("     ");
            display.append(relRefString);
            display.append(getWords(synset.getWordSenses()));

            display.append(" -- ");
            display.append(synset.getGloss());
            display.append("\n");
          }
          display.append("\n");
        }
      } catch (Exception e){
        e.printStackTrace();
      }
    }

    resultPane.setText(display.toString());
  }


  private String getWords(List<WordSense> words){
    StringBuffer wordsString = new StringBuffer("");
    for (int j = 0; j<words.size(); j++){
      WordSense word = words.get(j);
      wordsString.append(word.getWord().getLemma().replace('_',' '));
      if (j<(words.size()-1)){
        wordsString.append(", ");
      }
    }
    return wordsString.toString();
  }

  private void sentenceFrames(List<WordSense> senses){
    StringBuffer display = new StringBuffer("");
    for (int i=0; i<senses.size(); i++) {
      WordSense currSense = senses.get(i);
      Synset currSynset = currSense.getSynset();
      Verb currVerb = (Verb) currSense;
      List<VerbFrame> frames = currVerb.getVerbFrames();

      display.append("\nSense ");
      display.append(i+1);
      display.append("\n  ");
      display.append(getWords(currSynset.getWordSenses()));
      display.append(" -- ");
      display.append(currSynset.getGloss());
      display.append("\n");

        for (int j=0; j<frames.size(); j++){
          display.append("        *> ");
          display.append(frames.get(j).getFrame());
          display.append("\n");
        }
    }
    resultPane.setText(display.toString());
  }


  public String getLabel(Relation r){

    String result = "";
    switch (r.getType()){
      case Relation.REL_ANTONYM:
        result = "Antonym";
        break;
      case Relation.REL_ATTRIBUTE:
        result = "Attribute";
        break;
      case Relation.REL_CAUSE:
        result = "Cause";
        break;
      case Relation.REL_DERIVED_FROM_ADJECTIVE:
        result = "Derived From Adjective";
        break;
      case Relation.REL_ENTAILMENT:
        result = "Entailment";
        break;
      case Relation.REL_HYPERNYM:
        result = "Hypernym";
        break;
      case Relation.REL_HYPONYM:
        result = "Hyponym";
        break;
      case Relation.REL_MEMBER_HOLONYM:
        result = "Member Holonym";
        break;
      case Relation.REL_MEMBER_MERONYM:
        result = "Member Meronym";
        break;
      case Relation.REL_PARTICIPLE_OF_VERB:
        result = "Participle Of Verb";
        break;
      case Relation.REL_PART_HOLONYM:
        result = "Holonym";
        break;
      case Relation.REL_PART_MERONYM:
        result = "Meronym";
        break;
      case Relation.REL_PERTAINYM:
        result = "Pertainym";
        break;
      case Relation.REL_SEE_ALSO:
        result = "See Also";
        break;
      case Relation.REL_SIMILAR_TO:
        result = "Similar To";
        break;
      case Relation.REL_SUBSTANCE_HOLONYM:
        result = "Substance Holonym";
        break;
      case Relation.REL_SUBSTANCE_MERONYM:
        result = "Substance Meronym";
        break;
      case Relation.REL_VERB_GROUP:
        result = "Verb Group";
        break;
    }
    return result;
  }

  public String getDescription(int rel){

    String result = "";
    switch (rel){
      case Relation.REL_ANTONYM:
        result = "Antonyms:";
        break;
      case Relation.REL_ATTRIBUTE:
        result = "Attributes:";
        break;
      case Relation.REL_CAUSE:
        result = "Cause:";
        break;
      case Relation.REL_DERIVED_FROM_ADJECTIVE:
        result = "Derived From Adjective:";
        break;
      case Relation.REL_ENTAILMENT:
        result = "Entailments:";
        break;
      case Relation.REL_HYPERNYM:
        result = "Hypernyms:";
        break;
      case Relation.REL_HYPONYM:
        result = "Hyponyms:";
        break;
      case Relation.REL_MEMBER_HOLONYM:
        result = "Member Holonyms:";
        break;
      case Relation.REL_MEMBER_MERONYM:
        result = "Member Meronyms:";
        break;
      case Relation.REL_PARTICIPLE_OF_VERB:
        result = "Participle Of Verb:";
        break;
      case Relation.REL_PART_HOLONYM:
        result = "Holonyms:";
        break;
      case Relation.REL_PART_MERONYM:
        result = "Meronyms:";
        break;
      case Relation.REL_PERTAINYM:
        result = "Pertainyms:";
        break;
      case Relation.REL_SEE_ALSO:
        result = "See Also:";
        break;
      case Relation.REL_SIMILAR_TO:
        result = "Similar To:";
        break;
      case Relation.REL_SUBSTANCE_HOLONYM:
        result = "Substance Holonyms:";
        break;
      case Relation.REL_SUBSTANCE_MERONYM:
        result = "Substance Meronyms:";
        break;
      case Relation.REL_VERB_GROUP:
        result = "Verb Group:";
        break;
    }
    return result;
  }


  /**
   * Called by the GUI when this viewer/editor has to initialise itself for a
   * specific object.
   * @param target the object (be it a {@link gate.Resource},
   * {@link gate.DataStore} or whatever) this viewer has to display
   */
  @Override
  public void setTarget(Object target){

    if (false == target instanceof WordNet) {
      throw new IllegalArgumentException();
    }

    this.wnMain = (WordNet)target;
  }

  private class RelationItem extends JMenuItem{

    private static final long serialVersionUID = -4096509078870160667L;
    
    int relType;
    List<WordSense> senses;

    public RelationItem(String name, int type, List<WordSense> sen) {
      super(name);
      this.addActionListener(WordNetViewer.this);
      relType = type;
      senses = sen;
      setName(name);
    }

    public int getRelationType() {
      return relType;
    }

    public List<WordSense> getSenses(){
      return senses;
    }
  }

}
