package gate.termraider;

import gate.creole.PackagedController;
import gate.creole.metadata.AutoInstance;
import gate.creole.metadata.AutoInstanceParam;
import gate.creole.metadata.CreoleResource;

@CreoleResource(name = "PMI Example (English)",
    icon = "TermRaiderApp",
    comment = "Example application for the PMI (pointwise mutual information) tool",
     autoinstances = @AutoInstance(parameters = {
        @AutoInstanceParam(name="pipelineURL", value="applications/pmi-example.gapp"),
        @AutoInstanceParam(name="menu", value="TermRaider")}))
public class PMIExample extends PackagedController {
  private static final long serialVersionUID = -4725697168124226331L;
}
