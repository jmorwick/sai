package net.sourcedestination.sai.db;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Stream;

import net.sourcedestination.sai.db.graph.Graph;
import net.sourcedestination.sai.experiment.retrieval.Retriever;
import net.sourcedestination.sai.util.Task;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public abstract class DBPopulator implements Function<DBInterface,Task> {

	static Logger logger = LogManager.getLogger(DBPopulator.class);


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
				logger.info("begining task " + getTaskName() + " populating " + db);
				getGraphStream().filter(g -> {
					logger.info("added graph #" + db.addGraph(g) + " to " + db);
					graphsProcessed.incrementAndGet();
					return cancel; // if this is true, the stream will exit
				}).findFirst();
				logger.info("finished task " + getTaskName() + " populating " + db);
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
