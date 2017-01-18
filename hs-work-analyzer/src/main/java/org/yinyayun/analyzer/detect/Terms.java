package org.yinyayun.analyzer.detect;

import java.util.Collection;
import java.util.HashSet;

import org.yinyayun.analyzer.dic.Dictionary.WordType;

/**
 * @author yinyayun 分词结果对象
 */
public class Terms {
    private String term;
    private String source;
    private Collection<WordType> types;

    public Terms(String term, String source, Collection<WordType> types) {
        super();
        this.term = term;
        this.source = source;
        if (types == null) {
            this.types = new HashSet<WordType>();
            this.types.add(WordType.UNKOWN);
        }
        else {
            this.types = types;
        }
    }

    public String getTerm() {
        return term;
    }

    public String getSource() {
        return source;
    }

    public Collection<WordType> getType() {
        return types;
    }

    @Override
    public String toString() {
        return String.format("term:[%s],source:[%s],types:%s", term, source, types == null ? "NA" : types.toString());
    }
}
