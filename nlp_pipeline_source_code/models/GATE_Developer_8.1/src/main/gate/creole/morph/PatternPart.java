package gate.creole.morph;

public class PatternPart {
	
	private String partString = null;
	private int type = ParsingFunctions.AND;
	
	public PatternPart(String string, int type) {
		this.partString = string;
		this.type = type;
	}

	public String getPartString() {
		return partString;
	}

	public void setPartString(String partString) {
		this.partString = partString;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
}
