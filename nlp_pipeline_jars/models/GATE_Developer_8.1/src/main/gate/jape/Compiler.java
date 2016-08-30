/*
 *  Compiler.java - compile .jape files
 *
 *  Copyright (c) 1995-2012, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 *  Hamish Cunningham, 23/02/2000
 *
 *  $Id: Compiler.java 17530 2014-03-04 15:57:43Z markagreenwood $
 */

package gate.jape;

import java.io.*;
import java.net.URL;
import gate.Gate;
import gate.creole.Transducer;

/**
 * A utility class for compilig JAPE files.
 * Take a list of .jape files names and compile them to .ser.
 * Also recognises a -v option which makes it chatty.
 * Note that this is provided as a command line tool. If you
 * wish to serialize JAPE grammars from code you should use
 * the Transducer's serialize method instead.
 */
public class Compiler {

  private static String defaultEncoding = "UTF-8";

  /**
   * Take a list of .jape files names and compile them to .ser.
   */
  public static void main(String[] args) throws Exception {

    boolean verbose = false;

    int argsIndex = 0;
    while(args[argsIndex].toCharArray()[0] == '-') {
      if(args[argsIndex++].equals("-v")) {
        verbose = true;
      }
    }

    if (verbose) System.out.print("Initializing GATE...");
    Gate.runInSandbox(true);
	Gate.init();
    if (verbose) System.out.println(" Done");


 	for( ; argsIndex<args.length; argsIndex++) {
	  File in = new File(args[argsIndex]);
	  if (verbose) System.out.print("Compiling " + in.getAbsolutePath()+"... ");
	  compile(in.toURI().toURL(),serFromJape(in));
	  if (verbose) System.out.println("Done");
    }
  }

  /**
   * Loads the specified JAPE grammar into GATE and then serializes it
   * into the specified file. This file can then be used to create
   * a new transducer instance via the <code>binaryGrammarURL</code>
   * init time parameter.
   */
  public static void compile(URL jape, File ser) throws Exception {

    Transducer transducer = new Transducer();
    transducer.setGrammarURL(jape);
    transducer.setEncoding(defaultEncoding);
    transducer.init();

    FileOutputStream out = new FileOutputStream(ser);
	ObjectOutputStream s = new ObjectOutputStream(out);
	transducer.serialize(s);
	s.flush();
	s.close();
    out.close();
  }

  /**
   * Convert a .jape file name to a .ser file name.
   */
  private static File serFromJape(File jape) {
    String base = jape.getAbsolutePath();
    if(base.toLowerCase().endsWith(".jape"))
      base = base.substring(0, base.length() - 5);
    return new File(base + ".ser");
  }
}