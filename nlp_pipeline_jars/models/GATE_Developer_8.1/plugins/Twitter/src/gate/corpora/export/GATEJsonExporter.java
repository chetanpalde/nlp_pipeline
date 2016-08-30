package gate.corpora.export;

/*
 *  Copyright (c) 1995-2014, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 *  Mark A. Greenwood 17/07/2014
 *
 */

import gate.Annotation;
import gate.AnnotationSet;
import gate.Corpus;
import gate.CorpusExporter;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.GateConstants;
import gate.Utils;
import gate.corpora.DocumentJsonUtils;
import gate.creole.metadata.AutoInstance;
import gate.creole.metadata.CreoleParameter;
import gate.creole.metadata.CreoleResource;
import gate.creole.metadata.Optional;
import gate.creole.metadata.RunTime;
import gate.util.InvalidOffsetException;
import gate.util.LuckyException;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.io.SerializedString;
import com.fasterxml.jackson.databind.ObjectMapper;

@CreoleResource(name = "GATE JSON Exporter",
    comment = "Export documents and corpora in JSON format",
    tool = true, autoinstances = @AutoInstance, icon = "GATEJSON")
public class GATEJsonExporter extends CorpusExporter {
  
  private static final long serialVersionUID = -8087536348560365618L;

  protected static final ObjectMapper MAPPER = new ObjectMapper();
  
  /**
   * No-op, exists only as a host for the parameter annotations.
   */
  @Optional
  @RunTime
  @CreoleParameter(comment = "The annotation set from which " +
  		"otherwise-unspecified entity annotations will be taken")
  public void setEntitiesAnnotationSetName(String name) {}
  public String getEntitiesAnnotationSetName() { return null; }
  
  /**
   * No-op, exists only as a host for the parameter annotations.
   */
  @RunTime
  @CreoleParameter(comment = "Annotation types to export.  " +
  		"Plain annotation types will be taken from the set named " +
  		"by the annotationSetName parameter, entries containing " +
  		"a colon are treated as setName:type (with an empty setName " +
  		"denoting the default set).")
  public void setAnnotationTypes(Set<String> types) {}
  public Set<String> getAnnotationTypes() { return null; }
  
  /**
   * No-op, exists only as a host for the parameter annotations.
   */
  @RunTime
  @CreoleParameter(defaultValue = "false", comment = "Whether " +
  		"to wrap the output as a JSON array.  When exporting a corpus, " +
  		"true will write a JSON array of objects, one per document, " +
  		"whereas false will simply output one object per document " +
  		"separated by newlines.")
  public void setExportAsArray(Boolean array) {}
  public Boolean getExportAsArray() { return null; }
  
  /**
   * No-op, exists only as a host for the parameter annotations.
   */
  @RunTime
  @Optional
  @CreoleParameter(defaultValue = GateConstants.ORIGINAL_MARKUPS_ANNOT_SET_NAME,
          comment = "Annotation set in which the \"document " +
  		"annotation\" can be found.  These annotations serve to delimit " +
  		"the parts of the document that should be output, and the result " +
  		"will contain one JSON object per annotation, with the " +
  		"annotation's features as additional JSON properties.")
  public void setDocumentAnnotationASName(String asName) {}
  public String getDocumentAnnotationASName() { return null; }

  /**
   * No-op, exists only as a host for the parameter annotations.
   */
  @RunTime
  @Optional
  @CreoleParameter(defaultValue = "Tweet", comment = "Annotation type " +
  		"for \"document annotations\".  These annotations serve to delimit " +
      "the parts of the document that should be output, and the result " +
      "will contain one JSON object per annotation, with the " +
      "annotation's features as additional JSON properties.  If " +
      "unspecified, or if a given GATE document contains none of " +
      "these annotations, then the whole document content will be output.")
  public void setDocumentAnnotationType(String type) {}
  public String getDocumentAnnotationType() { return null; }

  public GATEJsonExporter() {
    super("GATE JSON", "json","application/json");
  }

  @Override
  public void export(Document doc, OutputStream out, FeatureMap options)
    throws IOException {
    try(JsonGenerator generator = openGenerator(out, options)) {
      export(doc, generator, options);
    }
  }
  
  public void export(Corpus corpus, OutputStream out, FeatureMap options)
    throws IOException {
    try(JsonGenerator generator = openGenerator(out, options)) {
      Iterator<Document> docIter = corpus.iterator();
      int currentDocIndex = 0;
      while(docIter.hasNext()) {
        boolean docWasLoaded =
                corpus.isDocumentLoaded(currentDocIndex);
        Document currentDoc = docIter.next();
        try {
          export(currentDoc, generator, options);
        } finally {
          // unload if necessary
          if(!docWasLoaded) {
            corpus.unloadDocument(currentDoc);
            Factory.deleteResource(currentDoc);
          }
          currentDocIndex++;
        }
      }
    }
  }
  
  /**
   * Create a JsonGenerator ready to write to the given output stream.
   * If the specified options indicate that we want to wrap the output
   * in an array then output the array start event in preparation.
   */
  protected JsonGenerator openGenerator(OutputStream out, FeatureMap options)
    throws IOException {
    JsonGenerator generator = MAPPER.getFactory().createGenerator(out);
    generator.disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET);
    generator.enable(JsonGenerator.Feature.AUTO_CLOSE_JSON_CONTENT);
    if(options.containsKey("exportAsArray") && ((Boolean)options.get("exportAsArray")).booleanValue()) {
      generator.writeStartArray();
    } else {
      // writing concatenated, put newlines in between
      generator.setRootValueSeparator(new SerializedString("\n"));
    }
    
    return generator;
  }

  public void export(Document doc, JsonGenerator generator, FeatureMap options)
    throws IOException {
    try {
      AnnotationSet defaultEntitiesAS =
        doc.getAnnotations((String)options.get("entitiesAnnotationSetName"));
      
      @SuppressWarnings("unchecked")
      Collection<String> types = (Collection<String>)options.get("annotationTypes");
      
      Map<String,Collection<Annotation>> annotationsMap = new LinkedHashMap<>();
      
      for (String type : types) {
        String[] setAndType = type.split(":", 2);
        if(setAndType.length == 1) {
          annotationsMap.put(type, defaultEntitiesAS.get(type));
        } else {
          annotationsMap.put(type, doc.getAnnotations(setAndType[0]).get(setAndType[1]));
        }
      }
      
      // look for document annotations
      AnnotationSet docAnnots = null;
      if(options.containsKey("documentAnnotationType")) {
        docAnnots = doc.getAnnotations((String)options.get("documentAnnotationASName"))
                .get((String)options.get("documentAnnotationType"));
      }
      if(docAnnots == null || docAnnots.isEmpty()) {
        // no document annotations, write everything
        Map<String, Collection<Annotation>> sortedAnnots = new LinkedHashMap<>();
        for(Map.Entry<String, Collection<Annotation>> entry : annotationsMap.entrySet()) {
          sortedAnnots.put(entry.getKey(), Utils.inDocumentOrder((AnnotationSet)entry.getValue()));
        }
        DocumentJsonUtils.writeDocument(doc, 0L, Utils.end(doc), sortedAnnots, null, null, generator);
      } else {
        for(Annotation docAnnot : Utils.inDocumentOrder(docAnnots)) {
          Map<String, Collection<Annotation>> coveredAnnotations = new HashMap<>();
          for(Map.Entry<String, Collection<Annotation>> entry : annotationsMap.entrySet()) {
            coveredAnnotations.put(entry.getKey(),
                    Utils.inDocumentOrder(((AnnotationSet)entry.getValue()).getContained(
                            Utils.start(docAnnot), Utils.end(docAnnot))));
          }
          DocumentJsonUtils.writeDocument(doc, Utils.start(docAnnot), Utils.end(docAnnot),
                  coveredAnnotations, docAnnot.getFeatures(), null, generator);
        }
      }
    } catch(InvalidOffsetException e) {
      // should never happen, as all offsets come from the document itself
      throw new LuckyException("Invalid offset found within document", e);
    }
  }
}
