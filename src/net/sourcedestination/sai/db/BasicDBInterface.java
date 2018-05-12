package net.sourcedestination.sai.db;

import java.util.*;
import java.util.stream.Stream;

import net.sourcedestination.sai.graph.Feature;
import net.sourcedestination.sai.graph.Graph;
import net.sourcedestination.sai.graph.MutableGraph;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

import static net.sourcedestination.sai.graph.Graph.getFeature;
import static net.sourcedestination.sai.graph.Graph.getIndexesFeature;
import static net.sourcedestination.sai.graph.Graph.SAI_ID_NAME;
import static net.sourcedestination.sai.graph.Graph.getIDFeature;

public class BasicDBInterface implements DBInterface {
	private Map<Integer, Graph> db;
	private Multimap<String, Feature> featuresWithName;
	private BiMap<Integer,Feature> featureIDs;
	private Multimap<String, Integer> graphsWithFeatureName;
	private Multimap<Feature, Integer> graphsWithFeature;
	private int nextFeatureID = 1;
	private int nextGraphID = 1;

	public BasicDBInterface() {
		featuresWithName = HashMultimap.create();
		featureIDs = HashBiMap.create();
		db = Maps.newHashMap();
		graphsWithFeatureName = HashMultimap.create();
		graphsWithFeature = HashMultimap.create();
	}

	@Override
	public void disconnect() {
		// does nothing
	}

	private int lookupID(Feature f) {
		if(featureIDs.values().contains(f))
			return featureIDs.inverse().get(f);

		throw new IllegalArgumentException();
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
		if(!featureIDs.containsValue(f)) {
			featureIDs.put(nextFeatureID,  f);
			featuresWithName.put(f.getName(), f);
			nextFeatureID++;
		}
	}

	@Override
	public void deleteGraph(int graphID) {
		db.remove(graphID);

		//TODO: update reference tags
	}

	@Override
	public int getDatabaseSize() {
		return db.size();
	}

	@Override
	public Stream<Integer> retrieveGraphsWithFeature(Feature f) {
		return graphsWithFeature.get(f).stream();
	}

	@Override
	public Stream<Integer> retrieveGraphsWithFeatureName(String name) {
		return graphsWithFeatureName.get(name).stream();
	}

	public Set<String> getFeatureNames() {
		return featuresWithName.keySet();
	}

	public void addIndex(int graphID, int indexGraphID) {
		if(!db.containsKey(graphID) || !db.containsKey(indexGraphID))
			throw new IllegalArgumentException("graphid doesn't exist");
		var ireplace = new MutableGraph(db.get(indexGraphID));
		ireplace.addFeature(getIndexesFeature(graphID));
		this.replaceGraph(indexGraphID, ireplace);
	}

	/** assigns a fresh id to the graph and adds it to the database
	 * 
	 * @param g1 the graph to be added to the database
	 * @return the id of the graph after it was added to the database
	 */
	@Override
	public int addGraph(Graph g1) {
		var g = new MutableGraph(g1);
		var newGraphIDtemp = 0;
		if(getFeature(g.getFeatures(), SAI_ID_NAME) != null) {
			newGraphIDtemp = Integer.parseInt(getFeature(g.getFeatures(), 
					SAI_ID_NAME).getValue());
			if(db.containsKey(newGraphIDtemp)) {
				newGraphIDtemp = nextGraphID;
				System.out.println("!!!");
			}
		} else newGraphIDtemp = nextGraphID;
		final var newGraphID = newGraphIDtemp;


		nextGraphID = Math.max(nextGraphID, newGraphID) + 1; // update next fresh graph id

		//update SAI-id tag
		Optional<Feature> saiID = 
				g.getFeatures()
				.filter(f -> f.getName().equals(SAI_ID_NAME))
				.findFirst();
		// remove the old one
		saiID.ifPresent(g::removeFeature);
		g.addFeature(getIDFeature(newGraphID)); // add the new one

		//insert into db
		db.put(newGraphID, g);

		//add all features
		g.getFeatures().forEach(f -> {
			addFeature(f);
			graphsWithFeatureName.put(f.getName(), newGraphID);
			graphsWithFeature.put(f, newGraphID);
		});
		g.getNodeIDs().forEach(nid -> {
			g.getNodeFeatures(nid).forEach(f -> {
				addFeature(f);
				graphsWithFeatureName.put(f.getName(), newGraphID);
				graphsWithFeature.put(f, newGraphID);
			});
		});
		g.getNodeIDs().forEach(eid -> {
			g.getEdgeFeatures(eid).forEach(f -> {
				addFeature(f);
				graphsWithFeatureName.put(f.getName(), newGraphID);
				graphsWithFeature.put(f, newGraphID);
			});
		});


		return newGraphID;
	}

	public void replaceGraph(int indexGraphID, Graph g2) {
		MutableGraph g = new MutableGraph(g2);

		//update SAI-id tag
		Optional<Feature> saiID = 
				g.getFeatures()
				.filter(f -> f.getName().equals(SAI_ID_NAME))
				.findFirst();
		// remove the old one
		saiID.ifPresent(g::removeFeature);
		g.addFeature(getIDFeature(indexGraphID)); // add the new one
		
		if(db.containsKey(indexGraphID))
			db.remove(indexGraphID);
		addGraph(g);
	}

}
