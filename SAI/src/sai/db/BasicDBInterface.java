package sai.db;

import java.util.InputMismatchException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Set;
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

public class BasicDBInterface implements DBInterface {

	private File dbfile;
	
	private Map<Integer, Graph> db;
	private Multimap<Integer, Integer> indexing;
	private Multimap<Integer, Integer> indexedBy;
	private Set<Integer> indexes;
	private Set<Integer> residentGraphs;
	private Multimap<String, Feature> featuresWithName;
	private BiMap<Integer,Feature> featureIDs; 
	private Set<Integer> hiddenGraphs;
	private Multimap<String, Integer> graphsWithFeatureName;
	private Multimap<Feature, Integer> graphsWithFeature;
	private int nextFeatureID = 1;
	private int nextGraphID = 1;

	public BasicDBInterface(File dbfile) {
		this.dbfile = dbfile;
	}
	public BasicDBInterface() {
	}

	public void setDBFile(File dbfile) {
		this.dbfile = dbfile;
	}
	
	public File getDBFile() { return dbfile; }
	
	/** loads database details completely in to memory from the db file 
	 * provided to the constructor.
	 */
	@Override
	public void connect() throws AccessDeniedException {
		featuresWithName = HashMultimap.create();
		featureIDs = HashBiMap.create();
		db = Maps.newHashMap();
		indexes = Sets.newHashSet();
		residentGraphs = Sets.newHashSet();
		hiddenGraphs = Sets.newHashSet();
		indexing = HashMultimap.create();
		indexedBy = HashMultimap.create();
		graphsWithFeatureName = HashMultimap.create();
		graphsWithFeature = HashMultimap.create();
		
		
		if(dbfile == null) return;
		
		if(!dbfile.exists() || !dbfile.canRead())
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
				if(nextGraphID <= gid) nextGraphID = gid+1;
				int numNodes = lin.nextInt();
				int numEdges = lin.nextInt();
				MutableGraph g = new MutableGraph();
				while(lin.hasNext()) {
					Feature f = featureIDs.get(lin.nextInt());
					g.addFeature(f);
					graphsWithFeatureName.put(f.getName(), gid);
					graphsWithFeature.put(f, gid);
				}
				lin.close();

				if(g.getFeatures().contains(Graphs.INDEX))
					indexes.add(gid);
				else residentGraphs.add(gid);
				
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
						graphsWithFeatureName.put(f.getName(), gid);
						graphsWithFeature.put(f, gid);
					}
					lin.close();
				}
				
				g.addFeature(new Feature("sai-id", ""+gid));
				
				//put complete graph in db...
				db.put(gid, g);
			}
			
			//read in indexing details
			int numIndexes = Integer.parseInt(in.readLine());
			for(int i=0; i<numIndexes; i++) {
				String line = in.readLine();
				Scanner lin = new Scanner(line);
				lin.useDelimiter(",");
				int iid = lin.nextInt();
				while(lin.hasNext()) {
					int gid = lin.nextInt();
					indexing.put(iid, gid);
					indexedBy.put(gid, iid);
				}
				lin.close();
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
	
	/** saves database details completely to the db file 
	 * provided to the constructor.
	 * @throws FileNotFoundException 
	 */
	@Override
	public void disconnect() throws AccessDeniedException {
		PrintWriter out = null;
		try {
			out = new PrintWriter(dbfile);
		} catch (FileNotFoundException e) {
			throw new AccessDeniedException(dbfile.getAbsolutePath());
		}
		
		//check for write access
		if(!dbfile.canWrite()) {
			out.close();
			throw new AccessDeniedException(dbfile.getAbsolutePath());
		}
		
		// destroy duplicated information
		residentGraphs = null;
		indexes = null;
		indexedBy = null;
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
			Graph g = retrieveGraph(gid, MutableGraph.getFactory());
			//print out general graph info on one line
			out.print(gid+",");
			out.print(g.getNodeIDs().size()+",");
			out.print(g.getEdgeIDs().size());
			for(Feature f : g.getFeatures()) 
				out.print("," + lookupID(f));
			out.print("\n");
			//print a line for each node
			for(int n : g.getNodeIDs()) {
				out.print(n);
				for(Feature f : g.getNodeFeatures(n)) 
					out.print("," + lookupID(f));
				out.print("\n");
			}
			//print a line for each edge
			for(int e : g.getEdgeIDs()) {
				out.print(e+","+g.getEdgeSourceNodeID(e)+","+g.getEdgeTargetNodeID(e));
				for(Feature f : g.getEdgeFeatures(e)) 
					out.print("," + lookupID(f));
				out.print("\n");
			}
		}
		db = null;
		
		//write indexes to file
		out.println(indexing.keySet().size());
		for(int iid : indexing.keySet()) {
			out.print(iid);
			for(int gid : indexing.get(iid)) {
				out.print(","+gid);
			}
			out.println();
		}
		indexing = null;
		featureIDs = null;
		
		out.close();
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
	public Iterator<Integer> getGraphIDIterator() {
		return Sets.difference(residentGraphs, hiddenGraphs).iterator();
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
	
	/** assigns a fresh id to the graph and adds it to the database
	 * 
	 * @param g the graph to be added to the database
	 * @return 
	 * @return the id of the graph after it was added to the database
	 */
	@Override
	public int addGraph(Graph g1) {
		MutableGraph g = null;
		if(g instanceof MutableGraph)
			g = (MutableGraph)g1;
		else g = new MutableGraph(g1);
		
		//insure this has a proper sai id 
		if(getFeature(g.getFeatures(), "sai-id") == null ||
		   db.containsKey(Integer.parseInt(getFeature(g.getFeatures(), "sai-id").getValue())))
		   g.addFeature(new Feature("sai-id", ""+nextGraphID)); //create a fresh id for this graph
		int newGraphID = Integer.parseInt(getFeature(g.getFeatures(), "sai-id").getValue());
		nextGraphID = Math.max(nextGraphID, newGraphID) + 1; // update next fresh graph id
		
		//handle if this is an index
		if(g.getFeatures().contains(Graphs.INDEX))
			indexes.add(newGraphID);
		else residentGraphs.add(newGraphID);
		db.put(newGraphID, g);
		
		//add all features
		for(Feature f : g.getFeatures())
			addFeature(f);
		for(int nid : g.getNodeIDs())
			for(Feature f : g.getNodeFeatures(nid))
				addFeature(f);
		for(int eid : g.getEdgeIDs())
			for(Feature f : g.getEdgeFeatures(eid))
				addFeature(f);
		
		return newGraphID;
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
		indexes.remove(graphID);
		residentGraphs.remove(graphID);
		for(int iid : indexedBy.get(graphID)) {
			indexing.remove(iid, graphID);
		}
		indexedBy.removeAll(graphID);
	}
	
	@Override
	public int getDatabaseSize() {
		return residentGraphs.size() + indexes.size();
	}
	
	public Iterator<Integer> retrieveGraphsWithFeature(Feature f) {
		return graphsWithFeature.get(f).iterator();
	}
	
	public Iterator<Integer> retrieveGraphsWithFeatureName(String name) {
		return graphsWithFeatureName.get(name).iterator();
	}
	
	
	@Override
	public Iterator<Integer> getIndexIDIterator() {
		return Sets.difference(indexes, hiddenGraphs).iterator();
	}

	@Override
	public void addIndex(int gid, int iid) {
		indexing.put(iid, gid);
		indexedBy.put(gid, iid);
	}

	@Override
	public Set<Integer> retrieveIndexIDs(int graphID) {
		return Sets.newHashSet(indexedBy.get(graphID));
	}

	@Override
	public Set<Integer> retrieveIndexedGraphIDs(int indexID) {
		return Sets.newHashSet(indexing.get(indexID));
	}

	public Set<String> getFeatureNames() {
		return featuresWithName.keySet();
	}

	@Override
	public int getDatabaseSizeWithoutIndices() {
		return residentGraphs.size();
	}

}
