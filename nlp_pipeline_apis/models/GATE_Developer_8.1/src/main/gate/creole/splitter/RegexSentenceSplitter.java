/*
 *  Copyright (c) 1995-2012, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 *  Valentin Tablan, 04 Sep 2007
 *
 *  $Id: RegexSentenceSplitter.java 17595 2014-03-08 13:05:32Z markagreenwood $
 */
package gate.creole.splitter;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.regex.*;

import org.apache.commons.io.IOUtils;

import gate.*;
import gate.creole.*;
import gate.creole.metadata.CreoleParameter;
import gate.creole.metadata.CreoleResource;
import gate.creole.metadata.Optional;
import gate.creole.metadata.RunTime;
import gate.util.*;

/**
 * A fast sentence splitter replacement based on regular expressions.
 */
@CreoleResource(name="RegEx Sentence Splitter", icon="sentence-splitter", comment="A sentence splitter based on regular expressions.", helpURL="http://gate.ac.uk/userguide/sec:annie:regex-splitter")
public class RegexSentenceSplitter extends AbstractLanguageAnalyser {

  /**
   * Parameter name
   */
  public static final String SPLIT_DOCUMENT_PARAMETER_NAME = "document";

  /**
   * Parameter name
   */
  public static final String SPLIT_INPUT_AS_PARAMETER_NAME = "inputASName";

  /**
   * Parameter name
   */
  public static final String SPLIT_OUTPUT_AS_PARAMETER_NAME = "outputASName";

  /**
   * Parameter name
   */
  public static final String SPLIT_ENCODING_PARAMETER_NAME = "encoding";

  /**
   * Parameter name
   */
  public static final String SPLIT_SPLIT_LIST_PARAMETER_NAME = "splitListURL";


  /**
   * Parameter name
   */
  public static final String SPLIT_NON_SPLIT_LIST_PARAMETER_NAME = "nonSplitListURL";

  /**
   * serialisation ID
   */
  private static final long serialVersionUID = 1L;

  /**
   * Output annotation set name.
   */
  protected String outputASName;

  /**
   * Encoding used when reading config files
   */
  protected String encoding;

  /**
   * URL pointing to a file with regex patterns for internal sentence splits.
   */
  protected URL internalSplitListURL;

  /**
   * URL pointing to a file with regex patterns for external sentence splits.
   */
  protected URL externalSplitListURL;

  /**
   * URL pointing to a file with regex patterns for non sentence splits.
   */
  protected URL nonSplitListURL;


  protected Pattern internalSplitsPattern;

  protected Pattern externalSplitsPattern;

  protected Pattern nonSplitsPattern;

  protected Pattern compilePattern(URL paternsListUrl, String encoding)
          throws UnsupportedEncodingException, IOException {
    BufferedReader reader = null;
    StringBuffer patternString = new StringBuffer();
    
    try {
      reader =
              new BomStrippingInputStreamReader(paternsListUrl.openStream(),
                      encoding);
      
      String line = reader.readLine();
      while(line != null) {
        line = line.trim();

        if(line.length() == 0 || line.startsWith("//")) {
          // ignore empty lines and comments
        } else {
          if(patternString.length() > 0) patternString.append("|");
          patternString.append("(?:" + line + ")");
        }
        // move to next line
        line = reader.readLine();
      }
    } finally {
      IOUtils.closeQuietly(reader);
    }
    return Pattern.compile(patternString.toString());
  }


//  protected enum StartEnd {START, END};

  /**
   * A comparator for MatchResult objects. This is used to find the next match
   * result in a text. A null value is used to signify that no more matches are
   * available, hence nulls are the largest value, according to this comparator.
   * @author Valentin Tablan (valyt)
   */
  private class MatchResultComparator implements Comparator<MatchResult>{

    /* (non-Javadoc)
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    @Override
    public int compare(MatchResult o1, MatchResult o2) {
      if(o1 == null && o2 == null) return 0;
      if(o1 == null) return 1;
      if(o2 == null) return -1;
      //at this point both match results are not null
      return o1.start() - o2.start();
    }
  }

  @Override
  public void execute() throws ExecutionException {
    interrupted = false;
    int lastProgress = 0;
    fireProgressChanged(lastProgress);
    //get pointers to the annotation sets
    AnnotationSet outputAS = (outputASName == null ||
            outputASName.trim().length() == 0) ?
                             document.getAnnotations() :
                             document.getAnnotations(outputASName);

    String docText = document.getContent().toString();

    /* If the document's content is empty or contains only whitespace,
     * we drop out right here, since there's nothing to sentence-split.     */
    if (docText.trim().length() < 1)  {
      return;
    }

    Matcher internalSplitMatcher = internalSplitsPattern.matcher(docText);
    Matcher externalSplitMatcher = externalSplitsPattern.matcher(docText);

    Matcher nonSplitMatcher = nonSplitsPattern.matcher(docText);
    //store all non split locations in a list of pairs
    List<int[]> nonSplits = new LinkedList<int[]>();
    while(nonSplitMatcher.find()){
      nonSplits.add(new int[]{nonSplitMatcher.start(), nonSplitMatcher.end()});
    }
    //this lists holds the next matches at each step
    List<MatchResult> nextSplitMatches = new ArrayList<MatchResult>();
    //initialise matching process
    MatchResult internalMatchResult = null;
    if(internalSplitMatcher.find()){
      internalMatchResult = internalSplitMatcher.toMatchResult();
      nextSplitMatches.add(internalMatchResult);
    }
    MatchResult externalMatchResult = null;
    if(externalSplitMatcher.find()){
      externalMatchResult = externalSplitMatcher.toMatchResult();
      nextSplitMatches.add(externalMatchResult);
    }
    MatchResultComparator comparator = new MatchResultComparator();
    int lastSentenceEnd = 0;

    while(!nextSplitMatches.isEmpty()){
      //see which one matches first
      Collections.sort(nextSplitMatches, comparator);
      MatchResult nextMatch = nextSplitMatches.remove(0);
      if(nextMatch == internalMatchResult){
        //we have a new internal split; see if it's vetoed or not
        if(!veto(nextMatch, nonSplits)){
          //split is not vetoed
          try {
            //add the split annotation
            FeatureMap features = Factory.newFeatureMap();
            features.put("kind", "internal");
            outputAS.add(new Long(nextMatch.start()), new Long(nextMatch.end()),
                    "Split", features);
            //generate the sentence annotation
            int endOffset = nextMatch.end();
            //find the first non whitespace character starting from where the
            //last sentence ended
            while(lastSentenceEnd < endOffset &&
                  Character.isWhitespace(
                          Character.codePointAt(docText, lastSentenceEnd))){
              lastSentenceEnd++;
            }
            //if there is any useful text between the two offsets, generate
            //a new sentence
            if(lastSentenceEnd < nextMatch.start()){
              outputAS.add(new Long(lastSentenceEnd), new Long(endOffset),
                      ANNIEConstants.SENTENCE_ANNOTATION_TYPE,
                      Factory.newFeatureMap());
            }
            //store the new sentence end
            lastSentenceEnd = endOffset;
          } catch(InvalidOffsetException e) {
            // this should never happen
            throw new ExecutionException(e);
          }
        }
        //prepare for next step
        if(internalSplitMatcher.find()){
          internalMatchResult = internalSplitMatcher.toMatchResult();
          nextSplitMatches.add(internalMatchResult);
        }else{
          internalMatchResult = null;
        }
      }else if(nextMatch == externalMatchResult){
        //we have a new external split; see if it's vetoed or not
        if(!veto(nextMatch, nonSplits)){
          //split is not vetoed
          try {
            //generate the split
            FeatureMap features = Factory.newFeatureMap();
            features.put("kind", "external");
            outputAS.add(new Long(nextMatch.start()), new Long(nextMatch.end()),
                    "Split", features);
            //generate the sentence annotation
            //find the last non whitespace character, going backward from
            //where the external skip starts
            int endOffset = nextMatch.start();
            while(endOffset > lastSentenceEnd &&
                    Character.isSpaceChar(
                            Character.codePointAt(docText, endOffset -1))){
              endOffset--;
            }
            //find the first non whitespace character starting from where the
            //last sentence ended
            while(lastSentenceEnd < endOffset &&
                    Character.isSpaceChar(
                            Character.codePointAt(docText, lastSentenceEnd))){
              lastSentenceEnd++;
            }
            //if there is any useful text between the two offsets, generate
            //a new sentence
            if(lastSentenceEnd < endOffset){
              outputAS.add(new Long(lastSentenceEnd), new Long(endOffset),
                      ANNIEConstants.SENTENCE_ANNOTATION_TYPE,
                      Factory.newFeatureMap());
            }
            //store the new sentence end
            lastSentenceEnd = nextMatch.end();
          } catch(InvalidOffsetException e) {
            // this should never happen
            throw new ExecutionException(e);
          }
        }
        //prepare for next step
        if(externalSplitMatcher.find()){
          externalMatchResult = externalSplitMatcher.toMatchResult();
          nextSplitMatches.add(externalMatchResult);
        }else{
          externalMatchResult = null;
        }
      }else{
        //malfunction
        throw new ExecutionException("Invalid state - cannot identify match!");
      }
      //report progress
      int newProgress = 100 * lastSentenceEnd / docText.length();
      if(newProgress - lastProgress > 20){
        lastProgress = newProgress;
        fireProgressChanged(lastProgress);
      }
    }//while(!nextMatches.isEmpty()){
    fireProcessFinished();
  }


  /**
   * Checks whether a possible match is being vetoed by a non split match. A
   * possible match is vetoed if it any nay overlap with a veto region.
   *
   * @param split the match result representing the split to be tested
   * @param vetoRegions regions where matches are not allowed. For efficiency
   * reasons, this method assumes these regions to be non overlapping and sorted
   * in ascending order.
   * All veto regions that end before the proposed match are also discarded
   * (again for efficiency reasons). This requires the proposed matches to be
   * sent to this method in ascending order, so as to avoid malfunctions.
   * @return <tt>true</tt> iff the proposed split should be ignored
   */
  private boolean veto(MatchResult split, List<int[]> vetoRegions){
    //if no more non splits available, accept everything
    for(Iterator<int[]> vetoRegIter = vetoRegions.iterator();
        vetoRegIter.hasNext();){
      int[] aVetoRegion = vetoRegIter.next();
      if(aVetoRegion[1] -1 < split.start()){
        //current veto region ends before the proposed split starts
        //--> discard the veto region
        vetoRegIter.remove();
      }else if(split.end() -1 < aVetoRegion[0]){
        //veto region starts after the split ends
        //-> we can return false
        return false;
      }else{
        //we have overlap
        return true;
      }
    }
    //if we got this far, all veto regions are before the split
    return false;
  }

  @Override
  public Resource init() throws ResourceInstantiationException {
    super.init();
    try {
      //sanity checks
      if(internalSplitListURL == null)
        throw new ResourceInstantiationException("No list of internal splits provided!");
      if(externalSplitListURL == null)
        throw new ResourceInstantiationException("No list of external splits provided!");
      if(nonSplitListURL == null)
        throw new ResourceInstantiationException("No list of non splits provided!");
      if(encoding == null)
        throw new ResourceInstantiationException("No encoding provided!");

      //load the known abbreviations list
      internalSplitsPattern = compilePattern(internalSplitListURL, encoding);
      externalSplitsPattern = compilePattern(externalSplitListURL, encoding);
      nonSplitsPattern = compilePattern(nonSplitListURL, encoding);
    } catch(UnsupportedEncodingException e) {
      throw new ResourceInstantiationException(e);
    } catch(IOException e) {
      throw new ResourceInstantiationException(e);
    }

    return this;
  }

  /**
   * @return the outputASName
   */
  public String getOutputASName() {
    return outputASName;
  }

  /**
   * @param outputASName the outputASName to set
   */
  @RunTime
  @Optional
  @CreoleParameter(comment="The annotation set to be used as output for 'Sentence' and 'Split' annotations")
  public void setOutputASName(String outputASName) {
    this.outputASName = outputASName;
  }

  /**
   * @return the encoding
   */
  public String getEncoding() {
    return encoding;
  }

  /**
   * @param encoding the encoding to set
   */
  @CreoleParameter(comment="The encoding used for reading the definition files", defaultValue="UTF-8")
  public void setEncoding(String encoding) {
    this.encoding = encoding;
  }

  /**
   * @return the internalSplitListURL
   */
  public URL getInternalSplitListURL() {
    return internalSplitListURL;
  }

  /**
   * @param internalSplitListURL the internalSplitListURL to set
   */
  @CreoleParameter(defaultValue="resources/regex-splitter/internal-split-patterns.txt", suffixes="txt", comment="The URL to the internal splits pattern list")
  public void setInternalSplitListURL(URL internalSplitListURL) {
    this.internalSplitListURL = internalSplitListURL;
  }

  /**
   * @return the externalSplitListURL
   */
  public URL getExternalSplitListURL() {
    return externalSplitListURL;
  }

  /**
   * @param externalSplitListURL the externalSplitListURL to set
   */
  @CreoleParameter(defaultValue="resources/regex-splitter/external-split-patterns.txt", comment="The URL to the external splits pattern list", suffixes="txt")
  public void setExternalSplitListURL(URL externalSplitListURL) {
    this.externalSplitListURL = externalSplitListURL;
  }

  /**
   * @return the nonSplitListURL
   */
  public URL getNonSplitListURL() {
    return nonSplitListURL;
  }

  /**
   * @param nonSplitListURL the nonSplitListURL to set
   */
  @CreoleParameter(defaultValue="resources/regex-splitter/non-split-patterns.txt", comment="The URL to the non splits pattern list", suffixes="txt")
  public void setNonSplitListURL(URL nonSplitListURL) {
    this.nonSplitListURL = nonSplitListURL;
  }

  /**
   * @return the internalSplitsPattern
   */
  public Pattern getInternalSplitsPattern() {
    return internalSplitsPattern;
  }

  /**
   * @param internalSplitsPattern the internalSplitsPattern to set
   */
  public void setInternalSplitsPattern(Pattern internalSplitsPattern) {
    this.internalSplitsPattern = internalSplitsPattern;
  }
}
