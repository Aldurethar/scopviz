package de.tu_darmstadt.informatik.tk.scopviz.io;

import java.io.IOException;
import java.net.URL;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceGraphML;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

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
	 * @param id
	 *            unique ID
	 * @param fileName
	 *            path to the file on disk
	 * @return the imported Graphstream-Graph
	 */
	public Graph readGraph(String id, final String fileName) {
		Graph g = new DefaultGraph(id);
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
	 * Opens a open dialog
	 * 
	 * @param stage
	 *            the parent window of the open file window
	 * @return the imported Graphstream-Graph
	 */
	public Graph readGraph(final Stage stage) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("open graph");
		String fileName = fileChooser.showOpenDialog(stage).getPath();
		return readGraph(fileName);
	}
	/**
	 * Imports a GraphML file.
	 * 
	 * @param id
	 *            unique ID
	 * @param fileURL
	 *            URL of the file
	 * @return the imported Graphstream-Graph
	 */
	public Graph readGraph(String id, final URL fileURL) {
		Graph g = new DefaultGraph(id);
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