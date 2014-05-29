package sai.graph;

import java.util.Set;

import sai.db.DBInterface;


public class Graphs {

	private static final String INDEX_FEATURE_NAME = "index graph";
	private static final String DIRECTED_GRAPH_FEATURE_NAME = "directed graph";

	public static <G extends Graph> G copyWithoutEdge(Graph g, GraphFactory<G> gf, int edgeID) {
        MutableGraph t = new MutableGraph(g);
        if(t.getEdgeIDs().contains(edgeID))
        	t.removeEdge(edgeID);
        return gf.copy(t);
	}
	
	public static <G extends Graph> G copyWithoutNode(Graph g, GraphFactory<G> gf, int nodeID) {
        MutableGraph t = new MutableGraph(g);
        if(t.getNodeIDs().contains(nodeID))
        	t.removeNode(nodeID);
        return gf.copy(t);
	}
	

    /** returns the feature of the given feature-class associated with this feature set.
     * If more than one such feature exists, an arbitrary selection is returned.
     * If no such feature exists, null is returned.
     * @param features the set of features to search
     * @return the feature with the matching name (if any)
     */
    public static Feature getFeature(Set<Feature> features, String featureName) {
        for(Feature f : features) {
            if(f.getName().equals(featureName))
                return f;
        }
        return null;
    }
    

	public static final Feature getIndexTag(DBInterface db) {
		return db.getFeature(INDEX_FEATURE_NAME, "true");
	}
	
	public static final Feature getDirectedTag(DBInterface db) {
		return db.getFeature(DIRECTED_GRAPH_FEATURE_NAME, "true");
	}
	
	public static boolean isIndex(Graph g) {
		for(Feature f : g.getFeatures()) 
			if(f.getName().equals(INDEX_FEATURE_NAME) && 
					f.getValue().equals("true"))
				return true;
		return false;
	}
	
	public static boolean isDirected(Graph g) {
		for(Feature f : g.getFeatures()) 
			if(f.getName().equals(INDEX_FEATURE_NAME) && 
					f.getValue().equals("true"))
				return true;
		return false;
	}
}
