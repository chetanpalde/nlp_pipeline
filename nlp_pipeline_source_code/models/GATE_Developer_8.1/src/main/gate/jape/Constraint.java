/*
 *  Constraint.java - transducer class
 *
 *  Copyright (c) 1995-2012, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 *  Hamish Cunningham, 24/07/98
 *
 *  $Id: Constraint.java 17597 2014-03-08 15:19:43Z markagreenwood $
 */

package gate.jape;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Factory;
import gate.FeatureMap;
import gate.creole.ontology.Ontology;
import gate.jape.constraint.AnnotationFeatureAccessor;
import gate.jape.constraint.ConstraintPredicate;
import gate.jape.constraint.EqualPredicate;
import gate.util.SimpleFeatureMapImpl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A set of predicates/expressions that apply to a single
 * {@link Annotation} type. It doesn't extend PatternElement, even
 * though it has to "match", because a set of Constraint must be applied
 * together in order to avoid doing separate selectAnnotations calls for
 * each one.
 * <p>
 * <b>Matching Logic:</b>The matching function of a non-negated
 * constraint can be defined as, "There exists an annotation for which
 * all of the predicates are true." Thus, given a collection of
 * annotations, only a single annotation must meet all of the predicates
 * of the constraint in order for the match to be successful.
 * <p>
 * <b>Negation:</b> A negated constraint means strictly the opposite -
 * "There does not exist an annotation for which any of these predicates
 * are true." Negation does <b>not</b> mean "There exists an annotation
 * for which all of these predicates are false." Thus, negation makes
 * more intuitive sense when thought of as applying to a set of
 * annotations rather than to an individual annotation.
 */
public class Constraint implements JapeConstants, gate.creole.ANNIEConstants,
                       Serializable, Cloneable {

  private static final long serialVersionUID = -7856281017609822483L;

  /** Construction from annot type string */
  public Constraint(String annotType) {
    this.annotType = annotType;
  } // Construction from annot type

  /**
   * Construction from annot type and FeatureMap. Creates
   * {@link EqualPredicate}s for each feature in the map
   */
  public Constraint(String annotType, FeatureMap attrs) {
    this(annotType);

    for(Map.Entry<Object, Object> entry : attrs.entrySet()) {
      addAttribute(entry.getKey().toString(), entry.getValue());
    }
  } // Construction from annot type and attribute sequence

  /** The type of annotation we're looking for. */
  private String annotType;

  /** Are we negated? */
  private boolean negated = false;

  /** Set negation. */
  public void negate() {
    negated = true;
  }

  /** Change the sign of the negation flag. */
  public void changeSign() {
    negated = !negated;
  }

  /** Access to negation flag. */
  public boolean isNegated() {
    return negated;
  }

  /** Get the type of annotation we're looking for. */
  public String getAnnotType() {
    return annotType;
  }

  /**
   * The list of predicates that must match the annotation features.
   */
  private List<ConstraintPredicate> predicates = new ArrayList<ConstraintPredicate>();

  /** Get the attributes that must be present on the matched annotation. */
  public List<ConstraintPredicate> getAttributeSeq() {
    return predicates;
  }

  /** Predicate that acts on class feature, if one is set. */
  protected ConstraintPredicate ontLookupClassPred;

  /**
   * FeatureMap that may contain ontology-related features and values
   * pulled from any predicates that operate on those features
   */
  protected FeatureMap ontFeatureMap;

  /** Add an attribute. */
  public void addAttribute(ConstraintPredicate attr) {
    predicates.add(attr);

    // check for LOOKUP_CLASS_FEATURE_NAME and
    // LOOKUP_ONTOLOGY_FEATURE_NAME
    // predicates and store some information about them once so we don't
    // have to look it up on every matches call.
    if(attr.getAccessor() instanceof AnnotationFeatureAccessor) {
      String featureName = (String)attr.getAccessor().getKey();
      if(featureName.equals(LOOKUP_CLASS_FEATURE_NAME)) {
        ontLookupClassPred = attr;
        getOntFeatureMap().put(LOOKUP_CLASS_FEATURE_NAME,
                ontLookupClassPred.getValue());
        if(!attr.getOperator().equals(ConstraintPredicate.EQUAL))
          gate.util.Err
                  .println("Warning: If an ontology is specified at runtime, "
                         + "Ontology class feature will be compared using "
                         + "ontology subsumption, not " + attr.getOperator());
      }
      else if(featureName.equals(LOOKUP_ONTOLOGY_FEATURE_NAME)) {
        getOntFeatureMap().put(LOOKUP_ONTOLOGY_FEATURE_NAME, attr.getValue());
      }
    }

  } // addAttribute

  /**
   * Generate a FeatureMap to perform ontology-related compare.
   */
  protected FeatureMap getOntFeatureMap() {
    if(ontFeatureMap == null) ontFeatureMap = new SimpleFeatureMapImpl();

    return ontFeatureMap;
  }

  /** Create and add an attribute. */
  public void addAttribute(String name, Object value) {
    ConstraintPredicate attr = Factory.getConstraintFactory().createPredicate(
            name, value);
    addAttribute(attr);
  } // addAttribute

  /**
   * Add all predicates from the given collection to this object. Does
   * not remove or replace any existing predicates.
   *
   * @param attrs
   */
  public void addAttributes(Collection<ConstraintPredicate> attrs) {
    // don't just call addAll because we want to check for
    // ontology-related features
    for(ConstraintPredicate pred : attrs) {
      addAttribute(pred);
    }
  }

  /**
   * Need cloning for processing of macro references. See comments on
   * <CODE>PatternElement.clone()</CODE>
   */
  @Override
  public Object clone() {
    Constraint newC = null;
    try {
      newC = (Constraint)super.clone();
    }
    catch(CloneNotSupportedException e) {
      throw (new InternalError(e.toString()));
    }
    newC.annotType = annotType;
    newC.predicates = new ArrayList<ConstraintPredicate>(predicates);
    return newC;
  } // clone

  /**
   * Returns a boolean value indicating whether this Constraint is
   * equivalent to the given Constraint. If the given object is not a
   * Constraint, compares the two objects using
   * <CODE>Object.equals()</CODE>.
   */
  @Override
  public boolean equals(Object other) {
    if(!(other instanceof Constraint)) return super.equals(other);
    Constraint o = (Constraint)other;

    return (o.negated == negated && o.annotType.equals(annotType) && o.predicates
            .equals(predicates));
  }

  /**
   * Returns an integer hash code for this object.
   */
  @Override
  public int hashCode() {
    int hashCode = negated ? 0 : 37 * 17;
    hashCode = 37 * hashCode + annotType.hashCode();
    hashCode = 37 * hashCode + predicates.hashCode();
    return hashCode;
  }

  /**
   * Finish: replace dynamic data structures with Java arrays; called
   * after parsing.
   */
  public void finish() {
    /*
     * if(attrs1 == null || attrs1.size() == 0) { attrs2 = new
     * JdmAttribute[0]; attrs1 = null; return; } int attrsLen =
     * attrs1.size(); attrs2 = new JdmAttribute[attrsLen];
     *
     * int i = 0; //for(Enumeration e = attrs1.getElements();
     * e.hasMoreElements(); i++) { // attrs2[i] = (JdmAttribute)
     * e.nextElement(); //} Iterator iter = attrs1.keySet().iterator();
     * while(iter.hasNext()) { String name = (String) iter.next();
     * Object value = attrs1.get(name); attrs2[i++] = new
     * JdmAttribute(name, value); } attrs1 = null;
     */
  } // finish

  /** Create a string representation of the object. */
  @Override
  public String toString() {
    return getDisplayString("Constraint: ");
  }

  /** Create a string representation of the object. */
  public String getDisplayString(String prefix) {
    StringBuffer buf = new StringBuffer(prefix);
    if(negated) buf.append("!");
    buf.append(annotType);
    if(!predicates.isEmpty()) {
      buf.append("(");
      buf.append(getAttributesString());
      buf.append(")");
    }
    return buf.toString();
  } // toString

  /**
   * Returns string representation of all the attributes that is
   * appropriate for display.
   */
  public String getAttributesString() {
    StringBuffer retVal = new StringBuffer();

    for(Iterator<ConstraintPredicate> iter = predicates.iterator(); iter
            .hasNext();) {
      ConstraintPredicate attr = iter.next();
      retVal.append(attr);
      if(iter.hasNext()) retVal.append(",");
    }

    return retVal.toString();
  }

  public String shortDesc() {
    return getDisplayString("");
  } // shortDesc

  /**
   * Invoke {@link #matches(Annotation, Ontology, AnnotationSet)} on all provided
   * annotations.
   *
   * @param annots collection of Annotations to test
   * @param ontology optional Ontology to compare ont-specific features
   * @return Collection of annotations which matches successfully
   *         against predicates.
   */
  public List<Annotation> matches(Collection<Annotation> annots,
          Ontology ontology, AnnotationSet context) {
    List<Annotation> retVal = new ArrayList<Annotation>();

    if(annots == null) return retVal;

    for(Annotation annot : annots) {
      if(matches(annot, ontology, context)) {
        retVal.add(annot);
        // small optimization - if constraint is negated and there is a
        // match,
        // don't bother checking all the other annots since a negated
        // constraint
        // fails if even a single annotation matches it.
        if(negated) break;
      }
    }

    return retVal;
  }

  /**
   * Test if an annotation is of the proper type for this constraint and
   * if it complies with the {@link ConstraintPredicate}s of this
   * constraint.
   *
   * @param annot an Annotation
   * @param ontologyLR optional ontology to use when comparing
   *          ont-related features
   * @return <code>true</code> if the annotation is of the proper type
   *         and matches all predicates. If the constraint is negated,
   *         an annotation need only match a single predicate to return
   *         true.
   */
  public final boolean matches(Annotation annot, Ontology ontologyLR, 
          AnnotationSet context) {
    if(annot == null) return false;
    if(!annot.getType().equals(getAnnotType())) return false;

    // if no predicates, then we have a match based solely on the annot
    // type.
    if(predicates == null || predicates.isEmpty()) return true;

    for(ConstraintPredicate predicate : predicates) {
      boolean successful = false;
      try {
        // do some special checking if this predicate deals with the
        // ontology.  Note that we assume the operator for the predicate
        // was ==. We issue a warning when the predicate is first set
        // if it's anything else.
        if(predicate == ontLookupClassPred && ontologyLR != null)
          successful = annot.getFeatures().subsumes(ontologyLR, ontFeatureMap);
        else successful = predicate.matches(annot, context);
      } catch(JapeException je) {
        gate.util.Err.println(je.getMessage());
      }
      if(!successful) return false;
      // else, keep checking the rest of the predicates
    } // checked all predicates
    return true;
  }

  /**
   * Test if an annotation is of the proper type for this constraint and
   * if it complies with the {@link ConstraintPredicate}s of this
   * constraint.
   *
   * @param annot a Annotation
   * @return <code>true</code> if the annotation is of the proper type
   *         and matches all predicates. If the constraint is negated,
   *         an annotation need only match a single predicate to return
   *         true.
   */
  public boolean matches(Annotation annot, AnnotationSet context) {
    return matches(annot, null, context);
  }

} // class Constraint

// $Log$
// Revision 1.14 2006/01/06 22:37:24 kwilliams
// Implement equals(Object) and hashCode() so we can usefully be put
// into Sets, HashMaps, etc.
//
// Revision 1.13 2006/01/06 22:03:04 kwilliams
// Define other constructors in terms of Constraint(String,FeatureMap)
//
// Revision 1.12 2005/07/15 15:37:32 valyt
// New toString() method from Ken Williams
//
// Revision 1.11 2005/01/11 13:51:36 ian
// Updating copyrights to 1998-2005 in preparation for v3.0
//
// Revision 1.10 2004/07/21 17:10:07 akshay
// Changed copyright from 1998-2001 to 1998-2004
//
// Revision 1.9 2004/03/25 13:01:14 valyt
// Imports optimisation throughout the Java sources
// (to get rid of annoying warnings in Eclipse)
//
// Revision 1.8 2001/09/13 12:09:49 kalina
// Removed completely the use of jgl.objectspace.Array and such.
// Instead all sources now use the new Collections, typically ArrayList.
// I ran the tests and I ran some documents and compared with keys.
// JAPE seems to work well (that's where it all was). If there are
// problems
// maybe look at those new structures first.
//
// Revision 1.7 2000/11/08 16:35:02 hamish
// formatting
//
// Revision 1.6 2000/10/26 10:45:30 oana
// Modified in the code style
//
// Revision 1.5 2000/10/16 16:44:33 oana
// Changed the comment of DEBUG variable
//
// Revision 1.4 2000/10/10 15:36:35 oana
// Changed System.out in Out and System.err in Err;
// Added the DEBUG variable seted on false;
// Added in the header the licence;
//
// Revision 1.3 2000/05/25 16:10:41 valyt
// JapeGUI is working
//
// Revision 1.2 2000/04/20 13:26:41 valyt
// Added the graph_drawing library.
// Creating of the NFSM and DFSM now works.
//
// Revision 1.1 2000/02/23 13:46:05 hamish
// added
//
// Revision 1.1.1.1 1999/02/03 16:23:01 hamish
// added gate2
//
// Revision 1.8 1998/11/05 13:36:30 kalina
// moved to use array of JdmAttributes for selectNextAnnotation instead
// of a sequence
//
// Revision 1.7 1998/11/01 22:35:56 kalina
// attribute seq hashtable mod
//
// Revision 1.6 1998/09/23 12:48:02 hamish
// negation added; noncontiguous BPEs disallowed
//
// Revision 1.5 1998/08/12 15:39:34 hamish
// added padding toString methods
//
// Revision 1.4 1998/07/31 13:12:14 mks
// done RHS stuff, not tested
//
// Revision 1.3 1998/07/30 11:05:15 mks
// more jape
//
// Revision 1.2 1998/07/29 11:06:55 hamish
// first compiling version
//
// Revision 1.1.1.1  1998/07/28 16:37:46  hamish
// gate2 lives
