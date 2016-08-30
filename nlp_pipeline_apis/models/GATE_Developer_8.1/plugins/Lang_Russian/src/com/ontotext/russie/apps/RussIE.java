package com.ontotext.russie.apps;

import gate.creole.PackagedController;
import gate.creole.metadata.AutoInstance;
import gate.creole.metadata.AutoInstanceParam;
import gate.creole.metadata.CreoleResource;

@CreoleResource(name = "RussIE", icon = "Russian", autoinstances = @AutoInstance(parameters = {
	@AutoInstanceParam(name="pipelineURL", value="resources/RussIE.xgapp"),
	@AutoInstanceParam(name="menu", value="Russian")}),
    comment = "Basic version of the RussIE application",
    helpURL = "http://gate.ac.uk/userguide/sec:misc-creole:language-plugins:russian")
public class RussIE extends PackagedController {

  private static final long serialVersionUID = 271518814092944109L;

}
