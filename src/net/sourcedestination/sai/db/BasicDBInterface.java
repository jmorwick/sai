package net.sourcedestination.sai.db;

import java.util.*;
import java.util.stream.Stream;

import net.sourcedestination.sai.db.graph.Feature;
import net.sourcedestination.sai.db.graph.Graph;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

import static net.sourcedestination.sai.db.graph.GraphSerializer.canonicalId;

public class BasicDBInterface implements DBInterface  {
	private Map<Integer, Graph> db;
	private Multimap<String, Feature> featuresWithName;
	private Multimap<String, Integer> graphsWithFeatureName;
	private Multimap<Feature, Integer> graphsWithFeature;
	private String name;
	private final boolean USE_NATURAL_KEYS;
	private int lastGraphIdGenerated = 0;

	public BasicDBInterface(String name, boolean useNaturalKeys) {
		featuresWithName = HashMultimap.create();
		db = Maps.newHashMap();
		graphsWithFeatureName = HashMultimap.create();
		graphsWithFeature = HashMultimap.create();
		this.USE_NATURAL_KEYS = useNaturalKeys;
		this.name = name;
	}
	public BasicDBInterface(String name) {
		this(name, false);
	}
	public BasicDBInterface(boolean useNaturalKeys) {
		this("default-db", useNaturalKeys);
	}
	public BasicDBInterface() {
		this("default-db", false);
	}



	@Override
	public void disconnect() {
		// does nothing
	}

	/** this db is "connected" when a db file has been loaded and is available 
	 * for use.
	 */
	@Override
	public boolean isConnected() {
		return db != null;
	}

	@Override
	public Graph retrieveGraph(int graphID) {
		return db.get(graphID);
	}

	@Override
	public Stream<Integer> getGraphIDStream() {
		return new HashSet<>(db.keySet()).stream();
	}

	private void addFeature(Feature f) {
		if(!featuresWithName.containsValue(f)) {
			featuresWithName.put(f.getName(), f);
		}
	}

	@Override
	public void deleteGraph(int graphID) {
		db.remove(graphID);
	}

	@Override
	public int getDatabaseSize() {
		return db.size();
	}

	public Stream<Integer> retrieveGraphsWithFeature(Feature f) {
		return graphsWithFeature.get(f).stream();
	}

	public Stream<Integer> retrieveGraphsWithFeatureName(String name) {
		return graphsWithFeatureName.get(name).stream();
	}

	public Set<String> getFeatureNames() {
		return featuresWithName.keySet();
	}


	@Override
	public int addGraph(Graph g) {
		final int graphId;
		if (USE_NATURAL_KEYS) {
			graphId = canonicalId(g);
		} else synchronized (this) {
			graphId = ++lastGraphIdGenerated;
		}
		addGraph(graphId, g);
		return graphId;
	}

	/** assigns a fresh id to the graph and adds it to the database
	 *
	 * @param g the graph to be added to the database
	 * @return the id of the graph after it was added to the database
	 */
	@Override
	public void addGraph(int graphId, Graph g) {
		//insert into db
		db.put(graphId, g);

		//index on all features
		g.getFeatures().forEach(f -> {
			addFeature(f);
			graphsWithFeatureName.put(f.getName(), graphId);
			graphsWithFeature.put(f, graphId);
		});
		g.getNodeIDs().forEach(nid -> {
			g.getNodeFeatures(nid).forEach(f -> {
				addFeature(f);
				graphsWithFeatureName.put(f.getName(), graphId);
				graphsWithFeature.put(f, graphId);
			});
		});
		g.getNodeIDs().forEach(eid -> {
			g.getEdgeFeatures(eid).forEach(f -> {
				addFeature(f);
				graphsWithFeatureName.put(f.getName(), graphId);
				graphsWithFeature.put(f, graphId);
			});
		});
	}

	@Override
    public String toString() { return name; }

}
