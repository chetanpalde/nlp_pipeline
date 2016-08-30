/*
 * UnusedPluginUnloader.java
 * 
 * Copyright (c) 1995-2014, The University of Sheffield. See the file
 * COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 * 
 * This file is part of GATE (see http://gate.ac.uk/), and is free software,
 * licenced under the GNU Library General Public License, Version 2, June 1991
 * (in the distribution as file licence.html, and also available at
 * http://gate.ac.uk/gate/licence.html).
 * 
 * Johann Petrak 2014-05-22
 */
package gate.creole;

import gate.Gate;
import gate.Gate.DirectoryInfo;
import gate.Gate.ResourceInfo;
import gate.Resource;
import gate.creole.metadata.AutoInstance;
import gate.creole.metadata.CreoleResource;
import gate.gui.ActionsPublisher;
import gate.resources.img.svg.PluginUnloaderIcon;
import gate.util.GateException;

import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;

/**
 * A tool option that will try its best to unload just the plugins for which we
 * do not have any known instances.
 * 
 * @author Johann Petrak
 * @author Mark A. Greenwood
 */
@SuppressWarnings("serial")
@CreoleResource(tool = true, isPrivate = true, autoinstances = @AutoInstance, name = "Unload Unused Plugins", helpURL = "http://gate.ac.uk/userguide/sec:misc-creole:dev-tools", comment = "Unloads all plugins for which we cannot find any loaded instances")
public class UnusedPluginUnloader extends AbstractResource implements
  ActionsPublisher {

  // the cached set of actions so we don't have to keep creating them
  private List<Action> actions;

  @Override
  public List<Action> getActions() {

    // if we have already built the action list then just return it
    if(actions != null) return actions;

    // create the empty actions list
    actions = new ArrayList<Action>();

    // we need access to the hidden instances as well and we can only do this
    // through the impl so of something weird is going on and we have a
    // different CreoleRegister than we expect then don't add the menu item
    if(!(Gate.getCreoleRegister() instanceof CreoleRegisterImpl))
      return actions;

    actions.add(new AbstractAction("Unload Unused Plugins",
      new PluginUnloaderIcon(24, 24)) {

      @Override
      public void actionPerformed(ActionEvent e) {

        // get a handle to the Creole register implementation
        CreoleRegisterImpl reg = (CreoleRegisterImpl)Gate.getCreoleRegister();

        // this will hold the set of plugins that are to be unloaded
        Set<URL> pluginsToUnload = new HashSet<URL>();

        for(URL plugin : reg.getDirectories()) {
          // for each registered plugin...

          // assume the plugin is unused
          boolean unused = true;

          // get the plugin nifo
          DirectoryInfo dInfo = Gate.getDirectoryInfo(plugin);

          for(ResourceInfo rInfo : dInfo.getResourceInfoList()) {
            // for each Resource the plugin defines...

            try {
              // get the instances of the resource
              List<Resource> loaded =
                reg.getAllInstances(rInfo.getResourceClassName(), true);

              if(!loaded.isEmpty()) {
                // if there are any instances then the plugin is in use
                unused = false;
                break;
              }
            } catch(GateException e1) {
              // ignore this, in the worst case we won't unload this plugin ...
            }
          }

          // if we went through all the Resources and there aren't instances of
          // any of them then the plugin is not in use and can be unloaded
          if(unused) pluginsToUnload.add(plugin);
        }

        //TODO replace this with a GUI to give users some control        
        if(pluginsToUnload.isEmpty()) {
          System.out.println("No plugin unloaded");
        } else {
          for(URL plugin : pluginsToUnload) {
            // The system logs plugins getting unloaded, so we do not have to do
            // it
            System.out.println("Trying to unload plugin: " + plugin);
            reg.removeDirectory(plugin);           
          }
          System.out.println("Plugins unloaded: " + pluginsToUnload.size());
          System.out.println("\nPlugins still loaded:");
          for(URL plugin : reg.getDirectories()) {
            System.out.println("  " + plugin);
          }
        }        
      }
    });

    // return the list of actions
    return actions;
  }
}
