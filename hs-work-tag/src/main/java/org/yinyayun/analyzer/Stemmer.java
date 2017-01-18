package org.yinyayun.analyzer;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

/**
 * 单复数映射
 * 
 * @author yinyayun
 */
public class Stemmer {
    private Map<String, String> map = new HashMap<String, String>();
    private static Stemmer stemmer = null;

    private Stemmer() {
        loader("data/stemmer-lexicon.txt");
    }

    private Stemmer(String lexiconPath) {
        loader(lexiconPath);
    }

    public static Stemmer getStemmer() {
        if (stemmer == null) {
            stemmer = new Stemmer();
        }
        return stemmer;
    }

    public static Stemmer getStemmer(String lexiconPath) {
        if (stemmer == null) {
            stemmer = new Stemmer(lexiconPath);
        }
        return stemmer;
    }

    private void loader(String lexiconPath) {
        try {
            File diff = new File(lexiconPath);
            List<String> lines = FileUtils.readLines(diff);
            for (String line : lines) {
                String[] parts = line.split("##");
                if (parts.length == 2 && !parts[0].equals(parts[1]))
                    map.put(parts[0], parts[1]);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getSource(String word) {
        String source = map.get(word);
        if (StringUtils.isNotEmpty(source)) {
            return source;
        }
        return word;
    }
}
