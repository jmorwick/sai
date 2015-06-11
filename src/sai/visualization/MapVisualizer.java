package sai.visualization;

import java.awt.image.BufferedImage;
import java.util.Map;

import sai.graph.Graph;

public interface MapVisualizer {
	public BufferedImage visualize(Graph g1, Graph g2, Map<Integer,Integer> m);
}
