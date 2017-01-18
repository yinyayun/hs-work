package org.yinyayun;

import org.yinyayun.common.PropertiesUtils;
import org.yinyayun.index.IndexBuilderForHtable;

/**
 * 创建索引
 * 
 * @author yinyayun
 */
public class IndexBuilderMain {
    public static void main(String[] args) {
        PropertiesUtils propertiesUtils = PropertiesUtils.getPropertiesUtils();
        // 参数加载
        String[] dbInfos = propertiesUtils.getDBInfo();
        String sql = propertiesUtils.getIndexSql();
        String indexPath = propertiesUtils.getIndexPath();
        String primaryKey = propertiesUtils.getIndexPrimaryField();
        String[] fields = propertiesUtils.getIndexFields();
        // 索引builder
        IndexBuilderForHtable indexBuilderForHtable = new IndexBuilderForHtable(dbInfos[0], dbInfos[1], dbInfos[2],
                dbInfos[3], sql, indexPath, primaryKey, fields);
        indexBuilderForHtable.buildIndex();
    }

}
