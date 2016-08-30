package gate.creole.orthomatcher;

import gate.Annotation;

/**
 * RULE #15: Does every token in the shorter name appear in the longer name?
 */
public class MatchRule16 implements OrthoMatcherRule {

    OrthoMatcher orthmatcher;
	
	public MatchRule16(OrthoMatcher orthomatcher){
			this.orthmatcher=orthomatcher;
	}
	
	@Override
  public boolean value(String s1, String s2) {
	
	  boolean result =true;
	  
		//do a token-by-token test
	    Annotation token1, token2;
	    // catch (ExecutionException e) {}
	    for (int i=0; i < orthmatcher.tokensShortAnnot.size(); i++) {
	      token1 = orthmatcher.tokensShortAnnot.get(i);
	      //first check if not punctuation, because we need to skip it
	      if (token1.getFeatures().get(OrthoMatcher.TOKEN_KIND_FEATURE_NAME).equals(OrthoMatcher.PUNCTUATION_VALUE))
	        continue;

	      String ts1 = (String)token1.getFeatures().get(OrthoMatcher.TOKEN_STRING_FEATURE_NAME);
	      boolean foundMatch = false;
	      for (int j=0; j<orthmatcher.tokensLongAnnot.size(); j++) {
	        // Out.prln("i = " + i);
	        token2 = orthmatcher.tokensLongAnnot.get(j);
	        if (token2.getFeatures().get(OrthoMatcher.TOKEN_KIND_FEATURE_NAME).equals(OrthoMatcher.PUNCTUATION_VALUE))
	          continue;

	        String ts2 = (String)token2.getFeatures().get(OrthoMatcher.TOKEN_STRING_FEATURE_NAME);

	        if (i == 0 && j == 0) {
	          foundMatch = orthmatcher.getOrthography().fuzzyMatch(ts1, ts2);
	        }
	        else {
	          if (orthmatcher.caseSensitive) {
	            if (ts2.equals(ts1)) {
	              foundMatch = true;
	              break;
	            }
	          }
	          else {
	            if (ts2.equalsIgnoreCase(ts1)) {
	              foundMatch = true;
	              break;
	            }
	          }
	        }
	      }//for
	      //if no match for the current tokenShortAnnot, then it is not a coref of the
	      //longer annot
	      if (!foundMatch)
	        result = false;
	    } // for

	    //only get to here if all word tokens in the short annot were found in
	    //the long annot, so there is a coref relation
	    if (OrthoMatcher.log.isDebugEnabled())
	      OrthoMatcher.log.debug("rule 16 matched " + s1 + " to " + s2);
	    
	    if(result) OrthoMatcherHelper.usedRule(16);
	    return result;
	}
	
  @Override
  public String getId(){
    return "MatchRule16";
  }
}
