package de.tu_darmstadt.informatik.tk.scopviz.graphs;

public class GraphHelper {

	public static MyGraph merge(MyGraph graph1, MyGraph graph2) {
		Double targetMinX = graph1.getMinX();
		Double targetMaxX = graph1.getMaxX();
		Double sourceMinX = graph2.getMinX();
		Double sourceMaxX = graph2.getMaxX();
		Double scalingFactor = ((targetMaxX - targetMinX + 1) / graph1.getNodeCount())
				/ ((sourceMaxX - sourceMinX + 1) / graph2.getNodeCount());

		// TODO
		return null;
	}

}
