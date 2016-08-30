/*
 *  Copyright (c) 2006, The University of Sheffield.
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 *  Ian Roberts 30/10/2006
 *
 *  $Id: UpdateSavedApp.java 17530 2014-03-04 15:57:43Z markagreenwood $
 *
 */
package gate.util.persistence;

import java.io.*;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

/* (non-Javadoc)
 * Note that this class is not part of the GATE persistence API, but must be in
 * the gate.util.persistence package to have access to the package-private
 * class GateApplication.
 */

/**
 * <p>Handy command-line utility that loads a saved application state in the
 * old serialized-object format and resaves it in XML format.  Note that this
 * utility does not load the application into GATE and resave it, but merely
 * converts the persistent representation from one format to another.  If you
 * have an old-style saved state that will not load (because, for example, it
 * refers to a plugin that is not available) you can convert it to XML format
 * with this tool and then hand-edit the resulting XML to fix it.</p>
 *
 * <p>Usage: java -classpath &lt;gate.jar and lib/*.jar&gt;
 * gate.util.persistence.UpdateSavedApp &lt;oldFormatFile&gt;
 * &lt;newFormatFile&gt;</p>
 */
public class UpdateSavedApp {
  public static void main(String[] argv) throws Exception {
    if(argv.length < 2) {
      System.err.println("Usage:");
      System.err.println("  UpdateSavedApp <oldFile> <newFile>");
      System.exit(1);
    }
    
    File oldFile = new File(argv[0]);
    File newFile = new File(argv[1]);

    // make sure not to clobber an existing file
    if(newFile.exists()) {
      System.err.println(newFile + " already exists.");
      System.err.println("Please move it out of the way.  This tool will "
          + "not overwrite an existing file,");
      System.err.println("in particular the new file must not be the same as "
          + "the old.");
      System.exit(1);
    }

    // open old file for reading
    FileInputStream fis = new FileInputStream(oldFile);
    BufferedInputStream bis = new BufferedInputStream(fis);
    ObjectInputStream ois = new ObjectInputStream(bis);


    // load URL list and app from old file
    Object oldUrlList = ois.readObject();
    Object obj = ois.readObject();

    // close input stream
    ois.close();

    // put them together in a GateApplication
    GateApplication persistApp = new GateApplication();
    persistApp.urlList = oldUrlList;
    persistApp.application = obj;

    // create XStream for writing new file
    XStream xs = new XStream(new StaxDriver());

    // save XML application
    FileWriter fw = new FileWriter(newFile);
    BufferedWriter bw = new BufferedWriter(fw);
    xs.toXML(persistApp, bw);

    bw.close();
  }
}
