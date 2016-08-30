package gate.util;

import junit.framework.*;

/**
 * Title:        Gate2
 * Description:
 * Copyright:    Copyright (c) 2000
 * Company:      University Of Sheffield
 * @version 1.0
 */

public class TestFeatureMap extends TestCase {

    /** Construction */
    public TestFeatureMap(String name) { super(name); }

    /** Test the testPutAndGet()... methods. */
    public void testPutAndGet() throws Exception {
        assertTrue(true);
        SimpleFeatureMapImpl map = new SimpleFeatureMapImpl();
        map.put("1", "bala");
        map.put("1", "bala2");
        map.put("2", "20");
        map.put("3", null);
        map.put(null, "5");

        Object value = null;
        /**
         * test1:
         *      get replaced value by normal key
         */
        value = map.get("1");
        assertSame(value, "bala2");
        /**
         * test 2:
         *      get normal value by normal key
         */
        value = map.get("2");
        assertSame(value, "20");
        /**
         * Test 3:
         *      get null value by the key
         */
        value = map.get("3");
        assertSame(value, null);
        /**
         * test 4:
         *      try to get value by 'null' key
         */
        value = map.get(null);
        assertSame(value, "5");
    } // testPutAndGet()

    public void testSubsume() throws Exception {
        assertTrue(true);
        SimpleFeatureMapImpl map = new SimpleFeatureMapImpl();
        SimpleFeatureMapImpl map2 = new SimpleFeatureMapImpl();
        map.put("1", "bala");
        map2.put("1", map.get("1"));

        map.put("2", "20");
        /**
         * test1:
         *      subsume partially - map1 and map2 has one common element
         */
         assertTrue(map.subsumes(map2));
        /**
         * test 2:
         *      map2 do NOT subsumes map1
         */
         assertTrue(!map2.subsumes(map));
        /**
         * Test 3:
         *      subsume partially - map1 and map2.keySet()
         */
         assertTrue(map.subsumes(map2, map2.keySet()));
        /**
         * test 4:
         *      map2 SUBSUMES and map using the map2.keySet()
         */
         assertTrue(map2.subsumes(map, map2.keySet()));

        /**
         * test 5,6,7,8:
         *      test1,2,3,4 with NULL's in the map and
         *      not NULL's the map2 under the same key "3"
         */
         map.put("3", null);
         map2.put("3", "not null");

         assertTrue(!map.subsumes(map2));
         assertTrue(!map2.subsumes(map));
         assertTrue(!map.subsumes(map2, map2.keySet()));
         assertTrue(!map2.subsumes(map, map2.keySet()));

         /**
          * Test 9,10,11,12 repeat the same test but with compatible (null) values
          * under the same key "3"
          */
         map2.put("3", null);

         assertTrue(map.subsumes(map2));
         assertTrue(!map2.subsumes(map));
         assertTrue(map.subsumes(map2, map2.keySet()));
         assertTrue(map2.subsumes(map, map2.keySet()));

         /**
          * Test 13,14,15,16 repeat the same test but with null keys in the two of the maps
          */
         map.put(null, "5");
         map2.put(null, "5");

         assertTrue(map.subsumes(map2));
         assertTrue(!map2.subsumes(map));
         assertTrue(map.subsumes(map2, map2.keySet()));
         assertTrue(map2.subsumes(map, map2.keySet()));
    } // testSubsume()

    /** Test suite routine for the test runner */
    public static Test suite() {
        return new TestSuite(TestFeatureMap.class);
    } // suite

    public static void main(String args[]){
        TestFeatureMap app = new TestFeatureMap("TestFeatureMap");
        try {
            app.testPutAndGet();
            app.testSubsume();
        } catch (Exception e) {
            e.printStackTrace (Err.getPrintWriter());
        }
    } // main
} // TestFeatureMap