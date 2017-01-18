/**
 * Copyright (c) 2016, yayunyin@126.com All Rights Reserved
 */
package org.yinyayun.crawler.business.netaporter;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.yinyayun.crawler.common.CommonUtils;
import org.yinyayun.crawler.core.ResultCollector;
import org.yinyayun.crawler.core.WebPageParserA;

/**
 * NetAReporterProductParser.java
 *
 * @author yinyayun
 */
public class NetAReporterProductParser extends WebPageParserA {
    private String prefixUrl;
    private String[] excludeKeywords = {"pocket", "closure", "strap", "fringe", "handle", "logo", "pockets", "closures",
            "straps", "fringes", "handles", "made in", ":"};
    private String endWith = "/p/382048401$";
    private static Pattern pattern = Pattern.compile("\\/p\\/\\d+$");

    public NetAReporterProductParser(ResultCollector resultCollector, String prefixUrl) {
        super(resultCollector);
        this.prefixUrl = prefixUrl;
    }

    @Override
    public boolean shouldVisit(String url) {
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            return true;
        }
        return false;
    }

    @Override
    public void parser(String url, Document document) {
        logger.info("parser url:{}", url);
        if (!url.startsWith(prefixUrl)) {
            return;
        }
        Map<String, String> attrs = new HashMap<String, String>();
        StringBuilder builder = new StringBuilder();
        Element element = document.getElementById("accordion-2");
        if (element != null) {
            Elements classElements = element.getElementsByClass("font-list-copy");
            for (Element classElement : classElements) {
                Elements liElements = classElement.getElementsByTag("li");
                for (Element liElement : liElements) {
                    String text = liElement.text();
                    if (text.startsWith("-")) {
                        text = text.substring(1).trim();
                    }
                    if (!exclude(text)) {
                        if (builder.length() > 0) {
                            builder.append("##");
                        }
                        builder.append(text);
                    }
                }
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
