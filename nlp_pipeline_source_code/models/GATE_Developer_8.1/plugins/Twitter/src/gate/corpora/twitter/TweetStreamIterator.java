/*
 *  Copyright (c) 1995-2014, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *  
 *  $Id: TweetStreamIterator.java 18420 2014-10-30 19:26:45Z ian_roberts $
 */
package gate.corpora.twitter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.zip.GZIPInputStream;
import org.apache.log4j.Logger;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;


public class TweetStreamIterator implements Iterator<Tweet> {

  // Borrowed from gcp IOConstants
  public static final String SEARCH_KEY = "search_metadata";
  public static final String STATUS_KEY = "statuses";
  
  private static final Logger logger = Logger.getLogger(TweetStreamIterator.class.getName());

  private ObjectMapper objectMapper;
  private JsonParser jsonParser;
  private MappingIterator<JsonNode> iterator;
  private List<String> contentKeys, featureKeys;
  private boolean nested;
  private Iterator<JsonNode> nestedStatuses;
  private JsonNode nextNode;
  private boolean handleEntities;
  
  public TweetStreamIterator(String json, List<String> contentKeys, 
          List<String> featureKeys) throws JsonParseException, IOException {
    this(json, contentKeys, featureKeys, true);
  }
  
  
  public TweetStreamIterator(String json, List<String> contentKeys, 
          List<String> featureKeys, boolean handleEntities) throws JsonParseException, IOException {
    this.contentKeys = contentKeys;
    this.featureKeys = featureKeys;
    this.handleEntities = handleEntities;
    objectMapper = new ObjectMapper();
    jsonParser = objectMapper.getFactory().createParser(json);
    init();
  }
  
  public TweetStreamIterator(InputStream input, List<String> contentKeys, 
          List<String> featureKeys, boolean gzip) throws JsonParseException, IOException {
    this(input, contentKeys, featureKeys, gzip, true);
  }
  
  public TweetStreamIterator(InputStream input, List<String> contentKeys, 
          List<String> featureKeys, boolean gzip, boolean handleEntities)
                  throws JsonParseException, IOException {
    this.contentKeys = contentKeys;
    this.featureKeys = featureKeys;
    this.handleEntities = handleEntities;
    InputStream workingInput;
    
    // Following borrowed from gcp JSONStreamingInputHandler
    objectMapper = new ObjectMapper();

    if (gzip) {
      workingInput = new GZIPInputStream(input);
    }
    else {
      workingInput = input;
    }
    
    jsonParser = objectMapper.getFactory().createParser(workingInput).enable(Feature.AUTO_CLOSE_SOURCE);
    init();
  }

  private void init() throws JsonParseException, IOException {
    // If the first token in the stream is the start of an array ("[")
    // then assume the stream as a whole is an array of objects
    // To handle this, simply clear the token - The MappingIterator
    // returned by readValues will cope with the rest in either form.
    if(jsonParser.nextToken() == JsonToken.START_ARRAY) {
      jsonParser.clearCurrentToken();
    }
    iterator = objectMapper.readValues(jsonParser, JsonNode.class);
    this.nested = false;
    this.nestedStatuses = null;
  }
  
  @Override
  public boolean hasNext() {
    /* Suppressing empty documents in Population.populateCorpus is tricky.
     * So hasNext() returns true if their *could* be more tweets in the 
     * file, and next() returns null if there are none in the current 
     * main JsonNode; populateCorpus has to text for null.
     */
    return this.iterator.hasNext()  || 
            (this.nested && (this.nestedStatuses != null) && this.nestedStatuses.hasNext());
    // Belt & braces: this.nested should suffice.
  }

  
  @Override
  public Tweet next() {
    Tweet result = null;
    try {
      if (this.nested && this.nestedStatuses.hasNext()) {
        result = Tweet.readTweet(this.nestedStatuses.next(), contentKeys, featureKeys, handleEntities);
        // Clear the nested flag once the last item in the statuses
        // value's list has been used, so that the next call to next()
        // will drop to the else if clause.
        this.nested = this.nestedStatuses.hasNext();
      }
      
      else if (this.iterator.hasNext()) {
        this.nextNode = this.iterator.next();

        if (isSearchResultList(this.nextNode)) {
          this.nestedStatuses = getStatuses(this.nextNode).iterator();
          this.nested = this.nestedStatuses.hasNext();
          // Set the nested flag according as there is anything left
          // in the statuses value array (which could be empty).
        }
        else {
          this.nested = false;
          this.nestedStatuses = null;
          result = Tweet.readTweet(nextNode, contentKeys, featureKeys, handleEntities);
        }
      }
    }
    catch (IOException e) {
      logger.warn("Internal error in TweetStreamIterator", e);
    }
    return result;
  }

  
  @Override
  public void remove() {
    throw new UnsupportedOperationException("The TweetStream is read-only.");
  }
  
  
  public void close() throws IOException {
    iterator.close();
    jsonParser.close();
  }
  
  
  public static boolean isSearchResultList(JsonNode json) {
    return json.has(SEARCH_KEY) && json.has(STATUS_KEY);
  }
  
  
  public static ArrayNode getStatuses(JsonNode json) throws IOException {
    JsonNode statusList = json.get(STATUS_KEY);
    if (! (statusList instanceof ArrayNode)) {
      throw new IOException("Bad tweet format: value of 'statuses' is not an array!");
    }
    return (ArrayNode) statusList;    
  }
  
  
  public static boolean nonEmpty(JsonNode json) {
    boolean result = false;
    if (isSearchResultList(json)) {
      try {
        result = (getStatuses(json).size() > 0);
      }
      catch (IOException e) {
        logger.warn("Internal error in TweetStreamIterator", e);
      }
    }
    else {
      result = true;
    }
    return result;
  }
  
    
}
