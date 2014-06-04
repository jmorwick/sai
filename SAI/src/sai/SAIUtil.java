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

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import sai.graph.Feature;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * @version 0.2.0
 * @author Joseph Kendall-Morwick
 */

public class SAIUtil {	

    /** creates a collection from an iterator to allow for-each over iterators. Only
     * the isEmpty() and iterator() methods are supported, so this is mostly only 
     * intended to shorten for-each loop syntax */
    public static <A> Collection<A> iteratorToCollection(final Iterator<A> i) {
        return new Collection<A>() {

            public int size() {
                throw new UnsupportedOperationException("Not supported.");
            }

            public boolean isEmpty() {
                return i.hasNext();
            }

            public boolean contains(Object o) {
                throw new UnsupportedOperationException("Not supported.");
            }

            public Iterator<A> iterator() {
                return i;
            }

            public Object[] toArray() {
                throw new UnsupportedOperationException("Not supported.");
            }

            public <T> T[] toArray(T[] a) {
                throw new UnsupportedOperationException("Not supported.");
            }

            public boolean add(A e) {
                throw new UnsupportedOperationException("Not supported.");
            }

            public boolean remove(Object o) {
                throw new UnsupportedOperationException("Not supported.");
            }

            public boolean containsAll(Collection<?> c) {
                throw new UnsupportedOperationException("Not supported.");
            }

            public boolean addAll(Collection<? extends A> c) {
                throw new UnsupportedOperationException("Not supported.");
            }

            public boolean removeAll(Collection<?> c) {
                throw new UnsupportedOperationException("Not supported.");
            }

            public boolean retainAll(Collection<?> c) {
                throw new UnsupportedOperationException("Not supported.");
            }

            public void clear() {
                throw new UnsupportedOperationException("Not supported.");
            }

        };
    }


	/** returns a predicate which identifies features whose names match 'names'.
	 * 
	 * @param names the names of features to keep
	 * @return a predicate which identifies features whose names match 'names'.
	 */
    public static Predicate<Feature> featureWhiteListFilter(final Set<String> names) {
    	return new Predicate<Feature>() {
			@Override
			public boolean apply(Feature f) {
				return (names.contains(f.getName()));		
			}
    		
    	};
    }
    
	/** returns a predicate which identifies features whose names match 'names'.
	 * 
	 * @param names the names of features to keep
	 * @return a predicate which identifies features whose names match 'names'.
	 */
    public static Predicate<Feature> featureWhiteListFilter(String ... names) {
    	Set<String> sNames = Sets.newHashSet();
    	for(String name : names) sNames.add(name);
    	return featureWhiteListFilter(sNames);
    }
	    	
    
    /** returns only those features whose names match the specified names
     * 
     * @param features features to select from
     * @param names names of features to retain
     * @return only features whose names match
     */
    public static Set<Feature> retainOnly(Set<Feature> features, 
    		Set<String> names) {
    	return Sets.filter(features, featureWhiteListFilter(names));
    }  
    

    /** returns only those features whose names match the specified names
     * 
     * @param features features to select from
     * @param names names of features to retain
     * @return only features whose names match
     */
    public static Set<Feature> retainOnly(Set<Feature> features, 
    		String ... names) {
    	return Sets.filter(features, featureWhiteListFilter(names));
    }

}