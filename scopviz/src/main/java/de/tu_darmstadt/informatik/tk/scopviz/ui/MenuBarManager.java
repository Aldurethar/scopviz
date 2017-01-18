package de.tu_darmstadt.informatik.tk.scopviz.ui;

import de.tu_darmstadt.informatik.tk.scopviz.io.GraphMLExporter;
import de.tu_darmstadt.informatik.tk.scopviz.main.GraphManager;
import de.tu_darmstadt.informatik.tk.scopviz.main.Main;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;

/**
 * Manager Class for the Menu Bar.
 * 
 * @author Jascha Bohne
 * @version 1.1
 *
 */
public final class MenuBarManager {

	/**
	 * Private Constructor to prevent Instantiation.
	 */
	private MenuBarManager() {
	}

	/**
	 * Handler for the "new" MenuItem.
	 */
	public static final void newAction(ActionEvent event) {
		GraphDisplayManager.addGraph();
	}

	/**
	 * Handler for the "open" MenuItem.
	 */
	public static final void openAction(ActionEvent event) {
		GraphDisplayManager.addGraph(Main.getInstance().getPrimaryStage(), true);
	}

	/**
	 * Handler for the "add" MenuItem.
	 */
	public static final void addAction(ActionEvent event) {
		GraphDisplayManager.addGraph(Main.getInstance().getPrimaryStage(), false);
	}

	/**
	 * Handler for the "save" button.
	 */
	public static void saveAction(ActionEvent event) {
		GraphManager v = Main.getInstance().getGraphManager();
		if (v.getCurrentPath() != null) {
			new GraphMLExporter().writeGraph(v.getGraph(), v.getCurrentPath());
		} else {
			new GraphMLExporter().writeGraph(v.getGraph(), Main.getInstance().getPrimaryStage());
		}
	}

	/**
	 * Handler for the "save as..." button.
	 */
	public static void saveAsAction(ActionEvent event) {
		GraphManager v = Main.getInstance().getGraphManager();
		new GraphMLExporter().writeGraph(v.getGraph(), Main.getInstance().getPrimaryStage());
	}

	/**
	 * Handler for the "quit" button.
	 */
	public static void quitAction(ActionEvent event) {
		System.exit(0);
	}

	/**
	 * Handler for the "delete" button.
	 */
	public static void deleteAction(ActionEvent event) {
		GraphManager v = Main.getInstance().getGraphManager();
		if (v.getSelectedEdgeID() != null) {
			v.deleteEdge(v.getSelectedEdgeID());
		}
		if (v.getSelectedNodeID() != null) {
			v.deleteNode(v.getSelectedNodeID());
		}
	}

	/**
	 * Handler for the "undelete" button.
	 */
	public static void undeleteAction(ActionEvent event) {
		Main.getInstance().getGraphManager().undelete();
	}

	/**
	 * Handler for the "preferences" MenuItem.
	 */
	public static void preferencesAction(ActionEvent event) {
		OptionsManager.openOptionsDialog();
	}

	/**
	 * Handler for the "about" MenuItem.
	 */
	public static void aboutAction(ActionEvent event) {
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

}
