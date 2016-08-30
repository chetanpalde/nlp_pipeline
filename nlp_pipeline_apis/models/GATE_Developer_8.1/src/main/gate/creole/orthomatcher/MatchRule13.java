package gate.creole.orthomatcher;


/**
 * RULE #12: do the first and last tokens of one name
 * match the first and last tokens of the other?
 * Condition(s): case-sensitive match
 * Applied to: person annotations only
 */
public class MatchRule13 implements OrthoMatcherRule {

    OrthoMatcher orthomatcher;
	
	public MatchRule13(OrthoMatcher orthmatcher){
			this.orthomatcher=orthmatcher;
	}
	
	@Override
  public boolean value(String s1, String s2) {
	    // first do the easy case e.g. "Pan American" == "Pan Am"

	    boolean result =false;
	  
	    if (orthomatcher.tokensLongAnnot.size()>1 && orthomatcher.tokensShortAnnot.size()>1) {
//	    Out.prln("Rule 12");

	      // get first and last tokens of s1 & s2
	      String s1_first = (String)
	      orthomatcher.tokensLongAnnot.get(0).getFeatures().get(OrthoMatcher.TOKEN_STRING_FEATURE_NAME);
	      String s2_first = (String)
	      orthomatcher.tokensShortAnnot.get(0).getFeatures().get(OrthoMatcher.TOKEN_STRING_FEATURE_NAME);

	      MatchRule1 matchRule1=new MatchRule1(orthomatcher);
	      if (!(matchRule1.value(s1_first,s2_first) || OrthoMatcherHelper.initialMatch(s1_first,s2_first)))
	        result = false;
	      else {
      	      String s1_last = (String)
      	      orthomatcher.tokensLongAnnot.get(orthomatcher.tokensLongAnnot.size()-1).getFeatures().get(OrthoMatcher.TOKEN_STRING_FEATURE_NAME);
      	      String s2_last = (String)
      	      orthomatcher.tokensShortAnnot.get(orthomatcher.tokensShortAnnot.size()-1).getFeatures().get(OrthoMatcher.TOKEN_STRING_FEATURE_NAME);
      
      	      boolean retVal =  OrthoMatcherHelper.straightCompare(s1_last,s2_last,orthomatcher.caseSensitive);
      	      if (retVal && OrthoMatcher.log.isDebugEnabled()) {
      	        OrthoMatcher.log.debug("rule 13 matched " + s1 + "(id: " + orthomatcher.longAnnot.getId() + ") to "
      	                + s2+ "(id: " + orthomatcher.shortAnnot.getId() + ")");
      	      }
      	      result = retVal;
	      }
	    } // if (tokensLongAnnot.countTokens()>1
	    
	    if (result) OrthoMatcherHelper.usedRule(13);
	    return result;
	}
	
  @Override
  public String getId(){
    return "MatchRule13";
  }
}
