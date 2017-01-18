package org.yinyayun.analyzer.dic;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.yinyayun.analyzer.common.PreDecode;

/**
 * @author yinyayun 词典
 */
public class Dictionary {
    public final static String ENCODE = "UTF-8";
    public enum WordType {
        BRAND, // 品牌词
        STYLE, // 款式
        COLOR, // 颜色
        FEATURE, // 特征
        MATERICAL, // 材质
        UNKOWN// 未知
    }
    private static Dictionary dict = new Dictionary();
    private DictTree _ROOT_ = new DictTree();

    private Dictionary() {
        loadDict();
    }

    public final static Dictionary getDict() {
        return dict;
    }

    private void loadDict() {
        try {
            loadDict("dict/htable-brand.txt", WordType.BRAND);
            loadDict("dict/htable-color.txt", WordType.COLOR);
            loadDict("dict/htable-feature.txt", WordType.FEATURE);
            loadDict("dict/htable-material.txt", WordType.MATERICAL);
            loadDict("dict/htable-style.txt", WordType.STYLE);
        }
        catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 加载特定词典
     * 
     * @param dictPath
     * @param type
     * @throws IOException
     */
    private void loadDict(String dictPath, WordType type) throws IOException {
        List<String> lines = FileUtils.readLines(new File(dictPath), ENCODE);
        for (String line : lines) {
            List<String> sentences = PreDecode.predecode(line);
            if (sentences == null) {
                continue;
            }
            for (String sentence : sentences) {
                if (StringUtils.isNotEmpty(sentence)) {
                    _ROOT_.addChildNodes(sentence.split(" "), line, type);
                }
            }
        }
    }

    /**
     * 根据给定短语查找词典树中的节点
     * 
     * @param sentence
     * @return null:如果未找到对应的节点，但是返回非Null也并不表示就是找到了节点，需要判断该节点是否为一个词条的结束
     */
    public DictNode findNode(String sentence) {
        return _ROOT_.seekNode(sentence);
    }

    /**
     * 根据给定短语查找词典树中的节点
     * 
     * @param sentence
     * @return null:如果未找到对应的节点,但是返回非Null也并不表示就是找到了节点，需要判断该节点是否为一个词条的结束
     */
    public DictNode findNode(String[] tokens) {
        return _ROOT_.seekNode(tokens);
    }
}
