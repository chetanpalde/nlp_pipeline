package gate.creole.orthomatcher;



/**
 * RULE #5: if the 1st token of one name
 * matches the second name
 * e.g. "Pepsi Cola" == "Pepsi"
 * Condition(s): case-insensitive match
 * Applied to: all name annotations
 *
 * Note that we don't want to use nicknames here because you don't use nicknames for last names
 */
public class MatchRule6 implements OrthoMatcherRule {

  OrthoMatcher orthomatcher;
	
	public MatchRule6(OrthoMatcher orthmatcher){
		this.orthomatcher=orthmatcher;
	}
	
	@Override
  public boolean value(String s1, String s2) {
	  
	  boolean result=false;
	  
		   if (orthomatcher.tokensLongAnnot.size()> 1 &&
	            orthomatcher.tokensLongAnnot.get(0).getFeatures().get("kind").equals("number"))
	     result=false;
		   {
        	    //  if (s1.startsWith("Patrick") || s2.startsWith("Patrick")) {
        	    //  Out.prln("Rule 5: " + s1 + "and " + s2);
        	    //  }
        
        	    //require that when matching person names, the shorter one to be of length 1
        	    //for the rule to apply. In other words, avoid matching Peter Smith and
        	    //Peter Kline, because they share a Peter token.
        	    if ( (orthomatcher.shortAnnot.getType().equals(orthomatcher.personType)
        	            || orthomatcher.longAnnot.getType().equals(orthomatcher.personType)
        	    )
        	    &&
        	    orthomatcher.tokensShortAnnot.size()>1
        	    )
        	      result = false;
        	    else {
              	    if (orthomatcher.tokensLongAnnot.size()<=1)
              	      result = false; else 
              	    if (orthomatcher.tokensShortAnnot.get(0).getFeatures().containsKey("ortho_stop"))
              	      result = false; else
              	    
              	    {result = OrthoMatcherHelper.straightCompare((String)
              	            orthomatcher.tokensLongAnnot.get(0).getFeatures().get(OrthoMatcher.TOKEN_STRING_FEATURE_NAME),
              	            s2,
              	            orthomatcher.caseSensitive);
              	    }
        	    }
	    
		   }
		   
		  if (result && OrthoMatcher.log.isDebugEnabled()) {
		    OrthoMatcher.log.debug("rule 6 matched " + s1 + " to " + s2);
       }
	    if (result) OrthoMatcherHelper.usedRule(6);
	    
	    return result;
	}
	
  @Override
  public String getId(){
    return "MatchRule6";
  }
}
