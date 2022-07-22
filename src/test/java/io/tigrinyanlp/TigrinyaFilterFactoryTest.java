package io.tigrinyanlp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;

/**
 * Unit test for TigrinyaFilterFactoryTest.
 * @author Biniam Gebremichael
 */
public class TigrinyaFilterFactoryTest
{

    @Test
    public void testInvalidStart()
    {
        TigrinyaFilter<String> filter = new TigrinyaFilterFactory.InvalidStart();
        assertEquals("", filter.consume("'ምስ",1,new HashMap<String, String>()) );
        assertEquals("ምስ", filter.consume("ምስ",1,new HashMap<String, String>()) );
    }
    @Test
    public void testInvalidWords()
    {
        TigrinyaFilter<String> filter = new TigrinyaFilterFactory.InvalidWords();
        assertEquals("", filter.consume("ም፱ስ",1,new HashMap<String, String>()) );
        assertEquals("", filter.consume("ም5ስ",1,new HashMap<String, String>()) );
        assertEquals("ምስ", filter.consume("ምስ",1,new HashMap<String, String>()) );
    }
    @Test
    public void testReplacableWords()
    {
        TigrinyaFilter<String> filter = new TigrinyaFilterFactory.ReplacableWords();
        assertEquals("ምስ", filter.consume("ምሥ",1,new HashMap<String, String>()) );
        assertEquals("ህጹጽ", filter.consume("ኅፁፅ",1,new HashMap<String, String>()) );
    }
    @Test
    public void testShortWord()
    {
        TigrinyaFilter<String> filter = new TigrinyaFilterFactory.ShortWord();
        assertEquals("", filter.consume("ም",1,new HashMap<String, String>()) );
        assertEquals("", filter.consume("ምስ",1,new HashMap<String, String>()) );
        assertEquals("ምስ", filter.consume("ምስ",3,new HashMap<String, String>()) );
    }
    @Test
    public void testSplitVerbFromProposition()
    {
        TigrinyaFilter<String> filter = new TigrinyaFilterFactory.SplitVerbFromProposition();
         assertEquals("ብዘይስራሕ", filter.consume("ብዘይስራሕ",1, Collections.singletonMap("ስራሕ","V")));
        assertEquals("", filter.consume("ብዘይስራሕተኛ",1, Collections.singletonMap("ስራሕተኛ","N")));
        assertEquals("ብዘይፍለጥ", filter.consume("ብዘይፍለጥ",1, Collections.singletonMap("ፍለጥ","V")));
        assertEquals("ብዘይፍለጥ", filter.consume("ብዘይፍለጥ",1, new HashMap<String, String>()) );
        assertEquals("ስራሕ", filter.consume("ስራሕ",1,new HashMap<String, String>()) );
    }
}
