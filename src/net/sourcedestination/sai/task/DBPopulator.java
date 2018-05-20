package net.sourcedestination.sai.task;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Stream;

import net.sourcedestination.sai.db.DBInterface;
import net.sourcedestination.sai.graph.Graph;

public abstract class DBPopulator implements Function<DBInterface,Task> {

	public abstract Stream<Graph> getGraphStream();
	public abstract int getNumGraphs();

	@Override
	public Task apply(DBInterface db) {
		Class dbpopClass = this.getClass();
		return new Task() {
			private boolean cancel = false;
			private boolean finished = false;
			private AtomicInteger graphsProcessed = new AtomicInteger(0);

			@Override
			public Object get() {
				getGraphStream().filter(g -> {
					db.addGraph(g);
					graphsProcessed.incrementAndGet();
					return cancel; // if this is true, the stream will exit
				}).findFirst();
				return graphsProcessed.get();
			}

			@Override
			public String getTaskName() { return dbpopClass.getCanonicalName(); }

			@Override
			public void cancel() {
				cancel = true;
			}

			@Override
			public int getProgressUnits() {
				return graphsProcessed.get();
			}

			@Override
			public int getTotalProgressUnits() { return getNumGraphs(); }
		};
	}
	
}
