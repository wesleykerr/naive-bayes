package com.riotgames.bigdata.naive.bayes;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(AppTest.class);
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp() {
        NaiveBayes bayes = new NaiveBayes();
        bayes.processLine("1 0| height=6 weight=180 foot=12");
        bayes.processLine("1 1| height=5.92 weight=190 foot=11");
        bayes.processLine("1 2| height=5.58 weight=170 foot=12");
        bayes.processLine("1 3| height=5.92 weight=165 foot=10");
        bayes.processLine("0 4| height=5 weight=100 foot=6");
        bayes.processLine("0 5| height=5.5 weight=150 foot=8");
        bayes.processLine("0 6| height=5.42 weight=130 foot=7");
        bayes.processLine("0 7| height=5.75 weight=150 foot=9");
        
        bayes.processLine("0 sample| height=6.0 weight=130 foot=8");
        
        
//        double[] params = bayes.getParams(1, "", "foot");
//        System.out.println("Count: " + params[NaiveBayes.N]);
//        System.out.println("Mean: " + params[NaiveBayes.MEAN]);
//        
//        double v = params[NaiveBayes.M2] / (params[NaiveBayes.N] - 1);
//        System.out.println("Variance: " + v);
//        System.out.println("Standard Deviation: " + Math.sqrt(v));
        
        // expected values height (from R -- 4, 5.855, 0.03503333, 0.1871719)
        // expected values weight (from R -- 4, 176.25, 122.9167, 11.08678)
        // expected values foot (from R -- 4, 11.25, 0.9166667, 0.9574271)
        
        assertTrue(true);
    }
}
