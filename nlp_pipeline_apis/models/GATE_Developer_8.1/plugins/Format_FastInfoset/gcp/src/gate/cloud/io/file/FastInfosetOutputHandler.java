/*
 * FastInfosetOutputHandler.java
 * 
 * Copyright (c) 2007-2013, The University of Sheffield. See the file
 * COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 * 
 * This file is part of GATE (see http://gate.ac.uk/), and is free software,
 * licenced under the GNU Library General Public License, Version 2, June 1991
 * (in the distribution as file licence.html, and also available at
 * http://gate.ac.uk/gate/licence.html).
 * 
 * Mark A. Greenwood, 09/08/2013
 */

package gate.cloud.io.file;

import static gate.cloud.io.IOConstants.PARAM_FILE_EXTENSION;
import gate.Annotation;
import gate.Document;
import gate.cloud.batch.DocumentID;
import gate.cloud.io.OutputHandler;
import gate.corpora.DocumentStaxUtils;
import gate.util.Benchmark;
import gate.util.GateException;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import com.sun.xml.fastinfoset.stax.StAXDocumentSerializer;

/**
 * An {@link OutputHandler} that writes to files, in GATE Stand-off format using
 * the FastInfoset XML encoding.
 */
public class FastInfosetOutputHandler extends AbstractFileOutputHandler {

  @Override
  protected void configImpl(Map<String, String> configData) throws IOException,
    GateException {
    // make sure we default to .finf as the extension
    if(!configData.containsKey(PARAM_FILE_EXTENSION)) {
      configData.put(PARAM_FILE_EXTENSION, ".finf");
    }
    super.configImpl(configData);
  }

  @Override
  protected void outputDocumentImpl(Document document, DocumentID documentId)
    throws IOException, GateException {
    
    //get the relevant annotations we want to save
    Map<String, Collection<Annotation>> annotationSetsMap =
      collectAnnotations(document);

    // 
    OutputStream outputStream = getFileOutputStream(documentId);
    try {

      // start the document and write the XML decl
      StAXDocumentSerializer xsw = new StAXDocumentSerializer(outputStream);
      xsw.writeStartDocument("1.0");

      // write the document (wrapped in some benchmarking stuff)
      String saveStandoffBID =
        Benchmark.createBenchmarkId("saveFastInfoset", document.getName());
      long startTime = Benchmark.startPoint();
      DocumentStaxUtils.writeDocument(document, annotationSetsMap, xsw, "");
      xsw.writeEndDocument();
      Benchmark.checkPoint(startTime, saveStandoffBID, this, null);

      // flush and close the XSW just to be on the safe side
      xsw.flush();
      xsw.close();
    } catch(XMLStreamException e) {
      throw (IOException)new IOException("Error writing FastInfoset XML")
        .initCause(e);
    } finally {
      // closing the XSW doesn't close the stream (by design)
      outputStream.close();
    }
  }
}