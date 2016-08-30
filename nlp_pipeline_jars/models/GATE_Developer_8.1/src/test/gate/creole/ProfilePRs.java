/*
 *  ProfilePRs.java
 *
 *  Copyright (c) 1995-2012, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 *  Kalina Bontcheva, 04/10/2001
 *
 *  $Id: ProfilePRs.java 17886 2014-04-20 09:46:53Z markagreenwood $
 */

package gate.creole;

import java.io.File;
import java.util.*;

import gate.*;
import gate.creole.gazetteer.DefaultGazetteer;
import gate.creole.orthomatcher.OrthoMatcher;
import gate.creole.splitter.SentenceSplitter;
import gate.creole.tokeniser.DefaultTokeniser;
import gate.util.GateException;
import gate.util.Out;
import gate.util.profile.Profiler;
//import java.text.NumberFormat;

/**
 * This class provides a main function that:
 * <UL>
 * <LI>
 * initialises the GATE library, and creates all PRs
 * <LI>
 * takes a directory name as argument
 * <LI>
 * for each .html file in that directory:
 * <BR>  create a GATE document from the file
 * <BR>  run the PRs on the document
 * <BR>  dump some statistics in the end
 * </UL>
 */
public class ProfilePRs {

  /** String to print when wrong command-line args */
  private static String usage =
    "usage: ProfilePRs [-dir directory-name | file(s)]";

  private static double totalDocLength = 0;
  private static int docs = 0;
  private static Profiler prof = new Profiler();
  private static double maxDocLength = 0;

  /** Main function */
  public static void main(String[] args) throws Exception {
    // say "hi"
    Out.prln("processing command line arguments");

    // check we have a directory name or list of files
    List<File> inputFiles = null;
    if(args.length < 1) throw new GateException(usage);
    if(args[0].equals("-dir")) { // list all the files in the dir
      if(args.length < 2) throw new GateException(usage);
      File dir = new File(args[1]);
      File[] filesArray = dir.listFiles();
      if(filesArray == null)
        throw new GateException(
          dir.getPath() + " is not a directory; " + usage
        );
      inputFiles = Arrays.asList(filesArray);
    } else { // all args should be file names
      inputFiles = new ArrayList<File>();
      for(int i = 0; i < args.length; i++)
        inputFiles.add(new File(args[i]));
    }

    prof.initRun("Measuring performance on directory " + args[1]);
//    prof.enable(false);
//    prof.enableGCCalling(false);

    // initialise GATE
    prof.checkPoint("Before GATE.init()");
    Gate.init();
    //tell GATE we're in batch mode
//    gate.Main.batchMode = true;


    // create some processing resources
    prof.checkPoint("Before creating the processing resources");

    //create a default tokeniser
    FeatureMap params = Factory.newFeatureMap();
    DefaultTokeniser tokeniser = (DefaultTokeniser) Factory.createResource(
                    "gate.creole.tokeniser.DefaultTokeniser", params);
    prof.checkPoint("Tokeniser initialised");

    //create a default gazetteer
    params = Factory.newFeatureMap();
    DefaultGazetteer gaz = (DefaultGazetteer) Factory.createResource(
                          "gate.creole.gazetteer.DefaultGazetteer", params);
    prof.checkPoint("Gazetteer initialised");

    //create a splitter
    params = Factory.newFeatureMap();
    SentenceSplitter splitter = (SentenceSplitter) Factory.createResource(
                          "gate.creole.splitter.SentenceSplitter", params);
    prof.checkPoint("Sentence splitter initialised");

    //create a tagger
    params = Factory.newFeatureMap();
    POSTagger tagger = (POSTagger) Factory.createResource(
                          "gate.creole.POSTagger", params);
    prof.checkPoint("POSTagger initialised");

    //create a grammar
    params = Factory.newFeatureMap();
    ANNIETransducer transducer = (ANNIETransducer) Factory.createResource(
                          "gate.creole.ANNIETransducer", params);
    prof.checkPoint("Grammars initialised");

    //create an orthomatcher
    params = Factory.newFeatureMap();
    OrthoMatcher orthomatcher = (OrthoMatcher) Factory.createResource(
                          "gate.creole.orthomatcher.OrthoMatcher", params);
    prof.checkPoint("Orthomatcher initialised");


    // for each document
    //   create a gate doc
    //   set as the document for hte PRs
    //   run the PRs
    //   dump output from the doc
    //   delete the doc
    Out.prln("\nLooping on input files list");
    Iterator<File> filesIter = inputFiles.iterator();
    docs = inputFiles.size();
    int fileNo=0;
    while(filesIter.hasNext()) {
      File inFile = filesIter.next(); // the current file
      fileNo++;

      // set the source URL parameter to a "file:..." URL string
      params.clear();
      params.put(Document.DOCUMENT_URL_PARAMETER_NAME, inFile.toURI().toURL().toExternalForm());
      params.put(Document.DOCUMENT_ENCODING_PARAMETER_NAME, "");

      // create the document
      Document doc = (Document) Factory.createResource(
        "gate.corpora.DocumentImpl", params
      );
      totalDocLength += doc.getContent().size().longValue();

      if (maxDocLength < doc.getContent().size().longValue())
        maxDocLength = doc.getContent().size().longValue();

      // set the document param on the PRs
      tokeniser.setDocument(doc);
      prof.checkPoint("Processing file " + inFile.getPath() +
          ", #" + fileNo + "/" + docs, new String[0], true, false, false);
      tokeniser.execute();
      prof.checkPoint("", new String[] {"Tokenizer", "Processing"}, false, false, false);

      //run gazetteer
      gaz.setDocument(doc);
      gaz.execute();
      prof.checkPoint("", new String[] {"Gazettier", "Processing"}, false, false, false);

      //run splitter
      splitter.setDocument(doc);
      splitter.execute();
      prof.checkPoint("", new String[] {"Splitter", "Processing"}, false, false, false);

      //run the tagger
      tagger.setDocument(doc);
      tagger.execute();
      prof.checkPoint("", new String[] {"Tagger", "Processing"}, false, false, false);

      //run the transducer
      transducer.setDocument(doc);
      transducer.execute();
      prof.checkPoint("", new String[] {"JAPE grammars", "Processing"}, false, false, false);

      // run the orthomatcher
      orthomatcher.setDocument(doc);
      orthomatcher.execute();
      prof.checkPoint("", new String[] {"Orthomatcher", "Processing"}, false, false, false);

      // make the doc a candidate for garbage collection
      Factory.deleteResource(doc);

    } // input files loop

    prof.checkPoint("Done!");

    totalDocLength = totalDocLength/1024;
    Out.prln("\nTotal KBytes processed: " + (long)totalDocLength);
    Out.prln("\nMax document size in bytes: " + (long)maxDocLength +
      " (" + (long) maxDocLength/1024 + " Kb)");


    prof.printCategAvg("Processing", docs, totalDocLength, "kb");
    prof.printCategAvg("Tokenizer", docs, totalDocLength, "kb");
    prof.printCategAvg("Gazettier", docs, totalDocLength, "kb");
    prof.printCategAvg("Splitter", docs, totalDocLength, "kb");
    prof.printCategAvg("Tagger", docs, totalDocLength, "kb");
    prof.printCategAvg("JAPE grammars", docs, totalDocLength, "kb");
    prof.printCategAvg("Orthomatcher", docs, totalDocLength, "kb");
  } // main


} // class ProfilePRs
