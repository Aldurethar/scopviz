package de.tu_darmstadt.informatik.tk.scopviz.ui;

import de.tu_darmstadt.informatik.tk.scopviz.io.GraphMLExporter;
import de.tu_darmstadt.informatik.tk.scopviz.io.GraphMLImporter;
import de.tu_darmstadt.informatik.tk.scopviz.main.GraphManager;
import de.tu_darmstadt.informatik.tk.scopviz.main.Main;
import de.tu_darmstadt.informatik.tk.scopviz.main.SelectionModus;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;

public class ToolbarManager {

	/**
	 * GUIController reference
	 */
	private static GUIController controller;
	
	/**
	 * Set GUIController 
	 * @param guiController
	 */
	public static void setGUIController(GUIController guiController){
		controller = guiController;
	}
	/**
	 * Handler for the "open" MenuItem.
	 */
	public static EventHandler<ActionEvent> openHandler = new EventHandler<ActionEvent>() {

		/**
		 * Handle method gets called when the button is pressed.
		 * 
		 * @param arg0
		 *            the event that occurred to the button
		 */
		@Override
		public void handle(ActionEvent arg0) {
			Visualizer v = Main.getInstance().getVisualizer();
			GraphManager.addGraph(Main.getInstance().getPrimaryStage());
		}
	};

	/**
	 * Handler for the "save" button.
	 */
	public static EventHandler<ActionEvent> saveHandler = new EventHandler<ActionEvent>() {

		/**
		 * Handle method gets called when the button is pressed.
		 * 
		 * @param arg0
		 *            the event that occurred to the button
		 */
		@Override
		public void handle(ActionEvent arg0) {
			Visualizer v = Main.getInstance().getVisualizer();
			if (v.getCurrentPath() != null) {
				new GraphMLExporter().writeGraph(v.getGraph(), v.getCurrentPath());
			} else {
				new GraphMLExporter().writeGraph(v.getGraph(), Main.getInstance().getPrimaryStage());
			}
		}
	};

	public static EventHandler<ActionEvent> saveAsHandler = new EventHandler<ActionEvent>() {
		public void handle(ActionEvent evt) {
			Visualizer v = Main.getInstance().getVisualizer();
			new GraphMLExporter().writeGraph(v.getGraph(), Main.getInstance().getPrimaryStage());
		}
	};

	public static EventHandler<ActionEvent> quitHandler = new EventHandler<ActionEvent>() {
		public void handle(ActionEvent evt) {
			System.exit(0);
		}
	};

	/**
	 * Handler for the "save" button.
	 */
	public static EventHandler<ActionEvent> deleteHandler = new EventHandler<ActionEvent>() {

		/**
		 * Handle method gets called whenever the menuItem is presser
		 * 
		 * @param event
		 *            the event that occurred to the menuItem
		 */
		@Override
		public void handle(ActionEvent event) {
			Visualizer v = Main.getInstance().getVisualizer();
			if (v.getSelectedEdgeID() != null) {
				v.deleteEdge(v.getSelectedEdgeID());
			}
			if (v.getSelectedNodeID() != null) {
				v.deleteNode(v.getSelectedNodeID());
			}
		}
	};

	public static EventHandler<ActionEvent> undeleteHandler = new EventHandler<ActionEvent>() {
		public void handle(ActionEvent evt) {
			Main.getInstance().getVisualizer().undelete();
		}
	};

	// TODO split Modus Enum
	public static EventHandler<ActionEvent> selectModeHandler = new EventHandler<ActionEvent>() {
		public void handle(ActionEvent evt) {
			MenuItem src = (MenuItem) evt.getSource();
			if (src.getText().equals("Select Edges")) {
				src.setText("Select Nodes");
				Main.getInstance().setSelectModus(SelectionModus.SELECT_EDGES);
			} else {
				src.setText("Select Edges");
				Main.getInstance().setSelectModus(SelectionModus.SELECT_NODES);
			}
			
			controller.selectModusText.setText(Main.getInstance().getSelectModus().toString());
		}
	};

}
