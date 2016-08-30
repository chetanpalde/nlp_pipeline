package gate.creole.orthomatcher;

import gate.Annotation;

import java.util.Iterator;

import static gate.creole.orthomatcher.OrthoMatcher.*;

/**
 * RULE #4: Does the first non-punctuation token from the long string match
 * the first token from the short string?
 * e.g. "fred jones" == "fred"
 * Condition(s): case-insensitive match
 * Applied to: person annotations
 *
 * Modified by Andrew Borthwick, Spock Networks:  Disallow stop words
 */
public class MatchRule4 implements OrthoMatcherRule {
	
	OrthoMatcher orthomatcher;
	
	public MatchRule4(OrthoMatcher orthmatcher){
		this.orthomatcher=orthmatcher;
	}
	
	@Override
  public boolean value(String s1, String s2) {

		boolean allTokensMatch = true;
	    // Out.prln("MR4:  Matching" + s1 + " with " + s2);

	    Iterator<Annotation> tokensLongAnnotIter = orthomatcher.tokensLongAnnot.iterator();
	    Iterator<Annotation> tokensShortAnnotIter = orthomatcher.tokensShortAnnot.iterator();
	    while (tokensLongAnnotIter.hasNext() && tokensShortAnnotIter.hasNext()) {
	      Annotation token = tokensLongAnnotIter.next();
	      if (((String)token.getFeatures().get(TOKEN_KIND_FEATURE_NAME)).equals(PUNCTUATION_VALUE) ||
	              token.getFeatures().containsKey("ortho_stop"))
	        continue;
	      if (! ((String)(tokensShortAnnotIter.next().
	              getFeatures().get(TOKEN_STRING_FEATURE_NAME))).equals(
	                      token.getFeatures().get(TOKEN_STRING_FEATURE_NAME))) {
	        allTokensMatch = false;
	        break;
	      } // if (!tokensLongAnnot.nextToken()
	    } // while
	//  if (allTokensMatch)
	//  Out.prln("rule4 fired. result is: " + allTokensMatch);
	     if (allTokensMatch && log.isDebugEnabled()) {
	       log.debug("rule 4 matched " + s1 + "(id: " + orthomatcher.longAnnot.getId() + ") to " + s2+ "(id: " + orthomatcher.shortAnnot.getId() + ")");
	     }
	     
	    if (allTokensMatch) OrthoMatcherHelper.usedRule(4);
	    
	    return allTokensMatch;
	}
	
  @Override
  public String getId(){
    return "MatchRule4";
  }
}
