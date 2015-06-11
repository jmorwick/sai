package net.sourcedestination.sai.visualization;

import java.awt.image.BufferedImage;

import net.sourcedestination.sai.graph.Graph;

public interface GraphVisualizer {
	public BufferedImage visualize(Graph g);
}
