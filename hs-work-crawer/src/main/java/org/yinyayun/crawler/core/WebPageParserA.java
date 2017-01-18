package org.yinyayun.crawler.core;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.yinyayun.crawler.common.ParamsConstants;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

/**
 * @author yinyayun 页面解析抽象
 */
public abstract class WebPageParserA extends WebCrawler {
	protected ResultCollector resultCollector;

	public WebPageParserA(ResultCollector resultCollector) {
		this.resultCollector = resultCollector;
	}

	public abstract boolean shouldVisit(String url);

	public abstract void parser(String url, Document document);

	@Override
	public void visit(Page page) {
		String url = page.getWebURL().getURL();
		if (page.getParseData() instanceof HtmlParseData) {
			HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
			String html = htmlParseData.getHtml();
			parser(url, Jsoup.parse(html));
		}
	}

	@Override
	public boolean shouldVisit(Page referringPage, WebURL url) {
		final String href = url.getURL().toLowerCase();
		if (ParamsConstants.IMAGE_EXTENSIONS.matcher(href).matches()) {
			return false;
		}
		return shouldVisit(href);
	}
}
