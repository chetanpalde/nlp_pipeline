/*
 *  CorpusSaver.java
 *
 *  Copyright (c) 1995-2012, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 *  Kalina Bontcheva, 22/Nov/2001
 *
 *  $Id: CorpusSaver.java 17662 2014-03-14 16:19:05Z markagreenwood $
 */

package gate.util;

import java.io.File;
import java.text.NumberFormat;
import java.util.*;

import gate.*;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;

public class CorpusSaver {

  public CorpusSaver() {
  }

  public void init() {
    if (saveMode) {
      File path = new File(dsPath);
      try {
       ds = Factory.openDataStore("gate.persist.SerialDataStore",
                                  path.toURI().toURL().toString());
      } catch (Exception ex) {
        throw new gate.util.GateRuntimeException(ex.getMessage());
      }

      try {
        Corpus corpus = Factory.newCorpus("bnc");
        LanguageResource lr = ds.adopt(corpus);
        ds.sync(lr);
        theCorpus = (Corpus) lr;
      } catch (Exception ex) {
        throw new GateRuntimeException(ex.getMessage());
      }
    }

    if (processMode)
      initPRs();

  }

  public void initPRs() {
    if (applicationFile == null)
      throw new GateRuntimeException("Application not set!");
    
    try {
      Out.prln("App file is: " + applicationFile.getAbsolutePath());
      application = (Controller) gate.util.persistence.PersistenceManager
                    .loadObjectFromFile(applicationFile);
    }
    catch (Exception ex) {
      throw new GateRuntimeException("Corpus Benchmark Tool:" + ex.getMessage(), ex);
    }
  }//initPRs

  public void execute() {
    execute(startDir);
    try {
      if (saveMode) {
        ds.sync(theCorpus);
        Factory.deleteResource(theCorpus);
        if (ds != null)
          ds.close();
      }
      if (application != null) {
        Iterator<ProcessingResource> iter = new ArrayList<ProcessingResource>(application.getPRs()).iterator();
        while (iter.hasNext())
          Factory.deleteResource(iter.next());
      }
    } catch (Exception ex) {
      throw new GateRuntimeException(ex.getMessage());
    }
  }

  public void execute(File dir) {
    if (dir == null || (saveMode && ds == null))
      return;
    //first set the current directory to be the given one
    currDir = dir;
    Out.prln("Processing directory: " + currDir);

    List<File> files = new ArrayList<File>();
    List<File> dirs = new ArrayList<File>();
    File[] dirArray = currDir.listFiles();
    for (int i = 0; i < dirArray.length; i++) {
      if (dirArray[i].isDirectory())
        dirs.add(dirArray[i]);
      else if (dirArray[i].isFile())
        files.add(dirArray[i]);
    }

    saveFiles(files);

    //if no more subdirs left, return
    if (dirs.isEmpty())
      return;

    //there are more subdirectories to traverse, so iterate through
    for (int j = 0; j < dirs.size(); j++)
      execute(dirs.get(j));

  }//execute(dir)


  public static void main(String[] args) throws GateException {
    Gate.init();

//MainFrame mFramew = new MainFrame();
//mFramew.setSize(800, 600);
//mFramew.setVisible(true);
    
    CorpusSaver corpusSaver1 = new CorpusSaver();

    if(args.length < 2)
      throw new GateException("usage: [-process|-process_only] source_directory datastore_path application");
    int i = 0;
    while (i < args.length && args[i].startsWith("-")) {
      if(args[i].equals("-process")) {
        Out.prln("Processing and saving the corpus enabled. <P>");
        corpusSaver1.setProcessMode(true);
      } else if (args[i].equals("-process_only")) {
        Out.prln("Processing only enabled. <P>");
        corpusSaver1.setSaveMode(false);
        corpusSaver1.setProcessMode(true);
      }
      i++; //just ignore the option, which we do not recognise
    }//while

    String dirName = args[i];
    File dir = new File(dirName);
    if (!dir.isDirectory())
      throw new GateRuntimeException("Corpus directory should be "
                                     + "provided as a parameter");
    if(corpusSaver1.getSaveMode()){
      i++;
      if( i >= args.length)
        throw new GateRuntimeException("Datastore path not provided");
  
      if (corpusSaver1.getSaveMode()) {
        String storagePath = args[i];
        File storage = new File(storagePath);
        if (!storage.isDirectory())
          throw new GateRuntimeException("Please provide path to an existing "
                                         + "GATE serial datastore");
        corpusSaver1.setDSPath(storagePath);
      }
    }
    
    //get the last argument which is the application
    if (corpusSaver1.getProcessMode()) {
      i++;
      String appName = args[i];
      File appFile = new File(appName);
      if (!appFile.isFile())
        throw new GateException("Please provide an existing GATE application");
      else
        corpusSaver1.setApplicationFile(appFile);
    }

    Out.prln("Initialising GATE please wait...");
    corpusSaver1.init();
    corpusSaver1.setStartDir(dir);
    Out.prln("Processing...");
    double timeBefore = System.currentTimeMillis();
    corpusSaver1.execute();
    double timeAfter = System.currentTimeMillis();
    Out.prln("Done in " +
      NumberFormat.getInstance().format((timeAfter-timeBefore)/1000)
      + " seconds");

  }

  public void setStartDir(File newDir) {
    startDir = newDir;
  }

  public void setProcessMode(boolean mode) {
    processMode = mode;
  }

  public boolean getProcessMode() {
    return processMode;
  }

  public void setSaveMode(boolean mode) {
    saveMode = mode;
  }

  public boolean getSaveMode() {
    return saveMode;
  }

  public void setDSPath(String path){
    dsPath = path;
  }

  public void setApplicationFile(File newAppFile) {
    applicationFile = newAppFile;
  }


  protected void saveFiles(List<File> files) {
    if (files==null || files.isEmpty() ||
        (saveMode && (theCorpus == null || ds == null)))
      return;

    for(int i=0; i<files.size(); i++) {
      try {
        Document doc = Factory.newDocument(files.get(i).toURI().toURL());
        doc.setName(Files.getLastPathComponent(files.get(i).toURI().toURL().toString()));
        Out.prln("Storing document: " + doc.getName());
        //first process it with ANNIE if in process mode
        if (processMode)
          processDocument(doc);

        //then store it in the DS and add to corpus
        if (saveMode) {
          Document lr = (Document)ds.adopt(doc);
          theCorpus.add(lr);
          theCorpus.unloadDocument( lr);

          if (lr != doc)
            Factory.deleteResource(lr);
        }
        Factory.deleteResource(doc);
      } catch (Exception ex) {
        throw new GateRuntimeException(ex.getClass() + " " + ex.getMessage());
      }
    }//for
  }//saveFiles

  protected void processDocument(Document doc) {
    try {
      if (application instanceof CorpusController) {
        Corpus tempCorpus = Factory.newCorpus("temp");
        tempCorpus.add(doc);
        ((CorpusController)application).setCorpus(tempCorpus);
        application.execute();
        Factory.deleteResource(tempCorpus);
        tempCorpus = null;
      } else {
        Iterator<ProcessingResource> iter = application.getPRs().iterator();
        while (iter.hasNext())
          iter.next().setParameterValue("document", doc);
        application.execute();
      }
    } catch (ResourceInstantiationException ex) {
      throw new RuntimeException("Error executing application: "
                                    + ex.getMessage());
    } catch (ExecutionException ex) {
      throw new RuntimeException("Error executing application: "
                                    + ex.getMessage());
    }
  }


  /**
   * The directory from which we should generate/evaluate the corpus
   */
  private File startDir;
  private File currDir;

  private DataStore ds;
  private Corpus theCorpus;
  private String dsPath = "d:\\bnc";
  private Controller application = null;
  private File applicationFile = null;

  private boolean processMode = false;
  private boolean saveMode = true;
}
