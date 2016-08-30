package gate.creole.orthomatcher;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Document;
import gate.creole.ExecutionException;

import java.util.List;
import java.util.Map;
import java.util.Set;

/*
 * This interface is used so that one can create an orthography class that that
 * defines the behaviour of the Orthomatcher.
 */
public interface AnnotationOrthography {
  /**
   * Returns normalized content of an annotation - removes extra white spaces.
   *
   * @throws ExecutionException
   */
  public String getStringForAnnotation(Annotation a, gate.Document d)
      throws ExecutionException;

  public boolean fuzzyMatch(String s1, String s2);

  public boolean allNonStopTokensInOtherAnnot(List<Annotation> firstName,
      List<Annotation> secondName, String TOKEN_STRING_FEATURE_NAME,
      boolean caseSensitive);

  public String stripPersonTitle(String annotString, Annotation annot,
      Document doc, Map<Integer, List<Annotation>> tokensMap,
      Map<Integer, List<Annotation>> normalizedTokensMap, AnnotationSet nameAllAnnots)
      throws ExecutionException;

  public boolean matchedAlready(Annotation annot1, Annotation annot2,
      List<List<Integer>> matchesDocFeature, AnnotationSet nameAllAnnots);

  public Annotation updateMatches(Annotation newAnnot, String annotString,
      Map<Integer,String> processedAnnots, AnnotationSet nameAllAnnots,
      List<List<Integer>> matchesDocFeature);

  public void updateMatches(Annotation newAnnot, Annotation prevAnnot,
      List<List<Integer>> matchesDocFeature, AnnotationSet nameAllAnnots);

  public Set<String> buildTables(AnnotationSet nameAllAnnots);

  public boolean isUnknownGender(String gender);
}
