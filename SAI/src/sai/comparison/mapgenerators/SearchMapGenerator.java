/* Copyright 2011-2013 Joseph Kendall-Morwick

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

package sai.comparison.mapgenerators;

import sai.Graph;
import sai.Node;
import sai.comparison.MapGenerator;
import sai.comparison.mapgenerators.search.SearchQueue;

/**
 * Generates a mapping between two graphs by BFS in a map-space. 
 *
 * @version 2.0.0
 * @author Joseph Kendall-Morwick
 */
public class SearchMapGenerator extends MapGenerator {

    private SearchQueue searchQueue;
    private final long time;

    public SearchMapGenerator(SearchQueue q) {
        this(q, -1);
    }

    public SearchMapGenerator(SearchQueue q, long time) {
        this.time = time;
        searchQueue = q;
    }

    public SearchQueue getQueue() { return searchQueue; }


    public  Map<Node, Node> findBestSubgraphMapping(Graph a, Graph b) {
        searchQueue.seed(a, b);
        long startTime = System.currentTimeMillis();
        while(!searchQueue.empty() && 
                (System.currentTimeMillis() - startTime < time || time == -1)) {
            searchQueue.expand();
        }
        return searchQueue.getBestMap();
    }
    
}