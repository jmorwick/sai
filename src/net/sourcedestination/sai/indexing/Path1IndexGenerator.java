package net.sourcedestination.sai.indexing;

import java.util.Arrays;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import net.sourcedestination.sai.graph.Feature;
import net.sourcedestination.sai.graph.Graph;

import com.google.common.collect.Sets;
//TODO: update to use streams, move in to FeatureIndexGenerator file
public class Path1IndexGenerator implements FeatureIndexGenerator {
    public static final String PATH1NAME = "path1-index";
    
    private final String[] featureNames;
    
    public Path1IndexGenerator(String ... featureNames) {
    	this.featureNames = featureNames;
    }

    public static Set<Feature> generatePath1IndexFeatures(Graph s, String ... featureNames) {
    	Set<Feature> ret = Sets.newHashSet();
    	s.getEdgeIDs().forEach(e -> 
    		ret.addAll(generatePath1IndexFeature(s, e, featureNames)));
    	
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
            Set<String> featureNamesSet = Arrays.stream(featureNames)
            		.collect(Collectors.toSet());
            s.getEdgeFeatures(e)
            		.filter(f -> featureNamesSet.contains(f.getName()))
            		.forEach(f -> edgeFeatures.add(f));
            if(edgeFeatures.size() == 0) edgeFeatures.add(null); //make links without edge features

            s.getNodeFeatures(s.getEdgeSourceNodeID(e))
    		.filter(f -> featureNamesSet.contains(f.getName()))
    		.forEach(f -> fromNodeFeatures.add(f));
            
            s.getNodeFeatures(s.getEdgeTargetNodeID(e))
    		.filter(f -> featureNamesSet.contains(f.getName()))
    		.forEach(f -> toNodeFeatures.add(f));
            
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
