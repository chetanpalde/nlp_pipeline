package com.ontotext.russie.gazetteer;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.ontotext.russie.RussIEConstants;
import com.ontotext.russie.morph.Lemma;
import com.ontotext.russie.morph.LemmaImpl;

public class InflectionalGazetteerXMLReader implements RussIEConstants,
  ContentHandler {

  private List<Lemma> lemmas;

  private String wordform;

  protected List<String> listImportTypes;

  private StringBuffer tagContent;

  static final String DEFAULT_PARSER = "org.apache.xerces.parsers.SAXParser";

  public static final String TAG_RUSNAMES = "rusnames";

  public static final String TAG_NAME = "name";

  public static final String TAG_CAT = "cat";

  public static final String TAG_CAT_END = "/cat";

  public static final String TAG_FORM = "form";

  public static final String TAG_TAG = "tag";

  public static final String TAG_TAG_END = "/tag";

  public static final String TAG_PH = "ph";

  public static final String TAG_PH_END = "/ph";

  public static final String ATTR_N = "n";

  public static final String CAT_LOCATION = "Loc";

  public static final String CAT_PERSON_FAMILY = "PerFamily";

  public static final String CAT_PERSON_FIRST = "PerFirst";

  public static final String CAT_PERSON = "Per";

  private static Map<String, String> catVsMajorType;

  private String parserValue;

  Locator locator;

  Lemma lemma;

  String category;

  int occurance;

  String name;

  String type;

  static {
    catVsMajorType = new HashMap<String, String>();
    catVsMajorType.put("Loc", "location");
    catVsMajorType.put("Per", "person_full");
    catVsMajorType.put("PerFamily", "surname");
    catVsMajorType.put("PerFirst", "person_first");
  }

  public InflectionalGazetteerXMLReader(List<String> importTypes) {
    lemmas = new ArrayList<Lemma>();
    // listImportTypes = new ArrayList<String>();
    tagContent = new StringBuffer();
    parserValue = "org.apache.xerces.parsers.SAXParser";
    locator = null;
    listImportTypes = importTypes;
  }

  public static String getMajorType4Category(String cat) {
    return catVsMajorType.get(cat);
  }

  public void load(String fileName) {
    File file = new File(fileName);
    load(file);
  }

  public void load(File file) {
    FileReader reader = null;
    try {
      reader = new FileReader(file);
      parse(reader);
      reader.close();
    } catch(IOException e) {
      e.printStackTrace();
    } catch(SAXException e) {
      e.printStackTrace();
    }
  }

  public void setParser(String parserClass) {
    parserValue = parserClass;
  }

  public void parse(Reader r) throws IOException, SAXException {
    InputSource isrc = new InputSource(r);
    XMLReader reader = XMLReaderFactory.createXMLReader(parserValue);
    reader.setContentHandler(this);
    reader.parse(isrc);
  }

  public void parse(URL u) throws IOException, SAXException {
    InputSource isrc = new InputSource(u.toExternalForm());
    InputStream stream = u.openStream();
    try {
      isrc.setByteStream(stream);
      XMLReader reader = XMLReaderFactory.createXMLReader(parserValue);
      reader.setContentHandler(this);
      reader.parse(isrc);
    } finally {
      stream.close();
    }
  }

  public void setDocumentLocator(Locator locator) {
    this.locator = locator;
  }

  public void startDocument() throws SAXException {
  }

  public void endDocument() throws SAXException {
  }

  public void startElement(String namespaceURI, String localName, String qName,
                           Attributes atts) throws SAXException {
    tagContent = new StringBuffer();
    if(localName.equals("rusnames")) return;
    if(localName.equals("name")) {
      lemma = new LemmaImpl();
      lemmas.add(lemma);
      return;
    }
    if(localName.equals("cat")) return;
    if(localName.equals("form")) {
      lemma.getFeatureMap().put("occurance", new Integer(atts.getValue(0)));
      return;
    } else {
      return;
    }
  }

  public void endElement(String namespaceURI, String localName, String qName)
    throws SAXException {
    if(localName.equals("cat"))
      try {
        String mtype = catVsMajorType.get(tagContent.toString().trim());
        if(listImportTypes.contains(mtype))
          lemma.getFeatureMap().put("majorType", mtype);
      } catch(Exception x) {
        System.out.println("Unknown Category :" + tagContent);
      }
    if(localName.equals("ph")) try {
      wordform = new String(tagContent.toString().getBytes(), "UTF-8");
      wordform = wordform.trim();
    } catch(UnsupportedEncodingException x) {
    }
    if(localName.equals("tag")) {
      type = tagContent.toString().trim();
      lemma.addWordForm(wordform, type);
      if(type.charAt(0) == 'N' && type.charAt(type.length() - 1) == 'n' ||
        type.charAt(0) == 'V' && type.charAt(type.length() - 1) == 'i')
        lemma.setMainForm(wordform, type);
    }
    tagContent = new StringBuffer();
  }

  public void characters(char ch[], int start, int length) throws SAXException {
    tagContent.append(ch, start, length);
  }

  public void startPrefixMapping(String s, String s1) throws SAXException {
  }

  public void endPrefixMapping(String s) throws SAXException {
  }

  public void ignorableWhitespace(char ac[], int i, int j) throws SAXException {
  }

  public void processingInstruction(String s, String s1) throws SAXException {
  }

  public void skippedEntity(String s) throws SAXException {
  }

  public List<Lemma> getLemmas() {
    return new ArrayList<Lemma>(lemmas);
  }

}
