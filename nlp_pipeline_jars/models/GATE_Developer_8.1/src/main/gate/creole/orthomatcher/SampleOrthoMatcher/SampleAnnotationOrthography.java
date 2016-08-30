package gate.creole.orthomatcher.SampleOrthoMatcher;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Document;
import gate.creole.ExecutionException;
import gate.creole.orthomatcher.AnnotationOrthography;

import java.util.List;
import java.util.Map;
import java.util.Set;


/*
 * This sample orthography shows you how to create your own orthography.
 * Those methods that you do not need to change can use the code from the BasicAnnotationOrthography.
 * This sample othography copies the behavior of the default one - BasicAnnotationOrthography.
 */
public class SampleAnnotationOrthography implements gate.creole.orthomatcher.AnnotationOrthography {
    
  @SuppressWarnings("unused")
  private final String personType;
  
  private final AnnotationOrthography defaultOrthography;
  
  @SuppressWarnings("unused")
  private final boolean extLists;
  
  public SampleAnnotationOrthography(String personType, boolean extLists,
      AnnotationOrthography defaultOrthography) {
    this.personType = personType;
    this.defaultOrthography = defaultOrthography;
    this.extLists = extLists;
  }

  @Override
  public String getStringForAnnotation(Annotation a, gate.Document d)
      throws ExecutionException {

    return defaultOrthography.getStringForAnnotation(a,d);
  }
  
  @Override
  public String stripPersonTitle (String annotString, Annotation annot, Document doc, Map<Integer, List<Annotation>> tokensMap, Map<Integer, List<Annotation>> normalizedTokensMap,AnnotationSet nameAllAnnots)
    throws ExecutionException {
        return defaultOrthography.stripPersonTitle(annotString,annot,doc,tokensMap,normalizedTokensMap,nameAllAnnots);
    }
  
  @Override
  public boolean matchedAlready(Annotation annot1, Annotation annot2,List<List<Integer>> matchesDocFeature,AnnotationSet nameAllAnnots) {
        return defaultOrthography.matchedAlready(annot1,annot2,matchesDocFeature,nameAllAnnots);
    }

    @Override
    public void updateMatches(Annotation newAnnot, Annotation prevAnnot,List<List<Integer>> matchesDocFeature,AnnotationSet nameAllAnnots) {
             defaultOrthography.updateMatches(newAnnot, prevAnnot,matchesDocFeature,nameAllAnnots);
    } 
    
    @Override
    public Set<String> buildTables(AnnotationSet nameAllAnnots) {

      return defaultOrthography.buildTables(nameAllAnnots);
    }

  @Override
  public boolean allNonStopTokensInOtherAnnot(List<Annotation> arg0,
      List<Annotation> arg1, String arg2, boolean arg3) {
    
    return defaultOrthography.allNonStopTokensInOtherAnnot(arg0, arg1, arg2, arg3);
  }

  @Override
  public boolean fuzzyMatch(String arg1,
      String arg2) {
    
    return defaultOrthography.fuzzyMatch(arg1, arg2);
  }

  @Override
  public Annotation updateMatches(Annotation newAnnot, String annotString,Map<Integer, String> processedAnnots,AnnotationSet nameAllAnnots,List<List<Integer>> matchesDocFeature) {
    
    return defaultOrthography.updateMatches(newAnnot, annotString, processedAnnots,nameAllAnnots,matchesDocFeature);
  }

  @Override
  public boolean isUnknownGender(String arg0) {
    
    return defaultOrthography.isUnknownGender(arg0);
  }

}
