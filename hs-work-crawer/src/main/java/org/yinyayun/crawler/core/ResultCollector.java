package org.yinyayun.crawler.core;

import java.io.File;
import java.util.concurrent.ArrayBlockingQueue;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author yinyayun 抓取结果收集与异步存储
 */
public class ResultCollector {
	public final static Logger logger_ = LoggerFactory.getLogger(ResultCollector.class);
	private ArrayBlockingQueue<String> blockingQueue = new ArrayBlockingQueue<String>(1000);
	private static ResultCollector resultCollector;
	private File resultFile;

	public ResultCollector(String resultSavePath) {
		this.resultFile = new File(resultSavePath);
		new Thread(new CollectorThread()).start();
	}

	public static ResultCollector buildCollector(String resultSavePath) {
		if (resultCollector == null) {
			resultCollector = new ResultCollector(resultSavePath);
		}
		return resultCollector;
	}

	public void putString(String line) {
		try {
			blockingQueue.put(line);
		} catch (InterruptedException e) {
			logger_.error(e.getMessage(), e);
		}
	}

	public String takeString() throws InterruptedException {
		return blockingQueue.take();
	}

	class CollectorThread implements Runnable {

		@Override
		public void run() {
			while (true) {
				try {
					String result = blockingQueue.take();
					FileUtils.writeStringToFile(resultFile, result.concat("\n"), "UTF-8", true);
					logger_.info("store result:{}", result);
				} catch (Exception e) {
					logger_.error(e.getMessage(), e);
				}
			}
		}

	}
}
