/*  GazetteerListener.java
 *  Copyright (c) 1995-2012, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 *  borislav popov 08/05/2000
 *
 *  $Id: GazetteerListener.java 15333 2012-02-07 13:18:33Z ian_roberts $
 */
package gate.creole.gazetteer;

import java.util.EventListener;

/**
 * Listener for GazetteerEvents
 */
public interface GazetteerListener extends EventListener {

  /**
   * Called when a Gazetteer event has occured
   * @param e Gazetteer Event   */
  public void processGazetteerEvent(GazetteerEvent e);

}