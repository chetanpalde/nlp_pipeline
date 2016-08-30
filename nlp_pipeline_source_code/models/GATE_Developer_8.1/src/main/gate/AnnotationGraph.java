/*
 *  AnnotationGraph.java
 *
 *  Copyright (c) 1995-2012, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June1991.
 *
 *  A copy of this licence is included in the distribution in the file
 *  licence.html, and is also available at http://gate.ac.uk/gate/licence.html.
 *
 *  Hamish Cunningham, 19/Jan/2000
 *
 *  $Id: AnnotationGraph.java 17530 2014-03-04 15:57:43Z markagreenwood $
 */

package gate;

/** NOT IN USE AT PRESENT. <P>Annotation graphs are defined at
  * <A HREF=http://www.ldc.upenn.edu/annotation/>the LDC's annotation site</A>
  */
public interface AnnotationGraph {

  /** find a node by ID */
  public Node getNode(Long id);

  //  /** Greatest lower bound on an annotation: the greatest anchor in the AG
  //    * such that there is a node with this anchor which structurally precedes
  //    * the start node of annotation a. */
  //  public Long greatestLowerBound(Annotation a);
  //
  //  /** Least upper bound on an annotation: the smallest anchor in the AG
  //    * such that there is a node with this anchor is structurally preceded
  //    * by the end node of annotation a. */
  //  public Long leastUpperBound(Annotation a);
  //
  //  /** The set of annotations overlapping a */
  //  public AnnotationGraph getOverlappingAnnotations(Annotation a);
  //
  //  /** The set of annotations included by a */
  //  public AnnotationGraph getIncludedAnnotations(Annotation a);*/

  /** Get annotations by type */
  public AnnotationGraph getAnnotations(String type);

  /** Get annotations by type and features */
  public AnnotationGraph getAnnotations(String type, FeatureMap features);

  /** Get annotations by type and position. This is the set of annotations of
    * a particular type which share the smallest leastUpperBound that is >=
    * offset
    */
  public AnnotationGraph getAnnotations(String type, Long offset);

  /** Get annotations by type, features and offset */
  public AnnotationGraph getAnnotations(String type, FeatureMap features,
					Long offset);

  /** Creates a new node with the offset offset
    * @param offset the offset in document where the node will point
    */
  public Node putNodeAt(Long id, long offset)
    throws gate.util.InvalidOffsetException;

  /** Returns the Id of the annotation graph*/
  public Long getId();

  public Annotation newAnnotation(Long id, Node start, Node end, String type);

  public Annotation newAnnotation(Long id,long start, long end, String type);

} // interface AnnotationGraph