/*
 *  MajorityVoteAnnotationConsensus.java
 *
 *  Copyright (c) 1995-2014, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 3, June 2007 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *  
 *  $Id: MajorityVoteAnnotationConsensus.java 18463 2014-11-17 19:59:36Z ian_roberts $
 */
package gate.crowdsource.ne;

import java.util.ArrayList;
import java.util.List;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Factory;
import gate.Utils;
import gate.creole.AbstractLanguageAnalyser;
import gate.creole.ExecutionException;
import gate.creole.ExecutionInterruptedException;
import gate.creole.metadata.CreoleParameter;
import gate.creole.metadata.CreoleResource;
import gate.creole.metadata.Optional;
import gate.creole.metadata.RunTime;

@CreoleResource(name = "Majority-vote consensus builder (annotation)", comment = "Process results of a crowd annotation task to find "
        + "where annotators agree and disagree.", helpURL = "http://gate.ac.uk/userguide/sec:crowd:annotation:adjudication")
public class MajorityVoteAnnotationConsensus extends AbstractLanguageAnalyser {

  private static final long serialVersionUID = -983107328639648414L;

  private String resultASName;

  private String resultAnnotationType;

  private String consensusASName;

  private String disputeASName;

  private Integer minimumAgreement;

  public String getResultASName() {
    return resultASName;
  }

  @RunTime
  @Optional
  @CreoleParameter(comment = "Annotation set containing the entity "
          + "annotations generated from crowd judgments", defaultValue = "crowdResults")
  public void setResultASName(String resultASName) {
    this.resultASName = resultASName;
  }

  public String getResultAnnotationType() {
    return resultAnnotationType;
  }

  @RunTime
  @CreoleParameter(comment = "The annotation type to process")
  public void setResultAnnotationType(String resultAnnotationType) {
    this.resultAnnotationType = resultAnnotationType;
  }

  public String getConsensusASName() {
    return consensusASName;
  }

  @RunTime
  @Optional
  @CreoleParameter(comment = "Annotation set into which consensus annotations should "
          + "be placed.  Only annotations that were marked by at least the minimum number "
          + "of annotators will be moved to this set")
  public void setConsensusASName(String consensusASName) {
    this.consensusASName = consensusASName;
  }

  public String getDisputeASName() {
    return disputeASName;
  }

  @Optional
  @RunTime
  @CreoleParameter(comment = "Annotation set into which disputed annotations should "
          + "be placed.", defaultValue = "crowdDisputed")
  public void setDisputeASName(String disputeASName) {
    this.disputeASName = disputeASName;
  }

  public Integer getMinimumAgreement() {
    return minimumAgreement;
  }

  @RunTime
  @CreoleParameter(comment = "Minimum number of annotators who must agree on a single "
          + "annotation span for it to be approved and added to the consensus set")
  public void setMinimumAgreement(Integer minimumAgreement) {
    this.minimumAgreement = minimumAgreement;
  }

  public void execute() throws ExecutionException {
    if(isInterrupted()) throw new ExecutionInterruptedException();
    interrupted = false;

    AnnotationSet allResults =
            getDocument().getAnnotations(resultASName)
                    .get(resultAnnotationType);
    AnnotationSet consensusAS = getDocument().getAnnotations(consensusASName);
    AnnotationSet disputeAS = getDocument().getAnnotations(disputeASName);

    // inDocumentOrder groups annotations with the same span together,
    // with shorter ones before longer ones
    List<Annotation> currentGroup = new ArrayList<>(minimumAgreement * 2);
    for(Annotation result : Utils.inDocumentOrder(allResults)) {
      if(!currentGroup.isEmpty()
              && !currentGroup.get(currentGroup.size() - 1).coextensive(result)) {
        // group is complete
        processGroup(currentGroup, consensusAS, disputeAS);
        currentGroup.clear();
      }
      currentGroup.add(result);
    }
    // process the last group, if non-empty (the only way it can
    // be empty is if there were no annotations at all in allResults)
    if(!currentGroup.isEmpty()) {
      processGroup(currentGroup, consensusAS, disputeAS);
    }
  }

  /**
   * Process a single group of co-extensive annotations, and either
   * create a consensus annotation if there is sufficient agreement, or
   * copy them all to the dispute set if not.
   * 
   * @param group the group of annotations to process, which will all
   *          have exactly the same span.
   * @param consensusAS consensus set
   * @param disputeAS disputed set
   * @throws ExecutionException
   */
  protected void processGroup(List<Annotation> group,
          AnnotationSet consensusAS, AnnotationSet disputeAS)
          throws ExecutionException {
    if(group.size() >= minimumAgreement) {
      Utils.addAnn(consensusAS, group.get(0), resultAnnotationType, Factory.newFeatureMap());
    } else {
      for(Annotation a : group) {
        // toFeatureMap does a shallow clone of the original annotation's features
        Utils.addAnn(disputeAS, a, a.getType(), Utils.toFeatureMap(a.getFeatures()));
      }
    }
  }

}
