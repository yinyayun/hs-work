package org.yinyayun.crawler.core;

import org.yinyayun.crawler.common.CrawlerConfigUtils;
import org.yinyayun.crawler.common.ProxyStruct;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.crawler.CrawlController.WebCrawlerFactory;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

/**
 * @author yinyayun
 * 
 *         完成Crawler4j的实际启动
 */
public class CrawlerControl {
	private String seedUrl;
	private String agent;
	private ProxyStruct proxyStruct;
	private String tempFold;
	private int depth;
	private int threadNumber;
	private int delayTime;
	private WebPageParserA parser;

	public CrawlerControl(String seedUrl, String agent, ProxyStruct proxyStruct, String tempFold, int depth,
			int threadNumber, int delayTime, WebPageParserA parser) {
		this.seedUrl = seedUrl;
		this.agent = agent;
		this.proxyStruct = proxyStruct;
		this.tempFold = tempFold;
		this.depth = depth;
		this.threadNumber = threadNumber;
		this.delayTime = delayTime;
		this.parser = parser;
	}

	public void doCrawler() throws Exception {
		CrawlConfig crawlConfig = CrawlerConfigUtils.getCrawlerConfig(tempFold, depth, agent, delayTime, proxyStruct);
		RobotstxtConfig robotstxtConfig = CrawlerConfigUtils.getRobotstxtConfig(agent);
		PageFetcher pageFetcher = new PageFetcher(crawlConfig);
		RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
		CrawlController controller = new CrawlController(crawlConfig, pageFetcher, robotstxtServer);
		controller.addSeed(seedUrl);
		WebCrawlerFactory<WebPageParserA> webCrawlerFactory = new WebCrawlerFactory<WebPageParserA>() {

			public WebPageParserA newInstance() throws Exception {
				return parser;
			}
		};
		// 以阻塞方式启动
		controller.start(webCrawlerFactory, threadNumber);
	}
}
