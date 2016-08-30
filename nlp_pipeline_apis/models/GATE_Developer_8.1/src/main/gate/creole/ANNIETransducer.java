/*
 *  Copyright (c) 1995-2012, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 *  Valentin Tablan, 20 Sep 2001
 *
 *  $Id: ANNIETransducer.java 17604 2014-03-09 10:08:13Z markagreenwood $
 */
package gate.creole;

import gate.creole.metadata.CreoleParameter;
import gate.creole.metadata.CreoleResource;
import gate.creole.metadata.HiddenCreoleParameter;
import java.net.URL;

/**
 * The ANNIE named entity transducer.
 * This is a JAPE transducer and this class is here to allow the specification
 * in creole.xml of a default grammar to be used in .
 */
@CreoleResource(name = "ANNIE NE Transducer",
  comment = "ANNIE named entity grammar.",
  helpURL = "http://gate.ac.uk/userguide/sec:annie:semantic-tagger",
  icon = "ne-transducer"
  )
public class ANNIETransducer extends Transducer {

  private static final long serialVersionUID = 7443615855994597034L;

  /**
   * The ontology parameter is not used for this PR and therefore hidden.
   * 
   * @param ontology
   */
  @HiddenCreoleParameter
  @Override
  public void setOntology(gate.creole.ontology.Ontology ontology) {
    super.setOntology(ontology);
  }

  /**
   * The binaryGrammarURL parameter is not used for this PR and therefore hidden.
   * 
   * @param url
   */
  @HiddenCreoleParameter
  @Override
  public void setBinaryGrammarURL(URL url) {
    super.setBinaryGrammarURL(url);
  }


  /**
   * The grammarURL parameter provides the ANNIE main.jape file as a default
   * for this PR.
   * 
   * @param newGrammarURL
   */
  @CreoleParameter(
    comment = "The URL to the grammar file.",
    suffixes = "jape",
    defaultValue = "resources/NE/main.jape"
  )
  @Override
  public void setGrammarURL(java.net.URL newGrammarURL) {
    super.setGrammarURL(newGrammarURL);
  }

}