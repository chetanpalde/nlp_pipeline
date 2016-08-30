/*
 * FastInfosetDocumentFormat.java
 * 
 * Copyright (c) 1995-2013, The University of Sheffield. See the file
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
import gate.GateConstants;
import gate.Resource;
import gate.TextualDocument;
import gate.creole.ResourceInstantiationException;
import gate.creole.metadata.AutoInstance;
import gate.creole.metadata.CreoleResource;
import gate.event.StatusListener;
import gate.util.DocumentFormatException;
import gate.util.Out;
import gate.xml.XmlDocumentHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.io.IOUtils;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.sun.xml.fastinfoset.sax.SAXDocumentParser;
import com.sun.xml.fastinfoset.stax.StAXDocumentParser;
import com.sun.xml.fastinfoset.stax.StAXManager;

@CreoleResource(name = "Fast Infoset Document Format", isPrivate = true, autoinstances = {@AutoInstance(hidden = true)}, comment = "Format parser for GATE XML stored in the binary Fast Infoset format", helpURL = "http://gate.ac.uk/userguide/sec:creole:fastinfoset")
public class FastInfosetDocumentFormat extends TextualDocumentFormat {

  private static final long serialVersionUID = -2394353168913311584L;
  
  private static StAXManager staxManager;

  /** Default construction */
  public FastInfosetDocumentFormat() {
    super();
  }

  /** We could collect repositioning information during XML parsing */
  @Override
  public Boolean supportsRepositioning() {
    return new Boolean(true);
  }

  /** Old style of unpackMarkup (without collecting of RepositioningInfo) */
  @Override
  public void unpackMarkup(Document doc) throws DocumentFormatException {
    unpackMarkup(doc, (RepositioningInfo)null, (RepositioningInfo)null);
  }

  /**
   * Unpack the markup in the document. This converts markup from the native
   * format (e.g. XML) into annotations in GATE format. Uses the
   * markupElementsMap to determine which elements to convert, and what
   * annotation type names to use. If the document was created from a String,
   * then is recommended to set the doc's sourceUrl to <b>null</b>. So, if the
   * document has a valid URL, then the parser will try to parse the XML
   * document pointed by the URL.If the URL is not valid, or is null, then the
   * doc's content will be parsed. If the doc's content is not a valid XML then
   * the parser might crash.
   * 
   * @param doc
   *          The gate document you want to parse. If
   *          <code>doc.getSourceUrl()</code> returns <b>null</b> then the
   *          content of doc will be parsed. Using a URL is recommended because
   *          the parser will report errors correctly if the XML document is not
   *          well formed.
   */
  @Override
  public void unpackMarkup(Document doc, RepositioningInfo repInfo,
      RepositioningInfo ampCodingInfo) throws DocumentFormatException {
    if((doc == null)
        || (doc.getSourceUrl() == null && doc.getContent() == null)) {

    throw new DocumentFormatException(
        "GATE document is null or no content found. Nothing to parse!"); }// End
                                                                          // if

    // Create a status listener
    StatusListener statusListener = new StatusListener() {
      @Override
      public void statusChanged(String text) {
        // This is implemented in DocumentFormat.java and inherited here
        fireStatusChanged(text);
      }
    };

    // determine whether we have a GATE format document or not
    String content = doc.getContent().toString();
    if(content.length() > 2048) {
      content = content.substring(0, 2048);
    }
    boolean gateFormat = isGateFIFormat(content);

    if(gateFormat) {
      unpackGateFormatMarkup(doc, statusListener);
    } else {
      unpackGeneralXmlMarkup(doc, repInfo, ampCodingInfo, statusListener);
    }
  }

  /**
   * Unpacks markup in the GATE-specific standoff XML markup format.
   * 
   * @param doc
   *          the document to process
   * @param statusListener
   *          optional status listener to receive status messages
   * @throws DocumentFormatException
   *           if a fatal error occurs during parsing
   */
  private void unpackGateFormatMarkup(Document doc,
      StatusListener statusListener) throws DocumentFormatException {
    boolean docHasContentButNoValidURL = hasContentButNoValidUrl(doc);

    try {
      Reader inputReader = null;
      InputStream inputStream = null;
      XMLStreamReader xsr = null;
      String encoding = ((TextualDocument)doc).getEncoding();
      if(docHasContentButNoValidURL) {
        xsr =
            new StAXDocumentParser(IOUtils.toInputStream(doc.getContent()
                .toString(), encoding), getStAXManager());
      } else {
        inputStream = doc.getSourceUrl().openStream();
        xsr = new StAXDocumentParser(inputStream, getStAXManager());
      }

      // find the opening GateDocument tag
      xsr.nextTag();

      // parse the document
      try {
        DocumentStaxUtils.readGateXmlDocument(xsr, doc, statusListener);
      } finally {
        xsr.close();
        if(inputStream != null) {
          inputStream.close();
        }
        if(inputReader != null) {
          inputReader.close();
        }
      }
    } catch(XMLStreamException e) {
      doc.getFeatures().put("parsingError", Boolean.TRUE);

      Boolean bThrow =
          (Boolean)doc.getFeatures().get(
              GateConstants.THROWEX_FORMAT_PROPERTY_NAME);

      if(bThrow != null && bThrow.booleanValue()) {
        // the next line is commented to avoid Document creation fail on
        // error
        throw new DocumentFormatException(e);
      } else {
        Out.println("Warning: Document remains unparsed. \n"
            + "\n  Stack Dump: ");
        e.printStackTrace(Out.getPrintWriter());
      } // if
    } catch(IOException ioe) {
      throw new DocumentFormatException("I/O exception for "
          + doc.getSourceUrl().toString(), ioe);
    }
  }

  /**
   * Returns the StAX manager, creating one if it is currently null.
   * 
   * @return <code>staxFactory</code>
   * @throws XMLStreamException
   */
  private static StAXManager getStAXManager() throws XMLStreamException {
    if(staxManager == null) {
      staxManager = new StAXManager(StAXManager.CONTEXT_READER);
    }
    return staxManager;
  }

  /**
   * Unpack markup from any XML format. The XML elements are translated to
   * annotations on the Original markups annotation set.
   * 
   * @param doc
   *          the document to process
   * @throws DocumentFormatException
   */
  private void unpackGeneralXmlMarkup(Document doc, RepositioningInfo repInfo,
      RepositioningInfo ampCodingInfo, StatusListener statusListener)
      throws DocumentFormatException {
    boolean docHasContentButNoValidURL = hasContentButNoValidUrl(doc);

    XmlDocumentHandler xmlDocHandler = null;
    try {

      // Create a new Xml document handler
      xmlDocHandler =
          new XmlDocumentHandler(doc, this.markupElementsMap,
              this.element2StringMap);

      // Register a status listener with it
      xmlDocHandler.addStatusListener(statusListener);

      // set repositioning object
      xmlDocHandler.setRepositioningInfo(repInfo);

      // set the object with ampersand coding positions
      xmlDocHandler.setAmpCodingInfo(ampCodingInfo);

      // create the parser
      SAXDocumentParser newxmlParser = new SAXDocumentParser();

      // Set up the factory to create the appropriate type of parser
      // Fast Infoset doesn't support validating which is good as we would want
      // it off any way, but we do want it to be namesapace aware
      newxmlParser.setFeature("http://xml.org/sax/features/namespaces", true);
      newxmlParser.setFeature("http://xml.org/sax/features/namespace-prefixes",
          true);
      newxmlParser.setContentHandler(xmlDocHandler);
      newxmlParser.setErrorHandler(xmlDocHandler);
      newxmlParser.setDTDHandler(xmlDocHandler);
      newxmlParser.setEntityResolver(xmlDocHandler);

      // Parse the XML Document with the appropriate encoding
      Reader docReader = null;
      try {
        InputSource is;
        if(docHasContentButNoValidURL) {
          // no URL, so parse from string
          is = new InputSource(new StringReader(doc.getContent().toString()));
        } else if(doc instanceof TextualDocument) {
          // textual document - load with user specified encoding
          String docEncoding = ((TextualDocument)doc).getEncoding();
          // don't strip BOM on XML.
          docReader =
              new InputStreamReader(doc.getSourceUrl().openStream(),
                  docEncoding);
          is = new InputSource(docReader);
          // must set system ID to allow relative URLs (e.g. to a DTD) to
          // work
          is.setSystemId(doc.getSourceUrl().toString());
        } else {
          // let the parser decide the encoding
          is = new InputSource(doc.getSourceUrl().toString());
        }
        newxmlParser.parse(is);
      } finally {
        // make sure the open streams are closed
        if(docReader != null) docReader.close();
      }

      ((DocumentImpl)doc).setNextAnnotationId(xmlDocHandler
          .getCustomObjectsId());
    } catch(SAXException e) {
      doc.getFeatures().put("parsingError", Boolean.TRUE);

      Boolean bThrow =
          (Boolean)doc.getFeatures().get(
              GateConstants.THROWEX_FORMAT_PROPERTY_NAME);

      if(bThrow != null && bThrow.booleanValue()) {
        throw new DocumentFormatException(e);
      } else {
        Out.println("Warning: Document remains unparsed. \n"
            + "\n  Stack Dump: ");
        e.printStackTrace(Out.getPrintWriter());
      }

    } catch(IOException e) {
      throw new DocumentFormatException("I/O exception for "
          + doc.getSourceUrl(), e);
    } finally {
      if(xmlDocHandler != null)
        xmlDocHandler.removeStatusListener(statusListener);
    }
  }

  /**
   * Determine whether the given document content string represents a GATE
   * custom format XML document.
   */
  private static boolean isGateFIFormat(String content) {
    return (content.indexOf("GateDocument") != -1);
  }

  /** Initialise this resource, and return it. */
  @Override
  public Resource init() throws ResourceInstantiationException {

    // based on info from
    // http://www.iana.org/assignments/media-types/application/fastinfoset

    // Register XML mime type
    MimeType mime = new MimeType("application", "fastinfoset");

    // Register the class handler for this mime type
    mimeString2ClassHandlerMap.put(mime.getType() + "/" + mime.getSubtype(),
        this);

    // Register the mime type with mine string
    mimeString2mimeTypeMap.put(mime.getType() + "/" + mime.getSubtype(), mime);

    // Register file sufixes for this mime type
    suffixes2mimeTypeMap.put("finf", mime);

    // Register magic numbers for this mime type
    // this should be E0 00 00 01
    // but I can't seem to get it to work

    // Set the mimeType for this language resource
    setMimeType(mime);

    return this;
  }
}
