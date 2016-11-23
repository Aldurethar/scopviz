package de.tu_darmstadt.informatik.tk.scopviz.ui;

import java.net.URL;
import java.util.ResourceBundle;

import javax.swing.JPanel;

import de.tu_darmstadt.informatik.tk.scopviz.main.Main;
import de.tu_darmstadt.informatik.tk.scopviz.ui.handlers.ResizeListener;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;

public class GUIController implements Initializable {

	@FXML
	public SwingNode swingNode;
	@FXML
	public Pane pane;

	@FXML
	public Button zoomIn;
	@FXML
	public Button zoomOut;
	@FXML
	public Button createNode;
	@FXML
	public Button createEdge;

	@FXML
	public ScrollPane toolboxScrollPane;
	@FXML
	public ScrollPane layerScrollPane;
	@FXML
	public ScrollPane propertiesScrollPane;
	@FXML
	public ScrollPane metricScrollPane;

	@FXML
	public ListView<String> toolbox;
	@FXML
	public ListView<String> properties;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		assert swingNode != null : "fx:id=\"swingNode\" was not injected: check your FXML file 'NewBetterCoolerWindowTest.fxml'.";

		assert pane != null : "fx:id=\"pane\" was not injected: check your FXML file 'NewBetterCoolerWindowTest.fxml'.";

		assert zoomIn != null : "fx:id=\"zoomIn\" was not injected: check your FXML file 'NewBetterCoolerWindowTest.fxml'.";
		assert zoomOut != null : "fx:id=\"zoomOut\" was not injected: check your FXML file 'NewBetterCoolerWindowTest.fxml'.";
		assert createNode != null : "fx:id=\"createNode\" was not injected: check your FXML file 'NewBetterCoolerWindowTest.fxml'.";
		assert createEdge != null : "fx:id=\"createEdge\" was not injected: check your FXML file 'NewBetterCoolerWindowTest.fxml'.";

		assert layerScrollPane != null : "fx:id=\"layerScrollPane\" was not injected: check your FXML file 'NewBetterCoolerWindowTest.fxml'.";
		assert propertiesScrollPane != null : "fx:id=\"propertiesScrollPane\" was not injected: check your FXML file 'NewBetterCoolerWindowTest.fxml'.";
		assert metricScrollPane != null : "fx:id=\"metricSrollPane\" was not injected: check your FXML file 'NewBetterCoolerWindowTest.fxml'.";
		assert toolboxScrollPane != null : "fx:id=\"toolboxScrollPane\" was not injected: check your FXML file 'NewBetterCoolerWindowTest.fxml'.";

		assert toolbox != null : "fx:id=\"toolbox\" was not injected: check your FXML file 'NewBetterCoolerWindowTest.fxml'.";
		assert properties != null : "fx:id=\"properties\" was not injected: check your FXML file 'NewBetterCoolerWindowTest.fxml'.";

		ToolboxManager.initialize(toolbox);

		PropertiesManager.initialize(properties);

		ButtonManager.initialize(this);

		initializeZoomButtons();
		initializeCreateButtons();
		
		initializeDisplayPane();

	}

	private void initializeZoomButtons() {
		zoomIn.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent evt) {
				Main.getInstance().getVisualizer().zoomIn();
			}
		});

		zoomOut.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent evt) {
				Main.getInstance().getVisualizer().zoomOut();
			}
		});
	}

	private void initializeCreateButtons() {
		createNode.setOnAction(ButtonManager.createNodeHandler);
		createEdge.setOnAction(ButtonManager.createEdgeHandler);
		swingNode.setOnMouseClicked(ButtonManager.clickedHandler);
	}

	private void initializeDisplayPane(){
		pane.heightProperty().addListener(new ResizeListener(swingNode, pane));
		pane.widthProperty().addListener(new ResizeListener(swingNode, pane));
		swingNode.setContent((JPanel) Main.getInstance().getVisualizer().getView());
		pane.setMinSize(200, 200);
	}
}
