/**
 * Copyright (c) 2016, yayunyin@126.com All Rights Reserved
 */
package org.yinyayun.crawler.business.netaporter;

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
public class NetAPorterCrawlerMain {

    public final static Logger logger = LoggerFactory.getLogger(NetAPorterCrawlerMain.class);
    public final static int batchExecutorNumber = 4;
    public final static int depth = 1;
    public final static int crawlerNumber = 4;
    public final static int delayTime = 500;
    public final static String[] mainUrls = {//
            "https://www.net-a-porter.com/gb/en/d/Shop/Bags/All?cm_sp=topnav-_-bags-_-topbar&pn=%d&npp=60&image_view=product&dscroll=0&pscroll=700"};
    public final static String checkUrl = "https://www.net-a-porter.com";
    public final static String prefixUrl = "https://www.net-a-porter.com/gb/en/product";

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
            parsers.add(new NetAReporterProductParser(resultCollector, prefixUrl));
        }
        return parsers;
    }

}
