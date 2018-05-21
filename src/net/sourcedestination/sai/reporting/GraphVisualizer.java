package net.sourcedestination.sai.reporting;

import java.awt.image.BufferedImage;

import net.sourcedestination.sai.db.graph.Graph;

public interface GraphVisualizer {
	public BufferedImage visualize(Graph g);
}
