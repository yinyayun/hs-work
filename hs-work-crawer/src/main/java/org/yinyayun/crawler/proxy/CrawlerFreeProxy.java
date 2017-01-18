package org.yinyayun.crawler.proxy;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.net.telnet.TelnetClient;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yinyayun.crawler.common.ProxyStruct;

/**
 * 抓取可用的代理
 * 
 * @author yinyayun
 *
 */
public class CrawlerFreeProxy {
	public final static Logger logger = LoggerFactory.getLogger(CrawlerFreeProxy.class);
	private boolean useHttps = false;
	private String testUrl;
	private String proxyUrl;
	private String userAgent;

	public CrawlerFreeProxy(boolean useHttps, String userAgent, String proxyUrl, String testUrl) {
		this.testUrl = testUrl;
		this.useHttps = useHttps;
		this.proxyUrl = proxyUrl;
		this.userAgent = userAgent;
	}

	/**
	 * 解析代理页面，主要针对快代理页面进行抓取
	 * 
	 * @return
	 */
	public List<ProxyStruct> crawlerProxyList() {
		List<ProxyStruct> proxyStructs = new ArrayList<ProxyStruct>();
		try {
			proxyStructs.addAll(crawlerProxyList(1));
		} catch (MyTimeOutException e) {
			try {
				proxyStructs.addAll(e.proxyStructs);
				proxyStructs.addAll(crawlerProxyList(e.errorPageIndex));
			} catch (Exception e1) {
				logger.error(e.getMessage(), e);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return proxyStructs;
	}

	/**
	 * 从第几页抓取
	 * 
	 * @param forPageIndex
	 * @return
	 * @throws Exception
	 */
	private List<ProxyStruct> crawlerProxyList(int forPageIndex) throws Exception {
		logger.info("crawler proxy...");
		List<ProxyStruct> proxyStructs = new ArrayList<ProxyStruct>();
		int index = forPageIndex;
		try {
			for (; index <= 10; index++) {
				String url = proxyUrl + "/" + index;
				Document document = Jsoup.connect(url).get();
				Element freelist = document.getElementById("index_free_list");
				Elements tbodyElements = freelist.getElementsByTag("tbody");
				for (Element tbElement : tbodyElements) {
					Elements trElements = tbElement.getElementsByTag("tr");
					for (Element trElement : trElements) {
						Elements tdElements = trElement.getElementsByTag("td");
						String host = tdElements.get(0).text();
						int port = Integer.valueOf(tdElements.get(1).text());
						String httpType = tdElements.get(3).text();
						// 仅仅抓取HTTP,HTTPS方式的代理
						if (useHttps && httpType.indexOf("HTTPS") == -1) {
							continue;
						}
						logger.info("start check proxy:{}-{}-{}", host, port, httpType);
						if (telnetTest(host, port)) {
							ProxyStruct proxyStruct = new ProxyStruct(host, port);
							proxyStructs.add(proxyStruct);
							logger.info("add proxy:{}:{} to useable queue.", host, port);
							return proxyStructs;
						}
						logger.info("end check proxy:{}-{}-{}", host, port, httpType);
					}
				}
			}
		} catch (SocketTimeoutException e) {
			throw new MyTimeOutException(index, proxyStructs, e);
		} catch (Exception e) {
			throw e;
		}
		return proxyStructs;
	}

	private boolean telnetTest(String host, int port) {
		TelnetClient telnet = new TelnetClient();
		try {
			telnet.setConnectTimeout(500);
			telnet.setDefaultTimeout(500);
			telnet.connect(host, port);
		} catch (Exception e) {
			return false;
		} finally {
			try {
				telnet.disconnect();
			} catch (IOException e) {
			}
		}
		return testHttpByJsoup(host, port);
	}
	/**
	 * 用该代理测试对应的http页面
	 * @param host
	 * @param port
	 * @return
	 */
	private boolean testHttpByJsoup(String host, int port) {
		Connection conn = Jsoup.connect(testUrl);
		conn.proxy(host, port);
		conn.userAgent(userAgent);
		conn.timeout(3000);
		try {
			conn.get();
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	class MyTimeOutException extends Exception {
		private static final long serialVersionUID = 1L;
		int errorPageIndex = 0;
		List<ProxyStruct> proxyStructs;

		public MyTimeOutException(int errorPageIndex, List<ProxyStruct> proxyStructs, Throwable cause) {
			super(cause);
			this.proxyStructs = proxyStructs;
			this.errorPageIndex = errorPageIndex;
		}
	}
}
