package gate.jape.functest;

import java.io.File;

public enum TransducerType {
	//TODO: fix plug-in locations
	CLASSIC ("gate.creole.Transducer", ""),
	PDA("com.ontotext.jape.pda.Transducer", "/home/mihail/Work/Ontotext/Projects/GATE/External/Artifacts/SourceCode/japePDA"),
	PLUS("gate.jape.plus.Transducer", "../gate-futures/jplus"),
	PDAPLUS("gate.jape.plus.Transducer", "/home/mihail/Work/Ontotext/Projects/GATE/External/Artifacts/SourceCode/jpdaplus");
	
	private final String clazz;
	private final File pluginDir;
	
	TransducerType(String clazz, String pluginDir) {
		this.clazz = clazz;
		this.pluginDir = new File(pluginDir);
	}
	
	public String getFqdnClass() {
		return this.clazz;
	}
	
	public File getPlugInDir() {
		return this.pluginDir;
	}
}
