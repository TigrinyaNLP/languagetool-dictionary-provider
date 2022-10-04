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

    public static List<TigrinyaFilter > build() {
        List<TigrinyaFilter > filter = Arrays.asList(
                //accept frequency >2 and word length > 1
                //replace "ፀ" "ኀ" "ሠ" family with their corresponding Tigrinya families
                new ReplacableWords(),
                // remove words that have numbers or english characters
                new InvalidWords(),
                // remove words that start or end with '
                new InvalidStart(),
                // detect missing space like ብዘይስራሕ = should have been ብዘይ ስራሕ. same is true for ስለ|ምስ|ከም|እንተ
                new SplitVerbFromProposition(),
                // remove a one character word or word with frequency <2
                new ShortWord()
        );
        return filter;
    }

    public static class InvalidWords implements TigrinyaFilter {
        String pattern = "[፩|፪|፫|፬|፭|፮|፯|፰|፱|፲|፳|፴|፵|፶|፷|፸|፹|፺|፻|፼|A-Z|a-z|0-9]";
        Pattern r = Pattern.compile(pattern);

        @Override
        public TigrinyaWordEntity consume(TigrinyaWordEntity entry, Map<String, String> posMap) {
            if(entry==null){return null;}
            return r.matcher(entry.getWord()).find() ? null : entry;
        }
    }

    public static class ShortWord implements TigrinyaFilter {

        @Override
        public TigrinyaWordEntity consume(TigrinyaWordEntity entry, Map<String, String> posMap) {
            if(entry==null){return null;}
            return entry.getWord().length() > 1 && entry.getFreq() > 2 ? entry : null;
        }
    }


    static class SplitVerbFromProposition implements TigrinyaFilter {
        Pattern r = Pattern.compile("^(ስለ|ምስ|ከም|እንተ|ብዘይ)(.+)$");

        @Override
        public TigrinyaWordEntity consume(TigrinyaWordEntity entry, Map<String, String> posMap) {
            if(entry==null){return null;}
            Matcher matcher = r.matcher(entry.getWord());
            if (matcher.find()) {
                String verb = matcher.group(2);
                String pos = posMap.get(verb);
                String posw = posMap.get(entry.getWord());
                //remove joined words if root is verb longer than 2 chars or non verb >3 chars exist
                if (verb.length() > 2 && posw != null && pos != null && (posw.startsWith("V") || pos.startsWith("V"))) {
                    return null;
                } else if (verb.length() > 3 && pos != null) {
//                    System.out.println(matcher.group(1) + " " + verb + " - " + posw + "/" + pos);
                    return null;
                }
            }
            return entry;
        }
    }

    static class InvalidStart implements TigrinyaFilter {
        Pattern r = Pattern.compile("^[\"|'|']+");
        Pattern r2 = Pattern.compile("[\"|'|']+$");

        @Override
        public TigrinyaWordEntity consume(TigrinyaWordEntity entry, Map<String, String> posMap) {
            if(entry==null){return null;}
            return r.matcher(entry.getWord()).find() || r2.matcher(entry.getWord()).find() ? null : entry;
        }
    }

    static class ReplacableWords implements TigrinyaFilter {
        @Override
        public TigrinyaWordEntity consume(TigrinyaWordEntity entry, Map<String, String> posMap) {
            if(entry==null){return null;}
            List<String> wrongTigrinya = Arrays.asList("ፀ", "ፁ", "ፂ", "ፃ", "ፄ", "ፅ", "ፆ", "ፇ", "ኀ", "ኁ", "ኂ", "ኃ", "ኄ", "ኅ", "ኆ", "ኇ", "ሠ", "ሡ", "ሢ", "ሣ", "ሤ", "ሥ", "ሦ", "ሧ");
            List<String> correctTigrinya = Arrays.asList("ጸ", "ጹ", "ጺ", "ጻ", "ጼ", "ጽ", "ጾ", "ጿ", "ሀ", "ሁ", "ሂ", "ሃ", "ሄ", "ህ", "ሆ", "ሇ", "ሰ", "ሱ", "ሲ", "ሳ", "ሴ", "ስ", "ሶ", "ሷ");
            for (int i = 0; i < wrongTigrinya.size(); i++) {
                String word = entry.getWord().replace(wrongTigrinya.get(i), correctTigrinya.get(i));
                entry = new TigrinyaWordEntity(word,entry.getPos(),entry.getFreq());
            }
            return entry;
        }
    }

}
