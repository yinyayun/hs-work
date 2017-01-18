package org.yinyayun.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.SortedDocValues;
import org.apache.lucene.search.FieldCache;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.util.BytesRef;

/**
 * 自定义collector,完成所有命中产品ID的收集
 * 
 * @author yinyayun
 */
public class HatbleCollector extends org.apache.lucene.search.Collector {
    private List<String> primaryKeys = new ArrayList<String>();
    private String primaryField;
    private SortedDocValues sortedDocValues;

    public HatbleCollector(String primaryField) {
        this.primaryField = primaryField;
    }

    @Override
    public void setScorer(Scorer scorer) throws IOException {
    }

    @Override
    public void collect(int doc) throws IOException {
        primaryKeys.add(getPrimaryValue(doc));
    }

    private String getPrimaryValue(int doc) {
        BytesRef result = new BytesRef();
        sortedDocValues.get(doc, result);
        return result.utf8ToString();
    }

    @Override
    public void setNextReader(AtomicReaderContext context) throws IOException {
        sortedDocValues = FieldCache.DEFAULT.getTermsIndex(context.reader(), primaryField);
    }

    @Override
    public boolean acceptsDocsOutOfOrder() {
        return false;
    }

    public List<String> getPrimaryKeys() {
        return primaryKeys;
    }

}
