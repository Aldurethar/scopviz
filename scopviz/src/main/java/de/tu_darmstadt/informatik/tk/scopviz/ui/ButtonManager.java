package de.tu_darmstadt.informatik.tk.scopviz.ui;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.ui.geom.Point3;

import de.tu_darmstadt.informatik.tk.scopviz.debug.Debug;
import de.tu_darmstadt.informatik.tk.scopviz.main.GraphManager;
import de.tu_darmstadt.informatik.tk.scopviz.main.Layer;
import de.tu_darmstadt.informatik.tk.scopviz.main.Main;
import de.tu_darmstadt.informatik.tk.scopviz.main.Modus;
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
	 * Reference to the GUIController used by the app for access to UI Elements.
	 */
	private static GUIController guiController;

	/**
	 * Initializes the ButtonManager by getting access to the GUIController.
	 * 
	 * @param guiCon
	 *            a reference to the GUIController used by the App
	 */
	public static void initialize(GUIController guiCon) {
		guiController = guiCon;
	}

	/**
	 * Handler for the "Create Node" button.
	 */
	public static EventHandler<ActionEvent> createNodeHandler = new EventHandler<ActionEvent>() {

		/**
		 * Handle method gets called when the button is pressed.
		 * 
		 * @param arg0
		 *            the event that occurred to the button
		 */
		@Override
		public void handle(ActionEvent arg0) {
			switch (Main.getInstance().getModus()) {
			// end create node mode when the button is clicked while in create
			// node mode
			case CREATE_NODE:
				Main.getInstance().setModus(Modus.NORMAL);
				Debug.out("Modus set to Normal");
				guiController.createNode.setText("Knoten hinzufügen");
				break;
			// enter create node mode when the button is clicked while in normal
			// mode
			case NORMAL:
				Main.getInstance().setModus(Modus.CREATE_NODE);
				Debug.out("Modus set to Create Node");
				guiController.createNode.setText("Ende");
				break;
			// enter create node mode when button is clicked in any other
			// situation
			default:
				Main.getInstance().setModus(Modus.CREATE_NODE);
				Debug.out("Modus set to Create Node");
				guiController.createNode.setText("Ende");
				guiController.createEdge.setText("Kante hinzufügen");
				Main.getInstance().getVisualizer().deselect();
				break;
			}
		}
	};

	/**
	 * Handler for the "Create Edge" button.
	 */
	public static EventHandler<ActionEvent> createEdgeHandler = new EventHandler<ActionEvent>() {

		/**
		 * Handle method gets called when the button is pressed.
		 * 
		 * @param arg0
		 *            the event that occurred to the button
		 */
		@Override
		public void handle(ActionEvent arg0) {
			// Deselect any previously selected nodes or edges
			Main.getInstance().getVisualizer().deselect();
			switch (Main.getInstance().getModus()) {
			// end create edge mode when the button is clicked in create edge
			// mode
			case CREATE_EDGE:
				Main.getInstance().setModus(Modus.NORMAL);
				Debug.out("Modus set to Normal");
				guiController.createEdge.setText("Kante hinzufügen");
				break;
			// enter create edge mode when button is clicked in normal mode
			case NORMAL:
				Main.getInstance().setModus(Modus.CREATE_EDGE);
				Debug.out("Modus set to Create Edge");
				guiController.createEdge.setText("Ende");
				break;
			// enter create edge mode when button is clicked in any other
			// situation
			default:
				Main.getInstance().setModus(Modus.CREATE_EDGE);
				Debug.out("Modus set to Create Edge");
				guiController.createEdge.setText("Ende");
				guiController.createNode.setText("Knoten hinzufügen");
				break;
			}
		}
	};

	public static EventHandler<ActionEvent> zoomInHandler = new EventHandler<ActionEvent>() {
		public void handle(ActionEvent evt) {
			Main.getInstance().getVisualizer().zoomIn();
		}
	};

	public static EventHandler<ActionEvent> zoomOutHandler = new EventHandler<ActionEvent>() {
		public void handle(ActionEvent evt) {
			Main.getInstance().getVisualizer().zoomOut();
		}
	};

	/**
	 * Handler for clicks on the graph viewer.
	 */
	public static EventHandler<MouseEvent> clickedHandler = new EventHandler<MouseEvent>() {

		/**
		 * Handle method gets called whenever a click is registered within the
		 * graph viewer
		 * 
		 * @param event
		 *            the click event that occurred to the graph viewer
		 */
		@Override
		public void handle(MouseEvent event) {
			Visualizer visualizer = Main.getInstance().getVisualizer();
			Modus currentMod = Main.getInstance().getModus();
			Graph graph = visualizer.getGraph();
			Point3 cursorPos = visualizer.getView().getCamera().transformPxToGu(event.getX(), event.getY());
			if (currentMod == Modus.CREATE_NODE) {
				Node n = graph.addNode(Main.getInstance().getUnusedID());
				n.setAttribute("xyz", cursorPos);
				Debug.out("Added Node at Position (" + cursorPos.x + "/" + cursorPos.y + ")");
			}
		}
	};
	
	
	
	
	public static EventHandler<ActionEvent> underlayHandler = new EventHandler<ActionEvent>(){

		@Override
		public void handle(ActionEvent arg0) {
			GraphManager.setCurrentLayer(Layer.UNDERLAY);
			GraphManager.switchActiveGraph();
		}
		
		
	};
	
	public static EventHandler<ActionEvent> operatorHandler = new EventHandler<ActionEvent>(){

		@Override
		public void handle(ActionEvent arg0) {
			GraphManager.setCurrentLayer(Layer.OPERATOR);
			GraphManager.switchActiveGraph();
		}
		
		
	};
	
	public static EventHandler<ActionEvent> mappingHandler = new EventHandler<ActionEvent>(){

		@Override
		public void handle(ActionEvent arg0) {
			GraphManager.setCurrentLayer(Layer.MAPPING);
			GraphManager.switchActiveGraph();
		}
			
			
	};
		
	public static EventHandler<ActionEvent> symbolRepHandler = new EventHandler<ActionEvent>(){

		@Override
		public void handle(ActionEvent arg0) {
			GraphManager.setCurrentLayer(Layer.SYMBOL);
			GraphManager.switchActiveGraph();
		}
			
			
	};
	
	
}
