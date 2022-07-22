package io.tigrinyanlp;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 1. Run this class to clean up the dictionary
 * 2. run DictionaryBuilder to compile it to a .dict file
 * 3. make sure the .dict file is valid by running DictionaryExporter
 *
 * @author Biniam Gebremichael
 */
public class TigrinyaDictionaryReader {


    public static  Map<String, String> readPos(URL url) throws IOException {
        Map<String, String> posMap = new HashMap<>();
        String pattern = "(.+)\t(.+)\t(\\w+)";
        Pattern r = Pattern.compile(pattern);
        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
        String line;
        int count = 0;
        while ((line = reader.readLine()) != null) {
            Matcher m = r.matcher(line);
            if (m.find()) {
                String word = m.group(1);
                String pos = m.group(3);
                posMap.put(word, pos);
            }
        }
        return posMap;
    }


    public static Set<TigrinyaWordEntity> readDictionary(URL freq, Map<String, String> posMap, List<TigrinyaFilter<String>> pipes) throws IOException {
        SortedSet<TigrinyaWordEntity> dictionary = new TreeSet<>();
        String pattern = "<w\\s+f=\"(\\d+)\"\\s+[^>]+>([^<]+)</w>";
        Pattern r = Pattern.compile(pattern);
        BufferedReader reader = new BufferedReader(new InputStreamReader(freq.openStream()));
        String line;
        int count = 0;
        while ((line = reader.readLine()) != null) {
            Matcher m = r.matcher(line);
            if (m.find()) {
                String word = m.group(2);
                Integer frequency = Integer.valueOf(m.group(1));
                for (TigrinyaFilter<String> pipe : pipes) {
                    word = pipe.consume(word, frequency, posMap);
                }
                if (word.length() > 1 && frequency > 2) {
                    String pos = posMap.get(word);
                    dictionary.add(new TigrinyaWordEntity(word, pos, frequency));
                    count++;
                }
            }
        }
        reader.close();
        System.out.println("read " + count + " words. Writing " + dictionary.size() + ". Removed " + (count - dictionary.size()));
        return dictionary;
    }


    public static Set<TigrinyaWordEntity> readDictionary(URL freq, Map<String, String> posMap) throws IOException {
        return readDictionary(freq,posMap,new ArrayList<TigrinyaFilter<String>>());
    }



    public static void saveWordlistDictionary(URL url, Set<TigrinyaWordEntity> dictionary) throws IOException {
        String file = url.getFile().replace("target/classes", "src/main/resources");

        System.out.println("saving to = " + file);
        BufferedWriter out = new BufferedWriter(new FileWriter(file));
        out.write("<wordlist locale=\"ti\" description=\"Tigrinya\" date=\"1646943648009\" version=\"1\">\n");
        for (TigrinyaWordEntity wordFreq : dictionary) {
            out.write(wordFreq.toString() + "\n");
        }
        out.write("</wordlist>");
        out.close();
    }




    public static void saveDictionary(URL url, Set<TigrinyaWordEntity> dictionary) throws IOException {
        String file = url.getFile().replace("target/classes", "src/main/resources");
        System.out.println("saving to = " + file);
        BufferedWriter out = new BufferedWriter(new FileWriter(file));
        for (TigrinyaWordEntity wordFreq : dictionary) {
            out.write(wordFreq.toDic() + "\n");
        }
        out.close();
    }


}
