package org.yinyayun.crawler.business.ebay;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yinyayun.crawler.agent.UserAgentFactory;
import org.yinyayun.crawler.common.CommonUtils;
import org.yinyayun.crawler.common.ParamsConstants;
import org.yinyayun.crawler.common.PropertiesUtils;
import org.yinyayun.crawler.core.CrawlerExecutor;
import org.yinyayun.crawler.core.ResultCollector;
import org.yinyayun.crawler.core.WebPageParserA;
import org.yinyayun.crawler.proxy.ProxyFactory;

/**
 * @author yinyayun
 * @version 2016年9月20日 下午1:43:04
 * 
 */
public class EbayCrawlerMain {
	public final static Logger logger = LoggerFactory.getLogger(EbayCrawlerMain.class);
	public final static int batchExecutorNumber = 4;
	public final static int depth = 1;
	public final static int crawlerNumber = 3;
	public final static int delayTime = 500;
	public final static String mainUrl = "http://www.ebay.com/sch/i.html?_from=R40&_sacat=0&_mPrRngCbx=1&_udlo=2500&_udhi=&_nkw=bag&_pgn=%d&_skc=96&rt=nc";
	public final static String checkUrl = "http://www.ebay.com/robot.txt";
	public final static String prefixUrl = "http://www.ebay.com/itm";

	public static void main(String[] args) {
		//
		String logPath = PropertiesUtils.getProperties(ParamsConstants.LOG4J_PATH);
		CommonUtils.initLog4j(logPath);
		//
		String matainPath = PropertiesUtils.getProperties(ParamsConstants.GENERATE_MATAIN_FOLD);
		String resultSavePath = CommonUtils.getResultSavePath(matainPath);
		List<String> tempFolds = CommonUtils.createTempFolds(matainPath, batchExecutorNumber);
		//
		ProxyFactory proxyFactory = new ProxyFactory(true, checkUrl);
		UserAgentFactory agentFactory = new UserAgentFactory();
		CrawlerExecutor crawlerExecutor = new CrawlerExecutor(batchExecutorNumber,
				ResultCollector.buildCollector(resultSavePath), proxyFactory, agentFactory);
		//

		List<WebPageParserA> parsers = createParsers(batchExecutorNumber,
				ResultCollector.buildCollector(resultSavePath));
		List<String> batchurls = new ArrayList<String>();
		//
		for (int i = 1; i < 200; i++) {
			batchurls.add(String.format(mainUrl, i));
			if (batchurls.size() >= batchExecutorNumber) {
				logger.info("do batch crawler for urls:{}", batchurls);
				crawlerExecutor.executorCrawlerTask(depth, crawlerNumber, delayTime, batchurls, tempFolds, parsers);
				batchurls.clear();
				CommonUtils.sleep(3000);
			}
		}
	}

	/**
	 * 创建指定个数的解析实例
	 * 
	 * @param parserNumber
	 * @param resultCollector
	 * @return
	 */
	private static List<WebPageParserA> createParsers(int parserNumber, ResultCollector resultCollector) {
		List<WebPageParserA> parsers = new ArrayList<WebPageParserA>();
		for (int i = 1; i <= parserNumber; i++) {
			parsers.add(new EbayProductParser(resultCollector, prefixUrl));
		}
		return parsers;
	}
}
