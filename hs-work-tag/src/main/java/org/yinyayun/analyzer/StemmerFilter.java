package org.yinyayun.analyzer;

import java.io.IOException;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;

/**
 * 单复数转换
 * 
 * @author yinyayun
 */
public class StemmerFilter extends TokenFilter {
    private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
    private final Stemmer stemmer = Stemmer.getStemmer();

    public StemmerFilter(Version matchVersion, TokenStream in) {
        super(in);
    }

    @Override
    public final boolean incrementToken() throws IOException {
        if (input.incrementToken()) {
            int len = termAtt.length();
            char[] chs = termAtt.buffer();
            String word = new String(chs, 0, len);
            String source = stemmer.getSource(word);
            for (int i = 0; i < source.length(); i++) {
                chs[i] = source.charAt(i);
            }
            termAtt.setLength(source.length());
            return true;
        }
        else
            return false;
    }
}
