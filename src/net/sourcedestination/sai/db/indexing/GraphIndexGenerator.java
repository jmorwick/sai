package net.sourcedestination.sai.db.indexing;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.sourcedestination.sai.db.graph.Feature;
import net.sourcedestination.sai.db.graph.Graph;
import static net.sourcedestination.sai.util.StreamUtil.concatenateLists;

import com.google.common.base.Function;

@FunctionalInterface
public interface GraphIndexGenerator<I> extends Function<Graph,Stream<I>> {

    public static Stream<List<Feature>> enumeratePaths(Graph s,
                                                       int minLength,
                                                       int maxLength,
                                                       String ... featureNames) {
        return IntStream.range(minLength, maxLength+1)
                .mapToObj(length -> (Integer)length)
                .flatMap(length -> s.getNodeIDs()
                    .flatMap(nid -> enumeratePathsFromSource(s, nid, maxLength, featureNames)));
    }

    public static Stream<List<Feature>> enumeratePathsFromSource(Graph s,
                                                                 int nid,
                                                                 int length,
                                                                 String ... featureNames) {
        assert length >= 0;
        assert s != null;
        assert s.getNodeIDsSet().contains(nid);
        var featureNameSet = Sets.newHashSet(featureNames);

        if(length == 0) // stream singleton lists for each relevant feature
            return s.getNodeFeatures(nid)
                    .filter(f -> featureNameSet.contains(f.getName()))
                    .map(f -> Lists.newArrayList(f));

        // otherwise, find relevant features and call recursively on each edge
        return s.getNodeFeatures(nid)
                .filter(nf -> featureNameSet.contains(nf.getName())) // find relevant features
                .flatMap(nf -> s.getIncidentToEdges(nid) // for each edge...
                        .flatMap(eid -> enumeratePathsFromSource(s,
                                        s.getEdgeTargetNodeID(eid),
                                        length-1,
                                        featureNames) // find paths from this edge, 1 shorter
                                 .map(ls -> concatenateLists(Lists.newArrayList(nf), ls))));
    }



    public static Stream<List<Feature>> enumeratePathsWithEdgeFeatures(Graph s,
                                                       int minLength,
                                                       int maxLength,
                                                       String ... featureNames) {
        return IntStream.range(minLength, maxLength)
                .mapToObj(length -> (Integer)length)
                .flatMap(length -> s.getNodeIDs()
                        .flatMap(nid -> enumeratePathsWithEdgeFeaturesFromSource(s, nid, maxLength, featureNames)));
    }

    public static Stream<List<Feature>> enumeratePathsWithEdgeFeaturesFromSource(Graph s,
                                                                 int nid,
                                                                 int length,
                                                                 String ... featureNames) {
        assert length > 0;
        assert s != null;
        assert s.getNodeIDsSet().contains(nid);
        var featureNameSet = Sets.newHashSet(featureNames);

        if(length == 1) // stream singleton lists for each relevant feature
            return s.getNodeFeatures(nid)
                    .filter(f -> featureNameSet.contains(f.getName()))
                    .map(f -> Lists.newArrayList(f));

        // otherwise, find relevant features and call recursively on each edge
        return s.getNodeFeatures(nid)
                .filter(nf -> featureNameSet.contains(nf.getName())) // find relevant features
                .flatMap(nf -> s.getIncidentToEdges(nid) // for each edge...
                        .flatMap(eid -> s.getEdgeFeatures(eid)
                                // null concatenated on in case edge features aren't wanted
                                .filter(ef -> featureNameSet.contains(ef.getName())) // find relevant features
                                .flatMap(ef -> enumeratePathsWithEdgeFeaturesFromSource(s,
                                        s.getEdgeTargetNodeID(eid),
                                        length-1,
                                        featureNames) // find paths from this edge, 1 shorter
                                        .map(ls -> concatenateLists(Lists.newArrayList(nf,ef),ls))
                                )));
    }
}