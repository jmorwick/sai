package sai.visualization;

import java.awt.image.BufferedImage;

import sai.graph.Graph;

public interface GraphVisualizer {
	public BufferedImage visualize(Graph g);
}
