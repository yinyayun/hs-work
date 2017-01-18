package org.yinyayun;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.yinyayun.common.PropertiesUtils;
import org.yinyayun.common.TagUtils;

/**
 * @author yinyayun
 */
public class Store2DB {
    public static void main(String[] args) throws IOException {
        String inserSql = "INSERT INTO htable_features_map(itemid,feature_ids) VALUES (?,?)";
        // 加载打完标签的结果
        String resultJson = loadResult();
        Map<String, List<String>> ids = TagUtils.stringToMap(resultJson);
        StringBuilder builder = new StringBuilder();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getConnection();
            ps = conn.prepareStatement(inserSql);
            int batch = 0;
            for (Entry<String, List<String>> entry : ids.entrySet()) {
                builder.setLength(0);
                String id = entry.getKey();
                List<String> features = entry.getValue();
                Set<String> repeates = new HashSet<String>();
                for (String fea : features) {
                    if (repeates.contains(fea)) {
                        continue;
                    }
                    repeates.add(fea);
                    if (builder.length() > 0) {
                        builder.append(",");
                    }
                    builder.append(fea);
                }
                ps.setString(1, id);
                ps.setString(2, builder.toString());
                ps.addBatch();
                if (++batch % 1000 == 0) {
                    System.out.println("批量提交");
                    ps.executeBatch();
                }
            }
            ps.executeBatch();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            TagUtils.close(ps);
            TagUtils.close(conn);
        }
    }

    public static String loadResult() throws IOException {
        PropertiesUtils propertiesUtils = PropertiesUtils.getPropertiesUtils();
        String resultPath = propertiesUtils.getTagResultPath();
        return FileUtils.readFileToString(new File(resultPath));
    }

    public static Connection getConnection() {
        PropertiesUtils propertiesUtils = PropertiesUtils.getPropertiesUtils();
        // 参数加载
        String[] dbInfos = propertiesUtils.getDBInfo();
        return TagUtils.getConnection(dbInfos[0], dbInfos[1], dbInfos[2], dbInfos[3]);
    }
}
