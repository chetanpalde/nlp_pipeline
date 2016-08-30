package gate.creole.orthomatcher;


/**
 * RULE #3: adding a possessive at the end
 * of one name causes a match
 * e.g. "Standard and Poor" == "Standard and Poor's"
 * and also "Standard and Poor" == "Standard's"
 * Condition(s): case-insensitive match
 * Applied to: all name annotations
 */
public class MatchRule3 implements OrthoMatcherRule {

	  OrthoMatcher orthomatcher;
		
		public MatchRule3(OrthoMatcher orthmatcher){
			this.orthomatcher=orthmatcher;
		}
	
	@Override
  public boolean value(String s1,  String s2) { //short string

	  boolean result=false;
	  
		if (s2.endsWith("'s") || s2.endsWith("'")
	            ||(s1.endsWith("'s")|| s1.endsWith("'"))) {

	      String s2_poss = null;

	      if (!s2.endsWith("'s")) s2_poss = s2.concat("'s");
	      else s2_poss = s2.concat("'");

	      if (s2_poss != null && OrthoMatcherHelper.straightCompare(s1, s2_poss,orthomatcher.caseSensitive)) {
	        if (OrthoMatcher.log.isDebugEnabled()) {
	          OrthoMatcher.log.debug("rule 3 matched " + s1 + " to " + s2);
	        }
	        result = true;
	      }

	      // now check the second case i.e. "Standard and Poor" == "Standard's"
	      String token = (String)
	      orthomatcher.tokensLongAnnot.get(0).getFeatures().get(OrthoMatcher.TOKEN_STRING_FEATURE_NAME);

	      if (!token.endsWith("'s")) s2_poss = token.concat("'s");
	      else s2_poss = token.concat("'");

	      if (s2_poss != null && OrthoMatcherHelper.straightCompare(s2_poss,s2,orthomatcher.caseSensitive)) {
	        if (OrthoMatcher.log.isDebugEnabled()){
	          OrthoMatcher.log.debug("rule 3 matched " + s1 + " to " + s2);
	        }
	        result = true;
	      }

	    } // if (s2.endsWith("'s")
		
		  if (result) OrthoMatcherHelper.usedRule(3);
		  
	    return result;
	}
	
  @Override
  public String getId(){
    return "MatchRule3";
  }
}
