/*
 *  Copyright (c) 1995-2014, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *  
 *  $Id: Population.java 18579 2015-02-17 18:49:14Z johann_p $
 */
package gate.corpora.twitter;

import gate.Corpus;
import gate.Document;
import gate.DocumentContent;
import gate.Factory;
import gate.corpora.DocumentContentImpl;
import gate.creole.ResourceInstantiationException;
import gate.creole.metadata.AutoInstance;
import gate.creole.metadata.CreoleResource;
import gate.gui.NameBearerHandle;
import gate.gui.ResourceHelper;
import gate.util.GateRuntimeException;
import gate.util.InvalidOffsetException;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


@CreoleResource(name = "Twitter Corpus Populator", tool = true, autoinstances = @AutoInstance,
    comment = "Populate a corpus from Twitter JSON containing multiple Tweets",
    helpURL = "http://gate.ac.uk/userguide/sec:social:twitter:format")
public class Population extends ResourceHelper  {

  private static final long serialVersionUID = 1443073039199794668L;

  private static final Logger logger = Logger.getLogger(Population.class.getName());
  
  private static final int COUNTER_DIGITS = 9;

  
  public static void populateCorpus(final Corpus corpus, URL inputUrl, PopulationConfig config) 
      throws ResourceInstantiationException {
    populateCorpus(corpus, inputUrl, config.getEncoding(), config.getContentKeys(), 
        config.getFeatureKeys(), config.getTweetsPerDoc(), config.isProcessEntities());
  }
  
  /**
   * 
   * @param corpus
   * @param inputUrl
   * @param encoding
   * @param contentKeys
   * @param featureKeys
   * @param tweetsPerDoc 0 = put them all in one document; otherwise the number per document
   * @throws ResourceInstantiationException
   */
  public static void populateCorpus(final Corpus corpus, URL inputUrl, String encoding, List<String> contentKeys,
      List<String> featureKeys, int tweetsPerDoc) throws ResourceInstantiationException {
    populateCorpus(corpus, inputUrl, encoding, contentKeys, featureKeys, tweetsPerDoc, true);
  }

  public static void populateCorpus(final Corpus corpus, URL inputUrl, String encoding, List<String> contentKeys,
          List<String> featureKeys, int tweetsPerDoc, boolean processEntities) throws ResourceInstantiationException {

    InputStream input = null;
    try {
      input = inputUrl.openStream();
      
      // TODO Detect & handle gzipped input.
      // TODO handling of entities, once there's GUI to control it
      TweetStreamIterator tweetSource = new TweetStreamIterator(input, contentKeys, featureKeys, false, processEntities);

      int tweetCounter = 0;
      int tweetDocCounter = 0;
      Document document = newDocument(inputUrl, tweetCounter, COUNTER_DIGITS);
      StringBuilder content = new StringBuilder();
      Map<PreAnnotation, Integer> annotandaOffsets = new HashMap<PreAnnotation, Integer>();
      
      /* TweetStreamIterator.hasNext() returns true if there might be more
       * tweets in the file; a concatenated set of search results might
       * have an object with an empty statuses array followed by one 
       * with some tweet in the array; in that case, we ignore the first null
       * and keep looking.       */
      
      while (tweetSource.hasNext()) {
        Tweet tweet = tweetSource.next();
        // next() == null means there wasn't anything ready in the stream,
        // but there might be next time.
        if (tweet != null) {
          tweetDocCounter++;
          if ( (tweetsPerDoc > 0) && (tweetDocCounter >= tweetsPerDoc) ) {
            closeDocument(document, content, annotandaOffsets, corpus);
            tweetDocCounter = 0;
            document = newDocument(inputUrl, tweetCounter, COUNTER_DIGITS);
            content = new StringBuilder();
            annotandaOffsets = new HashMap<PreAnnotation, Integer>();
          }
          
          int startOffset = content.length();
          content.append(tweet.getString());
          for (PreAnnotation preAnn : tweet.getAnnotations()) {
            annotandaOffsets.put(preAnn, startOffset);
          }
          
          content.append('\n');
          tweetCounter++;
        }
      } // end of Tweet loop
      
      closeDocument(document, content, annotandaOffsets, corpus);
      
      if(corpus.getDataStore() != null) {
        corpus.getDataStore().sync(corpus);
      }
      
    }
    catch (Exception e) {
      throw new ResourceInstantiationException(e);
    }
    finally {
      if (input != null) {
        try {
          input.close();
        } 
        catch(IOException e) {
          logger.warn("Error in Twitter Population", e);
        }
      }
      
    }
    
  }


  private static Document newDocument(URL url, int counter, int digits) throws ResourceInstantiationException {
    Document document = Factory.newDocument("");
    String code = StringUtils.leftPad(Integer.toString(counter), digits, '0');
    String name = StringUtils.stripToEmpty(StringUtils.substring(url.getPath(), 1)) + "_" + code;
    document.setName(name);
    document.setSourceUrl(url);
    document.getFeatures().put(Document.DOCUMENT_MIME_TYPE_PARAMETER_NAME, TweetUtils.MIME_TYPE);
    document.getFeatures().put("gate.SourceURL", url.toString());
    return document;
  }

  
  private static void closeDocument(Document document, StringBuilder content, Map<PreAnnotation, Integer> annotandaOffsets, Corpus corpus) throws InvalidOffsetException {
    if (content.length() == 0) {
      Factory.deleteResource(document);
    }
    else {    
      DocumentContent contentImpl = new DocumentContentImpl(content.toString());
      document.setContent(contentImpl);
      for (PreAnnotation preAnn : annotandaOffsets.keySet()) {
        try {
          preAnn.toAnnotation(document, annotandaOffsets.get(preAnn));
        } catch (InvalidOffsetException ex) {
          // show the content in the error message, so it becomes more easy to find the 
          // cause of an InvalidOffsetException in a large file that has many json entries.
          throw new GateRuntimeException("Attempt to add annotation "+preAnn+" for text="+contentImpl,ex);
        }
      }
      corpus.add(document);
      
      if (corpus.getLRPersistenceId() != null) {
        corpus.unloadDocument(document);
        Factory.deleteResource(document);
      }
    }
  }

  
  @Override
  protected List<Action> buildActions(final NameBearerHandle handle) {
    List<Action> actions = new ArrayList<Action>();

    if(!(handle.getTarget() instanceof Corpus)) return actions;

    actions.add(new AbstractAction("Populate from Twitter JSON files") {
      private static final long serialVersionUID = -8511779592856786327L;

      @Override
      public void actionPerformed(ActionEvent e)  {
        final PopulationDialogWrapper dialog = new PopulationDialogWrapper();

        // If no files were selected then just stop
        try {
          final List<URL> fileUrls = dialog.getFileUrls();
          if ( (fileUrls == null) || fileUrls.isEmpty() ) {
            return;
          }
          
          // Run the population in a separate thread so we don't lock up the GUI
          Thread thread =
              new Thread(Thread.currentThread().getThreadGroup(),
                  "Twitter JSON Corpus Populator") {
                public void run() {
                  for (URL fileUrl : fileUrls) {
                    try {
                      populateCorpus((Corpus) handle.getTarget(), fileUrl, dialog.getConfig());
                    } catch(ResourceInstantiationException e) {
                      logger.warn("Error in Twitter Population, url="+fileUrl, e);
                    }
                  }
                }
              };
          thread.setPriority(Thread.MIN_PRIORITY);
          thread.start();
        }
        catch(MalformedURLException e0) {
          logger.warn("Error in Twitter Population", e0);
        }
      }
    });

    return actions;
  }

}
