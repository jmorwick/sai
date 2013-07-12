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

package sai.comparison.mapcomparators;


import sai.DBInterface;
import sai.Graph;
import sai.Node;
import sai.comparison.MapComparator;
import sai.comparison.MapComparator.Judgement;
import sai.comparison.MapHeuristic;

/**
 * @version 2.0.0
 * @author Joseph Kendall-Morwick
 */
@Deprecated public class UtilityBasedComparator extends MapComparator {

    private final MapHeuristic h;

    public UtilityBasedComparator(DBInterface db,
            Graph s1, Graph s2, Map<Node,Node> m1, Map<Node,Node> m2,
            final MapHeuristic h) {
        super(db, s1, s2, m1, m2,
                new Function<Judgement, T5<DBInterface, Graph, Graph,
                    Map<Node,Node>, Map<Node,Node>>>() {

            @Override
            public Judgement implementation(T5<DBInterface, Graph, Graph, Map<Node, Node>, Map<Node, Node>> args) {
                double v1 = h.getValue(args.a2(), args.a3(), args.a4());
                double v2 = h.getValue(args.a2(), args.a3(), args.a5());
                if( v1 > v2 ) return Judgement.FIRST_IS_BETTER;
                if( v2 > v1 ) return Judgement.SECOND_IS_BETTER;
                return Judgement.EQUIVALENT;
            }
        });
        this.h = h;
    }


}