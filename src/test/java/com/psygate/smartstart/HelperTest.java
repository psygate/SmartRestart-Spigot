/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.psygate.smartstart;

import com.psygate.smartrestart.Helper;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author florian
 */
public class HelperTest {

    public HelperTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testPatterns() {
        Random rand = new Random(8743823L);
        LinkedList<String> teststrings = new LinkedList<>();

        for (Helper.Container suffix : Helper.getSuffixes().values()) {
            for (int i = 0; i < 100; i++) {
                teststrings.add(rand.nextInt(100) + suffix.getStrSuffix());
            }
        }

        for (String test : teststrings) {
            Helper.timeStringAsMillis(test);
        }
    }
}
