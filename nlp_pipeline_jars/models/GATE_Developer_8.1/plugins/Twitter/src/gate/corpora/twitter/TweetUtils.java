/*
 *  Copyright (c) 1995-2014, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *  
 *  $Id: TweetUtils.java 18496 2014-12-12 15:13:48Z ian_roberts $
 */
package gate.corpora.twitter;

import gate.Factory;
import gate.FeatureMap;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

/* REFERENCES
 * Jackson API
 * http://wiki.fasterxml.com/JacksonHome
 * Standard: RFC 4627
 * https://tools.ietf.org/html/rfc4627
 * */

public class TweetUtils  {
  
  public static final String PATH_SEPARATOR = ":";
  public static final String MIME_TYPE = "text/x-json-twitter";
  public static final String DEFAULT_ENCODING = "UTF-8";
  public static final String TWEET_ANNOTATION_TYPE = "Tweet";

  public static final String DEFAULT_TEXT_ATTRIBUTE = "text";

  public static final String[] DEFAULT_CONTENT_KEYS = {DEFAULT_TEXT_ATTRIBUTE, 
    "created_at", "user:name"};
  public static final String[] DEFAULT_FEATURE_KEYS = {"user:screen_name", 
    "user:location", "id_str", "source", "truncated", "retweeted_status:id"};
  
  /**
   * The JSON property representing entities (e.g. hashtags).
   */
  public static final String ENTITIES_ATTRIBUTE = "entities";
  
  /**
   * Date parser that understands the "created_at" timestamp format.
   * The parser can cope with dates in any timezone but the returned
   * DateTime objects will always be anchored in UTC.
   */
  // Month names in Twitter API responses are English, so force locale
  public static final DateTimeFormatter CREATED_AT_FORMAT = DateTimeFormat.forPattern(
          "EEE MMM dd HH:mm:ss Z yyyy").withZoneUTC().withLocale(Locale.ENGLISH);

  
  public static List<Tweet> readTweets(String string) throws IOException {
    if (string.startsWith("[")) {
      return readTweetList(string, null, null);
    }
  
    // implied else
    return readTweetLines(string, null, null);
  }


  public static List<Tweet> readTweets(String string, List<String> contentKeys, List<String> featureKeys) throws IOException {
    if (string.startsWith("[")) {
      return readTweetList(string, contentKeys, featureKeys);
    }

    // implied else
    return readTweetLines(string, contentKeys, featureKeys);
  }
  
  
  public static List<Tweet>readTweetLines(String string, List<String> contentKeys, List<String> featureKeys) throws IOException {
    String[] lines = string.split("[\\n\\r]+");
    return readTweetStrings(lines, contentKeys, featureKeys);
  }
  

  public static List<Tweet>readTweetStrings(String[] lines, List<String> contentKeys, List<String> featureKeys) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    List<Tweet> tweets = new ArrayList<Tweet>();
    
    for (String line : lines) {
      if (line.length() > 0) {
        JsonNode jnode = mapper.readTree(line);
        tweets.add(Tweet.readTweet(jnode, contentKeys, featureKeys));
      }
    }
    
    return tweets;
  }

  
  public static List<Tweet>readTweetStrings(List<String> lines, List<String> contentKeys, List<String> featureKeys) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    List<Tweet> tweets = new ArrayList<Tweet>();
    
    for (String line : lines) {
      if (line.length() > 0) {
        JsonNode jnode = mapper.readTree(line);
        tweets.add(Tweet.readTweet(jnode, contentKeys, featureKeys));
      }
    }
    
    return tweets;
  }

  
  public static List<Tweet> readTweetList(String string, List<String> contentKeys, List<String> featureKeys) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    List<Tweet> tweets = new ArrayList<Tweet>();
    ArrayNode jarray = (ArrayNode) mapper.readTree(string);
    for (JsonNode jnode : jarray) {
      tweets.add(Tweet.readTweet(jnode, contentKeys, featureKeys));
    }
    return tweets;
  }


  public static Object process(JsonNode node) {
    /* JSON types: number, string, boolean, array, object (dict/map),
     * null.  All map keys are strings.
     */

    if (node.isBoolean()) {
      return node.asBoolean();
    }
    if (node.isIntegralNumber()) {
      // use Long even if the number is representable as an Integer,
      // since Long is better supported in JAPE etc.
      if(node.canConvertToLong()) {
        return node.asLong();
      } else {
        return node.bigIntegerValue();
      }
    }
    if (node.isNumber()) {
      // fractional number, as integers would have been caught by
      // the previous test.  The numberValue will be a Double
      // unless the parser was specifically configured to use
      // BigDecimal instead
      return node.numberValue();
    }
    if (node.isTextual()) {
      return node.asText();
    }
      
    if (node.isNull()) {
      return null;
    }
    
    if (node.isArray()) {
      List<Object> list = new ArrayList<Object>();
      for (JsonNode item : node) {
        list.add(process(item));
      }
      return list;
    }

    if (node.isObject()) {
      FeatureMap map = Factory.newFeatureMap();
      Iterator<String> keys = node.fieldNames();
      while (keys.hasNext()) {
        String key = keys.next();
        map.put(key, process(node.get(key)));
      }
      return map;
    }

    return node.toString();
  }

  

  public static FeatureMap process(JsonNode node, List<String> keepers) {
    FeatureMap found = Factory.newFeatureMap();
    for (String keeper : keepers) {
      String[] keySequence = StringUtils.split(keeper, PATH_SEPARATOR);
      Object value = dig(node, keySequence, 0);
      if (value != null) {
        found.put(keeper, value);
      }
    }
    return found;
  }
  
  
  /**
   * Dig through a JSON object, key-by-key (recursively).
   * @param node
   * @param keySequence
   * @return the value held by the last key in the sequence; this will
   * be a FeatureMap if there is further nesting
   */
  public static Object dig(JsonNode node, String[] keySequence, int index) {
    if ( (index >= keySequence.length) || (node == null) ) {
      return null;
    }
    
    if (node.has(keySequence[index])) {
      JsonNode value = node.get(keySequence[index]); 
      if (keySequence.length == (index + 1)) {
        // Found last key in sequence; convert the JsonNode
        // value to a normal object (possibly FeatureMap)
        return process(value);
      }
      else if (value != null){
        // Found current key; keep digging for the rest
        return dig(value, keySequence, index + 1);
      }
    }
    
    return null;
  }

  

}
