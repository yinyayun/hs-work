package org.yinyayun.analyzer.dic;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.yinyayun.analyzer.dic.Dictionary.WordType;

/**
 * @author yinyayun 简单词典树
 */
public class DictTree {
    private Map<String, DictTree> childs = new HashMap<String, DictTree>(50);
    private DictNode currentNode = new DictNode();

    private DictTree addChildNode(String word, String source, WordType type) {
        DictTree dictTree = childs.get(word);
        if (dictTree == null) {
            dictTree = new DictTree();
            childs.put(word, dictTree);
        }
        return dictTree;
    }

    /**
     * 加载词条
     * 
     * @param words
     * @param source
     * @param type
     * @return
     */
    public DictTree addChildNodes(String[] words, String source, WordType type) {
        DictTree dictTree = this;
        for (String word : words) {
            dictTree = dictTree.addChildNode(word, source, type);
        }
        dictTree.currentNode.setSource(source);
        dictTree.currentNode.addWordType(type);
        return dictTree;
    }

    /**
     * 查找节点
     * 
     * @param sentence
     * @return
     */
    public DictNode seekNode(String sentence) {
        if (StringUtils.isEmpty(sentence)) {
            return null;
        }
        return seekNode(sentence.split(" "));
    }

    /**
     * 查找节点
     * 
     * @param sentences
     * @return
     */
    public DictNode seekNode(String[] sentences) {
        if (ArrayUtils.isEmpty(sentences)) {
            return null;
        }
        DictTree tree = this;
        for (String sentence : sentences) {
            tree = tree.getChild(sentence);
            if (tree == null) {
                return null;
            }
        }
        return tree.currentNode;
    }

    private DictTree getChild(String word) {
        return childs.get(word);
    }
}
