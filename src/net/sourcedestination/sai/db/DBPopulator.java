package net.sourcedestination.sai.db;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import jdk.nashorn.api.scripting.URLReader;
import net.sourcedestination.sai.db.graph.Graph;
import net.sourcedestination.sai.db.graph.GraphDeserializer;
import net.sourcedestination.sai.util.Task;
import org.json.JSONArray;
import org.json.JSONTokener;

import static java.time.LocalDateTime.now;
import static java.time.format.DateTimeFormatter.ofPattern;

public abstract class DBPopulator implements Function<DBInterface,Task> {

	private static Logger logger = Logger.getLogger(DBPopulator.class.getCanonicalName());

	private static AtomicInteger nextId = new AtomicInteger(0);

	public abstract Stream<Graph> getGraphStream();
	public abstract int getNumGraphs();



	@Override
	public Task apply(DBInterface db) {
		Class dbpopClass = this.getClass();
		return new Task() {
			private String name = dbpopClass.getSimpleName() + nextId.incrementAndGet();
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
			public String getTaskName() {
				return name;
			}

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




    public static DBPopulator getSaiJsonPopulator(URL jsonFile) {
	    var parsedFile = new JSONArray(new JSONTokener(new URLReader(jsonFile)));
	    return new DBPopulator() {
            @Override
            public Stream<Graph> getGraphStream() {
                return IntStream.range(0, parsedFile.length())
                        .mapToObj(parsedFile::getJSONObject)
                        .map(GraphDeserializer::saiJsonDecode);
            }

            @Override
            public int getNumGraphs() {
                return parsedFile.length();
            }
        };
    }

    public static DBPopulator getSaiJsonPopulator(File jsonFile) {
        try {
            var parsedFile = new JSONArray(new JSONTokener(new FileReader(jsonFile)));
            return new DBPopulator() {
                @Override
                public Stream<Graph> getGraphStream() {
                    return IntStream.range(0, parsedFile.length())
                            .mapToObj(parsedFile::getJSONObject)
                            .map(GraphDeserializer::saiJsonDecode);
                }

                @Override
                public int getNumGraphs() {
                    return parsedFile.length();
                }
            };
        } catch(IOException e) { throw new RuntimeException(e); }
    }
}
