/* Copyright 2011-2013 Joseph Kendall-Morwick

     This file is part of SAI: The Structure Access Interface.

    jmorwick-javalib is free software: you can redistribute it and/or modify
    it under the terms of the Lesser GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    jmorwick-javalib is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    Lesser GNU General Public License for more details.

    You should have received a copy of the Lesser GNU General Public License
    along with jmorwick-javalib.  If not, see <http://www.gnu.org/licenses/>.

 */
package sai;

import info.kendall_morwick.funcles.Funcles;
import info.kendall_morwick.funcles.T2;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import com.google.common.collect.Maps;

import static info.kendall_morwick.funcles.Tuple.makeTuple;
import sai.comparison.heuristics.GraphMatchingHeuristic;
import sai.comparison.matching.MatchingGenerator;
import sai.retrieval.GraphRetriever;
import sai.test.graph.Index;
import sai.test.graph.Node;
import sai.db.DBInterface;
import sai.graph.Graph;
import sai.graph.GraphFactory;

//All the contents of this class/file need to be redistributed in the new SAI design



//DBInterface graveyard:


/*
public void setFeatureSetComparator(BinaryRelation<Set<? extends Feature>> fsc) {
    this.fsc = fsc;
}

public boolean featureSetsCompatible(
        Set<? extends Feature> t1s,
        Set<? extends Feature> t2s) {
    return Funcles.apply(fsc, t1s, t2s);
}

*/

//----------------------- Indexing -------------------------------------
//private Set<IndexGenerator> indexers = new HashSet<IndexGenerator>();
//private Set<IndexRetriever> retrievers = new HashSet<IndexRetriever>();

/** adds an indexer for this interface.  whenever 'indexGraph' is called on a
 * graph 'g', all indexers added through this method will have their 'indexGraph'
 * methods called on g, and the resulting indices will be saved to the database.
 *
 * @param ig
 *
public void addIndexer(IndexGenerator ig) {
    indexers.add(ig);
}


/** Adds an index retriever to this database.  Whenever findIndices is
 * called on a graph 'g', each retriever added through this method will
 * have its 'retrieveIndices' method called on g, and the resulting
 * indices will be returned together by findIndices.
 *
 * @param r
 *
public void addRetriever(IndexRetriever r) {
    retrievers.add(r);
}

/** uses registered Retrievers to find indices that should be related to
 * the query graph.  The query graph need not be saved into the database and
 * this method will not save the query graph to the database.  
 * @param g
 * @return
 *
public Set<Index> findIndices(Graph g) {
    Set<Index> indices = new HashSet<Index>();
    for (IndexRetriever r : retrievers) {
        indices.addAll(r.retrieveIndices(g));
    }
    return indices;
}


/** uses registerd Indexers to generate or locate indices which should be
 * associated with stored graph g.  Generated indices are saved to the
 * database.  
 * @param g
 *
public void indexGraph(Graph g) {
    if (g.getID() < 1) {
        return; //only index graphs saved to the DB
    }
    for (IndexGenerator ig : indexers) {
        for (Index i : ig.generateIndices(g)) {
            if (i.getID() < 1) {
                i.saveToDatabase();
            }
            addIndex(g, i);
        }
    }
}
*/






//----------------------------------------------------------------------------


/**  This class can be used to build a complete interface for retrieving
 * similar structures from a graph database.  In order to use this class,
 * a properly configured Database Interface is required, in addition to a
 * map generator implementation and a map heuristic for ranking the
 * retrieved graphs.
 *
 *
 * @version 0.2.0
 * @author Joseph Kendall-Morwick <jmorwick@indiana.edu>
 */
public class RetrievalInterface<G extends Graph> {
    private final DBInterface db;
    private final GraphRetriever<G> r;
    private final MatchingGenerator gen;
    private final GraphMatchingHeuristic h;
    private int lastRetrievedGraphID = -1;
    private int lastRetrievedIndexSetSize = -1;

    public RetrievalInterface(DBInterface db,
    		GraphFactory<G> gf,
            GraphRetriever<G> r,
            MatchingGenerator gen,
            GraphMatchingHeuristic h) {
        this.db = db;
        this.r =r;
        this.gen = gen;
        this.h = h;
    }

    public MatchingGenerator getMapGenerator() { return gen; }

    public GraphMatchingHeuristic getMapHeuristic() { return h; }

    /** returns the id of the last graph to be retrieved from this interface
     * 
     * @return
     */
    public int getLastRetrievedGraphID() {
        return lastRetrievedGraphID;
    }

    public GraphRetriever getRetriever() { return r; }

    /** returns an iterator which iterates through graphs similar to the query.
     *
     *
     * @param query the graph for which similar graphs are retrieved
     * @param directComparisonRetrievalSize The maximum number of graphs to perform an expensive pair-wise similarity test between
     * @return
     */
    public Iterator<Graph> retrieve(final Graph query,
            final int directComparisonRetrievalSize) {
        final Iterator<T2<Graph,Map<Node,Node>>> i =
                retrieveWithMap(query,directComparisonRetrievalSize);
            return new Iterator<Graph>() {

            public boolean hasNext() {
                return i.hasNext();
            }

            public Graph next() {
                Graph g =  i.next().a1();
                lastRetrievedGraphID = g.getSaiID();
                return g;
            }

            public void remove() {
                throw new UnsupportedOperationException("Not supported.");
            }
        };
    }

    /** performs the same operation as 'retrieve', but additionally includes
     * the maps formed between the query and target graphs used for ranking.
     *
     */
    public Iterator<T2<Graph,Map<Node,Node>>> retrieveWithMap(final Graph query,
            final int directComparisonRetrievalSize) {
        final Set<Index> indices = db.findIndices(query);
        lastRetrievedIndexSetSize = indices.size();
        final Iterator<Graph> ir = r.retrieve(indices);
        final Map<Graph, Double> utility = Maps.newHashMap();
        final Map<Graph,Map<Node,Node>> maps = Maps.newHashMap();
        final PriorityQueue<Graph> retrievalQueue =
                new PriorityQueue<Graph>(directComparisonRetrievalSize,
                new Comparator<Graph>() {

            public int compare(Graph o1, Graph o2) {
                if(!utility.containsKey(o1)) {
                    maps.put(o1, Funcles.apply(gen, query, o1));
                    utility.put(o1, h.apply(query, o1, maps.get(o1)));
                }
                if(!utility.containsKey(o2)) {
                    maps.put(o2, Funcles.apply(gen, query, o2));
                    utility.put(o2, h.apply(query, o2, maps.get(o2)));
                }
                return utility.get(o1) > utility.get(o2) ? -1 :
                    utility.get(o1) > utility.get(o2) ? 1 : 0;
            }
        });
        
        return new Iterator<T2<Graph,Map<Node,Node>>>() {

            public boolean hasNext() {
                return retrievalQueue.size() > 0 || ir.hasNext();
            }

            public T2<Graph,Map<Node,Node>> next() {
                while(ir.hasNext() &&
                        retrievalQueue.size() < directComparisonRetrievalSize)
                    retrievalQueue.add(ir.next());


                Graph g = retrievalQueue.remove();
                if(!maps.containsKey(g))
                    maps.put(g, gen.apply(makeTuple(query, g)));

                lastRetrievedGraphID = g.getSaiID();

                return makeTuple(g,maps.get(g));
            }

            public void remove() {
                throw new UnsupportedOperationException("Not supported.");
            }
        };
    }

    public DBInterface getDB() {
        return db;
    }
}