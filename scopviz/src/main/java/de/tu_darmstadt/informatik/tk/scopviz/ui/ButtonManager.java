package de.tu_darmstadt.informatik.tk.scopviz.ui;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.ui.geom.Point3;

import de.tu_darmstadt.informatik.tk.scopviz.debug.Debug;
import de.tu_darmstadt.informatik.tk.scopviz.main.CreateModus;
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
			CreateModus currentMod = Main.getInstance().getCreateModus();
			Graph graph = visualizer.getGraph();
			Point3 cursorPos = visualizer.getView().getCamera().transformPxToGu(event.getX(), event.getY());
			
			Node n;
			
			switch(currentMod){
			
			case CREATE_STANDARD_NODE: 
				n = graph.addNode(Main.getInstance().getUnusedID());
				n.setAttribute("xyz", cursorPos);
				Debug.out("Added Node at Position (" + cursorPos.x + "/" + cursorPos.y + ")");
				Main.getInstance().setCreateModus(CreateModus.CREATE_NONE);
				break;
			
			case CREATE_SOURCE_NODE:
				n = graph.addNode(Main.getInstance().getUnusedID());
				n.setAttribute("xyz", cursorPos);
				n.setAttribute("ui.style", "fill-color: rgb(0, 0, 255);");
				Debug.out("Added Source Node at Position (" + cursorPos.x + "/" + cursorPos.y + ")");
				Main.getInstance().setCreateModus(CreateModus.CREATE_NONE);
				break;
				
			case CREATE_SINK_NODE:
				n = graph.addNode(Main.getInstance().getUnusedID());
				n.setAttribute("xyz", cursorPos);
				n.setAttribute("ui.style", "fill-color: rgb(255, 0, 0);");
				Debug.out("Added Sink Node at Position (" + cursorPos.x + "/" + cursorPos.y + ")");
				Main.getInstance().setCreateModus(CreateModus.CREATE_NONE);
				break;
				
			case CREATE_PROC_NODE:
				n = graph.addNode(Main.getInstance().getUnusedID());
				n.setAttribute("xyz", cursorPos);
				n.setAttribute("ui.style", "fill-color: rgb(0, 255, 0);");
				Debug.out("Added ProcEn Node at Position (" + cursorPos.x + "/" + cursorPos.y + ")");
				Main.getInstance().setCreateModus(CreateModus.CREATE_NONE);
				break;
				
			default:
				break;
			}
		}
	};
	
	
	
	
	public static EventHandler<ActionEvent> underlayHandler = new EventHandler<ActionEvent>(){

		@Override
		public void handle(ActionEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		
		
	};
	
	public static EventHandler<ActionEvent> operatorHandler = new EventHandler<ActionEvent>(){

		@Override
		public void handle(ActionEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		
		
	};
	
	public static EventHandler<ActionEvent> mappingHandler = new EventHandler<ActionEvent>(){

		@Override
		public void handle(ActionEvent arg0) {
			// TODO Auto-generated method stub
			
		}
			
			
	};
		
	public static EventHandler<ActionEvent> symbolRepHandler = new EventHandler<ActionEvent>(){

		@Override
		public void handle(ActionEvent arg0) {
			// TODO Auto-generated method stub
			
		}
			
			
	};
	
	
}
