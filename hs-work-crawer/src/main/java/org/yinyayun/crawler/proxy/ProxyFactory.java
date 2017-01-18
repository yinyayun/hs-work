package org.yinyayun.crawler.proxy;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yinyayun.crawler.common.CommonUtils;
import org.yinyayun.crawler.common.ParamsConstants;
import org.yinyayun.crawler.common.PropertiesUtils;
import org.yinyayun.crawler.common.ProxyStruct;

/**
 * 代理获取类
 * 
 * @author yinyayun
 *
 */
public class ProxyFactory {
	public final static Logger logger_ = LoggerFactory.getLogger(ProxyFactory.class);
	private ArrayBlockingQueue<ProxyStruct> proxyQueue = new ArrayBlockingQueue<ProxyStruct>(1000);
	private Set<String> proxys = Collections.synchronizedSet(new HashSet<>());
	private boolean noneProxy = false;// 如果初始化时没有指定代理，那么则不适用阻塞获取代理的方式

	public ProxyFactory(boolean userDefineProxy, String testUrl) {
		if (userDefineProxy) {
			String proxyString = PropertiesUtils.getProperties(ParamsConstants.USER_DEFINE_PROXYS);
			loadUserDefineProxy(proxyString);
		} else {
			String agent = PropertiesUtils.getProperties(ParamsConstants.PROXY_AGENT);
			String proxyUrl = PropertiesUtils.getProperties(ParamsConstants.PROXY_SOURCE_URL);
			new Thread(new RefreshProxy(new CrawlerFreeProxy(true, agent, proxyUrl, testUrl))).start();
		}
	}

	public ProxyFactory(boolean useHttps, String agent, String proxyUrl, String testUrl) {
		new Thread(new RefreshProxy(new CrawlerFreeProxy(useHttps, agent, proxyUrl, testUrl))).start();
	}

	public ProxyStruct takeProxy() {
		if (noneProxy) {
			return null;
		}
		ProxyStruct proxyStruct = null;
		try {
			proxyStruct = proxyQueue.take();
		} catch (Exception e) {
			logger_.error(e.getMessage(), e);
		}
		return proxyStruct;
	}

	/**
	 * 归还proxy
	 * 
	 * @param proxyStruct
	 */
	public void returnProxy(ProxyStruct proxyStruct) {
		if (proxyStruct == null) {
			return;
		}
		if (proxyStruct.faildTimes >= 10) {
			cleanUnuseProxy(proxyStruct);
		} else {
			try {
				proxyQueue.put(proxyStruct);
			} catch (Exception e) {
			}
		}
	}

	private void cleanUnuseProxy(ProxyStruct proxyStruct) {
		proxys.remove(proxyStruct.toString());
	}

	private void loadProxy(CrawlerFreeProxy crawlerFreeProxy) {
		List<ProxyStruct> proxyStructs = crawlerFreeProxy.crawlerProxyList();
		for (ProxyStruct proxyStruct : proxyStructs) {
			String proxyKey = proxyStruct.toString();
			try {
				if (!proxys.contains(proxyKey)) {
					proxyQueue.put(proxyStruct);
					proxys.add(proxyKey);
					logger_.info("refresh proxy:{}", proxyKey);
				}
			} catch (Exception e) {
				logger_.error(e.getMessage(), e);
			}
		}
	}

	private void loadUserDefineProxy(String proxyString) {
		if (proxyString == null || proxyString.trim().length() == 0) {
			noneProxy = true;
		} else {
			String[] proxyStrs = proxyString.split(";");
			for (String proxyStr : proxyStrs) {
				try {
					String[] parts = proxyStr.split(":");
					if (parts.length == 2) {
						ProxyStruct proxyStruct = new ProxyStruct(parts[0], Integer.valueOf(parts[1]));
						proxyQueue.put(proxyStruct);
						proxys.add(proxyStruct.toString());
					} else if (parts.length == 4) {
						ProxyStruct proxyStruct = new ProxyStruct(parts[0], Integer.valueOf(parts[1]), parts[2],
								parts[3]);
						proxyQueue.put(proxyStruct);
						proxys.add(proxyStruct.toString());
					} else {
						logger_.error("error proxy string:{}", proxyStr);
					}
				} catch (Exception e) {
					logger_.error(String.format("error parser proxy:%s", proxyStr), e);
				}
			}
			if (proxyQueue.size() == 0) {
				noneProxy = true;
			}
		}
	}

	class RefreshProxy implements Runnable {
		private CrawlerFreeProxy crawler;

		public RefreshProxy(CrawlerFreeProxy crawler) {
			this.crawler = crawler;
		}

		@Override
		public void run() {
			while (true) {
				loadProxy(crawler);
				CommonUtils.sleep(10 * 60 * 1000);
			}
		}

	}
}
