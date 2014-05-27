package sai.graph;


public class BasicGraphFactory implements GraphFactory<BasicGraphWrapper> {

	@Override
	public BasicGraphWrapper copy(Graph g) {
		return new BasicGraphWrapper(new MutableGraph(g));
	}

	@Override
	public BasicGraphWrapper copy(Graph g, int id) {
		MutableGraph mg = new MutableGraph(g);
		mg.setID(id);
		return new BasicGraphWrapper(mg);
	}

}
