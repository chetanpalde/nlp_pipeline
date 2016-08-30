package gate.creole.orthomatcher;


/** RULE #1: If the two names are identical then they are the same
 * no longer used, because I do the check for same string via the
 * hash table of previous annotations
 * Condition(s): depend on case
 * Applied to: annotations other than names
 */
public class MatchRule1 implements OrthoMatcherRule{

	OrthoMatcher orthomatcher;
	
	public MatchRule1(OrthoMatcher orthmatcher){
		this.orthomatcher=orthmatcher;
	}
	
	@Override
  public boolean value(String s1,
	          String s2) {
	    
	  
	    boolean retVal = OrthoMatcherHelper.straightCompare(s1, s2, orthomatcher.caseSensitive);
	    //if straight compare didn't work, try a little extra logic
	    if (!retVal)
	      retVal = orthomatcher.getOrthography().fuzzyMatch(s1, s2);

	    if (retVal && OrthoMatcher.log.isDebugEnabled()) {
	      OrthoMatcher.log.debug("rule 1 matched " + s1 + "(id: " + orthomatcher.longAnnot.getId() + ") to "
	              + s2+ "(id: " + orthomatcher.shortAnnot.getId() + ")");
	    }
	  
	    if (retVal) OrthoMatcherHelper.usedRule(1);
	    
	    return retVal;
	}
	
  @Override
  public String getId(){
    return "MatchRule1";
  }
}
