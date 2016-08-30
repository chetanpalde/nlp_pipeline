package chinese;

import gate.creole.PackagedController;
import gate.creole.metadata.AutoInstance;
import gate.creole.metadata.AutoInstanceParam;
import gate.creole.metadata.CreoleParameter;
import gate.creole.metadata.CreoleResource;

import java.net.URL;
import java.util.List;

@CreoleResource(name = "Chinese IE System", icon = "ChineseLanguage",
    comment = "Ready-made Chinese IE application",
    autoinstances = @AutoInstance(parameters = {
	@AutoInstanceParam(name="pipelineURL", value="resources/chinese.gapp"),
	@AutoInstanceParam(name="menu", value="Chinese")}))
public class ChineseIE extends PackagedController {

}
