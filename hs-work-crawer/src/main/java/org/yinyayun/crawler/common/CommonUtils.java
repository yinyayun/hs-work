package org.yinyayun.crawler.common;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

public class CommonUtils {
	public final static Logger logger = LoggerFactory.getLogger(CommonUtils.class);

	public static String mapToString(Map<String, String> map) {
		return JSON.toJSONString(map);
	}

	@SuppressWarnings("unchecked")
	public static Map<String, String> strToMap(String str) {
		return JSON.parseObject(str, Map.class);
	}

	public static void sleep(long milons) {
		try {
			Thread.sleep(milons);
		} catch (Exception e) {
			logger.info(e.getMessage(), e);
		}
	}

	public static void initLog4j(String logFold) {
		InputStream inputStream = null;
		File fold = new File(logFold);
		try {
			if (!fold.exists()) {
				fold.mkdirs();
			}
			inputStream = new FileInputStream(new File("conf/log4j.properties"));
			Properties properties = new Properties();
			properties.load(inputStream);
			properties.setProperty("log4j.file.dir", logFold);
			PropertyConfigurator.configure(properties);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			close(inputStream);
		}
	}

	public static void close(Closeable closeable) {
		try {
			if (closeable != null)
				closeable.close();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	public static String getSuffix() {
		return new SimpleDateFormat(ParamsConstants.TIME_EXPRESS).format(new Date());
	}

	/**
	 * 构建文件的保存路径
	 * 
	 * @param mainFold
	 * @return
	 */
	public static String getResultSavePath(String mainFold) {
		File mainDir = new File(mainFold);
		if (!mainDir.exists()) {
			mainDir.mkdirs();
		}
		File resultDir = new File(mainDir, "data");
		if (!resultDir.exists()) {
			resultDir.mkdirs();
		}
		String resultName = String.format("result.%s.txt", getSuffix());
		return new File(resultDir, resultName).getAbsolutePath();
	}

	/**
	 * 构建temp目录
	 * 
	 * @param mainFold
	 * @param foldNumber
	 * @return
	 */
	public static List<String> createTempFolds(String mainFold, int foldNumber) {
		File mainDir = new File(mainFold);
		if (!mainDir.exists()) {
			mainDir.mkdirs();
		}
		File tempDir = new File(mainDir, "temp");
		if (!tempDir.exists()) {
			tempDir.mkdirs();
		}
		List<String> tempFolds = new ArrayList<String>();
		for (int i = 1; i <= foldNumber; i++) {
			tempFolds.add(new File(tempDir, "" + i).getAbsolutePath());
		}
		return tempFolds;
	}
}
