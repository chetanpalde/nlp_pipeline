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
 *  $Id: VPChunker.java 17588 2014-03-08 07:50:36Z markagreenwood $
 */
package gate.creole;

import gate.creole.metadata.CreoleParameter;
import gate.creole.metadata.CreoleResource;
import gate.creole.metadata.HiddenCreoleParameter;
import gate.creole.ontology.Ontology;
import java.net.URL;
import java.util.List;

/**
 * ANNIE VP Chunker module. It is actually a JAPE grammar; this class is here
 * so we can have a separate entry in creol.xml in order to point to the default
 * VP chunking grammar.
 */
@CreoleResource(name = "ANNIE VP Chunker",
  comment = "ANNIE VP Chunker component.",
  helpURL = "http://gate.ac.uk/userguide/sec:parsers:vgchunker",
  icon = "pr"
  )
public class VPChunker extends Transducer {

  private static final long serialVersionUID = 5829148669995600034L;

  @HiddenCreoleParameter
  @Override
  public void setOntology(Ontology o) {
    super.setOntology(o);
  }

  @HiddenCreoleParameter
  @Override
  public void setBinaryGrammarURL(URL grammar) {
    super.setBinaryGrammarURL(grammar);
  }

  @HiddenCreoleParameter
  @Override
  public void setAnnotationAccessors(List<String> accessors) {
    super.setAnnotationAccessors(accessors);
  }

  @HiddenCreoleParameter
  @Override
  public void setOperators(List<String> operators) {
    super.setOperators(operators);
  }

  /**
   * The grammarURL parameter provides the ANNIE VerbGroups.jape file as a default
   * for this PR.
   *
   * @param newGrammarURL
   */
  @CreoleParameter(
    comment = "The URL to the grammar file.",
    suffixes = "jape",
    defaultValue = "../ANNIE/resources/VP/VerbGroups.jape"
  )
  @Override
  public void setGrammarURL(java.net.URL newGrammarURL) {
    super.setGrammarURL(newGrammarURL);
  }

}