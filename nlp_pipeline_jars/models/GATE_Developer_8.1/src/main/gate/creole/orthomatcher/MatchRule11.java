package gate.creole.orthomatcher;


/**
 * RULE #11: does one name consist of contractions
 * of the first two tokens of the other name?
 * e.g. "Communications Satellite" == "ComSat"
 * and "Pan American" == "Pan Am"
 * Condition(s): case-sensitive match
 * Applied to: organisation annotations only
 */
public class MatchRule11 implements OrthoMatcherRule {

	OrthoMatcher orthomatcher;
	
	public MatchRule11(OrthoMatcher orthmatcher){
			this.orthomatcher=orthmatcher;
	}
	
	@Override
  public boolean value(String s1, String s2) {
	    // first do the easy case e.g. "Pan American" == "Pan Am"
      boolean result =false;
	    
	    String token11 = null;
	    String token12 = null;
	    String token21 = null;
	    String token22 = null;

	    if (orthomatcher.tokensLongAnnot.size() < 2)
	      result = false;
	    else {
    	    // 1st get the first two tokens of s1
    	    token11 = (String)
    	    orthomatcher.tokensLongAnnot.get(0).getFeatures().get(OrthoMatcher.TOKEN_STRING_FEATURE_NAME);
    	    token12 = (String)
    	    orthomatcher.tokensLongAnnot.get(1).getFeatures().get(OrthoMatcher.TOKEN_STRING_FEATURE_NAME);
    
    	    // now check for the first case i.e. "Pan American" == "Pan Am"
    	    if (orthomatcher.tokensShortAnnot.size() == 2)  {
    
    	      token21 = (String)
    	      orthomatcher.tokensShortAnnot.get(0).getFeatures().get(OrthoMatcher.TOKEN_STRING_FEATURE_NAME);
    	      token22 = (String)
    	      orthomatcher.tokensShortAnnot.get(0).getFeatures().get(OrthoMatcher.TOKEN_STRING_FEATURE_NAME);
    
    	      if (token11.startsWith(token21)
    	              && token12.startsWith(token22))
    	        result = true;
    
    	    } // if (tokensShortAnnot.countTokens() == 2)
    
    	    // now the second case e.g.  "Communications Satellite" == "ComSat"
    	    else if (orthomatcher.tokensShortAnnot.size()==1 && s2.length()>=3) {
    
    	      // split the token into possible contractions
    	      // ignore case for matching
    	      for (int i=2;i<s2.length();i++) {
    	        token21=s2.substring(0,i+1);
    	        token22=s2.substring(i+1);
    
    	        if (token11.startsWith(token21)
    	                && token12.startsWith(token22))
    	          result = true;
    	      }// for
    	    } // else if
	    }

	    if (result) OrthoMatcherHelper.usedRule(11);
	    return result;
	}
	
  @Override
  public String getId(){
    return "MatchRule11";
  }
}
