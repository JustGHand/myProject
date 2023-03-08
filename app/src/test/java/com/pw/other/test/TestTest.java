package com.pw.other.test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class TestTest {
    private com.pw.other.test.Test mTest;
    @Before
    public void setUp() throws Exception {
        mTest = new com.pw.other.test.Test();
    }
    @Test
    public void testSum() throws Exception{
        //expected: 6, sum of 1 and 5
        assertEquals(6d, mTest.sum(2d, 5d), 0);
    }
}