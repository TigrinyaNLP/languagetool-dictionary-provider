package io.tigrinyanlp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.languagetool.broker.DefaultResourceDataBroker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 1. Run this class to clean up the dictionary
 * 2. run DictionaryBuilder to compile it to a .dict file
 * 3. make sure the .dict file is valid by running DictionaryExporter
 *
 * @author Biniam Gebremichael
 */

public class TigrinyaDictionaryBuilder
{

    private static Gson gson = new GsonBuilder().create();
    static final DefaultResourceDataBroker defaultResourceDataBroker = new DefaultResourceDataBroker("/io/tigrinyanlp", "/io/tigrinyanlp/rules");

    // download https://github.com/fgaim/Tigrinya-WordCount/blob/main/ti_word_count.txt in /hunspell/ti/
    static final String hunspell_extra = "/hunspell/ti/ti_word_count.txt";

    static final String hunspell_dic = "/hunspell/ti/ti_ER.dict";
    static final String hunspell_freq = "/hunspell/ti/ti_ER_wordlist.xml";

    static final URL dic = defaultResourceDataBroker.getFromResourceDirAsUrl(hunspell_dic);
    static final URL freq = (URL) defaultResourceDataBroker.getFromResourceDirAsUrl(hunspell_freq);
    static final URL input = defaultResourceDataBroker.getFromResourceDirAsUrl(hunspell_extra);

    public static void main(String[] args) throws Exception {
        Map<String, String> posMap = TigrinyaDictionaryReader.readPos(dic);
        Set<TigrinyaWordEntity> readDictionary = TigrinyaDictionaryReader.readDictionary(freq,posMap);
        Map<String, Integer> newWords = readInput(input);
        for (Map.Entry<String, Integer> entry : newWords.entrySet()) {
            if(!posMap.keySet().contains(entry.getKey()) && entry.getValue()>70){
                try {
                    String pos = getPos(entry.getKey());
                    System.out.println(entry.getKey() + " / " + entry.getValue() + " / " + pos);
                    Thread.sleep(10);
                    readDictionary.add(new TigrinyaWordEntity(entry.getKey(), pos, entry.getValue()));
                }catch (Exception e){
                    System.out.println(e.getMessage());
                }
            }
        }

        TigrinyaDictionaryReader.saveWordlistDictionary(freq,readDictionary);
        TigrinyaDictionaryReader.saveDictionary(dic,readDictionary);
    }

    private static Map<String, Integer> readInput(URL url) throws IOException {
        Map<String, Integer> posMap = new HashMap<>();
        String pattern = "(.+)\t(\\d+)";
        Pattern r = Pattern.compile(pattern);
        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
        String line;
        int count = 0;
        while ((line = reader.readLine()) != null) {
            Matcher m = r.matcher(line);
            if (m.find()) {
                String word = m.group(1);
                Integer freq = Integer.valueOf(m.group(2));
                posMap.put(word, freq);
            }
        }
        return posMap;
    }

    private static String getPos(String word) throws Exception {

        HttpClient client =  HttpClientBuilder.create().build();
        HttpGet request = new HttpGet("https://tigtag.herokuapp.com/api/analyzer?input_text=" + word);
        HttpResponse response = client.execute(request);

// Get the response
        BufferedReader rd = new BufferedReader
                (new InputStreamReader(
                        response.getEntity().getContent()));

        String responses = "";
        String line = "";
        while ((line = rd.readLine()) != null) {
            responses = responses + line;
        }


        if (!responses.isEmpty()) {
            try {
                List list = gson.fromJson(responses, List.class);
                return (String) ((List) list.get(0)).get(1);
            } catch (Exception e) {
                return "";
            }
        }
        return "";
    }
}
