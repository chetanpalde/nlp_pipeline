/*
 * Copyright (c) 2009-2014, The University of Sheffield.
 * 
 * This file is part of GATE (see http://gate.ac.uk/), and is free software,
 * Licensed under the GNU Library General Public License, Version 3, June 2007
 * (in the distribution as file licence.html, and also available at
 * http://gate.ac.uk/gate/licence.html).
 */
package gate.textrazor;

import gate.AnnotationSet;
import gate.Resource;
import gate.Utils;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;
import gate.creole.metadata.CreoleParameter;
import gate.creole.metadata.CreoleResource;
import gate.creole.metadata.Optional;
import gate.creole.metadata.RunTime;
import gate.event.ProgressListener;
import gate.util.InvalidOffsetException;

import java.text.NumberFormat;
import java.util.Arrays;

import com.textrazor.AnalysisException;
import com.textrazor.NetworkException;
import com.textrazor.TextRazor;
import com.textrazor.annotations.AnalyzedText;
import com.textrazor.annotations.Entity;

/**
 * The PR uses TextRazor online service to annotate documents.
 * 
 * @author Ian Roberts
 */
@CreoleResource(name = "TextRazor Service PR",
  comment = "Runs the TextRazor annotation service (http://textrazor.com) on a GATE document",
  helpURL = "http://gate.ac.uk/userguide/sec:misc-creole:textrazor")
public class TextRazorServicePR extends gate.creole.AbstractLanguageAnalyser
  implements ProgressListener {

  private static final long serialVersionUID = 6295675573632131139L;

  /**
   * API key. One has to obtain this from TextRazor by creating an account
   * online
   */
  private String apiKey;

  /**
   * Name of the annotation set where new annotations should be created.
   */
  private String outputASName;

  /**
   * TextRazor service
   */
  private TextRazor client = null;

  /** Initialise this resource, and return it. */
  public Resource init() throws ResourceInstantiationException {
    if(getApiKey() == null || getApiKey().isEmpty()) { throw new ResourceInstantiationException(
      "Invalid API key. Please visit TextRazor web site for more information"); }
    // intiate the service
    client = new TextRazor(getApiKey());
    client.setExtractors(Arrays.asList("words", "entities"));
    client.setCleanupHTML(false);
    return this;
  }

  /* this method is called to reinitialize the resource */
  public void reInit() throws ResourceInstantiationException {
    // reinitialization code
    init();
  }

  /**
   * Should be called to execute this PR on a document.
   */
  public void execute() throws ExecutionException {
    fireStatusChanged("Checking runtime parameters");
    progressChanged(0);
    // if no document provided
    if(document == null) { throw new ExecutionException("Document is null!"); }
    // start time
    long startTime;
    try {
      // obtain the content
      String documentContent = document.getContent().toString();
      if(documentContent.trim().length() == 0) return;
      // annotation set to use
      AnnotationSet set =
        outputASName == null || outputASName.trim().length() == 0 ? document
          .getAnnotations() : document.getAnnotations(outputASName);
      startTime = System.currentTimeMillis();
      // now process the text
      // post the content to a service and obtain output
      // what we get back is the mathcing text which uri in them
      AnalyzedText result = client.analyze(documentContent);
      fireStatusChanged("Copying annotations on the document");
      
      if(result.getResponse().getEntities() == null) {
        System.out.println("No entities found");
      } else {
        for(Entity ent : result.getResponse().getEntities()) {
          set.add((long)ent.getStartingPos(), (long)ent.getEndingPos(), "TREntity", Utils.featureMap(
                  "type", ent.getType(),
                  "freebaseTypes", ent.getFreebaseTypes(),
                  "confidence", ent.getConfidenceScore(),
                  "ann_id", ent.getId(),
                  "ent_id", ent.getEntityId(),
                  "link", ent.getWikiLink()));
        }
      }
    } catch(NetworkException e) {
      throw new ExecutionException(e);
    } catch(InvalidOffsetException e) {
      throw new ExecutionException(e);
    } catch(AnalysisException e) {
      throw new ExecutionException(e);
    }
    // progress
    progressChanged(100);
    fireProcessFinished();
    // let everyone who is interested know that we have now finished
    fireStatusChanged(document.getName() +
      " tagged with TextRazorServicePR in " +
      NumberFormat.getInstance().format(
        (double)(System.currentTimeMillis() - startTime) / 1000) + " seconds!");
  }


  public String getOutputASName() {
    return outputASName;
  }

  @RunTime
  @CreoleParameter
  @Optional
  public void setOutputASName(String outputASName) {
    this.outputASName = outputASName;
  }

  /**
   * API key. One has to obtain this from TextRazor by creating an account
   * online
   */
  public String getApiKey() {
    return apiKey;
  }

  /**
   * API key. One has to obtain this from TextRazor by creating an account
   * online
   */
  @CreoleParameter(comment = "API key. One has to obtain this from TextRazor by creating an account online")
  public void setApiKey(String apiKey) {
    this.apiKey = apiKey;
  }

  @Override
  public void progressChanged(int i) {
    fireProgressChanged(i);
  }

  @Override
  public void processFinished() {
    fireProcessFinished();
  }
} // class
