package gate.creole.orthomatcher;

public interface OrthoMatcherRule {
	
	  String getId();
	  boolean value(String s1,String s2);
	  String description=""; 
}
