package net.sourcedestination.sai.comparison.distance;

import net.sourcedestination.funcles.function.Function2;
import net.sourcedestination.sai.graph.Graph;

/**
 * Created by jmorwick on 7/2/17.
 */
@FunctionalInterface
public interface GraphDistance<G extends Graph> extends Function2<G,G,Double> {

    /** a method that can be referenced as a comparator for graphs */
    public default int compare(G g1, G g2) {
        //create an integer from the [-1,1] value below for comparisons
        double result = apply(g1,g2);
        if(result < 0) return (int)result - 1;
        if(result > 0) return (int)result + 1;
        return 0;
    }
}
