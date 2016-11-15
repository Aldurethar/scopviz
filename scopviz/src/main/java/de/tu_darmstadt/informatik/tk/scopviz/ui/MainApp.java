package de.tu_darmstadt.informatik.tk.scopviz.ui;

import java.io.IOException;

import javax.swing.JButton;

import javafx.application.Application;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainApp extends Application {

	private Stage primaryStage;
	private VBox rootLayout;

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;

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

			swingNode.setContent(new JButton("BLAAAAAA"));

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
