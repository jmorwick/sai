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

package sai.comparison.subgraphcomparators;

import info.km.funcles.BinaryRelation;
import info.km.funcles.Funcles;
import info.km.funcles.ProcessingThread;
import info.km.funcles.T2;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Multimap;

import sai.DBInterface;
import sai.Feature;
import sai.Graph;
import sai.Node;
import sai.comparison.ResultUnavailableException;
import sai.comparison.Util;

/**
 * This class may not be included in 1.0; I'm still considering its inclusion
 *
 * @version 0.2.0
 * @author Joseph Kendall-Morwick
 */
public class CompleteSubgraphComparator implements BinaryRelation<Graph> {

    public static boolean compare(DBInterface db, Graph g1, Graph g2, 
            Class<? extends Feature> ... features) {
        CompleteSubgraphComparator csc = new CompleteSubgraphComparator(db, features);
        return Funcles.apply(csc, g1, g2);
    }
    
    public static ProcessingThread<T2<Graph,Graph>,Boolean> compareInBackground(DBInterface db, Graph g1, Graph g2, 
            Class<? extends Feature> ... features) {
        CompleteSubgraphComparator csc = new CompleteSubgraphComparator(db, features);
        return Funcles.applyInBackground(csc, g1, g2);
    }

    private Set<Class<? extends Feature>> featureTypes = 
    		new HashSet<Class<? extends Feature>>();
	private DBInterface db;

    public CompleteSubgraphComparator(final DBInterface db,
            final Class<? extends Feature> ... featureTypes) {
    	this.db = db;
    	for(Class<? extends Feature> f : featureTypes) 
    		this.featureTypes.add(f);
    }

    
    @Override
    public boolean apply(T2<Graph,Graph> args)
    		throws ResultUnavailableException {
    	Graph sub = args.a1();
    	Graph sup = args.a2();
                BigInteger numMappings = Util.getNumberOfCompleteMappings(
                        sub,
                        sup,
                        featureTypes);
                Multimap<Node, Node> possibilities = Util.nodeCompatibility(
                        sub,
                        sup,
                        featureTypes);
                BigInteger currentMapping = BigInteger.ZERO;


                if(!db.FeatureSetsCompatible(
                        sub,
                        sup,
                        featureTypes)) {
                    return false;
                }


                while(currentMapping.compareTo(numMappings) < 0) {
                    Map<Node,Node> map = possibilities.getIthCompleteMapping(currentMapping);
                    if(map.size() < possibilities.size()) {
                        return false;
                    } else if(Util.matchedEdges(
                            sub,
                        sup,
                        map, Util.completeEdgeMatchCounter, featureTypes) ==
                        sub.edgeSet().size() &&
                        map.size() == sup.vertexSet().size()) {
                        return true;
                    }

                    if(Thread.currentThread() instanceof ProcessingThread) {
                    	ProcessingThread pc = (ProcessingThread)Thread.currentThread();
                        if(pc.askedToDie()) break;
                    }
                    currentMapping = currentMapping.add(BigInteger.ONE);
                }

                if(currentMapping.equals(numMappings)) {
                    return false;
                }
                throw new ResultUnavailableException();
            }
        };
    }


}