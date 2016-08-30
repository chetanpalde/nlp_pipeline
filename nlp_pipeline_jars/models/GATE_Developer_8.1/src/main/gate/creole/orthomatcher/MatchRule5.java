package gate.creole.orthomatcher;

import static gate.creole.ANNIEConstants.TOKEN_KIND_FEATURE_NAME;
import static gate.creole.ANNIEConstants.TOKEN_STRING_FEATURE_NAME;
import static gate.creole.orthomatcher.OrthoMatcher.PUNCTUATION_VALUE;
import static gate.creole.orthomatcher.OrthoMatcher.log;
import gate.Annotation;

import java.util.Iterator;
/**
 * RULE #4Name: Does all the non-punctuation tokens from the long string match the corresponding tokens 
 * in the short string?  
 * This basically identifies cases where the two strings match token for token, excluding punctuation
 * Applied to: person annotations
 *
 * Modified by Andrew Borthwick, Spock Networks:  Allowed for nickname match
 */
public class MatchRule5 implements OrthoMatcherRule {

	OrthoMatcher orthomatcher;
	
	public MatchRule5(OrthoMatcher orthmatcher){
		this.orthomatcher=orthmatcher;
	}
	
	@Override
  public boolean value(String s1, String s2) {
	  
		boolean allTokensMatch = true;
//	    if (s1.equals("wilson")) {
//	      log.debug("MR4 Name: Matching" + tokensLongAnnot + " with " + tokensShortAnnot);
//	      log.debug("MR4 Name: Matching " + s1 + " with " + s2);
//	    }  
	    if (orthomatcher.tokensLongAnnot.size() == 0 || orthomatcher.tokensShortAnnot.size() == 0) {
	      log.debug("Rule 5 rejecting " + s1 + " and " + s2 + " because one doesn't have any tokens");
	      return false;
	    }
	    Iterator<Annotation> tokensLongAnnotIter = orthomatcher.tokensLongAnnot.iterator();
	    Iterator<Annotation> tokensShortAnnotIter = orthomatcher.tokensShortAnnot.iterator();
	    while (tokensLongAnnotIter.hasNext() && tokensShortAnnotIter.hasNext()) {
	      Annotation token = tokensLongAnnotIter.next();
	      if (((String)token.getFeatures().get(TOKEN_KIND_FEATURE_NAME)).equals(PUNCTUATION_VALUE))
	        continue;
	      if (! orthomatcher.getOrthography().fuzzyMatch((String)(tokensShortAnnotIter.next().
	              getFeatures().get(TOKEN_STRING_FEATURE_NAME)),
	              (String) token.getFeatures().get(TOKEN_STRING_FEATURE_NAME))) {
	        allTokensMatch = false;
	        break;
	      }
	    }
	    if (allTokensMatch && log.isDebugEnabled()) {
	      log.debug("rule 5 matched " + s1 + "(id: " + orthomatcher.longAnnot.getId() + ", offset: " + orthomatcher.longAnnot.getStartNode().getOffset() + ") to " + 
	                                    s2+  "(id: " + orthomatcher.shortAnnot.getId() + ", offset: " + orthomatcher.shortAnnot.getStartNode().getOffset() + ")");
	    }   
	    
	    if (allTokensMatch) OrthoMatcherHelper.usedRule(5);
	    
	    return allTokensMatch;
	}
	
  @Override
  public String getId(){
    return "MatchRule5";
  }
}
