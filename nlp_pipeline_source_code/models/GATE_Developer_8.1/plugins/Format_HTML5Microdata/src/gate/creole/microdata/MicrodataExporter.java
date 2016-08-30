/*
 * MicrodataExporter.java
 * 
 * Copyright (c) 2011-2014, The University of Sheffield.
 * 
 * This file is part of GATE (see http://gate.ac.uk/), and is free software,
 * licenced under the GNU Library General Public License, Version 3, June 2007
 * (in the distribution as file licence.html, and also available at
 * http://gate.ac.uk/gate/licence.html).
 * 
 * Mark A. Greenwood, 11/06/2011
 */

package gate.creole.microdata;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Document;
import gate.DocumentExporter;
import gate.FeatureMap;
import gate.GateConstants;
import gate.creole.metadata.AutoInstance;
import gate.creole.metadata.CreoleParameter;
import gate.creole.metadata.CreoleResource;
import gate.creole.metadata.Optional;
import gate.creole.metadata.RunTime;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("serial")
@CreoleResource(name = "HTML5 Microdata Exporter", comment = "Exports Annotations as HTML5 Microdata", tool = true, autoinstances = @AutoInstance, icon="HTML5")
public class MicrodataExporter extends DocumentExporter {

  private URL configURL;
  private String annotationSetName;

  public URL getConfigURL() {
    return configURL;
  }
  
  @RunTime
  @CreoleParameter(defaultValue = "resources/schema.org/ANNIE.xml")
  public void setConfigURL(URL configURL) {
    this.configURL = configURL;
  }
  
  public String getAnnotationSetName() {
    return annotationSetName;
  }
  
  @RunTime
  @Optional
  @CreoleParameter
  public void setAnnotationSetName(String annotationSetName) {
    this.annotationSetName = annotationSetName;
  }
  
  public MicrodataExporter() {
    super("HTML5 Microdata","html","text/html");
  }

  @Override
  public void export(Document document, OutputStream out, FeatureMap options)
    throws IOException {

    Microdata microdata = Microdata.load((URL)options.get("configURL"));
    AnnotationSet inputAS = document.getAnnotations((String)options.get("annotationSetName"));
    
    PrintStream pout = new PrintStream(out);
    
    boolean itempropAsMeta = true;

    // TODO list
    // 1. deal with co-reference (each itemscope can have multiple entries but
    // may need to use ids and itemref from the microdata format)

    AnnotationSet originalMarkups =
      document.getAnnotations(GateConstants.ORIGINAL_MARKUPS_ANNOT_SET_NAME);

    AnnotationSet body = originalMarkups.get("html");
    if(body.size() != 1) return;
    body = originalMarkups.get("body");
    if(body.size() != 1) return;

    inputAS =
      inputAS.get(body.firstNode().getOffset(), body.lastNode().getOffset());

    Set<Integer> used = new HashSet<Integer>();
    Set<Integer> created = new HashSet<Integer>();

    try {
      for(ItemScope scope : microdata.getItemscopes()) {
        for(Annotation a : scope.getMatchingAnnotations(inputAS)) {
          if(!used.contains(a.getId())) {
            created.addAll(scope.addMicrodata(a, itempropAsMeta,
              originalMarkups));
            used.add(a.getId());
          }
        }
      }
      
      pout.println(document.toXml(null, false));
      
    } catch(Exception e) {
      throw new IOException(e);
    }
    finally {
      for(Integer id : created) {
        originalMarkups.remove(originalMarkups.get(id));
      }
    }    
  }
}
