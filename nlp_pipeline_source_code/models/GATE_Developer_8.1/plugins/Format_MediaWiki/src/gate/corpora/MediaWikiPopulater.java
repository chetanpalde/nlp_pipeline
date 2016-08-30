/*
 * MediaWikiPopulater.java
 *
 * Copyright (c) 2012-2013, The University of Sheffield. See the file COPYRIGHT.txt
 * in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 * This file is part of GATE (see http://gate.ac.uk/), and is free software,
 * licenced under the GNU Library General Public License, Version 2, June 1991
 * (in the distribution as file licence.html, and also available at
 * http://gate.ac.uk/gate/licence.html).
 *
 * Mark A. Greenwood, 01/08/2013
 */

package gate.corpora;

import gate.Corpus;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.creole.metadata.AutoInstance;
import gate.creole.metadata.CreoleResource;
import gate.gui.MainFrame;
import gate.gui.NameBearerHandle;
import gate.gui.ResourceHelper;
import gate.util.ExtensionFileFilter;
import info.bliki.wiki.dump.IArticleFilter;
import info.bliki.wiki.dump.Siteinfo;
import info.bliki.wiki.dump.WikiArticle;
import info.bliki.wiki.dump.WikiXMLParser;
import info.bliki.wiki.model.WikiModel;

import java.awt.event.ActionEvent;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;

import org.xml.sax.SAXException;

@CreoleResource(name = "MediaWiki Corpus Populater", tool = true,
    autoinstances = @AutoInstance,
    comment = "Populate a corpus from a MediaWiki XML dump",
    helpURL = "http://gate.ac.uk/userguide/sec:creole:mediawiki")
public class MediaWikiPopulater extends ResourceHelper {

  private static final long serialVersionUID = -2505026690286047751L;

  /**
   * so that we don't end up with a document littered with unparsed
   * "magic words" we we need a custom model that we can use to filter them out
   */
  private final static WikiModel model = new WikiModel("${image}", "${title}") {
    @Override
    public String getRawWikiContent(String namespace, String articleName,
        Map<String, String> templateParameters) {
      String rawContent =
          super.getRawWikiContent(namespace, articleName, templateParameters);

      if(rawContent == null) {
        // if we return 'null' then the magic variables end up in the doc with
        // full markup which isn't really what we want, so we just return the
        // empty string instead to remove them entirely from the document
        return "";
      } else {
        return rawContent;
      }
    }
  };

  @SuppressWarnings("serial")
  @Override
  protected List<Action> buildActions(final NameBearerHandle handle) {
    List<Action> actions = new ArrayList<Action>();

    if(!(handle.getTarget() instanceof Corpus)) return actions;

    actions.add(new AbstractAction("Populate from MediaWiki XML Dump") {

      @Override
      public void actionPerformed(ActionEvent e) {

        // configure the file chooser ready for use
        final JFileChooser filer = MainFrame.getFileChooser();
        filer.setFileSelectionMode(JFileChooser.FILES_ONLY);
        filer.setDialogTitle("Select a MediaWiki XML Dump File");
        filer.resetChoosableFileFilters();
        filer.setAcceptAllFileFilterUsed(false);
        ExtensionFileFilter filter =
            new ExtensionFileFilter("MediaWiki XML Dump Files (*.xml)", "xml");
        filer.addChoosableFileFilter(filter);
        filer.setFileFilter(filter);

        // if no file was selected then just stop
        if(filer.showOpenDialog(MainFrame.getInstance()) != JFileChooser.APPROVE_OPTION)
          return;

        // we want to run the population in a separate thread so we don't lock
        // up the GUI
        Thread thread =
            new Thread(Thread.currentThread().getThreadGroup(),
                "MediaWiki XML Dump Corpus Populater") {
              public void run() {
                try {
                  populateCorpus((Corpus)handle.getTarget(), filer.getSelectedFile()
                      .toURI().toURL());
                } catch(MalformedURLException e) {
                  // this really should not be possible so just dump the
                  // exception and quit
                  e.printStackTrace();
                }
              }
            };
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.start();
      }
    });

    return actions;
  }

  public static void populateCorpus(final Corpus corpus, URL xml) {
    try {
      // get the model ready for parsing
      model.setUp();

      // the parser needs an InputStream so lets build one up from the original
      // document content
      InputStream in = xml.openStream();

      // create a parser to load the XML document
      WikiXMLParser parser = new WikiXMLParser(in, new IArticleFilter() {

        @Override
        public void process(WikiArticle article, Siteinfo site)
            throws SAXException {

          try {
            // extract the page content and convert it to HTML
            // copy relevant metadata onto the document
            FeatureMap features = Factory.newFeatureMap();
            features.put("mediawiki.title", article.getTitle());
            features.put("mediawiki.timestamp", article.getTimeStamp());
            features.put("mediawiki.id", article.getId());
            features.put("mediawiki.revision", article.getRevisionId());
            features.put("mediawiki.sitename", site.getSitename());
            features.put("mediawiki.base", site.getBase());

            FeatureMap params = Factory.newFeatureMap();
            params.put(Document.DOCUMENT_STRING_CONTENT_PARAMETER_NAME,
                article.getText());
            params.put(Document.DOCUMENT_MIME_TYPE_PARAMETER_NAME,
                "text/x-mediawiki");

            Document doc =
                (Document)Factory.createResource("gate.corpora.DocumentImpl",
                    params, features, article.getTitle());

            corpus.add(doc);

            if(corpus.getLRPersistenceId() != null) {
              // persistent corpus -> unload the document
              corpus.unloadDocument(doc);
              Factory.deleteResource(doc);
            }
          } catch(Exception e) {
            e.printStackTrace();
          }
        }
      });

      // now we are all set let's parse the MediaWiki XML file
      parser.parse();

      if(corpus.getDataStore() != null) {
        // if this corpus is in a datastore make sure we sync it back
        corpus.getDataStore().sync(corpus);
      }

    } catch(Exception e) {
      // oh dear, something went wrong and it's unlikely there is anything we
      // can do about it so let's just throw our hands in the air and pass the
      // responsibility up the stack and hope someone else will deal with it!
      throw new RuntimeException(e);
    } finally {
      // signal that, at least for now, we have finished with the model
      model.tearDown();
    }
  }
}
