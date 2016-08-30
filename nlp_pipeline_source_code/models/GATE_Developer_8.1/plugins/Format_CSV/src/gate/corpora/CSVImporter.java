/*
 * CSVImporter.java
 * 
 * Copyright (c) 2013, The University of Sheffield. See the file COPYRIGHT.txt
 * in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 * 
 * This file is part of GATE (see http://gate.ac.uk/), and is free software,
 * licenced under the GNU Library General Public License, Version 2, June 1991
 * (in the distribution as file licence.html, and also available at
 * http://gate.ac.uk/gate/licence.html).
 * 
 * Mark A. Greenwood, 10/09/2013
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
import gate.util.Files;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;

import au.com.bytecode.opencsv.CSVReader;

@SuppressWarnings("serial")
@CreoleResource(name = "CSV Corpus Populater", tool = true, autoinstances = @AutoInstance, comment = "Populate a corpus from CSV files", helpURL = "http://gate.ac.uk/userguide/sec:creole:csv")
public class CSVImporter extends ResourceHelper {

  private static JComponent dialog = null;

  private static SpinnerNumberModel textColModel = null;

  private static JCheckBox cboFeatures = null;

  private static JCheckBox cboDocuments = null;

  private static JTextField txtURL = null;

  private static JTextField txtSeparator = null;

  private static JTextField txtQuoteChar = null;
  
  private static JTextField txtEncoding = null;

  private static FileFilter CSV_FILE_FILTER = new ExtensionFileFilter(
      "CSV Files (*.csv)", "csv");

  private static void buildDialog() {
    // we'll use the same dialog instance regardless of the corpus we are
    // populating so we'll create a single static instance

    if (dialog != null) return;
    
    dialog = new JPanel();
    
    textColModel = new SpinnerNumberModel(0, 0,
        Integer.MAX_VALUE, 1);
    
    cboFeatures = new JCheckBox(
        "1st Row Contains Column Labels", true);

    cboDocuments = new JCheckBox(
        "Create One Document Per Row", false);

    txtURL = new JTextField(30);

    txtSeparator = new JTextField(",", 3);

    txtQuoteChar = new JTextField("\"", 3);
    
    txtEncoding = new JTextField("UTF-8");

    
    dialog.setLayout(new GridBagLayout());

    GridBagConstraints constraints = new GridBagConstraints();
    constraints.gridx = GridBagConstraints.RELATIVE;
    constraints.gridy = 0;
    constraints.gridwidth = 2;
    constraints.anchor = GridBagConstraints.WEST;
    constraints.fill = GridBagConstraints.NONE;
    constraints.insets = new Insets(0, 0, 0, 5);
    dialog.add(new JLabel("CSV File URL:"), constraints);

    constraints = new GridBagConstraints();
    constraints.gridx = GridBagConstraints.RELATIVE;
    constraints.gridy = 0;
    constraints.gridwidth = 5;
    constraints.fill = GridBagConstraints.HORIZONTAL;
    constraints.insets = new Insets(0, 0, 0, 10);
    dialog.add(txtURL, constraints);

    constraints = new GridBagConstraints();
    constraints.gridx = GridBagConstraints.RELATIVE;
    constraints.gridy = 0;
    constraints.gridwidth = 1;
    constraints.anchor = GridBagConstraints.NORTHWEST;
    JButton btnCSVURL = new JButton(MainFrame.getIcon("open-file"));
    dialog.add(btnCSVURL, constraints);

    constraints = new GridBagConstraints();
    constraints.gridx = GridBagConstraints.RELATIVE;
    constraints.gridy = 1;
    constraints.gridwidth = 1;
    constraints.fill = GridBagConstraints.HORIZONTAL;
    constraints.insets = new Insets(0, 0, 15, 5);
    dialog.add(new JLabel("Encoding:"), constraints);
    
    constraints = new GridBagConstraints();
    constraints.gridx = GridBagConstraints.RELATIVE;
    constraints.gridy = 1;
    constraints.gridwidth = 1;
    constraints.fill = GridBagConstraints.HORIZONTAL;
    constraints.insets = new Insets(0, 15, 15, 10);
    dialog.add(txtEncoding, constraints);
    
    constraints = new GridBagConstraints();
    constraints.gridx = GridBagConstraints.RELATIVE;
    constraints.gridy = 1;
    constraints.gridwidth = 1;
    constraints.fill = GridBagConstraints.HORIZONTAL;
    constraints.insets = new Insets(0, 0, 15, 5);
    dialog.add(new JLabel("Column Separator:"), constraints);

    constraints = new GridBagConstraints();
    constraints.gridx = GridBagConstraints.RELATIVE;
    constraints.gridy = 1;
    constraints.gridwidth = 1;
    constraints.fill = GridBagConstraints.HORIZONTAL;
    constraints.insets = new Insets(0, 15, 15, 10);
    dialog.add(txtSeparator, constraints);

    constraints = new GridBagConstraints();
    constraints.gridx = GridBagConstraints.RELATIVE;
    constraints.gridy = 1;
    constraints.gridwidth = 1;
    constraints.fill = GridBagConstraints.HORIZONTAL;
    constraints.insets = new Insets(0, 0, 15, 5);
    dialog.add(new JLabel("Quote Character:"), constraints);

    constraints = new GridBagConstraints();
    constraints.gridx = GridBagConstraints.RELATIVE;
    constraints.gridy = 1;
    constraints.gridwidth = 1;
    constraints.fill = GridBagConstraints.HORIZONTAL;
    constraints.insets = new Insets(0, 0, 15, 10);
    dialog.add(txtQuoteChar, constraints);

    constraints = new GridBagConstraints();
    constraints.gridx = GridBagConstraints.RELATIVE;
    constraints.gridy = 2;
    constraints.gridwidth = 3;
    constraints.anchor = GridBagConstraints.NORTHWEST;
    constraints.insets = new Insets(0, 0, 15, 5);
    dialog.add(new JLabel("Document Content Is In Column"), constraints);

    constraints = new GridBagConstraints();
    constraints.gridx = GridBagConstraints.RELATIVE;
    constraints.gridy = 2;
    constraints.gridwidth = 3;
    constraints.anchor = GridBagConstraints.NORTHWEST;
    dialog.add(new JSpinner(textColModel), constraints);

    constraints = new GridBagConstraints();
    constraints.gridx = GridBagConstraints.RELATIVE;
    constraints.gridy = 3;
    constraints.gridwidth = GridBagConstraints.RELATIVE;
    constraints.anchor = GridBagConstraints.NORTHWEST;
    dialog.add(cboFeatures, constraints);

    constraints = new GridBagConstraints();
    constraints.gridx = GridBagConstraints.RELATIVE;
    constraints.gridy = 4;
    constraints.gridwidth = GridBagConstraints.RELATIVE;
    constraints.anchor = GridBagConstraints.NORTHWEST;
    dialog.add(cboDocuments, constraints);

    btnCSVURL.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        JFileChooser filer = MainFrame.getFileChooser();

        filer.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        filer.setDialogTitle("Select a CSV File");
        filer.resetChoosableFileFilters();
        filer.setAcceptAllFileFilterUsed(false);
        filer
            .addChoosableFileFilter((javax.swing.filechooser.FileFilter)CSV_FILE_FILTER);
        filer
            .setFileFilter((javax.swing.filechooser.FileFilter)CSV_FILE_FILTER);

        if(filer.showOpenDialog(dialog) != JFileChooser.APPROVE_OPTION) return;
        try {
          txtURL.setText(filer.getSelectedFile().toURI().toURL()
              .toExternalForm());
        } catch(IOException ioe) {
          // do nothing here
        }
      }
    });
  }

  @Override
  protected List<Action> buildActions(final NameBearerHandle handle) {
    List<Action> actions = new ArrayList<Action>();

    if(!(handle.getTarget() instanceof Corpus)) return actions;

    actions.add(new AbstractAction("Populate from CSV File") {
      @Override
      public void actionPerformed(ActionEvent e) {

        buildDialog();
        
        // display the populater dialog and return if it is cancelled
        if(JOptionPane.showConfirmDialog(null, dialog,
            "Populate From CSV File", JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) return;

        // we want to run the population in a separate thread so we don't lock
        // up the GUI
        Thread thread =
            new Thread(Thread.currentThread().getThreadGroup(),
                "CSV Corpus Populater") {

              public void run() {
                try {

                  // unescape the strings that define the format of the file and
                  // get the actual chars
                  char separator =
                      StringEscapeUtils.unescapeJava(txtSeparator.getText())
                          .charAt(0);
                  char quote =
                      StringEscapeUtils.unescapeJava(txtQuoteChar.getText())
                          .charAt(0);

                  // see if we can convert the URL to a File instance
                  File file = null;
                  try {
                    file = Files.fileFromURL(new URL(txtURL.getText()));
                  } catch(IllegalArgumentException iae) {
                    // this will happen if someone enters an actual URL, but we
                    // handle that later so we can just ignore the exception for
                    // now and keep going
                  }

                  if(file != null && file.isDirectory()) {
                    // if we have a File instance and that points at a directory
                    // then....

                    // get all the CSV files in the directory structure
                    File[] files =
                        Files.listFilesRecursively(file, CSV_FILE_FILTER);

                    for(File f : files) {
                      // for each file...

                      // skip directories as we don't want to handle those
                      if(f.isDirectory()) continue;

                      if(cboDocuments.isSelected()) {
                        // if we are creating lots of documents from a single
                        // file
                        // then call the populate method passing through all the
                        // options from the GUI
                        populate((Corpus)handle.getTarget(), f.toURI().toURL(), txtEncoding.getText(),
                            (Integer)textColModel.getValue(),
                            cboFeatures.isSelected(), separator, quote);
                      } else {
                        // if we are creating a single document from a single
                        // file
                        // then call the createDoc method passing through all
                        // the
                        // options from the GUI
                        createDoc((Corpus)handle.getTarget(),
                            f.toURI().toURL(), txtEncoding.getText(),
                            (Integer)textColModel.getValue(),
                            cboFeatures.isSelected(), separator, quote);
                      }
                    }
                  } else {
                    // we have a single URL to process so...

                    if(cboDocuments.isSelected()) {
                      // if we are creating lots of documents from a single file
                      // then call the populate method passing through all the
                      // options from the GUI
                      populate((Corpus)handle.getTarget(),
                          new URL(txtURL.getText()), txtEncoding.getText(),
                          (Integer)textColModel.getValue(),
                          cboFeatures.isSelected(), separator, quote);
                    } else {
                      // if we are creating a single document from a single file
                      // then call the createDoc method passing through all the
                      // options from the GUI
                      createDoc((Corpus)handle.getTarget(),
                          new URL(txtURL.getText()), txtEncoding.getText(),
                          (Integer)textColModel.getValue(),
                          cboFeatures.isSelected(), separator, quote);
                    }
                  }
                } catch(Exception e) {
                  // TODO give a sensible error message
                  e.printStackTrace();
                }
              }
            };

        // let's leave the GUI nice and responsive
        thread.setPriority(Thread.MIN_PRIORITY);

        // lets get to it and do some actual work!
        thread.start();

      }
    });

    return actions;
  }

  public static void populate(Corpus corpus, URL csv, String encoding, int column,
      boolean colLabels) {
    populate(corpus, csv, encoding, column, colLabels, ',', '"');
  }

  /**
   * Create a new document from each row and push it into the specified corpus
   * 
   * @param corpus
   *          the Corpus to add documents to
   * @param csv
   *          the URL of the CSV file to processes
   * @param column
   *          the (zero index based) column which contains the text content
   * @param colLabels
   *          true if the first row contains column labels, true otherwise
   * @param separator
   *          the character that is used to separate columns (usually ,)
   * @param quote
   *          the character used to quote data that includes the column
   *          separator (usually ")
   */
  public static void populate(Corpus corpus, URL csv, String encoding, int column,
      boolean colLabels, char separator, char quote) {
    CSVReader reader = null;
    try {
      // open a CSVReader over the URL
      reader =
          new CSVReader(new InputStreamReader(csv.openStream(),encoding), separator,
              quote);

      // if we are adding features read the first line
      String[] features = (colLabels ? reader.readNext() : null);

      String[] nextLine;
      while((nextLine = reader.readNext()) != null) {
        // for each line in the file...

        // skip the line if there are less columns than we need to get to the
        // content
        if(column >= nextLine.length) continue;

        // skip the line if the column with the content is empty
        if(nextLine[column].trim().equals("")) continue;

        FeatureMap fmap = Factory.newFeatureMap();
        if(colLabels) {
          // copy all the features from the row into a FeatureMap using the
          // labels from the first line
          for(int i = 0; i < features.length; ++i) {
            if(i != column && i < nextLine.length) {
              fmap.put(features[i], nextLine[i]);
            }
          }
        }

        // setup the initialization params for the document
        FeatureMap params = Factory.newFeatureMap();
        params.put(Document.DOCUMENT_STRING_CONTENT_PARAMETER_NAME,
            nextLine[column]);

        // create the document
        Document doc =
            (Document)Factory.createResource(
                gate.corpora.DocumentImpl.class.getName(), params, fmap);

        // add the document to the corpus
        corpus.add(doc);

        if(corpus.getLRPersistenceId() != null) {
          // persistent corpus -> unload the document
          corpus.unloadDocument(doc);
          Factory.deleteResource(doc);
        }

      }

      if(corpus.getDataStore() != null) {
        // if this corpus is in a datastore make sure we sync it back
        corpus.getDataStore().sync(corpus);
      }
    } catch(Exception e) {
      // not much we can do other than report the exception
      throw new RuntimeException("Unable to open CSV file: " + csv, e);
    } finally {
      // if we opened the reader successfully then close it so we don't leak
      // file handles
      if(reader != null) IOUtils.closeQuietly(reader);
    }
  }

  public static void createDoc(Corpus corpus, URL csv, String encoding, int column,
      boolean colLabels) {
    createDoc(corpus, csv, encoding, column, colLabels, ',', '"');
  }

  /**
   * Creates a single document from the CSV file
   * 
   * @param corpus
   *          the Corpus to add documents to
   * @param csv
   *          the URL of the CSV file to processes
   * @param column
   *          the (zero index based) column which contains the text content
   * @param colLabels
   *          true if the first row contains column labels, true otherwise
   * @param separator
   *          the character that is used to separate columns (usually ,)
   * @param quote
   *          the character used to quote data that includes the column
   *          separator (usually ")
   */
  public static void createDoc(Corpus corpus, URL csv, String encoding, int column,
      boolean colLabels, char separator, char quote) {
    CSVReader reader = null;
    Document doc = null;
    try {
      // open a CSVReader over the URL
      reader =
          new CSVReader(new InputStreamReader(csv.openStream(),encoding), separator,
              quote);

      // if we are adding features read the first line
      String[] features = (colLabels ? reader.readNext() : null);

      // create an empty document to which we will add the content as we go
      doc = Factory.newDocument("");

      String[] nextLine;
      while((nextLine = reader.readNext()) != null) {
        // for each line in the file...

        // skip the line if there are less columns than we need to get to the
        // content
        if(column >= nextLine.length) continue;

        // skip the line if the column with the doc content is empty
        if(nextLine[column].trim().equals("")) continue;

        FeatureMap fmap = Factory.newFeatureMap();
        if(colLabels) {
          // put the data from the other columns into a FeatureMap using the
          // labels from the first row
          for(int i = 0; i < features.length; ++i) {
            if(i != column && i < nextLine.length) {
              fmap.put(features[i], nextLine[i]);
            }
          }
        }

        // find out how long the document currently is
        // TODO can we keep a running track of this to avoid this call?
        long length = doc.getContent().size();

        // add the new text to the document
        doc.edit(length, length, new DocumentContentImpl(nextLine[column]
            + "\n\n"));

        // add the spanning annotation to the Original markups set, we use the
        // type "Text" if the columns don't have labels
        doc.getAnnotations("Original markups").add(length,
            length + nextLine[column].length(),
            (colLabels ? features[column] : "Text"), fmap);
      }

      // store the original csv file URL as a document feature
      doc.getFeatures().put("csvURL", csv.toExternalForm());

      // so that the doc gets recreated properly put the XML for the doc we just
      // created into the init param that will be used if the document is
      // recreated
      doc.setParameterValue(Document.DOCUMENT_STRING_CONTENT_PARAMETER_NAME,
          doc.toXml());

      // add the document to the corpus
      corpus.add(doc);

      if(corpus.getLRPersistenceId() != null) {
        // persistent corpus -> unload the document
        corpus.unloadDocument(doc);
        Factory.deleteResource(doc);
      }

      if(corpus.getDataStore() != null) {
        // if this corpus is in a datastore make sure we sync it back
        corpus.getDataStore().sync(corpus);
      }
    } catch(Exception e) {
      // if we failed somewhere then delete the part built document
      if(doc != null) Factory.deleteResource(doc);

      // throw a "helpful" exception
      throw new RuntimeException("Unable to open CSV file: " + csv, e);
    } finally {
      // if we got as far as opening a reader over the file then close it
      if(reader != null) IOUtils.closeQuietly(reader);
    }
  }
}
