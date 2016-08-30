/*
 * Copyright (c) 2009-2013, The University of Sheffield.
 * 
 * This file is part of GATE (see http://gate.ac.uk/), and is free software,
 * Licensed under the GNU Library General Public License, Version 3, June 2007
 * (in the distribution as file licence.html, and also available at
 * http://gate.ac.uk/gate/licence.html).
 */

package gate.alchemyAPI;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Factory;
import gate.FeatureMap;
import gate.Utils;
import gate.creole.ExecutionException;
import gate.creole.metadata.CreoleParameter;
import gate.creole.metadata.CreoleResource;
import gate.creole.metadata.RunTime;
import gate.util.InvalidOffsetException;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.alchemyapi.api.AlchemyAPI_KeywordParams;

/**
 * @author Mark A. Greenwood
 */
@CreoleResource(name = "AlchemyAPI: Keyword Extraction", comment = "Runs the AlchemyAPI Keyword Extraction service on a GATE document", icon = "Alchemy")
public class KeywordExtraction extends AbstractAlchemyPR {

  private static final long serialVersionUID = -8443994795704361590L;

  /**
   * Number of sentences to be sent to Alchemy API to process in one batch
   * Default is 10 sentences.
   */
  private Integer numberOfSentencesInBatch = 10;

  /**
   * This is the number of sentences that are used as sentences in context both
   * on left and right side and not annotated when sent as part of a batch
   * unless there's no more sentence to be considered as part of the context.
   */
  private Integer numberOfSentencesInContext = 5;

  protected String annotationType;

  /** debug */
  private boolean DEBUG = false;

  /**
   * Should be called to execute this PR on a document.
   */
  public void execute() throws ExecutionException {
    fireStatusChanged("Checking runtime parameters");
    progressChanged(0);
    // if no document provided
    if(document == null) { throw new ExecutionException("Document is null!"); }

    // obtain the content
    String documentContent = document.getContent().toString();

    if(documentContent.trim().length() == 0) return;

    // annotation set to use
    AnnotationSet set =
      outputASName == null || outputASName.trim().length() == 0 ? document
        .getAnnotations() : document.getAnnotations(outputASName);
    AnnotationSet inputAS =
      inputASName == null || inputASName.trim().length() == 0 ? document
        .getAnnotations() : document.getAnnotations(inputASName);
    // start time
    long startTime = System.currentTimeMillis();
    // all sentences
    List<Annotation> allSents = Utils.inDocumentOrder(inputAS.get("Sentence"));
    // if no sentence annotations report that
    if(allSents.size() == 0) { throw new ExecutionException(
      "Atleast one sentence must be provided"); }
    // Tokens
    List<Annotation> allTokens = Utils.inDocumentOrder(inputAS.get("Token"));

    // if no token annotations report that
    if(allTokens.size() == 0) { throw new ExecutionException(
      "Document must have Tokens"); }

    for(int i = 0; i < allSents.size(); i += numberOfSentencesInBatch) {
      if(interrupted) return;
      int endIndex = i + numberOfSentencesInBatch - 1;
      if(endIndex >= allSents.size()) endIndex = allSents.size() - 1;

      // we add numberOfSentencesInContext in left and right context if
      // they are available
      int contextStartIndex = i - numberOfSentencesInContext;

      if(contextStartIndex < 0) contextStartIndex = 0;
      int contextEndIndex = endIndex + numberOfSentencesInContext;
      if(contextEndIndex >= allSents.size()) {
        contextEndIndex = allSents.size() - 1;
      }

      // obtain the string to be annotated
      String sentString =
        Utils.stringFor(document, Utils.start(allSents.get(contextStartIndex)),
          Utils.end(allSents.get(contextEndIndex)));

      // the actual content
      String contentString =
        Utils.stringFor(document, Utils.start(allSents.get(i)),
          Utils.end(allSents.get(endIndex)));

      // progress
      progressChanged((int)(i * 90 / (double)allSents.size()));
      fireStatusChanged("Posted a part of document for processing");
      // now process the text
      // post the content to a service and obtain output
      // what we get back is the mathcing text which uri in them
      List<TextSpan> result = process(sentString.toString());
      fireStatusChanged("Copying annotations on the document");

      // since we don't have any offsets returned from the alchemy API we
      // first annotated longer spans and ignored the overlapping smaller spans
      Collections.sort(result);

      for(TextSpan r : result) {
        int index = -1;
        // annotate all occurrences of the matching Text
        while((index = contentString.indexOf(r.text, index + 1)) >= 0) {
          int stOffset = Utils.start(allSents.get(i)).intValue() + index;
          int enOffset = stOffset + r.text.length();
          try {
            if(set.getCovering(annotationType, (long)stOffset, (long)enOffset)
              .isEmpty()) {
              FeatureMap map = Factory.newFeatureMap();
              map.putAll(r.featureMap);
              set.add((long)stOffset, (long)enOffset, annotationType, map);
            }
          } catch(InvalidOffsetException e) {
            throw new ExecutionException(e);
          }
        }
      }
    }

    fireStatusChanged("deleting Mention annotations not in sync with Tokens");
    // get rid of annotations that donot sync with token boundaries
    AnnotationSet mentionSet = set.get("Mention");
    Set<Annotation> toDelete = new HashSet<Annotation>();
    for(Annotation aMention : mentionSet) {
      long start = Utils.start(aMention);
      long end = Utils.end(aMention);
      // obtain the contained token annotations
      AnnotationSet tokenSet = inputAS.get("Token").getContained(start, end);
      if(tokenSet.isEmpty()) {
        toDelete.add(aMention);
        continue;
      }
      // sort the tokens
      // start and end boundaries must be in sync
      List<Annotation> orderedTokens = Utils.inDocumentOrder(tokenSet);
      if(start != Utils.start(orderedTokens.get(0)).longValue() ||
        end != Utils.end(orderedTokens.get(orderedTokens.size() - 1))
          .longValue()) {
        toDelete.add(aMention);
        continue;
      }
    }
    // delete the mentions not in sync with tokens boundaries
    for(Annotation toDelAnnot : toDelete) {
      set.remove(toDelAnnot);
    }
    // progress
    progressChanged(100);
    // let everyone who is interested know that we have now finished
    fireStatusChanged(document.getName() +
      " tagged with AlchemyAPIServicePR in " +
      NumberFormat.getInstance().format(
        (double)(System.currentTimeMillis() - startTime) / 1000) + " seconds!");
  }

  /**
   * Using URLConnection to connect to alchemy API
   * 
   * @param text
   * @return
   * @throws ExecutionException
   */
  protected List<TextSpan> process(String text) throws ExecutionException {

    if(DEBUG) System.out.println("About to process: " + text);

    // the list of results we will evenutally return
    List<TextSpan> toReturn = new ArrayList<TextSpan>();

    // the params to configure the service
    AlchemyAPI_KeywordParams params = new AlchemyAPI_KeywordParams();

    // get the XML result doc back from the service
    Document doc = null;
    try {
      doc = alchemy.TextGetRankedKeywords(text, params);
    } catch(Exception e) {
      e.printStackTrace();
      throw new ExecutionException(e);
    }

    // convert the XML into TextSpan instances
    NodeList entitiesList = doc.getElementsByTagName("keyword");
    for(int i = 0; i < entitiesList.getLength(); i++) {
      Node n = entitiesList.item(i);
      NodeList children = n.getChildNodes();
      TextSpan r = new TextSpan();

      for(int j = 0; j < children.getLength(); j++) {
        Node cn = children.item(j);
        if(cn.getNodeName().equals("text")) {
          r.text = cn.getTextContent();
        } else if(cn.getNodeName().equals("relevance")) {
          r.featureMap.put("relevance", cn.getTextContent());
        }
      }

      if(DEBUG) System.out.println(r);

      toReturn.add(r);
    }
    return toReturn;
  }

  public Integer getNumberOfSentencesInBatch() {
    return numberOfSentencesInBatch;
  }

  @CreoleParameter(comment = "Number of sentences to be sent to Alchemy API to process in one batch")
  @RunTime
  public void setNumberOfSentencesInBatch(Integer numberOfSentencesInBatch) {
    this.numberOfSentencesInBatch = numberOfSentencesInBatch;
  }

  public Integer getNumberOfSentencesInContext() {
    return numberOfSentencesInContext;
  }

  @CreoleParameter(comment = "This is the number of sentences that are used as sentences in context both on left and right side and not annotated when sent as part of a less there are no more sentences to be considered as part of the context")
  @RunTime
  public void setNumberOfSentencesInContext(Integer numberOfSentencesInContext) {
    this.numberOfSentencesInContext = numberOfSentencesInContext;
  }

  public String getAnnotationType() {
    return annotationType;
  }

  @RunTime
  @CreoleParameter(defaultValue = "Keyword")
  public void setAnnotationType(String annotationType) {
    this.annotationType = annotationType;
  }
}