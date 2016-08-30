/*
 *  Copyright (c) 1995-2014, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *  
 *  Mark A. Greenwood, 23/06/2014
 */
package gate.corpora;

import gate.AnnotationSet;
import gate.DocumentContent;
import gate.FeatureMap;
import gate.Resource;
import gate.corpora.datasift.DataSift;
import gate.corpora.datasift.Interaction;
import gate.creole.ResourceInstantiationException;
import gate.creole.metadata.AutoInstance;
import gate.creole.metadata.CreoleResource;
import gate.util.DocumentFormatException;
import gate.util.InvalidOffsetException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

@CreoleResource(name = "GATE DataSift JSON Document Format", isPrivate = true, autoinstances = {@AutoInstance(hidden = true)}, comment = "Format parser for DataSift JSON files")
public class DataSiftFormat extends TextualDocumentFormat {

  private static final long serialVersionUID = -1768496491819413313L;

  /** Initialise this resource, and return it. */
  public Resource init() throws ResourceInstantiationException {
    // Register ad hoc MIME-type
    // There is an application/json mime type, but I don't think
    // we want everything to be handled this way?
    MimeType mime = new MimeType("text", "x-json-datasift");
    // Register the class handler for this MIME-type
    mimeString2ClassHandlerMap.put(mime.getType() + "/" + mime.getSubtype(),
        this);
    // Register the mime type with string
    mimeString2mimeTypeMap.put(mime.getType() + "/" + mime.getSubtype(), mime);
    // Register file suffixes for this mime type
    suffixes2mimeTypeMap.put("datasift.json", mime);
    // Register magic numbers for this mime type
    // magic2mimeTypeMap.put("Subject:",mime);
    // Set the mimeType for this language resource
    setMimeType(mime);
    return this;
  }

  @Override
  public void cleanup() {
    super.cleanup();

    MimeType mime = getMimeType();

    mimeString2ClassHandlerMap.remove(mime.getType() + "/" + mime.getSubtype());
    mimeString2mimeTypeMap.remove(mime.getType() + "/" + mime.getSubtype());
    suffixes2mimeTypeMap.remove("datasift.json");
  }

  @Override
  public void unpackMarkup(gate.Document doc) throws DocumentFormatException {
    if((doc == null)
        || (doc.getSourceUrl() == null && doc.getContent() == null)) { throw new DocumentFormatException(
        "GATE document is null or no content found. Nothing to parse!"); }

    setNewLineProperty(doc);
    String jsonString = StringUtils.trimToEmpty(doc.getContent().toString());

    // TODO build the new content
    StringBuilder concatenation = new StringBuilder();
    
    try {
      ObjectMapper om = new ObjectMapper();
      
      JsonFactory factory = new JsonFactory(om);
      JsonParser parser = factory.createParser(jsonString);
      
      Map<DataSift,Long> offsets = new HashMap<DataSift,Long>();
      
      Iterator<DataSift> it = parser.readValuesAs(DataSift.class);
      while(it.hasNext()) {
        DataSift ds = it.next();
        offsets.put(ds,(long)concatenation.length());
        concatenation.append(ds.getInteraction().getContent()).append("\n\n");
      }
      
      // Set new document content
      DocumentContent newContent =
          new DocumentContentImpl(concatenation.toString());

      doc.edit(0L, doc.getContent().size(), newContent);
      
      AnnotationSet originalMarkups = doc.getAnnotations("Original markups");
      for (Map.Entry<DataSift, Long> item : offsets.entrySet()) {
        DataSift ds = item.getKey();
        Interaction interaction = ds.getInteraction();
        Long start = item.getValue();
        
        FeatureMap features = interaction.asFeatureMap();        
        features.putAll(ds.getFurtherData());
        
        originalMarkups.add(start,start+interaction.getContent().length(),"Interaction",features);            
      }
      
    } catch(InvalidOffsetException | IOException e) {
      throw new DocumentFormatException(e);
    }
  }
}
