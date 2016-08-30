/*
 *  DefaultActionContext.java
 *
 *  Copyright (c) 1995-2012, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 *  $Id: $
 *
 */

package gate.jape;

import gate.Controller;
import gate.Corpus;
import gate.FeatureMap;
import gate.ProcessingResource;

/**
 * Default implementation for an action context.<br>
 * Note: A JAPE RHS should only ever use the methods defined in
 * the ActionContext interface, the additional methods implemented here
 * are for use by the Transducer only.
 * 
 * @author Johann Petrak
 */
public class DefaultActionContext implements ActionContext {

  private static final long serialVersionUID = -6337282565397213344L;

  protected Corpus corpus;
  protected FeatureMap prfeatures;
  protected String prname;
  protected Controller controller;
  protected boolean phaseEnded = false;
  protected ProcessingResource pr;
  protected boolean debug = false;

  public DefaultActionContext() {}

  public void setCorpus(Corpus corpus) {
    this.corpus = corpus;
  }
  public void setPRFeatures(FeatureMap features) {
    this.prfeatures = features;
  }

  public void setPRName(String name) {
    this.prname = name;
  }
  
  public void setPR(ProcessingResource pr) {
    this.pr = pr;
  }
  
  @Override
  public Corpus getCorpus() {
    return corpus;
  }

  @Override
  public FeatureMap getPRFeatures() {
    return prfeatures;
  }
  
  @Override
  public String getPRName() {
    return prname;
  }

  public void setController(Controller c) {
    controller = c;
  }

  @Override
  public Controller getController() {
    return controller;
  }
  
  @Override
  public boolean isDebuggingEnabled() {
    return debug;
  }
  
  public void setDebuggingEnabled(boolean debug) {
    this.debug = debug;
  }

  @Override
  public boolean endPhase() {
    phaseEnded = true;
    return true;
  }


  public boolean isPhaseEnded() {
    return phaseEnded;
  }

  public void setPhaseEnded(boolean isended) {
    phaseEnded = isended;
  }
  
  @Override
  public boolean isPREnabled() {
    return gate.Utils.isEnabled(controller, pr);
  }

}
