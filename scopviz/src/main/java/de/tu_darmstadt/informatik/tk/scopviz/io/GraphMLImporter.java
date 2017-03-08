package de.tu_darmstadt.informatik.tk.scopviz.io;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;

import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

import de.tu_darmstadt.informatik.tk.scopviz.debug.Debug;
import de.tu_darmstadt.informatik.tk.scopviz.main.Main;
import de.tu_darmstadt.informatik.tk.scopviz.main.MyGraph;
import de.tu_darmstadt.informatik.tk.scopviz.ui.OptionsManager;
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
	 * Filesource from which to read the Graph Data.
	 */
	private MyFileSourceGraphML fs = new MyFileSourceGraphML();

	/**
	 * Imports a GraphML file.
	 * 
	 * @param id
	 *            unique ID
	 * @param fileName
	 *            path to the file on disk
	 * @return the imported Graphstream-Graph
	 */
	public MyGraph readGraph(String id, final String fileName) {
		MyGraph g = new MyGraph(id);
		fs.addSink(g);
		try {
			fs.readAll(fileName);
		} catch (IOException e) {
			System.out.println("GraphML File doesn't exist or can't be opened");
			e.printStackTrace();
		}
		fs.removeSink(g);
		handleAttributes(g);
		return g;
	}

	/**
	 * adds default values for typeofNode and typeofDevice to all Nodes 
	 * and converts yEd attributes to regular ones
	 * 
	 * @param g
	 *            the graph that the attributes will be added onto
	 */
	private void handleAttributes(MyGraph g) {
		for (Node n : g.getNodeSet()) {
			if (!n.hasAttribute("typeofNode")) {
				n.addAttribute("typeofNode", "standard");
			}
			if (!n.hasAttribute("typeofDevice")) {
				n.addAttribute("typeofDevice", "unknown");
			}
			if (!n.hasAttribute("lat")) {
				n.addAttribute("lat", OptionsManager.getDefaultLat());
			}
			if (!n.hasAttribute("long")) {
				n.addAttribute("long", OptionsManager.getDefaultLong());
			}
			if (!n.hasAttribute("ui.label") && n.hasAttribute("yEd.label")){
				n.addAttribute("ui.label", n.getAttribute("yEd.label").toString());
				n.removeAttribute("yEd.label");
			}
			if (n.hasAttribute("yEd.x")){	
				n.addAttribute("x", Double.parseDouble(n.getAttribute("yEd.x").toString()));
				n.removeAttribute("yEd.x");
			}
			if (n.hasAttribute("yEd.y")){	
				n.addAttribute("y", Double.parseDouble(n.getAttribute("yEd.y").toString()));
				n.removeAttribute("yEd.y");
			}
		}
	}

	/**
	 * Imports a GraphML file. Opens a open dialog. Returns null if the process
	 * is aborted.
	 * 
	 * @param id
	 *            the id to use for the new Graph
	 * @param stage
	 *            the parent window of the open file window
	 * @return the imported Graphstream-Graph
	 */
	public MyGraph readGraph(final String id, final Stage stage) {
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
	// TODO backup reader/Exception handling
	public MyGraph readGraph(String id, final URL fileURL) {
		MyGraph g = new MyGraph(id);
		fs.addSink(g);
		try {
			fs.readAll(fileURL);
		} catch (IOException e) {
			System.out.println("GraphML File doesn't exist or can't be opened");
			e.printStackTrace();
		}
		fs.removeSink(g);
		handleAttributes(g);
		Debug.out(g.getId());
		return g;
	}

	/**
	 * Returns a List of all the Subgraphs within the FileSource.
	 * 
	 * @return the list of subgraphs
	 */
	public LinkedList<SingleGraph> subGraphs() {
		return fs.getSubGraphs();
	}
}