/*
 *  TextCategorizationPR.java
 *
 *  Copyright (c) 1995-2013, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 *  Valentin Tablan, 19 Nov 2013
 *
 *  $Id: TextCategorizationPR.java 18734 2015-06-01 16:16:42Z ian_roberts $
 */
package gate.ml.categorization;

import de.bwaldvogel.liblinear.Feature;
import de.bwaldvogel.liblinear.FeatureNode;
import de.bwaldvogel.liblinear.Linear;
import de.bwaldvogel.liblinear.Model;
import edu.ucla.sspace.common.DocumentVectorBuilder;
import edu.ucla.sspace.common.SemanticSpace;
import edu.ucla.sspace.common.SemanticSpaceIO;
import edu.ucla.sspace.vector.DenseVector;
import edu.ucla.sspace.vector.DoubleVector;
import gate.Annotation;
import gate.AnnotationSet;
import gate.Factory;
import gate.FeatureMap;
import gate.LanguageAnalyser;
import gate.Resource;
import gate.Utils;
import gate.creole.AbstractLanguageAnalyser;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;
import gate.creole.metadata.CreoleParameter;
import gate.creole.metadata.CreoleResource;
import gate.creole.metadata.Optional;
import gate.creole.metadata.RunTime;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * A simple text classification PR, using a <a 
 * href="https://github.com/fozziethebeat/S-Space/">Semantic Space</a>
 * implementation to generate vectors for the annotations to be classified, 
 * and a <a href="http://www.csie.ntu.edu.tw/~cjlin/liblinear">LIBLINEAR</a> 
 * model to perform the actual classification.
 */
@CreoleResource(name = "Text Categorization PR",
    comment = "Classify text based on a semantic space")
public class TextCategorizationPR extends AbstractLanguageAnalyser implements
                                                                   LanguageAnalyser {
  
  private static Logger logger = Logger.getLogger(TextCategorizationPR.class);
  
  /**
   * Serialisation UID.
   */
  private static final long serialVersionUID = 141243024466478134L;

  /**
   * The semantic space used to generate 'document' feature vectors for the 
   * annotations to be classified.
   */
  protected SemanticSpace semanticSpace;
  
  protected DocumentVectorBuilder vectorBuilder;
  
  /**
   * The LibLinear model used for classification.
   */
  protected Model libLinearModel;
  

  /**
   * Thresholds to be used when classifying text annotations. If the model 
   * classifies an input annotation as class &quot;X&quot;, and &quot;X&quot;
   * exists as a key in this map, then the classification is only effected if 
   * the classification probability emitted by the model is greater than or 
   * equal to the value in the map. Values in this map are expected to be
   * probabilities, i.e. positive values between 0.0 and 1.0. All negative 
   * values are ignored and the default will be used instead (the category with 
   * the highest probability wins, regardless of what the probability actually 
   * is).
   * 
   * Note that the liblinear model used must be able to produce classification 
   * probabilities. At the time of writing, only Linear Regression models are 
   * able to do so, while SVM-based one are not. Models that cannot supply
   * probabilities will simply return 1.0 as the classification probability, 
   * causing values in this map to have no effect.   
   */  
  protected double[] categoryThresholds;
  
  
  /**
   * The String labels for the categories (the LIBLINEAR classifier simply 
   * returns integer values for the labels).
   */
  protected String[] categoryLabels;
  
  
  /**
   * The set of stop-words to be used. The values in this list are matched
   * verbatim against the values of the configured input feature on the input
   * annotations.  
   */
  protected Set<String> stopWords;
  
  /**
   * URL to a file containing stop words, one on each line.
   * Lines starting with # or // are considered comments and are ignored.
   * White space at the start and end of each line's content will be ignored.
   */
  protected URL stopWordsURL;
  
  /**
   * The name for the annotation set used for input.
   */
  protected String inputASName;
  
  /**
   * The type of input annotations.
   */
  protected String inputAnnotationType;
  
  /**
   * The type for token annotations.
   */
  protected String tokenAnnotationType;
  
  /**
   * The feature on token annotations used to collect the input text.
   */
  protected String inputFeatureName;
  
  /**
   * The name for the output annotation set.
   */
  protected String outputASName;
  
  /**
   * The type of annotations produced. If this is the same as 
   * inputAnnotationType, and the input and output annotation sets are the same, 
   * then no new annotations will be created. The output feature value will
   * simply be added to the input annotations. 
   */
  protected String outputAnnotationType;
  
  /**
   * The name feature of the feature on the output annotations that stores the 
   * class. 
   */
  protected String outputFeatureName;
  
  /**
   * URL for the file containing the serialized semantic space.
   */
  protected URL sematicSpaceURL;
  
  /**
   * The URL to the LIBLINEAR model used for classification.
   */
  protected URL modelURL;

  /**
   * URL to a file defining the categories used by this PR.
   */
  protected URL categoriesURL;
  
  @Override
  public Resource init() throws ResourceInstantiationException {
    // load the model
    if(modelURL != null) {
      InputStreamReader isr = null;
      try{
        // the default implementation uses ISO-8859-1, so we do the same
        isr = new InputStreamReader(
            new BufferedInputStream(modelURL.openStream()), "ISO-8859-1");
        libLinearModel = Linear.loadModel(isr);
        isr.close();
      } catch(IOException ioe) {
        throw new ResourceInstantiationException(
            "IO Error while loading the model from " + modelURL, ioe);
      }
    } else throw new ResourceInstantiationException("No model URL provided.");
    // load the semantic space
    if(sematicSpaceURL != null) {
      if(sematicSpaceURL.getProtocol().equalsIgnoreCase("file")) {
        try {
          semanticSpace = SemanticSpaceIO.load(sematicSpaceURL.getFile());
        } catch(IOException e) {
          throw new ResourceInstantiationException(
              "I/O error while loading the semantic space from " + 
                  sematicSpaceURL, e);
        }    
      } else {
        throw new ResourceInstantiationException(
            "URL provided for the semantic space file (" + sematicSpaceURL +
            ")was not to a local file.");
      }
    } else {
      throw new ResourceInstantiationException(
          "No URL provided for the semantic space file.");
    }
    
    // create the vector builder
    Properties config = new Properties();
    config.put(DocumentVectorBuilder.USE_TERM_FREQUENCIES_PROPERTY, true);
    vectorBuilder = new DocumentVectorBuilder(semanticSpace, config);
    
    // load the stop words
    stopWords = null;
    if(stopWordsURL != null) {
      BufferedReader swReader = null;
      try {
        swReader = new BufferedReader(
            new InputStreamReader(stopWordsURL.openStream(), "UTF-8"));
        stopWords = new HashSet<String>();
        String line = swReader.readLine();
        while(line != null) {
          line = line.trim();
          if(line.startsWith("#") || line.startsWith("//") || line.length() == 0) {
            // ignore comment and empty lines
          } else {
            stopWords.add(line);
          }
          line = swReader.readLine();
        }
      } catch(IOException e) {
        throw new ResourceInstantiationException(
            "I/O error while reading the stop words.", e);
      } finally {
        if (swReader != null) {
          try {
            swReader.close();
          } catch(IOException e) {
            throw new ResourceInstantiationException(
                "I/O error whilke closing the stop words file.", e); 
          }
        }
      }
    }
    
    // read the categories
    List<String> categoryNames = new ArrayList<String>();
    List<Double> categoryProbs = new ArrayList<Double>();
    boolean customProbs = false;
    BufferedReader catReader = null;
    if(categoriesURL != null) {
      try {
        catReader = new BufferedReader(
            new InputStreamReader(categoriesURL.openStream(), "UTF-8"));
        String line = catReader.readLine();
        while(line != null) {
          line = line.trim();
          if(line.startsWith("#") || line.startsWith("//") || line.length() == 0) {
            // ignore comment and empty lines
          } else {
            String[] elems = line.split(",");
            if(elems.length == 0) {
              logger.warn("Ignoring illegal line in categories file: \"" + 
                line + "\".");
            } else {
              categoryNames.add(elems[0]);
              double prob = -1;
              if(elems.length > 1) {
                try {
                  prob = Double.parseDouble(elems[1]);
                } catch(NumberFormatException e) {
                  logger.error(
                      "Illegal value for probablity in categories file \"" + 
                      elems[1] + "\" was ignored.");
                }
              }
              categoryProbs.add(prob);
              if(prob > 0) customProbs = true;
              if(elems.length > 2) {
                logger.warn("Line in categories file has more than 2 entries." +
                " Entries starting with " + elems[2] + " were ignored. " +
                "Line was:\n" + line);
              }
            }
          }
          line = catReader.readLine();
        }
        categoryLabels = categoryNames.toArray(new String[categoryNames.size()]);
        if(customProbs) {
          if(libLinearModel.isProbabilityModel()) {
            categoryThresholds = new double[categoryProbs.size()];
            for(int i = 0; i< categoryThresholds.length; i++) {
              categoryThresholds[i] = categoryProbs.get(i);
            }            
          } else {
            logger.warn("The LIBLINEAR model provided cannot supply " + 
                "probabilities. Custom probability thresholds will be ignored.");
            categoryThresholds = null;
          }
        }
      } catch (IOException ioe){
        throw new ResourceInstantiationException(
            "I/O error while reading the categories file.", ioe); 
      } finally {
        if(catReader != null) try {
          catReader.close();
        } catch (IOException ioe) {
          throw new ResourceInstantiationException(
              "I/O error while closing the categories file.", ioe); 
        }
      }
    } else {
      throw new ResourceInstantiationException(
          "No categories file URL was provided.");
    }
    
    
    return super.init();
  }

  @Override
  public void execute() throws ExecutionException {
    // normalize parameters
    if(inputASName == null || inputASName.length() == 0) inputASName = "";
    if(outputASName == null || outputASName.length() == 0) outputASName = "";
    // should we use the input annotations for output
    boolean sameAnnotation = inputASName.equals(outputASName) && 
        inputAnnotationType.equals(outputAnnotationType);
    // validate parameter values
    if(inputAnnotationType == null || inputAnnotationType.length() == 0) {
      throw new ExecutionException("No input annotation type provided."); 
    }
    if(inputFeatureName == null || inputFeatureName.length() == 0) {
      throw new ExecutionException("No input feature name provided."); 
    }
    if(tokenAnnotationType == null || tokenAnnotationType.length() == 0) {
      throw new ExecutionException("No token annotation type provided."); 
    }
    if(outputAnnotationType == null || outputAnnotationType.length() == 0) {
      throw new ExecutionException("No output annotation type provided."); 
    }
    if(outputFeatureName == null || outputFeatureName.length() == 0) {
      throw new ExecutionException("No output feature name provided."); 
    }
    
    AnnotationSet outputAS = document.getAnnotations(outputASName);
    
    // collect instance annotations
    AnnotationSet inputAS = document.getAnnotations(inputASName);
    AnnotationSet instances = inputAS.get(inputAnnotationType);
    for(Annotation instAnn : instances) {
      // collect the tokens
      List<Annotation> instTokens = Utils.inDocumentOrder(
          Utils.getContainedAnnotations(inputAS, instAnn, tokenAnnotationType));
      // create a new context
      StringBuilder instanceStrBld = new StringBuilder();
      boolean first = true;
      for(Annotation token : instTokens) {
        String tokenString = (String)token.getFeatures().get(inputFeatureName);
        if(tokenString != null && tokenString.length() > 0) {
          if(first) {
            first = false;
          } else {
            instanceStrBld.append(' ');
          }
          instanceStrBld.append(tokenString);
        }
      }
      
      String instanceText = instanceStrBld.toString();
      if(instanceText.length() > 0) {
        DoubleVector instanceVector =  vectorBuilder.buildVector(
            new BufferedReader(new StringReader(instanceText)),
            new DenseVector(semanticSpace.getVectorLength()));
        // classify the vector
        Feature[] features = new Feature[libLinearModel.getNrFeature()];
        for(int i = 0; i <  features.length; i++) {
          // in liblinear, features start from 1, hence the "i + 1" below
          features[i] = new FeatureNode(i + 1, instanceVector.get(i));
        }
        double[] probs = new double[libLinearModel.getNrClass()];
        // the value returned is always an int. Returned as a double because
        // that's what cool C programmers do, or whatever...
        // We're uncool Java types, so we bring it back to int.
        int label = (int) Linear.predictValues(libLinearModel, features, probs);
        double probability = 1.0;
        // do we need to check probabilities?
        if(categoryThresholds != null && categoryThresholds[label] > 0) {
          // prob[i] is the value of $\theta^T x$ 
          // Once mapped through the logistic function, this becomes the 
          // probability of the instance belonging to class i, as opposed to 
          // all other classes.
          // LIBLINEAR normalizes the values so that all probabilities sum up 
          // to 1 (i.e. the output is a probability distribution over the set 
          // of classes). They should really be using softmax for that, but 
          // anyway... 
          // However, we don't want that, as we're only interested in the 
          // confidence the model has in this particular classification, and we 
          // don't know for sure that the classes are mutually-exclusive 
          probability = probs[label];
          // convert to an actual probability, by applying the logistic function
          probability = 1 / (1 + Math.exp(-probability));
          if(probability < categoryThresholds[label]) probability = -1;            
        }
        if(probability > 0) {
          // effect the classification
          if(sameAnnotation) {
            instAnn.getFeatures().put(outputFeatureName, categoryLabels[label]);
            instAnn.getFeatures().put(outputFeatureName + "-confidence", 
                new Double(probability));
          } else {
            FeatureMap fm = Factory.newFeatureMap();
            fm.put(outputFeatureName, categoryLabels[label]);
            fm.put(outputFeatureName + "-confidence", 
                new Double(probability));
            Utils.addAnn(outputAS, instAnn, outputAnnotationType, fm);
          }
        }
      }
      
    }
    
  }

  @Override
  public void cleanup() {
    libLinearModel = null;
    semanticSpace = null;
    stopWords = null;
  }

  public String getInputASName() {
    return inputASName;
  }

  /**
   * Set the name for the annotation set used for input.
   * @param inputASName
   */
  @CreoleParameter(comment="The name for the annotation set used for input", 
      defaultValue = "")
  @RunTime
  public void setInputASName(String inputASName) {
    this.inputASName = inputASName;
  }

  public String getInputAnnotationType() {
    return inputAnnotationType;
  }

  /**
   * Set the type of input annotations
   * @param inputAnnotationType
   */
  @CreoleParameter(comment="The type of input annotations", 
      defaultValue="Sentence")
  @RunTime
  public void setInputAnnotationType(String inputAnnotationType) {
    this.inputAnnotationType = inputAnnotationType;
  }

  public String getTokenAnnotationType() {
    return tokenAnnotationType;
  }

  /**
   * Set the type for token annotations.
   * @param tokenAnnotationType
   */
  @CreoleParameter(comment = "The type for token annotations.", 
      defaultValue = "Token")
  @RunTime
  public void setTokenAnnotationType(String tokenAnnotationType) {
    this.tokenAnnotationType = tokenAnnotationType;
  }

  public String getInputFeatureName() {
    return inputFeatureName;
  }

  /**
   * Set the feature on token annotations used to collect the input text.
   * @param inputFeatureName
   */
  @CreoleParameter(comment = 
      "The feature on the token annotations used to collect the text for " + 
      "each input annotation.", defaultValue = "root")
  @RunTime
  public void setInputFeatureName(String inputFeatureName) {
    this.inputFeatureName = inputFeatureName;
  }

  public String getOutputASName() {
    return outputASName;
  }

  /**
   * Set the name for the output annotation set
   * @param outputASName
   */
  @CreoleParameter(comment = "The name for the output annotation set", 
      defaultValue = "")
  @RunTime
  public void setOutputASName(String outputASName) {
    this.outputASName = outputASName;
  }

  public String getOutputAnnotationType() {
    return outputAnnotationType;
  }

  /**
   * Set the type of annotations produced. If this is the same as 
   * inputAnnotationType, and the input and output annotation sets are the same, 
   * then no new annotations will be created. The output feature value will
   * simply be added to the input annotations. 
   * @param outputAnnotationName
   */
  @CreoleParameter(defaultValue = "Sentence", comment = 
          "The type of annotations produced. If this is the same as " + 
          "inputAnnotationType, and the input and output annotation sets are the same, " +  
          "then no new annotations will be created. The output feature value will " +
          "simply be added to the input annotations. ")
  @RunTime
  public void setOutputAnnotationType(String outputAnnotationName) {
    this.outputAnnotationType = outputAnnotationName;
  }

  public String getOutputFeatureName() {
    return outputFeatureName;
  }

  /**
   * Set the name feature of the feature on the output annotations that stores 
   * the class. 
   * @param outputFeatureName
   */
  @CreoleParameter(defaultValue = "category", comment = 
        "The name feature of the feature on the output annotations that " + 
        "stores the class. ")
  @RunTime
  public void setOutputFeatureName(String outputFeatureName) {
    this.outputFeatureName = outputFeatureName;
  }

  public URL getSematicSpaceURL() {
    return sematicSpaceURL;
  }

  /**
   * Set the URL for the file containing the serialized semantic space.
   * @param sematicSpaceURL
   */
  @CreoleParameter(comment = 
      "URL for the file containing the serialized semantic space. This must be a local file:// URL.")
  public void setSematicSpaceURL(URL sematicSpaceURL) {
    this.sematicSpaceURL = sematicSpaceURL;
  }

  public URL getModelURL() {
    return modelURL;
  }

  /**
   * Set the URL to the LibLinear model used for classification.
   * @param modelURL
   */
  @CreoleParameter(comment = "The URL to the LIBLINEAR model used for classification")
  public void setModelURL(URL modelURL) {
    this.modelURL = modelURL;
  }

  public URL getStopWordsURL() {
    return stopWordsURL;
  }

  /**
   * Set the URL to a file containing stop words, one on each line.
   * @param stopWordsURL
   */
  @CreoleParameter(
      comment = "URL to a file containing stop words, one on each line, using UTF-8.")
  @Optional
  public void setStopWordsURL(URL stopWordsURL) {
    this.stopWordsURL = stopWordsURL;
  }

  public URL getCategoriesURL() {
    return categoriesURL;
  }

  /**
   * Sets the URL to the file containing the categories.
   * @param categoriesURL
   */
  @CreoleParameter(comment = "A comma-separated file containing the category " + 
      "name and, optionally, a custom probability threshold for that category.")
  public void setCategoriesURL(URL categoriesURL) {
    this.categoriesURL = categoriesURL;
  }
  
}
