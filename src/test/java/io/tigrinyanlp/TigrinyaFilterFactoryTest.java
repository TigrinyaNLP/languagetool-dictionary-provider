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
        TigrinyaFilter filter = new TigrinyaFilterFactory.InvalidStart();
        assertEquals("", filter.consume(new TigrinyaWordEntity("'ምስ","N", 1),new HashMap<String, String>()) );
        assertEquals("ምስ", filter.consume(new TigrinyaWordEntity("ምስ","N", 1), new HashMap<String, String>()) );
    }
    @Test
    public void testInvalidWords()
    {
        TigrinyaFilter filter = new TigrinyaFilterFactory.InvalidWords();
        assertEquals("", filter.consume(new TigrinyaWordEntity("ም፱ስ","N", 1), new HashMap<String, String>()) );
        assertEquals("", filter.consume(new TigrinyaWordEntity("ም5ስ","N", 1), new HashMap<String, String>()) );
        assertEquals("ምስ", filter.consume(new TigrinyaWordEntity("ምስ","N", 1), new HashMap<String, String>()) );
    }
    @Test
    public void testReplacableWords()
    {
        TigrinyaFilter filter = new TigrinyaFilterFactory.ReplacableWords();
        assertEquals("ምስ", filter.consume(new TigrinyaWordEntity("ምሥ","N", 1), new HashMap<String, String>()) );
        assertEquals("ህጹጽ", filter.consume(new TigrinyaWordEntity("ኅፁፅ","N", 1), new HashMap<String, String>()) );
    }
    @Test
    public void testShortWord()
    {
        TigrinyaFilter filter = new TigrinyaFilterFactory.ShortWord();
        assertEquals("", filter.consume(new TigrinyaWordEntity("ም","N", 1), new HashMap<String, String>()) );
        assertEquals("", filter.consume(new TigrinyaWordEntity("ምስ","N", 1), new HashMap<String, String>()) );
        assertEquals("ምስ", filter.consume(new TigrinyaWordEntity("ምስ","N", 3),new HashMap<String, String>()) );
    }
    @Test
    public void testSplitVerbFromProposition()
    {
        TigrinyaFilter filter = new TigrinyaFilterFactory.SplitVerbFromProposition();
         assertEquals("ብዘይስራሕ", filter.consume(new TigrinyaWordEntity("ብዘይስራሕ","N", 1),  Collections.singletonMap("ስራሕ","V")));
        assertEquals("", filter.consume(new TigrinyaWordEntity("ብዘይስራሕተኛ","N", 1),  Collections.singletonMap("ስራሕተኛ","N")));
        assertEquals("ብዘይፍለጥ", filter.consume(new TigrinyaWordEntity("ብዘይፍለጥ","N", 1),  Collections.singletonMap("ፍለጥ","V")));
        assertEquals("ብዘይፍለጥ", filter.consume(new TigrinyaWordEntity("ብዘይፍለጥ","N", 1),  new HashMap<String, String>()) );
        assertEquals("ስራሕ", filter.consume(new TigrinyaWordEntity("ስራሕ","N", 1), new HashMap<String, String>()) );
    }
}
