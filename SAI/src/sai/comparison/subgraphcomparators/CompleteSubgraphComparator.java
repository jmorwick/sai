/* Copyright 2011 Joseph Kendall-Morwick

     This file is part of SAI: The Structure Access Interface.

    SAI is free software: you can redistribute it and/or modify
    it under the terms of the Lesser GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    SAI is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    Lesser GNU General Public License for more details.

    You should have received a copy of the Lesser GNU General Public License
    along with jmorwick-javalib.  If not, see <http://www.gnu.org/licenses/>.

 */

package org.dataandsearch.sai.comparison.subgraphcomparators;

import info.kendallmorwick.util.Map;
import info.kendallmorwick.util.MultiMap;
import info.kendallmorwick.util.function.Function;
import info.kendallmorwick.util.function.ResultContainer;
import java.math.BigInteger;
import org.dataandsearch.sai.DBInterface;
import org.dataandsearch.sai.Feature;
import org.dataandsearch.sai.Graph;
import org.dataandsearch.sai.Node;
import org.dataandsearch.sai.comparison.SubgraphComparator;
import org.dataandsearch.sai.comparison.Util;

/**
 * This class may not be included in 1.0; I'm still considering its inclusion
 *
 * @version 0.2.0
 * @author Joseph Kendall-Morwick
 */
public class CompleteSubgraphComparator extends SubgraphComparator {

    public static boolean compare(DBInterface db, Graph g1, Graph g2, 
            Class<? extends Feature> ... features) {
        
        CompleteSubgraphComparator csc = new CompleteSubgraphComparator(db, g1, g2, features);
        csc.waitTillDone();
        return csc.isSubgraph();
    }

    

    private boolean checkedGraphFeatures = false;

    public CompleteSubgraphComparator(final DBInterface db,
            final Graph g1,
            final Graph g2,
            final Class<? extends Feature> ... features) {
        this(db, g1, g2, -1, features);
    }

    public CompleteSubgraphComparator(final DBInterface db,
            final Graph g1,
            final Graph g2,
            final long time,
            final Class<? extends Feature> ... features) {
        super(db, g1, g2, time, getBody(db, features));
    }

    public static Function<Judgement, SubgraphComparator> getBody(
            final DBInterface db,
            final Class<? extends Feature> ... featureTypes) {
        return new Function<Judgement, SubgraphComparator>() {

            @Override
            public Judgement implementation(SubgraphComparator sgc) {
                ResultContainer<Judgement> rc = this.getMyResultContainer();
                BigInteger numMappings = Util.getNumberOfCompleteMappings(
                        sgc.getPotentialSubgraph(),
                        sgc.getPotentialSupergraph(),
                        featureTypes);
                MultiMap<Node, Node> possibilities = Util.nodeCompatibility(
                        sgc.getPotentialSubgraph(),
                        sgc.getPotentialSupergraph(),
                        featureTypes);
                BigInteger currentMapping = BigInteger.ZERO;


                if(!db.FeatureSetsCompatible(
                        sgc.getPotentialSubgraph().getFeatures(),
                        sgc.getPotentialSupergraph().getFeatures(),
                        featureTypes)) {
                    return Judgement.IS_NOT_SUBGRAPH;
                }


                while(currentMapping.compareTo(numMappings) < 0) {
                    Map<Node,Node> map = possibilities.getIthCompleteMapping(currentMapping);
                    if(map.size() < possibilities.size()) {
                        return Judgement.IS_NOT_SUBGRAPH;
                    } else if(Util.matchedEdges(
                            sgc.getPotentialSubgraph(),
                        sgc.getPotentialSupergraph(),
                        map, Util.completeEdgeMatchCounter, featureTypes) ==
                        sgc.getPotentialSubgraph().edgeSet().size() &&
                        map.size() == sgc.getPotentialSubgraph().vertexSet().size()) {
                        return Judgement.IS_SUBGRAPH;
                    }

                    if(rc != null) {
                        if(rc.askedToDie()) break;
                    }
                    currentMapping = currentMapping.add(BigInteger.ONE);
                }

                if(currentMapping.equals(numMappings)) {
                    return Judgement.IS_NOT_SUBGRAPH;
                }
                return Judgement.UNKNOWN;
            }
        };
    }


}