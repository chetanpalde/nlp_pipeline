/*
 *  CebuanoGazetteer.java
 *
 *  Copyright (c) 1995-2014, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 */

package cebuano;

import java.net.URL;

import gate.creole.gazetteer.DefaultGazetteer;
import gate.creole.metadata.CreoleParameter;
import gate.creole.metadata.CreoleResource;

@CreoleResource(name="Cebuano Gazetteer")
public class CebuanoGazetteer extends DefaultGazetteer {

  private static final long serialVersionUID = 6056825116054902597L;

  @CreoleParameter(defaultValue="resources/gazetteer/cebuano/lists.def")
  public void setListsURL(URL url) {
    super.setListsURL(url);
  }
}
