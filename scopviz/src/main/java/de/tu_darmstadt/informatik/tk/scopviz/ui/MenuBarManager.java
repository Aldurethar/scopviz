package de.tu_darmstadt.informatik.tk.scopviz.ui;

import de.tu_darmstadt.informatik.tk.scopviz.io.GraphMLExporter;
import de.tu_darmstadt.informatik.tk.scopviz.main.GraphManager;
import de.tu_darmstadt.informatik.tk.scopviz.main.Main;
import de.tu_darmstadt.informatik.tk.scopviz.main.SelectionMode;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;

public class MenuBarManager {

	/**
	 * Handler for the "open" MenuItem.
	 */
	public static final EventHandler<ActionEvent> openHandler = new EventHandler<ActionEvent>() {

		/**
		 * Handle method gets called when the button is pressed.
		 * 
		 * @param arg0
		 *            the event that occurred to the button
		 */
		@Override
		public void handle(ActionEvent arg0) {
			// GraphManager v = Main.getInstance().getGraphManager();
			GraphDisplayManager.addGraph(Main.getInstance().getPrimaryStage());
		}
	};

	/**
	 * Handler for the "save" button.
	 */
	public static final EventHandler<ActionEvent> saveHandler = new EventHandler<ActionEvent>() {

		/**
		 * Handle method gets called when the button is pressed.
		 * 
		 * @param arg0
		 *            the event that occurred to the button
		 */
		@Override
		public void handle(ActionEvent arg0) {
			GraphManager v = Main.getInstance().getGraphManager();
			if (v.getCurrentPath() != null) {
				new GraphMLExporter().writeGraph(v.getGraph(), v.getCurrentPath());
			} else {
				new GraphMLExporter().writeGraph(v.getGraph(), Main.getInstance().getPrimaryStage());
			}
		}
	};

	public static final EventHandler<ActionEvent> saveAsHandler = new EventHandler<ActionEvent>() {
		public void handle(ActionEvent evt) {
			GraphManager v = Main.getInstance().getGraphManager();
			new GraphMLExporter().writeGraph(v.getGraph(), Main.getInstance().getPrimaryStage());
		}
	};

	public static final EventHandler<ActionEvent> quitHandler = new EventHandler<ActionEvent>() {
		public void handle(ActionEvent evt) {
			System.exit(0);
		}
	};

	/**
	 * Handler for the "save" button.
	 */
	public static final EventHandler<ActionEvent> deleteHandler = new EventHandler<ActionEvent>() {

		/**
		 * Handle method gets called whenever the menuItem is presser
		 * 
		 * @param event
		 *            the event that occurred to the menuItem
		 */
		@Override
		public void handle(ActionEvent event) {
			GraphManager v = Main.getInstance().getGraphManager();
			if (v.getSelectedEdgeID() != null) {
				v.deleteEdge(v.getSelectedEdgeID());
			}
			if (v.getSelectedNodeID() != null) {
				v.deleteNode(v.getSelectedNodeID());
			}
		}
	};

	public static final EventHandler<ActionEvent> undeleteHandler = new EventHandler<ActionEvent>() {
		public void handle(ActionEvent evt) {
			Main.getInstance().getGraphManager().undelete();
		}
	};

	public static final EventHandler<ActionEvent> selectModeHandler = new EventHandler<ActionEvent>() {
		public void handle(ActionEvent evt) {
			MenuItem src = (MenuItem) evt.getSource();
			if (src.getText().equals("Select Edges")) {
				src.setText("Select Nodes");
				Main.getInstance().setSelectModus(SelectionMode.SELECT_EDGES);
			} else {
				src.setText("Select Edges");
				Main.getInstance().setSelectModus(SelectionMode.SELECT_NODES);
			}

		}
	};

}
