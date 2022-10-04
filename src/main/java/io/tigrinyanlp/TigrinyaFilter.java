package io.tigrinyanlp;

import java.util.Map;

/**
 * 1. Run this class to clean up the dictionary
 * 2. run DictionaryBuilder to compile it to a .dict file
 * 3. make sure the .dict file is valid by running DictionaryExporter
 *
 * @author Biniam Gebremichael
 */
public interface TigrinyaFilter  {
    TigrinyaWordEntity consume(TigrinyaWordEntity wordEntity, Map<String, String> posMap);
}
