/*
 * FastInfosetExporter.java
 * 
 * Copyright (c) 2012-2013, The University of Sheffield. See the file
 * COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 * 
 * This file is part of GATE (see http://gate.ac.uk/), and is free software,
 * licenced under the GNU Library General Public License, Version 2, June 1991
 * (in the distribution as file licence.html, and also available at
 * http://gate.ac.uk/gate/licence.html).
 * 
 * Mark A. Greenwood, 01/08/2013
 */

package gate.corpora;

import gate.Document;
import gate.DocumentExporter;
import gate.FeatureMap;
import gate.creole.metadata.AutoInstance;
import gate.creole.metadata.CreoleResource;

import java.io.IOException;
import java.io.OutputStream;

import javax.xml.stream.XMLStreamException;

import com.sun.xml.fastinfoset.stax.StAXDocumentSerializer;

@SuppressWarnings("serial")
@CreoleResource(name = "Fast Infoset Exporter", tool = true, autoinstances = @AutoInstance, comment = "Export GATE documents to GATE XML stored in the binary Fast Infoset format", helpURL = "http://gate.ac.uk/userguide/sec:creole:fastinfoset")
public class FastInfosetExporter extends DocumentExporter {

  public FastInfosetExporter() {
    super("Fast Infoset","finf","application/fastinfoset");
  }
  
  public void export(Document doc, OutputStream out, FeatureMap options)
    throws IOException {

    StAXDocumentSerializer xsw = new StAXDocumentSerializer(out);

    try {
      xsw.writeStartDocument("1.0");
      DocumentStaxUtils.writeDocument(doc, xsw, "");
      xsw.writeEndDocument();
      xsw.flush();
      xsw.close();
    } catch(XMLStreamException e) {
      throw new IOException(e);
    }
  }
}
