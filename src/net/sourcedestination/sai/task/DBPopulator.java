package net.sourcedestination.sai.task;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Stream;

import net.sourcedestination.sai.db.DBInterface;
import net.sourcedestination.sai.graph.Graph;
import net.sourcedestination.sai.reporting.DBListener;
import net.sourcedestination.sai.reporting.Log;
import static net.sourcedestination.funcles.tuple.Tuple.makeTuple;

public abstract class DBPopulator implements Function<DBInterface,Task<Log>> {

	public abstract Stream<Graph> getGraphStream();
	public abstract int getNumGraphs();

	@Override
	public Task<Log> apply(DBInterface db) {
		Class dbpopClass = this.getClass();
		return new Task<Log>() {
			private boolean cancel = false;
			private boolean finished = false;
			private AtomicInteger graphsProcessed = new AtomicInteger(0);

			@Override
			public Log get() {
				Log log = new Log("Populate Database", 
						makeTuple("generator", dbpopClass.getCanonicalName()),
						makeTuple("numGraphs", ""+getNumGraphs()));
				DBListener dbl = new DBListener(db, log);
				getGraphStream().filter(g -> {
					dbl.addGraph(g);
					graphsProcessed.incrementAndGet();
					return cancel; // if this is true, the stream will exit
				}).findFirst();
				return log;
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
