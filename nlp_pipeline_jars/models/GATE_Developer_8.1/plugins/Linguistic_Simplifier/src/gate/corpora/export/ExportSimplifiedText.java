/*
 * ExportSimplifiedText.java
 *
 * Copyright (c) 2004-2014, The University of Sheffield.
 * 
 * This file is part of GATE (see http://gate.ac.uk/), and is free software,
 * Licensed under the GNU Library General Public License, Version 3, June 2007
 * (in the distribution as file licence.html, and also available at
 * http://gate.ac.uk/gate/licence.html).
 * 
 * Mark A. Greenwood, 2014
 */

package gate.corpora.export;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Document;
import gate.DocumentExporter;
import gate.FeatureMap;
import gate.creole.metadata.AutoInstance;
import gate.creole.metadata.CreoleParameter;
import gate.creole.metadata.CreoleResource;
import gate.creole.metadata.Optional;
import gate.creole.metadata.RunTime;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Vector;

@CreoleResource(name = "Simplified Text Exporter",
    comment = "Simplified text exporter (plain text output)",
    tool = true, autoinstances = @AutoInstance, icon = "LinguisticSimplifier")
public class ExportSimplifiedText extends DocumentExporter {
  
  private static final long serialVersionUID = 7490823488963303438L;
  
  private String annotationSetName;

  public String getAnnotationSetName() {
    return annotationSetName;
  }

  @RunTime
  @Optional
  @CreoleParameter
  public void setAnnotationSetName(String annotationSetName) {
    this.annotationSetName = annotationSetName;
  }

  public ExportSimplifiedText() {
    super("Simplified Text","txt","text/plain");
  }
  
  @Override
  public void export(Document doc, OutputStream out, FeatureMap options)
      throws IOException {
    AnnotationSet redundantBits =
        doc.getAnnotations((String)options.get("annotationSetName")).get("Redundant");
    
    SortedAnnotationList sortedAnnotations = new SortedAnnotationList();
    for(Annotation redundant : redundantBits) {
      sortedAnnotations.addSortedExclusive(redundant);
    }

    long insertPositionEnd;
    long insertPositionStart;

    StringBuffer editableContent =
        new StringBuffer(doc.getContent().toString());

    for(int i = sortedAnnotations.size() - 1; i >= 0; --i) {
      Annotation redundant = sortedAnnotations.get(i);
      insertPositionStart = redundant.getStartNode().getOffset().longValue();
      insertPositionEnd = redundant.getEndNode().getOffset().longValue();

      if(insertPositionEnd != -1 && insertPositionStart != -1) {
        String replacement = "";
        
        if (redundant.getFeatures().containsKey("replacement")) {
          replacement = (String)redundant.getFeatures().get("replacement");
        }
        
        editableContent.replace((int)insertPositionStart, (int)insertPositionEnd, replacement);
      }
    }
    
    PrintWriter pout = new PrintWriter(out);
    pout.println(editableContent.toString());
    pout.flush();

  }
  
  private static class SortedAnnotationList extends Vector<Annotation> {

    private static final long serialVersionUID = -3517593401660887655L;

    public SortedAnnotationList() {
      super();
    }

    public boolean addSortedExclusive(Annotation annot) {
      Annotation currAnot = null;

      for(int i = 0; i < size(); ++i) {
        currAnot = get(i);
        if(annot.overlaps(currAnot)) { return false; }
      }

      long annotStart = annot.getStartNode().getOffset().longValue();
      long currStart;

      for(int i = 0; i < size(); ++i) {
        currAnot = get(i);
        currStart = currAnot.getStartNode().getOffset().longValue();
        if(annotStart < currStart) {
          insertElementAt(annot, i);

          return true;
        }
      }

      int size = size();
      insertElementAt(annot, size);
      return true;
    }
  }
}
