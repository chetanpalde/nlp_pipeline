package gate.creole.morph;

public class RHS {
	private short methodIndex;
	private String[] parameters;
	private boolean verb = false;
	private boolean noun = false;
	private short patternIndex = 0; 
	
	public RHS(String function, String category, short patternIndex) {
		methodIndex = ParsingFunctions.getMethodIndex(function.trim());
		parameters = ParsingFunctions.getParameterValues(function.trim());
		if(category.equals("verb"))
			verb = true;
		else if(category.equals("noun"))
			noun = true;
		else if(category.equals("*")) {
			verb = true;
			noun = true;
		}
		this.patternIndex = patternIndex;
	}

	public short getMethodIndex() {
		return methodIndex;
	}

	public String[] getParameters() {
		return parameters;
	}

	public void setParameters(String[] parameters) {
		this.parameters = parameters;
	}

	public boolean isNoun() {
		return noun;
	}

	public boolean isVerb() {
		return verb;
	}

	public short getPatternIndex() {
		return patternIndex;
	}
}
