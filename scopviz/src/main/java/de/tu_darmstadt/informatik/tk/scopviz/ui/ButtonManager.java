package de.tu_darmstadt.informatik.tk.scopviz.ui;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.ui.geom.Point3;

import de.tu_darmstadt.informatik.tk.scopviz.debug.Debug;
import de.tu_darmstadt.informatik.tk.scopviz.main.CreateModus;
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
	public static EventHandler<ActionEvent> zoomInHandler = new EventHandler<ActionEvent>() {
		public void handle(ActionEvent evt) {
			Main.getInstance().getVisualizer().zoomIn();
		}
	};

	/**
	 * Handler for zoom out Button
	 */
	public static EventHandler<ActionEvent> zoomOutHandler = new EventHandler<ActionEvent>() {
		public void handle(ActionEvent evt) {
			Main.getInstance().getVisualizer().zoomOut();
		}
	};

	/**
	 * Handler for clicks on the graph viewer.
	 */
	public static EventHandler<MouseEvent> clickedToolboxHandler = new EventHandler<MouseEvent>() {

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
			Graph graph = visualizer.getGraph();
			Point3 cursorPos = visualizer.getView().getCamera().transformPxToGu(event.getX(), event.getY());
			
			Node n;
			
			// Create node based on creation Mode
			switch(Main.getInstance().getCreateModus()){
			
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
			
			if(!CREATE_MORE_THEN_ONE){
				Main.getInstance().setCreateModus(CreateModus.CREATE_NONE);
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
