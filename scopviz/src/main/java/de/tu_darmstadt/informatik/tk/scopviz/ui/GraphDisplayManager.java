package de.tu_darmstadt.informatik.tk.scopviz.ui;

import java.awt.Dimension;
import java.net.URL;
import java.util.ArrayList;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;

import de.tu_darmstadt.informatik.tk.scopviz.io.GraphMLImporter;
import de.tu_darmstadt.informatik.tk.scopviz.main.CreationMode;
import de.tu_darmstadt.informatik.tk.scopviz.main.GraphManager;
import de.tu_darmstadt.informatik.tk.scopviz.main.Layer;
import de.tu_darmstadt.informatik.tk.scopviz.main.Main;
import de.tu_darmstadt.informatik.tk.scopviz.main.MyGraph;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * This class holds all Visualizers, provides Functions to add Graphs and get
 * corresponding Visualizers.
 * 
 * @author Matthias Wilhelm
 * @version 1.0
 *
 */
public class GraphDisplayManager {
	private static final GraphManager emptyLayer = new GraphManager(new SingleGraph("g"));
	private static final String GRAPH_STRING_ID_PREFIX = "graph";

	private static ArrayList<GraphManager> vList = new ArrayList<GraphManager>();
	private static Layer currentLayer = Layer.UNDERLAY;
	private static GUIController guiController;

	private static int currentVisualizer = 0;
	private static int count = 0;

	public static void setGuiController(GUIController guiController) {
		GraphDisplayManager.guiController = guiController;
	}

	/**
	 * Adds an empty Graph to the collection.
	 * 
	 * @return the id to access the specific Graph
	 */
	public static int addGraph() {
		String id = getGraphStringID(count);
		Graph g = new MyGraph(id);
		GraphManager v = new GraphManager(g);
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
	public static int addGraph(String fileName) {
		String id = getGraphStringID(count);
		GraphMLImporter importer = new GraphMLImporter();

		Graph g = importer.readGraph(id, Main.class.getResource(fileName));
		g.addAttribute("layer", currentLayer);

		GraphManager v = new GraphManager(g);
		vList.add(v);

		return count++;
	}

	/**
	 * Opens a Wizard and adds the chosen Graph to the collection.
	 * 
	 * @param stage
	 *            the root Window of the program
	 * @return the id to access the specific Graph
	 */
	public static int addGraph(Stage stage) {
		String id = getGraphStringID(count);
		GraphMLImporter importer = new GraphMLImporter();

		Graph g = importer.readGraph(id, stage);
		g.addAttribute("layer", currentLayer);

		GraphManager v = new GraphManager(g);
		vList.add(v);

		switchActiveGraph();

		return count++;
	}

	/**
	 * Imports and adds the specified Graph to the collection.
	 * 
	 * @param fileURL
	 *            URL of the file
	 * @return the id to access the specific Graph
	 */
	public static int addGraph(URL fileURL) {
		String id = getGraphStringID(count);
		GraphMLImporter importer = new GraphMLImporter();

		Graph g = importer.readGraph(id, fileURL);
		g.addAttribute("layer", currentLayer);

		GraphManager v = new GraphManager(g);
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
	/*
	 * public static Visualizer getVisualizer(int id) { return vList.get(id); }
	 */

	private static String getGraphStringID(int id) {
		return GRAPH_STRING_ID_PREFIX + id;
	}

	/**
	 * Switches the active Graph to the give id.
	 * 
	 * @param id
	 *            of the graph which to switch to
	 */

	public static void switchActiveGraph() {
		// TODO Clean up, is copied out the ResizeListener and should be handled
		// somewhere else
		Pane pane = guiController.pane;
		Dimension dim = new Dimension((int) pane.getWidth() - 5, (int) pane.getHeight() - 5);
		Main.getInstance().getGraphManager().getView().setPreferredSize(dim);

		guiController.swingNode.setContent(Main.getInstance().getGraphManager().getView());

		Main.getInstance().setCreationMode(CreationMode.CREATE_NONE);
	}

	/**
	 * get the current Visualizer. To get change it call
	 * {@link #switchActiveGraph(int)}
	 * 
	 * @return the current Visualizer
	 * @see #switchActiveGraph(int)
	 */
	public static GraphManager getCurrentGraphManager() {
		return vList.get(currentVisualizer);
	}

	public static GraphManager getGraphManager() {
		for (GraphManager viz : vList) {
			if (viz.getGraph().getAttribute("layer").equals(currentLayer)) {
				return viz;
			}
		}
		return emptyLayer;
	}

	public static Layer getCurrentLayer() {
		return currentLayer;
	}

	public static void setCurrentLayer(Layer currentLayer) {
		GraphDisplayManager.currentLayer = currentLayer;
	}
}