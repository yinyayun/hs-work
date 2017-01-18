package org.yinyayun.crawler.business.luisaviaroma;

import java.util.HashMap;
import java.util.Map;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.yinyayun.crawler.common.CommonUtils;
import org.yinyayun.crawler.core.ResultCollector;
import org.yinyayun.crawler.core.WebPageParserA;

/**
 * @author yinyayun ebay页面解析逻辑
 */
public class LuisaviaromaProductParser extends WebPageParserA {
    private String prefixUrl;
    private String[] excludeKeywords = {"pocket", "closure", "strap", "fringe", "handle", "logo", "pockets", "closures",
            "straps", "fringes", "handles", "made in", ":"};

    public LuisaviaromaProductParser(ResultCollector resultCollector, String prefixUrl) {
        super(resultCollector);
        this.prefixUrl = prefixUrl;
    }

    @Override
    public boolean shouldVisit(String url) {
        if (url.indexOf("subline") == -1) {
            return false;
        }
        return url.startsWith(prefixUrl);
    }

    @Override
    public void parser(String url, Document document) {
        logger.info("parser url:{}", url);
        if (url.toLowerCase().indexOf("subline") == -1) {
            return;
        }
        Map<String, String> attrs = new HashMap<String, String>();
        Element sp_details = document.getElementById("sp_details");
        if (sp_details == null) {
            return;
        }
        StringBuilder builder = new StringBuilder();
        for (Element element : sp_details.getElementsByTag("li")) {
            String text = element.text();
            // 必须不包含特殊的关键词
            if (!exclude(text)) {
                if (builder.length() > 0) {
                    builder.append("##");
                }
                builder.append(text);
            }
        }
        if (builder.length() > 0) {
            attrs.put("features", builder.toString());
            resultCollector.putString(CommonUtils.mapToString(attrs));
        }
    }

    private boolean exclude(String setence) {
        setence = setence.toLowerCase();
        for (String excludeKeyword : excludeKeywords) {
            if (setence.indexOf(" ".concat(excludeKeyword).concat(" ")) > -1
                    || setence.indexOf(" ".concat(excludeKeyword)) > -1
                    || setence.indexOf(excludeKeyword.concat(" ")) > -1) {
                return true;
            }
        }
        return false;
    }
}
