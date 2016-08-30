/*
  TestPR2.java

  Hamish Cunningham, 4/Sept/2000

  $Id: TestPR2.java 1961 2001-09-26 18:23:13Z valyt $
*/

package testpkg;

import java.util.*;

import gate.*;
import gate.util.*;
import gate.creole.*;


/** A simple ProcessingResource for testing purposes.
  */
public class TestPR2 extends AbstractProcessingResource
implements ProcessingResource
{

  /** Default Construction */
  public TestPR2() {
    this(null);
  } // Default Construction

  /** Construction from name and features */
  public TestPR2(FeatureMap features) {
    this.features = features;
  } // Construction from name and features

  /** Get the features associated with this corpus. */
  public FeatureMap getFeatures() { return features; }

  /** Set the feature set */
  public void setFeatures(FeatureMap features) { this.features = features; }

  /** The features associated with this resource. */
  protected FeatureMap features;

  /** Run the thing. */
  public void execute() {
    features = Factory.newFeatureMap();
    features.put("I", "am in a bad mood");
  } // run

  /** Initialisation */
  public Resource init() {
    return this;
  } // init

} // class TestPR2
