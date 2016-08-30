/*
 *  Copyright (c) 1995-2014, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *  
 *  $Id: JSONTweetFormat.java 18436 2014-11-06 19:03:45Z ian_roberts $
 */
package gate.corpora;

import gate.AnnotationSet;
import gate.DocumentContent;
import gate.GateConstants;
import gate.Resource;
import gate.corpora.twitter.PreAnnotation;
import gate.corpora.twitter.Tweet;
import gate.corpora.twitter.TweetStreamIterator;
import gate.corpora.twitter.TweetUtils;
import gate.creole.ResourceInstantiationException;
import gate.creole.metadata.AutoInstance;
import gate.creole.metadata.CreoleResource;
import gate.util.DocumentFormatException;
import gate.util.InvalidOffsetException;
import gate.util.Out;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;


/** Document format for handling JSON tweets: either one 
 *  object {...} or a list [{tweet...}, {tweet...}, ...].
 *  
 *  This format produces one GATE document from one JSON file.
 */
@CreoleResource(name = "GATE JSON Tweet Document Format", isPrivate = true,
    autoinstances = {@AutoInstance(hidden = true)},
    comment = "Format parser for Twitter JSON files",
    helpURL = "http://gate.ac.uk/userguide/sec:social:twitter:format")

public class JSONTweetFormat extends TextualDocumentFormat {
  private static final long serialVersionUID = 6878020036304333918L;

  
  /** Default construction */
  public JSONTweetFormat() { super();}

  /** Initialise this resource, and return it. */
  public Resource init() throws ResourceInstantiationException{
    // Register ad hoc MIME-type
    // There is an application/json mime type, but I don't think
    // we want everything to be handled this way?
    MimeType mime = new MimeType("text","x-json-twitter");
    // Register the class handler for this MIME-type
    mimeString2ClassHandlerMap.put(mime.getType()+ "/" + mime.getSubtype(), this);
    // Register the mime type with string
    mimeString2mimeTypeMap.put(mime.getType() + "/" + mime.getSubtype(), mime);
    // Register file suffixes for this mime type
    suffixes2mimeTypeMap.put("json", mime);
    // Register magic numbers for this mime type
    //magic2mimeTypeMap.put("Subject:",mime);
    // Set the mimeType for this language resource
    setMimeType(mime);
    return this;
  }
  
  @Override
  public void cleanup() {
    super.cleanup();
    
    MimeType mime = getMimeType();
    
    mimeString2ClassHandlerMap.remove(mime.getType()+ "/" + mime.getSubtype());
    mimeString2mimeTypeMap.remove(mime.getType() + "/" + mime.getSubtype());
    suffixes2mimeTypeMap.remove("json");
  }

  @Override
  public void unpackMarkup(gate.Document doc) throws DocumentFormatException{
    if ( (doc == null) || (doc.getSourceUrl() == null && doc.getContent() == null) ) {
      throw new DocumentFormatException("GATE document is null or no content found. Nothing to parse!");
    }

    setNewLineProperty(doc);
    String jsonString = StringUtils.trimToEmpty(doc.getContent().toString());
    try {
      // Parse the String
      Iterator<Tweet> tweetSource = new TweetStreamIterator(jsonString, null, null);
      Map<Tweet, Long> tweetStarts = new LinkedHashMap<Tweet, Long>();
      
      // Put them all together to make the unpacked document content
      StringBuilder concatenation = new StringBuilder();
      while(tweetSource.hasNext()) {
        Tweet tweet = tweetSource.next();
        if(tweet != null) {
          // TweetStreamIterator can return null even when hasNext is true,
          // for search result style JSON.  This is not a problem, just ignore
          // and check hasNext again.
          tweetStarts.put(tweet, (long) concatenation.length());
          concatenation.append(tweet.getString()).append("\n\n");
        }
      }

      // Set new document content 
      DocumentContent newContent = new DocumentContentImpl(concatenation.toString());
      doc.edit(0L, doc.getContent().size(), newContent);

      // Create Original markups annotations for each tweet
      for (Tweet tweet : tweetStarts.keySet()) {
        for (PreAnnotation preAnn : tweet.getAnnotations()) {
          preAnn.toAnnotation(doc, tweetStarts.get(tweet));
        }
      }
    }
    catch (InvalidOffsetException | IOException e) {
      doc.getFeatures().put("parsingError", Boolean.TRUE);

      Boolean bThrow =
              (Boolean)doc.getFeatures().get(
                      GateConstants.THROWEX_FORMAT_PROPERTY_NAME);

      if(bThrow != null && bThrow.booleanValue()) {
        // the next line is commented to avoid Document creation fail on
        // error
        throw new DocumentFormatException(e);
      }
      else {
        Out.println("Warning: Document remains unparsed. \n"
                + "\n  Stack Dump: ");
        e.printStackTrace(Out.getPrintWriter());
      } // if
    }
  }

}
