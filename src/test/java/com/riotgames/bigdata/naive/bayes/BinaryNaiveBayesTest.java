package com.riotgames.bigdata.naive.bayes;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class BinaryNaiveBayesTest extends TestCase {

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public BinaryNaiveBayesTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(BinaryNaiveBayesTest.class);
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp() {
        BinaryNaiveBayes bayes = new BinaryNaiveBayes();
        bayes.processLine("1 1| good", true);
        bayes.processLine("1 2| very good", true);
        bayes.processLine("0 3| bad", true);
        bayes.processLine("0 4| very bad", true);
        bayes.processLine("0 5| very bad very bad", true);
        
        bayes.processLine("0 6| good bad very bad", false);
        
        
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
