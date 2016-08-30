/*
 *  TestConstraints
 *
 *  Copyright (c) 1995-2012, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 *  Eric Sword, 03/09/08
 *
 *  $Id$
 */
package gate.jape.functest;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.Gate;
import gate.corpora.TestDocument;
import gate.jape.Transducer;
import gate.jape.parser.ParseCpsl;
import gate.jape.parser.ParseException;
import gate.util.InvalidOffsetException;
import gate.util.Out;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Set;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.log4j.Logger;

/**
 * Tests for Constraint predicate logic
 */
public class TestConstraints extends BaseJapeTests {
  private static final Logger logger = Logger.getLogger(TestConstraints.class);

  public TestConstraints(String name) {
    super(name);
  }

  /**
   * Try to transduce on empty document
   */
  public void test000TransduceEmptyDoc() throws Exception {
    final String japeFilePath = "/jape/test/japefiles/Req-GATETao-8.1.1.jape";
    Set<Annotation> actualResults = doTest(Factory.newDocument(""), japeFilePath, annoCreatorEmpty, null);
    assertEquals("There must be no transduced annotations.", 0, actualResults.size());
  }

  /**
   * GATE Tao:8.1.1
   * 
   * @throws Exception
   */
  public void test811MatchText() throws Exception {
    final String japeFilePath = "/jape/test/japefiles/Req-GATETao-8.1.1.jape";
    final String docFilePath = "/jape/test/docfiles/Req-GATETao-8.1.1.txt";

    String[] expectedResults = {"SimpleText", "ComplexText", "ComplexText",
        "ComplexText", "ComplexText"};
    int[] expectedStartOffsets = {0, 3, 10, 25, 42};
    int[] expectedEndOffsets = {2, 10, 14, 31, 46};

    AnnotationCreator annocreator = new BaseAnnotationCreator() {
      @Override
      public AnnotationSet createAnnots(Document doc)
              throws InvalidOffsetException {
        FeatureMap feat = Factory.newFeatureMap();
        feat.put("string", "of");
        add(0, 2, "Token", feat);

        feat = Factory.newFeatureMap();
        feat.put("string", "http");
        add(3, 7, "Token", feat);
        feat = Factory.newFeatureMap();
        feat.put("string", ":");
        add(7, 8, "Token", feat);
        feat = Factory.newFeatureMap();
        feat.put("string", "/");
        add(8, 9, "Token", feat);
        feat = Factory.newFeatureMap();
        feat.put("string", "/");
        add(9, 10, "Token", feat);
        feat = Factory.newFeatureMap();
        feat.put("string", "www");
        add(10, 13, "Token", feat);
        feat = Factory.newFeatureMap();
        feat.put("string", ".");
        add(13, 14, "Token", feat);

        feat = Factory.newFeatureMap();
        feat.put("string", "ftp");
        add(25, 28, "Token", feat);
        feat = Factory.newFeatureMap();
        feat.put("string", ":");
        add(28, 29, "Token", feat);
        feat = Factory.newFeatureMap();
        feat.put("string", "/");
        add(29, 30, "Token", feat);
        feat = Factory.newFeatureMap();
        feat.put("string", "/");
        add(30, 31, "Token", feat);
        feat = Factory.newFeatureMap();

        feat = Factory.newFeatureMap();
        feat.put("string", "www");
        add(42, 45, "Token", feat);
        feat = Factory.newFeatureMap();
        feat.put("string", ".");
        add(45, 46, "Token", feat);
        return as;
      }
    };

    doCommonTest(japeFilePath, docFilePath, expectedResults, expectedStartOffsets, expectedEndOffsets, annocreator);
  }

  /**
   * GATE Tao:8.1.1
   * 
   * Trying to match string features on document with no annotations
   * 
   * @throws Exception
   */
  public void test811MatchStringInEmptyAnnotSet() throws Exception {
    final String japeFilePath = "/jape/test/japefiles/Req-GATETao-8.1.1.jape";
    final String docFilePath = "/jape/test/docfiles/Req-GATETao-8.1.1.txt";
    Set<Annotation> actualResults = doTest(docFilePath, japeFilePath, annoCreatorEmpty);
    assertEquals("There must be no transduced annotations.", 0, actualResults.size());
  }

  /**
   * GATE Tao: 8.1.2
   * 
   * @throws Exception
   */
  public void test812MatchAnnot() throws Exception {
    final String japeFilePath = "/jape/test/japefiles/Req-GATETao-8.1.2.jape";
    final String docFilePath = "/jape/test/docfiles/Req-GATETao-8.1.2.txt";

    String[] expectedResults = {"SimpleAnnotation", "SimpleAnnotation"};
    int[] expectedStartOffsets = {0, 100};
    int[] expectedEndOffsets = {52, 136};

    doCommonTest(japeFilePath, docFilePath, expectedResults, expectedStartOffsets, expectedEndOffsets, annoCreator81LocOrgDateJob);
  }

  /**
   * GATE Tao: 8.1.2
   * 
   * Trying to match annotations on document with no annotations
   * 
   * @throws Exception
   */
  public void test812MatchNegativeAnnotationsInEmptyAnnotSet() throws Exception {
    final String japeFilePath = "/jape/test/japefiles/Req-GATETao-8.1.2-Negatives.jape";
    final String docFilePath = "/jape/test/docfiles/Req-GATETao-8.1.2.txt";
    Set<Annotation> actualResults = doTest(docFilePath, japeFilePath, annoCreatorEmpty);
    assertEquals("There must be no transduced annotations.", 0, actualResults.size());
  }

  /**
   * GATE Tao: 8.1.2
   * 
   * Matching negation on annotation (e.g {!Lookup}) on a document with annotations
   * 
   * @throws Exception
   */
  public void test812MatchNegativeAnnotations() throws Exception {
    final String japeFilePath = "/jape/test/japefiles/Req-GATETao-8.1.2-Negatives.jape";
    final String docFilePath = "/jape/test/docfiles/Req-GATETao-8.1.2.txt";

    String[] expectedResults = {"NegativeAnnotation", "NegativeAnnotation", "NegativeAnnotation","NegativeAnnotation", "NegativeAnnotation", "NegativeAnnotation","NegativeAnnotation", "NegativeAnnotation", "NegativeAnnotation"};
    int[] expectedStartOffsets = {15, 24, 35, 68, 77, 88, 110, 115, 126};
    int[] expectedEndOffsets = {23, 34, 52, 76, 87, 99, 114, 125, 136};

    doCommonTest(japeFilePath, docFilePath, expectedResults, expectedStartOffsets, expectedEndOffsets, annoCreator81LocOrgDateJob);
  }

  /**
   * GATE Tao: 8.1.3
   * 
   * LHS Operators:  contains
   * @throws Exception 
   * 
   * 
   */
  public void test813OpContains() throws Exception {
    final String japeFilePath = "/jape/test/japefiles/Req-GATETao-8.1.3-LHSOP_contains.jape";
    final String docFilePath = "/jape/test/docfiles/Req-GATETao-8.1.2.txt";

    String[] expectedResults = {"OpContains", "OpContains", "OpContains"};
    int[] expectedStartOffsets = {0, 53, 100};
    int[] expectedEndOffsets = {14, 67, 109};

    doCommonTest(japeFilePath, docFilePath, expectedResults, expectedStartOffsets, expectedEndOffsets, annoCreator81LocOrgDateJob);
  }
  

  
  /**
   * GATE Tao: 8.1.3
   * 
   * LHS Operators:  within
   * @throws Exception 
   * 
   */
  public void test813OpWithin() throws Exception {
    final String japeFilePath = "/jape/test/japefiles/Req-GATETao-8.1.3-LHSOP_within.jape";
    final String docFilePath = "/jape/test/docfiles/Req-GATETao-8.1.2.txt";

    String[] expectedResults = {"OpWithin", "OpWithin", "OpWithin"};
    int[] expectedStartOffsets = {0, 53, 100};
    int[] expectedEndOffsets = {14, 67, 109};

    doCommonTest(japeFilePath, docFilePath, expectedResults, expectedStartOffsets, expectedEndOffsets, annoCreator81LocOrgDateJob);
  }
  
  /**
   * GATE Tao: 8.1.3
   * 
   * LHS Operators:  notWithin
   * @throws Exception 
   * 
   */
  public void test813OpNotWithin() throws Exception {
    final String japeFilePath = "/jape/test/japefiles/Req-GATETao-8.1.3-LHSOP_notWithin.jape";
    final String docFilePath = "/jape/test/docfiles/Req-GATETao-8.1.5.txt";

    String[] expectedResults = {"OpNotWithin"};
    int[] expectedStartOffsets = {5};
    int[] expectedEndOffsets = {11};
    doCommonTest(japeFilePath, docFilePath, expectedResults, expectedStartOffsets, expectedEndOffsets, annoCreator815MultipleActions);
  }
  
  /**
   * GATE Tao: 8.1.3
   * 
   * LHS Operators:  notContains
   * @throws Exception 
   * 
   */
  public void test813OpNotContains() throws Exception {
    final String japeFilePath = "/jape/test/japefiles/Req-GATETao-8.1.3-LHSOP_notContains.jape";
    final String docFilePath = "/jape/test/docfiles/Req-GATETao-8.1.5.txt";

    String[] expectedResults = {"OpNotContains","OpNotContains"};
    int[] expectedStartOffsets = {0,12};
    int[] expectedEndOffsets = {4,20};
    doCommonTest(japeFilePath, docFilePath, expectedResults, expectedStartOffsets, expectedEndOffsets, annoCreator815MultipleActions);
  }
  
  /**
   * GATE Tao: 8.1.3
   * 
   * LHS Operators:  contains & within in document with no annotations
   * @throws Exception 
   * 
   */
  public void test813OpContainWithinNoMatch() throws Exception {
    final String japeContainsFilePath = "/jape/test/japefiles/Req-GATETao-8.1.3-LHSOP_contains.jape";
    final String japeWithinFilePath =  "/jape/test/japefiles/Req-GATETao-8.1.3-LHSOP_within.jape";
    final String docFilePath = "/jape/test/docfiles/Req-GATETao-8.1.2.txt";

    Set<Annotation> actualResults = doTest(docFilePath, japeWithinFilePath, annoCreatorEmpty);
    assertEquals("There must be no transduced annotations.", 0, actualResults.size());
    
    actualResults = doTest(docFilePath, japeContainsFilePath, annoCreatorEmpty);
    assertEquals("There must be no transduced annotations.", 0, actualResults.size());
  }
  
  /**
   * Gate Tao: 8.1.3
   * 
   * Operator: ==
   * Case: Special characters in the rule and text (quote here)
   * 
   * @throws Exception
   */
  public void test813OpEqualsStringSpecialChars() throws Exception {
    final String japeFilePath = "/jape/test/japefiles/Req-GATETao-8.1.3-op-equals-string-wquot.jape";
    final String docFilePath = "/jape/test/docfiles/Req-GATETao-8.1.3.txt";
    
    String[] expectedResults = {"OpEquals"};
    int[] expectedStartOffsets = {5};
    int[] expectedEndOffsets = {12};

    doCommonTest(japeFilePath, docFilePath, expectedResults, expectedStartOffsets, expectedEndOffsets, annoCreator813Operators);
  }
  
  /**
   * Gate Tao: 8.1.3
   * 
   * Operator: ==
   * Case: Emty String feature
   * 
   * @throws Exception
   */
  public void test813OpEqualsStringEmptyFeature() throws Exception {
    final String japeFilePath = "/jape/test/japefiles/Req-GATETao-8.1.3-op-equals-string-ef.jape";
    final String docFilePath = "/jape/test/docfiles/Req-GATETao-8.1.3.txt";
    
    String[] expectedResults = {"OpEquals", "OpEquals"};
    int[] expectedStartOffsets = {1, 35};
    int[] expectedEndOffsets = {5, 43};
    
    AnnotationCreator operatorsAnnoCreator = new BaseAnnotationCreator() {
      @Override
      public AnnotationSet createAnnots(Document doc) throws InvalidOffsetException {
        FeatureMap feat = Factory.newFeatureMap();
        feat.put("string", "");
        add(1, 5, "Token", feat);
        
        feat = Factory.newFeatureMap();
        feat.put("string", "qu\"oted");
        add(6, 13, "Token", feat);
        
        feat = Factory.newFeatureMap();
        feat.put("int", 101l);
        add(14, 17, "Token", feat);
        
        feat = Factory.newFeatureMap();
        feat.put("int", -10);
        add(18, 21, "Token", feat);
        
        feat = Factory.newFeatureMap();
        feat.put("double", 3.14f);
        add(22, 26, "Token", feat);
        
        feat = Factory.newFeatureMap();
        feat.put("double", -273.15);
        add(27, 34, "Token", feat);
        
        feat = Factory.newFeatureMap();
        feat.put("string", "");
        add(35, 43, "Token", feat);
        return as;
      }
    };

    doCommonTest(japeFilePath, docFilePath, expectedResults, expectedStartOffsets, expectedEndOffsets, operatorsAnnoCreator);
  }
  
  /**
   * GATE Tao: 8.1.3
   * 
   * Operator: <
   * Case: Integer
   * 
   * @throws Exception
   */
  public void test813OpLessThanIntegerNegative() throws Exception {
    final String japeFilePath = "/jape/test/japefiles/Req-GATETao-8.1.3-op-lt-int-negative.jape";
    final String docFilePath = "/jape/test/docfiles/Req-GATETao-8.1.3.txt";
    
    String[] expectedResults = {"OpLessThan"};
    int[] expectedStartOffsets = {17};
    int[] expectedEndOffsets = {20};
    doCommonTest(japeFilePath, docFilePath, expectedResults, expectedStartOffsets, expectedEndOffsets, annoCreator813Operators);
  }
  
  /**
   * GATE Tao: 8.1.3
   * 
   * Operator: <
   * Case: Double
   * 
   * @throws Exception
   */
  public void test813OpLessThanDoubleNegative() throws Exception {
    final String japeFilePath = "/jape/test/japefiles/Req-GATETao-8.1.3-op-lt-double-negative.jape";
    final String docFilePath = "/jape/test/docfiles/Req-GATETao-8.1.3.txt";
    
    String[] expectedResults = {"OpLessThan", "OpLessThan"};
    int[] expectedStartOffsets = {17, 26};
    int[] expectedEndOffsets = {20, 33};
    doCommonTest(japeFilePath, docFilePath, expectedResults, expectedStartOffsets, expectedEndOffsets, annoCreator813Operators);
  }
  
  /**
   * GATE Tao: 8.1.3
   * 
   * Operator: <=
   * Case: Integer
   * 
   * @throws Exception
   */
  public void test813OpLessEqualsIntegerNegative() throws Exception {
    final String japeFilePath = "/jape/test/japefiles/Req-GATETao-8.1.3-op-le-int-negative.jape";
    final String docFilePath = "/jape/test/docfiles/Req-GATETao-8.1.3.txt";
    
    String[] expectedResults = {"OpLessThan"};
    int[] expectedStartOffsets = {17};
    int[] expectedEndOffsets = {20};
    doCommonTest(japeFilePath, docFilePath, expectedResults, expectedStartOffsets, expectedEndOffsets, annoCreator813Operators);
  }
  
  /**
   * GATE Tao: 8.1.3
   * 
   * Operator: <=
   * Case: Double
   * 
   * @throws Exception
   */
  public void test813OpLessEqualsDoubleNegative() throws Exception {
    final String japeFilePath = "/jape/test/japefiles/Req-GATETao-8.1.3-op-le-double-negative.jape";
    final String docFilePath = "/jape/test/docfiles/Req-GATETao-8.1.3.txt";
    
    String[] expectedResults = {"OpLessThan"};
    int[] expectedStartOffsets = {26};
    int[] expectedEndOffsets = {33};
    doCommonTest(japeFilePath, docFilePath, expectedResults, expectedStartOffsets, expectedEndOffsets, annoCreator813Operators);
  }
  
  /**
   * GATE Tao: 8.1.3
   * 
   * Operator: ==
   * Case: Natural number
   * 
   * @throws Exception
   */
  public void test813OpEqualsIntegerNatural() throws Exception {
    final String japeFilePath = "/jape/test/japefiles/Req-GATETao-8.1.3-op-equals-int-natural.jape";
    final String docFilePath = "/jape/test/docfiles/Req-GATETao-8.1.3.txt";
    
    String[] expectedResults = {"OpEquals"};
    int[] expectedStartOffsets = {13};
    int[] expectedEndOffsets = {16};
  
    doCommonTest(japeFilePath, docFilePath, expectedResults, expectedStartOffsets, expectedEndOffsets, annoCreator813Operators);
  }
  
  /**
   * GATE Tao: 8.1.3
   * 
   * Operator: ==
   * Case: Negative integer
   * 
   * @throws Exception
   */
  public void test813OpEqualsIntegerNegative() throws Exception {
    final String japeFilePath = "/jape/test/japefiles/Req-GATETao-8.1.3-op-equals-int-negative.jape";
    final String docFilePath = "/jape/test/docfiles/Req-GATETao-8.1.3.txt";
    
    String[] expectedResults = {"OpEquals"};
    int[] expectedStartOffsets = {17};
    int[] expectedEndOffsets = {20};
  
    doCommonTest(japeFilePath, docFilePath, expectedResults, expectedStartOffsets, expectedEndOffsets, annoCreator813Operators);
  }
  
  /**
   * GATE Tao: 8.1.3
   * 
   * Operator: ==
   * Case: Positive real number
   * 
   * @throws Exception
   */
  public void test813OpEqualsDoublePositive() throws Exception {
    final String japeFilePath = "/jape/test/japefiles/Req-GATETao-8.1.3-op-equals-double-positive.jape";
    final String docFilePath = "/jape/test/docfiles/Req-GATETao-8.1.3.txt";
    
    String[] expectedResults = {"OpEquals"};
    int[] expectedStartOffsets = {21};
    int[] expectedEndOffsets = {25};
  
    doCommonTest(japeFilePath, docFilePath, expectedResults, expectedStartOffsets, expectedEndOffsets, annoCreator813Operators);
  }
  
  /**
   * GATE Tao: 8.1.3
   * 
   * Operator: ==
   * Case: Negative real number
   * 
   * @throws Exception
   */
  public void test813OpEqualsDoubleNegative() throws Exception {
    final String japeFilePath = "/jape/test/japefiles/Req-GATETao-8.1.3-op-equals-double-negative.jape";
    final String docFilePath = "/jape/test/docfiles/Req-GATETao-8.1.3.txt";
    
    String[] expectedResults = {"OpEquals"};
    int[] expectedStartOffsets = {26};
    int[] expectedEndOffsets = {33};
  
    doCommonTest(japeFilePath, docFilePath, expectedResults, expectedStartOffsets, expectedEndOffsets, annoCreator813Operators);
  }
  
  
  /**
   * GATE Tao: 8.1.3
   * 
   * Operator: >
   * Case: String
   * 
   * @throws Exception
   */
  public void test813OpGreaterThanString() throws Exception {
    final String japeFilePath = "/jape/test/japefiles/Req-GATETao-8.1.3-op-gt-string.jape";
    final String docFilePath = "/jape/test/docfiles/Req-GATETao-8.1.3.txt";
    
    String[] expectedResults = {"OpGreaterThan", "OpGreaterThan", "OpGreaterThan"};
    int[] expectedStartOffsets = {0, 5 ,34};
    int[] expectedEndOffsets = {4, 12, 42};
  
    doCommonTest(japeFilePath, docFilePath, expectedResults, expectedStartOffsets, expectedEndOffsets, annoCreator813Operators);
  }
  
  /**
   * GATE Tao: 8.1.3
   * 
   * Operator: >
   * Case: Natural Number
   * 
   * @throws Exception
   */
  public void test813OpGreaterThanNaturalNumber() throws Exception {
    final String japeFilePath = "/jape/test/japefiles/Req-GATETao-8.1.3-op-gt-int-positive.jape";
    final String docFilePath = "/jape/test/docfiles/Req-GATETao-8.1.3.txt";
    
    String[] expectedResults = {"OpGreaterThan", "OpGreaterThan"};
    int[] expectedStartOffsets = {13, 21};
    int[] expectedEndOffsets = {16, 25};
  
    doCommonTest(japeFilePath, docFilePath, expectedResults, expectedStartOffsets, expectedEndOffsets, annoCreator813Operators);
  }
  
  
  /**
   * GATE Tao: 8.1.3
   * 
   * Operator: >
   * Case: Negative Integer
   * 
   * @throws Exception
   */
  public void test813OpGreaterThanNegativeInteger() throws Exception {
    final String japeFilePath = "/jape/test/japefiles/Req-GATETao-8.1.3-op-gt-int-negative.jape";
    final String docFilePath = "/jape/test/docfiles/Req-GATETao-8.1.3.txt";
    
    String[] expectedResults = {"OpGreaterThan", "OpGreaterThan", "OpGreaterThan"};
    int[] expectedStartOffsets = {13, 17, 21};
    int[] expectedEndOffsets = {16, 20, 25};
  
    doCommonTest(japeFilePath, docFilePath, expectedResults, expectedStartOffsets, expectedEndOffsets, annoCreator813Operators);
  }
  
  /**
   * GATE Tao: 8.1.3
   * 
   * Operator: >
   * Case: Positive Real Number
   * 
   * @throws Exception
   */
  public void test813OpGreaterThanPositiveDouble() throws Exception {
    final String japeFilePath = "/jape/test/japefiles/Req-GATETao-8.1.3-op-gt-double-positive.jape";
    final String docFilePath = "/jape/test/docfiles/Req-GATETao-8.1.3.txt";
    
    String[] expectedResults = {"OpGreaterThan", "OpGreaterThan"};
    int[] expectedStartOffsets = {13, 21};
    int[] expectedEndOffsets = {16, 25};
  
    doCommonTest(japeFilePath, docFilePath, expectedResults, expectedStartOffsets, expectedEndOffsets, annoCreator813Operators);
  }
  
  /**
   * GATE Tao: 8.1.3
   * 
   * Operator: >
   * Case: Negative Real Number
   * 
   * @throws Exception
   */
  public void test813OpGreaterThanNegativeDouble() throws Exception {
    final String japeFilePath = "/jape/test/japefiles/Req-GATETao-8.1.3-op-gt-double-negative.jape";
    final String docFilePath = "/jape/test/docfiles/Req-GATETao-8.1.3.txt";
    
    String[] expectedResults = {"OpGreaterThan", "OpGreaterThan", "OpGreaterThan", "OpGreaterThan"};
    int[] expectedStartOffsets = {13, 17, 21, 26};
    int[] expectedEndOffsets = {16, 20, 25, 33};
  
    doCommonTest(japeFilePath, docFilePath, expectedResults, expectedStartOffsets, expectedEndOffsets, annoCreator813Operators);
  }
  
  /**
   * GATE Tao: 8.1.3
   * 
   * Operator: >=
   * Case: String
   * 
   * @throws Exception
   */
  public void test813OpGreaterEqualsString() throws Exception {
    final String japeFilePath = "/jape/test/japefiles/Req-GATETao-8.1.3-op-ge-string.jape";
    final String docFilePath = "/jape/test/docfiles/Req-GATETao-8.1.3.txt";
    
    String[] expectedResults = {"OpGreaterEquals"};
    int[] expectedStartOffsets = {0};
    int[] expectedEndOffsets = {4};
  
    doCommonTest(japeFilePath, docFilePath, expectedResults, expectedStartOffsets, expectedEndOffsets, annoCreator813Operators);
  }
  
  /**
   * GATE Tao: 8.1.3
   * 
   * Operator: >=
   * Case: Positive Integer
   * 
   * @throws Exception
   */
  public void test813OpGreaterEqualsPositiveInteger() throws Exception {
    final String japeFilePath = "/jape/test/japefiles/Req-GATETao-8.1.3-op-ge-int-positive.jape";
    final String docFilePath = "/jape/test/docfiles/Req-GATETao-8.1.3.txt";
    
    String[] expectedResults = {"OpGreaterEquals"};
    int[] expectedStartOffsets = {13};
    int[] expectedEndOffsets = {16};
  
    doCommonTest(japeFilePath, docFilePath, expectedResults, expectedStartOffsets, expectedEndOffsets, annoCreator813Operators);
  }
  
  /**
   * GATE Tao: 8.1.3
   * 
   * Operator: >=
   * Case: Negative Integer
   * 
   * @throws Exception
   */
  public void test813OpGreaterEqualsNegativeInteger() throws Exception {
    final String japeFilePath = "/jape/test/japefiles/Req-GATETao-8.1.3-op-ge-int-negative.jape";
    final String docFilePath = "/jape/test/docfiles/Req-GATETao-8.1.3.txt";
    
    String[] expectedResults = {"OpGreaterEquals", "OpGreaterEquals", "OpGreaterEquals"};
    int[] expectedStartOffsets = {13, 17, 21};
    int[] expectedEndOffsets = {16, 20, 25};
  
    doCommonTest(japeFilePath, docFilePath, expectedResults, expectedStartOffsets, expectedEndOffsets, annoCreator813Operators);
  }
 
  /**
   * GATE Tao: 8.1.3
   * 
   * Operator: >=
   * Case: Positive Real Number
   * 
   * @throws Exception
   */
  public void test813OpGreaterEqualsPositiveDouble() throws Exception {
    final String japeFilePath = "/jape/test/japefiles/Req-GATETao-8.1.3-op-ge-double-positive.jape";
    final String docFilePath = "/jape/test/docfiles/Req-GATETao-8.1.3.txt";
    
    String[] expectedResults = {"OpGreaterEquals", "OpGreaterEquals"};
    int[] expectedStartOffsets = {13, 21};
    int[] expectedEndOffsets = {16, 25};
  
    doCommonTest(japeFilePath, docFilePath, expectedResults, expectedStartOffsets, expectedEndOffsets, annoCreator813Operators);
  }
  
  /**
   * GATE Tao: 8.1.3
   * 
   * Operator: >=
   * Case: Negative Real Number
   * 
   * @throws Exception
   */
  public void test813OpGreaterEqualsNegativeDouble() throws Exception {
    final String japeFilePath = "/jape/test/japefiles/Req-GATETao-8.1.3-op-ge-double-negative.jape";
    final String docFilePath = "/jape/test/docfiles/Req-GATETao-8.1.3.txt";
    
    String[] expectedResults = {"OpGreaterEquals", "OpGreaterEquals", "OpGreaterEquals", "OpGreaterEquals"};
    int[] expectedStartOffsets = {13, 17, 21, 26};
    int[] expectedEndOffsets = {16, 20, 25, 33};
  
    doCommonTest(japeFilePath, docFilePath, expectedResults, expectedStartOffsets, expectedEndOffsets, annoCreator813Operators);
  }
  
  /**
   *  GATE Tao: 8.1.4
   * 
   * Meta-Property: LHS length
   * 
   * @throws Exception
   */
  public void test814MetaLeftLength() throws Exception {
    final String japeFilePath = "/jape/test/japefiles/Req-GATETao-8.1.4-meta-length.jape";
    final String docFilePath = "/jape/test/docfiles/Req-GATETao-8.1.4.txt";
    
    String[] expectedResults = {"MetaLength"};
    int[] expectedStartOffsets = {0};
    int[] expectedEndOffsets = {60};
    
    doCommonTest(japeFilePath, docFilePath, expectedResults, expectedStartOffsets, expectedEndOffsets, annoCreator814MetaProps);
    }
  
  /**
   *  GATE Tao: 8.1.4
   * 
   * Meta-Property: LHS string
   * 
   * @throws Exception
   */
  public void test814MetaLeftString() throws Exception {
    final String japeFilePath = "/jape/test/japefiles/Req-GATETao-8.1.4-meta-string.jape";
    final String docFilePath = "/jape/test/docfiles/Req-GATETao-8.1.4.txt";
    
    String[] expectedResults = {"MetaString"};
    int[] expectedStartOffsets = {0};
    int[] expectedEndOffsets = {60};
    
    doCommonTest(japeFilePath, docFilePath, expectedResults, expectedStartOffsets, expectedEndOffsets, annoCreator814MetaProps);
  }
  
  /**
   *  GATE Tao: 8.1.4
   * 
   * Meta-Property: LHS cleanString
   * 
   * @throws Exception
   */
  public void test814MetaLeftCleanString() throws Exception {
    final String japeFilePath = "/jape/test/japefiles/Req-GATETao-8.1.4-meta-cleanString.jape";
    final String docFilePath = "/jape/test/docfiles/Req-GATETao-8.1.4.txt";
    
    String[] expectedResults = {"MetaCleanString"};
    int[] expectedStartOffsets = {0};
    int[] expectedEndOffsets = {60};
    
    doCommonTest(japeFilePath, docFilePath, expectedResults, expectedStartOffsets, expectedEndOffsets, annoCreator814MetaProps);
  }
  
  /**
   *  GATE Tao: 8.1.4
   * 
   * Meta-Property: RHS string
   * 
   * @throws Exception
   */
  public void test814MetaRightString() throws Exception {
    final String japeFilePath = "/jape/test/japefiles/Req-GATETao-8.1.4-meta-rhs-string.jape";
    final String docFilePath = "/jape/test/docfiles/Req-GATETao-8.1.4.txt";
    
    String[] expectedResults = {"MetaRightString"};
    int[] expectedStartOffsets = {0};
    int[] expectedEndOffsets = {60};

    Set<Annotation> actualResults = doTest(docFilePath, japeFilePath, annoCreator814MetaProps);
    
    compareResults(expectedResults, actualResults);
    compareStartOffsets(actualResults, expectedStartOffsets);
    compareEndOffsets(actualResults, expectedEndOffsets);
    
    FeatureMap feats = actualResults.iterator().next().getFeatures();
    Object oValue = feats.get("value");
    assertNotNull("Annotation features must contain \"value\" feature", oValue);
    assertEquals("RHS value must be equals to matched text", "     and when    the   lamb opened  the     seventh     seal", (String)oValue);
  
  }
  
  /**
   * GATE Tao: 8.1.5
   * 
   * Multiple Patterns/Actions - Sequential
   * 
   * @throws Exception
   */
  public void test815MultiPatternActionsSequential() throws Exception{
    final String japeFilePath = "/jape/test/japefiles/Req-GATETao-8.1.5-multipat-seq.jape";
    final String docFilePath = "/jape/test/docfiles/Req-GATETao-8.1.5.txt";
    
    String[] expectedResults = {"PersonJobTitle1", "PersonJobTitle2"};
    int[] expectedStartOffsets = {5, 12};
    int[] expectedEndOffsets = {11, 20};
    
    doCommonTest(japeFilePath, docFilePath, expectedResults, expectedStartOffsets, expectedEndOffsets, annoCreator815MultipleActions);
  
  }
  
  /**
   * GATE Tao: 8.1.5
   * 
   * Multiple Patterns/Actions - Nested
   * 
   * @throws Exception
   */
  public void test815MultiPatternActionsNested() throws Exception{
    final String japeFilePath = "/jape/test/japefiles/Req-GATETao-8.1.5-multipat-nest.jape";
    final String docFilePath = "/jape/test/docfiles/Req-GATETao-8.1.5.txt";
    
    String[] expectedResults = {"PersonJobTitle1", "PersonJobTitle2"};
    int[] expectedStartOffsets = {5, 5};
    int[] expectedEndOffsets = {11, 20};
    
    doCommonTest(japeFilePath, docFilePath, expectedResults, expectedStartOffsets, expectedEndOffsets, annoCreator815MultipleActions);
  }
  
  
  /**
   * GATE Tao: 8.1.6
   * 
   * LHS Macros
   * 
   * @throws Exception
   */
  public void test816Macro() throws Exception{
    final String japeFilePath = "/jape/test/japefiles/Req-GATETao-8.1.6-macro.jape";
    final String docFilePath = "/jape/test/docfiles/Req-GATETao-8.1.6.txt";
    
    String[] expectedResults = {"MoneyCurrencyUnit", "MoneyCurrencyUnit"};
    int[] expectedStartOffsets = {31, 49};
    int[] expectedEndOffsets = {44, 57};
    
    AnnotationCreator annoCreator816Macros = new BaseAnnotationCreator() {
      @Override
      public AnnotationSet createAnnots(Document doc) throws InvalidOffsetException {
        final String token = "Token";
        FeatureMap feats = Factory.newFeatureMap();
        feats.put("kind", "number");
        add(31, 32, token, feats);
        add(33, 36, token, feats);
        add(49, 53, token, feats);
        
        feats = Factory.newFeatureMap();
        feats.put("string", ",");
        add(32,33, token, feats);
        
        feats = Factory.newFeatureMap();
        feats.put("string", "K");
        add(36, 36, token, feats);

        feats = Factory.newFeatureMap();
        feats.put("majorType", "currency_unit");
        add(41, 44, "Lookup", feats);
        
        feats = Factory.newFeatureMap();
        feats.put("majorType", "currency_unit");
        add(54, 57, "Lookup", feats);
        return as;
      }
    };
    doCommonTest(japeFilePath, docFilePath, expectedResults, expectedStartOffsets, expectedEndOffsets, annoCreator816Macros);
  }
  
  /**
   * GATE Tao: 8.1.7
   * 
   * Contexts
   * 
   * @throws Exception
   */
  public void test817ContextForeAndAft() throws Exception{
    final String japeFilePath = "/jape/test/japefiles/Req-GATETao-8.1.7-ctx-foreaft.jape";
    final String docFilePath = "/jape/test/docfiles/Req-GATETao-8.1.7.txt";
    
    String[] expectedResults = {"Emailaddress1"};
    int[] expectedStartOffsets = {9};
    int[] expectedEndOffsets = {25};
    
    
    doCommonTest(japeFilePath, docFilePath, expectedResults, expectedStartOffsets, expectedEndOffsets, annoCreator817Contexts);
  }
  
  /**
   * GATE Tao: 8.1.8
   * 
   * Contexts
   * 
   * @throws Exception
   */
  public void test818ContextBeginsAt() throws Exception{
    final String japeFilePath = "/jape/test/japefiles/Req-GATETao-8.1.7-ctx-begins.jape";
    final String docFilePath = "/jape/test/docfiles/Req-GATETao-8.1.7.txt";
    
    String[] expectedResults = {"SurnameStartingWithDe"};
    int[] expectedStartOffsets = {58};
    int[] expectedEndOffsets = {66};
    
    doCommonTest(japeFilePath, docFilePath, expectedResults, expectedStartOffsets, expectedEndOffsets, annoCreator817Contexts);
  }
  
  
  public void testGoodOperators() throws Exception {
    String japeFile = "/jape/operators/operator_tests.jape";
    String[] expectedResults = {"AndEqual", "RegExMatch",
        "NotEqualandGreaterEqual", "NotEqual", "EqualAndNotEqualRegEx",
        "EqualAndNotExistance", "OntoTest", "OntoTest2"};

    Set<Annotation> actualResults = doTest(DEFAULT_DATA_FILE, japeFile,
            basicAnnotCreator, TestDocument.getTestServerName() + "tests/demo.owl");
    Out.println(actualResults);
    compareResults(expectedResults, actualResults);
  }

  public void testBadCompare() throws Exception {
    String japeFile = "/jape/operators/bad_operator_tests.jape";

    Set<Annotation> orderedResults = doTest(DEFAULT_DATA_FILE, japeFile,
            basicAnnotCreator);
    assertTrue("No results should be found", orderedResults.isEmpty());
  }

  public void testBadPattern() throws Exception {
    final String JAPE_PREFIX = "Phase: first\n Options: control = appelt\nRule: RuleOne\n";
    String japeString = JAPE_PREFIX + "({A.f1=~\"[a.\"}):abc" + "-->{}";

    try {
      parseJapeString(japeString);
      assertTrue("Should have thrown exception for bad grammer", false);
    }
    catch(ParseException e) {
      Out.println(e.getMessage());
    }

    japeString = JAPE_PREFIX + "({A.f1=~[a.}):abc" + "-->{}";
    try {
      parseJapeString(japeString);
      assertTrue("Should have thrown exception for bad grammer", false);
    }
    catch(ParseException e) {
      Out.println(e.getMessage());
      // insert test of error message if really want
    }
  }

  public void testMetaPropertyAccessors() throws Exception {
    String data = "foo bar blah word4    word5  ";
    String japeFile = "/jape/operators/meta_property_tests.jape";
    String[] expectedResults = {"LengthAccessorEqual", "StringAccessorEqual",
        "CleanStringAccessorEqual"};

    AnnotationCreator ac = new BaseAnnotationCreator() {
      @Override
      public AnnotationSet createAnnots(Document doc)
              throws InvalidOffsetException {
        FeatureMap feat = Factory.newFeatureMap();
        feat.put("f1", "aval");
        feat.put("f2", "2");
        feat.put("f3", 3);
        add(4, 7, "A", feat);

        add(8, 12, "A");
        add(12, 28, "B");
        return as;
      }
    };

    Set<Annotation> actualResults = doTest(Factory.newDocument(data), japeFile,
            ac, null);
    Out.println(actualResults);
    compareResults(expectedResults, actualResults);
  }

  public void testCustomPredicates() throws Exception {
    String japeFile = "/jape/operators/custom_predicates_tests.jape";
    String[] expectedResults = {"Contains", "IsContained", "Contains",
        "IsContained"};

    AnnotationCreator ac = new BaseAnnotationCreator() {
      @Override
      public AnnotationSet createAnnots(Document doc)
              throws InvalidOffsetException {
        add(4, 7, "A");

        // this feature map isn't necessary. Just including it
        // to make sure it doesn't interfere with anything.
        FeatureMap feat = Factory.newFeatureMap();
        feat.put("f2", "bar");
        add(5, 6, "B", feat);

        add(12, 28, "F");
        add(14, 20, "E");

        // test exact length matches
        add(30, 35, "A");
        add(30, 35, "B");

        add(40, 45, "F");
        add(40, 45, "E");

        // these shouldn't match
        add(36, 37, "A");
        add(36, 38, "B");
        add(37, 38, "B");

        add(40, 45, "F");
        add(40, 46, "E");
        add(41, 46, "E");

        return as;
      }
    };

    Set<Annotation> actualResults = doTest(DEFAULT_DATA_FILE, japeFile, ac);
    Out.println(actualResults);
    compareResults(expectedResults, actualResults);
  }

  public void testCustomPredicatesWithConstraints() throws Exception {
    String japeFile = "/jape/operators/custom_predicates_tests.jape";
    String[] expectedResults = {"ContainsWithConstraints",
        "ContainsWithMetaProperty"};

    AnnotationCreator ac = new BaseAnnotationCreator() {
      @Override
      public AnnotationSet createAnnots(Document doc)
              throws InvalidOffsetException {
        FeatureMap cFeat = Factory.newFeatureMap();
        cFeat.put("f1", "foo");
        add(4, 7, "C", cFeat);
        add(4, 8, "C", Factory.newFeatureMap());

        FeatureMap bFeat = Factory.newFeatureMap();
        bFeat.put("f2", "bar");
        add(5, 6, "B", bFeat);

        // this combo won't work because B doesn't have the feature and
        // isn't long enough
        add(8, 10, "C", cFeat);
        add(8, 9, "B");

        add(11, 13, "C");
        // a combo that should work
        add(12, 16, "C", cFeat);
        add(12, 15, "C");
        add(12, 16, "B");

        // here's one with no B at all
        add(17, 20, "C", cFeat);

        return as;
      }
    };

    Set<Annotation> actualResults = doTest(DEFAULT_DATA_FILE, japeFile, ac);
    Out.println(actualResults);
    compareResults(expectedResults, actualResults);
  }

  public void testRanges() throws Exception {
    String japeFile = "/jape/operators/range_tests.jape";
    String[] expectedResults = {"OneToTwoB", "ThreeA", "OneToTwoB",
        "ZeroToThreeC", "ThreeToFourB", "ThreeToFourB", "ZeroToThreeC",
        "ZeroToThreeC"};

    AnnotationCreator ac = new BaseAnnotationCreator() {
      @Override
      public AnnotationSet createAnnots(Document doc)
              throws InvalidOffsetException {

        // OneToTwoB check
        addInc("F");
        addInc("B");
        addInc("G");

        // ThreeA check
        addInc("A");
        addInc("A");
        addInc("A");

        // should not trigger OneToTwoB
        addInc("F");
        addInc("G");

        // ThreeA check - should not match
        addInc("A");
        addInc("A");

        // OneToTwoB - trigger it once for two different variants
        addInc("F");
        addInc("B");
        addInc("G");
        addInc("F");
        addInc("B");
        addInc("B");
        addInc("G");

        // ZeroToThreeC check - no Cs
        addInc("D");
        addInc("E");

        // ThreeToFourB
        addInc("F");
        addInc("B");
        addInc("B");
        addInc("B");
        addInc("G");
        addInc("F");
        addInc("B");
        addInc("B");
        addInc("B");
        addInc("B");
        addInc("G");

        // ZeroToThreeC check - 1 C
        addInc("D");
        addInc("C");
        addInc("E");

        // ZeroToThreeC check - 3 C
        addInc("D");
        addInc("C");
        addInc("C");
        addInc("C");
        addInc("E");

        // ZeroToThreeC check - 4 C = no match
        addInc("D");
        addInc("C");
        addInc("C");
        addInc("C");
        addInc("C");
        addInc("E");

        return as;
      }
    };

    Set<Annotation> actualResults = doTest(DEFAULT_DATA_FILE, japeFile, ac);
    Out.println(actualResults);
    compareResults(expectedResults, actualResults);
  }

  public void testKleeneOperators() throws Exception {
    String japeFile = "/jape/operators/kleene_tests.jape";
    String[] expectedResults = {"OptionalB", "PlusA", "OptionalB", "PlusA",
        "StarC", "StarC", "StarC"};

    AnnotationCreator ac = new BaseAnnotationCreator() {
      @Override
      public AnnotationSet createAnnots(Document doc)
              throws InvalidOffsetException { // OptionalB check
         addInc("C");
        addInc("B");
        addInc("C");

        // PlusA check
        addInc("A");
        addInc("A");
        addInc("A");

        // OptionalB check
        addInc("C");
        addInc("C");
        // PlusA
        // addInc("A");
        addInc("A");

        // no match
        addInc("B");

        // StarC
        addInc("D");
        addInc("E");

        // StarC
        addInc("D");
        addInc("C");
        addInc("E");

        // StarC
        addInc("D");
        addInc("C");
        addInc("C");
        addInc("C");
        addInc("E");

        return as;
      }
    };

    Set<Annotation> actualResults = doTest(DEFAULT_DATA_FILE, japeFile, ac);
    Out.println(actualResults);
    compareResults(expectedResults, actualResults);
  }

  /* Utility features */
public void doCommonTest(String japeFilePath, String docFilePath, 
        String[] expectedResults, int[] expectedStartOffsets, int[] expectedEndOffsets, 
        AnnotationCreator annocreator) throws Exception {
   
  Set<Annotation> actualResults = doTest(docFilePath, japeFilePath, annocreator);
  
  compareResults(expectedResults, actualResults);
  compareStartOffsets(actualResults, expectedStartOffsets);
  compareEndOffsets(actualResults, expectedEndOffsets);
}
  
private final   AnnotationCreator annoCreatorEmpty = new BaseAnnotationCreator() {
  @Override
  public AnnotationSet createAnnots(Document doc) throws InvalidOffsetException {
    return as;
  }
};

private final AnnotationCreator annoCreator81LocOrgDateJob = new BaseAnnotationCreator() {
  @Override
  public AnnotationSet createAnnots(Document doc) throws InvalidOffsetException {
    /* line 1 */
    add(0, 5, "Location");
    add(0, 14, "Location");
    add(6, 14, "Location");
    add(15, 23, "Organization");
    FeatureMap feat = Factory.newFeatureMap();
    feat.put("month", "September");
    add(24, 34, "Date", feat);
    add(35, 52, "JobTitle");
    
    /* line 2 */
    add(53, 58, "Location");
    add(53, 67, "Location");
    add(59, 67, "Location");
    add(68, 76, "Organization");
    feat = Factory.newFeatureMap();
    feat.put("month", "November");
    add(77, 87, "Date", feat);
    add(88, 99, "JobTitle");
    
    /* line 3 */
    add(100, 106, "Location");
    add(100, 109, "Location");
    add(107, 109, "Location");
    add(110, 114, "Organization");
    
    feat = Factory.newFeatureMap();
    feat.put("month", "October");
    add(115, 125, "Date", feat);
    
    add(126, 136, "JobTitle");
    
    return as;
  }
};
  
private final AnnotationCreator annoCreator813Operators = new BaseAnnotationCreator() {
  @Override
  public AnnotationSet createAnnots(Document doc) throws InvalidOffsetException {
    FeatureMap feat = Factory.newFeatureMap();
    feat.put("string", "room");
    add(0, 4, "Token", feat);
    
    feat = Factory.newFeatureMap();
    feat.put("string", "qu\"oted");
    add(5, 12, "Token", feat);
    
    feat = Factory.newFeatureMap();
    feat.put("int", 101l);
    add(13, 16, "Token", feat);
    
    feat = Factory.newFeatureMap();
    feat.put("int", -10l);
    add(17, 20, "Token", feat);
    
    feat = Factory.newFeatureMap();
    feat.put("double", 3.14d);
    add(21, 25, "Token", feat);
    
    feat = Factory.newFeatureMap();
    feat.put("double", -273.15d);
    add(26, 33, "Token", feat);
    
    feat = Factory.newFeatureMap();
    feat.put("string", "qu\\\"oted");
    add(34, 42, "Token", feat);
    return as;
  }
};

private final  AnnotationCreator annoCreator814MetaProps = new BaseAnnotationCreator() {
  @Override
  public AnnotationSet createAnnots(Document doc) throws InvalidOffsetException {
    add(0, 60, "Span");
    return as;
  }
};

private final AnnotationCreator annoCreator815MultipleActions = new BaseAnnotationCreator() {
  @Override
  public AnnotationSet createAnnots(Document doc) throws InvalidOffsetException {
    final String tp = "TempPerson";
    add(0, 4, tp);
    
    FeatureMap feats = Factory.newFeatureMap();
    feats.put("majorType", "jobtitle");
    add(5,11, "Lookup", feats);
    add(12, 20, tp);
    
    add(35, 49, "Lookup", feats);
    add(35, 58, tp);
    
    return as;
  }
};

private final AnnotationCreator annoCreator817Contexts = new BaseAnnotationCreator() {
  @Override
  public AnnotationSet createAnnots(Document doc) throws InvalidOffsetException {
    final String tok = "Token";
    final String str = "string";
    
    FeatureMap feats = Factory.newFeatureMap();
    feats.put("type", "elmail");
    add(9, 25, "Annotation", feats);
    add(30, 44, "Annotation", feats);
   
    feats = Factory.newFeatureMap();
    feats.put(str, "<");
    add(8,9, tok, feats);
    
    feats = Factory.newFeatureMap();
    feats.put(str, ">");
    add(25, 26, tok, feats);

    feats = Factory.newFeatureMap();
    feats.put(str, "de");
    add(58, 60, tok, feats);
    
    feats = Factory.newFeatureMap();
    feats.put("majorType", "name");
    feats.put("minorType", "surname");
    add(58, 66, "Lookup", feats);

    return as;
  }
};

  /**
   * Visually, this creates an annot set like this:
   * 0-------------1--------------2 12345678901234567890 _AA
   * (f1,"atext"), (f2,"2"), (f3,3) _B
   * (f1,"btext),	(f2,"2"),	(f4,"btext4") ___BB
   * (f1,"btext),	(f2,"2"),	(f4,"btext4") ______B (f1,"cctext"),
   * (f2,"2"), (f3,3l), (f4,"ctext4") ______CC (f1,"cctext"), (f2,"2"),
   * (f3,3l), (f4,"ctext4") ________CC (f1,"cctext"), (f2,"1"),
   * (f4,"ctext4") _____________DD (f1,"dtext"), (f3,3l)
   * _______________DD (f2,2l) _________________DD (ontology,
   * "http://gate.ac.uk/tests/demo.owl"), (class, "Businessman")
   * ____________________D (ontology,
   * "http://gate.ac.uk/tests/demo.owl"), (class, "Country")
   * 0-------------1--------------2 12345678901234567890
   */
  protected AnnotationCreator basicAnnotCreator = new BaseAnnotationCreator() {
    @Override
    public AnnotationSet createAnnots(Document doc)
            throws InvalidOffsetException {
      FeatureMap feat = Factory.newFeatureMap();
      feat.put("f1", "atext");
      feat.put("f2", "2");
      feat.put("f3", 3l);
      add(2, 4, "A", feat);

      feat = Factory.newFeatureMap();
      feat.put("f1", "btext");
      feat.put("f2", "2");
      feat.put("f4", "btext4");
      add(2, 3, "B", feat);
      add(4, 6, "B", feat);

      feat = Factory.newFeatureMap();
      feat.put("f1", "cctext");
      feat.put("f2", "2");
      feat.put("f3", 3l);
      feat.put("f4", "ctext4");
      add(6, 7, "B", feat);
      add(6, 8, "C", feat);

      feat = Factory.newFeatureMap();
      feat.put("f1", "cctext");
      feat.put("f2", "1");
      feat.put("f4", "ctext4");
      add(8, 10, "C", feat);

      feat = Factory.newFeatureMap();
      feat.put("f1", "dtext");
      feat.put("f3", 3l);
      add(12, 14, "D", feat);

      feat = Factory.newFeatureMap();
      feat.put("f2", 2l);
      add(14, 16, "D", feat);
      
      feat = Factory.newFeatureMap();
      feat.put("ontology", TestDocument.getTestServerName() + "tests/demo.owl");
      feat.put("class", "Businessman");
      add(16, 18, "D", feat);

      feat = Factory.newFeatureMap();
      feat.put("ontology", TestDocument.getTestServerName() + "tests/demo.owl");
      feat.put("class", "Country");
      add(18, 19, "D", feat);
      return as;
    }
  };

  /**
   * Fast routine for parsing a small string of JAPE rules.
   * 
   * @param japeRules
   * @throws ParseException
   */
  protected static void parseJapeString(String japeRules) throws ParseException {
    StringReader sr = new StringReader(japeRules);
    ParseCpsl parser = Factory.newJapeParser(sr, new HashMap<String, Object>());
    Transducer transducer = parser.MultiPhaseTransducer();
    transducer.finish(Gate.getClassLoader());
  }

  /* Set up for Runners */

  public static Test suite() {
    Test suite = new TestSetup(new TestSuite(TestConstraints.class)) {
      @Override
      protected void setUp() {
        setUpGate();
        logger.info("GATE initialized and fixure set up.");
      }
    };
    return suite;
  }

  // main method for running this test as a standalone test
  public static void main(String[] args) {
    junit.textui.TestRunner.run(TestConstraints.suite());
  }
} // class TestJape
