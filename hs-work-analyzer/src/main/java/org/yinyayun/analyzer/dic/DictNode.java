package org.yinyayun.analyzer.dic;

import java.util.Collection;
import java.util.HashSet;

import org.yinyayun.analyzer.dic.Dictionary.WordType;

/**
 * @author yinyayun
 */
public class DictNode {
    // 原词条信息
    private String source;
    // 同一个词条可能存在多类型
    private Collection<WordType> types;

    public void addWordType(WordType wordType) {
        if (wordType != null) {
            if (types == null) {
                types = new HashSet<WordType>();
            }
            types.add(wordType);
        }
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSource() {
        return source;
    }

    public Collection<WordType> getTypes() {
        return types;
    }

    @Override
    public String toString() {
        return String.format("source:%s,type:%s", source, source == null ? "NAN" : types.toString());
    }
}
