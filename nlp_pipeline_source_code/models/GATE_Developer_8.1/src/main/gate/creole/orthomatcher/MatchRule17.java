package gate.creole.orthomatcher;

import gate.creole.ANNIEConstants;

/**
 * RULE #16: Conservative match rule
 * Require every token in one name to match the other except for tokens that are on a stop word list
 */
public class MatchRule17 implements OrthoMatcherRule {

    OrthoMatcher orthomatcher;
	
	public MatchRule17(OrthoMatcher orthmatcher){
			this.orthomatcher=orthmatcher;
	}
	
	@Override
  public boolean value(String s1, String s2) {
	  
	  boolean result=false;
	  OrthoMatcherHelper.usedRule(17);
	  
		//reversed execution of allNonStopTokensInOtherAnnot
		if (orthomatcher.getOrthography().allNonStopTokensInOtherAnnot(orthomatcher.tokensLongAnnot, orthomatcher.tokensShortAnnot,ANNIEConstants.TOKEN_STRING_FEATURE_NAME,orthomatcher.caseSensitive)) {
		      result = orthomatcher.getOrthography().allNonStopTokensInOtherAnnot(orthomatcher.tokensShortAnnot, orthomatcher.tokensLongAnnot,ANNIEConstants.TOKEN_STRING_FEATURE_NAME,orthomatcher.caseSensitive);
		    }
		
		return result;
	}
	
  @Override
  public String getId(){
    return "MatchRule17";
  }
}
