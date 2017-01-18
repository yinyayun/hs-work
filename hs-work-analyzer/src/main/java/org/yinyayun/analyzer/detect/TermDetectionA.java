package org.yinyayun.analyzer.detect;

import java.util.List;

import org.yinyayun.analyzer.dic.Dictionary;

/**
 * @author yinyayun Term检测
 */
public abstract class TermDetectionA {
    protected Dictionary dictionary;

    public TermDetectionA() {
        dictionary = Dictionary.getDict();
    }

    public abstract List<Terms> detectTerm(String pragraph);
}
