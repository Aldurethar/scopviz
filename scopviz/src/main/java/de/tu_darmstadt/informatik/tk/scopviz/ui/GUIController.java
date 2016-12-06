package de.tu_darmstadt.informatik.tk.scopviz.ui;

import java.net.URL;
import java.util.ResourceBundle;

import javax.swing.JPanel;

import de.tu_darmstadt.informatik.tk.scopviz.main.GraphManager;
import de.tu_darmstadt.informatik.tk.scopviz.main.Main;
import de.tu_darmstadt.informatik.tk.scopviz.ui.handlers.ResizeListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.Pane;
import javafx.util.Callback;
import javafx.util.Pair;

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
	
	@FXML
	public Button underlayButton;
	@FXML
	public Button operatorButton;
	@FXML
	public Button mappingButton;
	@FXML
	public Button symbolRepButton;

	// The Toolbar Items
	@FXML
	public MenuItem open;
	@FXML
	public MenuItem save;
	@FXML
	public MenuItem saveAs;
	@FXML
	public MenuItem quit;
	@FXML
	public MenuItem delete;
	@FXML
	public MenuItem undelete;
	@FXML
	public MenuItem selectMode;

	// The contents of the corresponding ScrollPanes
	@FXML
	public TableView<Pair<Object, String>> toolbox;
	@FXML
	public TableView<KeyValuePair> properties;
	@FXML
	public ListView<String> metricListView;
	@FXML
	public ListView<String> layerListView;

	@FXML
	public TableColumn<Pair<Object, String>, String> toolboxStringColumn;
	@FXML
	public TableColumn<Pair<Object, String>, Object> toolboxObjectColumn;

	@FXML
	public TableColumn<KeyValuePair, String> propertiesStringColumn;
	@FXML
	public TableColumn propertiesObjectColumn;

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

		assert underlayButton != null : "fx:id=\"underlayButton\" was not injected: check your FXML file 'NewBetterCoolerWindowTest.fxml'.";
		assert operatorButton != null : "fx:id=\"operatorButton\" was not injected: check your FXML file 'NewBetterCoolerWindowTest.fxml'.";
		assert mappingButton != null : "fx:id=\"mappingButton\" was not injected: check your FXML file 'NewBetterCoolerWindowTest.fxml'.";
		assert symbolRepButton != null : "fx:id=\"symbolRepButton\" was not injected: check your FXML file 'NewBetterCoolerWindowTest.fxml'.";

		assert open != null : "fx:id=\"open\" was not injected: check your FXML file 'NewBetterCoolerWindowTest.fxml'.";
		assert save != null : "fx:id=\"save\" was not injected: check your FXML file 'NewBetterCoolerWindowTest.fxml'.";
		assert saveAs != null : "fx:id=\"saveAs\" was not injected: check your FXML file 'NewBetterCoolerWindowTest.fxml'.";
		assert quit != null : "fx:id=\"quit\" was not injected: check your FXML file 'NewBetterCoolerWindowTest.fxml'.";
		assert delete != null : "fx:id=\"delete\" was not injected: check your FXML file 'NewBetterCoolerWindowTest.fxml'.";
		assert undelete != null : "fx:id=\"undelete\" was not injected: check your FXML file 'NewBetterCoolerWindowTest.fxml'.";
		assert selectMode != null : "fx:id=\"selectMode\" was not injected: check your FXML file 'NewBetterCoolerWindowTest.fxml'.";

		assert layerListView != null : "fx:id=\"layerListView\" was not injected: check your FXML file 'NewBetterCoolerWindowTest.fxml'.";
	
		assert toolbox != null : "fx:id=\"toolbox\" was not injected: check your FXML file 'NewBetterCoolerWindowTest.fxml'.";
		assert properties != null : "fx:id=\"properties\" was not injected: check your FXML file 'NewBetterCoolerWindowTest.fxml'.";
		assert metricListView != null : "fx:id=\"metricListView\" was not injected: check your FXML file 'NewBetterCoolerWindowTest.fxml'.";

		assert toolboxStringColumn != null : "fx:id=\"toolboxString\" was not injected: check your FXML file 'NewBetterCoolerWindowTest.fxml'.";
		assert toolboxObjectColumn != null : "fx:id=\"toolboxObject\" was not injected: check your FXML file 'NewBetterCoolerWindowTest.fxml'.";

		assert propertiesStringColumn != null : "fx:id=\"propertiesString\" was not injected: check your FXML file 'NewBetterCoolerWindowTest.fxml'.";
		assert propertiesObjectColumn != null : "fx:id=\"propertiesObject\" was not injected: check your FXML file 'NewBetterCoolerWindowTest.fxml'.";

		initializeToolbox();
		initializeProperties();
		
		// Remove Header for TableViews
		removeHeaderTableView(toolbox);
		removeHeaderTableView(properties);

		// Initialize the Managers for the various managers for UI elements
		ToolboxManager.initializeItems(toolbox);
		PropertiesManager.initializeItems(properties);
		ButtonManager.initialize(this);
		ToolbarManager.initialize(this);
		GraphManager.setGuiController(this);

		// Bind all the handlers to their corresponding UI elements
		initializeZoomButtons();
		initializeCreateButtons();
		initializeLayerButton();
		initializeDisplayPane();
		initializeToolBar();
	}

	private void initializeToolBar() {
		open.setOnAction(ToolbarManager.openHandler);
		save.setOnAction(ToolbarManager.saveHandler);
		saveAs.setOnAction(ToolbarManager.saveAsHandler);
		quit.setOnAction(ToolbarManager.quitHandler);
		delete.setOnAction(ToolbarManager.deleteHandler);
		undelete.setOnAction(ToolbarManager.undeleteHandler);
		selectMode.setOnAction(ToolbarManager.selectModeHandler);
	}

	/**
	 * Sets the handlers for the zoomin and zoomout buttons.
	 */
	private void initializeZoomButtons() {
		zoomIn.setOnAction(ButtonManager.zoomInHandler);
		zoomOut.setOnAction(ButtonManager.zoomOutHandler);
	}

	/**
	 * Sets the Handlers for the create node and create edge buttons.
	 */
	private void initializeCreateButtons() {
		createNode.setOnAction(ButtonManager.createNodeHandler);
		createEdge.setOnAction(ButtonManager.createEdgeHandler);
		swingNode.setOnMouseClicked(ButtonManager.clickedHandler);
	}
	
	private void initializeLayerButton(){
		underlayButton.setOnAction(ButtonManager.underlayHandler);
		operatorButton.setOnAction(ButtonManager.operatorHandler);
		mappingButton.setOnAction(ButtonManager.mappingHandler);
		symbolRepButton.setOnAction(ButtonManager.symbolRepHandler);
	}

	/**
	 * Sets the minimum size and adds the handlers to the graph display.
	 */
	private void initializeDisplayPane() {
		pane.heightProperty().addListener(new ResizeListener(swingNode, pane));
		pane.widthProperty().addListener(new ResizeListener(swingNode, pane));
		swingNode.setContent((JPanel) Main.getInstance().getVisualizer().getView());
		pane.setMinSize(200, 200);
	}

	/**
	 * 
	 */
	@SuppressWarnings({ "unchecked" })
	private void initializeToolbox() {
		toolboxStringColumn.setCellValueFactory(new ToolboxManager.PairKeyFactory());
		toolboxObjectColumn.setCellValueFactory(new ToolboxManager.PairValueFactory());

		toolbox.getColumns().setAll(toolboxObjectColumn, toolboxStringColumn);

		toolboxObjectColumn.setCellFactory(
				new Callback<TableColumn<Pair<Object, String>, Object>, TableCell<Pair<Object, String>, Object>>() {
					@Override
					public TableCell<Pair<Object, String>, Object> call(
							TableColumn<Pair<Object, String>, Object> column) {
						return new ToolboxManager.PairValueCell();
					}
				});

	}

	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	private void initializeProperties() {
		propertiesStringColumn.setCellValueFactory(new PropertyValueFactory<KeyValuePair, String>("key"));

		propertiesObjectColumn.setCellValueFactory(new PropertyValueFactory<KeyValuePair, Object>("value"));
		propertiesObjectColumn.setCellFactory(TextFieldTableCell.forTableColumn());
		propertiesObjectColumn.setOnEditCommit(PropertiesManager.setOnEditCommitHandler);

		properties.getColumns().setAll(propertiesStringColumn, propertiesObjectColumn);
	}

	/**
	 * Removes the TableView Header for a given TableView
	 * 
	 * @param tableView
	 */
	private void removeHeaderTableView(TableView<?> tableView) {
		tableView.widthProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
				// Get the table header
				Pane header = (Pane) tableView.lookup("TableHeaderRow");
				if (header != null && header.isVisible()) {
					header.setMaxHeight(0);
					header.setMinHeight(0);
					header.setPrefHeight(0);
					header.setVisible(false);
					header.setManaged(false);
				}
			}
		});
	}
}
