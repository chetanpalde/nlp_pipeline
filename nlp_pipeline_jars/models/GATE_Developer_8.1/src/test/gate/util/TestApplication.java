/*
 *  Copyright (c) 1995-2012, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 *  Thomas Heitz, 10/Mars/2010
 *
 *  $Id$
 */

package gate.util;

import gate.*;
import gate.util.persistence.PersistenceManager;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.*;
import java.util.*;

/**
 * Test an application against its previous run on a corpus.
 * If the results have changed more than fail.treshold parameter
 * then compare both previous and current application with the gold standard.
 */
public class TestApplication extends TestCase {

  public static Test suite() {
    return new TestSuite(TestApplication.class);
  }

  /**
  <pre>
   The config file includes:
   - the application to be run
   - three directories containing
     - a copy of the clean documents
       - these are annotated with the current application
       - the annotated version becomes a build artifact (which can be used
         to update the reference set, in the case of good changes)
     - a set of documents annotated with the previous version
     - the gold standard
   - for each such directory configuration includes the annotation set
     and annotation types to be used
  </pre>
  */
  @Override
  protected void setUp() throws Exception {

    // initialisations
    propertiesFile = new File("test.application.properties");
    properties = new Properties();
    annotationFeatures = new HashSet<String>();
    applicationAnnotationSet = "";
    goldAnnotationSet = "";
    failThreshold = 0.7;

    // read values from the properties file
    Out.prln();
    Out.prln("Load properties file: " + propertiesFile.getAbsolutePath());
    FileInputStream propertiesFileInputStream = null;
    try {
      propertiesFileInputStream = new FileInputStream(propertiesFile);
      properties.load(propertiesFileInputStream);
      String property = properties.getProperty("document.encoding");
      if (property != null && !property.equals("")) {
        documentEncoding = property.trim();
        Out.prln("Document encoding: " + documentEncoding);
      }
      property = properties.getProperty("application.file");
      if (property != null && !property.equals("")) {
        applicationFile = new File(property.trim());
        Out.prln("Application path: "+applicationFile.getAbsolutePath());
      } else {
        throw new IllegalArgumentException("application.file");
      }
      property = properties.getProperty("clean.documents.directory");
      if (property != null && !property.equals("")) {
        cleanDocumentsDirectory = new File(property.trim());
        Out.prln("Clean documents directory: "
          + cleanDocumentsDirectory.getAbsolutePath());
      } else {
        throw new IllegalArgumentException("clean.documents.directory");
      }
      property = properties.getProperty("previous.run.directory");
      if (property != null && !property.equals("")) {
        previousRunDirectory = new File(property.trim());
        Out.prln("Previous run directory: "
          + previousRunDirectory.getAbsolutePath());
      }
      property = properties.getProperty("gold.standard.directory");
      if (property != null && !property.equals("")) {
        goldStandardDirectory = new File(property.trim());
        Out.prln("Gold standard directory: "
          + goldStandardDirectory.getAbsolutePath());
      }
      property = properties.getProperty("application.annotation.set");
      if (property != null && !property.equals("")) {
        applicationAnnotationSet = property.trim();
        Out.prln("Application annotation set: " + applicationAnnotationSet);
      }
      property = properties.getProperty("gold.annotation.set");
      if (property != null && !property.equals("")) {
        goldAnnotationSet = property.trim();
        Out.prln("Gold annotation set: " + goldAnnotationSet);
      }
      property = properties.getProperty("annotation.types");
      if (property != null && !property.equals("")) {
        annotationTypes = new HashSet<String>(
          Arrays.asList(property.trim().split(", ?")));
        Out.prln("Annotation types: "
          + Arrays.toString(annotationTypes.toArray()));
      } else {
        throw new IllegalArgumentException("annotation.types");
      }
      property = properties.getProperty("annotation.features");
      if (property != null && !property.equals("")) {
        annotationFeatures = new HashSet<String>(
          Arrays.asList(property.trim().split(", ?")));
        Out.prln("Annotation features: "
          + Arrays.toString(annotationFeatures.toArray()));
      }
      property = properties.getProperty("fail.threshold");
      if (property != null && !property.equals("")) {
        failThreshold = Double.parseDouble(property.trim());
        Out.prln("Fail threshold: " + failThreshold);
      }
      property = properties.getProperty("results.file");
      if (property != null && !property.equals("")) {
        resultsFile = new File(property.trim());
        Out.prln("Results file: "+resultsFile.getAbsolutePath());
      }
      Out.prln();

    } catch (IOException e) {
      Out.prln("Could not load the properties file:");
      Out.prln("test.application.properties");
      Out.prln("The file must be in the directory from" +
        " where you run the test.");
      e.printStackTrace();

    } catch (IllegalArgumentException e) {
      Out.prln("Property " + e.getMessage() + " is empty or not defined!");

    } finally {
      if (propertiesFileInputStream != null) {
        propertiesFileInputStream.close();
      }
    }
  }

  @Override
  protected void tearDown() throws Exception {

    FileOutputStream propertiesFileOutputStream = null;
    try {
      propertiesFileOutputStream = new FileOutputStream(propertiesFile);
      properties.store(propertiesFileOutputStream,
" Property file for the JUnit TestApplication test.\n" +
"\n" +
"# the encoding for all the documents, optional\n" +
"#document.encoding=utf-8\n" +
"\n" +
"# the application to be run on the clean documents directory\n" +
"#application.file=/home/thomas/.tmp/gate/plugins/ANNIE/ANNIE_with_defaults.gapp\n" +
"\n" +
"# documents with only original markups\n" +
"# these are annotated with the current application\n" +
"#clean.documents.directory=/home/thomas/.tmp/corpus/gatecorpora/business/clean\n" +
"\n" +
"# the previous run documents directory that will be filled automatically\n" +
"# with the current run of the application on the clean documents\n" +
"# it can be empty the first time and then you will get no results\n" +
"#previous.run.directory=\n" +
"\n" +
"# annotated documents from the clean documents, optional\n" +
"#gold.standard.directory=/home/thomas/.tmp/corpus/gatecorpora/business/marked\n" +
"\n" +
"# annotation set, types and features\n" +
"# if an annotation set is empty then use the default set\n" +
"# only annotation.features is optional\n" +
"#application.annotation.set=\n" +
"#gold.annotation.set=Key\n" +
"#annotation.types=Location, Date, Person\n" +
"#annotation.features=\n" +
"\n" +
"# average F1 score threshold, between 0 and 1 to make the test failed\n" +
"# it also determines if the previous and current application must be\n" +
"# compared with the goldstandard, default value is 0.7\n" +
"# 0 will never fail\n" +
"# 1 will fail for any difference between documents\n" +
"#fail.threshold=0.9\n" +
"\n" +
"# file where to save the result in HTML format\n" +
"# if not set will use a temporary file\n" +
"#results.file=");
    }
    catch (IOException e) {
      Out.prln("Could not save the properties file:");
      Out.prln("test.application.properties");
      Out.prln("The file must be in the directory from" +
        " where you run the test.");
      e.printStackTrace();
    } finally {
      if (propertiesFileOutputStream != null) {
        propertiesFileOutputStream.close();
      }
    }
  }

  /**
   <pre>
   The logic is:
    - annotate the clean docs with the current version
    - perform anndif between current version and the previous version
    - if there are any differences:
      - perform anndif between current version and the GS
      - perform anndif between previous version and the GS
      - produce a report containing the evaluation numbers
        for the 3 diffs performed
      - produce a build artifact with the detailed changes
        (actual individual annotations that are different, in e.g. HTML format,
         similar to what CBT currently produces).
   </pre>
  */
  public void test() {

  Writer writer = null;
  try {
  // initialise GATE
  if (!Gate.isInitialised()) { Gate.init(); }

  // load the application
  Out.prln("Load the application");
  CorpusController controller = (CorpusController)
    PersistenceManager.loadObjectFromFile(applicationFile);
  controller.init();

  // create a corpus from the clean documents
  Corpus newCorpus = Factory.newCorpus("New corpus");
  FileFilter acceptAllFileFilter = new FileFilter() {
    @Override
    public boolean accept(File pathname) {
      return true;
    }
  };
  newCorpus.populate(cleanDocumentsDirectory.toURI().toURL(),
    acceptAllFileFilter, documentEncoding, false);

  // run the application on the clean documents
  Out.prln("Run the application on the clean "+newCorpus.size()+" documents");
  controller.setCorpus(newCorpus);
  controller.execute();

  // store the resulting documents in a temporary directory
  Out.prln("Save the documents processed in ");
  File temporaryDirectory = File.createTempFile("gate-test-application-", null);
  if (!temporaryDirectory.delete()
   || !temporaryDirectory.mkdir()) {
    throw new IOException("Unable to create temporary directory.\n"
      + temporaryDirectory.getCanonicalPath());
  }
  for (Object object : newCorpus) {
    Document document = (Document) object;
    writer = new BufferedWriter(new FileWriter(new File(temporaryDirectory,
      // use the same file name as the original document
      Files.fileFromURL(document.getSourceUrl()).getName())));
    writer.write(document.toXml());
    writer.close();
  }
  Out.prln(temporaryDirectory.getPath());

  // save the location of the previous run to be reuse later
  properties.put("previous.run.directory",
    temporaryDirectory.getCanonicalPath());

  // if previous directory exist and is not empty
  if (previousRunDirectory != null
   && previousRunDirectory.listFiles().length > 0) {

    // create the results file and write an HTML header
    if (resultsFile == null) {
      resultsFile = File.createTempFile(
        "gate-test-application-results-", ".html");
      properties.put("results.file", resultsFile.getCanonicalPath());
    }
    writer = new BufferedWriter(new FileWriter(resultsFile));
    writer.write(BEGINHTML + nl);
    writer.write(BEGINHEAD);
    writer.write("GATE Test Application");
    writer.write(ENDHEAD + nl);
    writer.write("<h1>GATE Test Application</h1>" + nl);
    writer.write("<p>Application: " + applicationFile.getPath() + "<br>" + nl);
    writer.write("Fail Threshold: " + failThreshold + "</p>" + nl);
    writer.write("<p>&nbsp;</p>" + nl);

    // compare documents annotations between the previous and new run
    Out.prln("Compare previous and new application");
    writer.write("<h2>Compare previous and new application</h2>" + nl);
    AnnotationDiffer annotationDiffer;
    List<AnnotationDiffer.Pairing> pairings =
      new ArrayList<AnnotationDiffer.Pairing>();
    double averageF1MeasurePerDocument = 0;
    for (Object object : newCorpus) {
      Document newDocument = (Document) object;
      writer.write("<h3>Document: " + newDocument.getName() + "</h3>" + nl);
      String fileName = Files.fileFromURL(newDocument.getSourceUrl()).getName();
      Document previousDocument = Factory.newDocument(new File(
        previousRunDirectory, fileName).toURI().toURL(), documentEncoding);
      double averageF1MeasurePerType = 0;
      for (String type : annotationTypes) {
        writer.write("<h4>Annotation type: " + type + "</h4>" + nl);
        annotationDiffer = new AnnotationDiffer();
        annotationDiffer.setSignificantFeaturesSet(annotationFeatures);
        pairings.clear();
        pairings.addAll(annotationDiffer.calculateDiff(
          previousDocument.getAnnotations(applicationAnnotationSet).get(type),
          newDocument.getAnnotations(applicationAnnotationSet).get(type)));
        if (annotationDiffer.getCorrectMatches()
         != annotationDiffer.getKeysCount()) {
          writer.write(printHTMLForPairings(pairings, newDocument));
        }
        averageF1MeasurePerType += annotationDiffer.getFMeasureStrict(1);
      }
      averageF1MeasurePerDocument +=
        averageF1MeasurePerType / annotationTypes.size();
      writer.write("Average F1 measure is "
        + averageF1MeasurePerType / annotationTypes.size() + "</p>" + nl);
    }
    averageF1MeasurePerDocument =
      averageF1MeasurePerDocument / newCorpus.size();
    Out.prln("Average F1 measure is " + averageF1MeasurePerDocument);

    // if different enough then
    if (averageF1MeasurePerDocument < failThreshold
     && goldStandardDirectory != null) {

      // compare previous with gold standard
      Out.prln("Compare previous application and gold standard");
      writer.write("<h2>Compare previous application and gold standard</h2>" + nl);
      for (Object object : newCorpus) {
        Document newDocument = (Document) object;
        writer.write("<h3>Document: " + newDocument.getName() + "</h3>" + nl);
        String fileName = Files.fileFromURL(
          newDocument.getSourceUrl()).getName();
        Document goldDocument = Factory.newDocument(new File(
          goldStandardDirectory, fileName).toURI().toURL(), documentEncoding);
        Document previousDocument = Factory.newDocument(new File(
          previousRunDirectory, fileName).toURI().toURL(), documentEncoding);
        for (String type : annotationTypes) {
          writer.write("<h4>Annotation type: " + type + "</h4>" + nl);
          annotationDiffer = new AnnotationDiffer();
          annotationDiffer.setSignificantFeaturesSet(annotationFeatures);
          pairings.clear();
          pairings.addAll(annotationDiffer.calculateDiff(
            goldDocument.getAnnotations(goldAnnotationSet).get(type),
            previousDocument.getAnnotations(applicationAnnotationSet).get(type)));
          if (annotationDiffer.getCorrectMatches()
           != annotationDiffer.getKeysCount()) {
            writer.write(printHTMLForPairings(pairings, newDocument));
          }
        }
      }

      // compare new with gold standard
      Out.prln("Compare new application and gold standard");
      writer.write("<h2>Compare new application and gold standard</h2>" + nl);
      for (Object object : newCorpus) {
        Document newDocument = (Document) object;
        writer.write("<h3>Document: " + newDocument.getName() + "</h3>" + nl);
        String fileName = Files.fileFromURL(
          newDocument.getSourceUrl()).getName();
        Document goldDocument = Factory.newDocument(new File(
          goldStandardDirectory, fileName).toURI().toURL(), documentEncoding);
        for (String type : annotationTypes) {
          writer.write("<h4>Annotation type: " + type + "</h4>" + nl);
          annotationDiffer = new AnnotationDiffer();
          annotationDiffer.setSignificantFeaturesSet(annotationFeatures);
          pairings.clear();
          pairings.addAll(annotationDiffer.calculateDiff(
            goldDocument.getAnnotations(goldAnnotationSet).get(type),
            newDocument.getAnnotations(applicationAnnotationSet).get(type)));
          if (annotationDiffer.getCorrectMatches()
           != annotationDiffer.getKeysCount()) {
            writer.write(printHTMLForPairings(pairings, newDocument));
          }
        }
      }
    }

    // write an HTML footer in the results file
    writer.write(ENDHTML + nl);
    Out.prln("Results have been written to " + resultsFile.getPath());

    if (averageF1MeasurePerDocument < failThreshold) {
      // this test fail
      fail("The average F1 measure is " + averageF1MeasurePerDocument
        + " which is inferior to the fail threshold " + failThreshold);
    }
  } else {
    Out.prln();
    Out.prln("Previous run directory is missing or empty.");
    Out.prln("You will need to run again this test with your new application");
    Out.prln("so it can compute the differences.");
  }

  } catch (GateException e) {
    e.printStackTrace();
  } catch (GateRuntimeException e) {
    e.printStackTrace();
  } catch (IOException e) {
    e.printStackTrace();
  } finally {
    if (writer != null) {
      try {
        writer.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
  }

  private String printHTMLForPairings(List<AnnotationDiffer.Pairing> pairings,
                                      Document document) {
    final String nl = Strings.getNl();
    StringBuilder builder = new StringBuilder();

    // table header
    builder.append("<table cellpadding=\"0\" border=\"1\">").append(nl);
    builder.append("<tr><th>Start</th><th>End</th><th>Key</th>");
    if (annotationFeatures != null && !annotationFeatures.isEmpty()) {
      builder.append("<th>Features</th>");
    }
    builder.append("<th>=?</th><th>Start</th><th>End</th><th>Response</th>");
    if (annotationFeatures != null && !annotationFeatures.isEmpty()) {
      builder.append("<th>Features</th>");
    }
    builder.append("</tr>").append(nl);

    // table content
    for (AnnotationDiffer.Pairing pairing : pairings) {
      if (pairing.getType() == AnnotationDiffer.CORRECT_TYPE) {
        continue;
      }
      Annotation key = pairing.getKey();
      if (key == null) {
        builder.append("<tr><td></td><td></td><td></td>");
        if (annotationFeatures != null && !annotationFeatures.isEmpty()) {
          builder.append("<td></td>");
        }
      } else {
        String keyString;
        try {
          keyString = document.getContent().getContent(
            key.getStartNode().getOffset(),
            key.getEndNode().getOffset()).toString();
        } catch(InvalidOffsetException e) {
          throw new LuckyException(e);
        }
        builder.append("<tr>")
          .append("<td>").append(key.getStartNode().getOffset().toString())
          .append("</td><td>").append(key.getEndNode().getOffset().toString())
          .append("</td><td>").append(keyString)
          .append("</td>");
        if (annotationFeatures != null && !annotationFeatures.isEmpty()) {
          builder.append("<td>")
            .append(key.getFeatures().toString()).append("</td>");
        }
      }
      String type = "";
      switch(pairing.getType()) {
        case AnnotationDiffer.CORRECT_TYPE: type = "="; break;
        case AnnotationDiffer.PARTIALLY_CORRECT_TYPE: type = "~"; break;
        case AnnotationDiffer.MISSING_TYPE: type = "-?"; break;
        case AnnotationDiffer.SPURIOUS_TYPE: type = "?-"; break;
        case AnnotationDiffer.MISMATCH_TYPE: type = "<>"; break;
      }
      builder.append("<td>").append(type).append("</td>");
      Annotation response = pairing.getResponse();
      if (response == null) {
        builder.append("<td></td><td></td><td></td>");
        if (annotationFeatures != null && !annotationFeatures.isEmpty()) {
          builder.append("<td></td>");
        }
      } else {
        String responseString;
        try {
          responseString = document.getContent().getContent(
            response.getStartNode().getOffset(),
            response.getEndNode().getOffset()).toString();
        } catch(InvalidOffsetException e) {
          throw new LuckyException(e);
        }
        builder
          .append("<td>").append(response.getStartNode().getOffset().toString())
          .append("</td><td>").append(response.getEndNode().getOffset().toString())
          .append("</td><td>").append(responseString).append("</td>");
        if (annotationFeatures != null && !annotationFeatures.isEmpty()) {
          builder.append("<td>")
            .append(response.getFeatures().toString()).append("</td>");
        }
      }
      builder.append("</tr>").append(nl);
    }

    builder.append("</table>").append(nl);
    builder.append("<p>&nbsp;</p>").append(nl);
    return builder.toString();
  }

  protected Properties properties;
  protected File propertiesFile;
  protected String documentEncoding;
  protected File applicationFile;
  protected File cleanDocumentsDirectory;
  protected File previousRunDirectory;
  protected File goldStandardDirectory;
  protected String applicationAnnotationSet;
  protected String goldAnnotationSet;
  protected Set<String> annotationTypes;
  protected Set<String> annotationFeatures;
  protected double failThreshold;
  protected File resultsFile;

  final String nl = Strings.getNl();
  static final String BEGINHTML =
    "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">" +
    "<html>";
  static final String ENDHTML = "</body></html>";
  static final String BEGINHEAD = "<head>" +
    "<meta content=\"text/html; charset=utf-8\" http-equiv=\"content-type\">"
    + "<title>";
  static final String ENDHEAD = "</title></head><body>";
}
