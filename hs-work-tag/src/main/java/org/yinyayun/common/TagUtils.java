package org.yinyayun.common;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

/**
 * 工具类
 * 
 * @author yinyayun
 */
public class TagUtils {
    public final static Logger logger = LoggerFactory.getLogger(TagUtils.class);
    public final static Pattern PATTERN_HTML = Pattern.compile("<[^>]+>", Pattern.CASE_INSENSITIVE);

    /**
     * 构建indexWriter
     * 
     * @param analyzer
     * @return
     * @throws IOException
     */
    public static IndexWriter getWriter(String indexPath, Analyzer analyzer) throws IOException {
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_47, analyzer);
        indexWriterConfig.setOpenMode(OpenMode.CREATE);
        return new IndexWriter(getDiectionary(indexPath), indexWriterConfig);
    }

    /**
     * 获取索引目录
     * 
     * @return
     * @throws IOException
     */
    public static Directory getDiectionary(String indexPath) throws IOException {
        return FSDirectory.open(new File(indexPath));
    }

    /**
     * 获取检索器
     * 
     * @return
     * @throws IOException
     */
    public static IndexSearcher getSearcher(String indexPath) throws IOException {
        Directory dir = getDiectionary(indexPath);
        IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(dir));
        return searcher;
    }

    /**
     * map转string
     * 
     * @param map
     * @return
     */
    public static String mapToString(Map<String, List<String>> map) {
        return JSON.toJSONString(map);
    }

    /**
     * str转map
     * 
     * @param str
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Map<String, List<String>> stringToMap(String str) {
        Map<String, List<String>> map = JSON.parseObject(str, Map.class);
        return map;
    }

    /**
     * 获取数据库连接
     * 
     * @param driver
     * @param url
     * @param user
     * @param passwd
     * @return
     */
    public static Connection getConnection(String driver, String url, String user, String passwd) {
        Connection conn = null;
        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, user, passwd);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }

    /**
     * 过滤HTML标签
     * 
     * @param source
     * @return
     */
    public static String htmlTagFilter(String source) {
        source = source.trim().toLowerCase();
        Matcher matcher = PATTERN_HTML.matcher(source);
        return matcher.replaceAll(""); // 过滤html标签
    }

    public static void close(AutoCloseable c) {
        if (c != null) {
            try {
                c.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void close(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
