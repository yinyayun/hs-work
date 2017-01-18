package org.yinyayun.crawler.common;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;

/**
 * @author yinyayun
 * @version 2016年9月20日 下午1:47:25
 */
public class CrawlerConfigUtils {
    public static CrawlConfig getCrawlerConfig(String tempFold, int depth, String agent, int delaytime,
            ProxyStruct proxyStruct) {
        CrawlConfig config = new CrawlConfig();
        config.setCrawlStorageFolder(tempFold);
        config.setPolitenessDelay(delaytime);
        config.setMaxDepthOfCrawling(depth);
        config.setMaxPagesToFetch(1000000);
        config.setMaxConnectionsPerHost(400);
        config.setMaxTotalConnections(400);
        config.setSocketTimeout(60000);
        config.setConnectionTimeout(60000);
        if (proxyStruct != null) {
            config.setProxyHost(proxyStruct.host);
            config.setProxyPort(proxyStruct.port);
            if (proxyStruct.passwd != null) {
                config.setProxyUsername(proxyStruct.userName);
                config.setProxyPassword(proxyStruct.passwd);
            }
        }
        if (agent != null) {
            config.setUserAgentString(agent);
        }
        config.setResumableCrawling(false);
        return config;
    }

    public static RobotstxtConfig getRobotstxtConfig(String agent) {
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        robotstxtConfig.setEnabled(false);
        robotstxtConfig.setUserAgentName(agent);
        return robotstxtConfig;
    }
}
