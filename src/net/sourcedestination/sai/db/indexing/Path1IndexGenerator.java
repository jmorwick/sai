package net.sourcedestination.sai.db.indexing;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import net.sourcedestination.sai.db.graph.Feature;
import net.sourcedestination.sai.db.graph.Graph;

import com.google.common.collect.Sets;
//TODO: update to generalize to longer paths, perhaps move everything in to FeatureIndexGenerator
public class Path1IndexGenerator implements FeatureIndexGenerator {
    public static final String PATH1NAME = "path1-index";
    
    private final String[] featureNames;
    
    public Path1IndexGenerator(String ... featureNames) {
    	this.featureNames = featureNames;
    }

    public static Set<Feature> generatePath1IndexFeatures(Graph s, String ... featureNames) {
    	var ret = Sets.<Feature>newHashSet();
    	FeatureIndexGenerator.enumeratePaths(s, 1,1, featureNames)
                .map(ls -> new Feature(PATH1NAME, generatePathKIndexFeature(ls)))
                .forEach(ret::add);
    	return ret;
    }
    
    private static final Pattern replacementPattern = Pattern.compile("(,|:|\\\\)");
    public static String encodeValue(String v) {
    	return replacementPattern.matcher(v).replaceAll("\\\\$1");
    }
    private static String generatePathKIndexFeature(List<Feature> ls) {
        var ret = "";
        for(var f : ls) {
            ret += ret.length() == 0 ? "" : ", ";
            ret += "[" + encodeValue(f.getName()) + ":" + encodeValue(f.getValue()) + "]";
        }
        return ret;
    }

	@Override
	public Set<Feature> apply(Graph g) {
		return generatePath1IndexFeatures(g, featureNames);
	}

}
