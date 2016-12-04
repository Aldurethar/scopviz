package de.tu_darmstadt.informatik.tk.scopviz.main;

import java.net.URL;
import java.util.ArrayList;

import javax.swing.JPanel;

import org.graphstream.graph.Graph;

import de.tu_darmstadt.informatik.tk.scopviz.debug.Debug;
import de.tu_darmstadt.informatik.tk.scopviz.io.GraphMLImporter;
import de.tu_darmstadt.informatik.tk.scopviz.ui.GUIController;
import de.tu_darmstadt.informatik.tk.scopviz.ui.Visualizer;
import javafx.embed.swing.SwingNode;
import javafx.scene.layout.Pane;

/**
 * Class holds all Graphs and Functions to interact with them.
 * 
 * @author Matthias Wilhelm
 * @version 1.0
 *
 */
public class GraphManager {
	private static final String GRAPH_STRING_ID_PREFIX = "graph";
	ArrayList<Graph> gList;
	ArrayList<Visualizer> vList;
	private static GraphManager instance;
	private int count;
	private static GUIController guiController;
	private int currentVisualizer = 0; 

	public static void setGuiController(GUIController guiController) {
		GraphManager.guiController = guiController;
	}

	private GraphManager() {
		count = 0;
		gList = new ArrayList<Graph>();
		vList = new ArrayList<Visualizer>();
	}

	/**
	 * Returns the singular instance of the Class, grants access to the
	 * Singleton. Initializes the instance when called for the first time.
	 * 
	 * @return the singular instance of the class
	 */
	public static GraphManager getInstance() {
		if (instance == null) {
			instance = new GraphManager();
		}
		return instance;
	}

	/**
	 * Adds an empty Graph to the collection.
	 * 
	 * @return the id to access the specific Graph
	 */
	public int addGraph() {
		String id = getGraphStringID(count);
		Graph g = new MyGraph(id);
		Visualizer v = new Visualizer(g);
		gList.add(g);
		vList.add(v);
		return ++count;
	}

	/**
	 * Imports and adds the specified Graph to the collection.
	 * 
	 * @param fileName
	 *            path to the file on disk
	 * @return the id to access the specific Graph
	 */
	public int addGraph(String fileName) {
		String id = getGraphStringID(count);
		GraphMLImporter importer = new GraphMLImporter();
		Graph g = importer.readGraph(id, Main.class.getResource(fileName));
		Visualizer v = new Visualizer(g);
		gList.add(g);
		vList.add(v);
		return count++;
	}

	/**
	 * Imports and adds the specified Graph to the collection.
	 * 
	 * @param fileURL
	 *            URL of the file
	 * @return the id to access the specific Graph
	 */
	public int addGraph(URL fileURL) {
		String id = getGraphStringID(count);
		GraphMLImporter importer = new GraphMLImporter();
		Graph g = importer.readGraph(id, fileURL);
		Visualizer v = new Visualizer(g);
		gList.add(g);
		vList.add(v);
		return ++count;
	}

	/**
	 * Returns the Visualizer for the given graph id
	 * 
	 * @param id
	 *            of the graph
	 * @return visualizer for the graph
	 */
	public Visualizer getVisualizer(int id) {
		return vList.get(id);
	}

	private String getGraphStringID(int id) {
		return GRAPH_STRING_ID_PREFIX + id;
	}

	public void switchActiveGraph(int id) {
		currentVisualizer = id;
		guiController.swingNode.setContent((JPanel) vList.get(id).getView());
	}
	
	public Visualizer getCurrentVisualizer() {		
		return vList.get(currentVisualizer);
	}
}