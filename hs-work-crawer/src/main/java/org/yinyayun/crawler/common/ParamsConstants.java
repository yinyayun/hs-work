package org.yinyayun.crawler.common;

import java.util.regex.Pattern;

public class ParamsConstants {
	public final static String TIME_EXPRESS = "yyyyMMddHHmmss";
	public final static Pattern IMAGE_EXTENSIONS = Pattern.compile(".*\\.(bmp|gif|jpg|png)$");
	//
	public final static String LOG4J_PATH = "log_path";
	public final static String PROXY_AGENT = "proxy_test_agent";
	public final static String PROXY_SOURCE_URL = "remote_proxy_url";
	public final static String GENERATE_MATAIN_FOLD = "generate_main_fold";
	public final static String USER_DEFINE_PROXYS = "user_define_proxys";
}
