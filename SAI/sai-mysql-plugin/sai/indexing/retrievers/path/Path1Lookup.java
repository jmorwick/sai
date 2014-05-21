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

package sai.indexing.retrievers.path;

import java.util.Set;

import info.kendall_morwick.funcles.BinaryRelation;
import info.kendall_morwick.funcles.T2;

import com.google.common.base.Function;
import com.google.common.collect.Sets;

import sai.graph.jgrapht.Feature;
import sai.graph.jgrapht.Graph;
import sai.indexing.generators.path.Path1;
import sai.DBInterface;
import sai.indexing.Index;
import sai.indexing.IndexGenerator;
import sai.indexing.IndexRetriever;

/**
 * Similar to the Path1 generator but only generates an index if it does not
 * already exist in the database.  This should be prefered over the Path1
 * generator, since the lookup is relatively inexpensive.  
 *
 * @version 0.2.0
 * @author Joseph Kendall-Morwick
 */
public class Path1Lookup implements IndexGenerator {
    private IndexRetriever retriever;
    private Function<T2<Graph, Graph>,BinaryRelation<Graph>> compFactory;
    private final Path1 gen;


    public Path1Lookup(String ... featureTypes
            ) {
        gen = new Path1(featureTypes);
        retriever = new Path1Retriever(db, featureTypes);
    }
    

    @Override
    public Set<Index> generateIndices(Graph s) {
        Set<Index> indices = gen.generateIndices(s);
        T2<Set<Index>,Set<Index>> t = 
                Path1Retriever.findOriginalLinkIndices(getDB(), indices);
        return Sets.union(t.a1(), t.a2());
    }


}