package de.tu_darmstadt.informatik.tk.scopviz.io;

import java.io.IOException;
import java.net.URL;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceGraphML;

/**
 * Importer to import a graph from a GraphML file and return it as a Graph
 * object.
 * 
 * @author jascha-b
 * @version 1.1
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
		fs.removeSink(g);
		return g;
	}

	/**
	 * Imports a GraphML file.
	 * 
	 * @param fileURL
	 *            URL of the file
	 * @return the imported Graphstream-Graph
	 */
	public Graph readGraph(final URL fileURL) {
		Graph g = new DefaultGraph("g");
		FileSource fs = new FileSourceGraphML();
		fs.addSink(g);
		try {
			fs.readAll(fileURL);
		} catch (IOException e) {
			System.out.println("GraphML File doesn't exist or can't be opened");
			e.printStackTrace();
		}
		fs.removeSink(g);
		return g;
	}
}