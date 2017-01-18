package org.yinyayun.crawler.agent;

import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 创建useragent
 * 
 * @author yinyayun
 *
 */
public class UserAgentFactory {
	public final static Logger logger_ = LoggerFactory.getLogger(UserAgentFactory.class);
	private AtomicLong point = new AtomicLong(0);
	private List<String> agents;
	private int agetsNumber;

	public UserAgentFactory() {
		init();
	}

	private void init() {
		try {
			agents = FileUtils.readLines(new File("conf/user-agent.txt"), "UTF-8");
			this.agetsNumber = agents.size();
		} catch (Exception e) {
			logger_.error("load user-agent.txt error!");
		}
	}

	/**
	 * 获取agent
	 * 
	 * @return
	 */
	public String takeUserAgent() {
		if (agents == null) {
			return null;
		}
		int index = (int) (point.incrementAndGet() % agetsNumber);
		String useragent = agents.get(index);
		logger_.info("take agent:{}", useragent);
		return useragent;
	}
}
