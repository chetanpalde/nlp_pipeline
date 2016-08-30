/*
 * TheDuplicator.java
 * 
 * Copyright (c) 1995-2014, The University of Sheffield. See the file
 * COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 * 
 * This file is part of GATE (see http://gate.ac.uk/), and is free software,
 * licenced under the GNU Library General Public License, Version 2, June 1991
 * (in the distribution as file licence.html, and also available at
 * http://gate.ac.uk/gate/licence.html).
 * 
 * Mark A. Greenwood, 24/4/2014
 */
package gate.creole;

import gate.Factory;
import gate.Resource;
import gate.creole.metadata.AutoInstance;
import gate.creole.metadata.CreoleResource;
import gate.gui.MainFrame;
import gate.gui.NameBearerHandle;
import gate.gui.ResourceHelper;
import gate.resources.img.svg.TheDuplicatorIcon;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.UIManager;

@SuppressWarnings("serial")
@CreoleResource(tool = true, isPrivate = true, autoinstances = @AutoInstance, name = "The Duplicator", helpURL = "http://gate.ac.uk/userguide/sec:misc-creole:dev-tools", comment = "Duplicate any resource with a right click menu option")
public class TheDuplicator extends ResourceHelper {

  @Override
  protected List<Action> buildActions(final NameBearerHandle handle) {
    final MainFrame mf = MainFrame.getInstance();
    int height =
        mf.getFontMetrics(UIManager.getFont("MenuItem.font")).getHeight();
    List<Action> rightClick = new ArrayList<Action>();
    rightClick.add(new AbstractAction("Duplicate" , new TheDuplicatorIcon(height,height)
    ) {
      @Override
      public void actionPerformed(ActionEvent e) {
        try {
          Resource r = (Resource)handle.getTarget();
          Factory.duplicate(r);
        }
        catch (Exception ex) {
          ex.printStackTrace();
        }
      }
    });
    return rightClick;
  }
}
