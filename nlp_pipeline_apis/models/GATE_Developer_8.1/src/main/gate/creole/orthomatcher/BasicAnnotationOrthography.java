package gate.creole.orthomatcher;

import static gate.creole.ANNIEConstants.ANNOTATION_COREF_FEATURE_NAME;
import static gate.creole.ANNIEConstants.LOOKUP_ANNOTATION_TYPE;
import static gate.creole.orthomatcher.OrthoMatcherHelper.getStringForSpan;
import static gate.creole.orthomatcher.OrthoMatcherHelper.round2Places;
import gate.Annotation;
import gate.AnnotationSet;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.creole.ExecutionException;
import gate.util.BomStrippingInputStreamReader;
import gate.util.Err;
import gate.util.InvalidOffsetException;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

/*
 * This class defines an orthography which defines the primary behaviour of the
 * Orthomatcher processing resource in GATE.
 */
public class BasicAnnotationOrthography implements AnnotationOrthography {
  private final boolean extLists;

  private final String personType;

  private final String unknownType;

  private Map<String, Set<String>> nicknameMap =
      new HashMap<String, Set<String>>();

  private final Double minimumNicknameLikelihood;

  public BasicAnnotationOrthography(String personType, boolean extLists,
      String unknownType, URL nicknameFile, Double minimumNicknameLikelihood,
      String encoding) {
    this.personType = personType;
    this.extLists = extLists;
    this.unknownType = unknownType;
    this.minimumNicknameLikelihood = minimumNicknameLikelihood;
    try {
      if(nicknameFile != null) this.initNicknames(encoding, nicknameFile);
    } catch(IOException e) {
      log.warn("Could not load nickname map.", e);
    }
  }

  protected static final Logger log = Logger
      .getLogger(BasicAnnotationOrthography.class);

  @Override
  public String getStringForAnnotation(Annotation a, gate.Document d)
      throws ExecutionException {
    String annotString =
        getStringForSpan(a.getStartNode().getOffset(), a.getEndNode()
            .getOffset(), d);
    // now do the reg. exp. substitutions
    annotString = annotString.replaceAll("\\s+", " ");
    return annotString;
  }

  @Override
  public boolean fuzzyMatch(String s1, String s2) {
    String s1Lower = s1.toLowerCase();
    String s2Lower = s2.toLowerCase();
    if(s1Lower.equals(s2Lower)) { return true; }
    // System.out.println("Now comparing " + s1 + " | " + s2) ;
    Set<String> formalNameSet = nicknameMap.get(s1Lower);
    if(formalNameSet != null) {
      if(formalNameSet.contains(s2Lower)) { return true; }
    }
    formalNameSet = nicknameMap.get(s2Lower);
    if(formalNameSet != null) {
      if(formalNameSet.contains(s1Lower)) { return true; }
    }
    return false;
  }

  /**
   * @return true if all of the tokens in firstName are either found in second
   *         name or are stop words
   */
  @Override
  public boolean allNonStopTokensInOtherAnnot(List<Annotation> firstName,
      List<Annotation> secondName, String TOKEN_STRING_FEATURE_NAME,
      boolean caseSensitive) {
    for(Annotation a : firstName) {
      if(!a.getFeatures().containsKey("ortho_stop")) {
        String aString = (String)a.getFeatures().get(TOKEN_STRING_FEATURE_NAME);
        boolean foundAMatchInSecond = false;
        for(Annotation b : secondName) {
          if(OrthoMatcherHelper.straightCompare(aString, (String)b
              .getFeatures().get(TOKEN_STRING_FEATURE_NAME), caseSensitive)) {
            foundAMatchInSecond = true;
            break;
          }
        }
        if(!foundAMatchInSecond) { return false; }
      }
    }
    return true;
  }

  /**
   * Return a person name without a title. Also remove title from global
   * variable tokensMap
   */
  @Override
  public String stripPersonTitle(String annotString, Annotation annot,
      Document doc, Map<Integer, List<Annotation>> tokensMap,
      Map<Integer,List<Annotation>> normalizedTokensMap, AnnotationSet nameAllAnnots)
      throws ExecutionException {
    FeatureMap queryFM = Factory.newFeatureMap();
    // get the offsets
    Long startAnnot = annot.getStartNode().getOffset();
    Long endAnnot = annot.getEndNode().getOffset();
    // determine "Lookup" annotation set
    queryFM.clear();
    queryFM.put("majorType", "title");
    AnnotationSet as1 = nameAllAnnots.getContained(startAnnot, endAnnot);
    if(as1 == null || as1.isEmpty()) return annotString;
    AnnotationSet as = as1.get("Lookup", queryFM);
    if(as != null && !as.isEmpty()) {
      List<Annotation> titles = new ArrayList<Annotation>(as);
      Collections.sort(titles, new gate.util.OffsetComparator());
      Iterator<Annotation> iter = titles.iterator();
      while(iter.hasNext()) {
        Annotation titleAnn = iter.next();
        // we've not found a title at the start offset,
        // there's no point in looking further
        // coz titles come first
        if(titleAnn.getStartNode().getOffset().compareTo(startAnnot) != 0)
          return annotString;
        try {
          // the title from the current annotation
          String annotTitle =
              doc.getContent()
                  .getContent(titleAnn.getStartNode().getOffset(),
                      titleAnn.getEndNode().getOffset()).toString();
          // eliminate the title from annotation string and return the result
          if(annotTitle.length() < annotString.length()) {
            // remove from the array of tokens, so then we can compare properly
            // the remaining tokens
            // log.debug("Removing title from: " + annot + " with string " +
            // annotString);
            // log.debug("Tokens are " + tokensMap.get(annot.getId()));
            // log.debug("Title is " + annotTitle);
            tokensMap.get(annot.getId()).remove(0);
            normalizedTokensMap.get(annot.getId()).remove(0);
            return annotString.substring(annotTitle.length() + 1,
                annotString.length());
          }
        } catch(InvalidOffsetException ioe) {
          throw new ExecutionException("Invalid offset of the annotation");
        }// try
      }// while
    }// if
    return annotString;
  }

  @Override
  public boolean matchedAlready(Annotation annot1, Annotation annot2,
      List<List<Integer>> matchesDocFeature, AnnotationSet nameAllAnnots) {
    // the two annotations are already matched if the matches list of the first
    // contains the id of the second
    @SuppressWarnings("unchecked")
    List<Integer> matchesList =
        (List<Integer>)annot1.getFeatures().get(ANNOTATION_COREF_FEATURE_NAME);
    if((matchesList == null) || matchesList.isEmpty())
      return false;
    else if(matchesList.contains(annot2.getId())) return true;
    return false;
  }

  @Override
  public Annotation updateMatches(Annotation newAnnot, String annotString,
      Map<Integer, String> processedAnnots, AnnotationSet nameAllAnnots,
      List<List<Integer>> matchesDocFeature) {
    Annotation matchedAnnot = null;
    Integer id;
    // first find a processed annotation with the same string
    // TODO: Andrew Borthwick 7/26/08: The below is very inefficient. We should
    // be doing a lookup into a hash
    // which is indexed on string rather than testing every id. Need to have the
    // index be String + Type
    // for safety
    Iterator<Integer> iter = processedAnnots.keySet().iterator();
    // System.out.println("ID's examined: ");
    while(iter.hasNext()) {
      id = iter.next();
      String oldString = processedAnnots.get(id);
      // System.out.print(id + " ");
      if(annotString.equals(oldString)) {
        Annotation tempAnnot = nameAllAnnots.get(id);
        if(tempAnnot == null) {
          log.debug("Orthomatcher: TempAnnot is null when looking at "
              + annotString + " | " + oldString + " | old id: " + id);
          return null;
        }
        // Below is a new Spock addition to prevent unpredictable behavior when
        // the same string is given more than one type. We want to return null
        // if there is no match on name + type (other than Unknown)
        if(newAnnot.getType().equals(unknownType)
            || tempAnnot.getType().equals(newAnnot.getType())) {
          matchedAnnot = tempAnnot;
          break;
        }
      }
    }// while
     // System.out.println();
    if(matchedAnnot == null) return null;
    @SuppressWarnings("unchecked")
    List<Integer> matchesList =
        (List<Integer>)matchedAnnot.getFeatures().get(ANNOTATION_COREF_FEATURE_NAME);
    if((matchesList == null) || matchesList.isEmpty()) {
      // no previous matches, so need to add
      if(matchesList == null) {
        matchesList = new ArrayList<Integer>();
        matchedAnnot.getFeatures().put(ANNOTATION_COREF_FEATURE_NAME,
            matchesList);
        matchesDocFeature.add(matchesList);
      }// if
      matchesList.add(matchedAnnot.getId());
      matchesList.add(newAnnot.getId());
    } else {
      // just add the new annotation
      matchesList.add(newAnnot.getId());
    }// if
     // add the matches list to the new annotation
    newAnnot.getFeatures().put(OrthoMatcher.ANNOTATION_COREF_FEATURE_NAME,
        matchesList);
    return matchedAnnot;
  }

  @Override
  public void updateMatches(Annotation newAnnot, Annotation prevAnnot,
      List<List<Integer>> matchesDocFeature, AnnotationSet nameAllAnnots) {
    @SuppressWarnings("unchecked")
    List<Integer> matchesList =
        (List<Integer>)prevAnnot.getFeatures().get(
            OrthoMatcher.ANNOTATION_COREF_FEATURE_NAME);
    if((matchesList == null) || matchesList.isEmpty()) {
      // no previous matches, so need to add
      if(matchesList == null) {
        matchesList = new ArrayList<Integer>();
        prevAnnot.getFeatures().put(OrthoMatcher.ANNOTATION_COREF_FEATURE_NAME,
            matchesList);
        matchesDocFeature.add(matchesList);
      }// if
      matchesList.add(prevAnnot.getId());
      matchesList.add(newAnnot.getId());
    } else {
      // just add the new annotation
      matchesList.add(newAnnot.getId());
    }// if
     // add the matches list to the new annotation
    newAnnot.getFeatures().put(OrthoMatcher.ANNOTATION_COREF_FEATURE_NAME,
        matchesList);
    // propagate the gender if two persons are matched
    if(prevAnnot.getType().equals(this.personType)) {
      String prevGender =
          (String)prevAnnot.getFeatures().get(
              OrthoMatcher.PERSON_GENDER_FEATURE_NAME);
      String newGender =
          (String)newAnnot.getFeatures().get(
              OrthoMatcher.PERSON_GENDER_FEATURE_NAME);
      boolean unknownPrevGender = isUnknownGender(prevGender);
      boolean unknownNewGender = isUnknownGender(newGender);
      if(unknownPrevGender && !unknownNewGender)
        prevAnnot.getFeatures().put(OrthoMatcher.PERSON_GENDER_FEATURE_NAME,
            newGender);
      else if(unknownNewGender && !unknownPrevGender)
        newAnnot.getFeatures().put(OrthoMatcher.PERSON_GENDER_FEATURE_NAME,
            prevGender);
    }// if
  }

  /**
   * Tables for namematch info (used by the namematch rules)
   */
  @Override
  public Set<String> buildTables(AnnotationSet nameAllAnnots) {
    FeatureMap tempMap = Factory.newFeatureMap();
    // reset the tables first
    Set<String> cdg = new HashSet<String>();
    if(!extLists) {
      // i.e. get cdg from Lookup annotations
      // get all Lookup annotations
      tempMap.clear();
      tempMap.put(gate.creole.ANNIEConstants.LOOKUP_MAJOR_TYPE_FEATURE_NAME,
          "cdg");
      // now get all lookup annotations which are cdg
      AnnotationSet nameAnnots =
          nameAllAnnots.get(LOOKUP_ANNOTATION_TYPE, tempMap);
      if((nameAnnots == null) || nameAnnots.isEmpty()) return cdg;
      Iterator<Annotation> iter = nameAnnots.iterator();
      while(iter.hasNext()) {
        Annotation annot = iter.next();
        // get the actual string
        Long offsetStartAnnot = annot.getStartNode().getOffset();
        Long offsetEndAnnot = annot.getEndNode().getOffset();
        try {
          gate.Document doc = nameAllAnnots.getDocument();
          String annotString =
              doc.getContent().getContent(offsetStartAnnot, offsetEndAnnot)
                  .toString();
          cdg.add(annotString);
        } catch(InvalidOffsetException ioe) {
          ioe.printStackTrace(Err.getPrintWriter());
        }
      }// while
    }// if
    return cdg;
  }// buildTables

  @Override
  public boolean isUnknownGender(String gender) {
    if(gender == null) return true;
    if(gender.equalsIgnoreCase("male") || gender.equalsIgnoreCase("female"))
      return false;
    return true;
  } // isUnknownGender

  protected Map<String, Set<String>> initNicknames(String nicknameFileEncoding,
          java.net.URL fileURL) throws IOException {
    Pattern spacePat = Pattern.compile("(\\s+)");
    nicknameMap = new HashMap<String, Set<String>>();
    // create the relative URL
    BufferedReader reader = null;
    try {
      reader = new BomStrippingInputStreamReader(fileURL.openStream(),
              nicknameFileEncoding);
      String lineRead = null;

      while((lineRead = reader.readLine()) != null) {
        if(lineRead.length() == 0 || lineRead.charAt(0) == '#') {
          continue;
        }
        List<String> nickNameLine =
                Arrays.asList(spacePat.split(lineRead
                        .toLowerCase().trim()));
        if(nickNameLine.size() != 3
                && (nickNameLine.size() != 4 && ((nickNameLine.get(3) != "M") || nickNameLine
                        .get(3) != "F"))) {
          continue;
        }
        if(round2Places(Double.valueOf(nickNameLine.get(2))) < OrthoMatcherHelper
                .round2Places(minimumNicknameLikelihood)) {
          continue;
        }
        if(nicknameMap.containsKey(nickNameLine.get(0))) {
          /*
           * System.out.println("Adding to existing nickname of " +
           * nickNameLine.get(0) + " " + nickNameLine.get(1));
           */
          nicknameMap.get(nickNameLine.get(0)).add(nickNameLine.get(1));
        } else {
          /*
           * System.out.println("Adding new nickname of " +
           * nickNameLine.get(0) + " " + nickNameLine.get(1));
           */
          nicknameMap.put(
                  nickNameLine.get(0),
                  new HashSet<String>(
                          Collections.singleton(nickNameLine.get(1))));
        }
      }
    } finally {
      IOUtils.closeQuietly(reader);
    }
    return nicknameMap;
  }
}
