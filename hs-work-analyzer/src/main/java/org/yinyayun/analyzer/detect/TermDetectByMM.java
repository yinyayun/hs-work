package org.yinyayun.analyzer.detect;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.yinyayun.analyzer.common.PreDecode;
import org.yinyayun.analyzer.dic.DictNode;

/**
 * @author yinyayun 基于正向最大匹配进行分词
 */
public class TermDetectByMM extends TermDetectionA {
    // 最大探测长度
    private int maxDetectLen = 8;

    public TermDetectByMM(int maxDetectLen) {
        this.maxDetectLen = maxDetectLen;
    }

    public TermDetectByMM() {
        this(8);
    }

    /**
     * 如果没有分词结果，则返回null
     */
    @Override
    public List<Terms> detectTerm(String pragraph) {
        if (StringUtils.isEmpty(pragraph)) {
            return null;
        }
        List<Terms> terms = new ArrayList<Terms>();
        List<String> sentences = PreDecode.predecode(pragraph);
        for (String sentence : sentences) {
            String[] tokens = sentence.split(" ");
            terms.addAll(mmDetect(tokens));
        }
        return terms;
    }

    /**
     * 正想最大检测
     * 
     * @param tokens
     * @return
     */
    public List<Terms> mmDetect(String[] tokens) {
        List<Terms> terms = new ArrayList<Terms>();
        int tokenSize = tokens.length;
        for (int i = 0; i < tokenSize; i++) {
            boolean detectTerm = false;
            for (int j = Math.min(tokenSize, i + maxDetectLen); j > i; j--) {
                String[] subTokens = (String[]) ArrayUtils.subarray(tokens, i, j);
                DictNode dictNode = dictionary.findNode(subTokens);
                // 找到节点，并且找到的节点并是一个独立词条的根节点
                if (dictNode != null && StringUtils.isNotEmpty(dictNode.getSource())) {
                    i = j - 1;
                    detectTerm = true;
                    terms.add(new Terms(arrayToString(subTokens), dictNode.getSource(), dictNode.getTypes()));
                    break;
                }
            }
            if (!detectTerm) {
                terms.add(new Terms(tokens[i], tokens[i], null));
            }
        }
        return terms;
    }

    private String arrayToString(String[] arrays) {
        StringBuilder builder = new StringBuilder();
        for (String array : arrays) {
            if (builder.length() > 0) {
                builder.append(" ");
            }
            builder.append(array);
        }
        return builder.toString();
    }
}
