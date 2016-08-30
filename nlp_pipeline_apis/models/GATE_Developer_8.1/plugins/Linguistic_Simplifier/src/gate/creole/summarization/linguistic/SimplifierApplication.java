/*
 * Simplifier.java
 *
 * Copyright (c) 2004-2014, The University of Sheffield.
 * 
 * This file is part of GATE (see http://gate.ac.uk/), and is free software,
 * Licensed under the GNU Library General Public License, Version 3, June 2007
 * (in the distribution as file licence.html, and also available at
 * http://gate.ac.uk/gate/licence.html).
 * 
 * Mark A. Greenwood, 2013
 */
package gate.creole.summarization.linguistic;

import gate.creole.PackagedController;
import gate.creole.metadata.AutoInstance;
import gate.creole.metadata.AutoInstanceParam;
import gate.creole.metadata.CreoleResource;

@CreoleResource(name = "Linguistic Simplifier", icon = "LinguisticSimplifier",
    comment = "Example application for the linguistic simplifier",
    autoinstances = @AutoInstance(parameters = {
  @AutoInstanceParam(name="pipelineURL", value="resources/application.xgapp"),
  @AutoInstanceParam(name="menu", value="Summarization")}))
public class SimplifierApplication extends PackagedController{
  private static final long serialVersionUID = 4737799098593521859L;
}
