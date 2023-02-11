package org.example;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue()
    {
        String line = "abcabcbb";
        char[] chs = line.toCharArray();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < chs.length; i++)
        {
            sb.append("\"" + chs[i] + "\",");
        }
        System.out.println(sb.toString());
        //assertTrue( true );
    }

}
