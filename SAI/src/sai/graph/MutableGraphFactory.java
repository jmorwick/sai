package sai.graph;

public class MutableGraphFactory implements GraphFactory {

	@Override
	public MutableGraph copy(Graph g) {
		return new MutableGraph(g);
	}

}
