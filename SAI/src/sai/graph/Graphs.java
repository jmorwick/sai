package sai.graph;

import java.util.Set;


public class Graphs {

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
    
}
