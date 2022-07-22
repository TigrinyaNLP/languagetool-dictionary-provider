package io.tigrinyanlp;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 1. Run this class to clean up the dictionary
 * 2. run DictionaryBuilder to compile it to a .dict file
 * 3. make sure the .dict file is valid by running DictionaryExporter
 *
 * @author Biniam Gebremichael
 */
public class TigrinyaFilterFactory {

    public static List<TigrinyaFilter<String>> build() {
        List<TigrinyaFilter<String>> filter = Arrays.asList(
                //accept frequency >2 and word length > 1
                //replace "ፀ" "ኀ" "ሠ" family with their corresponding Tigrinya families
                new ReplacableWords(),
                // remove words that have numbers or english characters
                new InvalidWords(),
                // remove words that start or end with '
                new InvalidStart(),
                // detect missing space like ብዘይስራሕ = should have been ብዘይ ስራሕ. add if missing
                new SplitVerbFromProposition(),
                // remove a one character word or word with frequency <2
                new ShortWord()
        );
        return filter;
    }

    public static class InvalidWords implements TigrinyaFilter<String> {
        String pattern = "[፩|፪|፫|፬|፭|፮|፯|፰|፱|፲|፳|፴|፵|፶|፷|፸|፹|፺|፻|፼|A-Z|a-z|0-9]";
        Pattern r = Pattern.compile(pattern);

        @Override
        public String consume(String word, Integer frequency, Map<String, String> posMap) {
            return r.matcher(word).find() ? "" : word;
        }
    }

    public static class ShortWord implements TigrinyaFilter<String> {

        @Override
        public String consume(String word, Integer frequency, Map<String, String> posMap) {
            return word.length() > 1 && frequency > 2 ? word : "";
        }
    }


    static class SplitVerbFromProposition implements TigrinyaFilter<String> {
        Pattern r = Pattern.compile("^(ስለ|ምስ|ከም|እንተ|ብዘይ)(.+)$");

        @Override
        public String consume(String word, Integer frequency, Map<String, String> posMap) {
            Matcher matcher = r.matcher(word);
            if (matcher.find()) {
                String verb = matcher.group(2);
                String pos = posMap.get(verb);
                String posw = posMap.get(word);
                //remove joined words if root exists
                if (verb.length() > 2 && posw != null && pos != null && (posw.startsWith("V") || pos.startsWith("V"))) {
                    return "";
                } else if (verb.length() > 3 && pos != null) {
                    System.out.println(matcher.group(1) + " " + verb + " - " + posw + "/" + pos);
                    return "";
                }
            }
            return word;
        }
    }

    static class InvalidStart implements TigrinyaFilter<String> {
        Pattern r = Pattern.compile("^'");
        Pattern r2 = Pattern.compile("'$");

        @Override
        public String consume(String word, Integer frequency, Map<String, String> posMap) {
            return r.matcher(word).find() || r2.matcher(word).find() ? "" : word;
        }
    }

    static class ReplacableWords implements TigrinyaFilter<String> {
        @Override
        public String consume(String word, Integer frequency, Map<String, String> posMap) {
            List<String> wrongTigrinya = Arrays.asList("ፀ", "ፁ", "ፂ", "ፃ", "ፄ", "ፅ", "ፆ", "ፇ", "ኀ", "ኁ", "ኂ", "ኃ", "ኄ", "ኅ", "ኆ", "ኇ", "ሠ", "ሡ", "ሢ", "ሣ", "ሤ", "ሥ", "ሦ", "ሧ");
            List<String> correctTigrinya = Arrays.asList("ጸ", "ጹ", "ጺ", "ጻ", "ጼ", "ጽ", "ጾ", "ጿ", "ሀ", "ሁ", "ሂ", "ሃ", "ሄ", "ህ", "ሆ", "ሇ", "ሰ", "ሱ", "ሲ", "ሳ", "ሴ", "ስ", "ሶ", "ሷ");
            for (int i = 0; i < wrongTigrinya.size(); i++) {
                word = word.replace(wrongTigrinya.get(i), correctTigrinya.get(i));
            }
            return word;
        }
    }

}
