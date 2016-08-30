package gate.creole.orthomatcher;


/**
 * RULE #14: if the last token of one name
 * matches the second name
 * e.g. "Hamish Cunningham" == "Cunningham"
 * Condition(s): case-insensitive match
 * Applied to: all person annotations
 *
 * Don't need to nicknames here
 */
public class MatchRule15 implements OrthoMatcherRule {

  OrthoMatcher orthomatcher;
	
	public MatchRule15(OrthoMatcher orthmatcher){
			this.orthomatcher=orthmatcher;
	}
	
	@Override
  public boolean value(String s1, String s2) {
		
	  boolean result=false;
	  
	//  if (s1.equalsIgnoreCase("chin") || s2.equalsIgnoreCase("chin"))
	//  Out.prln("Rule 14 " + s1 + " and " + s2);
	    String s1_short = (String)
	    orthomatcher.tokensLongAnnot.get(
	    		orthomatcher.tokensLongAnnot.size()-1).getFeatures().get(OrthoMatcher.TOKEN_STRING_FEATURE_NAME);
	//  Out.prln("Converted to " + s1_short);
	    if (orthomatcher.tokensLongAnnot.size()>1 && OrthoMatcherHelper.straightCompare(s1_short, s2,orthomatcher.caseSensitive)) {
	     if (OrthoMatcher.log.isDebugEnabled()) {
	       OrthoMatcher.log.debug("rule 15 matched " + s1 + "(id: " + orthomatcher.longAnnot.getId() + ") to "  + s2 
	                + "(id: " + orthomatcher.shortAnnot.getId() + ")");
	     }
	      result = true;
	    }

	    if (result) OrthoMatcherHelper.usedRule(15);
	    return result;
	}
	
  @Override
  public String getId(){
    return "MatchRule15";
  }
}
