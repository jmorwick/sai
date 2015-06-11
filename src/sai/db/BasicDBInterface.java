package sai.db;

import java.util.InputMismatchException;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Stream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.AccessDeniedException; 

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import sai.graph.Feature;
import sai.graph.Graph;
import sai.graph.GraphFactory;
import sai.graph.Graphs;
import sai.graph.MutableGraph;
import static sai.graph.Graphs.getFeature;

// TODO: go over and look for more potential 1.8 updates

public class BasicDBInterface implements DBInterface {

	private File dbfile;

	private Map<Integer, Graph> db;
	private Multimap<String, Feature> featuresWithName;
	private BiMap<Integer,Feature> featureIDs; 
	private Set<Integer> hiddenGraphs;
	private Multimap<String, Integer> graphsWithFeatureName;
	private Multimap<Feature, Integer> graphsWithFeature;
	private int nextFeatureID = 1;
	private int nextGraphID = 1;

	public BasicDBInterface() {
		featuresWithName = HashMultimap.create();
		featureIDs = HashBiMap.create();
		db = Maps.newHashMap();
		hiddenGraphs = Sets.newHashSet();
		graphsWithFeatureName = HashMultimap.create();
		graphsWithFeature = HashMultimap.create();
	}

	public BasicDBInterface(File dbfile) throws AccessDeniedException {
		this();
		this.dbfile = dbfile;

		if(dbfile == null || !dbfile.exists() || !dbfile.canRead())
			throw new AccessDeniedException(dbfile.getAbsolutePath());

		try {
			nextFeatureID = 0;
			nextGraphID = 0;
			BufferedReader in = new BufferedReader(new FileReader(dbfile));
			int numFeatureNames = Integer.parseInt(in.readLine());

			//read in features
			for(int i=0; i<numFeatureNames; i++) {
				Scanner lin = new Scanner(in.readLine());
				lin.useDelimiter(",");
				final String name = lin.next();
				while(lin.hasNext()) {
					final int fid = lin.nextInt();
					final String value = lin.next();
					if(nextFeatureID <= fid) nextFeatureID = fid+1;
					Feature f = new Feature(name, value);
					featuresWithName.put(name, f);
					featureIDs.put(fid, f);
				}
				lin.close();
			}
			//read in graphs

			int numGraphs = Integer.parseInt(in.readLine());
			for(int i=0; i<numGraphs; i++) {
				//get general graph into
				String line = in.readLine();
				Scanner lin = new Scanner(line);
				lin.useDelimiter(",");
				int gid = lin.nextInt();
				int numNodes = lin.nextInt();
				int numEdges = lin.nextInt();
				MutableGraph g = new MutableGraph();
				while(lin.hasNext()) {
					Feature f = featureIDs.get(lin.nextInt());
					g.addFeature(f);
				}
				lin.close();

				//get graph nodes
				for(int j=0; j<numNodes; j++) {
					line = in.readLine();
					lin = new Scanner(line);
					lin.useDelimiter(",");
					final int nid = lin.nextInt();
					g.addNode(nid);
					while(lin.hasNext()) {
						Feature f = featureIDs.get(lin.nextInt());
						g.addNodeFeature(nid, f);
						graphsWithFeatureName.put(f.getName(), gid);
						graphsWithFeature.put(f, gid);
					}
					lin.close();
				}

				//get graph edges
				for(int j=0; j<numEdges; j++) {
					lin = new Scanner(in.readLine());
					lin.useDelimiter(",");
					final int eid = lin.nextInt();
					final int nid1 = lin.nextInt();
					final int nid2 = lin.nextInt();
					g.addEdge(eid, nid1, nid2);
					while(lin.hasNext()) {
						Feature f = featureIDs.get(lin.nextInt());
						g.addEdgeFeature(eid, f);
					}
					lin.close();
				}
				replaceGraph(gid, g);
			}
			in.close();
		} catch (NumberFormatException e) {
			e.printStackTrace();
			throw new AccessDeniedException(dbfile.getAbsolutePath(), 
					null, "formatting error when reading file");
		} catch (IOException e) {
			e.printStackTrace();
			throw new AccessDeniedException(dbfile.getAbsolutePath(), 
					null, "I/O error when reading file");
		} catch (InputMismatchException e) {
			e.printStackTrace();
			throw new AccessDeniedException(dbfile.getAbsolutePath(), 
					null, "formatting error when reading file");
		} catch (NoSuchElementException e) {
			e.printStackTrace();
			throw new AccessDeniedException(dbfile.getAbsolutePath(), 
					null, "formatting error when reading file");
		}
	}

	public void setDBFile(File dbfile) throws AccessDeniedException {
		this.dbfile = dbfile;
		//check for write access
		if((!dbfile.canWrite() && dbfile.exists()) ||
				(!dbfile.getParentFile().canWrite() && !dbfile.exists())) {
			throw new AccessDeniedException(dbfile.getAbsolutePath());
		}
	}

	public File getDBFile() { return dbfile; }

	/** saves database details completely to the db file 
	 * provided to the constructor.
	 * @throws FileNotFoundException 
	 */
	@Override
	public void disconnect() {
		try {
		PrintWriter out = new PrintWriter(dbfile);

		// destroy duplicated information
		graphsWithFeatureName = null;
		graphsWithFeature = null;


		//write features to file
		out.println(featuresWithName.keySet().size()); // start with number of features
		for(String name : featuresWithName.keySet()) {
			out.print(name);
			for(Feature f : featuresWithName.get(name)) {
				out.print("," + lookupID(f));
				out.print("," + f.getValue());
			}
			out.print("\n");
		}
		featuresWithName = null; 

		//write graphs to file
		out.print(db.keySet().size() + "\n");
		for(int gid : db.keySet()) {
			Graph g = retrieveGraph(gid, MutableGraph::new);
			//print out general graph info on one line
			out.print(gid+",");
			out.print(g.getNodeIDs().count()+",");
			out.print(g.getEdgeIDs().count());
			g.getFeatures().forEach(f -> out.print("," + lookupID(f)));
			out.print("\n");
			//print a line for each node
			g.getNodeIDs().forEach(n -> {
				out.print(n);
				g.getNodeFeatures(n).forEach(f -> out.print("," + lookupID(f)));
				out.print("\n");
			});
			//print a line for each edge
			g.getEdgeIDs().forEach(e -> {
				out.print(e+","+g.getEdgeSourceNodeID(e)+","+g.getEdgeTargetNodeID(e));
				g.getEdgeFeatures(e).forEach(f -> out.print("," + lookupID(f)));
				out.print("\n");
			});
		}
		db = null;
		featureIDs = null;

		out.close();
		} catch (FileNotFoundException e) {
			// TODO: use exception here?
		}
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
	public <G extends Graph> G retrieveGraph(int graphID, GraphFactory<G> f) {
		if(hiddenGraphs.contains(graphID) || !db.containsKey(graphID))
			return null;

		//otherwise, rebuild it...
		G g =  f.copy(db.get(graphID));
		db.put(graphID, g);
		return g;
	}


	@SuppressWarnings("unchecked")
	public <G extends Graph> G retrieveGraph(int graphID) {
		if(hiddenGraphs.contains(graphID) || !db.containsKey(graphID))
			return null;

		return (G) db.get(graphID);
	}

	@Override
	public Stream<Integer> getGraphIDStream() {
		return db.keySet().stream().filter(id -> !hiddenGraphs.contains(id));
	}

	@Override
	public Set<Integer> getHiddenGraphs() {
		return Sets.newHashSet(hiddenGraphs);
	}

	@Override
	public void hideGraph(int graphID) {
		hiddenGraphs.add(graphID);
	}

	@Override
	public void unhideGraph(int graphID) {
		hiddenGraphs.remove(graphID);
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
		MutableGraph ireplace = new MutableGraph(db.get(indexGraphID));
		ireplace.addFeature(Graphs.getIndexesFeature(graphID));
		this.replaceGraph(indexGraphID, ireplace);
	}

	/** assigns a fresh id to the graph and adds it to the database
	 * 
	 * @param g the graph to be added to the database
	 * @return the id of the graph after it was added to the database
	 */
	@Override
	public int addGraph(Graph g1) {
		MutableGraph g = new MutableGraph(g1);
		int newGraphIDtemp;
		if(getFeature(g.getFeatures(), Graphs.SAI_ID_NAME) != null) {
			newGraphIDtemp = Integer.parseInt(getFeature(g.getFeatures(), 
					Graphs.SAI_ID_NAME).getValue());
			if(db.containsKey(newGraphIDtemp)) {
				newGraphIDtemp = nextGraphID;
				System.out.println("!!!");
			}
		} else newGraphIDtemp = nextGraphID;
		final int newGraphID = newGraphIDtemp;


		nextGraphID = Math.max(nextGraphID, newGraphID) + 1; // update next fresh graph id

		//update SAI-id tag
		Optional<Feature> saiID = 
				g.getFeatures()
				.filter(f -> f.getName().equals(Graphs.SAI_ID_NAME))
				.findFirst();
		if(saiID.isPresent()) g.removeFeature(saiID.get()); // remove the old one
		g.addFeature(Graphs.getIDFeature(newGraphID)); // add the new one

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
				.filter(f -> f.getName().equals(Graphs.SAI_ID_NAME))
				.findFirst();
		if(saiID.isPresent()) g.removeFeature(saiID.get()); // remove the old one
		g.addFeature(Graphs.getIDFeature(indexGraphID)); // add the new one
		
		if(db.containsKey(indexGraphID))
			db.remove(indexGraphID);
		addGraph(g);
	}

}
