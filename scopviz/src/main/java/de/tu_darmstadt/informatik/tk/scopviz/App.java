package de.tu_darmstadt.informatik.tk.scopviz;

import com.tinkerpop.blueprints.impls.tg.*;
import com.tinkerpop.blueprints.*;

/**
 * Hello world!
 *
 */
public class App {

	public static void main(String[] args) {
		
		Graph graph = new TinkerGraph();
		Vertex a = graph.addVertex(null);
		Vertex b = graph.addVertex(null);
		a.setProperty("name", "marko");
		b.setProperty("name", "peter");
		Edge e = graph.addEdge(null, a, b, "knows");
		System.out.println(e.getVertex(Direction.OUT).getProperty("name") + "--" + e.getLabel() + "-->" + e.getVertex(Direction.IN).getProperty("name"));
		System.out.println("Hello World!");
	}
}
