package org.yinyayun.crawler.core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yinyayun.crawler.agent.UserAgentFactory;
import org.yinyayun.crawler.common.ProxyStruct;
import org.yinyayun.crawler.proxy.ProxyFactory;

/**
 * @author yinyayun 启动抓取线程,批量抓取指定页面
 */
public class CrawlerExecutor {
	public final static Logger logger = LoggerFactory.getLogger(CrawlerExecutor.class);
	private ExecutorService executorService;
	private ProxyFactory proxyFactory;
	private UserAgentFactory userAgentFactory;

	public CrawlerExecutor(int poolSize, ResultCollector resultCollector, ProxyFactory proxyFactory,
			UserAgentFactory userAgentFactory) {
		this.executorService = Executors.newFixedThreadPool(poolSize);
		this.proxyFactory = proxyFactory;
		this.userAgentFactory = userAgentFactory;
	}

	public void executorCrawlerTask(int depth, int threadNumber, int delayTime, List<String> urls,
			List<String> tempFolds, List<WebPageParserA> parsers) {
		List<Future<Boolean>> futures = new ArrayList<Future<Boolean>>();
		for (int i = 0; i < urls.size(); i++) {
			String agent = userAgentFactory.takeUserAgent();
			ProxyStruct proxyStruct = proxyFactory.takeProxy();
			try {
				CrawlerControl control = new CrawlerControl(urls.get(i), agent, proxyStruct, tempFolds.get(i), depth,
						threadNumber, delayTime, parsers.get(i));
				futures.add(executorService.submit(new CrawlerTask(control)));
				logger.info("execute crawler url:{} use proxy:{} useagent:{}", urls.get(i), proxyStruct, agent);
			} finally {
				proxyFactory.returnProxy(proxyStruct);
			}
		}
		for (int i = 0; i < urls.size(); i++) {
			Boolean result = null;
			try {
				result = futures.get(i).get();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
			if (result != null && result) {
				logger.info("success to crawler url:{}", urls.get(i));
			} else {
				logger.info("fail to crawler url:{}", urls.get(i));
			}
		}
	}

	class CrawlerTask implements Callable<Boolean> {
		private CrawlerControl control;

		public CrawlerTask(CrawlerControl control) {
			this.control = control;
		}

		@Override
		public Boolean call() {
			try {
				control.doCrawler();
				return true;
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				return false;
			}
		}

	}
}
