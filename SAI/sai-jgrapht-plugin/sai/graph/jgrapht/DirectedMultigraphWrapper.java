package sai.graph.jgrapht;

import java.util.Collection;
import java.util.Set;

import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.DirectedMultigraph;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import sai.graph.Feature;

public class DirectedMultigraphWrapper<N, E> extends DirectedMultigraph<N, E> 
	implements sai.graph.Graph {

	private BiMap<E, Integer> edgeIDs; 
	private BiMap<N, Integer> nodeIDs;
	private Set<Feature> features;
	private Multimap <Integer, Feature> nodeFeatures;
	private Multimap <Integer, Feature> edgeFeatures;
	private int nextEdgeID = 1;
	private int nextNodeID = 1;

	public DirectedMultigraphWrapper(Class<? extends E> edgeClass) {
		super(edgeClass);
		edgeIDs = HashBiMap.create();
		nodeIDs = HashBiMap.create();
		nodeFeatures = HashMultimap.create();
		edgeFeatures = HashMultimap.create();
		features = Sets.newHashSet();
	}

	public DirectedMultigraphWrapper(EdgeFactory<N, E> ef) {
		super(ef);
		edgeIDs = HashBiMap.create();
		nodeIDs = HashBiMap.create();
		nodeFeatures = HashMultimap.create();
		edgeFeatures = HashMultimap.create();
		features = Sets.newHashSet();
	}

	////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////
	//// overridden mutators to keep state of ids consistent...
	////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////
	
	@Override
	public E addEdge(N n1, N n2) {
		E e = super.addEdge(n1, n2);
		if(e != null && !edgeIDs.containsKey(e)) {
			edgeIDs.put(e, nextEdgeID++);
		}
		return e;
	}

	@Override
	public boolean addEdge(N n1, N n2, E e) {
		boolean success = super.addEdge(n1, n2, e);
		if(success && !edgeIDs.containsKey(e))
			edgeIDs.put(e, nextEdgeID++);
		return success;
	}
	
	@Override
	public boolean addVertex(N n) {
		boolean success = super.addVertex(n);
		if(success && !nodeIDs.containsKey(n))
			nodeIDs.put(n, nextNodeID++);
		return success;
	}
	
	@Override
	public boolean removeVertex(N n) {
		boolean success = super.removeVertex(n);
		if(success) {
			int nid = nodeIDs.get(n);
			nodeFeatures.removeAll(nid);
			nodeIDs.remove(n);
		}
		return success;
	}

	@Override
	public boolean removeAllVertices(Collection<? extends N> nodes) {
		boolean success = super.removeAllVertices(nodes);
		if(success) {
			for(N n : nodes) {
				int nid = nodeIDs.get(n);
				nodeFeatures.removeAll(nid);
				nodeIDs.remove(n);
			}
		}
		return success;
	}

	@Override
	public boolean removeAllEdges(Collection<? extends E> edges) {
		boolean success = super.removeAllEdges(edges);
		if(success) {
			for(E e : edges) {
				int eid = edgeIDs.get(e);
				edgeFeatures.removeAll(eid);
				edgeIDs.remove(e);
			}
		}
		return success;
	}
	
	@Override
	public boolean removeAllEdges(E[] edges) {
		boolean success = super.removeAllEdges(edges);
		if(success) {
			for(E e : edges) {
				int eid = edgeIDs.get(e);
				edgeFeatures.removeAll(eid);
				edgeIDs.remove(e);
			}
		}
		return success;
	}
	

	@Override
	public Set<E> removeAllEdges(N n1, N n2) {
		Set<E> edges = super.removeAllEdges(n1, n2);
		for(E e : edges) {
			int eid = edgeIDs.get(e);
			edgeFeatures.removeAll(eid);
			edgeIDs.remove(e);
		}
		return edges;
	}
	

	@Override
	public boolean removeEdge(E e) {
		boolean success = super.removeEdge(e);
		if(success) {
			int eid = edgeIDs.get(e);
			edgeFeatures.removeAll(eid);
			edgeIDs.remove(e);
		}
		return success;
	}
	

	@Override
	public E removeEdge(N n1, N n2) {
		E e = super.removeEdge(n1, n2);
		if(e != null) {
			int eid = edgeIDs.get(e);
			edgeFeatures.removeAll(eid);
			edgeIDs.remove(e);
		}
		return e;
	}
	
	// TODO: create tests using SAI graph and checking consistency w/ wrapper

	////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////
	// SAI feature modification methods....
	////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////

	public void addFeature(Feature f) {
		features.add(f);
	}

	public void addNodeFeature(int nid, Feature f) {
		if(!nodeIDs.containsValue(nid))
			throw new IllegalArgumentException("No such node with id #"+nid);
		nodeFeatures.put(nid, f);
	}
	
	public void addEdgeFeature(int eid, Feature f) {
		if(!edgeIDs.containsValue(eid))
			throw new IllegalArgumentException("No such edge with id #"+eid);
		nodeFeatures.put(eid, f);
	}

	////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////
	// SAI interface implementation....
	////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////
	
	@Override
	public Set<Integer> getEdgeIDs() {
		return Sets.newHashSet(edgeIDs.values());
	}

	@Override
	public Set<Integer> getNodeIDs() {
		return Sets.newHashSet(nodeIDs.values());
	}

	@Override
	public Set<Feature> getFeatures() {
		return Sets.newHashSet(features);
	}

	@Override
	public Set<Feature> getNodeFeatures(int n) {
		return Sets.newHashSet(nodeFeatures.get(n));
	}

	@Override
	public Set<Feature> getEdgeFeatures(int e) {
		return Sets.newHashSet(nodeFeatures.get(e));
	}

	@Override
	public int getEdgeSourceNodeID(int edgeID) {
		return nodeIDs.get(getEdgeSource(edgeIDs.inverse().get(edgeID)));
	}

	@Override
	public int getEdgeTargetNodeID(int edgeID) {
		return nodeIDs.get(getEdgeTarget(edgeIDs.inverse().get(edgeID)));
	}
}
