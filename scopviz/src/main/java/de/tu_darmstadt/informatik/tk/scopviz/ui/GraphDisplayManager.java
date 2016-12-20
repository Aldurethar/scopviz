package de.tu_darmstadt.informatik.tk.scopviz.ui;

import java.awt.Dimension;
import java.net.URL;
import java.util.ArrayList;

import org.apache.commons.math3.exception.NullArgumentException;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.view.Camera;

import de.tu_darmstadt.informatik.tk.scopviz.debug.Debug;
import de.tu_darmstadt.informatik.tk.scopviz.io.GraphMLImporter;
import de.tu_darmstadt.informatik.tk.scopviz.main.CreationMode;
import de.tu_darmstadt.informatik.tk.scopviz.main.GraphManager;
import de.tu_darmstadt.informatik.tk.scopviz.main.Layer;
import de.tu_darmstadt.informatik.tk.scopviz.main.Main;
import de.tu_darmstadt.informatik.tk.scopviz.main.MyGraph;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
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
	private static final String GRAPH_STRING_ID_PREFIX = "graph";

	private static Point3 oldMousePos;
	private static Point3 oldViewCenter;

	private static ArrayList<GraphManager> vList = new ArrayList<GraphManager>();
	private static int count = 0;
	private static GUIController guiController;
	private static int currentGraphManager = 0;
	private static Layer currentLayer = Layer.UNDERLAY;
	private final static GraphManager emptyLayer = new GraphManager(new SingleGraph("g"));

	private static GraphMLImporter importer = new GraphMLImporter();

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
		Graph g = importer.readGraph(id, Main.class.getResource(fileName));
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
		Graph g = importer.readGraph(id, stage);
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
		Graph g = importer.readGraph(id, fileURL);
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
	public static int addGraph(Graph g, boolean replaceCurrent) {
		if (g == null) {
			throw new NullArgumentException();
		}
		// create and format the GraphManager
		GraphManager v = new GraphManager(g);
		g.addAttribute("layer", currentLayer);
		v.setStylesheet(OptionsManager.DEFAULT_STYLESHEET);
		g.addAttribute("ui.antialias");

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

		// show the graph
		vList.add(v);
		switchActiveGraph();
		OptionsManager.adjustNodeGraphics(OptionsManager.getAllNodeGraphics()[0]);
		return ret;
	}

	private static void removeAllCurrentGraphs() {
		// TODO weird multithread behavior
		for (int i = 0; i < vList.size(); i++) {
			GraphManager man = vList.get(i);
			if (man.getGraph().getAttribute("layer").equals(currentLayer)) {
				vList.remove(i);
				count--;
			}
		}
	}

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
		Pane pane = guiController.pane;
		Main.getInstance().getGraphManager().getView()
				.setPreferredSize(new Dimension((int) pane.getWidth() - 5, (int) pane.getHeight() - 5));
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
		return vList.get(currentGraphManager);
	}

	public static GraphManager getGraphManager() {
		for (GraphManager man : vList) {
			if (man.getGraph().getAttribute("layer").equals(currentLayer)) {
				return man;
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

	public static final EventHandler<ScrollEvent> scrollHandler = new EventHandler<ScrollEvent>() {

		@Override
		public void handle(ScrollEvent event) {
			double deltaY = event.getDeltaY();
			getCurrentGraphManager().zoom(deltaY / -100);
		}

	};

	public static final EventHandler<MouseEvent> rememberLastClickedPosHandler = new EventHandler<MouseEvent>() {

		@Override
		public void handle(MouseEvent event) {
			Camera cam = getCurrentGraphManager().getView().getCamera();
			oldMousePos = cam.transformPxToGu(event.getSceneX(), event.getSceneY());
			oldViewCenter = getCurrentGraphManager().getView().getCamera().getViewCenter();
			Debug.out("Last mouse click position remembered: " + oldMousePos.x + "/" + oldMousePos.y);
		}
	};

	public static final EventHandler<MouseEvent> mouseDraggedHandler = new EventHandler<MouseEvent>() {

		@Override
		public void handle(MouseEvent event) {
			Camera cam = getCurrentGraphManager().getView().getCamera();
			Point3 newMousePos = cam.transformPxToGu(event.getSceneX(), event.getSceneY());
			double offsetX = oldMousePos.x - newMousePos.x;
			double offsetY = oldMousePos.y - newMousePos.y;
			double newX = oldViewCenter.x + offsetX;
			double newY = oldViewCenter.y + offsetY;
			Debug.out("Pan by " + offsetX + "/" + offsetY + ": Center moved from " + oldViewCenter.x + "/"
					+ oldViewCenter.y + " to " + newX + "/" + newY);
			cam.setViewCenter(newX, newY, oldViewCenter.z);
		}
	};
}