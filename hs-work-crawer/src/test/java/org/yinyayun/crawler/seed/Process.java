/**
 * Copyright (c) 2016, yayunyin@126.com All Rights Reserved
 */
package org.yinyayun.crawler.seed;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.yinyayun.crawler.common.CommonUtils;

import com.alibaba.fastjson.JSON;

/**
 * Process.java
 *
 * @author yinyayun
 */
public class Process {
    static Pattern pattern = Pattern.compile("\\(\\w+\\)");

    public static void main(String[] args) {
        try {
            Map<String, Integer> repeates = new HashMap<String, Integer>();
            String[] paths = {"C:/Users/yinyayun/Desktop/Crawler/data/result.20161107195039.txt"};
            for (String path : paths) {
                List<String> lines = FileUtils.readLines(new File(path));
                for (String line : lines) {
                    line = line.toLowerCase().trim();
                    Map<String, String> map = JSON.parseObject(line, Map.class);
                    String str = map.get("features");
                    String[] features = str.split("##");
                    for (String feature : features) {
                        if (feature.startsWith("weighs approximately")) {
                            continue;
                        }
                        String[] parts = feature.split(",");
                        for (String part : parts) {
                            part = part.trim();
                            Matcher matcher = pattern.matcher(part);
                            if (matcher.find()) {
                                part = matcher.replaceAll("").trim();
                            }
                            if (part.split(" ").length == 1) {
                                continue;
                            }
                            Integer it = repeates.get(part);
                            if (it == null) {
                                it = 0;
                            }
                            repeates.put(part, ++it);
                        }
                    }
                }
            }
            FileUtils.deleteQuietly(new File("c:/Users/yinyayun/Desktop/process/feature-other-1108.txt"));
            for (Map.Entry<String, Integer> entry : repeates.entrySet()) {
                if (entry.getValue() >= 3) {
                    FileUtils.writeStringToFile(new File("c:/Users/yinyayun/Desktop/process/feature-other-1108"),
                            entry.getKey() + "\n", "UTF-8", true);
                }
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
