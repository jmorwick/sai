package net.sourcedestination.sai.analysis.metrics;

import Jama.EigenvalueDecomposition;
import net.sourcedestination.sai.analysis.GraphMetric;
import net.sourcedestination.sai.db.graph.Graph;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class EigenvalueCount implements GraphMetric {
    @Override
    public Double apply(Graph g) {
        var matrix = g.getAdjacencyMatrix();
        var eigenGen = new EigenvalueDecomposition(matrix);
        var eigenValues = new HashSet<Double>();
        for(double eigenvalue : eigenGen.getRealEigenvalues())
            eigenValues.add(eigenvalue);
        return ((double)eigenValues.size())/g.getNodeIDs().count();
    }

}
