package sai.indexing;

import java.util.Set;
import java.util.regex.Pattern;

import com.google.common.collect.Sets;

import sai.SAIUtil;
import sai.graph.Feature;
import sai.graph.Graph;

public class Path1IndexGenerator implements FeatureIndexGenerator {
    public static final String PATH1NAME = "path1-index";
    
    private final String[] featureNames;
    
    public Path1IndexGenerator(String ... featureNames) {
    	this.featureNames = featureNames;
    }

    public static Set<Feature> generatePath1IndexFeatures(Graph s, String ... featureNames) {
    	Set<Feature> ret = Sets.newHashSet();
    	for(int e : s.getEdgeIDs()) {
    		ret.addAll(generatePath1IndexFeature(s, e, featureNames));
    	}
    	return ret;
    }
    
    private static Pattern replacementPattern = Pattern.compile("(,|:|\\\\)");
    public static String encodeValue(String v) {
    	return replacementPattern.matcher(v).replaceAll("\\\\$1");
    }
    private static Set<Feature> generatePath1IndexFeature(Graph s, int e, String ... featureNames) {
    	    Set<Feature> ret = Sets.newHashSet();
            Set<Feature> fromNodeFeatures = Sets.newHashSet();
            Set<Feature> toNodeFeatures = Sets.newHashSet();
            Set<Feature> edgeFeatures = Sets.newHashSet();
            edgeFeatures.addAll(SAIUtil.retainOnly(s.getEdgeFeatures(e), featureNames));
            if(edgeFeatures.size() == 0) edgeFeatures.add(null); //make links without edge features
            fromNodeFeatures.addAll(
                    SAIUtil.retainOnly(s.getNodeFeatures(s.getEdgeSourceNodeID(e)),
                    featureNames));
            toNodeFeatures.addAll(
                    SAIUtil.retainOnly(s.getNodeFeatures(s.getEdgeTargetNodeID(e)),
                    featureNames));
            for(Feature n1f : fromNodeFeatures) {
                for(Feature n2f : toNodeFeatures) {
                    for(Feature ef : edgeFeatures) {
                    	ret.add(new Feature(PATH1NAME, 
                    			encodeValue(n1f.getName()) + "," + 
                            	encodeValue(n1f.getValue()) +
                            	(ef != null ? ":" + 
                    			encodeValue(ef.getName()) + "," + 
                            	encodeValue(ef.getValue()) : "") + ":" + 
                    			encodeValue(n2f.getName()) + "," + 
                            	encodeValue(n2f.getValue())));
                    }
                }
            }
            return ret;
    }

	@Override
	public Set<Feature> apply(Graph g) {
		return generatePath1IndexFeatures(g, featureNames);
	}

}
