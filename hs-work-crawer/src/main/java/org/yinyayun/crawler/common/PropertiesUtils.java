package org.yinyayun.crawler.common;

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
	private static Properties properties = new Properties();

	static {
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(new File("conf/system.properties"));
			properties.load(inputStream);
			logger.info("print system properties...");
			for (String key : properties.stringPropertyNames()) {
				logger.info("{}={}", key, properties.get(key));
			}
		} catch (Exception e) {
			throw new RuntimeException("init properties error!");
		} finally {
			CommonUtils.close(inputStream);
		}
	}

	public static String getProperties(String key) {
		return properties.getProperty(key);
	}
}
