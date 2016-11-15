package de.tu_darmstadt.informatik.tk.scopviz;

import java.io.IOException;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceGraphML;

/**
 * @version 1.1
 * @author jascha-b
 *
 */
public class GraphMLImporter {
	/**
	 * Imports a GraphML file.
	 * 
	 * @param fileName
	 *            path to the file on disk
	 * @return the imported Graphstream-Graph
	 */
	public Graph readGraph(final String fileName) {
		Graph g = new DefaultGraph("g");
		FileSource fs = new FileSourceGraphML();
		fs.addSink(g);
		try {
			fs.readAll(fileName);
		} catch (IOException e) {
			System.out.println("GraphML File doesn't exist or can't be opened");
			e.printStackTrace();
		}
		return g;
	}
}