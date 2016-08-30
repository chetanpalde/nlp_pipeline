/*
 *  FSMInstance.java
 *
 *  Copyright (c) 1995-2012, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 *  Valentin Tablan, 05/May/2000
 *
 *  $Id: FSMInstance.java 17595 2014-03-08 13:05:32Z markagreenwood $
 */
package gate.fsm;

import java.io.Serializable;
import java.util.*;

import gate.*;
import gate.jape.RightHandSide;
import gate.util.Err;
import gate.util.InvalidOffsetException;

/**
  * The objects of this class represent instances of working Finite State
  * Machine during parsing a gate document (annotation set).
  * In order to completely define the state a FSM is in one needs to store
  * information regarding:
  * -the position in the FSM transition graph
  * -the position in the annotation graph
  * -the set of bindings that occured up to the current state.
  *  note that a set of bindings is an object of type Map that maps names
  * (java.lang.String) to bags of annotations (gate.AnnotationSet)
  */
public class FSMInstance implements Comparable<FSMInstance>, Cloneable, Serializable {

  private static final long serialVersionUID = -2081096002552818621L;

  /** Creates a new FSMInstance object.
    * @param supportGraph the transition graph of the FSM
    * @param FSMPosition the state this instance will be in
    * @param startNode the node in the AnnotationSet where this FSM instance
    * started the matching
    * @param AGPosition the node in the AnnotationSet up to which this FSM Instance
    * advanced during the matching.
    * @param bindings a HashMap that maps from labels (objects of type String)
    * to sets of annotations (objects of type AnnotationSet). This map stores
    * all the bindings that took place during the matching process.
    * This FSMInstance started the matching on an AnnotationSet from "startNode"
    * and advanced to "AGPosition"; during this process it traversed the path in
    * the transition graph "supportGraph" from the initial state to
    * "FSMPosition" and made the bindings stored in "bindings".
    */
  public FSMInstance(FSM supportGraph, State FSMPosition,
                     Node startNode, Node AGPosition,
                     HashMap<String, AnnotationSet> bindings,
                     Document document) {

    this.supportGraph = supportGraph;
    this.FSMPosition = FSMPosition;
    this.startNode = startNode;
    this.AGPosition = AGPosition;
    this.bindings = bindings;
    this.document = document;
    length = AGPosition.getOffset().longValue() -
             startNode.getOffset().longValue();
    fileIndex = FSMPosition.getFileIndex();
    priority = FSMPosition.getPriority();
  }

  /** Returns the FSM transition graph that backs this FSM instance
    * @return an FSM object
    */
  public FSM getSupportGraph(){ return supportGraph; }

  /** Returns the position in the support graph for this FSM instance
    * @return an object of type State
    */
  public State getFSMPosition(){ return FSMPosition; }

  /** Sets the position in the support transition graph for this FSM instance
    * Convenience method for when the state is not known at construction time.
    */
  public void setFSMPosition(State newFSMPos) {
    FSMPosition = newFSMPos;
    fileIndex = FSMPosition.getFileIndex();
    priority = FSMPosition.getPriority();
  }

  /** Returns the index in the Jape definition file of the rule that caused
    * the generation of the FSM state this instance is in.
    * This value is correct if and only if this FSM instance is in a final
    * state of the FSM transition graph.
    * @return an int value.
    */
  public int getFileIndex(){ return fileIndex; }

  /** Returns the node in the AnnotationSet from which this FSM instance
    * started the matching process.
    * @return a gate.Node object
    */
  public Node getStartAGPosition(){ return startNode; }

  /** Returns the node up to which this FSM instance advanced in the
    * Annotation graph during the matching process.
    * @return a gate.Node object
    */
  public Node getAGPosition(){ return AGPosition; }

  /** Sets the current position in the AnnotationSet.
    * Convenience method for cases when this value is not known at construction
    * time.
    * @param node a position in the AnnotationSet
    */
  public void setAGPosition(Node node){
    AGPosition = node;
    length = AGPosition.getOffset().longValue() -
             startNode.getOffset().longValue();
  }

  /** Gets the map representing the bindings that took place during the matching
    * process this FSM instance performed.
    * @return a HashMap object
    */
  public Map<String, AnnotationSet> getBindings() { return bindings; }

  /** Returns the length of the parsed region in the document under scrutiny.
    * More precisely this is the distnace between the Node in the annotation
    * graph where the matching started and the current position.
    * @return a long value
    */
  public long getLength() { return length; }

  /** Overrides the hashCode method from Object so this obejcts can be stored in
    * hash maps and hash sets.
    */
  @Override
  public int hashCode() {
    return (int)length ^ priority ^ fileIndex ^ bindings.hashCode() ^
           FSMPosition.getAction().hashCode();
  }

  @Override
  public boolean equals(Object other){
    if (other == null) return false;
    if(other instanceof FSMInstance){
      FSMInstance otherFSM = (FSMInstance)other;
      boolean result = length == otherFSM.length &&
             priority == otherFSM.priority &&
             fileIndex == otherFSM.fileIndex &&
             bindings.equals(otherFSM.bindings) &&
             FSMPosition.getAction().equals(otherFSM.FSMPosition.getAction());
      return result;
    }else{
      //equals should never throw an exception it should just return null
      //throw new ClassCastException(other.getClass().toString());
      return false;
    }
  }

  /** Returns a clone of this object.
    * The cloning is done bitwise except for the bindings that are cloned by
    * themselves
    * @return an Object value that is actually a FSMInstance object
    */
  @Override
  public Object clone() {
    //do a classic clone except for bindings which need to be cloned themselves
    try {
      FSMInstance clone = (FSMInstance)super.clone();
      clone.bindings = new HashMap<String,AnnotationSet>(bindings);
      return clone;
    } catch (CloneNotSupportedException cnse) {
      cnse.printStackTrace(Err.getPrintWriter());
      return null;
    }
  }

  /*
  public Object clone() {
  //do a classic clone except for bindings which need to be cloned themselves
  //Out.println("Clone!");
    FSMInstance clone = FSMInstance.getNewInstance(this.supportGraph,
                                                   this.FSMPosition,
                                                   this.startNode,
                                                   this.AGPosition,
                                                   null);
    clone.bindings = (HashMap)(bindings.clone());
    return (FSMInstance)clone;
  }
  */

  /** Implementation of the compareTo method required by the Comparable
    * interface. The comparison is based on the size of the matched region and
    * the index in the definition file of the rule associated to this FSM
    * instance (which needs to be in a final state)
    * The order imposed by this method is the priority needed in case of a
    * multiple match.
    */
  @Override
  public int compareTo(FSMInstance other) {
      if(other == this) return 0;
            if(length < other.getLength()) return -1;
      else if(length > other.getLength()) return 1;
      //equal length
      else if(priority < other.priority) return -1;
      else if(priority > other.priority) return 1;
      //equal priority
      else return other.fileIndex - fileIndex;
  }

  /** Returns a textual representation of this FSM instance.
    */
  @Override
  public String toString() {
    String res = "";
    RightHandSide rhs = getFSMPosition().getAction();
    if(rhs != null){
      res += rhs.getPhaseName() + "." + rhs.getRuleName() + ": \"";
      try{
        res += document.getContent().getContent(
                        getStartAGPosition().getOffset(),
                        getAGPosition().getOffset()).toString() + "\"";
      }catch(InvalidOffsetException ioe){
        ioe.printStackTrace(Err.getPrintWriter());
      }

      Iterator<String> labelIter = bindings.keySet().iterator();
      res += "\n{";
      while(labelIter.hasNext()){
        String label = labelIter.next();
        Collection<Annotation> annots = bindings.get(label);
        res += "\n" + label + ": ";
        Iterator<Annotation> annIter = annots.iterator();
        while(annIter.hasNext()){
          Annotation ann  = annIter.next();
          res += ann.getType() + "(\"";
          try{
            res += document.getContent().
                            getContent(ann.getStartNode().getOffset(),
                                       ann.getEndNode().getOffset()).toString();
          }catch(InvalidOffsetException ioe){
            ioe.printStackTrace(Err.getPrintWriter());
          }
          res += "\") ";
        }
      }
      res += "\n}";
    }else{
      res +=  "FSM position :" + FSMPosition.getIndex() +
              "\nFirst matched ANN at:" + startNode.getId() +
              "\nLast matched ANN at :" + AGPosition.getId() +
              "\nPriority :" + priority +
              "\nFile index :" + fileIndex +
              "\nBindings     :" + bindings;
    }
    return res;
  }

  /** The FSM for which this FSMInstance is an instance of. */
  private FSM supportGraph;

  /** The current state of this FSMInstance */
  private State FSMPosition;

  /** The place (Node) in the AnnotationSet where the matching started*/
  private Node AGPosition, startNode;

  /** A map from java.lang.String to gate.AnnotationSet describing all the
    * bindings that took place during matching.
    * needs to be HashMap instead of simply Map in order to cloneable
    */
  private Map<String, AnnotationSet> bindings;

  /** The size of the matched region in the Annotation Set*/
  private long length = 0;

  /**
    * The index in the definition file of the rule from which the AGPosition
    * state was generated.
    */
  private int fileIndex;


  private Document document;
  /**
    * The priority in the definition file of the rule from which the AGPosition
    * state was generated.
    */
  private int priority;

  /** Static method that provides new FSM instances. This method handles some
    * basic object pooling in order to reuse the FSMInstance objects.
    * This is considered to be a good idea because during jape transducing
    * a large number of FSMIntances are needed for short periods.
    */
  public static FSMInstance getNewInstance(FSM supportGraph, State FSMPosition,
                                           Node startNode, Node AGPosition,
                                           HashMap<String, AnnotationSet> bindings,
                                           Document doc) {
    FSMInstance res;
    if(myInstances.isEmpty()) res = new FSMInstance(supportGraph, FSMPosition,
                                                    startNode, AGPosition,
                                                    bindings, doc);
    else {
      res = myInstances.removeFirst();
      res.supportGraph = supportGraph;
      res.FSMPosition = FSMPosition;
      res.startNode = startNode;
      res.AGPosition = AGPosition;
      res.bindings = bindings;
    }
    return res;
  }

  /** Static method used to return a FSMInstance that is not needed anymore
    */
  public static void returnInstance(FSMInstance ins) {
    myInstances.addFirst(ins);
  }

  /** Release all the FSMInstances that are not currently in use */
  public static void clearInstances() {
    myInstances = new LinkedList<FSMInstance>();
  }

  /** The list of existing instances of type FSMInstance */
  private static LinkedList<FSMInstance> myInstances;

  /** The offset in the input List where the last matched annotation was*/
  static{
    myInstances = new LinkedList<FSMInstance>();
  }

} // FSMInstance
