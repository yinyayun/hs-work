package org.yinyayun.analyzer;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.yinyayun.analyzer.common.PreDecode;
import org.yinyayun.analyzer.detect.TermDetectByMM;
import org.yinyayun.analyzer.detect.TermDetectionA;
import org.yinyayun.analyzer.detect.Terms;
import org.yinyayun.analyzer.dic.DictNode;
import org.yinyayun.analyzer.dic.Dictionary;
import org.yinyayun.analyzer.dic.Dictionary.WordType;

/**
 * @author yinyayun
 */
public class AnalyzerTest {
    private String paragraph = "Dooney & Bourke Denver Broncos Dooney & Bourke Triple-Zip Crossbody Bag";

    /**
     * 预处理测试
     */
    @Test
    public void testDecode() {
        System.out.println("测试预处理：");
        List<String> sents = PreDecode.predecode("backpack; tote");
        for (String sent : sents) {
            System.out.println(">>>>>>>>>>" + sent);
        }
    }

    /**
     * 词典测试
     */
    @Test
    public void testDictSeek() {
        Dictionary dictionary = Dictionary.getDict();
        try {
            // 品牌词
            DictNode node = dictionary.findNode("nat & nin");
            Assert.assertEquals("nat & nin".equals(node.getSource()), true);
            if (node.getTypes() == null) {
                Assert.fail("ERROR");
            }
            else {
                Assert.assertEquals(node.getTypes().contains(WordType.BRAND), true);
            }
            //
            DictNode colorNode = dictionary.findNode("blossom pink");
            Assert.assertEquals("blossom pink".equals(colorNode.getSource()), true);
            if (colorNode.getTypes() == null) {
                Assert.fail("ERROR");
            }
            else {
                Assert.assertEquals(colorNode.getTypes().contains(WordType.COLOR), true);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    /**
     * 测试分词
     */
    @Test
    public void testAnalyzer() {
        System.out.println("测试分词：");
        TermDetectionA termDetection = new TermDetectByMM();
        String[] paragraphs = {"Tommy Hilfiger Double-Sided Saffiano Faux Leather Norah Tote", //
                "INC International Concepts Baker Satchel, Only at Macy's", //
                "Handbag", //
                "  MICHAEL Michael Kors Jet Set Travel Large Travel Continental Wallet", //
                " Patricia Nash Chania Crossbody", //
                "  Large Bryant Park Satchel", //
                " Crossgrain Turnlock Tote", //
                " Patricia Nash Signature Map Torri Crossbody"};//
        for (String paragraph : paragraphs) {
            System.out.println(String.format("待分词文本：[%s]", paragraph));
            List<Terms> terms = termDetection.detectTerm(paragraph);
            System.out.println(">>>>>>>>>>");
            for (Terms term : terms) {
                System.out.println(">>>>>>>>>>" + term);
            }
            System.out.println(">>>>>>>>>>");
        }

    }
}