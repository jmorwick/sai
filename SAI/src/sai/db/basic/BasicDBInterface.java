package sai.db.basic;

import java.util.HashMap;
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

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import sai.db.DBInterface;
import sai.graph.Edge;
import sai.graph.Feature;
import sai.graph.Graph;
import sai.graph.GraphFactory;
import sai.graph.Node;
import sai.graph.basic.MutableGraph;

public class BasicDBInterface implements DBInterface {

	private File dbfile;
	private GraphFactory<?> gf;
	
	private Map<Integer, Graph> db;
	private Multimap<Integer, Integer> indexing;
	private Multimap<Integer, Integer> indexedBy;
	private Set<Integer> indexes;
	private Set<Integer> residentGraphs;
	private Multimap<Integer,Integer> featureCompatibility;
	private Multimap<String, Integer> featuresWithName;
	private Map<Integer,Feature> features;
	private Set<Integer> hiddenGraphs;

	public BasicDBInterface(File dbfile, GraphFactory<?> gf) {
		this.dbfile = dbfile;
		this.gf = gf;
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
		if(!dbfile.exists() || !dbfile.canRead())
			throw new AccessDeniedException(dbfile.getAbsolutePath());
		
		try {
			BufferedReader in = new BufferedReader(new FileReader(dbfile));
			int numFeatureNames = Integer.parseInt(in.readLine());
			
			//read in features
			featuresWithName = HashMultimap.create();
			features = new HashMap<Integer,Feature>();
			for(int i=0; i<numFeatureNames; i++) {
				Scanner lin = new Scanner(in.readLine());
				lin.useDelimiter(",");
				final String name = lin.next();
				while(lin.hasNext()) {
					final int fid = lin.nextInt();
					final String value = lin.next();
					featuresWithName.put(name, fid);
					features.put(fid, new Feature() {

						@Override
						public int getID() {
							return fid;
						}

						@Override
						public String getValue() {
							return value;
						}

						@Override
						public String getName() {
							return name;
						}
					});
				}
				lin.close();
			}
			
			//read in feature relationships
			featureCompatibility = HashMultimap.create();
			int numFeatureRelationships = Integer.parseInt(in.readLine());
			for(int i=0; i<numFeatureRelationships; i++) {
				Scanner lin = new Scanner(in.readLine());
				lin.useDelimiter(",");
				int fid = lin.nextInt();
				while(lin.hasNext()) 
					featureCompatibility.put(fid, lin.nextInt());
				lin.close();
			}
			
			//read in graphs
			db = new HashMap<Integer, Graph>();
			indexes = Sets.newHashSet();
			residentGraphs = Sets.newHashSet();
			hiddenGraphs = Sets.newHashSet();
			
			int numGraphs = Integer.parseInt(in.readLine());
			for(int i=0; i<numGraphs; i++) {
				//get general graph into
				Scanner lin = new Scanner(in.readLine());
				int gid = lin.nextInt();
				boolean directed = lin.nextBoolean();
				boolean multi = lin.nextBoolean();
				boolean pseudo = lin.nextBoolean();
				boolean index = lin.nextBoolean();
				int numNodes = lin.nextInt();
				int numEdges = lin.nextInt();
				MutableGraph g = new MutableGraph(directed, multi, pseudo, index);
				while(lin.hasNext()) 
					g.addFeature(features.get(lin.nextInt()));
				lin.close();
				
				//get graph nodes
				for(int j=0; j<numNodes; j++) {
					lin = new Scanner(in.readLine());
					final int nid = lin.nextInt();
					Node n = g.addNode(nid);
					while(lin.hasNext()) 
						g.addFeature(n, features.get(lin.nextInt()));
					lin.close();
				}
				
				//get graph edges
				for(int j=0; j<numEdges; j++) {
					lin = new Scanner(in.readLine());
					final int eid = lin.nextInt();
					final int nid1 = lin.nextInt();
					final int nid2 = lin.nextInt();
					Edge e = g.addEdge(eid, g.getNode(nid1), g.getNode(nid2));
					while(lin.hasNext()) 
						g.addFeature(e, features.get(lin.nextInt()));
					lin.close();
				}
				
				//put complete graph in db...
				db.put(gid, gf.copy(g, gid));
				
			}
			
			//read in indexing details
			indexing = HashMultimap.create();
			indexedBy = HashMultimap.create();
			int numIndexes = Integer.parseInt(in.readLine());
			for(int i=0; i<numIndexes; i++) {
				Scanner lin = new Scanner(in.readLine());
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
			throw new AccessDeniedException(dbfile.getAbsolutePath(), 
					null, "formatting error when reading file");
		} catch (IOException e) {
			throw new AccessDeniedException(dbfile.getAbsolutePath(), 
					null, "I/O error when reading file");
		} catch (InputMismatchException e) {
			throw new AccessDeniedException(dbfile.getAbsolutePath(), 
					null, "formatting error when reading file");
		} catch (NoSuchElementException e) {
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

		
		//write features to file
		out.println(featuresWithName.keys().size()); // start with number of features
		for(String name : featuresWithName.keys()) {
			out.print(name);
			for(int fid : featuresWithName.get(name)) {
				out.print("," + fid);
				out.print("," + features.get(fid).getValue());
			}
			out.print("\n");
		}
		featuresWithName = null; 
		features = null;
		
		//write feature compatibility to file
		out.println(featureCompatibility.keySet().size()+"\n"); //output how many records
		for(int f : featureCompatibility.keySet()) {
			out.print(f);
			for (int cf : featureCompatibility.get(f)) {
				out.print("," + cf);
			}
			out.print("\n");
		}
		featureCompatibility = null; //destroy records of which features are compatible
		
		
		//write graphs to file
		out.print(db.keySet() + "\n");
		for(int gid : db.keySet()) {
			Graph g = db.get(gid);
			//print out general graph info on one line
			out.print(g.getID()+",");
			out.print(g.isDirectedgraph()+",");
			out.print(g.isMultigraph()+",");
			out.print(g.isPseudograph()+",");
			out.print(g.isIndex()+",");
			out.print(g.getNodes().size()+",");
			out.print(g.getEdges().size()+",");
			for(Feature f : g.getFeatures()) 
				out.print("," + f.getID());
			out.print("\n");
			//print a line for each node
			for(Node n : g.getNodes()) {
				out.print(n.getID());
				for(Feature f : n.getFeatures()) 
					out.print("," + f.getID());
				out.print("\n");
			}
			//print a line for each edge
			for(Edge e : g.getEdges()) {
				out.println(e.getID()+","+g.getEdgeSource(e)+","+g.getEdgeTarget(e));
				for(Feature f : e.getFeatures()) 
					out.print("," + f.getID());
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
			out.println("\n");
		}
		indexing = null;
		
		out.close();
	}

	/** this db is "connected" when a db file has been loaded and is available 
	 * for use.
	 */
	@Override
	public boolean isConnected() {
		return db != null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <G extends Graph> G retrieveGraph(int graphID, GraphFactory<G> f) {
		if(hiddenGraphs.contains(graphID) || !db.containsKey(graphID))
			return null;
		
		if(f == gf) { // if the same factory that created the graph is supplied, return it
			return (G) db.get(graphID);
		}
		
		//otherwise, rebuild it...
		return f.copy(db.get(graphID));
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

	@Override
	public Feature getFeature(final int featureID) {
		return features.get(featureID);
	}

	@Override
	public Set<String> getFeatureNames() {
		return featuresWithName.keySet();
	}

	@Override
	public Set<Integer> getFeatureIDs() {
		return features.keySet();
	}

	@Override
	public Set<Integer> getFeatureIDs(String featureName) {
		return Sets.newHashSet(featuresWithName.get(featureName));
	}

	@Override
	public void setCompatible(Feature fa, Feature fb) {
		featureCompatibility.put(fa.getID(), fb.getID());
	}

	@Override
	public void setNotCompatible(Feature fa, Feature fb) {
		featureCompatibility.remove(fa.getID(), fb.getID());
	}

	@Override
	public boolean isCompatible(Feature fa, Feature fb) {
		return featureCompatibility.containsEntry(fa.getID(), fb.getID());
	}

	@Override
	public int getDatabaseSize() {
		return residentGraphs.size() + indexes.size();
	}

	@Override
	public int getDatabaseSizeWithoutIndices() {
		return residentGraphs.size();
	}

}
