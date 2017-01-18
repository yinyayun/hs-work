package org.yinyayun;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yinyayun.common.PropertiesUtils;
import org.yinyayun.common.TagUtils;
import org.yinyayun.search.SearchForHtable;

/**
 * 给定特征文件，对产品进行打标签
 * 
 * @author yinyayun
 */
public class TagMatcherMain {
    public final static Logger logger = LoggerFactory.getLogger(TagMatcherMain.class);
    public final static String sourcePath = "data/fea.txt";
    public final static String XXXX = " xxxxx ";

    public static void main(String[] args) throws IOException {
        String indexPath = PropertiesUtils.getPropertiesUtils().getIndexPath();
        String resultPath = PropertiesUtils.getPropertiesUtils().getTagResultPath();
        TagMatcherMain tag = new TagMatcherMain();
        tag.tag(indexPath, resultPath);
    }

    private void tag(String indexPath, String resultSavePath) {
        Map<String, List<String>> rets = new HashMap<String, List<String>>();
        SearchForHtable searchForHtable = null;
        try {
            searchForHtable = new SearchForHtable(indexPath);
            Map<String, FeatureDesc> featureDecs = loadFeatures();
            for (Entry<String, FeatureDesc> featureDec : featureDecs.entrySet()) {
                String feature = featureDec.getKey();
                FeatureDesc featureDesc = featureDec.getValue();
                System.out.println("FEA:" + feature);
                List<String> ids = searchForHtable.span_search(new String[]{"title", "dscrp"}, "itemid", feature,
                        featureDesc.span ? 3 : 0);
                rets.put(feature, ids);
            }
            Map<String, List<String>> prodID2FeatureIDS = new HashMap<String, List<String>>();
            for (Entry<String, List<String>> ret : rets.entrySet()) {
                String feature = ret.getKey();
                String featureId = featureDecs.get(feature).id;
                List<String> prodIds = ret.getValue();
                for (String prodId : prodIds) {
                    List<String> featureIds = prodID2FeatureIDS.get(prodId);
                    if (featureIds == null) {
                        featureIds = new ArrayList<String>();
                        prodID2FeatureIDS.put(prodId, featureIds);
                    }
                    featureIds.add(featureId);
                }
            }
            saveByJson(prodID2FeatureIDS, resultSavePath);
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        finally {
            TagUtils.close(searchForHtable);
        }

    }

    /**
     * 加载特征,其中存在业务逻辑，即出现 'xxxxx'表示该短语采用跨度查询
     * <p>
     * 另外再准备特征数据时，无需关心单复数，因为在创建索引/查询时，已经进行了单复数处理
     * 
     * @return
     * @throws IOException
     */
    private Map<String, FeatureDesc> loadFeatures() throws IOException {
        Map<String, FeatureDesc> featureDecs = new HashMap<String, FeatureDesc>();
        List<String> features = FileUtils.readLines(new File(sourcePath));
        for (String feature : features) {
            String[] parts = feature.split("##");
            boolean span = false;
            if (parts[1].indexOf(XXXX) != -1) {
                parts[1] = parts[1].replace(XXXX, " ");
                span = true;
            }
            featureDecs.put(parts[1], new FeatureDesc(span, parts[0]));
        }
        return featureDecs;
    }

    /**
     * 结果转存
     * 
     * @param ret
     * @throws IOException
     */
    private void saveByJson(Map<String, List<String>> ret, String resultSavePath) throws IOException {
        FileUtils.deleteQuietly(new File(resultSavePath));
        FileUtils.writeStringToFile(new File(resultSavePath), TagUtils.mapToString(ret), "UTF-8", false);
    }

    static class FeatureDesc {
        // 该特征是否进行跨度
        private boolean span;
        // 该特征对应的ID
        private String id;

        public FeatureDesc(boolean span, String id) {
            this.span = span;
            this.id = id;
        }
    }

}
