package de.tu_darmstadt.informatik.tk.scopviz.main;

import java.awt.Dimension;
import java.io.IOException;
import javax.swing.JPanel;

import org.graphstream.graph.Graph;
import org.graphstream.ui.swingViewer.ViewPanel;
import javafx.application.Application;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import de.tu_darmstadt.informatik.tk.scopviz.io.GraphMLImporter;
import de.tu_darmstadt.informatik.tk.scopviz.ui.Visualizer;

/**
 * Main Class, initializes Graph, displays UI.
 * 
 * @author Jan Enders (jan.enders@stud.tu-darmstadt.de)
 * @version 1.0
 *
 */
public class MainApp extends Application {

	/**
	 * Primary Stage for the UI Scene.
	 */
	private Stage primaryStage;
	/**
	 * Root Object of the Scene Graph.
	 */
	private VBox rootLayout;
	/**
	 * Graph (Network or Operator Graph) to display.
	 */
	private Graph graph;
	/**
	 * Preferred size for the Graph Viewer.
	 */
	private final Dimension preferredViewerSize = new Dimension(300, 300);

	/**
	 * Main Method, launches the Application.
	 * 
	 * @param args
	 *            Optional String arguments (command line flags), none
	 *            implemented
	 */
	public static void main(final String[] args) {
		launch(args);
	}

	/**
	 * Initializes the Graph by importing it from a GraphML file.
	 */
	@Override
	public void init() {
		GraphMLImporter importer = new GraphMLImporter();
		graph = importer.readGraph(MainApp.class.getResource("/Example.graphml"));
	}

	/**
	 * Starts the Application by initializing the UI Layout.
	 */
	@Override
	public void start(final Stage stage) {
		this.primaryStage = stage;

		initRootLayout();

	}

	/**
	 * initializes the UI Layout by loading it from a FXML file.
	 */
	public void initRootLayout() {

		// Load root layout from fxml file.
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("/MainWindow.fxml"));
			rootLayout = (VBox) loader.load();
		} catch (IOException e) {
			System.err.println("FXML File could not be loaded. Could the Path be incorrect?");
			e.printStackTrace();
		}

		// Show the scene containing the root layout.
		Scene scene = new Scene(rootLayout);

		// Get Access to the SwingNode within the UI
		// TODO: Make this not terrible
		AnchorPane anchor = (AnchorPane) rootLayout.getChildren().get(1);
		Pane pane = (Pane) anchor.getChildren().get(1);
		SwingNode swingNode = (SwingNode) pane.getChildren().get(0);

		ViewPanel view = Visualizer.getView(graph);
		view.setPreferredSize(preferredViewerSize);
		swingNode.setContent((JPanel) view);

		primaryStage.setScene(scene);
		primaryStage.show();

	}

}
