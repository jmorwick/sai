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

package sai.maintenance;

import info.kendall_morwick.funcles.BinaryRelation;
import info.kendall_morwick.funcles.Funcles;
import info.kendall_morwick.funcles.Pair;
import info.kendall_morwick.funcles.T2;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;

import sai.db.DBInterface;
import sai.graph.Graph;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;

import static info.kendall_morwick.funcles.Tuple.makeTuple;

/**
 * Updates compatibility relationships between indices to assist in hierarchical 
 * index retrieval and index consolidation.
 *
 * @version 0.2.0
 * @author Joseph Kendall-Morwick
 */
public class IndexCompatabilityChecker 
implements Supplier<List<T2<Integer,Integer>>> {

	private List<BinaryRelation<Graph>> compatibilityCheckers = Lists.newArrayList();
	private DBInterface db;
	private int numThreads;
	private final long maxTime;
	private List<ListenableFuture<Boolean>>
	activeComparisons = Lists.newArrayList();
	private final Object LOCK = new Object();

	public IndexCompatabilityChecker(DBInterface db, long maxTime,
			int numThreads, 
			BinaryRelation<Graph> ... comparatorFactories) {
		this.numThreads = numThreads;
		this.maxTime = maxTime;
		this.db = db;

		if(comparatorFactories.length == 0) throw new IllegalArgumentException("Need at least one comparator");
		for(BinaryRelation<Graph> r : comparatorFactories) 
			this.compatibilityCheckers.add(r);
	}

	@Override
	public List<T2<Integer,Integer>> get() {
		List<T2<Integer,Integer>> results = Lists.newArrayList();
		Iterator<Integer> progress1 = db.getIndexIDIterator();

		while(progress1.hasNext()) {
			int i1 = progress1.next();
			Iterator<Integer> progress2 = db.getIndexIDIterator();
			while(progress2.hasNext()) {
				int i2 = progress1.next();
				if(i2 == i1) continue;

				boolean foundAnswer = false; //set to true when an answer for this pair is found
				for(BinaryRelation<Graph> r : compatibilityCheckers) {
					if(foundAnswer) continue;

					//start new thread
					synchronized(LOCK) { //prevent thread from finishing and signaling before we wait
						ListenableFuture<Boolean> pt = 
								applyInBackground(r, i1, i2);
						pt.suggestProcessingTime(maxTime);
						pt.wakeUpWhenDone(LOCK);
						activeComparisons.add(pt);

						//wait for one to finish if to many are running
						if(activeComparisons.size() == numThreads)
							try {
								LOCK.wait();
							} catch (InterruptedException e) {
								//TODO: ignore here?
							}
					}
					//process completed threads
					Iterator<ListenableFuture<Boolean>> i = activeComparisons.iterator();
					List<T2<Integer,Integer>> haltAll = Lists.newArrayList();
					while(i.hasNext()) {
						ListenableFuture<Boolean> pt = i.next();
						if(!pt.isAlive()) {
							if(pt.getResult() != null) {
								i.remove();

								//stop all other running checkers for this pair
								if(pt.getInput().a1().getSaiID() == i1.getSaiID() &&
										pt.getInput().a2().getSaiID() == i2.getSaiID())
									foundAnswer = true;
								haltAll.add(makeTuple(i1,i2));

								//if compatible, record this compatibility
								if(pt.getResult())
									db.indexedBy(i1, i2);  // TODO: right order?
							}
						} else {
							if(pt.getRunTime() > maxTime)
								pt.kill();
						}
					}
					for(ListenableFuture<Boolean> pt : activeComparisons) {
						if(haltAll.contains(pt.getInput()))
							pt.kill();
					}
				}
			}
		}

		return results;
	}

	private ListenableFuture<Boolean> applyInBackground(
			final BinaryRelation<Integer> r, final int i1, final int i2) {
		return ListenableFutureTask.create(new Callable<Boolean>() {

			@Override
			public Boolean call() throws Exception {
				return r.apply(Pair.makePair(i1, i2));
			}
			
		});
	}
}