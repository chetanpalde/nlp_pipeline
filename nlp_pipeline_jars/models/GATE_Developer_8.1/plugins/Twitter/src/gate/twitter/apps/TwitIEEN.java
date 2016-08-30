/*
 * TwitIEEN.java
 *
 * Copyright (c) 2013, The University of Sheffield. See the file COPYRIGHT.txt
 * in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 * This file is part of GATE (see http://gate.ac.uk/), and is free software,
 * licenced under the GNU Library General Public License, Version 2, June 1991
 * (in the distribution as file licence.html, and also available at
 * http://gate.ac.uk/gate/licence.html).
 *
 * Mark A. Greenwood, 06/09/2013
 */

package gate.twitter.apps;

import gate.creole.PackagedController;
import gate.creole.metadata.AutoInstance;
import gate.creole.metadata.AutoInstanceParam;
import gate.creole.metadata.CreoleResource;

@CreoleResource(name = "TwitIE (EN)", icon = "TwitIE",
    comment = "English TwitIE application",
    autoinstances = @AutoInstance(parameters = {
	@AutoInstanceParam(name="pipelineURL", value="resources/twitie-en.xgapp"),
	@AutoInstanceParam(name="menu", value="TwitIE")}))
public class TwitIEEN extends PackagedController {

  private static final long serialVersionUID = 3419027514263819064L;

}
