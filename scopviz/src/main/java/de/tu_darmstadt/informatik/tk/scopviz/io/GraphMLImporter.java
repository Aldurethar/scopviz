package de.tu_darmstadt.informatik.tk.scopviz.io;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;

import org.graphstream.graph.Node;

import de.tu_darmstadt.informatik.tk.scopviz.debug.Debug;
import de.tu_darmstadt.informatik.tk.scopviz.graphs.GraphHelper;
import de.tu_darmstadt.informatik.tk.scopviz.graphs.MyGraph;
import de.tu_darmstadt.informatik.tk.scopviz.main.Layer;
import de.tu_darmstadt.informatik.tk.scopviz.main.Main;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
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
			if (fileName.contains("shutdown.graphml")) {
				return new MyGraph((fileName.split(".")[0]));
			}
			System.out.println("GraphML File doesn't exist or can't be opened");
			e.printStackTrace();
		}
		if (fs.wasMultiGraph()) {
			for(MyGraph gSub : fs.getSubGraphs()){
				if("UNDERLAY".equals(gSub.getAttribute("layer"))){
					gSub.removeAttribute("layer");
					gSub.addAttribute("layer", Layer.UNDERLAY);
				}else if("OPERATOR".equals(gSub.getAttribute("layer"))){
					gSub.removeAttribute("layer");
					gSub.addAttribute("layer", Layer.OPERATOR);
				}else if("MAPPING".equals(gSub.getAttribute("layer"))){
					gSub.removeAttribute("layer");
					gSub.addAttribute("layer", Layer.MAPPING);
				}else if("SYMBOL".equals(gSub.getAttribute("layer"))){
					gSub.removeAttribute("layer");
					gSub.addAttribute("layer", Layer.SYMBOL);
				}
			}
			g = GraphHelper.newMerge(false, fs.getSubGraphs().toArray(new MyGraph[0]));
		}
		fs.removeSink(g);
		for (Node n : g.getNodeSet()) {
			n.removeAttribute("ui.class");
		}
		yEdConversion(g);
		return g;
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
		ExtensionFilter standard = new ExtensionFilter("GraphML Files", "*.graphml");
		fileChooser.getExtensionFilters().add(standard);
		fileChooser.getExtensionFilters().add(new ExtensionFilter("all Files", "*"));
		fileChooser.setSelectedExtensionFilter(standard);
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
			Debug.out("GraphML File doesn't exist or can't be opened");
			e.printStackTrace();
		}
		fs.removeSink(g);
		for (Node n : g.getNodeSet()) {
			n.removeAttribute("ui.class");
		}
		yEdConversion(g);
		return g;
	}

	/**
	 * Returns a List of all the Subgraphs within the FileSource.
	 * 
	 * @return the list of subgraphs
	 */
	public LinkedList<MyGraph> subGraphs() {
		return fs.getSubGraphs();
	}

	public void yEdConversion(MyGraph g) {
		for (Node n : g.getNodeSet()) {
			// yed conversion
			if ((!n.hasAttribute("ui.label") || n.getAttribute("ui.label").equals("")) && n.hasAttribute("yEd.label")) {
				n.addAttribute("ui.label", n.getAttribute("yEd.label").toString());
				n.removeAttribute("yEd.label");
			} else if (n.hasAttribute("ui.label")) {
				n.removeAttribute("yEd.label");
			}
			if (n.hasAttribute("yEd.x") && !n.getAttribute("yEd.x").equals("")) {
				n.addAttribute("x", Main.getInstance().convertAttributeTypes(n.getAttribute("yEd.x"), new Double(0.0)));
				n.removeAttribute("yEd.x");
			} else {
				n.removeAttribute("yEd.x");
			}
			if (n.hasAttribute("yEd.y") && !n.getAttribute("yEd.y").equals("")) {
				n.addAttribute("y", Main.getInstance().convertAttributeTypes(n.getAttribute("yEd.y"), new Double(0.0)));
				n.removeAttribute("yEd.y");
			} else {
				n.removeAttribute("yEd.y");
			}
		}
	}
}