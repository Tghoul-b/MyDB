package com.flyn.mydb;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void testInsert() {
            String s="INSERT INTO employee VALUES(\"李白\", 370783190000000000, \"中国\", \"男\");";
            String pattern="(?i)^(insert)\\s+(into)\\s+\\w+\\s+(values)\\s*"
                    +"\\(\\s*(\\S+)\\s*(,\\s*\\S+\\s*)*\\);$";
        System.out.println(s.matches(pattern));
    }
}