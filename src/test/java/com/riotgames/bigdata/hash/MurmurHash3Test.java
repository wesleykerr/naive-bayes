/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.riotgames.bigdata.hash;

import static junit.framework.Assert.assertTrue;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 *
 * @author wkerr
 */
public class MurmurHash3Test extends TestCase {

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public MurmurHash3Test( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( MurmurHash3Test.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        String[] tests = { "test1", "test2", "a", "b" };
        for (String s : tests) { 
            System.out.println("Hash: " + s + " " + MurmurHash3.murmurhash3_x86_32(s, 0, s.length(), 1));
        }
        assertTrue( true );
    }
 
}
