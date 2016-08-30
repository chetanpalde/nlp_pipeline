/*
 * Copyright (c) 2009-2013, The University of Sheffield.
 * 
 * This file is part of GATE (see http://gate.ac.uk/), and is free software,
 * Licensed under the GNU Library General Public License, Version 3, June 2007
 * (in the distribution as file licence.html, and also available at
 * http://gate.ac.uk/gate/licence.html).
 */

package gate.alchemyAPI;

import com.alchemyapi.api.AlchemyAPI;

import gate.Resource;
import gate.creole.AbstractLanguageAnalyser;
import gate.creole.ResourceInstantiationException;
import gate.creole.metadata.CreoleParameter;
import gate.creole.metadata.Optional;
import gate.creole.metadata.RunTime;
import gate.event.ProgressListener;

/**
 * An abstract PR that handles the basic AlchemyAPI stuff like the API key and
 * input/output annotation sets etc.
 * 
 * @author Mark A. Greenwood
 */
public class AbstractAlchemyPR extends AbstractLanguageAnalyser implements
  ProgressListener {

  private static final long serialVersionUID = 8753507425069804316L;

  /**
   * developer key. One has to obtain this from AlchemyAPI by creating an
   * account online
   */
  protected String apiKey;

  /**
   * The PR requires Sentence annotations as input. This parameter tells PR
   * where it can find the Sentence annotations
   */
  protected String inputASName;

  /**
   * Name of the annotation set where new annotations should be created.
   */
  protected String outputASName;

  /** an AlchemyAPI object */
  protected AlchemyAPI alchemy = null;

  /**
   * developer key. One has to obtain this from Alchemy API by creating an
   * account online
   */
  public String getApiKey() {
    return apiKey;
  }

  /**
   * developer key. One has to obtain this from Alchemy API by creating an
   * account online
   */
  @CreoleParameter(comment = "developer key. One has to obtain this from Alchemy API by creating an account online")
  public void setApiKey(String apiKey) {
    this.apiKey = apiKey;
  }

  /**
   * The PR requires Sentence annotations as input. This parameter tells PR
   * where it can find the Sentence annotations
   */
  public String getInputASName() {
    return inputASName;
  }

  /**
   * The PR requires Sentence annotations as input. This parameter tells PR
   * where it can find the Sentence annotations
   */
  @CreoleParameter
  @Optional
  @RunTime
  public void setInputASName(String inputASName) {
    this.inputASName = inputASName;
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

  /** Initialise this resource, and return it. */
  public Resource init() throws ResourceInstantiationException {
    
    // check that we have a key (although no guarantee it is valid)
    if(getApiKey() == null || getApiKey().isEmpty()) { throw new ResourceInstantiationException(
      "Invalid API key. Please visit http://www.alchemyapi.com for more information"); }

    // intiate the AlchemyAPI service
    alchemy = AlchemyAPI.GetInstanceFromString(getApiKey());
    
    // return the now initialized resource
    return this;
  }

  @Override
  public void progressChanged(int i) {
    fireProgressChanged(i);
  }

  @Override
  public void processFinished() {
    fireProcessFinished();
  }

}
