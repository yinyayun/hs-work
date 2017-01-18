package org.yinyayun.crawler.common;

/**
 * 代理数据
 * 
 * @author yinyayun
 *
 */
public class ProxyStruct {
	public String host;
	public int port;
	public int faildTimes;
	public String userName;
	public String passwd;
	public final static int threshold = 10;

	public ProxyStruct(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public ProxyStruct(String host, int port, String userName, String passwd) {
		this(host, port);
		this.userName = userName;
		this.passwd = passwd;

	}

	public void fail() {
		faildTimes++;
	}

	@Override
	public String toString() {
		return host + ":" + port;
	}
}
