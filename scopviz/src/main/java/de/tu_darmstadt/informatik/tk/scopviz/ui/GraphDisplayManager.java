package de.tu_darmstadt.informatik.tk.scopviz.ui;

import java.awt.Dimension;
import java.net.URL;
import java.util.ArrayList;

import org.apache.commons.math3.exception.NullArgumentException;

import de.tu_darmstadt.informatik.tk.scopviz.debug.Debug;
import de.tu_darmstadt.informatik.tk.scopviz.io.GraphMLImporter;
import de.tu_darmstadt.informatik.tk.scopviz.main.CreationMode;
import de.tu_darmstadt.informatik.tk.scopviz.main.GraphManager;
import de.tu_darmstadt.informatik.tk.scopviz.main.Layer;
import de.tu_darmstadt.informatik.tk.scopviz.main.Main;
import de.tu_darmstadt.informatik.tk.scopviz.main.MappingGraphManager;
import de.tu_darmstadt.informatik.tk.scopviz.main.MyGraph;
import javafx.event.EventHandler;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * This class holds all GraphManagers, provides Functions to add Graphs and get
 * corresponding GraphManagers.
 * 
 * @author Matthias Wilhelm
 * @version 1.1
 *
 */
public final class GraphDisplayManager {

	/** Prefix to add to the Name of the Graphs. */
	private static final String GRAPH_STRING_ID_PREFIX = "graph";

	/** A List of all GraphManagers managed by this class. */
	private static ArrayList<GraphManager> vList = new ArrayList<GraphManager>();

	/** The number of GraphManagers currently being managed. */
	private static int count = 0;

	/** Reference to the GUI Controller for Access to UI Elements. */
	private static GUIController guiController;

	/** The number of the currently used GraphManager. */
	private static int currentGraphManager = 0;

	/** The currently active Layer. */
	private static Layer currentLayer = Layer.UNDERLAY;

	/**
	 * An empty GraphManager to use with Layers not yet filled with another
	 * GraphManager.
	 */
	private final static GraphManager emptyLayer = new GraphManager(new MyGraph("g"));

	/** Importer for loading Graphs from Disk */
	private static GraphMLImporter importer = new GraphMLImporter();

	/**
	 * Private Constructor to prevent initialization.
	 */
	private GraphDisplayManager() {
	}

	/**
	 * Sets the Reference to the GUI Controller to use for accessing UI
	 * Elements.
	 * 
	 * @param guiController
	 *            a Reference to the GUI Controller
	 */
	public static void init(GUIController guiController) {
		GraphDisplayManager.guiController = guiController;

		addGraph();
		currentLayer = Layer.OPERATOR;
		addGraph();
		/*
		 * currentLayer = Layer.MAPPING; addGraph();
		 */
		currentLayer = Layer.SYMBOL;
		addGraph();
		currentLayer = Layer.UNDERLAY;
	}

	/**
	 * Adds an empty Graph to the collection.
	 * 
	 * @return the id to access the specific Graph
	 */
	public static int addGraph() {
		String id = getGraphStringID(count);
		MyGraph g = new MyGraph(id);
		return addGraph(g, true);
	}

	/**
	 * Imports and adds the specified Graph to the collection.
	 * 
	 * @param fileName
	 *            name of the file on disk uses System.getResource to get the
	 *            file
	 * @param replaceCurrent
	 *            if true the given graph will replace any preexisting graph in
	 *            the current layer, if false they will be merged.
	 * @return the id to access the specific Graph
	 */
	public static int addGraph(String fileName, boolean replaceCurrent) {
		String id = getGraphStringID(count);
		MyGraph g = importer.readGraph(id, Main.class.getResource(fileName));
		return addGraph(g, replaceCurrent);
	}

	/**
	 * Opens a Wizard and adds the chosen Graph to the collection.
	 * 
	 * @param stage
	 *            the root Window of the program
	 * @param replaceCurrent
	 *            if true the given graph will replace any preexisting graph in
	 *            the current layer, if false they will be merged.
	 * @return the id to access the specific Graph
	 */
	public static int addGraph(Stage stage, boolean replaceCurrent) {
		String id = getGraphStringID(count);
		MyGraph g = importer.readGraph(id, stage);
		if (g == null) {
			return currentGraphManager;
		}
		return addGraph(g, replaceCurrent);
	}

	/**
	 * Imports and adds the specified Graph to the collection.
	 * 
	 * @param fileURL
	 *            URL of the file
	 * @param replaceCurrent
	 *            if true the given graph will replace any preexisting graph in
	 *            the current layer, if false they will be merged.
	 * @return the id to access the specific Graph
	 */
	public static int addGraph(URL fileURL, boolean currentLayer) {
		String id = getGraphStringID(count);
		MyGraph g = importer.readGraph(id, fileURL);
		return addGraph(g, currentLayer);
	}

	/**
	 * Adds the graph to the collection. All other addGraph() functions call
	 * this one internally.
	 * 
	 * @param g
	 *            the Graph that should be added to the collection
	 * @param replaceCurrent
	 *            if true the given graph will replace any preexisting graph in
	 *            the current layer, if false they will be merged.
	 * @return the id to access the specific graph
	 */
	public static int addGraph(MyGraph g, boolean replaceCurrent) {
		if (g == null) {
			throw new NullArgumentException();
		}
		// create and format the GraphManager
		GraphManager v = new GraphManager(g);
		g.addAttribute("layer", currentLayer);

		int ret = 0;
		// replacing the current graph or merging
		if (replaceCurrent) {
			removeAllCurrentGraphs();
			;
			ret = count++;
		} else {
			// TODO add code for multigraph
			// return theIdOfTheMergedGraph;
		}

		// set ui.class
		v.convertUiClass();
		// set basic style
		v.setStylesheet(StylesheetManager.DEFAULT_STYLESHEET);
		// display the graph
		vList.add(v);
		switchActiveGraph();
		return ret;
	}

	/**
	 * Removes all GraphManagers from the current Layer.
	 */
	private static void removeAllCurrentGraphs() {
		// TODO weird multithread behavior, count auskommentier fuer matthias
		for (int i = 0; i < vList.size(); i++) {
			GraphManager man = vList.get(i);
			if (man.getGraph().getAttribute("layer").equals(currentLayer)) {
				vList.remove(i);
				// count--;
			}
		}
	}

	/**
	 * Returns a Graph Id to have a name for Graphs.
	 * 
	 * @param id
	 *            the number of the Graph
	 * @return the new String ID
	 */
	private static String getGraphStringID(int id) {
		return GRAPH_STRING_ID_PREFIX + id;
	}

	/**
	 * Switches the active Graph to the one with the given id.
	 * 
	 * @param id
	 *            the id of the graph to switch to
	 */
	public static void switchActiveGraph() {
		Pane pane = guiController.pane;
		Main.getInstance().getGraphManager().getView()
				.setPreferredSize(new Dimension((int) pane.getWidth() - 5, (int) pane.getHeight() - 5));
		guiController.swingNode.setContent(Main.getInstance().getGraphManager().getView());

		Main.getInstance().getGraphManager().updateStylesheet();
		Main.getInstance().setCreationMode(CreationMode.CREATE_NONE);
	}

	/**
	 * Returns a reference to the current Visualizer. To change it call
	 * {@link #switchActiveGraph(int)}
	 * 
	 * @return the current Visualizer
	 * @see #switchActiveGraph(int)
	 */
	public static GraphManager getCurrentGraphManager() {
		return vList.get(currentGraphManager);
	}

	/**
	 * Returns the GraphManager for the current Layer.
	 * 
	 * @return the GraphManager for the current Layer, or an empty GraphManager
	 *         if none is found
	 */
	public static GraphManager getGraphManager() {
		for (GraphManager man : vList) {
			if (man.getGraph().getAttribute("layer").equals(currentLayer)) {
				return man;
			}
		}
		return emptyLayer;
	}

	/**
	 * Returns the GraphManager for the given layer.
	 * 
	 * @param l
	 *            the given layer
	 * @return the GraphManager for the given Layer, or an empty GraphManager if
	 *         none is found
	 */
	public static GraphManager getGraphManager(Layer l) {
		for (GraphManager man : vList) {
			if (man.getGraph().getAttribute("layer").equals(l)) {
				return man;
			}
		}
		return emptyLayer;
	}

	/**
	 * Returns the currently active Layer.
	 * 
	 * @return the currently active Layer.
	 */
	public static Layer getCurrentLayer() {
		return currentLayer;
	}

	/**
	 * Sets the active Layer to a given one.
	 * 
	 * @param currentLayer
	 *            the layer to switch to
	 */
	public static void setCurrentLayer(Layer currentLayer) {
		if (currentLayer.equals(Layer.MAPPING)) {
			initMappingLayer();
		}
		GraphDisplayManager.currentLayer = currentLayer;
	}

	/**
	 * Sets up the Mapping Layer.
	 */
	private static void initMappingLayer() {
		GraphManager underlay = null, operator = null;
		MappingGraphManager mapping = null;
		for (GraphManager man : vList) {
			if (man.getGraph().getAttribute("layer").equals(Layer.UNDERLAY))
				underlay = man;
			else if (man.getGraph().getAttribute("layer").equals(Layer.OPERATOR))
				operator = man;
			else if (man.getGraph().getAttribute("layer").equals(Layer.MAPPING)
					&& MappingGraphManager.class.isInstance(man))
				mapping = (MappingGraphManager) man;
		}
		if (underlay == null) {
			Debug.out("no Underlay found");
			return;
		}
		if (operator == null) {
			Debug.out("no Operator found");
			return;
		}
		if (mapping == null || !mapping.hasGraphManagerAsParent(underlay)
				|| !mapping.hasGraphManagerAsParent(operator)) {
			Debug.out("no Mapping found");
			MyGraph g;
			g = new MyGraph(getGraphStringID(count));
			count++;
			mapping = new MappingGraphManager(g, underlay, operator);
			g.addAttribute("layer", Layer.MAPPING);
			g.addAttribute("ui.antialias");
			mapping.setStylesheet(StylesheetManager.DEFAULT_STYLESHEET);
			vList.add(mapping);

			underlay.addEdgeCreatedListener(mapping);
			underlay.addNodeCreatedListener(mapping);
			operator.addEdgeCreatedListener(mapping);
			operator.addNodeCreatedListener(mapping);
		}
		mapping.activated();
	}

	/**
	 * Handler for Scrolling while the Mouse is over the Graph Display Window.
	 */
	public static final EventHandler<ScrollEvent> scrollHandler = new EventHandler<ScrollEvent>() {

		@Override
		public void handle(ScrollEvent event) {
			double deltaY = event.getDeltaY();
			Main.getInstance().getGraphManager().zoom(deltaY / -100);
		}

	};

}