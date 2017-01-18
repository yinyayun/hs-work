package org.yinyayun.crawler.business.luisaviaroma;

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
 */
public class LuisaviaromaCrawlerMain {
    public final static Logger logger = LoggerFactory.getLogger(LuisaviaromaCrawlerMain.class);
    public final static int batchExecutorNumber = 4;
    public final static int depth = 1;
    public final static int crawlerNumber = 3;
    public final static int delayTime = 500;
    public final static String[] mainUrls = {//
            "http://www.luisaviaroma.com/women/catalog/bags/view+all/lang_EN/lineid_22/catid_0?Page=%d", //
            "http://www.luisaviaroma.com/men/catalog/bags/view+all/lang_EN/lineid_22/catid_0?Page=%d"};
    public final static String checkUrl = "http://www.luisaviaroma.com";
    public final static String prefixUrl = "http://www.luisaviaroma.com";

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
            for (int i = 1; i < 50; i++) {
                batchurls.add(String.format(mainUrl, i));
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
            parsers.add(new LuisaviaromaProductParser(resultCollector, prefixUrl));
        }
        return parsers;
    }
}
