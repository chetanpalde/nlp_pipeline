/*
 *  Lexicon.java
 *
 *  Copyright (c) 1995-2012, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 *  HepTag was originally written by Mark Hepple, this version contains
 *  modifications by Valentin Tablan and Niraj Aswani.
 *
 *  $Id: Lexicon.java 17402 2014-02-22 14:44:43Z markagreenwood $
 */
package hepple.postag;

/**
 * Title:        HepTag
 * Description:  Mark Hepple's POS tagger
 * Copyright:    Copyright (c) 2001
 * Company:      University of Sheffield
 * @author Mark Hepple
 * @version 1.0
 */

import gate.util.BomStrippingInputStreamReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.io.IOUtils;

/**
 * A {@link java.util.HashMap} that maps from lexical entry
 * ({@link java.lang.String}) to possible POS categories
 * ({@link java.util.List}
 */
class Lexicon extends HashMap<String,List<String>> {

  private static final long serialVersionUID = -2320126076517881896L;

  /**
   * Constructor.
   * @param lexiconURL an URL for the file contianing the lexicon.
   */
  public Lexicon(URL lexiconURL) throws IOException{
    this(lexiconURL, null);
  }

  /**
   * Constructor.
   * @param lexiconURL an URL for the file containing the lexicon.
   * @param encoding the character encoding to use for reading the lexicon.
   */
  public Lexicon(URL lexiconURL, String encoding) throws IOException{
    String line;
    BufferedReader lexiconReader = null;
    InputStream lexiconStream = null;
    
    try {
      lexiconStream = lexiconURL.openStream();
      
      if(encoding == null) {
        lexiconReader = new BomStrippingInputStreamReader(lexiconStream);
      } else {
        lexiconReader = new BomStrippingInputStreamReader(lexiconStream,encoding);
      }
  
      line = lexiconReader.readLine();
      String entry;
      List<String> categories;
      while(line != null){
        StringTokenizer tokens = new StringTokenizer(line);
        entry = tokens.nextToken();
        categories = new ArrayList<String>();
        while(tokens.hasMoreTokens()) categories.add(tokens.nextToken());
        put(entry, categories);
  
        line = lexiconReader.readLine();
      }//while(line != null)
    }
    finally {
      IOUtils.closeQuietly(lexiconReader);
      IOUtils.closeQuietly(lexiconStream);
    }
  }//public Lexicon(URL lexiconURL) throws IOException

}//class Lexicon
