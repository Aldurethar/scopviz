package de.tu_darmstadt.informatik.tk.scopviz.main;

import java.io.IOException;
import java.net.URL;

import de.tu_darmstadt.informatik.tk.scopviz.debug.Debug;
import de.tu_darmstadt.informatik.tk.scopviz.io.GraphMLImporter;
import de.tu_darmstadt.informatik.tk.scopviz.ui.GraphDisplayManager;
import de.tu_darmstadt.informatik.tk.scopviz.ui.OptionsManager;
import de.tu_darmstadt.informatik.tk.scopviz.ui.css.CSSManager;
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
	 * 
	 * @param stage
	 *            the Stage of the Application Window
	 */
	@Override
	public void start(final Stage stage) {
		this.primaryStage = stage;
		Main.getInstance().setPrimaryStage(this.primaryStage);
		initRootLayout();
		if (Debug.DEBUG_ENABLED) {
			GraphDisplayManager.setCurrentLayer(Layer.OPERATOR);
			GraphDisplayManager.addGraph(Debug.getDefaultOperatorGraph(), true);
			GraphDisplayManager.setCurrentLayer(Layer.UNDERLAY);
			GraphDisplayManager.addGraph(Debug.getDefaultUnderlayGraph(), true);
		} else {
			GraphMLImporter imp = new GraphMLImporter();
			GraphDisplayManager.setCurrentLayer(Layer.OPERATOR);
			try {
				GraphDisplayManager.addGraph(imp.readGraph("graph-1", "operator-shutdown.graphml"), true);
			} catch (Exception e) {
				Debug.out("INFORMATION: no previous operatorgraph", 1);
			}
			GraphDisplayManager.setCurrentLayer(Layer.UNDERLAY);
			try {
				GraphDisplayManager.addGraph(imp.readGraph("graph-2", "underlay-shutdown.graphml"), true);
			} catch (Exception e) {
				Debug.out("INFORMATION: no previous underlaygraph", 1);
			}
		}
		OptionsManager.load();
		CSSManager.addRule("node{text-alignment: at-right; size: 15px;}");
	}

	/**
	 * Initializes the UI Layout by loading it from a FXML file. Implicitly
	 * calls GUIController.initialize through the FXML loading process.
	 */
	public void initRootLayout() {

		// Load root layout from fxml file.
		try {
			FXMLLoader loader = new FXMLLoader();
			URL test = MainApp.class.getResource("/de/tu_darmstadt/informatik/tk/scopviz/main/MainWindow.fxml");
			loader.setLocation(test);
			rootLayout = (VBox) loader.load();
		} catch (IOException e) {
			System.err.println("FXML File could not be loaded. Could the Path be incorrect?");
			e.printStackTrace();
		}

		// Make the full program exit on clicking the close button
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				Main.getInstance().closeProgram();
			}
		});

		// Show the scene containing the root layout.
		Scene scene = new Scene(rootLayout);
		scene.getStylesheets().add(
				MainApp.class.getResource("/de/tu_darmstadt/informatik/tk/scopviz/main/GUITheme.css").toExternalForm());
		primaryStage.setMinHeight(400);
		primaryStage.setMinWidth(640);
		primaryStage.setScene(scene);
		primaryStage.show();

	}
}
