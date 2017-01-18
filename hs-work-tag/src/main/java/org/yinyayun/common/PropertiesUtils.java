package org.yinyayun.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author yinyayun 配置项读取
 */
public class PropertiesUtils {
    public final static Logger logger = LoggerFactory.getLogger(PropertiesUtils.class);
    private Properties properties = new Properties();
    //
    private static PropertiesUtils instance = null;

    private PropertiesUtils() {
        this("conf/system.properties");
    }

    private PropertiesUtils(String configPath) {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(new File(configPath));
            properties.load(inputStream);
            logger.info("print system properties...");
            for (String key : properties.stringPropertyNames()) {
                logger.info("{}={}", key, properties.get(key));
            }
        }
        catch (Exception e) {
            throw new RuntimeException("init properties error!");
        }
        finally {
            TagUtils.close(inputStream);
        }
    }

    public static PropertiesUtils getPropertiesUtils() {
        if (instance == null) {
            instance = new PropertiesUtils();
        }
        return instance;
    }

    public static PropertiesUtils getPropertiesUtils(String configPath) {
        if (instance == null) {
            instance = new PropertiesUtils(configPath);
        }
        return instance;
    }

    public String getProperties(String key) {
        return properties.getProperty(key);
    }

    /**
     * 数据源配置
     * 
     * @return
     */
    public String[] getDBInfo() {
        String[] dbInfo = new String[4];
        dbInfo[0] = properties.getProperty(Constants.DB_DRIVER);
        dbInfo[1] = properties.getProperty(Constants.DB_URL);
        dbInfo[2] = properties.getProperty(Constants.DB_USER);
        dbInfo[3] = properties.getProperty(Constants.DB_PASSWD);
        return dbInfo;
    }

    /**
     * 索引目录
     * 
     * @return
     */
    public String getIndexPath() {
        return properties.getProperty(Constants.INDEX_PATH);
    }

    /**
     * 获取索引字段
     * 
     * @return
     */
    public String[] getIndexFields() {
        String str = properties.getProperty(Constants.INDEX_FIELDS);
        return str.split(",");
    }

    /**
     * 索引主键
     * 
     * @return
     */
    public String getIndexPrimaryField() {
        return properties.getProperty(Constants.INDEX_PRIMARY_FIELD);
    }

    /**
     * 索引的SQL
     * 
     * @return
     */
    public String getIndexSql() {
        return properties.getProperty(Constants.INDEX_SQL);
    }

    public String getLogDir() {
        return properties.getProperty(Constants.LOG_PATH);
    }

    public String getTagResultPath() {
        return properties.getProperty(Constants.TAG_RESULT_PATH);
    }
}
