package de.tu_darmstadt.informatik.tk.scopviz.ui;

import java.util.ArrayList;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.graph.implementations.Graphs;

import de.tu_darmstadt.informatik.tk.scopviz.main.GraphManager;
import de.tu_darmstadt.informatik.tk.scopviz.main.Layer;
import de.tu_darmstadt.informatik.tk.scopviz.main.Main;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

/**
 * Manager to contain the various handlers for the buttons of the UI.
 * 
 * @author Jan Enders (jan.enders@stud.tu-darmstadt.de)
 * @version 1.0
 *
 */
public final class ButtonManager {

	/** Flag for creating more then one Edge at a time mode */
	public static final Boolean CREATE_MORE_THEN_ONE = true;

	/** List of the Buttons for Layer switching */
	private static ArrayList<Button> layerButtons;

	private static GUIController controller;

	/**
	 * Private Constructor to prevent Instantiation.
	 */
	private ButtonManager() {
	}

	/**
	 * Initializes the ButtonManager with a List of Buttons for Layer switching.
	 * 
	 * @param nList
	 *            the Layer switching Buttons
	 */
	public static void initialize(ArrayList<Button> nList, GUIController guiController, Button uButton) {
		layerButtons = nList;

		controller = guiController;
		setBorderStyle(uButton);
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
	 * Handler for the Underlay Layer switch Button.
	 */
	public static final EventHandler<ActionEvent> underlayHandler = new EventHandler<ActionEvent>() {

		@Override
		public void handle(ActionEvent arg0) {
			if (GraphDisplayManager.getCurrentLayer().equals(Layer.SYMBOL)) {
				controller.toolbox.setVisible(true);
				controller.symbolToolVBox.setVisible(false);

			}
			GraphDisplayManager.setCurrentLayer(Layer.UNDERLAY);
			GraphDisplayManager.switchActiveGraph();

			setBorderStyle((Button) arg0.getSource());
		}

	};

	/**
	 * Handler for the Operator Layer switch Button.
	 */
	public static final EventHandler<ActionEvent> operatorHandler = new EventHandler<ActionEvent>() {

		@Override
		public void handle(ActionEvent arg0) {
			if (GraphDisplayManager.getCurrentLayer().equals(Layer.SYMBOL)) {
				controller.toolbox.setVisible(true);
				controller.symbolToolVBox.setVisible(false);

			}

			GraphDisplayManager.setCurrentLayer(Layer.OPERATOR);
			GraphDisplayManager.switchActiveGraph();

			setBorderStyle((Button) arg0.getSource());
		}

	};

	/**
	 * Handler for the Mapping Layer switch Button.
	 */
	public static final EventHandler<ActionEvent> mappingHandler = new EventHandler<ActionEvent>() {

		@Override
		public void handle(ActionEvent arg0) {
			if (GraphDisplayManager.getCurrentLayer().equals(Layer.SYMBOL)) {
				controller.toolbox.setVisible(true);
				controller.symbolToolVBox.setVisible(false);

			}

			GraphDisplayManager.setCurrentLayer(Layer.MAPPING);
			GraphDisplayManager.switchActiveGraph();

			setBorderStyle((Button) arg0.getSource());
		}

	};

	/**
	 * Handler for the Symbol Representation Layer switch Button.
	 */
	public static final EventHandler<ActionEvent> symbolRepHandler = new EventHandler<ActionEvent>() {

		@Override
		public void handle(ActionEvent arg0) {
			if (!GraphDisplayManager.getCurrentLayer().equals(Layer.SYMBOL)) {
				controller.toolbox.setVisible(false);
				controller.symbolToolVBox.setVisible(true);

			}
			
			//add a copy of the underlay graph to the the symbol layer
			DefaultGraph gClone =(DefaultGraph) Graphs.clone(GraphDisplayManager.getGraphManager(Layer.UNDERLAY).getGraph());
			gClone.removeAttribute("layer");
			GraphDisplayManager.setCurrentLayer(Layer.SYMBOL);
			GraphDisplayManager.addGraph(gClone, true);
			
			//apply checkbox changes from last time
			//TODO abstract these things
			if(!controller.edgesVisibleCheckbox.isSelected()){
				
				for (Edge edge : Main.getInstance().getGraphManager().getGraph().getEachEdge()) {
					edge.addAttribute("ui.hide");
				}
				
			}
			
			if(!controller.nodeLabelCheckbox.isSelected()){
				GraphManager graphManager = Main.getInstance().getGraphManager();
				String stylesheet = graphManager.getStylesheet();
				graphManager.setStylesheet(stylesheet.concat("node{text-mode:hidden;}"));
				
			}
			
			if(!controller.edgeWeightCheckbox.isSelected()){
				GraphManager graphManager = Main.getInstance().getGraphManager();
				String stylesheet = graphManager.getStylesheet();
				graphManager.setStylesheet(stylesheet.concat("edge{text-mode:hidden;}"));
				
			}
			
			//nodesToSymbols(Main.getInstance().getGraphManager().getGraph());
			
			GraphDisplayManager.switchActiveGraph();
			setBorderStyle((Button) arg0.getSource());
		}

	};

	/**
	 * replaces all node sprites with symbol sprites corresponding with the device/hardware type
	 * @param g graph, which nodes should be symbolized
	 */
	private static void nodesToSymbols(Graph g){
		
		//TODO make it functional/make an extra stylesheet for this
		for(Node n: g.getEachNode()){
			
			if(n.getAttribute("ui.class").equals("standard") || n.getAttribute("ui.class").equals("source")){
				n.changeAttribute("ui.style", "fill-mode: image-scaled; fill-image: url('src/main/resources/png/computer.png');");
			}
			
			else if(n.getAttribute("ui.class").equals("source") || n.getAttribute("ui.class").equals("standard")){
				n.changeAttribute("ui.style", "fill-mode: image-scaled; fill-image: url('src/main/resources/png/router.png');");
			}
			
		
		}
	}
	
	public static ChangeListener<Boolean> edgeVisibleListener = new ChangeListener<Boolean>() {

		@Override
		public void changed(ObservableValue<? extends Boolean> ov, Boolean oldVal, Boolean newVal) {
			// Show edges
			if (newVal) {
				for (Edge edge : Main.getInstance().getGraphManager().getGraph().getEachEdge()) {
					edge.removeAttribute("ui.hide");
				}

				// Hide edges
			} else {
				for (Edge edge : Main.getInstance().getGraphManager().getGraph().getEachEdge()) {
					edge.addAttribute("ui.hide");
				}
			}
		}

	};

	public static ChangeListener<Boolean> nodeLabelListener = new ChangeListener<Boolean>() {

		@Override
		public void changed(ObservableValue<? extends Boolean> ov, Boolean oldVal, Boolean newVal) {
			GraphManager graphManager = Main.getInstance().getGraphManager();
			String stylesheet = graphManager.getStylesheet();

			// Show node weights
			if (newVal) {
				graphManager.setStylesheet(stylesheet.replace("node{text-mode:hidden;}", ""));

				// Hide node weights
			} else {
				graphManager.setStylesheet(stylesheet.concat("node{text-mode:hidden;}"));
			}
		}

	};

	public static ChangeListener<Boolean> edgeWeightListener = new ChangeListener<Boolean>() {

		@Override
		public void changed(ObservableValue<? extends Boolean> ov, Boolean oldVal, Boolean newVal) {
			GraphManager graphManager = Main.getInstance().getGraphManager();
			String stylesheet = graphManager.getStylesheet();

			// Show Edges weights
			if (newVal) {
				graphManager.setStylesheet(stylesheet.replace("edge{text-mode:hidden;}", ""));

				// Hide Edges weights
			} else {
				graphManager.setStylesheet(stylesheet.concat("edge{text-mode:hidden;}"));
			}
		}

	};

	/**
	 * Changes the border of the button that was pressed to red
	 * 
	 * @param currentButton
	 *            the button that was pressed
	 */
	private static void setBorderStyle(Button currentButton) {

		for (Button j : layerButtons) {
			if (j.equals(currentButton)) {
				j.setStyle(
						"-fx-background-color: red, red, red, -fx-faint-focus-color, -fx-body-color; -fx-background-insets: -0.2, 1, 2, -1.4, 2.6; -fx-background-radius: 3, 2, 1, 4, 1;");
			} else {
				j.setStyle("-fx-border-width: 0;");
			}

		}
	}

}
