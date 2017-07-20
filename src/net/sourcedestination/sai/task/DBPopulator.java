package net.sourcedestination.sai.task;

import java.util.function.Function;
import java.util.stream.Stream;

import net.sourcedestination.sai.db.DBInterface;
import net.sourcedestination.sai.graph.Graph;
import net.sourcedestination.sai.reporting.DBListener;
import net.sourcedestination.sai.reporting.Log;
import static net.sourcedestination.funcles.tuple.Tuple.makeTuple;

public class DBPopulator implements Function<DBInterface,Task<Log>> {
	
	private final Stream<? extends Graph> gstream;
	private final int numGraphs;
	
	public DBPopulator(Stream<? extends Graph> gstream) {
		this(gstream, -1);
	}

	public DBPopulator(Stream<? extends Graph> gstream, int numGraphs) {
		this.gstream = gstream;
		this.numGraphs = numGraphs;
	}


	@Override
	public Task<Log> apply(DBInterface db) {
		Class dbpopClass = this.getClass();
		return new Task<Log>() {
			private boolean cancel = false;
			private boolean finished = false;

			@Override
			public Log get() {
				Log log = new Log("Populate Database", 
						makeTuple("generator", dbpopClass.getCanonicalName()),
						makeTuple("numGraphs", ""+numGraphs));
				DBListener dbl = new DBListener(db, log);
				gstream.filter(g -> {
					dbl.addGraph(g);
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
				return db.getDatabaseSize();
			}

			@Override
			public int getTotalProgressUnits() { return numGraphs; }
		};
	}
	
}
