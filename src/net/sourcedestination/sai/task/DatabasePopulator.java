package net.sourcedestination.sai.task;

import java.util.function.Supplier;

import net.sourcedestination.sai.db.DBInterface;
import net.sourcedestination.sai.graph.Graph;
import net.sourcedestination.sai.reporting.DBListener;
import net.sourcedestination.sai.reporting.Log;
import static net.sourcedestination.funcles.tuple.Tuple.makeTuple;

public class DatabasePopulator implements TaskInitiator {
	
	private Supplier<Graph> gen;
	private int numGraphs;
	private DBInterface db;
	

	public DatabasePopulator(DBInterface db, Supplier<Graph> gen, int numGraphs) {
		this.gen = gen;
		this.numGraphs = numGraphs;
		this.db = db;
	}


	@Override
	public Task startTask() {
		return new Task() {
			private int i=0;

			@Override
			public Log call() throws Exception {
				Log log = new Log("Populate Database", 
						makeTuple("generator", gen.toString()), 
						makeTuple("numGraphs", ""+numGraphs));
				DBListener dbl = new DBListener(db, log);
				for(i=0; i<numGraphs; i++) {
					dbl.addGraph(gen.get());
				}
				return log;
			}
		};
	}
	
}
