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


import java.util.Map;

import info.km.funcles.BinaryRelation;
import info.km.funcles.Funcles;
import info.km.funcles.T2;
import info.km.funcles.T5;
import sai.DBInterface;
import sai.Graph;
import sai.Node;
import sai.comparison.MapHeuristic;
import sai.comparison.mapgenerators.search.SearchState;

/**
 * @version 2.0.0
 * @author Joseph Kendall-Morwick
 */
@Deprecated public class UtilityBasedComparator implements BinaryRelation<GraphMapping> {

    private final MapHeuristic h;

    public UtilityBasedComparator(final MapHeuristic h) {
    	this.h = h;
    }
                @Override
            public boolean apply(T2<SearchState,SearchState> args) {
                double v1 = Funcles.apply(h, args.a1().);
                double v2 = h.getValue(args.a2(), args.a3(), args.a5());
                if( v1 >= v2 ) return true;
                if( v2 > v1 ) return false;
            }
        });
        this.h = h;
    }


}