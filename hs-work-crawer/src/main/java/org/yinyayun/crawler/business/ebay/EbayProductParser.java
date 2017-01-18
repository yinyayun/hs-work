package org.yinyayun.crawler.business.ebay;

import java.util.HashMap;
import java.util.Map;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.yinyayun.crawler.common.CommonUtils;
import org.yinyayun.crawler.core.ResultCollector;
import org.yinyayun.crawler.core.WebPageParserA;

/**
 * @author yinyayun ebay页面解析逻辑
 */
public class EbayProductParser extends WebPageParserA {
	private String prefixUrl;

	public EbayProductParser(ResultCollector resultCollector, String prefixUrl) {
		super(resultCollector);
		this.prefixUrl = prefixUrl;
	}

	@Override
	public boolean shouldVisit(String url) {
		return url.startsWith(prefixUrl);
	}

	@Override
	public void parser(String url, Document document) {
		logger.info("parser url:{}", url);
		if (!url.startsWith(prefixUrl)) {
			return;
		}
		Map<String, String> attrs = new HashMap<String, String>();
		Elements itemElement = document.getElementsByClass("itemAttr");
		if (itemElement == null) {
			return;
		}
		for (Element element : itemElement) {
			Elements attrElements = element.getElementsByTag("td");
			for (int i = 0; i < attrElements.size(); i += 2) {
				String key = attrElements.get(i).text();
				if (key != null && key.length() > 0) {
					Elements valueElements = attrElements.get(i + 1).getElementsByTag("span");
					StringBuilder builder = new StringBuilder();
					for (Element valueElemet : valueElements) {
						builder.append(valueElemet.text());
					}
					if (key.endsWith(":")) {
						key = key.substring(0, key.length() - 1);
					}
					attrs.put(key, builder.toString());
				}
			}
		}
		if (attrs.size() > 0) {
			attrs.put("productUrl", url);
			resultCollector.putString(CommonUtils.mapToString(attrs));
		}
	}
}
