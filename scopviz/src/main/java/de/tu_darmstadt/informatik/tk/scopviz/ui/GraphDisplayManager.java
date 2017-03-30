package de.tu_darmstadt.informatik.tk.scopviz.ui;

import java.awt.Dimension;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import org.apache.commons.math3.exception.NullArgumentException;

import de.tu_darmstadt.informatik.tk.scopviz.debug.Debug;
import de.tu_darmstadt.informatik.tk.scopviz.graphs.GraphHelper;
import de.tu_darmstadt.informatik.tk.scopviz.graphs.GraphManager;
import de.tu_darmstadt.informatik.tk.scopviz.graphs.MappingGraphManager;
import de.tu_darmstadt.informatik.tk.scopviz.graphs.MyGraph;
import de.tu_darmstadt.informatik.tk.scopviz.io.GraphMLImporter;
import de.tu_darmstadt.informatik.tk.scopviz.main.CreationMode;
import de.tu_darmstadt.informatik.tk.scopviz.main.Layer;
import de.tu_darmstadt.informatik.tk.scopviz.main.Main;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.EventHandler;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
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
	 * Observable boolean value, true when currentLayer = symbol layer, false
	 * otherwise
	 */
	private static BooleanProperty inSymbolLayer = new SimpleBooleanProperty();

	/**
	 * set inSymbolLayer to true
	 */
	private static final void changeToSymbolLayer() {
		inSymbolLayer.set(true);
	};

	/**
	 * set inSymbolLayer to false
	 */
	private static final void changeToOtherLayer() {
		inSymbolLayer.set(false);
	};

	/**
	 * 
	 * @return inSymbolLayer property
	 */
	public static BooleanProperty inSymbolLayerProperty() {
		return inSymbolLayer;
	};

	/**
	 * An empty GraphManager to use with Layers not yet filled with another
	 * GraphManager.
	 */
	private static final GraphManager emptyLayer = new GraphManager(new MyGraph("g"));

	/** Importer for loading Graphs from Disk. */
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
	public static MyGraph addGraph() {
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
	public static MyGraph addGraph(String fileName, boolean replaceCurrent) {
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
	public static MyGraph addGraph(Stage stage, boolean replaceCurrent) {
		String id = getGraphStringID(count);
		MyGraph g = importer.readGraph(id, stage);
		if (g == null) {
			return getGraphManager().getGraph();
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
	public static MyGraph addGraph(URL fileURL, boolean replaceCurrent) {
		String id = getGraphStringID(count);
		MyGraph g = importer.readGraph(id, fileURL);
		return addGraph(g, replaceCurrent);
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
	public static MyGraph addGraph(MyGraph g, boolean replaceCurrent) {
		if (g == null) {
			throw new NullArgumentException();
		}

		GraphManager v;
		// replacing the current graph or merging
		if (replaceCurrent) {
			v = new GraphManager(g);
			v.getGraph().addAttribute("layer", currentLayer);
			// set default values
			GraphHelper.setAllDefaults(g);
			v.getGraph().addAttribute("ui.antialias");
			removeAllCurrentGraphs();
			vList.add(v);
			count++;
			// set basic style
			v.setStylesheet();
		} else {
			v = new GraphManager(GraphHelper.newMerge(false, getGraphManager().getGraph(), g));
			v.getGraph().addAttribute("layer", currentLayer);
			g.addAttribute("layer", currentLayer);
			v.getGraph().addAttribute("ui.antialias");
			g.addAttribute("ui.antialias");
			removeAllCurrentGraphs();
			vList.add(v);
			count++;
			// set basic style
			v.setStylesheet();
		}

		// set ui.class
		v.convertUiClass();
		// display the graph
		switchActiveGraph();
		return g;
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
	 */
	public static void switchActiveGraph() {
		Pane pane = guiController.pane;
		Main.getInstance().getGraphManager().getView()
				.setPreferredSize(new Dimension((int) pane.getWidth() - 5, (int) pane.getHeight() - 5));
		guiController.swingNode.setContent(Main.getInstance().getGraphManager().getView());

		Main.getInstance().getGraphManager().setStylesheet();
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
			initMappingLayer(false);
		}
		GraphDisplayManager.currentLayer = currentLayer;

		if (currentLayer.equals(Layer.SYMBOL)) {
			changeToSymbolLayer();
		} else {
			changeToOtherLayer();
		}
	}

	/**
	 * Sets up the Mapping Layer.
	 */
	public static void initMappingLayer(boolean force) {
		GraphManager underlay = null, operator = null;
		MappingGraphManager mapping = null;
		for (GraphManager man : vList) {
			if (man.getGraph().getAttribute("layer").equals(Layer.UNDERLAY)) {
				underlay = man;
			} else if (man.getGraph().getAttribute("layer").equals(Layer.OPERATOR)) {
				operator = man;
			} else if (man.getGraph().getAttribute("layer").equals(Layer.MAPPING)
					&& MappingGraphManager.class.isInstance(man)) {
				mapping = (MappingGraphManager) man;
			}
		}
		if (underlay == null) {
			Debug.out("ERROR: no Underlay found", 3);
			return;
		}
		if (operator == null) {
			Debug.out("ERROR: no Operator found", 3);
			return;
		}
		if (mapping == null || !mapping.hasGraphManagerAsParent(underlay) || !mapping.hasGraphManagerAsParent(operator)
				|| force) {
			if (mapping == null)
				Debug.out("WARNING: no Mapping found", 2);
			else {
				Debug.out("WARNING: old Mapping found", 2);
				vList.remove(mapping);
			}
			MyGraph g;
			g = new MyGraph(getGraphStringID(count));
			count++;
			mapping = new MappingGraphManager(g, underlay, operator);
			g.addAttribute("layer", Layer.MAPPING);
			g.addAttribute("ui.antialias");
			mapping.setStylesheet();
			vList.add(mapping);

		}
		mapping.activated();
		switchActiveGraph();
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

	/**
	 * reads a Mapping Graph and sets the underlay, operator and mapping layers
	 * accordingly
	 */
	public static void readMapping() {
		// import the root Graph
		MyGraph g = null;
		GraphMLImporter reader = new GraphMLImporter();
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("open graph");
		ExtensionFilter standard = new ExtensionFilter("GraphML Mapping Files", "*.graphmlMap");
		fileChooser.getExtensionFilters().add(standard);
		fileChooser.getExtensionFilters().add(new ExtensionFilter("all Files", "*"));
		fileChooser.setSelectedExtensionFilter(standard);
		try {
			String fileName = fileChooser.showOpenDialog(Main.getInstance().getPrimaryStage()).getPath();
			Main.getInstance().getGraphManager().setCurrentPath(fileName);
			g = reader.readGraph(getGraphStringID(count++), fileName);
			g.getId();
		} catch (NullPointerException e) {
			Debug.out("INFORMATION: Mapping loading aborted", 1);
			return;
		}

		// splitting graphs
		// saving the layer for reuse later
		Layer tempLayer = currentLayer;

		// underlay graph
		LinkedList<MyGraph> graphs = g.getAllSubGraphs();
		Iterator<MyGraph> graphIter = graphs.iterator();
		while (graphIter.hasNext()) {
			if (!"UNDERLAY".equalsIgnoreCase(graphIter.next().getAttribute("layer"))) {
				graphIter.remove();
			}
		}
		MyGraph tempGraph = GraphHelper.newMerge(false, graphs.toArray(new MyGraph[0]));
		currentLayer = Layer.UNDERLAY;
		addGraph(tempGraph, true);
		GraphManager und = getGraphManager(Layer.UNDERLAY);

		// operator graph
		graphs = g.getAllSubGraphs();
		graphIter = graphs.iterator();
		while (graphIter.hasNext()) {
			if (!"OPERATOR".equalsIgnoreCase(graphIter.next().getAttribute("layer"))) {
				graphIter.remove();
			}
		}
		tempGraph = GraphHelper.newMerge(false, graphs.toArray(new MyGraph[0]));
		currentLayer = Layer.OPERATOR;
		addGraph(tempGraph, true);
		GraphManager op = getGraphManager(Layer.OPERATOR);

		// Mapping graph
		MyGraph moreTempGraph = new MyGraph(getGraphStringID(count));
		moreTempGraph.addAttribute("layer", Layer.MAPPING);
		MappingGraphManager map = new MappingGraphManager(moreTempGraph, und, op);
		count++;
		g.addAttribute("layer", Layer.MAPPING);
		g.addAttribute("ui.antialias");
		map.setStylesheet();
		currentLayer = Layer.MAPPING;
		removeAllCurrentGraphs();
		vList.add(map);
		map.loadGraph(g);
		currentLayer = tempLayer;
		switchActiveGraph();
	}

}