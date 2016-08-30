package gate.creole.orthomatcher;


/**
 * RULE #7: if one of the tokens in one of the
 * names is in the list of separators eg. "&"
 * then check if the token before the separator
 * matches the other name
 * e.g. "R.H. Macy & Co." == "Macy"
 * Condition(s): case-sensitive match
 * Applied to: organisation annotations only
 */
public class MatchRule8 implements OrthoMatcherRule {

  OrthoMatcher orthomatcher;
	
	public MatchRule8(OrthoMatcher orthmatcher){
		this.orthomatcher=orthmatcher;
	}
	
	@Override
  public boolean value(String s1, String s2) {
		
	    boolean result=false;
	   
		  //don't try it unless the second string is just one token
	    if (orthomatcher.tokensShortAnnot.size() != 1)
	      result = false;
	    else
	    {
      	    String previous_token = null;
      
      	    for (int i = 0;  i < orthomatcher.tokensLongAnnot.size(); i++ ) {
      	      if (orthomatcher.connector.containsKey( orthomatcher.tokensLongAnnot.get(i).getFeatures().get(OrthoMatcher.TOKEN_STRING_FEATURE_NAME) )) {
      	        previous_token = (String) orthomatcher.tokensLongAnnot.get(i-1).getFeatures().get(OrthoMatcher.TOKEN_STRING_FEATURE_NAME);
      
      	        break;
      	      }
      	    }
      
      	    //now match previous_token with other name
      	    if (previous_token != null) {
      //	    if (s1.equalsIgnoreCase("chin") || s2.equalsIgnoreCase("chin"))
      //	    Out.prln("Rule7");
      	      result = OrthoMatcherHelper.straightCompare(previous_token,s2,orthomatcher.caseSensitive);
      
      	    }
	    }
	    
	    if (result) OrthoMatcherHelper.usedRule(8);
	    return result;

	}
	
  @Override
  public String getId(){
    return "MatchRule8";
  }
}
