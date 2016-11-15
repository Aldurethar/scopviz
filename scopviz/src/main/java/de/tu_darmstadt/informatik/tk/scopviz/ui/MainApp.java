package de.tu_darmstadt.informatik.tk.scopviz.ui;

import java.awt.Dimension;
import java.io.IOException;
import java.util.Collection;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.graphstream.graph.Graph;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.graphicGraph.GraphicGraph;
import org.graphstream.ui.swingViewer.ViewPanel;
import org.graphstream.ui.view.Camera;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.util.MouseManager;
import org.graphstream.ui.view.util.ShortcutManager;

import javafx.application.Application;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import de.tu_darmstadt.informatik.tk.scopviz.*;

public class MainApp extends Application {

	private Stage primaryStage;
	private VBox rootLayout;
	private Graph graph;

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;

		GraphMLImporter importer = new GraphMLImporter();
		graph = importer.readGraph("src/main/resources/Example.graphml");
		
		initRootLayout();

	}

	public void initRootLayout() {
		try {
			// Load root layout from fxml file.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("/MainWindow.fxml"));
			rootLayout = (VBox) loader.load();

			// Show the scene containing the root layout.
			Scene scene = new Scene(rootLayout);

			AnchorPane anchor = (AnchorPane) rootLayout.getChildren().get(1);
			Pane pane = (Pane) anchor.getChildren().get(1);
			SwingNode swingNode = (SwingNode) pane.getChildren().get(0);
			
			ViewPanel view = Visualizer.getView(ExampleGraphCreater.getGraph());
			view.setPreferredSize(new Dimension(300, 200));
			swingNode.setContent((JPanel) view)/*new JLabel("Graph Anzeige")*/;

			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
