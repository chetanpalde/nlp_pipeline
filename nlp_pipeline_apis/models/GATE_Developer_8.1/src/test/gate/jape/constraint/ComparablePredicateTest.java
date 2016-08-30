/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gate.jape.constraint;


import gate.AnnotationSet;
import gate.jape.JapeException;
import junit.framework.TestCase;

/**
 *
 * @author philipgooch
 */
public class ComparablePredicateTest extends TestCase {
    
    public ComparablePredicateTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of setValue method, of class ComparablePredicate.
     */
    public void testSetValue() {
        System.out.println("setValue");
        Object value = new Long(32);
        ComparablePredicate instance = new ComparablePredicateImpl();
        instance.setValue(value);
        assertTrue(true);
    }

    /**
     * Test of doMatch method, of class ComparablePredicate.
     */
    public void testDoMatch_Object_AnnotationSet() throws Exception {
        System.out.println("doMatch");
        Object value = null;
        AnnotationSet context = null;
        ComparablePredicate instance = new ComparablePredicateImpl();
        boolean expResult = false;
        boolean result = instance.doMatch(value, context);
        assertEquals(expResult, result);
       
    }

    /**
     * Test of doMatch method, of class ComparablePredicate.
     */
    public void testDoMatch_Object() throws Exception {
        System.out.println("doMatch");
        Object featureValue = null;
        ComparablePredicate instance = new ComparablePredicateImpl();
        boolean expResult = false;
        boolean result = instance.doMatch(featureValue);
        assertEquals(expResult, result);
        
    }

    /**
     * Test of compareValue method, of class ComparablePredicate.
     */
    public void testCompareValue() throws Exception {
        System.out.println("compareValue");

        ComparablePredicate instance = new ComparablePredicateImpl();

        instance.setValue(new Double(12.7));
        assertEquals(1, instance.compareValue(new String("3")));
        
        instance.setValue(new Double(12.7));
        assertEquals(1, instance.compareValue(new Integer(5)));

        instance.setValue(new Long(4));
        int res = instance.compareValue(new Integer(3));
        assertEquals(1, res);

        instance.setValue(new String("3"));
        assertEquals(3, instance.compareValue(new String("001")));

        instance.setValue(new String("3"));
        assertEquals(2, instance.compareValue(new String("1")));

        instance.setValue(new Long(35));
        assertEquals(1, instance.compareValue(new String("12")));

        instance.setValue(new Long(3));
        assertEquals(-1, instance.compareValue(new Integer(17)));

        instance.setValue(new Double(100.0));
        assertEquals(1, instance.compareValue(new Double(97.3)));

        instance.setValue(new Float(100.0));
        assertEquals(1, instance.compareValue(new Float(97.3)));
    }

    public class ComparablePredicateImpl extends ComparablePredicate {

      private static final long serialVersionUID = 1798408901473465705L;

        @Override
        public String getOperator() {
            return GREATER;
        }

        @Override
        public boolean doMatch(Object featureValue) throws JapeException {
            return false;
        }
    }

}
