/*
 *  Copyright (c) 2010--2014, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 *  $Id: CsvGenerator.java 17718 2014-03-20 20:40:06Z adamfunk $
 */
package gate.termraider.output;

import gate.termraider.bank.AbstractBank;
import gate.termraider.bank.AbstractTermbank;
import gate.termraider.util.Term;
import gate.util.GateException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;


public class CsvGenerator {
  
  public static void generateAndSaveCsv(AbstractTermbank bank, 
          Number threshold, File outputFile) throws GateException {
    PrintWriter writer = initializeWriter(outputFile);
    addComment(bank, "threshold = " + threshold);
    List<Term> sortedTerms = bank.getTermsByDescendingScore();
    
    addComment(bank, "Unfiltered nbr of terms = " + sortedTerms.size());
    int written = 0;
    writer.println(bank.getCsvHeader());
    
    for (Term term : sortedTerms) {
      Number score = bank.getDefaultScores().get(term);
      if (score.doubleValue() >= threshold.doubleValue()) {
        writer.println(bank.getCsvLine(term));
        written++;
      }
      else {  // the rest must be lower
        break;
      }
    }
    addComment(bank, "Filtered nbr of terms = " + written);
  }

  
  private static void addComment(AbstractBank termbank, String commentStr) {
    if (termbank.getDebugMode()) {
      System.out.println(commentStr);
    }
  }
  
  
  private static PrintWriter initializeWriter(File outputFile) throws GateException {
    try {
      return new PrintWriter(outputFile);
    } 
    catch(FileNotFoundException e) {
      throw new GateException(e);
    }
  }
  
}
