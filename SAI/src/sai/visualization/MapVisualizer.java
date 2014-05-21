package sai.visualization;

import java.awt.image.BufferedImage;
import java.util.Map;

import sai.graph.Graph;
import sai.graph.Node;

public interface MapVisualizer {
	public BufferedImage visualize(Graph g1, Graph g2, Map<Node,Node> m);
}
