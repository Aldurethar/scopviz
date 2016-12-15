package de.tu_darmstadt.informatik.tk.scopviz.ui;

import java.util.Iterator;

import org.graphstream.algorithm.Toolkit;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.ui.geom.Point3;

import de.tu_darmstadt.informatik.tk.scopviz.debug.Debug;
import de.tu_darmstadt.informatik.tk.scopviz.main.CreationMode;
import de.tu_darmstadt.informatik.tk.scopviz.main.GraphManager;
import de.tu_darmstadt.informatik.tk.scopviz.main.Layer;
import de.tu_darmstadt.informatik.tk.scopviz.main.Main;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

/**
 * Manager to contain the various handlers for the buttons of the UI.
 * 
 * @author Jan Enders (jan.enders@stud.tu-darmstadt.de)
 * @version 1.0
 *
 */
public class ButtonManager {

	/**
	 * Create more then one Edge at a time mode
	 */
	public static final Boolean CREATE_MORE_THEN_ONE = true;

	/**
	 * Handler for zoom in Button
	 */
	public static final EventHandler<ActionEvent> zoomInHandler = new EventHandler<ActionEvent>() {
		public void handle(ActionEvent evt) {
			Main.getInstance().getGraphManager().zoomIn();
		}
	};

	/**
	 * Handler for zoom out Button
	 */
	public static final EventHandler<ActionEvent> zoomOutHandler = new EventHandler<ActionEvent>() {
		public void handle(ActionEvent evt) {
			Main.getInstance().getGraphManager().zoomOut();
		}
	};

	/**
	 * Handler for clicks on the graph viewer.
	 */
	public static final EventHandler<MouseEvent> clickedHandler = new EventHandler<MouseEvent>() {

		/**
		 * Handle method gets called whenever a click is registered within the
		 * graph viewer
		 * 
		 * @param event
		 *            the click event that occurred to the graph viewer
		 */
		@Override
		public void handle(MouseEvent event) {
			GraphManager graphManager = Main.getInstance().getGraphManager();
			Graph graph = graphManager.getGraph();
			Point3 cursorPos = graphManager.getView().getCamera().transformPxToGu(event.getX(), event.getY());
			if (Main.getInstance().getCreationMode() == CreationMode.CREATE_NONE)
				Debug.out("(" + cursorPos.x + ", " + cursorPos.y + ")");
			Debug.out(getClosestEdge(cursorPos));
			Node n;

			// Create node based on creation Mode
			switch (Main.getInstance().getCreationMode()) {

			case CREATE_STANDARD_NODE:
				n = graph.addNode(Main.getInstance().getUnusedID());
				n.setAttribute("xyz", cursorPos);
				Debug.out("Added Node at Position (" + cursorPos.x + "/" + cursorPos.y + ")");

				break;

			case CREATE_SOURCE_NODE:
				n = graph.addNode(Main.getInstance().getUnusedID());
				n.setAttribute("xyz", cursorPos);
				n.setAttribute("ui.style", "fill-color: rgb(0, 0, 255);");
				Debug.out("Added Source Node at Position (" + cursorPos.x + "/" + cursorPos.y + ")");

				break;

			case CREATE_SINK_NODE:
				n = graph.addNode(Main.getInstance().getUnusedID());
				n.setAttribute("xyz", cursorPos);
				n.setAttribute("ui.style", "fill-color: rgb(255, 0, 0);");
				Debug.out("Added Sink Node at Position (" + cursorPos.x + "/" + cursorPos.y + ")");

				break;

			case CREATE_PROC_NODE:
				n = graph.addNode(Main.getInstance().getUnusedID());
				n.setAttribute("xyz", cursorPos);
				n.setAttribute("ui.style", "fill-color: rgb(0, 255, 0);");
				Debug.out("Added ProcEn Node at Position (" + cursorPos.x + "/" + cursorPos.y + ")");

				break;

			default:
				break;
			}

			PropertiesManager.setItemsProperties();

			if (!CREATE_MORE_THEN_ONE) {
				Main.getInstance().setCreationMode(CreationMode.CREATE_NONE);
			}
		}
	};

	public static Edge getClosestEdge(Point3 pos) {
		//TODO get a nice Max_Value, like 5% of maxWidth/maxHeight or something similar
		return getClosestEdge(pos, Double.MAX_VALUE);
	}

	public static Edge getClosestEdge(Point3 pos, double maxDistance) {
		//TODO clean up, relocate to ... somewhere useful
		double x0 = pos.x;
		double y0 = pos.y;
		GraphManager gm = Main.getInstance().getGraphManager();
		double dist = maxDistance;
		Edge result = null;
		for (Iterator<Edge> iterator = gm.getGraph().getEdgeIterator(); iterator.hasNext();) {
			Edge edge = (Edge) iterator.next();
			double[] n1 = Toolkit.nodePosition(edge.getNode0());
			double[] n2 = Toolkit.nodePosition(edge.getNode1());
			double x1 = n1[0];
			double y1 = n1[1];
			double x2 = n2[0];
			double y2 = n2[1];
			double a = Math.sqrt(Math.pow(y2 - y0, 2) + Math.pow(x2 - x0, 2));
			double b = Math.sqrt(Math.pow(y0 - y1, 2) + Math.pow(x0 - x1, 2));
			double c = Math.sqrt(Math.pow(y2 - y1, 2) + Math.pow(x2 - x1, 2));
			double cdist = Math.abs((y2 - y1) * x0 - (x2 - x1) * y0 + x2 * y1 - y2 * x1) / c;
			double alpha = Math.acos((Math.pow(b, 2) + Math.pow(c, 2) - Math.pow(a, 2)) / (2 * b * c));
			double beta = Math.acos((Math.pow(a, 2) + Math.pow(c, 2) - Math.pow(b, 2)) / (2 * a * c));
			if (cdist < dist && alpha <= Math.PI / 2 && beta <= Math.PI / 2) {
				Debug.out("" + alpha);
				dist = cdist;
				result = edge;
			}
		}
		return result;
	}

	public static final EventHandler<ActionEvent> underlayHandler = new EventHandler<ActionEvent>() {

		@Override
		public void handle(ActionEvent arg0) {
			GraphDisplayManager.setCurrentLayer(Layer.UNDERLAY);
			GraphDisplayManager.switchActiveGraph();
		}

	};

	public static final EventHandler<ActionEvent> operatorHandler = new EventHandler<ActionEvent>() {

		@Override
		public void handle(ActionEvent arg0) {
			GraphDisplayManager.setCurrentLayer(Layer.OPERATOR);
			GraphDisplayManager.switchActiveGraph();
		}

	};

	public static final EventHandler<ActionEvent> mappingHandler = new EventHandler<ActionEvent>() {

		@Override
		public void handle(ActionEvent arg0) {
			GraphDisplayManager.setCurrentLayer(Layer.MAPPING);
			GraphDisplayManager.switchActiveGraph();
		}

	};

	public static final EventHandler<ActionEvent> symbolRepHandler = new EventHandler<ActionEvent>() {

		@Override
		public void handle(ActionEvent arg0) {
			GraphDisplayManager.setCurrentLayer(Layer.SYMBOL);
			GraphDisplayManager.switchActiveGraph();
		}

	};

}
