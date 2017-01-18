package org.yinyayun.analyzer;

import java.io.IOException;
import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.Version;

/**
 * 英文单复数分词,基于标准分词，但是不作停用词处理
 * 
 * @author yinyayun
 */
public class StemmerAnalyzer extends Analyzer {

    public static final int DEFAULT_MAX_TOKEN_LENGTH = 5000;

    private int maxTokenLength = DEFAULT_MAX_TOKEN_LENGTH;

    public static final CharArraySet STOP_WORDS_SET = StopAnalyzer.ENGLISH_STOP_WORDS_SET;
    private Version matchVersion;

    public StemmerAnalyzer(Version matchVersion) {
        super();
        this.matchVersion = matchVersion;
    }

    public void setMaxTokenLength(int length) {
        maxTokenLength = length;
    }

    public int getMaxTokenLength() {
        return maxTokenLength;
    }

    @Override
    protected TokenStreamComponents createComponents(final String fieldName, final Reader reader) {
        final StandardTokenizer src = new StandardTokenizer(matchVersion, reader);
        src.setMaxTokenLength(maxTokenLength);
        TokenStream tok = new StandardFilter(matchVersion, src);
        tok = new LowerCaseFilter(matchVersion, tok);
        tok = new StemmerFilter(matchVersion, tok);
        return new TokenStreamComponents(src, tok) {
            @Override
            protected void setReader(final Reader reader) throws IOException {
                src.setMaxTokenLength(StemmerAnalyzer.this.maxTokenLength);
                super.setReader(reader);
            }
        };
    }
}
