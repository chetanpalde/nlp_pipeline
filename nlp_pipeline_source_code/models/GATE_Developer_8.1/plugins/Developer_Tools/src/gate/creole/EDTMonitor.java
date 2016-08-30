/*
 * EDTMonitor.java
 * 
 * Copyright (c) 1995-2014, The University of Sheffield. See the file
 * COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 * 
 * This file is part of GATE (see http://gate.ac.uk/), and is free software,
 * licenced under the GNU Library General Public License, Version 2, June 1991
 * (in the distribution as file licence.html, and also available at
 * http://gate.ac.uk/gate/licence.html).
 * 
 * Mark A. Greenwood, 3/3/2014
 */
package gate.creole;

import gate.Resource;
import gate.creole.metadata.AutoInstance;
import gate.creole.metadata.CreoleResource;

import javax.swing.RepaintManager;

import org.jdesktop.swinghelper.debug.CheckThreadViolationRepaintManager;

@CreoleResource(tool = true, isPrivate = true, autoinstances = @AutoInstance, name = "EDT Monitor", helpURL = "http://gate.ac.uk/userguide/sec:misc-creole:dev-tools", comment = "Warns whenever an AWT component is updated from anywhere other than the event dispatch thread")
public class EDTMonitor extends AbstractResource {
  private static final long serialVersionUID = -549306599972622831L;

  @Override
  public Resource init() throws ResourceInstantiationException {
    RepaintManager.setCurrentManager(new CheckThreadViolationRepaintManager());

    // These actually seems to cause things to hang sometimes so don't use it
    // until we have figured out why
    // EventDispatchThreadHangMonitor.initMonitoring();

    return this;
  }
}
