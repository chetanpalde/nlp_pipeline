/*
	PrintOutTokens.java 

	Hamish Cunningham, 10/Nov/2000

	$Id: PrintOutTokens.java 690 2000-11-15 18:09:23Z hamish $
*/

package testpkg;

import java.util.*;

import gate.*;
import gate.util.*;
import gate.creole.*;


/** A simple ProcessingResource for testing purposes.
  */
public class PrintOutTokens extends
AbstractProcessingResource implements ProcessingResource
{
  /** Default Construction */
  public PrintOutTokens() {
    this(null);
  } // Default Construction

  /** Construction from name and features */
  public PrintOutTokens(FeatureMap features) {
    this.features = features;
  } // Construction from name and features

  /** Get the features associated with this corpus. */
  public FeatureMap getFeatures() { return features; }

  /** Set the feature set */
  public void setFeatures(FeatureMap features) { this.features = features; } 

  /** The features associated with this resource. */
  protected FeatureMap features;

  /** The document to run on */
  protected Document document;

  /** Set the document we run on */
  public void setDocument(Document document) { this.document = document; }

  /** Run the thing. */
  public void run() {
  } // run

  /** Initialisation */
  public Resource init() {
    return this;
  } // init
   
} // class PrintOutTokens
