/*
 *  ActionContext.java
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
import java.io.Serializable;

/**
 * Interface describing an "action context" for a JAPE Java RHS. An action
 * context provides access to the JAPE processing resource's feature map and
 * the corpus the JAPE PR is running on.
 *
 * @author Johann Petrak
 */
public interface ActionContext  extends Serializable {
  /**
   * Provide access to the corpus a JAPE processing resource is running on.
   * @return the corpus LR the JAPE transducer is processing, null if no
   * such corpus exists.
   */
  public Corpus getCorpus();
  /**
   * Provide access to the feature map associated with the JAPE processing
   * resource.
   * @return the FeatureMap of the processing resource
   */
  public FeatureMap getPRFeatures();
  /**
   * Provide access to the controller running the PR this action context
   * lives in.
   * @return the Controller resource
   */
  
  /**
   * Provide access to the name of the current transducer PR.
   */ 
  public String getPRName();
  
  /**
   * Returns true if the PR this transducer is running in has a chance to
   * be run at all in its controller. This can be false if the PR is set
   * to never run in a conditional controller. In such a case any controllerStarted,
   * and controllerFinished blocks of the JAPE grammer are still run when
   * the controller is starting or finishing. This method can be used in the
   * controllerStarted or controllerFinished  blocks to prevent any unwanted
   * processing if the PR is disabled.
   */
  public boolean isPREnabled();
  
  /**
   * Returns true if debugging of this transducer has been enabled.
   * @return true if debugging of this transducer has been enabled, false otherwise
   */
  public boolean isDebuggingEnabled();
  
  public Controller getController();
  /**
   * Request the current JAPE phase to be ended as soon as possible.
   * After the current RHS code has returned, the phase will be ended as soon
   * as possible if the JAPE implementation supports this feature.
   * The method returns false if this feature is not supported or if it is
   * known that ending the phase prematurely is not possible, true otherwise.
   * @return true if ending the phase prematurely is supported, false otherwise
   */
  public boolean endPhase();
}
