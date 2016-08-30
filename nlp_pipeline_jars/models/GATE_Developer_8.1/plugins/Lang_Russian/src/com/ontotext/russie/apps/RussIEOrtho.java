package com.ontotext.russie.apps;

import gate.creole.PackagedController;
import gate.creole.metadata.AutoInstance;
import gate.creole.metadata.AutoInstanceParam;
import gate.creole.metadata.CreoleResource;

@CreoleResource(name = "RussIE + OrthoMatcher", icon = "Russian", autoinstances = @AutoInstance(parameters = {
  @AutoInstanceParam(name = "pipelineURL", value = "resources/RussIE_ortho.xgapp"),
  @AutoInstanceParam(name = "menu", value = "Russian")}),
    comment = "RussIE application with orthomatcher",
    helpURL = "http://gate.ac.uk/userguide/sec:misc-creole:language-plugins:russian")
public class RussIEOrtho extends PackagedController {

  private static final long serialVersionUID = -2605080273474193963L;

}
