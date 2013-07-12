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

package org.dataandsearch.sai.indexing.generators.path;

import org.dataandsearch.sai.indexing.generators.path.Path1;
import info.kendallmorwick.util.Set;
import info.kendallmorwick.util.function.Function;
import info.kendallmorwick.util.tuple.T2;
import org.dataandsearch.sai.DBInterface;
import org.dataandsearch.sai.Feature;
import org.dataandsearch.sai.Graph;
import org.dataandsearch.sai.comparison.SubgraphComparator;
import org.dataandsearch.sai.indexing.Index;
import org.dataandsearch.sai.indexing.IndexGenerator;
import org.dataandsearch.sai.indexing.IndexRetriever;
import org.dataandsearch.sai.indexing.retrievers.Path1Retriever;

/**
 * Similar to the Path1 generator but only generates an index if it does not
 * already exist in the database.  This should be prefered over the Path1
 * generator, since the lookup is relatively inexpensive.  
 *
 * @version 0.2.0
 * @author Joseph Kendall-Morwick
 */
public class Path1Lookup extends IndexGenerator {
    private IndexRetriever retriever;
    private Function<SubgraphComparator, T2<Graph, Graph>> compFactory;
    private final Path1 gen;


    public Path1Lookup(DBInterface db,
            Class<? extends Feature> ... featureTypes
            ) {
        super(db);
        gen = new Path1(db, featureTypes);
        retriever = new Path1Retriever(db, featureTypes);
    }
    

    @Override
    public Set<Index> generateIndices(Graph s) {
        Set<Index> indices = gen.generateIndices(s);
        T2<Set<Index>,Set<Index>> t = 
                Path1Retriever.findOriginalLinkIndices(getDB(), indices);
        return t.a1().union(t.a2());
    }


}