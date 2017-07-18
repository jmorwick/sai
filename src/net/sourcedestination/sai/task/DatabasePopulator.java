package net.sourcedestination.sai.task;

import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import net.sourcedestination.sai.db.DBInterface;
import net.sourcedestination.sai.graph.Graph;
import net.sourcedestination.sai.reporting.DBListener;
import net.sourcedestination.sai.reporting.Log;
import static net.sourcedestination.funcles.tuple.Tuple.makeTuple;

public class DatabasePopulator implements Function<DBInterface,Task<Log>> {
	
	private final Stream<? extends Graph> gstream;
	private final int numGraphs;
	
	public DatabasePopulator(Stream<? extends Graph> gstream) {
		this(gstream, -1);
	}

	public DatabasePopulator(Stream<? extends Graph> gstream, int numGraphs) {
		this.gstream = gstream;
		this.numGraphs = numGraphs;
	}


	@Override
	public Task<Log> apply(DBInterface db) {
		Class dbpopClass = this.getClass();
		return new Task<Log>() {
			private int i=0;
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
				finished = true;
				return log;
			}
			
			public void cancel() {
				cancel = true;
			}
			
			public boolean running() {
				return !finished;
			}
			
			public double getPercentageDone() {
				return numGraphs > 0 ? (double)i/(double)numGraphs :
						!finished ? 0 : 1;
			}
			
			public int getProgressUnits() {
				return i;
			}
		};
	}
	
}
