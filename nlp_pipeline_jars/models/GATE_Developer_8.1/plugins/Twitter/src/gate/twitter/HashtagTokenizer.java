/*
 * HashtagTokenizer.java
 * 
 * Copyright (c) 1995-2014, The University of Sheffield. See the file
 * COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 * 
 * This file is part of GATE (see http://gate.ac.uk/), and is free software,
 * licenced under the GNU Library General Public License, Version 2, June 1991
 * (in the distribution as file licence.html, and also available at
 * http://gate.ac.uk/gate/licence.html).
 * 
 * Mark A. Greenwood, 24 Jan 2014
 */
package gate.twitter;

import static gate.Utils.getAnnotationsAtOffset;
import static gate.Utils.stringFor;
import static org.apache.commons.lang.StringUtils.isAllLowerCase;
import static org.apache.commons.lang.StringUtils.isAllUpperCase;
import static org.apache.commons.lang.StringUtils.isAlpha;
import static org.apache.commons.lang.StringUtils.isNumeric;
import gate.Annotation;
import gate.AnnotationSet;
import gate.Factory;
import gate.FeatureMap;
import gate.Gate;
import gate.LanguageAnalyser;
import gate.Resource;
import gate.annotation.AnnotationSetImpl;
import gate.creole.AbstractLanguageAnalyser;
import gate.creole.ExecutionException;
import gate.creole.ExecutionInterruptedException;
import gate.creole.ResourceInstantiationException;
import gate.creole.metadata.CreoleParameter;
import gate.creole.metadata.CreoleResource;
import gate.creole.metadata.Optional;
import gate.creole.metadata.RunTime;
import gate.util.InvalidOffsetException;

import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@CreoleResource(name = "Hashtag Tokenizer", icon = "HashtagTokenizer",
    comment = "Tokenizes Multi-Word Hashtags",
    helpURL = "http://gate.ac.uk/userguide/sec:social:twitter:hashtag")
public class HashtagTokenizer extends AbstractLanguageAnalyser {

  private static final long serialVersionUID = -7848183952807024913L;

  // A comparator that sorts annotations by length; longest first
  private static Comparator<Annotation> lengthComparator =
    new Comparator<Annotation>() {
      @Override
      public int compare(Annotation a1, Annotation a2) {
        long l1 = a1.getEndNode().getOffset() - a1.getStartNode().getOffset();
        long l2 = a2.getEndNode().getOffset() - a2.getStartNode().getOffset();

        return (int)(l2 - l1);
      }
    };

  // the gazetteer that looks for likely words
  private LanguageAnalyser gaz;

  // the URL from which the gazetteer is loaded
  private URL gazURL;

  // the names of the input and output annotation sets
  private String inputASName, outputASName;

  // a debug flag
  private Boolean debug = Boolean.FALSE;

  public Boolean getDebug() {
    return debug;
  }

  @CreoleParameter(defaultValue = "false")
  @RunTime
  @Optional
  public void setDebug(Boolean debug) {
    this.debug = debug;
  }

  public URL getGazetteerURL() {
    return gazURL;
  }

  @CreoleParameter(defaultValue = "resources/hashtag/gazetteer/lists.def")
  public void setGazetteerURL(URL gazURL) {
    this.gazURL = gazURL;
  }

  public String getInputASName() {
    return inputASName;
  }

  @CreoleParameter
  @RunTime
  @Optional
  public void setInputASName(String inputASName) {
    this.inputASName = inputASName;
  }

  public String getOutputASName() {
    return outputASName;
  }

  @CreoleParameter
  @RunTime
  @Optional
  public void setOutputASName(String outputASName) {
    this.outputASName = outputASName;
  }

  @Override
  public Resource init() throws ResourceInstantiationException {

    // load and configure the hidden gazetteer
    FeatureMap hidden = Factory.newFeatureMap();
    Gate.setHiddenAttribute(hidden, true);

    FeatureMap params = Factory.newFeatureMap();
    params.put("listsURL", gazURL);
    params.put("caseSensitive", Boolean.FALSE);

    params.put("longestMatchOnly", Boolean.FALSE);
    params.put("wholeWordsOnly", Boolean.FALSE);

    if(gaz == null) {
      gaz =
        (LanguageAnalyser)Factory.createResource(
          "gate.creole.gazetteer.DefaultGazetteer", params, hidden,
          "Hashtag Tokenizer Gazetteer");
    } else {
      gaz.setParameterValues(params);
      gaz.reInit();
    }

    return this;
  }

  @Override
  public void execute() throws ExecutionException {
    // reset interrupt flag
    interrupted = false;
    AnnotationSet inputAS = document.getAnnotations(inputASName);
    AnnotationSet outputAS = document.getAnnotations(outputASName);

    FeatureMap features = Factory.newFeatureMap();

    long startTime = System.currentTimeMillis();
    fireStatusChanged("Tokenizing Hashtags: " + document.getName());
    fireProgressChanged(0);
    int count = 0;
    
    // get all the lookups we are going to use for decomposition...
    AnnotationSet lookups = new AnnotationSetImpl(document);

    try {
      // run the gazetteer to produce the HashtagLookup annotations
      gaz.setParameterValue("annotationSetName", inputASName);
      gaz.setDocument(document);
      gaz.execute();

      // get all the hashtags
      AnnotationSet hashtags = inputAS.get("Hashtag");

      for(Annotation hashtag : inputAS.get("Hashtag")) {
        // for each hashtag in the document...

        AnnotationSet contained = inputAS.getContained(hashtag.getStartNode().getOffset(), hashtag.getEndNode().getOffset());
        
        //clear away any left overs from previous tags
        lookups.clear();

        // which are the HashtagLookup
        lookups.addAll(contained.get("HashtagLookup"));

        // any other Lookups the user has generated
        lookups.addAll(contained.get("Lookup"));

        // and any number tokens
        features = Factory.newFeatureMap();
        features.put("kind", "number");
        lookups.addAll(contained.get("Token", features));

        // the _ appears to be allowed so add them as well
        features = Factory.newFeatureMap();
        features.put("string", "_");
        lookups.addAll(contained.get("Token", features));
        
        if(isInterrupted()) { throw new ExecutionInterruptedException(
          "The execution of the hashtag tokenizer has been abruptly interrupted!"); }

        // this will hold the best we have seen so far
        List<List<Annotation>> fewestTokens = new ArrayList<List<Annotation>>();

        // get all the lookups that start at the beginning of the hashtag
        List<Annotation> start =
          sort(getAnnotationsAtOffset(lookups, hashtag.getStartNode()
            .getOffset() + 1));

        for(Annotation a : start) {
          // for each lookup search for a valid tokenization
          List<List<Annotation>> found =
            search(lookups, hashtag.getEndNode().getOffset(), a);

          if(found != null) {
            // if we found a contender and it's the best so far store it
            if(fewestTokens.isEmpty()) {
              fewestTokens.addAll(found);
            } else if(found.get(0).size() == fewestTokens.get(0).size()) {
              fewestTokens.addAll(found);
            } else if(found.get(0).size() < fewestTokens.get(0).size()) {
              fewestTokens.clear();
              fewestTokens.addAll(found);
            }
          }
        }

        if(debug && fewestTokens.size() > 1) {
          System.out.println(stringFor(document, hashtag));
          display(fewestTokens);
        }

        if(fewestTokens.isEmpty()) {
          // if we didn't find any sensible tokenizations then let's see if the
          // hashtag is mized case

          String tagText = stringFor(document, hashtag).substring(1);
          if("mixedCaps".equals(getTokenType(tagText)[1])) {
            // if we have a mixed case hahstag then let's assume it is
            // CamelCased and split it accordingly

            // TODO think about camel case which includes numbers

            // a list to hold the tokens
            List<Annotation> found = new ArrayList<Annotation>();

            // start looking for token breaks aftert the initial #
            long begin = hashtag.getStartNode().getOffset() + 1;

            for(String token : tagText
              .split("((?<=[a-z])(?=[A-Z]))|((?<=[A-Z]{2,})(?=[a-z]))")) {
              // split the token at the case changes...

              // create the annotation in the Lookup set and add it to the found
              // list
              found.add(lookups.get(lookups.add(begin,
                (begin += token.length()), "CamelToken",
                Factory.newFeatureMap())));
            }

            // record the tokenization so we can process it later
            fewestTokens.add(found);
          }

        }

        if(!fewestTokens.isEmpty()) {
          // if we found a valid tokenization then...

          // remove any existing Token annotations
          inputAS.removeAll(inputAS.get("Token").getContained(
            hashtag.getStartNode().getOffset(),
            hashtag.getEndNode().getOffset()));

          // create a punctuation Token over the #
          features = Factory.newFeatureMap();
          features.put("string", "#");
          features.put("length", "1");
          features.put("kind", "punctuation");
          outputAS.add(hashtag.getStartNode().getOffset(), hashtag
            .getStartNode().getOffset() + 1, "Token", features);

          // let's assume that the first one we found is best
          int prefered = 0;

          for(int i = 0; i < fewestTokens.size(); ++i) {
            // check those we have found and skip over any that contain single
            // letter or mixed case words

            boolean okay = true;
            for(Annotation a : fewestTokens.get(i)) {
              // single letter words are not great
              if(a.getEndNode().getOffset() - a.getStartNode().getOffset() == 1)
                okay = false;
            }

            if(okay) {
              // if it contains neither a single letter word or a mixed case
              // word then we should definitely prefer this one
              prefered = i;
              break;
            }
          }

          for(Annotation a : fewestTokens.get(prefered)) {
            // for each new token...

            // find where it starts/ends and its length
            long startOffset = a.getStartNode().getOffset();
            long endOffset = a.getEndNode().getOffset();
            String length = Long.toString(endOffset - startOffset);

            // get the actual text
            String string = stringFor(document, a);

            // work out what kind of token it is and if it is a word
            // what its orthography is
            String[] tokenType = getTokenType(string);
            String kind = tokenType[0];
            String orth = tokenType[1];

            // create the new Token annotation
            features = Factory.newFeatureMap();
            features.put("string", string);
            features.put("length", length);
            features.put("kind", kind);
            if(orth != null) features.put("orth", orth);
            outputAS.add(startOffset, endOffset, "Token", features);

            if(debug) {
              // for debug purposes add a matching set of HashtagToken
              // annotations
              features = Factory.newFeatureMap();
              features.put("string", string);
              features.put("length", length);
              features.put("kind", kind);
              if(orth != null) features.put("orth", orth);
              outputAS.add(startOffset, endOffset, "HashtagToken", features);
            }
          }
        } else if(debug) {
          System.err.println(stringFor(document, hashtag));
          
          AnnotationSet tokens = inputAS.get("Token").getContained(
            hashtag.getStartNode().getOffset()+1,
            hashtag.getEndNode().getOffset());
          
          for (Annotation token : tokens) {
            
            features = Factory.newFeatureMap();
            features.putAll(token.getFeatures());
            outputAS.add(token.getStartNode().getOffset(), token.getEndNode().getOffset(), "HashtagToken", features);
          }
        }

        fireProgressChanged(count++ * 100 / hashtags.size());
      }

      fireProcessFinished();
      fireStatusChanged("Hashtags in " +
        document.getName() +
        " tokenized in " +
        NumberFormat.getInstance().format(
          (double)(System.currentTimeMillis() - startTime) / 1000) +
        " seconds!");
    } catch(InvalidOffsetException e) {
      throw new ExecutionException(e);
    } catch(ResourceInstantiationException e) {
      throw new ExecutionException(e);
    } finally {
      gaz.setDocument(null);

      if(!debug) {
        inputAS.removeAll(inputAS.get("HashtagLookup"));
      }
    }
  }

  @Override
  public void cleanup() {
    Factory.deleteResource(gaz);
  }

  private static String[] getTokenType(String string) {
    String kind = "symbol";
    String orth = null;
    if(isAlpha(string)) {
      kind = "word";

      if(isAllLowerCase(string))
        orth = "lowercase";
      else if(isAllUpperCase(string))
        orth = "allCaps";
      else if(isAllUpperCase(string.substring(0, 1)) &&
        isAllLowerCase(string.substring(1)))
        orth = "upperInitial";
      else orth = "mixedCaps";
    } else if(isNumeric(string)) {
      kind = "number";
    } else if(string.matches("(\\p{Punct})+")) {
      kind = "punctuation";
    }

    return new String[]{kind, orth};
  }

  private void display(List<List<Annotation>> found) {
    for(List<Annotation> tokens : found) {
      System.out.print("   ");
      for(Annotation token : tokens) {
        System.out.print(stringFor(document, token) + " ");
      }
      System.out.print("\n");
    }
  }

  /**
   * Depth first search through a set of annotations to find a contiguous set
   * for a given character range.
   **/
  private List<List<Annotation>> search(AnnotationSet lookups, Long endOffset,
                                        Annotation token)
    throws InvalidOffsetException {

    if("mixedCaps"
      .equals(getTokenType(stringFor(lookups.getDocument(), token))[1]))
      return null;

    List<List<Annotation>> shortest = new ArrayList<List<Annotation>>();

    if(token.getEndNode().getOffset().equals(endOffset)) {
      // if the token we are starting from ends at the right place then we
      // can stop and just return the single token as the result
      List<Annotation> found = new ArrayList<Annotation>();
      found.add(token);

      shortest.add(found);
      return shortest;
    }

    if(endOffset - token.getEndNode().getOffset() > 1) {
      // if there are two or more characters after this token then...

      // get the rest of the text of the hashtag
      String rest =
        lookups.getDocument().getContent()
          .getContent(token.getEndNode().getOffset() - 1, endOffset).toString();

      if(rest.substring(1).matches(rest.substring(0, 1) + "+")) {
        // if the rest of the hashtag is just the same as the last letter of
        // this token then someone has just lent on the keyboard for emphasis

        // so... extend the current token to include the rest of the hashtag
        Annotation newToken =
          lookups.get(lookups.add(token.getStartNode().getOffset(), endOffset,
            "HashtagLookup", Factory.newFeatureMap()));

        // return this extended token
        List<Annotation> found = new ArrayList<Annotation>();
        found.add(newToken);

        shortest.add(found);
        return shortest;
      }
    }

    // get all possibilities that start after the current annotation that
    // could be used to cover the next bit of the document
    List<Annotation> next =
      sort(getAnnotationsAtOffset(lookups, token.getEndNode().getOffset()));

    // if there aren't any annotations we can use then return null as we can
    // never fully cover the range from this point
    if(next == null || next.isEmpty()) return null;

    for(Annotation a : next) {
      // use each possible annotation to start a new search
      List<List<Annotation>> part = search(lookups, endOffset, a);

      if(part != null) {
        if(shortest.isEmpty()) {
          shortest.addAll(part);
        } else if(part.get(0).size() == shortest.get(0).size()) {
          shortest.addAll(part);
        } else if(part.get(0).size() < shortest.get(0).size()) {
          shortest.clear();
          shortest.addAll(part);
        }
      }
    }

    // if we didn't find a match then return null
    if(shortest.isEmpty()) return null;

    // add the token we started from to the beginning of the match
    for(List<Annotation> found : shortest) {
      found.add(0, token);
    }

    // return the match we found
    return shortest;
  }

  /**
   * Returns a list in which the Annotations are sorted by length, longest
   * first.
   **/
  private List<Annotation> sort(AnnotationSet annotations) {

    List<Annotation> sorted = new ArrayList<Annotation>();

    if(annotations == null || annotations.isEmpty()) return sorted;

    sorted.addAll(annotations);
    Collections.sort(sorted, lengthComparator);

    // TODO filter out annotations with the same span

    List<Annotation> filtered = new ArrayList<Annotation>();
    long length = -1;
    for(Annotation a : sorted) {
      long al = a.getEndNode().getOffset() - a.getStartNode().getOffset();
      if(length == -1 || al != length) {
        filtered.add(a);
        length = al;
      }
    }

    return filtered;
  }
}
