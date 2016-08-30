/*
 * Log4JALL.java
 * 
 * Copyright (c) 1995-2014, The University of Sheffield. See the file
 * COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 * 
 * This file is part of GATE (see http://gate.ac.uk/), and is free software,
 * licenced under the GNU Library General Public License, Version 2, June 1991
 * (in the distribution as file licence.html, and also available at
 * http://gate.ac.uk/gate/licence.html).
 * 
 * Mark A. Greenwood, 16/5/2014
 */
package gate.creole;

import gate.creole.metadata.AutoInstance;
import gate.creole.metadata.CreoleResource;
import gate.gui.ActionsPublisher;
import gate.resources.img.svg.Log4JALLIcon;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;

import org.apache.log4j.Appender;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

@SuppressWarnings("serial")
@CreoleResource(tool = true, isPrivate = true, autoinstances = @AutoInstance, name = "Log4J Level: ALL", helpURL = "http://gate.ac.uk/userguide/sec:misc-creole:dev-tools", comment = "Allows the Log4J log level to be set to ALL from within the GUI")
public class Log4JALL extends AbstractResource implements ActionsPublisher {

  // the cached set of actions so we don't have to keep creating them
  private List<Action> actions = null;

  @Override
  public List<Action> getActions() {

    // if we have already built the action list then just return it
    if(actions != null) return actions;

    // create the empty actions list
    actions = new ArrayList<Action>();

    actions.add(new AbstractAction("Log4J Level: ALL", new Log4JALLIcon(
      24, 24)) {

      @Override
      public void actionPerformed(ActionEvent e) {

        // for convenience get a handle on the root logger
        Logger rootLogger = Logger.getRootLogger();

        // set the log level to ALL on the top level repository
        rootLogger.getLoggerRepository().setThreshold(Level.ALL);

        @SuppressWarnings("unchecked")
        Enumeration<Logger> loggers =
          rootLogger.getLoggerRepository().getCurrentLoggers();
        while(loggers.hasMoreElements()) {
          // for each logger...
          Logger logger = loggers.nextElement();

          // if the log level is set, re-set it to ALL
          if(logger.getLevel() != null) logger.setLevel(Level.ALL);
        }

        @SuppressWarnings("unchecked")
        Enumeration<Appender> appenders = rootLogger.getAllAppenders();
        while(appenders.hasMoreElements()) {
          // for each appender...
          Appender appender = appenders.nextElement();

          if(appender instanceof AppenderSkeleton) {
            // try and set the output threshold to ALL
            ((AppenderSkeleton)appender).setThreshold(Level.ALL);
          }
        }
      }

    });

    //return the list of actions
    return actions;
  }
}
