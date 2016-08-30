/*
 *  ProgressPrinter.java
 *
 *  Copyright (c) 1995-2012, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 *  Valentin Tablan, 21/07/2000
 *
 *  $Id: ProgressPrinter.java 17600 2014-03-08 18:47:11Z markagreenwood $
 */

package gate.util;

import java.io.PrintStream;

import gate.event.ProgressListener;


/**
 * Class used to simulate the behaviour of a progress bar on an OutputStream.
 *
 */
public class ProgressPrinter implements ProgressListener {

  /**
   * Constructor.
   *
   * @param out the stream used for output
   * @param numberOfSteps the number of steps until the process is over (the
   *     number of characters printed for a full run)
   */
  public ProgressPrinter(PrintStream out, int numberOfSteps) {
    this.out = out;
    this.numberOfSteps = numberOfSteps;
  }

  /**
   * Constructor. Uses the default number of steps.
   *
   * @param out
   */
  public ProgressPrinter(PrintStream out) {
    this.out = out;
  }

  @Override
  public void processFinished() {
    for(int i = currentValue; i < numberOfSteps; i++) {
      out.print("#");
    }
    out.println("]");
    currentValue = 0;
    started = false;
  }

  @Override
  public void progressChanged(int newValue) {
    if(!started){
      out.print("[");
      started = true;
    }
    newValue = newValue * numberOfSteps / 100;
    if(newValue > currentValue){
      for(int i = currentValue; i < newValue; i++) {
        out.print("#");
      }
      currentValue = newValue;
    }
  }

  /**    *
   */
  int currentValue = 0;

  /**    *
   */
  int numberOfSteps = 70;

  /**    */
  PrintStream out;

  /**    */
  boolean started = false;

} // class ProgressPrinter
