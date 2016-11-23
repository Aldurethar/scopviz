package de.tu_darmstadt.informatik.tk.scopviz.main;

import java.io.IOException;
import org.graphstream.ui.swingViewer.ViewPanel;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * Main Class, initializes Graph, displays UI.
 * 
 * @author Jan Enders (jan.enders@stud.tu-darmstadt.de)
 * @version 1.2
 *
 */
public class MainApp extends Application {

	private Main main;

	/**
	 * Primary Stage for the UI Scene.
	 */
	private Stage primaryStage;
	/**
	 * Root Object of the Scene Graph.
	 */
	private VBox rootLayout;

	public static ViewPanel view;

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
		main = Main.getInstance();
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
			loader.setLocation(MainApp.class.getResource("/NewBetterCoolerWindowTest.fxml"));
			rootLayout = (VBox) loader.load();
		} catch (IOException e) {
			System.err.println("FXML File could not be loaded. Could the Path be incorrect?");
			e.printStackTrace();
		}

		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
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
