package net.sourcedestination.sai.db.indexing;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import net.sourcedestination.sai.db.graph.Feature;
import net.sourcedestination.sai.db.graph.Graph;

import com.google.common.collect.Sets;
public class Path1IndexGenerator implements IndexGenerator<String> {
    
    private final String[] featureNames;
    
    public Path1IndexGenerator(String ... featureNames) {
    	this.featureNames = featureNames;
    }

    public static Set<String> generatePath1IndexFeatures(Graph s, String ... featureNames) {
    	var ret = Sets.<String>newHashSet();
    	IndexGenerator.enumeratePaths(s, 1,1, featureNames)
                .map(ls -> generatePathKIndexFeature(ls))
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
	public Stream<String> apply(Graph g) {
        return generatePath1IndexFeatures(g, featureNames).stream();
	}

}
