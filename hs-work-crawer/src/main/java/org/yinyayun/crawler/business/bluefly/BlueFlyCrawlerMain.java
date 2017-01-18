/**
 * Copyright (c) 2016, yayunyin@126.com All Rights Reserved
 */
package org.yinyayun.crawler.business.bluefly;

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
 * NetAPorterCrawlerMain.java
 *
 * @author yinyayun
 */
public class BlueFlyCrawlerMain {

    public final static Logger logger = LoggerFactory.getLogger(BlueFlyCrawlerMain.class);
    public final static int batchExecutorNumber = 4;
    public final static int depth = 1;
    public final static int crawlerNumber = 4;
    public final static int delayTime = 300;
    public final static String[] mainUrls = {//
            "http://www.bluefly.com/search?pageSize=48&startIndex=%d&query=bag&categoryId=2"};
    public final static String checkUrl = "https://www.bluefly.com";
    public final static String prefixUrl = "https://www.bluefly.com";

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
        for (String mainUrl : mainUrls) {
            for (int i = 1; i < 555; i++) {
                int pngIndex = (i - 1) * 48;
                batchurls.add(String.format(mainUrl, pngIndex));
                if (batchurls.size() >= batchExecutorNumber) {
                    logger.info("do batch crawler for urls:{}", batchurls);
                    crawlerExecutor.executorCrawlerTask(depth, crawlerNumber, delayTime, batchurls, tempFolds, parsers);
                    batchurls.clear();
                    CommonUtils.sleep(3000);
                }
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
            parsers.add(new BlueFlyProductParser(resultCollector, prefixUrl));
        }
        return parsers;
    }

}
