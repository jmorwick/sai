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
package sai;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import sai.graph.Feature;

/**
 * @version 0.2.0
 * @author Joseph Kendall-Morwick
 */

public class SAIUtil {	

	/** returns a predicate which identifies features whose names match 'names'.
	 * 
	 * @param names the names of features to keep
	 * @return a predicate which identifies features whose names match 'names'.
	 */
    public static Predicate<Feature> featureWhiteListFilter(final Set<String> names) {
    	return f -> (names.contains(f.getName()));
    }
    
	/** returns a predicate which identifies features whose names match 'names'.
	 * 
	 * @param names the names of features to keep
	 * @return a predicate which identifies features whose names match 'names'.
	 */
    public static Predicate<Feature> featureWhiteListFilter(String ... names) {
    	return featureWhiteListFilter(Arrays.stream(names).collect(Collectors.toSet()));
    }
	    	
    
    /** returns only those features whose names match the specified names
     * 
     * @param features features to select from
     * @param names names of features to retain
     * @return only features whose names match
     */
    public static Set<Feature> retainOnly(Set<Feature> features, 
    		Set<String> names) {
		return features.stream().filter(featureWhiteListFilter(names)).collect(Collectors.toSet());
    }  
    

    /** returns only those features whose names match the specified names
     * 
     * @param features features to select from
     * @param names names of features to retain
     * @return only features whose names match
     */
    public static Set<Feature> retainOnly(Set<Feature> features, 
    		String ... names) {
    	return retainOnly(features, Arrays.stream(names).collect(Collectors.toSet()));
    }

}