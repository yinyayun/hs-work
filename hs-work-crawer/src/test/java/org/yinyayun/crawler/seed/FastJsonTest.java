package org.yinyayun.crawler.seed;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.alibaba.fastjson.JSON;

public class FastJsonTest {
	@Test
	public void testMapToJsonString() {
		String str = "{\"key1\":\"value1\",\"key2\":\"value2\",\"key3\":\"value3\"}";
		Map<String, String> map = new HashMap<String, String>();
		map.put("key1", "value1");
		map.put("key2", "value2");
		map.put("key3", "value3");
		Assert.assertEquals(JSON.toJSONString(map).equals(str), true);
	}

	@SuppressWarnings("unchecked")
	public void testStringToMap() {
		String str = "{\"key1\":\"value1\",\"key2\":\"value2\",\"key3\":\"value3\"}";
		Map<String, String> map = JSON.parseObject(str, Map.class);
		Assert.assertEquals(map.get("key1").equals("value1"), true);
		Assert.assertEquals(map.get("key2").equals("value2"), true);
		Assert.assertEquals(map.get("key3").equals("value3"), true);
	}

}
