package org.yinyayun.crawler.seed;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Assert;
import org.junit.Test;

public class JsoupTest {
	@Test
	public void testJsoup() {
		try {
			String html = FileUtils.readFileToString(new File("/Users/yinyayun/Desktop/product.html"), "utf-8");
			Document document = Jsoup.parse(html);
			Element elements = document.getElementById("J-quick-detail");
			Elements trElements = elements.getElementsByTag("tr");
			Map<String, String> keyValues = new HashMap<String, String>();
			for (Element trElement : trElements) {
				Elements tdElements = trElement.getElementsByTag("td");
				for (int i = 0; i < tdElements.size(); i += 2) {
					Element keyElement = tdElements.get(i);
					Element valueElement = tdElements.get(i + 1);
					String key = keyElement.getElementsByTag("span").text();
					if (key.endsWith(":")) {
						key = key.substring(0, key.length() - 1);
					}
					String value = valueElement.getElementsByTag("div").text();
					keyValues.put(key, value);
				}
			}
			for (Entry<String, String> entry : keyValues.entrySet()) {
				System.out.println(String.format("%s=%s", entry.getKey(), entry.getValue()));
			}
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
}
