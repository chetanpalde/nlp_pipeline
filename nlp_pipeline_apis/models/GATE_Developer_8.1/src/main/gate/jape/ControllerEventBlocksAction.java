/*
 *  ControllerEventBlocksAction.java
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
import gate.creole.ontology.Ontology;
import java.io.Serializable;

/**
 * The interface used by the action class generated for controller event blocks.
 *
 * @author Johann Petrak
 */
public interface ControllerEventBlocksAction extends Serializable {
  public void controllerExecutionStarted();
  public void controllerExecutionFinished();
  public void controllerExecutionAborted();
  public void setController(Controller c);
  public Controller getController();
  public void setThrowable(Throwable t);
  public Throwable getThrowable();
  public void setCorpus(Corpus c);
  public Corpus getCorpus();
  public void setActionContext(ActionContext ac);
  public ActionContext getActionContext();
  public void setOntology(Ontology o);
  public Ontology getOntology();
}
