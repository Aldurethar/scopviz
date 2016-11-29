package de.tu_darmstadt.informatik.tk.scopviz.ui;

import java.net.URL;
import java.util.ResourceBundle;

import javax.swing.JPanel;

import de.tu_darmstadt.informatik.tk.scopviz.debug.Debug;
import de.tu_darmstadt.informatik.tk.scopviz.main.Main;
import de.tu_darmstadt.informatik.tk.scopviz.ui.handlers.ResizeListener;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

/**
 * Controller class for the various GUI elements, gets instanced and initialized
 * by the FXML loading process. Can access GUI elements specified in the FXML
 * file through matching variable name here and id attribute in the FXML,
 * 
 * @author Dominik Renkel, Jan Enders
 * @version 1.1
 *
 */
public class GUIController implements Initializable {

	// The SwingNode and its containing Pane that house the graph viewer
	@FXML
	public SwingNode swingNode;
	@FXML
	public Pane pane;

	// The Button present in the UI
	@FXML
	public Button zoomIn;
	@FXML
	public Button zoomOut;
	@FXML
	public Button createNode;
	@FXML
	public Button createEdge;

	// The ScrollPanes surrounding the graph viewer, containing most
	// functionality
	@FXML
	public ScrollPane toolboxScrollPane;
	@FXML
	public ScrollPane layerScrollPane;
	@FXML
	public ScrollPane propertiesScrollPane;
	@FXML
	public ScrollPane metricScrollPane;

	// The contents of the corresponding ScrollPanes
	@FXML
	public ListView<String> toolbox;
	@FXML
	public ListView<String> properties;

	/**
	 * Initializes all the references to the UI elements specified in the FXML
	 * file. Gets called during FXML loading. Asserts the correct injection of
	 * all referenced UI elements and initializes them.
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// Assert the correct injection of all references from FXML
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

		// Initialize the Managers for the various managers for UI elements
		ToolboxManager.initialize(toolbox);
		PropertiesManager.initialize(properties);
		ButtonManager.initialize(this);

		
		
		// Bind all the handlers to their corresponding UI elements
		initializeZoomButtons();
		initializeCreateButtons();
		initializeDisplayPane();

	}

	/**
	 * Sets the handlers for the zoomin and zoomout buttons.
	 */
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

	/**
	 * Sets the Handlers for the create node and create edge buttons.
	 */
	private void initializeCreateButtons() {
		createNode.setOnAction(ButtonManager.createNodeHandler);
		createEdge.setOnAction(ButtonManager.createEdgeHandler);
		swingNode.setOnMouseClicked(ButtonManager.clickedHandler);
	}

	/**
	 * Sets the minimum size and adds the handlers to the graph display.
	 */
	private void initializeDisplayPane() {
		
		//swingNode.onMouseReleasedProperty().addListener(new SomethingHappenedListener());
		//swingNode.onMousePressedProperty().addListener(new SomethingHappenedListener());
		swingNode.setOnMouseReleased(new EventHandler<MouseEvent>(){
			public void handle(MouseEvent evt){
				Debug.out("pump!");
				Main.getInstance().getVisualizer().getFromViewer().pump();
			}
		});
		
		pane.heightProperty().addListener(new ResizeListener(swingNode, pane));
		pane.widthProperty().addListener(new ResizeListener(swingNode, pane));
		swingNode.setContent((JPanel) Main.getInstance().getVisualizer().getView());
		pane.setMinSize(200, 200);
	}
}
