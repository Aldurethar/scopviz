package de.tu_darmstadt.informatik.tk.scopviz.ui;

import de.tu_darmstadt.informatik.tk.scopviz.io.GraphMLExporter;
import de.tu_darmstadt.informatik.tk.scopviz.main.CreationMode;
import de.tu_darmstadt.informatik.tk.scopviz.main.GraphManager;
import de.tu_darmstadt.informatik.tk.scopviz.main.Main;
import de.tu_darmstadt.informatik.tk.scopviz.main.SelectionMode;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Region;

public class MenuBarManager {

	private static GUIController controller;

	/**
	 * Handler for the "new" MenuItem.
	 */
	public static final EventHandler<ActionEvent> newHandler = new EventHandler<ActionEvent>() {

		/**
		 * Handle method gets called when the button is pressed.
		 * 
		 * @param arg0
		 *            the event that occurred to the button
		 */
		@Override
		public void handle(ActionEvent arg0) {
			GraphDisplayManager.addGraph();
		}
	};

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
			GraphDisplayManager.addGraph(Main.getInstance().getPrimaryStage(), true);
		}
	};

	/**
	 * Handler for the "add" MenuItem.
	 */
	public static final EventHandler<ActionEvent> addHandler = new EventHandler<ActionEvent>() {

		/**
		 * Handle method gets called when the button is pressed.
		 * 
		 * @param arg0
		 *            the event that occurred to the button
		 */
		@Override
		public void handle(ActionEvent arg0) {
			GraphDisplayManager.addGraph(Main.getInstance().getPrimaryStage(), false);
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

				// unselect CreationMode
				Main.getInstance().setCreationMode(CreationMode.CREATE_NONE);
				controller.toolbox.getSelectionModel().clearSelection();

				// set SelectMode
				Main.getInstance().setSelectionMode(SelectionMode.SELECT_EDGES);

			} else {
				src.setText("Select Edges");

				// unselect CreationMode
				Main.getInstance().setCreationMode(CreationMode.CREATE_NONE);
				controller.toolbox.getSelectionModel().clearSelection();

				// set SelectMode
				Main.getInstance().setSelectionMode(SelectionMode.SELECT_NODES);

			}
		}
	};

	/**
	 * Handler for the "preferences" MenuItem.
	 */
	public static final EventHandler<ActionEvent> preferencesHandler = new EventHandler<ActionEvent>() {

		/**
		 * Handle method gets called when the button is pressed.
		 * 
		 * @param arg0
		 *            the event that occurred to the button
		 */
		@Override
		public void handle(ActionEvent arg0) {
			OptionsManager.openOptionsDialog();
		}
	};

	/**
	 * Handler for the "about" MenuItem.
	 */
	public static final EventHandler<ActionEvent> aboutHandler = new EventHandler<ActionEvent>() {

		/**
		 * Handle method gets called when the button is pressed.
		 * 
		 * @param arg0
		 *            the event that occurred to the button
		 */
		@Override
		public void handle(ActionEvent arg0) {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("About this programm");
			alert.setHeaderText(null);
			alert.setContentText("" + "Visualization Software of the Telecooperation group, \n"
					+ "Department of Computer Science, \n" + "Technische Universität Darmstadt. \n" + "\n"
					+ "Created by: \n" + "Jan Enders, Jascha Bohne, Dominik Renkel, \n"
					+ "Julian Ohl und Matthias Wilhelm \n" + "comissioned by Julien Gedeon");
			alert.getDialogPane().getChildren().stream().filter(node -> node instanceof Label)
					.forEach(node -> ((Label) node).setMinHeight(Region.USE_PREF_SIZE));
			alert.showAndWait();
		}
	};

	public static void setGUIController(GUIController con) {
		controller = con;
	}

}
