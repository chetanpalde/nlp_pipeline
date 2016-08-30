package gate.creole.orthomatcher.SampleOrthoMatcher;

import gate.Resource;
import gate.creole.ResourceInstantiationException;
import gate.creole.orthomatcher.AnnotationOrthography;
import gate.creole.orthomatcher.OrthoMatcher;

/*
 * This SampleOrthoMatcher shows you how to create your own Orthomatcher.
 */
public class SampleOrthoMatcher extends OrthoMatcher {

  private static final long serialVersionUID = -1774167249206778293L;

  @Override
  public Resource init() throws ResourceInstantiationException {
      
      super.setMinimumNicknameLikelihood(0.5); // the default value from the ANNIE/creole.xml

      super.init();
      
      AnnotationOrthography ortho = new SampleAnnotationOrthography(
          this.getPersonType(), 
          this.getExtLists(), 
          this.getOrthography());
      
      super.setOrthography(ortho);
      
      return this;
  }
  
}