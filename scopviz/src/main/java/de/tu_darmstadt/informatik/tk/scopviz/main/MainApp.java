package de.tu_darmstadt.informatik.tk.scopviz.main;

import java.io.IOException;

import de.tu_darmstadt.informatik.tk.scopviz.debug.Debug;
import de.tu_darmstadt.informatik.tk.scopviz.io.GraphMLExporter;
import de.tu_darmstadt.informatik.tk.scopviz.ui.GraphDisplayManager;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * Main UI Class, loads GUI from FXML file and initializes all UI Elements.
 * 
 * @author Jan Enders (jan.enders@stud.tu-darmstadt.de)
 * @version 1.2
 *
 */
public class MainApp extends Application {

	private static final boolean exportOnClose = false;

	/**
	 * Primary Stage for the UI Scene.
	 */
	private Stage primaryStage;
	/**
	 * Root Object of the Scene Graph.
	 */
	private VBox rootLayout;

	/**
	 * Main Method, launches the Application.
	 * 
	 * @param args
	 *            Optional String arguments (command line flags), none
	 *            implemented
	 */
	public static void main(final String[] args) {
		System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
		launch(args);
	}

	/**
	 * Initializes the Main Class by invoking getInstance() for the first time.
	 */
	@Override
	public void init() {
		Main.getInstance();
	}

	/**
	 * Starts the Application by initializing the UI Layout.
	 */
	@Override
	public void start(final Stage stage) {
		this.primaryStage = stage;
		Main.getInstance().setPrimaryStage(this.primaryStage);
		initRootLayout();
		if (Debug.DEBUG_ENABLED) {
			GraphDisplayManager.addGraph(Debug.getDefaultGraph(), true);
		}
	}

	/**
	 * Initializes the UI Layout by loading it from a FXML file. Implicitly
	 * calls GUIController.initialize through the FXML loading process.
	 */
	public void initRootLayout() {

		// Load root layout from fxml file.
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("/NewBetterCoolerWindowTest.fxml"));
			rootLayout = (VBox) loader.load();
		} catch (IOException e) {
			System.err.println("FXML File could not be loaded. Could the Path be incorrect?");
			e.printStackTrace();
		}

		// Make the full program exit on clicking the close button
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				if (exportOnClose) {
					GraphMLExporter exporter = new GraphMLExporter();
					exporter.writeGraph(Main.getInstance().getGraphManager().getGraph(), "shutdown.graphml");
				}

				System.exit(0);
			}
		});

		// Show the scene containing the root layout.
		Scene scene = new Scene(rootLayout);
		primaryStage.setMinHeight(400);
		primaryStage.setMinWidth(640);
		primaryStage.setScene(scene);
		primaryStage.show();

	}
}
