package org.yinyayun.index;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yinyayun.analyzer.StemmerAnalyzer;
import org.yinyayun.common.TagUtils;

/**
 * 索引创建
 * 
 * @author yinyayun
 */
public class IndexBuilderForHtable {
    public final static Logger logger = LoggerFactory.getLogger(IndexBuilderForHtable.class);
    private String driver;
    private String url;
    private String user;
    private String passwd;
    private String sql;
    private String indexPath;
    private String primaryKey;
    private String[] fields;

    public IndexBuilderForHtable(String driver, String url, String user, String passwd, String sql, String indexPath,
            String primaryKey, String[] fields) {
        super();
        this.driver = driver;
        this.url = url;
        this.user = user;
        this.passwd = passwd;
        this.sql = sql;
        this.primaryKey = primaryKey;
        this.indexPath = indexPath;
        this.fields = fields;
    }

    /**
     * 索引构建
     */
    public void buildIndex() {
        Connection conn = null;
        IndexWriter writer = null;
        Statement st = null;
        ResultSet rs = null;
        int counter = 0;
        try {
            Analyzer analyzer = new StemmerAnalyzer(Version.LUCENE_47);
            Map<String, Analyzer> fieldAnalyzers = new HashMap<String, Analyzer>();
            PerFieldAnalyzerWrapper analyzerWrapper = new PerFieldAnalyzerWrapper(analyzer, fieldAnalyzers);
            writer = TagUtils.getWriter(indexPath, analyzerWrapper);
            conn = TagUtils.getConnection(driver, url, user, passwd);
            st = conn.createStatement();
            rs = st.executeQuery(sql);
            // 遍历数据库记录
            while (rs.next()) {
                String[] values = new String[fields.length];
                for (int i = 0; i < values.length; i++) {
                    values[i] = rs.getString(fields[i]);
                }
                // 构建索引文档
                Document doc = getDocument(values);
                writer.addDocument(doc);
                if ((++counter) % 10000 == 0) {
                    System.out.println("commit index:" + counter);
                    writer.commit();
                }
            }
            writer.commit();
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        finally {
            TagUtils.close(rs);
            TagUtils.close(st);
            TagUtils.close(conn);
            TagUtils.close(writer);
        }
    }

    public Document getDocument(String[] values) {
        Document doc = new Document();
        for (int i = 0; i < fields.length; i++) {
            doc.add(buildField(fields[i], values[i]));
        }
        return doc;
    }

    /**
     * 构建不同类型的字段
     * <p>
     * 目前除主键外的字段一律采用TextField
     * 
     * @param field
     * @param value
     * @return
     */
    private IndexableField buildField(String field, String value) {
        if (field.equals(primaryKey)) {
            return new StringField(field, value, Store.NO);
        }
        else {
            String filterValue = TagUtils.htmlTagFilter(value);
            return new TextField(field, filterValue, Store.NO);
        }
    }

}
