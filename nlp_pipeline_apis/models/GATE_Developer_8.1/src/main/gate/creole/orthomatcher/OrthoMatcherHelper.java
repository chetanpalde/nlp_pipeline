package gate.creole.orthomatcher;

import gate.Annotation;
import gate.AnnotationSet;
import gate.creole.ExecutionException;
import gate.util.InvalidOffsetException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

public class OrthoMatcherHelper {
	
    protected static final Logger log = Logger.getLogger(OrthoMatcherHelper.class);
  
	  public static boolean straightCompare(String s1,
	          String s2,
	          boolean matchCase) {

	    boolean matched = false;
	    if (!matchCase)
	      matched = s1.equalsIgnoreCase(s2);
	    else matched =  s1.equals(s2) ;
	//  kalina: do not remove, nice for debug
	//  if (matched && (s2.startsWith("Kenneth") || s1.startsWith("Kenneth")))
	//  Out.prln("Rule1: Matched " + s1 + "and " + s2);
	    return matched;
	  }
	  
	  /**
	   * Returns true if only one of s1 and s2 is a single character and the two strings match on that
	   * initial
	   */
	  public static boolean initialMatch(String s1, String s2) {
	    return (((s1.length() == 1) ^ (s2.length() == 1) ) && (s1.charAt(0) == s2.charAt(0)));
	  }

	  /**
	   * Gets the content of an annotation
	   */
		public static String getStringForSpan(Long start, Long end,gate.Document d) throws ExecutionException {
		    try {
		      return d.getContent().getContent(start, end).toString();
		    }
		    catch (InvalidOffsetException e) {
		      //log.error("Weird offset exception in getStringForSpan", e);
		      throw new ExecutionException(e);
		    }
	  }
		 
	  public static boolean executeDisjunction(Map<Integer,OrthoMatcherRule> allrules, int[] executeRules,String longName,String shortName, boolean mr[]) {
		  
		  boolean result=false;
		  
		  for (int i = 0; i < executeRules.length; i = i + 1) {
		    
		    boolean current=allrules.get(executeRules[i]).value(longName, shortName);
		    mr[executeRules[i]]=current;
			  result=result || current;
		  }
		  
		  return result;
	  }
	  
	  public static Double round2Places(Double input) {
	    return Math.round(input*100.0)/100.0;
	  }
	  
	  /**
	   * It is used for test purposes.
	   * This table shows which rules have fired over a corpus.
	   */
	  public static boolean[] rulesUsedTable=null;
	  
	  static {
	    rulesUsedTable = new boolean[18];
      for(int i=0;i<rulesUsedTable.length;i++) rulesUsedTable[i]=false;
	  }
	  
	  /**
	   * It is used for test purposes.
	   * It sets that a specific rule has returned 'true'. 
	   */
	  public static void usedRule(int rule) {    
	    rulesUsedTable[rule]=true;
	  }
	  
	  /**
	   *  It is used for test purposes.
	   *  It saves which rules have fired(have returned 'true') while processing a corpus
	   *  Must be enabled - uncommented
	   */
	  public static void saveUsedTable() {
	     
	    //Iterator<Map.Entry<Integer, Boolean>> iter = rulesUsedTable.entrySet().iterator();
	    if (rulesUsedTable!=null) {   
	    log.debug("Saving table of used orthomatcher rules:");
	    
	    String table="";
	    
	    for(int i=0;i<rulesUsedTable.length;i++) table+="Rule: "+i+" fired: "+rulesUsedTable[i]+"\r\n";
	    
	    log.debug(table);
	    log.debug("End of table of used Orthomatcher rules"); 
	    }
	    else log.debug("Could not save the table of used orthomatcher rules. This also results when no Orthomatcher rule has returned 'true'."); 
	  }
	  
	  /*
	   * Converts a string array to an integer one.
	   */
	  public static int[] convertArrayToInteger(String[] input) {
	  
	       int[] result=new int[input.length];
	       
	       for(int i=0;i<input.length;i++) {
	          result[i] = Integer.parseInt(input[i].trim());
	       }
	       
	       return result;
	  }
	  /*
	   * It sorts a list of pairs by the first number which is the start point of an annotation.
	   * It encodes the pair in a single number, sorts by this number and then decodes to the original pair.
	   */
	  public static String SortByStartPosition(String input) {
	    
	    int ceil=100000;//a and b both must be less than ceil
	    
	    String[] pairs = input.trim().split(",");
	    
      int[] temp=new int[pairs.length];
      
      if (pairs.length>1) {
      
      int i=0;
      //encode in temp
      for(String pair: pairs){

        String[] s = pair.split(":");
        int x=Integer.parseInt(s[0].trim())* ceil + Integer.parseInt(s[1].trim());
        temp[i]=x;
        i++;
      }

      Arrays.sort(temp);
      
      //decode from temp
      String result="";
      for(int n: temp) {
        int a = n / ceil;
        int b = n % ceil;
        result=result+a+":"+b+", ";
      }
      
      return result;
      }
      else return input;//we do not need to sort a single pair
    }
	  
	  /*
	   * The feature "matches" contains annotation IDs.
	   * This method adds a new feature called "matches_positions" that tells the exact position of each match annotation from "matches".
	   * "matches" contains annotations IDs which are in general different and can not be used for comparison in tools like the Corpus Quality Assurance tool
	   * "matches_positions" can be used to check if the matches really match in for example the Corpus Quality Assurance tool
	   */
	  protected static void setMatchesPositions(AnnotationSet nameAllAnnots) {
	    
	    //get all annotations that have a matches feature
	    Set<String> fNames = new HashSet<String>();
	    fNames.add(gate.creole.ANNIEConstants.ANNOTATION_COREF_FEATURE_NAME);
	    AnnotationSet allMatchesAnnots =
	      nameAllAnnots.get(null, fNames);

	    if (allMatchesAnnots == null || allMatchesAnnots.isEmpty())
	      return;

	    for (Annotation currentMatchAnnot : allMatchesAnnots) {
	        
	        String matchValue=currentMatchAnnot.getFeatures().get(gate.creole.ANNIEConstants.ANNOTATION_COREF_FEATURE_NAME).toString();
	        
	        matchValue = matchValue.substring(1);
	        matchValue = matchValue.substring(0,matchValue.length()-1);
	        
	        String[] annotationsIDs = matchValue.split(",");

	        String matchPositionsValue="";//with the annotations positions
	        String sentinel = ", ";
	        
	        int[] integerIDs = OrthoMatcherHelper.convertArrayToInteger(annotationsIDs);
	        for (int i=0; i<integerIDs.length ; i++) {
	          
	        int id=integerIDs[i];
	        Annotation ann=null;
	        
	        Iterator<Annotation> iter = nameAllAnnots.iterator();
	        
	        //find the current annotation with ID from the match list - in order to get its start and end point
	        if (currentMatchAnnot.getId()==id)
	           ann=currentMatchAnnot; else {
	              while (iter.hasNext()) {
	                 Annotation a=iter.next();
	                 if (a.getId()==id)
	                 {
	                   ann = a;
	                   break;
	                 }
	              }
	           }
	        
	        //do the actual job of retrieving the start and end points
	        if (ann!=null) {
	          matchPositionsValue = matchPositionsValue + ann.getStartNode().getOffset()+":"+ann.getEndNode().getOffset()+sentinel;
	        }
	        
	        }//end going through the match ids
	        
	        //sort so that every time we have the "match_positions" generated the same way so that we can compare it
	        matchPositionsValue = OrthoMatcherHelper.SortByStartPosition(matchPositionsValue);
	        
	        //formating 
	        if (matchPositionsValue.endsWith(sentinel)) {
	        matchPositionsValue = matchPositionsValue.substring(0,matchPositionsValue.length()-sentinel.length());
	        }
	        matchPositionsValue = "["+matchPositionsValue+"]";
	        //finally insert the annotation
	        currentMatchAnnot.getFeatures().put("matches_positions", matchPositionsValue);
	        
	      //}
	    } //while - going through all the matches annotations(that have a feature "match") and adding the new feature
	  }//matchesPositions


}
