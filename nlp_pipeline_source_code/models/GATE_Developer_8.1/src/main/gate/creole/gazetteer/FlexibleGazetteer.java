/*
 * FlexibleGazetteer.java
 * 
 * Copyright (c) 2004-2012, The University of Sheffield.
 * 
 * This file is part of GATE (see http://gate.ac.uk/), and is free software,
 * licenced under the GNU Library General Public License, Version 2, June1991.
 * 
 * A copy of this licence is included in the distribution in the file
 * licence.html, and is also available at http://gate.ac.uk/gate/licence.html.
 * 
 * Niraj Aswani 02/2002
 * $Id: FlexibleGazetteer.java 17530 2014-03-04 15:57:43Z markagreenwood $
 */
package gate.creole.gazetteer;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.Gate;
import gate.ProcessingResource;
import gate.Resource;
import gate.Utils;
import gate.corpora.DocumentImpl;
import gate.creole.AbstractLanguageAnalyser;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;
import gate.util.InvalidOffsetException;
import java.util.List;


/**
 * <p>
 * Title: Flexible Gazetteer
 * </p>
 * <p>
 * The Flexible Gazetteer provides users with the flexibility to choose
 * their own customised input and an external Gazetteer. For example,
 * the user might want to replace words in the text with their base
 * forms (which is an output of the Morphological Analyser).
 * </p>
 * <p>
 * The Flexible Gazetteer performs lookup over a document based on the
 * values of an arbitrary feature of an arbitrary annotation type, by
 * using an externally provided gazetteer. It is important to use an
 * external gazetteer as this allows the use of any type of gazetteer
 * (e.g. an Ontological gazetteer).
 * </p>
 * 
 * @author niraj aswani
 * @version 1.0
 */
public class FlexibleGazetteer extends AbstractLanguageAnalyser 
  implements ProcessingResource {
  
  private static final long serialVersionUID = -1023682327651886920L;
  private static final String wrappedOutputASName = "Output";
  private static final String wrappedInputASName = "Input";
  
  // SET TO false BEFORE CHECKING IN
  private static final boolean DEBUG = false;

  /**
   * Does the actual loading and parsing of the lists. This method must be
   * called before the gazetteer can be used
   */
  @Override
  public Resource init() throws ResourceInstantiationException {
    if(gazetteerInst == null) { throw new ResourceInstantiationException(
        "No Gazetteer Provided!"); }
    return this;
  }

  /**
   * This method runs the gazetteer. It assumes that all the needed parameters
   * are set. If they are not, an exception will be fired.
   */
  @Override
  public void execute() throws ExecutionException {
    fireProgressChanged(0);
    fireStatusChanged("Checking Document...");
    if(document == null) { throw new ExecutionException(
        "No document to process!"); }
    // obtain the inputAS
    AnnotationSet inputAS = document.getAnnotations(inputASName);
    // anything in the inputFeatureNames?
    if(inputFeatureNames == null || inputFeatureNames.size() == 0) { throw new ExecutionException(
        "No input feature names provided!"); }
    // for each input feature, create a temporary document and run the
    // gazetteer
    for(String aFeature : inputFeatureNames) {
      // find out the feature name user wants us to use
      String[] keyVal = aFeature.split("\\.");
      // if invalid feature name
      if(keyVal.length != 2) {
        System.err.println("Invalid input feature name:" + aFeature);
        continue;
      }
      // keyVal[0] = annotation type
      // keyVal[1] = feature name
      // holds mapping for newly created annotations
      FlexGazMappingTable mappingTable = new FlexGazMappingTable();
      fireStatusChanged("Creating temporary Document for feature " + aFeature);
      StringBuilder newdocString =
          new StringBuilder(document.getContent().toString());
      // sort annotations
      List<Annotation> annotations =
          Utils.inDocumentOrder(inputAS.get(keyVal[0]));

      // remove duplicate annotations
      // (this makes the reverse mapping much easier)
      removeOverlappingAnnotations(annotations);
      // initially no space is deducted
      int totalDeductedSpaces = 0;
      // now replace the document content with the value of the feature that
      // user has provided
      for(Annotation currentAnnotation : annotations) {
        // if there's no such feature, continue
        if(!currentAnnotation.getFeatures().containsKey(keyVal[1])) continue;
        String newTokenValue =
            currentAnnotation.getFeatures().get(keyVal[1]).toString();
        // if no value found for this feature
        if(newTokenValue == null) continue;
        // feature value found so we need to replace it
        // find the start and end offsets for this token
        long startOffset = Utils.start(currentAnnotation);
        long endOffset = Utils.end(currentAnnotation);
        // let us find the difference between the lengths of the
        // actual string and the newTokenValue
        long actualLength = endOffset - startOffset;
        long lengthDifference = actualLength - newTokenValue.length();
        // so lets find out the new startOffset and endOffset
        long newStartOffset = startOffset - totalDeductedSpaces;
        long newEndOffset = newStartOffset + newTokenValue.length();
        totalDeductedSpaces += lengthDifference;

        mappingTable.add(startOffset, endOffset, newStartOffset, newEndOffset);
        
        // and finally replace the actual string in the document
        // with the new document
        newdocString.replace((int)newStartOffset, (int)newStartOffset
            + (int)actualLength, newTokenValue);
      }

      // proceed only if there was any replacement Map
      if(mappingTable.isEmpty()) continue;
      
      /* All the binary search stuff is done inside FlexGazMappingTable
       * now, so it's guaranteed to return valid original annotation start
       * and end offsets.       */
      
      // otherwise create a temporary document for the new text
      Document tempDoc = null;
      // update the status
      fireStatusChanged("Processing document with Gazetteer...");
      try {
        FeatureMap params = Factory.newFeatureMap();
        params.put("stringContent", newdocString.toString());
        // set the appropriate encoding
        if(document instanceof DocumentImpl) {
          params.put("encoding", ((DocumentImpl)document).getEncoding());
          params.put("markupAware", ((DocumentImpl)document).getMarkupAware());
        }
        FeatureMap features = Factory.newFeatureMap();
        Gate.setHiddenAttribute(features, true);
        tempDoc =
            (Document)Factory.createResource("gate.corpora.DocumentImpl",
                params, features);

        /* Mark the temp document with the locations of the input annotations so
         * that we can later eliminate Lookups that are out of scope.       */
        for (NodePosition mapping : mappingTable.getMappings()) {
          tempDoc.getAnnotations(wrappedInputASName).add(mapping.getTempStartOffset(), 
              mapping.getTempEndOffset(), "Input", Factory.newFeatureMap());
        }
      } 
      catch(ResourceInstantiationException rie) {
        throw new ExecutionException("Temporary document cannot be created", rie);
      } 
      catch(InvalidOffsetException e) {
        throw new ExecutionException("Error duplicating Input annotations", e);
      }
      try {
        // lets create the gazetteer based on the provided gazetteer name
        gazetteerInst.setDocument(tempDoc);
        gazetteerInst.setAnnotationSetName(wrappedOutputASName);
        fireStatusChanged("Executing Gazetteer...");
        gazetteerInst.execute();
        // now the tempDoc has been looked up, we need to shift the annotations
        // from this temp document to the original document
        fireStatusChanged("Transfering new annotations to the original one...");
        AnnotationSet originalDocOutput = document.getAnnotations(outputASName);
        
        if (DEBUG) {
          mappingTable.dump();
        }
        
        // Now iterate over the new annotations and transfer them from the 
        // temp document back to the real one
        for(Annotation currentLookup : tempDoc.getAnnotations(wrappedOutputASName)) {
          long tempStartOffset = Utils.start(currentLookup);
          long tempEndOffset = Utils.end(currentLookup);

          /* Ignore annotations that fall entirely outside the input annotations,
           * so that we don't get dodgy Lookups outside the area covered by
           * Tokens copied into a restricted working set by the AST PR
           * (for example)           */
          if (coveredByInput(tempStartOffset, tempEndOffset, tempDoc.getAnnotations(wrappedInputASName)))  {
            long destinationStart = mappingTable.getBestOriginalStart(tempStartOffset);
            long destinationEnd = mappingTable.getBestOriginalEnd(tempEndOffset);

            boolean valid = (destinationStart >= 0) && (destinationEnd >= 0);  

            if (valid) {
              // Now make sure there is no other annotation like this
              AnnotationSet testSet = originalDocOutput.getContained(destinationStart, destinationEnd).get(
                  currentLookup.getType(), currentLookup.getFeatures());
              for(Annotation annot : testSet) {
                if(Utils.start(annot) == destinationStart
                    && Utils.end(annot) == destinationEnd
                    && annot.getFeatures().size() == currentLookup.getFeatures().size()) {
                  valid = false;
                  break;
                }
              }
            }
            
            if(valid) {
              addToOriginal(originalDocOutput, destinationStart, destinationEnd, 
                  tempStartOffset, tempEndOffset, currentLookup, tempDoc);
            }
          } // END if coveredByInput(...)
        } // END for OVER ALL THE Lookups
      } 
      finally {
        gazetteerInst.setDocument(null);
        if(tempDoc != null) {
          // now remove the newDoc
          Factory.deleteResource(tempDoc);
        }
      }
    } // for
    fireProcessFinished();
  } // END execute METHOD

  
  /**
   * Removes the overlapping annotations. preserves the one that appears first
   * in the list.  This assumes the list has been sorted already.
   * 
   * @param annotations
   */
  private void removeOverlappingAnnotations(List<Annotation> annotations) {
    for(int i = 0; i < annotations.size() - 1; i++) {
      Annotation annot1 = annotations.get(i);
      Annotation annot2 = annotations.get(i + 1);
      long annot2Start = Utils.start(annot2);
      if(annot2Start >= Utils.start(annot1) && annot2Start < Utils.end(annot1)) {
        annotations.remove(annot2);
        i--;
        continue;
      }
    }
  }

  
  /* We try hard not to cause InvalidOffsetExceptions, but let's have
   * some better debugging info in case they happen.
   */
  private void addToOriginal(AnnotationSet original, long originalStart, long originalEnd, 
      long tempStart, long tempEnd, Annotation tempLookup, Document tempDoc) throws ExecutionException {
    try {
      original.add(originalStart, originalEnd, tempLookup.getType(), tempLookup.getFeatures());
    }
    catch(InvalidOffsetException ioe) {
      String errorDetails = String.format("temp %d, %d [%s]-> original %d, %d  ", tempStart, tempEnd, Utils.stringFor(tempDoc, tempLookup), 
          originalStart, originalEnd);
      throw new ExecutionException(errorDetails, ioe);
    }
  }

  
  
  /* Is this Lookup within the scope of the input annotations?  It might not be, if Token annotations
   * have been copied by AST only over the significant sections of the document.
   */
  private boolean coveredByInput(long tempStart, long tempEnd, AnnotationSet tempInputAS) {
    if (tempInputAS.getCovering(wrappedInputASName, tempStart, tempStart).isEmpty()) {
      return false;
    }
    // implied else
    if (tempInputAS.getCovering(wrappedInputASName, tempEnd, tempEnd).isEmpty()) {
      return false;
    }
    // implied else
    return true;
  }

  
  /**
   * Sets the document to work on
   * 
   * @param doc
   */
  @Override
  public void setDocument(gate.Document doc) {
    this.document = doc;
  }

  /**
   * Returns the document set up by user to work on
   * 
   * @return a {@link Document}
   */
  @Override
  public gate.Document getDocument() {
    return this.document;
  }

  /**
   * Sets the name of annotation set that should be used for storing new
   * annotations
   * 
   * @param outputASName
   */
  public void setOutputASName(String outputASName) {
    this.outputASName = outputASName;
  }

  /**
   * Returns the outputAnnotationSetName
   * 
   * @return a {@link String} value.
   */
  public String getOutputASName() {
    return this.outputASName;
  }

  /**
   * sets the input AnnotationSet Name
   * 
   * @param inputASName
   */
  public void setInputASName(String inputASName) {
    this.inputASName = inputASName;
  }

  /**
   * Returns the inputAnnotationSetName
   * 
   * @return a {@link String} value.
   */
  public String getInputASName() {
    return this.inputASName;
  }

  /**
   * Feature names for example: Token.string, Token.root etc... Values of these
   * features should be used to replace the actual string of these features.
   * This method allows a user to set the name of such features
   * 
   * @param inputs
   */
  public void setInputFeatureNames(java.util.List<String> inputs) {
    this.inputFeatureNames = inputs;
  }

  /**
   * Returns the feature names that are provided by the user to use their values
   * to replace their actual strings in the document
   * 
   * @return a {@link List} value.
   */
  public java.util.List<String> getInputFeatureNames() {
    return this.inputFeatureNames;
  }

  public Gazetteer getGazetteerInst() {
    return this.gazetteerInst;
  }

  public void setGazetteerInst(gate.creole.gazetteer.Gazetteer gazetteerInst) {
    this.gazetteerInst = gazetteerInst;
  }

  // Gazetteer Runtime parameters
  private gate.Document document;

  private java.lang.String outputASName;

  private java.lang.String inputASName;

  // Flexible Gazetteer parameter
  private Gazetteer gazetteerInst;

  private java.util.List<String> inputFeatureNames;
}
