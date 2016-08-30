/*
 *  Copyright (c) 2010--2014, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 *  $Id: AbstractBank.java 17718 2014-03-20 20:40:06Z adamfunk $
 */

package gate.termraider.bank;

import gate.Annotation;
import gate.Corpus;
import gate.Document;
import gate.creole.AbstractLanguageResource;
import gate.creole.metadata.CreoleParameter;
import gate.termraider.util.Term;
import gate.util.GateException;
import java.io.File;
import java.util.Set;


/**
 * A thing that has a score name, can be saved as CSV, and 
 * can be used to generate a SliderPanel (which needs 
 * min & max scores).
 */
public abstract class AbstractBank extends AbstractLanguageResource {

  private static final long serialVersionUID = -7924866363771311062L;
  
  protected Set<String> languages, types;
  protected int documentCount;
  
  public abstract Number getMinScore();
  
  public abstract Number getMaxScore();
  
  public int getDocumentCount() {
    return this.documentCount;
  }
  
  public abstract void saveAsCsv(Number threshold, File file)
    throws GateException;

  public abstract void saveAsCsv(File file)
    throws GateException;
  
  public Set<String> getLanguages() {
    return this.languages;
  }
  
  public Set<String> getTypes() {
    return this.types;
  }
  
  public Term makeTerm(Annotation annotation, Document document) {
    return new Term(annotation, document, 
            this.languageFeature, this.inputAnnotationFeature);
  }

  
  /* CREOLE */
  
  protected String scoreProperty;
  protected String languageFeature;
  protected String inputAnnotationFeature;
  protected Set<Corpus> corpora;
  protected boolean debugMode;
  protected String inputASName;




  /* Default value is overridden in the implementations   */
  @CreoleParameter(comment = "name of main score",
          defaultValue = "score")
  public void setScoreProperty(String name) {
    this.scoreProperty = name;
  }

  public String getScoreProperty() {
    return this.scoreProperty;
  }
  
  
  @CreoleParameter(comment = "language feature on term candidates",
          defaultValue = "lang")
  public void setLanguageFeature(String name) {
    this.languageFeature = name;
  }
  public String getLanguageFeature() {
    return this.languageFeature;
  }
  
  
  @CreoleParameter(comment = "input annotation feature",
          defaultValue = "canonical")
  public void setInputAnnotationFeature(String name) {
    this.inputAnnotationFeature = name;
  }
  public String getInputAnnotationFeature() {
    return this.inputAnnotationFeature;
  }
  
  @CreoleParameter(comment = "Processed corpora to analyse")
  public void setCorpora(Set<Corpus> corpora) {
    this.corpora = corpora;
  }

  public Set<Corpus> getCorpora() {
    return this.corpora;
  }

  @CreoleParameter(comment = "print debugging information during initialization",
          defaultValue = "false")
  public void setDebugMode(Boolean debug) {
    this.debugMode = debug;
  }

  public Boolean getDebugMode() {
    return this.debugMode;
  }
  
  @CreoleParameter(comment = "input AS name",
          defaultValue = "")
  public void setInputASName(String name) {
    this.inputASName = name;
  }
  public String getInputASName() {
    return this.inputASName;
  }
  
}
