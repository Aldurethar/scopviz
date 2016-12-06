package de.tu_darmstadt.informatik.tk.scopviz.main;

import java.awt.Dimension;
import java.net.URL;
import java.util.ArrayList;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;

import de.tu_darmstadt.informatik.tk.scopviz.io.GraphMLImporter;
import de.tu_darmstadt.informatik.tk.scopviz.ui.GUIController;
import de.tu_darmstadt.informatik.tk.scopviz.ui.Visualizer;
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
public class GraphManager {
	private static final String GRAPH_STRING_ID_PREFIX = "graph";
	private static ArrayList<Visualizer> vList = new ArrayList<Visualizer>();
	private static int count = 0;
	private static GUIController guiController;
	private static int currentVisualizer = 0;
	private static Layer currentLayer = Layer.UNDERLAY;
	private final static Visualizer emptyLayer = new Visualizer(new SingleGraph("g"));

	public static void setGuiController(GUIController guiController) {
		GraphManager.guiController = guiController;
	}

	/**
	 * Adds an empty Graph to the collection.
	 * 
	 * @return the id to access the specific Graph
	 */
	public static int addGraph() {
		String id = getGraphStringID(count);
		Graph g = new MyGraph(id);
		Visualizer v = new Visualizer(g);
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
		Visualizer v = new Visualizer(g);
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
		Visualizer v = new Visualizer(g);
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
		Visualizer v = new Visualizer(g);
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
		Main.getInstance().getVisualizer().getView()
				.setPreferredSize(new Dimension((int) pane.getWidth() - 5, (int) pane.getHeight() - 5));
		guiController.swingNode.setContent(Main.getInstance().getVisualizer().getView());
		
		Main.getInstance().setCreateModus(CreateModus.CREATE_NONE);
	}

	/**
	 * get the current Visualizer. To get change it call
	 * {@link #switchActiveGraph(int)}
	 * 
	 * @return the current Visualizer
	 * @see #switchActiveGraph(int)
	 */
	public static Visualizer getCurrentVisualizer() {
		return vList.get(currentVisualizer);
	}

	public static Visualizer getVisualizer() {
		for (Visualizer viz : vList) {
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
		GraphManager.currentLayer = currentLayer;
	}
}