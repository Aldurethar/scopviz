package de.tu_darmstadt.informatik.tk.scopviz.main;

import java.awt.Dimension;
import java.io.IOException;
import javax.swing.JPanel;

import org.graphstream.graph.Graph;
import org.graphstream.ui.swingViewer.ViewPanel;
import org.graphstream.ui.view.View;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import de.tu_darmstadt.informatik.tk.scopviz.debug.Debug;
import de.tu_darmstadt.informatik.tk.scopviz.io.GraphMLImporter;
import de.tu_darmstadt.informatik.tk.scopviz.ui.GUIController;
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
	private final Dimension preferredViewerSize = new Dimension(425, 367);
	
	private Visualizer visualizer;
	
	private static GUIController guiController;

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
		visualizer =  new Visualizer(graph);
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

		// Show the scene containing the root layout.
		Scene scene = new Scene(rootLayout);

		SwingNode swingNode = guiController.swingNode;
		Pane pane = guiController.pane;
		
		ViewPanel view = visualizer.getView();
		view.setPreferredSize(preferredViewerSize);
		swingNode.setContent((JPanel) view);
		pane.setMinSize(200, 200);
		

		ChangeListener<Number> resizeListener = new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
				view.setPreferredSize(new Dimension((int) pane.getWidth()-10, (int) pane.getHeight()-10));
				
				swingNode.setContent(view);
			}

		};

		pane.heightProperty().addListener(resizeListener);
		pane.widthProperty().addListener(resizeListener);
		
		guiController.zoomIn.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent evt){
				view.getCamera().setViewPercent(view.getCamera().getViewPercent()*0.95);
			}
		});
		
		guiController.zoomOut.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent evt){
				view.getCamera().setViewPercent(view.getCamera().getViewPercent()*1.05);
			}
		});
		
		primaryStage.setMinHeight(400);
		primaryStage.setMinWidth(640);
		primaryStage.setScene(scene);
		primaryStage.show();

	}
	
	public static void setGUIController(GUIController toSet){
		guiController = toSet;
	}
	
	

}
