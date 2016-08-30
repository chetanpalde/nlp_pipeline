package gate.creole.tokeniser;

import gate.Factory;
import gate.FeatureMap;
import gate.Gate;
import gate.Resource;
import gate.creole.AbstractLanguageAnalyser;
import gate.creole.ExecutionException;
import gate.creole.ExecutionInterruptedException;
import gate.creole.ResourceInstantiationException;
import gate.creole.Transducer;
import gate.creole.metadata.CreoleParameter;
import gate.creole.metadata.CreoleResource;
import gate.creole.metadata.Optional;
import gate.creole.metadata.RunTime;
import gate.event.ProgressListener;
import gate.event.StatusListener;
import gate.util.Benchmark;
import gate.util.Benchmarkable;
import gate.util.Out;

/**
 * A composed tokeniser containing a {@link SimpleTokeniser} and a
 * {@link gate.creole.Transducer}.
 * The simple tokeniser tokenises the document and the transducer processes its
 * output.
 */
@CreoleResource(name = "ANNIE English Tokeniser", comment = "A customisable English tokeniser.", helpURL = "http://gate.ac.uk/userguide/sec:annie:tokeniser", icon = "tokeniser")
public class DefaultTokeniser extends AbstractLanguageAnalyser implements Benchmarkable {

  private static final long serialVersionUID = 3860943928124433852L;

  public static final String
    DEF_TOK_DOCUMENT_PARAMETER_NAME = "document";

  public static final String
    DEF_TOK_ANNOT_SET_PARAMETER_NAME = "annotationSetName";

  public static final String
    DEF_TOK_TOKRULES_URL_PARAMETER_NAME = "tokeniserRulesURL";

  public static final String
    DEF_TOK_GRAMRULES_URL_PARAMETER_NAME = "transducerGrammarURL";

  public static final String
    DEF_TOK_ENCODING_PARAMETER_NAME = "encoding";

  public DefaultTokeniser() {
  }


  /** Initialise this resource, and return it. */
  @Override
  public Resource init() throws ResourceInstantiationException{
    try{
      //init super object
      super.init();
      //create all the componets
      FeatureMap params;
      FeatureMap features;

      params = Factory.newFeatureMap();
      if(tokeniserRulesURL != null)
        params.put(SimpleTokeniser.SIMP_TOK_RULES_URL_PARAMETER_NAME,
                   tokeniserRulesURL);
      params.put(SimpleTokeniser.SIMP_TOK_ENCODING_PARAMETER_NAME, encoding);

      if (tokeniser == null) {
        //tokeniser
        fireStatusChanged("Creating a tokeniser");
        if(DEBUG) Out.prln("Parameters for the tokeniser: \n" + params);
        features = Factory.newFeatureMap();
        Gate.setHiddenAttribute(features, true);
        tokeniser = (SimpleTokeniser)Factory.createResource(
                "gate.creole.tokeniser.SimpleTokeniser",
                params, features);
        tokeniser.setName("Tokeniser " + System.currentTimeMillis());
      }
      else {
        tokeniser.setParameterValues(params);
        tokeniser.reInit();
      }
      
      fireProgressChanged(50);

      params = Factory.newFeatureMap();
      if(transducerGrammarURL != null)
        params.put(Transducer.TRANSD_GRAMMAR_URL_PARAMETER_NAME,
                transducerGrammarURL);
      params.put(Transducer.TRANSD_ENCODING_PARAMETER_NAME, encoding);

      if (transducer == null) {
        //transducer
        fireStatusChanged("Creating a Jape transducer");
        if(DEBUG) Out.prln("Parameters for the transducer: \n" + params);
        features = Factory.newFeatureMap();
        Gate.setHiddenAttribute(features, true);
        transducer = (Transducer)Factory.createResource("gate.creole.Transducer",
                params, features);
        transducer.setName("Transducer " + System.currentTimeMillis());
      }
      else {
        transducer.setParameterValues(params);
        transducer.reInit();
      }
      fireProgressChanged(100);
      fireProcessFinished();
      
    }catch(ResourceInstantiationException rie){
      throw rie;
    }catch(Exception e){
      throw new ResourceInstantiationException(e);
    }
    return this;
  }
  
  @Override
  public void cleanup() {
    Factory.deleteResource(transducer);
    Factory.deleteResource(tokeniser);
  }

  @Override
  public void execute() throws ExecutionException {
    interrupted = false;

    FeatureMap params = null;
    fireProgressChanged(0);

    ProgressListener pListener = null;
    StatusListener sListener = null;

    try {

      // tokeniser
      params = Factory.newFeatureMap();
      params.put(SimpleTokeniser.SIMP_TOK_DOCUMENT_PARAMETER_NAME, document);
      params.put(SimpleTokeniser.SIMP_TOK_ANNOT_SET_PARAMETER_NAME,
              annotationSetName);
      tokeniser.setParameterValues(params);

      pListener = new IntervalProgressListener(0, 50);
      sListener = new StatusListener() {
        @Override
        public void statusChanged(String text) {
          fireStatusChanged(text);
        }
      };

      tokeniser.addProgressListener(pListener);
      tokeniser.addStatusListener(sListener);

      Benchmark.executeWithBenchmarking(tokeniser,
              Benchmark.createBenchmarkId("simpleTokeniser", getBenchmarkId()),
              this, null);

    } catch(Exception e) {
      throw new ExecutionException("The execution of the \"" + getName()
              + "\" tokeniser has been abruptly interrupted!", e);
    } finally {
      tokeniser.removeProgressListener(pListener);
      tokeniser.removeStatusListener(sListener);
      tokeniser.setDocument(null);
    }

    if(isInterrupted())
      throw new ExecutionInterruptedException("The execution of the \""
              + getName() + "\" tokeniser has been abruptly interrupted!");

    try {
      // transducer
      params = Factory.newFeatureMap();
      params.put(Transducer.TRANSD_DOCUMENT_PARAMETER_NAME, document);
      params.put(Transducer.TRANSD_INPUT_AS_PARAMETER_NAME, annotationSetName);
      params.put(Transducer.TRANSD_OUTPUT_AS_PARAMETER_NAME, annotationSetName);
      transducer.setParameterValues(params);

      pListener = new IntervalProgressListener(50, 100);
      transducer.addProgressListener(pListener);
      transducer.addStatusListener(sListener);

      Benchmark.executeWithBenchmarking(transducer,
              Benchmark.createBenchmarkId("transducer", getBenchmarkId()),
              this, null);

    } catch(Exception e) {
      throw new ExecutionException("The execution of the \"" + getName()
              + "\" tokeniser has been abruptly interrupted!", e);
    } finally {
      transducer.removeProgressListener(pListener);
      transducer.removeStatusListener(sListener);
      transducer.setDocument(null);
    }
  }// execute


  /**
   * Notifies all the PRs in this controller that they should stop their
   * execution as soon as possible.
   */
  @Override
  public synchronized void interrupt(){
    interrupted = true;
    tokeniser.interrupt();
    transducer.interrupt();
  }

  @CreoleParameter(defaultValue="resources/tokeniser/DefaultTokeniser.rules", comment="The URL to the rules file", suffixes="rules")
  public void setTokeniserRulesURL(java.net.URL tokeniserRulesURL) {
    this.tokeniserRulesURL = tokeniserRulesURL;
  }
  public java.net.URL getTokeniserRulesURL() {
    return tokeniserRulesURL;
  }
  
  @CreoleParameter(defaultValue="UTF-8", comment="The encoding used for reading the definitions")
  public void setEncoding(String encoding) {
    this.encoding = encoding;
  }
  public String getEncoding() {
    return encoding;
  }
  
  @CreoleParameter(defaultValue="resources/tokeniser/postprocess.jape", comment="The URL to the postprocessing transducer", suffixes="jape")
  public void setTransducerGrammarURL(java.net.URL transducerGrammarURL) {
    this.transducerGrammarURL = transducerGrammarURL;
  }
  public java.net.URL getTransducerGrammarURL() {
    return transducerGrammarURL;
  }
 // init()

  private static final boolean DEBUG = false;

  /** the simple tokeniser used for tokenisation*/
  protected SimpleTokeniser tokeniser;

  /** the transducer used for post-processing*/
  protected Transducer transducer;
  private java.net.URL tokeniserRulesURL;
  private String encoding;
  private java.net.URL transducerGrammarURL;
  private String annotationSetName;
  private String benchmarkId;

  @RunTime
  @Optional
  @CreoleParameter(comment="The annotation set to be used for the generated annotations")
  public void setAnnotationSetName(String annotationSetName) {
    this.annotationSetName = annotationSetName;
  }
  public String getAnnotationSetName() {
    return annotationSetName;
  }
  
  @Override
  public void setBenchmarkId(String benchmarkId) {
    this.benchmarkId = benchmarkId;
  }
  
  @Override
  public String getBenchmarkId() {
    if(benchmarkId == null) {
      return getName();
    }
    else {
      return benchmarkId;
    }
  }
}
