package de.tu_darmstadt.informatik.tk.scopviz.ui;

import java.awt.Dimension;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javax.swing.JPanel;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactoryInfo;

import de.tu_darmstadt.informatik.tk.scopviz.main.Main;
import de.tu_darmstadt.informatik.tk.scopviz.ui.handlers.KeyboardShortcuts;
import de.tu_darmstadt.informatik.tk.scopviz.ui.handlers.MyAnimationTimer;
import de.tu_darmstadt.informatik.tk.scopviz.ui.handlers.MyViewerListener;
import de.tu_darmstadt.informatik.tk.scopviz.ui.handlers.ResizeListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
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
	public SwingNode swingNodeWorldView;
	@FXML
	public Pane pane;

	// The buttons present in the UI
	@FXML
	public Button zoomIn;
	@FXML
	public Button zoomOut;

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
	public MenuItem newItem;
	@FXML
	public MenuItem open;
	@FXML
	public MenuItem add;
	@FXML
	public MenuItem save;
	@FXML
	public MenuItem saveAs;
	@FXML
	public MenuItem preferences;
	@FXML
	public MenuItem quit;
	@FXML
	public MenuItem delete;
	@FXML
	public MenuItem undelete;
	@FXML
	public MenuItem selectMode;
	@FXML
	public MenuItem about;

	// The contents of the corresponding ScrollPanes
	@FXML
	public TableView<Pair<Object, String>> toolbox;
	@FXML
	public TableView<KeyValuePair> properties;
	@FXML
	public ListView<String> metricListView;
	@FXML
	public ListView<String> layerListView;

	// The columns of the Toolbox
	@FXML
	public TableColumn<Pair<Object, String>, String> toolboxStringColumn;
	@FXML
	public TableColumn<Pair<Object, String>, Object> toolboxObjectColumn;

	// The columns of the Properites pane
	// TODO: Fix Generic type arguments for propertiesObjectColumn
	@FXML
	public TableColumn<KeyValuePair, String> propertiesStringColumn;
	@FXML
	public TableColumn propertiesObjectColumn;
	@FXML
	public TableColumn<KeyValuePair, String> propertiesTypeColumn;

	@FXML
	public Text createModusText;
	@FXML
	public Text selectModusText;
	@FXML
	public Text actualLayerText;
	
	@FXML 
	public VBox symbolToolVBox;
	@FXML
	public CheckBox edgesVisibleCheckbox;
	@FXML
	public CheckBox nodeLabelCheckbox;
	@FXML
	public CheckBox edgeWeightCheckbox;
	
	@FXML
	public Pane worldViewPane;

	/**
	 * Initializes all the references to the UI elements specified in the FXML
	 * file. Gets called during FXML loading. Asserts the correct injection of
	 * all referenced UI elements and initializes them.
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// Assert the correct injection of all references from FXML
		assertFXMLInjections();

		// Give the AnimationTimer access to UI elements
		MyAnimationTimer.setGUIController(this);

		initializeToolbox();
		initializeProperties();

		// Remove Header for the toolbox
		removeHeaderTableView(toolbox);

		// Initialize the Managers for the various for UI elements
		ToolboxManager.initializeItems(toolbox);
		PropertiesManager.initializeItems(properties);
		GraphDisplayManager.setGuiController(this);

		// Bind all the handlers to their corresponding UI elements
		initializeZoomButtons();
		initializeLayerButton();
		//initializeDisplayPane();
		initializeMenuBar();
		initializeSymbolRepToolbox();

		// Initialize the Text Labels for displaying the current state of the
		// Application
		initializeTextFields();

		// Setup the Keyboard Shortcuts
		KeyboardShortcuts.initialize(Main.getInstance().getPrimaryStage());

		
		initializeWorldView();
	}

	// Initialize world view for symbolRep.
	private void initializeWorldView() {
		
		JXMapViewer mapViewer = new JXMapViewer();

		// Create a TileFactoryInfo for OpenStreetMap
		TileFactoryInfo info = new OSMTileFactoryInfo();
		DefaultTileFactory tileFactory = new DefaultTileFactory(info);
		mapViewer.setTileFactory(tileFactory);
		
		// Use 8 threads in parallel to load the tiles
		tileFactory.setThreadPoolSize(8);

		// Set the focus
		GeoPosition frankfurt = new GeoPosition(50.11, 8.68);

		mapViewer.setZoom(7);
		mapViewer.setAddressLocation(frankfurt);
		
		JPanel testPane = new JPanel();
		
		testPane.setSize(new Dimension(500, 500));
		testPane.add(mapViewer);
		
		swingNodeWorldView.setContent(testPane);
		
		swingNode.setVisible(false);
		pane.setVisible(false);
		
		worldViewPane.setMinSize(200, 200);
		worldViewPane.setMaxSize(1000, 1000);
		
	}

	/**
	 * Initializes the Menu Bar with all its contents.
	 */
	private void initializeMenuBar() {
		MenuBarManager.setGUIController(this);

		newItem.setOnAction(MenuBarManager.newHandler);
		open.setOnAction(MenuBarManager.openHandler);
		add.setOnAction(MenuBarManager.addHandler);
		save.setOnAction(MenuBarManager.saveHandler);
		saveAs.setOnAction(MenuBarManager.saveAsHandler);
		preferences.setOnAction(MenuBarManager.preferencesHandler);
		quit.setOnAction(MenuBarManager.quitHandler);
		delete.setOnAction(MenuBarManager.deleteHandler);
		undelete.setOnAction(MenuBarManager.undeleteHandler);
		selectMode.setOnAction(MenuBarManager.selectModeHandler);
		about.setOnAction(MenuBarManager.aboutHandler);

	}

	/**
	 * Sets the handlers for the zoomin and zoomout buttons.
	 */
	private void initializeZoomButtons() {
		zoomIn.setOnAction(ButtonManager.zoomInHandler);
		zoomOut.setOnAction(ButtonManager.zoomOutHandler);
	}
	
	private void initializeSymbolRepToolbox(){
		// Hide SymbolRep Toolbox View
		symbolToolVBox.setVisible(false);
		
		edgesVisibleCheckbox.selectedProperty().addListener(ButtonManager.edgeVisibleListener);
		nodeLabelCheckbox.selectedProperty().addListener(ButtonManager.nodeLabelListener);
		edgeWeightCheckbox.selectedProperty().addListener(ButtonManager.edgeWeightListener);
	}

	/**
	 * Set the Handlers for the Layer switch Buttons.
	 */
	private void initializeLayerButton() {
		underlayButton.setOnAction(ButtonManager.underlayHandler);
		operatorButton.setOnAction(ButtonManager.operatorHandler);
		mappingButton.setOnAction(ButtonManager.mappingHandler);
		symbolRepButton.setOnAction(ButtonManager.symbolRepHandler);

		ArrayList<Button> layerButtons = new ArrayList<Button>();
		layerButtons.add(underlayButton);
		layerButtons.add(operatorButton);
		layerButtons.add(mappingButton);
		layerButtons.add(symbolRepButton);
		ButtonManager.initialize(layerButtons, this);
	}

	/**
	 * Sets the minimum size and adds the handlers to the graph display.
	 */
	private void initializeDisplayPane() {
		pane.heightProperty().addListener(new ResizeListener(swingNode, pane));
		pane.widthProperty().addListener(new ResizeListener(swingNode, pane));
		pane.setOnScroll(GraphDisplayManager.scrollHandler);
		swingNode.setContent((JPanel) Main.getInstance().getGraphManager().getView());
		swingNode.setOnMouseClicked(ButtonManager.clickedHandler);
		swingNode.setOnMousePressed(GraphDisplayManager.rememberLastClickedPosHandler);
		swingNode.setOnMouseDragged(GraphDisplayManager.mouseDraggedHandler);
		
		pane.setMinSize(200, 200);
	}

	/**
	 * Initialize the Toolbox.
	 */
	private void initializeToolbox() {

		ToolboxManager.initialize(this);
		MyViewerListener.setGUIController(this);

		toolboxStringColumn.setCellValueFactory(new ToolboxManager.PairKeyFactory());
		toolboxObjectColumn.setCellValueFactory(new ToolboxManager.PairValueFactory());

		toolboxObjectColumn.setCellFactory(
				new Callback<TableColumn<Pair<Object, String>, Object>, TableCell<Pair<Object, String>, Object>>() {
					@Override
					public TableCell<Pair<Object, String>, Object> call(
							TableColumn<Pair<Object, String>, Object> column) {
						return new ToolboxManager.PairValueCell();
					}
				});

		toolbox.getColumns().setAll(toolboxObjectColumn, toolboxStringColumn);

		// Click event for TableView row
		toolbox.setRowFactory(tv -> {
			TableRow<Pair<Object, String>> row = new TableRow<>();
			row.setOnMouseClicked(ToolboxManager.rowClickedHandler);
			return row;
		});

		// nothing is selected at the start
		toolbox.getSelectionModel().clearSelection();

	}

	/**
	 * Initialize the Properties Window.
	 */
	private void initializeProperties() {

		propertiesObjectColumn.setResizable(true);
		propertiesStringColumn.setResizable(true);

		propertiesStringColumn.setCellValueFactory(new PropertyValueFactory<KeyValuePair, String>("key"));

		propertiesObjectColumn.setCellValueFactory(new PropertyValueFactory<KeyValuePair, Object>("value"));
		propertiesObjectColumn.setCellFactory(TextFieldTableCell.forTableColumn());
		propertiesObjectColumn.setOnEditCommit(PropertiesManager.setOnEditCommitHandler);

		propertiesTypeColumn.setCellValueFactory(new PropertyValueFactory<KeyValuePair, String>("classTypeAsString"));

		properties.getColumns().setAll(propertiesStringColumn, propertiesObjectColumn, propertiesTypeColumn);

		properties.setRowFactory(PropertiesManager.rightClickCallback);

		properties.setPlaceholder(new Label("No graph element selected"));
		properties.getSelectionModel().clearSelection();

	}

	/**
	 * Initialize the Text Labels for displaying the State of the Application.
	 */
	private void initializeTextFields() {
		createModusText.setText(Main.getInstance().getCreationMode().toString());
		selectModusText.setText(Main.getInstance().getSelectionMode().toString());
		actualLayerText.setText(GraphDisplayManager.getCurrentLayer().toString());
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

	/**
	 * Asserts the correct Injection of all Elements from the FXML File.
	 */
	private void assertFXMLInjections() {
		assert swingNode != null : "fx:id=\"swingNode\" was not injected: check your FXML file 'NewBetterCoolerWindowTest.fxml'.";
		assert pane != null : "fx:id=\"pane\" was not injected: check your FXML file 'NewBetterCoolerWindowTest.fxml'.";

		assert zoomIn != null : "fx:id=\"zoomIn\" was not injected: check your FXML file 'NewBetterCoolerWindowTest.fxml'.";
		assert zoomOut != null : "fx:id=\"zoomOut\" was not injected: check your FXML file 'NewBetterCoolerWindowTest.fxml'.";

		assert underlayButton != null : "fx:id=\"underlayButton\" was not injected: check your FXML file 'NewBetterCoolerWindowTest.fxml'.";
		assert operatorButton != null : "fx:id=\"operatorButton\" was not injected: check your FXML file 'NewBetterCoolerWindowTest.fxml'.";
		assert mappingButton != null : "fx:id=\"mappingButton\" was not injected: check your FXML file 'NewBetterCoolerWindowTest.fxml'.";
		assert symbolRepButton != null : "fx:id=\"symbolRepButton\" was not injected: check your FXML file 'NewBetterCoolerWindowTest.fxml'.";

		assert newItem != null : "fx:id=\"newItem\" was not injected: check your FXML file 'NewBetterCoolerWindowTest.fxml'.";
		assert open != null : "fx:id=\"open\" was not injected: check your FXML file 'NewBetterCoolerWindowTest.fxml'.";
		assert add != null : "fx:id=\"add\" was not injected: check your FXML file 'NewBetterCoolerWindowTest.fxml'.";
		assert save != null : "fx:id=\"save\" was not injected: check your FXML file 'NewBetterCoolerWindowTest.fxml'.";
		assert saveAs != null : "fx:id=\"saveAs\" was not injected: check your FXML file 'NewBetterCoolerWindowTest.fxml'.";
		assert preferences != null : "fx:id=\"preferences\" was not injected: check your FXML file 'NewBetterCoolerWindowTest.fxml'.";
		assert quit != null : "fx:id=\"quit\" was not injected: check your FXML file 'NewBetterCoolerWindowTest.fxml'.";
		assert delete != null : "fx:id=\"delete\" was not injected: check your FXML file 'NewBetterCoolerWindowTest.fxml'.";
		assert undelete != null : "fx:id=\"undelete\" was not injected: check your FXML file 'NewBetterCoolerWindowTest.fxml'.";
		assert selectMode != null : "fx:id=\"selectMode\" was not injected: check your FXML file 'NewBetterCoolerWindowTest.fxml'.";
		assert about != null : "fx:id=\"about\" was not injected: check your FXML file 'NewBetterCoolerWindowTest.fxml'.";

		assert layerListView != null : "fx:id=\"layerListView\" was not injected: check your FXML file 'NewBetterCoolerWindowTest.fxml'.";

		assert toolbox != null : "fx:id=\"toolbox\" was not injected: check your FXML file 'NewBetterCoolerWindowTest.fxml'.";
		assert properties != null : "fx:id=\"properties\" was not injected: check your FXML file 'NewBetterCoolerWindowTest.fxml'.";
		assert metricListView != null : "fx:id=\"metricListView\" was not injected: check your FXML file 'NewBetterCoolerWindowTest.fxml'.";

		assert toolboxStringColumn != null : "fx:id=\"toolboxString\" was not injected: check your FXML file 'NewBetterCoolerWindowTest.fxml'.";
		assert toolboxObjectColumn != null : "fx:id=\"toolboxObject\" was not injected: check your FXML file 'NewBetterCoolerWindowTest.fxml'.";

		assert propertiesStringColumn != null : "fx:id=\"propertiesString\" was not injected: check your FXML file 'NewBetterCoolerWindowTest.fxml'.";
		assert propertiesObjectColumn != null : "fx:id=\"propertiesObject\" was not injected: check your FXML file 'NewBetterCoolerWindowTest.fxml'.";
		assert propertiesTypeColumn != null : "fx:id=\"propertiesType\" was not injected: check your FXML file 'NewBetterCoolerWindowTest.fxml'.";

		assert createModusText != null : "fx:id=\"createModusText\" was not injected: check your FXML file 'NewBetterCoolerWindowTest.fxml'.";
		assert selectModusText != null : "fx:id=\"selectModusText\" was not injected: check your FXML file 'NewBetterCoolerWindowTest.fxml'.";
		assert actualLayerText != null : "fx:id=\"actualLayerText\" was not injected: check your FXML file 'NewBetterCoolerWindowTest.fxml'.";
		
		assert symbolToolVBox != null : "fx:id=\"symbolToolVBox\" was not injected: check your FXML file 'NewBetterCoolerWindowTest.fxml'.";
		assert edgesVisibleCheckbox != null : "fx:id=\"edgesVisibleCheckbox\" was not injected: check your FXML file 'NewBetterCoolerWindowTest.fxml'.";
		assert nodeLabelCheckbox != null : "fx:id=\"nodeLabelCheckbox\" was not injected: check your FXML file 'NewBetterCoolerWindowTest.fxml'.";
		assert edgeWeightCheckbox != null : "fx:id=\"egdeWeightCheckbox\" was not injected: check your FXML file 'NewBetterCoolerWindowTest.fxml'.";
		
		assert swingNodeWorldView != null : "fx:id=\"swingNodeWorldView\" was not injected: check your FXML file 'NewBetterCoolerWindowTest.fxml'.";
		assert worldViewPane != null : "fx:id=\"worldViewPane\" was not injected: check your FXML file 'NewBetterCoolerWindowTest.fxml'.";
	}
}
