/*
 *  MajorityVoteClassificationConsensus.java
 *
 *  Copyright (c) 1995-2014, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 3, June 2007 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *  
 *  $Id: MajorityVoteClassificationConsensus.java 18463 2014-11-17 19:59:36Z ian_roberts $
 */
package gate.crowdsource.classification;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Factory;
import gate.FeatureMap;
import gate.Utils;
import gate.creole.AbstractLanguageAnalyser;
import gate.creole.ExecutionException;
import gate.creole.ExecutionInterruptedException;
import gate.creole.metadata.CreoleParameter;
import gate.creole.metadata.CreoleResource;
import gate.creole.metadata.Optional;
import gate.creole.metadata.RunTime;
import gate.crowdsource.CrowdFlowerConstants;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@CreoleResource(name = "Majority-vote consensus builder (classification)", comment = "Process results of a crowd annotation task to find "
        + "where annotators agree and disagree.", helpURL = "http://gate.ac.uk/userguide/sec:crowd:classification:adjudication")
public class MajorityVoteClassificationConsensus
                                                extends
                                                  AbstractLanguageAnalyser {

  private static final long serialVersionUID = -6741876068621064245L;

  /**
   * Defines the various actions that can be taken for units where there
   * is insufficient agreement between crowd annotators.
   */
  public static enum Action {
    /**
     * Move the disputed result annotations into one set so they can be
     * resolved locally in GATE Developer.
     */
    resolveLocally,

    /**
     * Prepare a new crowd annotation task for disputed entities,
     * offering just the options that were selected by the first round
     * annotators.
     */
    reAnnotateByCrowd
  }

  private String resultASName;

  private String resultAnnotationType;

  private String answerFeatureName;

  private String originalEntityASName;

  private String entityAnnotationType;

  private String consensusASName;

  private String disputeASName;

  private Integer minimumAgreement;

  private Action noAgreementAction;

  public String getResultASName() {
    return resultASName;
  }

  @Optional
  @RunTime
  @CreoleParameter(comment = "Annotation set containing the annotations representing crowd judgments", defaultValue = "crowdResults")
  public void setResultASName(String resultASName) {
    this.resultASName = resultASName;
  }

  public String getResultAnnotationType() {
    return resultAnnotationType;
  }

  @RunTime
  @CreoleParameter(comment = "Type of the annotations representing crowd judgments", defaultValue = "Mention")
  public void setResultAnnotationType(String resultAnnotationType) {
    this.resultAnnotationType = resultAnnotationType;
  }

  public String getAnswerFeatureName() {
    return answerFeatureName;
  }

  @RunTime
  @CreoleParameter(comment = "The feature on result annotations giving the selected answer", defaultValue = "answer")
  public void setAnswerFeatureName(String answerFeatureName) {
    this.answerFeatureName = answerFeatureName;
  }

  public String getOriginalEntityASName() {
    return originalEntityASName;
  }

  @Optional
  @RunTime
  @CreoleParameter(comment = "Annotation set containing the original entity annotations that were imported to form CrowdFlower units")
  public void setOriginalEntityASName(String originalEntityASName) {
    this.originalEntityASName = originalEntityASName;
  }

  public String getEntityAnnotationType() {
    return entityAnnotationType;
  }

  @RunTime
  @CreoleParameter(comment = "Type of the original entity annotations that were imported to form CrowdFlower units.", defaultValue = "Mention")
  public void setEntityAnnotationType(String entityAnnotationType) {
    this.entityAnnotationType = entityAnnotationType;
  }

  public String getConsensusASName() {
    return consensusASName;
  }

  @Optional
  @RunTime
  @CreoleParameter(comment = "Annotation set into which consensus annotations (which meet the minimum agreement threshold) should be placed", defaultValue = "crowdConsensus")
  public void setConsensusASName(String consensusASName) {
    this.consensusASName = consensusASName;
  }

  public String getDisputeASName() {
    return disputeASName;
  }

  @Optional
  @RunTime
  @CreoleParameter(comment = "Annotation set in which disputed annotations should be created.  Exactly what form these annotations take depends on the noAgreementAction.", defaultValue = "crowdDisputed")
  public void setDisputeASName(String disputeASName) {
    this.disputeASName = disputeASName;
  }

  public Integer getMinimumAgreement() {
    return minimumAgreement;
  }

  @RunTime
  @CreoleParameter(comment = "Minimum number of annotators who must agree for the entity to be approved and moved into the consensus set.")
  public void setMinimumAgreement(Integer minimumAgreement) {
    this.minimumAgreement = minimumAgreement;
  }

  public Action getNoAgreementAction() {
    return noAgreementAction;
  }

  @RunTime
  @CreoleParameter(comment = "The action to take on annotations which do not meet the minimum agreement threshold.", defaultValue = "resolveLocally")
  public void setNoAgreementAction(Action noAgreementAction) {
    this.noAgreementAction = noAgreementAction;
  }

  @SuppressWarnings("unchecked")
  public void execute() throws ExecutionException {
    if(isInterrupted()) throw new ExecutionInterruptedException();
    interrupted = false;
    AnnotationSet allEntities =
            getDocument().getAnnotations(originalEntityASName).get(
                    entityAnnotationType);
    AnnotationSet allResults =
            getDocument().getAnnotations(resultASName)
                    .get(resultAnnotationType);
    AnnotationSet consensusAS = getDocument().getAnnotations(consensusASName);
    AnnotationSet disputeAS = getDocument().getAnnotations(disputeASName);
    for(Annotation origEntity : allEntities) {
      AnnotationSet judgments =
              Utils.getCoextensiveAnnotations(allResults, origEntity);
      Map<String, Integer> answerCounts = new HashMap<String, Integer>();
      // tally up the answers
      for(Annotation judgment : judgments) {
        String answer = (String)judgment.getFeatures().get(answerFeatureName);
        if(answer != null) {
          Integer count = answerCounts.get(answer);
          if(count == null) {
            count = 1;
          } else {
            count += 1;
          }
          answerCounts.put(answer, count);
        }
      }

      // how many answers meet the threshold?
      int answersOverThreshold = 0;
      String agreedAnswer = null;
      for(String answer : answerCounts.keySet()) {
        if(answerCounts.get(answer) >= minimumAgreement) {
          answersOverThreshold++;
          agreedAnswer = answer;
        }
      }

      if(answersOverThreshold == 1) {
        // if exactly one answer is over threshold, we have a winner
        Utils.addAnn(consensusAS, origEntity, resultAnnotationType,
                Utils.featureMap(answerFeatureName, agreedAnswer));
      } else {
        // either no answer met the threshold, or more than one did
        // (only possible if threshold is below half) - disputed. What
        // we do next depends on the action setting.
        if(noAgreementAction == Action.resolveLocally) {
          // copy disputed answers into output set
          for(Annotation judgment : judgments) {
            FeatureMap fm = Factory.newFeatureMap();
            fm.putAll(judgment.getFeatures());
            Utils.addAnn(disputeAS, judgment, resultAnnotationType, fm);
          }
        } else if(noAgreementAction == Action.reAnnotateByCrowd) {
          // create a copy of the original entity annotation, with a
          // subset of options
          FeatureMap fm = Factory.newFeatureMap();
          for(Object k : origEntity.getFeatures().keySet()) {
            if(!CrowdFlowerConstants.UNIT_ID_FEATURE_NAME.equals(k)
                    && !"options".equals(k)) {
              fm.put(k, origEntity.getFeatures().get(k));
            }
          }

          // construct replacement "options" structure containing only
          // the options that were used in the judgments set. We have to
          // take care to (a) only include options that were in the
          // original "options" feature, and not judgments that picked
          // one of the task-wide common options, and (b) do our best to
          // keep the options in the same order as they were in the
          // original entity.
          Object origOptions = origEntity.getFeatures().get("options");
          try {
            if(origOptions instanceof Collection<?>) {
              Collection<String> newOptions =
                      origOptions.getClass().asSubclass(Collection.class)
                              .newInstance();
              for(Object origOption : (Collection<?>)origOptions) {
                String strOption = origOption.toString();
                if(answerCounts.containsKey(strOption)) {
                  newOptions.add(strOption);
                }
              }
              fm.put("options", newOptions);
            } else if(origOptions instanceof Map<?, ?>) {
              Map<String, Object> newOptions =
                      origOptions.getClass().asSubclass(Map.class)
                              .newInstance();
              for(Map.Entry<Object, Object> origOption : ((Map<Object, Object>)origOptions)
                      .entrySet()) {
                String strOption = origOption.getKey().toString();
                if(answerCounts.containsKey(strOption)) {
                  newOptions.put(strOption, origOption.getValue());
                }
              }
              fm.put("options", newOptions);
            }
          } catch(InstantiationException | IllegalAccessException e) {
            throw new ExecutionException("Couldn't create new options of type "
                    + origOptions.getClass().getName());
          }
          Utils.addAnn(disputeAS, origEntity, entityAnnotationType, fm);
        }
      }
    }

  }

}
