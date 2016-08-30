/*
 * HeapDumper.java
 * 
 * Copyright (c) 1995-2014, The University of Sheffield. See the file
 * COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 * 
 * This file is part of GATE (see http://gate.ac.uk/), and is free software,
 * licenced under the GNU Library General Public License, Version 2, June 1991
 * (in the distribution as file licence.html, and also available at
 * http://gate.ac.uk/gate/licence.html).
 * 
 * Mark A. Greenwood, 13/5/2014
 */
package gate.creole;

import gate.creole.metadata.AutoInstance;
import gate.creole.metadata.CreoleResource;
import gate.gui.ActionsPublisher;
import gate.gui.MainFrame;
import gate.resources.img.svg.HeapDumpIcon;
import gate.swing.XJFileChooser;
import gate.util.ExtensionFileFilter;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.management.MBeanServer;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

@SuppressWarnings("serial")
@CreoleResource(tool = true, isPrivate = true, autoinstances = @AutoInstance, name = "Java Heap Dumper", helpURL = "http://gate.ac.uk/userguide/sec:misc-creole:dev-tools", comment = "Dumps the Java heap to the specified file")
public class HeapDumper extends AbstractResource implements ActionsPublisher {

  // the cached set of actions so we don't have to keep creating them
  private List<Action> actions = null;

  // a handle to the HotSpot JVM
  private static volatile Object hotspot = null;

  // a handle to the method we'll use for doing the heap dump
  private static volatile Method dumper = null;

  static {
    synchronized(HeapDumper.class) {
      try {
        // This is complicated by the fact that the class we want might not
        // exist as it's an internal sun class so we have to do everything by
        // reflection so that the rest of the plugin loads normally even if we
        // abort this tool

        // Get the management bean server
        MBeanServer server = ManagementFactory.getPlatformMBeanServer();

        // get a handle to the HotSpot management bean
        hotspot =
          ManagementFactory.newPlatformMXBeanProxy(
            server,
            "com.sun.management:type=HotSpotDiagnostic",
            HeapDumper.class.getClassLoader().loadClass(
              "com.sun.management.HotSpotDiagnosticMXBean"));

        // get the dumpHeap method from the HotSpot bean
        dumper =
          hotspot.getClass().getMethod("dumpHeap", String.class, boolean.class);

      } catch(Exception e) {
        // for now we just swallow any problems and don't add the menu item
      }
    }
  }

  @Override
  public List<Action> getActions() {

    if(actions == null) {
      // let's build the actions list...

      // create an empty list
      actions = new ArrayList<Action>();

      if(isHotSpotAvailable()) {
        // only if the HotSpot JVM is available do we add the menu item...

        actions.add(new AbstractAction("Dump Java Heap...", new HeapDumpIcon(
          24, 24)) {

          @Override
          public void actionPerformed(ActionEvent e) {
            // show the file chooser so the user can say where to save the file
            XJFileChooser fileChooser = MainFrame.getFileChooser();
            ExtensionFileFilter filter =
              new ExtensionFileFilter("Java Heap Dump (*.hprof)", "hprof");
            
            fileChooser.resetChoosableFileFilters();
            fileChooser.setAcceptAllFileFilterUsed(true);            
            fileChooser.addChoosableFileFilter(filter);
            fileChooser.setFileFilter(filter);
            fileChooser.setMultiSelectionEnabled(false);
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setDialogTitle("Java Heap Dump Generator");

            // if the user canceled then just return
            if(fileChooser.showSaveDialog(MainFrame.getInstance()) != JFileChooser.APPROVE_OPTION)
              return;

            // get the file to dump into
            final File selectedFile = fileChooser.getSelectedFile();

            // if the file is null then something weird happened with the
            // chooser so just quit
            if(selectedFile == null) return;

            // the method we are using fails if the file already exists so try
            // deleting it and quit if we can't
            if(selectedFile.exists() && !selectedFile.delete()) {
              JOptionPane.showMessageDialog(MainFrame.getInstance(),
                "Unable to delete existing heap file",
                "Java Heap Dump Generator", JOptionPane.ERROR_MESSAGE);
            }

            // dumping the heap can take a while so we want to do it separate
            // from the EDT so we do it from a new thread
            Runnable runableAction = new Runnable() {
              @Override
              public void run() {

                // stop anyone doing anything else while we are dumping the heap
                // by locking the GUI
                MainFrame.lockGUI("Dumping Heap...");

                try {
                  // try and dump the heap
                  dumpHeap(selectedFile);
                } catch(Exception ex) {
                  ex.printStackTrace();
                } finally {
                  SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                      // unlock the GUI from the EDT
                      MainFrame.unlockGUI();
                    }
                  });
                }
              }
            };

            // run the thread we just built
            Thread thread = new Thread(runableAction, "Heap Dumper");
            thread.setPriority(Thread.MIN_PRIORITY);
            thread.start();
          }

        });
      }
    }

    return actions;
  }

  /**
   * Calling this method results in dumping the HotSpot JVM heap into the
   * specified file.
   * 
   * @param file
   *          the file in which to store the heap dump
   * @throws IOException
   *           if we are not running under the HotSpot JVM or an error occurs
   *           while storing the heap
   */
  public static void dumpHeap(File file) throws IOException {

    if(!isHotSpotAvailable())
      throw new IOException("Unable to access HotSpot to dump heap");

    try {
      dumper.invoke(hotspot, file.getAbsolutePath(), false);
    } catch(Exception e) {
      throw new IOException("Unable to dump heap", e);
    }
  }

  /**
   * Returns true if we are running under the HotSpot JVM and have managed to
   * access the appropriate management bean
   * 
   * @return true if we are running under the HotSpot JVM and have managed to
   *         access the appropriate management bean, false otherwise
   */
  public static boolean isHotSpotAvailable() {
    return dumper != null;
  }
}
