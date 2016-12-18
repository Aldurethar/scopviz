package de.tu_darmstadt.informatik.tk.scopviz.io;

import java.io.IOException;
import java.net.URL;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceGraphML;

import de.tu_darmstadt.informatik.tk.scopviz.main.Main;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * Importer to import a graph from a GraphML file and return it as a Graph
 * object.
 * 
 * @author Jascha Bohne
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
	 * Imports a GraphML file. Opens a open dialog. Returns null if the process
	 * is aborted.
	 * 
	 * @param stage
	 *            the parent window of the open file window
	 * @return the imported Graphstream-Graph
	 */
	public Graph readGraph(final String id, final Stage stage) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("open graph");
		try {
			String fileName = fileChooser.showOpenDialog(stage).getPath();
			Main.getInstance().getGraphManager().setCurrentPath(fileName);
			return readGraph(id, fileName);
		} catch (NullPointerException e) {
			return null;
		}
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