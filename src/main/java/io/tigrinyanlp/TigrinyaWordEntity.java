package io.tigrinyanlp;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;


/**
 * A convenience class to sort Tigrinya dictionary words
 *
 * @author Biniam Gebremichael
 */

public class TigrinyaWordEntity implements Comparable<TigrinyaWordEntity> {
    private final String word;
    private final String pos;
    private final Integer freq;

    public TigrinyaWordEntity(String word, String pos, Integer freq) {
        this.word = word;
        this.pos = pos;
        this.freq = freq;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TigrinyaWordEntity wordFreq = (TigrinyaWordEntity) o;
        return Objects.equals(word, wordFreq.word);
    }

    @Override
    public int hashCode() {
        return Objects.hash(word);
    }

    public String getWord() {
        return word;
    }

    public String getPos() {
        return pos;
    }

    public Integer getFreq() {
        return freq;
    }

    @Override
    public int compareTo(@NotNull TigrinyaWordEntity o) {
        return equals(o) ? 0 : (Objects.equals(o.freq, freq) ? 1 : o.freq - freq);
    }

    public String toDic() {
        return word+'\t'+word+'\t'+pos;
    }

    @Override
    public String toString() {
        return "<w f=\"" + freq + "\" flags=\"\">" + word + "</w>";
    }


    public static void main(String[] args) {
        TigrinyaWordEntity t1 = new TigrinyaWordEntity("ሃም", "NN",5);
        TigrinyaWordEntity t2 = new TigrinyaWordEntity("ሃምም", "NN",5);
        TigrinyaWordEntity t3 = new TigrinyaWordEntity("ሃም", "NN",7);
        SortedSet<TigrinyaWordEntity> s = new TreeSet<>();
        s.add(t1);
        s.add(t2);
        s.add(t3);
        for (TigrinyaWordEntity tigrinyaWordEntity : s) {
            System.out.println(tigrinyaWordEntity);
            System.out.println(tigrinyaWordEntity.toDic());
        }
    }

}



