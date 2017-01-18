package org.yinyayun.analyzer.common;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * @author yinyayun 前期预处理
 */
public class PreDecode {
    public final static char WHITE_SPACE = ' ';
    public final static char[] SPLITS = {',', ';', '!', ':'};
    // 结束符号,许要特殊处理,很多时候并不代表结束
    public final static char EMD = '.';
    // 括号开始
    public final static char[] BRACKETS_BEG = {'(', '[', '{'};
    // 括号结束
    public final static char[] BRACKETS_END = {')', ']', '}'};

    /**
     * 预处理：去除多余空格，去除特殊符号 将段落转成句子
     * 
     * @param pragraph
     * @return
     */
    public static List<String> predecode(String pragraph) {
        if (StringUtils.isEmpty(pragraph)) {
            return null;
        }
        //
        List<String> sentences = new ArrayList<String>();
        StringBuilder builder = new StringBuilder();
        pragraph = pragraph.toLowerCase().trim();
        char lastChar = 1;
        for (int i = 0; i < pragraph.length(); i++) {
            boolean lastChIsWhiteSpace = whiteSpace(lastChar);
            char ch = pragraph.charAt(i);
            if (Character.isLetter(ch) || Character.isDigit(ch)) {
                builder.append(ch);
                lastChar = ch;
            }
            // 空格处理
            else if (ch == WHITE_SPACE) {
                if (lastChar == EMD) {
                    sentences.add(builder.toString().trim());
                    builder.setLength(0);
                    lastChar = 1;
                }
                else if (!lastChIsWhiteSpace) {
                    builder.append(ch);
                    lastChar = ch;
                }
            }
            else if (ch == '/') {
                if (!lastChIsWhiteSpace) {
                    builder.append(WHITE_SPACE);
                }
                lastChar = WHITE_SPACE;
            }
            // 连接符处理
            else if (isConnector(ch)) {
                if (!lastChIsWhiteSpace) {
                    builder.append(WHITE_SPACE);
                }
                builder.append(ch);
                builder.append(WHITE_SPACE);
                lastChar = WHITE_SPACE;
            }
            // 引号处理
            else if (isQuotation(ch)) {
                if (Character.isLetter(lastChar) || Character.isDigit(lastChar)) {
                    builder.append(ch);
                    lastChar = ch;
                }
            }
            // 如果是分割符号，如逗号、开始括号等等
            else if (isSplits(ch) || isBracketsBeg(ch) || isBracketsEnd(ch)) {
                sentences.add(builder.toString().trim());
                builder.setLength(0);
                lastChar = 1;
            }
        }
        if (builder.length() > 0) {
            sentences.add(builder.toString().trim());
        }
        return sentences;
    }

    /**
     * 是否为分隔符
     * 
     * @param ch
     * @return
     */
    public static boolean isSplits(char ch) {
        for (char split : SPLITS) {
            if (ch == split) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否为空格
     * 
     * @param ch
     * @return
     */
    public static boolean whiteSpace(char ch) {
        return ch == WHITE_SPACE;
    }

    public static boolean isQuotation(char ch) {
        return ch == '\'' || ch == '"';
    }

    public static boolean isConnector(char ch) {
        return ch == '&' || ch == '-';
    }

    public static boolean isBracketsBeg(char ch) {
        for (char bracket : BRACKETS_BEG) {
            if (ch == bracket) {
                return true;
            }
        }
        return false;
    }

    public static boolean isBracketsEnd(char ch) {
        for (char bracket : BRACKETS_END) {
            if (ch == bracket) {
                return true;
            }
        }
        return false;
    }
}
