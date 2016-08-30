/*
 * Reveal.java
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

import gate.CreoleRegister;
import gate.Gate;
import gate.Resource;
import gate.creole.metadata.AutoInstance;
import gate.creole.metadata.CreoleResource;
import gate.event.CreoleEvent;
import gate.gui.ActionsPublisher;
import gate.gui.MainFrame;
import gate.gui.NameBearerHandle;
import gate.gui.ResourceHelper;
import gate.resources.img.svg.RevealIcon;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.UIManager;

@SuppressWarnings("serial")
@CreoleResource(tool = true, isPrivate = true, autoinstances = @AutoInstance, name = "Show/Hide Resources", helpURL = "http://gate.ac.uk/userguide/sec:misc-creole:dev-tools", comment = "Show resources that would otherwise be hidden, e.g. resources created for internal use by other resources")
public class Reveal extends ResourceHelper implements ActionsPublisher {
  private List<Action> actions;

  @Override
  public List<Action> getActions() {
    if(actions == null) {
      actions = new ArrayList<Action>();
      actions.add(new AbstractAction("Show Hidden Resources", new RevealIcon(
          24, 24)) {
        @Override
        public void actionPerformed(ActionEvent e) {
          MainFrame mf = MainFrame.getInstance();
          CreoleRegister reg = Gate.getCreoleRegister();
          List<Resource> resources = new ArrayList<Resource>();
          resources.addAll(reg.getLrInstances());
          resources.addAll(reg.getPrInstances());
          for(Resource r : resources) {
            if(Gate.getHiddenAttribute(r.getFeatures())) {
              Gate.setHiddenAttribute(r.getFeatures(), false);
              mf.resourceLoaded(new CreoleEvent(r, CreoleEvent.RESOURCE_LOADED));
            }
          }
        }
      });
    }
    return actions;
  }

  @Override
  protected List<Action> buildActions(final NameBearerHandle handle) {
    final MainFrame mf = MainFrame.getInstance();
    int height =
        mf.getFontMetrics(UIManager.getFont("MenuItem.font")).getHeight();
    List<Action> rightClick = new ArrayList<Action>();
    rightClick.add(new AbstractAction("Hide Resource", new RevealIcon(height,
        height)) {
      @Override
      public void actionPerformed(ActionEvent e) {
        Resource r = (Resource)handle.getTarget();
        mf.resourceUnloaded(new CreoleEvent(r, CreoleEvent.RESOURCE_UNLOADED));
        Gate.setHiddenAttribute(r.getFeatures(), true);
      }
    });
    return rightClick;
  }
}
