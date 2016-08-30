/*
 *  POSTagger.java
 *
 *  Copyright (c) 1995-2012, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 *  HepTag was originally written by Mark Hepple, this version contains
 *  modifications by Valentin Tablan and Niraj Aswani.
 *
 *  $Id: POSTagger.java 17605 2014-03-09 10:15:34Z markagreenwood $
 */

/*
 * INSTRUCTIONS for STAND-ALONE USE
 *
 * SYNOPSIS
 *     java hepple.postag.POSTagger [options] file1 [file2 ...]
 * OPTIONS:
 *     -h, --help : displays this message
 *     -l, --lexicon <lexicon file> : uses specified lexicon
 *     -r, --rules <rules file> : uses specified rules
 *
 * NOTE: requires gnu.getopt package
 */

/**
 * Title:        HepTag
 * Description:  Mark Hepple's POS tagger
 * Copyright:    Copyright (c) 2001
 * Company:      University of Sheffield
 * @author Mark Hepple
 * @version 1.0
 */
package hepple.postag;


import gate.util.BomStrippingInputStreamReader;
import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.io.IOUtils;

/**
 * A Java POS Tagger
 *
 * Author: Mark Hepple (hepple@dcs.shef.ac.uk)
 *
 * Input:  An ascii text file in "Brill input format", i.e. one
 *        sentence per line, tokens separated by spaces.
 *
 * Output: Same text with each token tagged, i.e. "token" -> "token/tag".
 *        Output is just streamed to std-output, so commonly will direct
 *        into some target file.
 *
 * Revision: 13/9/00. Version 1.0.
 *
 * Comments:
 *
 * Implements a version of the decision list based tagging method
 * described in:
 *
 * M. Hepple. 2000. Independence and Commitment: Assumptions for Rapid
 * Training and Execution of Rule-based Part-of-Speech Taggers.
 * Proceedings of the 38th Annual Meeting of the Association for
 * Computational Linguistics (ACL-2000). Hong Kong, October 2000.
 *
 * Modified by Niraj Aswani/Ian Roberts to allow explicit specification of the
 * character encoding to use when reading rules and lexicon files.
 *
 * $Id: POSTagger.java 17605 2014-03-09 10:15:34Z markagreenwood $
 *
 */

public class POSTagger {

//    static final int MAXTAGS = 200;

    protected Map<String, List<Rule>> rules;
//    public Rule[] rules = new Rule[MAXTAGS];
//    public Rule[] lastRules = new Rule[MAXTAGS];


    Lexicon lexicon;

    private String encoding;

    static final String staart = "STAART";

    private String[] staartLex = { staart };
    private String[] deflex_NNP = { "NNP"};
    private String[] deflex_JJ  = { "JJ"};
    private String[] deflex_CD  = { "CD"};
    private String[] deflex_NNS = { "NNS"};
    private String[] deflex_RB  = { "RB"};
    private String[] deflex_VBG = { "VBG"};
    private String[] deflex_NN  = { "NN"};

    public String[] wordBuff  = { staart,staart,staart,staart,
        staart,staart,staart };

    public String[] tagBuff   = { staart,staart,staart,staart,
        staart,staart,staart };
    public String[][] lexBuff = { staartLex,staartLex,staartLex,
         staartLex,staartLex,staartLex,
         staartLex };

    /**
     * Construct a POS tagger using the platform's native encoding to read the
     * lexicon and rules files.
     */
    public POSTagger(URL lexiconURL, URL rulesURL) throws InvalidRuleException,
                                                          IOException {
      this(lexiconURL, rulesURL, null);
    }

    /**
     * Construct a POS tagger using the specified encoding to read the lexicon
     * and rules files.
     */
    public POSTagger(URL lexiconURL, URL rulesURL, String encoding) throws InvalidRuleException,
                                                          IOException{
      this.encoding = encoding;
      this.lexicon = new Lexicon(lexiconURL, encoding);
      rules = new HashMap<String, List<Rule>>();
      readRules(rulesURL);
    }

  /**
   * Creates a new rule of the required type according to the provided ID.
   * @param ruleId the ID for the rule to be created
   */
  public Rule createNewRule(String ruleId) throws InvalidRuleException{
    try{
      String className = "hepple.postag.rules.Rule_" + ruleId;
      Class<?> ruleClass = Class.forName(className);
      return (Rule)ruleClass.newInstance();
    }catch(Exception e){
      throw new InvalidRuleException("Could not create rule " + ruleId + "!\n" +
                                     e.toString());
    }
  }

  /**
   * Runs the tagger over a set of sentences.
   * @param sentences a {@link java.util.List} of {@link java.util.List}s
   * of words to be tagged. Each list is a sentence represented as a list of
   * words.
   * @return a {@link java.util.List} of {@link java.util.List}s of
   * {@link java.lang.String}[]. A list of tagged sentences, each sentence
   * being itself a list having pairs of strings as elements with
   * the word on the first position and the tag on the second.
   */
  public List<List<String[]>> runTagger(List<List<String>> sentences){
    List<List<String[]>> output = new ArrayList<List<String[]>>();
    List<String[]> taggedSentence = new ArrayList<String[]>();
    Iterator<List<String>> sentencesIter = sentences.iterator();
    while(sentencesIter.hasNext()){
      List<String> sentence = sentencesIter.next();
      Iterator<String> wordsIter = sentence.iterator();
      while(wordsIter.hasNext()){
        String newWord = wordsIter.next();
        oneStep(newWord, taggedSentence);
      }//while(wordsIter.hasNext())
      //finished adding all the words from a sentence, add six more
      //staarts to flush all words out of the tagging buffer
      for(int i = 0; i < 6; i++){
        oneStep(staart, taggedSentence);
      }
      //we have a new finished sentence
      output.add(taggedSentence);
      taggedSentence = new ArrayList<String[]>();
    }//while(sentencesIter.hasNext())
    return output;
  }

  /**
   * Adds a new word to the window of 7 words (on the last position) and tags
   * the word currently in the middle (i.e. on position 3). This function
   * also reads the word on the first position and adds its tag to the
   * taggedSentence structure as this word would be lost at the next advance.
   * If this word completes a sentence then it returns true otherwise it
   * returns false.
   * @param word the new word
   * @param taggedSentence a List of pairs of strings representing the results
   * of tagging the current sentence so far.
   * @return returns true if a full sentence is now tagged, otherwise false.
   */
  protected boolean oneStep(String word, List<String[]> taggedSentence){
    //add the new word at the end of the text window
    for (int i=1 ; i<7 ; i++) {
      wordBuff[i-1] = wordBuff[i];
      tagBuff[i-1] = tagBuff[i];
      lexBuff[i-1] = lexBuff[i];
    }
    wordBuff[6] = word;
    lexBuff[6] = classifyWord(word);
    tagBuff[6] = lexBuff[6][0];

    //apply the rules to the word in the middle of the text window
    //Try to fire a rule for the current lexical entry. It may be the case that
    //no rule applies.
    List<Rule> rulesToApply = rules.get(lexBuff[3][0]);
    if(rulesToApply != null && rulesToApply.size() > 0){
      Iterator<Rule> rulesIter = rulesToApply.iterator();
      //find the first rule that applies, fire it and stop.
      while(rulesIter.hasNext() && !(rulesIter.next()).apply(this)){}
    }

    //save the tagged word from the first position
    String taggedWord = wordBuff[0];
    if(taggedWord != staart){
      taggedSentence.add(new String[]{taggedWord, tagBuff[0]});
      if(wordBuff[1] == staart){
        //wordTag[0] was the end of a sentence
        return true;
      }//if(wordBuff[1] == staart)
    }//if(taggedWord != staart)
    return false;

  }//protected List oneStep(String word, List taggedSentence)

  /**
   * Reads the rules from the rules input file
   */
  @SuppressWarnings("resource")
  public void readRules(URL rulesURL) throws IOException, InvalidRuleException{
    BufferedReader rulesReader = null;
    
    try {
      if(encoding == null) {
        rulesReader = new BomStrippingInputStreamReader(rulesURL.openStream());
      } else {
        rulesReader = new BomStrippingInputStreamReader(rulesURL.openStream(), this.encoding);
      }
  
      String line;
      Rule newRule;
  
      line = rulesReader.readLine();
      while(line != null){
        List<String> ruleParts = new ArrayList<String>();
        StringTokenizer tokens = new StringTokenizer(line);
        while (tokens.hasMoreTokens()) ruleParts.add(tokens.nextToken());
        if (ruleParts.size() < 3) throw new InvalidRuleException(line);
  
        newRule = createNewRule(ruleParts.get(2));
        newRule.initialise(ruleParts);
        List<Rule> existingRules = rules.get(newRule.from);
        if(existingRules == null){
          existingRules = new ArrayList<Rule>();
          rules.put(newRule.from, existingRules);
        }
        existingRules.add(newRule);
  
        line = rulesReader.readLine();
      }//while(line != null)
    }
    finally {
      IOUtils.closeQuietly(rulesReader);
    }
  }//public void readRules()

  public void showRules(){
    System.out.println(rules);
  }

  /**
   * Attempts to classify an unknown word.
   * @param wd the word to be classified
   */
  protected String[] classifyWord(String wd){
    String[] result;

    if (staart.equals(wd)) return staartLex;

    List<String> categories = lexicon.get(wd);
    if(categories != null){
      result = new String[categories.size()];
      for(int i = 0; i < result.length; i++){
        result[i] = categories.get(i);
      }
      return result;
    }

    //no lexical entry for the word. Try to guess
    if ('A' <= wd.charAt(0) && wd.charAt(0) <= 'Z') return deflex_NNP;

    for (int i=1 ; i < wd.length()-1 ; i++)
      if (wd.charAt(i) == '-') return deflex_JJ;

    for (int i=0 ; i < wd.length() ; i++)
      if ('0' <= wd.charAt(i) && wd.charAt(i) <= '9') return deflex_CD;

    if (wd.endsWith("ed") ||
        wd.endsWith("us") ||
        wd.endsWith("ic") ||
        wd.endsWith("ble") ||
        wd.endsWith("ive") ||
        wd.endsWith("ary") ||
        wd.endsWith("ful") ||
        wd.endsWith("ical") ||
        wd.endsWith("less")) return deflex_JJ;

    if (wd.endsWith("s")) return deflex_NNS;

    if (wd.endsWith("ly")) return deflex_RB;

    if (wd.endsWith("ing")) return deflex_VBG;

    return deflex_NN;
  }//private String[] classifyWord(String wd)


  /**
   * Main method. Runs the tagger using the arguments to find the resources
   * to be used for initialisation and the input file.
   */
  public static void main(String[] args){
    if(args.length == 0) help();
    try{
      LongOpt[] options = new LongOpt[]{
        new LongOpt("help", LongOpt.NO_ARGUMENT, null, 'h'),
        new LongOpt("lexicon", LongOpt.NO_ARGUMENT, null, 'l'),
        new LongOpt("rules", LongOpt.NO_ARGUMENT, null, 'r')
      };
      Getopt getopt = new Getopt("HepTag", args, "hl:r:", options);
      String lexiconUrlString = null;
      String rulesUrlString = null;
      int opt;
      while( (opt = getopt.getopt()) != -1 ){
        switch(opt) {
          // -h
          case 'h':{
            help();
            System.exit(0);
            break;
          }
          // -l new lexicon
          case 'l':{
            lexiconUrlString = getopt.getOptarg();
            break;
          }
          // -l new lexicon
          case 'r':{
            rulesUrlString = getopt.getOptarg();
            break;
          }
          default:{
            System.err.println("Invalid option " +
                               args[getopt.getOptind() -1] + "!");
            System.exit(1);
          }
        }//switch(opt)
      }//while( (opt = g.getopt()) != -1 )
      String[] fileNames = new String[args.length - getopt.getOptind()];
      for(int i = getopt.getOptind(); i < args.length; i++){
       fileNames[i - getopt.getOptind()] = args[i];
      }

      URL lexiconURL = (lexiconUrlString == null) ?
                       POSTagger.class.
                       getResource("/hepple/resources/sample_lexicon") :
                       new File(lexiconUrlString).toURI().toURL();

      URL rulesURL = (rulesUrlString == null) ?
                       POSTagger.class.
                       getResource("/hepple/resources/sample_ruleset.big") :
                       new File(rulesUrlString).toURI().toURL();

      POSTagger tagger = new POSTagger(lexiconURL, rulesURL);

      for(int i = 0; i < fileNames.length; i++){
        String file = fileNames[i];
        BufferedReader reader = null;
        
        try {
          reader = new BufferedReader(new FileReader(file));
        
          String line = reader.readLine();
  
          while(line != null){
            StringTokenizer tokens = new StringTokenizer(line);
            List<String> sentence = new ArrayList<String>();
            while(tokens.hasMoreTokens()) sentence.add(tokens.nextToken());
            List<List<String>> sentences = new ArrayList<List<String>>();
            sentences.add(sentence);
            List<List<String[]>> result = tagger.runTagger(sentences);
  
            Iterator<List<String[]>> iter = result.iterator();
            while(iter.hasNext()){
              List<String[]> sentenceFromTagger = iter.next();
              Iterator<String[]> sentIter = sentenceFromTagger.iterator();
              while(sentIter.hasNext()){
                String[] tag = sentIter.next();
                System.out.print(tag[0] + "/" + tag[1]);
                if(sentIter.hasNext()) System.out.print(" ");
                else System.out.println();
              }//while(sentIter.hasNext())
            }//while(iter.hasNext())
            line = reader.readLine();
          }//while(line != null)
        }
        finally {
          IOUtils.closeQuietly(reader);
        }
//
//
//
//        List result = tagger.runTagger(readInput(file));
//        Iterator iter = result.iterator();
//        while(iter.hasNext()){
//          List sentence = (List)iter.next();
//          Iterator sentIter = sentence.iterator();
//          while(sentIter.hasNext()){
//            String[] tag = (String[])sentIter.next();
//            System.out.print(tag[0] + "/" + tag[1]);
//            if(sentIter.hasNext()) System.out.print(" ");
//            else System.out.println();
//          }//while(sentIter.hasNext())
//        }//while(iter.hasNext())
      }//for(int i = 0; i < fileNames.length; i++)
    }catch(Exception e){
      e.printStackTrace();
    }
  }//public static void main(String[] args)

  /**
   * Prints the help message
   */
  private static void help(){
    System.out.println(
      "NAME\n" +
      "HepTag - a Part-of-Speech tagger\n" +
      "see http://www.dcs.shef.ac.uk/~hepple/papers/acl00/abstract.html \n\n" +
      "SYNOPSIS\n\tjava hepple.postag.POSTagger [options] file1 [file2 ...]\n\n" +
      "OPTIONS:\n" +
      "-h, --help \n\tdisplays this message\n" +
      "-l, --lexicon <lexicon file>\n\tuses specified lexicon\n" +
      "-r, --rules <rules file>\n\tuses specified rules");
  }

  /**
   * Reads one input file and creates the structure needed by the tagger
   * for input.
   */
  @SuppressWarnings("unused")
  private static List<List<String>> readInput(String file) throws IOException{
    BufferedReader reader = null;
    
    try {
      reader = new BufferedReader(new FileReader(file));
    
      String line = reader.readLine();
      List<List<String>> result = new ArrayList<List<String>>();
      while(line != null){
        StringTokenizer tokens = new StringTokenizer(line);
        List<String> sentence = new ArrayList<String>();
        while(tokens.hasMoreTokens()) sentence.add(tokens.nextToken());
        result.add(sentence);
        line = reader.readLine();
      }//while(line != null)
      return result;
    }
    finally {
      IOUtils.closeQuietly(reader);
    }
  }//private static List readInput(File file) throws IOException

}//public class POSTagger
