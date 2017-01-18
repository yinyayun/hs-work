package org.yinyayun.search;

import java.io.Closeable;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.util.Version;
import org.yinyayun.analyzer.StemmerAnalyzer;
import org.yinyayun.common.TagUtils;

/**
 * 对Hatble进行检索,采用跨度
 * 
 * @author yinyayun
 */
public class SearchForHtable implements Closeable {
    private IndexSearcher searcher;

    public SearchForHtable(String indexPath) throws IOException {
        searcher = TagUtils.getSearcher(indexPath);
    }

    /**
     * @param fields 检索字段
     * @param returenFields 返回字段
     * @param keyword 检索关键词
     * @param slop 跨度长度
     * @return
     * @throws IOException
     */
    public List<String> span_search(String[] fields, String returenFields, String keyword, int slop)
            throws IOException {
        List<String> tokens = analyzer(keyword);
        if (tokens.size() > 0) {
            System.out.println(tokens);
            BooleanQuery booleanQuery = new BooleanQuery();
            for (String field : fields) {
                PhraseQuery phraseQuery = new PhraseQuery();
                for (int i = 0; i < tokens.size(); i++) {
                    phraseQuery.add(new Term(field, tokens.get(i)));
                }
                phraseQuery.setSlop(slop);
                booleanQuery.add(phraseQuery, Occur.SHOULD);
            }
            System.out.println("query:" + booleanQuery.toString());
            HatbleCollector collector = new HatbleCollector(returenFields);
            searcher.search(booleanQuery, collector);
            return collector.getPrimaryKeys();
        }
        return new ArrayList<String>();
    }

    @SuppressWarnings("resource")
    public List<String> analyzer(String keyword) throws IOException {
        List<String> tokens = new ArrayList<String>();
        TokenStream tokenStream = new StemmerAnalyzer(Version.LUCENE_47).tokenStream("", new StringReader(keyword));
        tokenStream.reset();
        CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
        while (tokenStream.incrementToken()) {
            tokens.add(charTermAttribute.toString());
        }
        tokenStream.end();
        return tokens;
    }

    public void close() throws IOException {
        searcher.getIndexReader().close();
    }
}
