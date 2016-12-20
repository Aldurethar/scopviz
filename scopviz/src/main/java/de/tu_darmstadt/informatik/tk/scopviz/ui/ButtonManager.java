package de.tu_darmstadt.informatik.tk.scopviz.ui;

import java.util.ArrayList;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.ui.geom.Point3;

import de.tu_darmstadt.informatik.tk.scopviz.debug.Debug;
import de.tu_darmstadt.informatik.tk.scopviz.main.AuxilFunctions;
import de.tu_darmstadt.informatik.tk.scopviz.main.CreationMode;
import de.tu_darmstadt.informatik.tk.scopviz.main.GraphManager;
import de.tu_darmstadt.informatik.tk.scopviz.main.Layer;
import de.tu_darmstadt.informatik.tk.scopviz.main.Main;
import de.tu_darmstadt.informatik.tk.scopviz.main.SelectionMode;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Skin;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;

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

	// Test
	private static GUIController guiController;
	private static ArrayList<Button> list;

	public static void setGuiController(GUIController guiController, ArrayList<Button> nList) {
		ButtonManager.guiController = guiController;
		list = nList;
	}

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
			Node n;
			Edge selectedEdge = AuxilFunctions.getClosestEdge(cursorPos);
			if (Main.getInstance().getCreationMode().equals(CreationMode.CREATE_NONE) && Main.getInstance().getSelectionMode() == SelectionMode.SELECT_EDGES && selectedEdge != null) {
				Main.getInstance().getGraphManager().selectEdge(selectedEdge.getId());
			}

			// Create node based on creation Mode
			switch (Main.getInstance().getCreationMode()) {

			case CREATE_STANDARD_NODE:
				n = graph.addNode(Main.getInstance().getUnusedID());
				n.setAttribute("xyz", cursorPos);
				n.setAttribute("ui.class", "standard");
				Debug.out("Added Node at Position (" + cursorPos.x + "/" + cursorPos.y + ")");

				break;

			case CREATE_SOURCE_NODE:
				n = graph.addNode(Main.getInstance().getUnusedID());
				n.setAttribute("xyz", cursorPos);
				n.setAttribute("ui.class", "source");
				Debug.out("Added Source Node at Position (" + cursorPos.x + "/" + cursorPos.y + ")");

				break;

			case CREATE_SINK_NODE:
				n = graph.addNode(Main.getInstance().getUnusedID());
				n.setAttribute("xyz", cursorPos);
				n.setAttribute("ui.class", "sink");
				Debug.out("Added Sink Node at Position (" + cursorPos.x + "/" + cursorPos.y + ")");

				break;

			case CREATE_PROC_NODE:
				n = graph.addNode(Main.getInstance().getUnusedID());
				n.setAttribute("xyz", cursorPos);
				n.setAttribute("ui.class", "procEn");
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

	public static final EventHandler<ActionEvent> underlayHandler = new EventHandler<ActionEvent>() {

		@Override
		public void handle(ActionEvent arg0) {
			GraphDisplayManager.setCurrentLayer(Layer.UNDERLAY);
			GraphDisplayManager.switchActiveGraph();

			setBorderStyle((Button) arg0.getSource());
		}

	};

	public static final EventHandler<ActionEvent> operatorHandler = new EventHandler<ActionEvent>() {

		@Override
		public void handle(ActionEvent arg0) {
			GraphDisplayManager.setCurrentLayer(Layer.OPERATOR);
			GraphDisplayManager.switchActiveGraph();
			
			

			setBorderStyle((Button) arg0.getSource());
		}

	};

	public static final EventHandler<ActionEvent> mappingHandler = new EventHandler<ActionEvent>() {

		@Override
		public void handle(ActionEvent arg0) {
			GraphDisplayManager.setCurrentLayer(Layer.MAPPING);
			GraphDisplayManager.switchActiveGraph();
			
			setBorderStyle((Button) arg0.getSource());
		}

	};

	public static final EventHandler<ActionEvent> symbolRepHandler = new EventHandler<ActionEvent>() {

		@Override
		public void handle(ActionEvent arg0) {
			GraphDisplayManager.setCurrentLayer(Layer.SYMBOL);
			GraphDisplayManager.switchActiveGraph();
			
			setBorderStyle((Button) arg0.getSource());
		}

	};

		
	/**
	 * Changes the border of the button that was pressed to red
	 * @param currentButton the button that was pressed
	 */
	private static void setBorderStyle(Button currentButton) {

		for (Button j : list) {
			if (j.equals(currentButton)) {
				j.setStyle(
						"-fx-background-color: red, red, red, -fx-faint-focus-color, -fx-body-color; -fx-background-insets: -0.2, 1, 2, -1.4, 2.6; -fx-background-radius: 3, 2, 1, 4, 1;");
			} else {
				j.setStyle("-fx-border-width: 0;");
			}

		}
	}

}
