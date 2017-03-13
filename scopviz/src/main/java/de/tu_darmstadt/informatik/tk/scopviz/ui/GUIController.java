package de.tu_darmstadt.informatik.tk.scopviz.ui;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javax.swing.JPanel;
import javax.swing.event.MouseInputListener;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.input.CenterMapListener;
import org.jxmapviewer.input.PanKeyListener;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCursor;

import de.tu_darmstadt.informatik.tk.scopviz.main.Main;
import de.tu_darmstadt.informatik.tk.scopviz.metrics.TestMetric;
import de.tu_darmstadt.informatik.tk.scopviz.ui.handlers.KeyboardShortcuts;
import de.tu_darmstadt.informatik.tk.scopviz.ui.handlers.ResizeListener;
import de.tu_darmstadt.informatik.tk.scopviz.ui.mapView.WorldView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
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
	@FXML
	public StackPane stackPane;

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
	public MenuItem about;

	// The contents of the corresponding ScrollPanes
	@FXML
	public TableView<Pair<Object, String>> toolbox;
	@FXML
	public TableView<KeyValuePair> properties;
	@FXML
	public TableView<MetricRowData> metricbox;

	// The columns of the toolbox
	@FXML
	public TableColumn<Pair<Object, String>, String> toolboxStringColumn;
	@FXML
	public TableColumn<Pair<Object, String>, Object> toolboxObjectColumn;

	// The columns of the Properties pane
	// TODO: Fix Generic type arguments for propertiesObjectColumn
	@FXML
	public TableColumn<KeyValuePair, String> propertiesStringColumn;
	@FXML
	public TableColumn propertiesObjectColumn;
	@FXML
	public TableColumn<KeyValuePair, String> propertiesTypeColumn;

	// The columns of the metricbox
	@FXML
	public TableColumn<MetricRowData,String> metricBoxMetricColumn;
	@FXML
	public TableColumn<MetricRowData,String> metricBoxValueColumn;
	@FXML
	public TableColumn metricBoxUpdateColumn;

	@FXML
	public VBox symbolToolVBox;
	@FXML
	public CheckBox edgesVisibleCheckbox;
	@FXML
	public CheckBox nodeLabelCheckbox;
	@FXML
	public CheckBox edgeWeightCheckbox;
	@FXML
	public ChoiceBox<String> mapViewChoiceBox;
	
	//TODO
	@FXML
	public AnchorPane topLeftAPane;
	/**
	 * Initializes all the references to the UI elements specified in the FXML
	 * file. Gets called during FXML loading. Asserts the correct injection of
	 * all referenced UI elements and initializes them.
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// Assert the correct injection of all references from FXML
		assertFXMLInjections();

		initializeToolbox();
		initializeProperties();
		initializeMetricbox();

		// Remove Header for the toolbox
		removeHeaderTableView(toolbox);

		// Initialize the Managers for the various for UI elements
		ToolboxManager.initializeItems();
		PropertiesManager.initializeItems(properties);

		GraphDisplayManager.init(this);

		// Bind all the handlers to their corresponding UI elements
		initializeZoomButtons();
		initializeLayerButton();
		initializeMenuBar();
		initializeSymbolRepToolbox();

		initializeDisplayPane();

		initializeWorldView();

		// Setup the Keyboard Shortcuts
		KeyboardShortcuts.initialize(Main.getInstance().getPrimaryStage());

	}

	private void initializeWorldView() {

		JXMapViewer mapViewer = new JXMapViewer();

		WorldView.initAttributes(mapViewer, this);

		// center map if double clicked / middle clicked
		mapViewer.addMouseListener(new CenterMapListener(mapViewer));
		// zoom with mousewheel
		mapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCursor(mapViewer));
		// TODO make this work
		mapViewer.addKeyListener(new PanKeyListener(mapViewer));
		// "Drag map around" Listener
		MouseInputListener mia = new PanMouseInputListener(mapViewer);
		mapViewer.addMouseListener(mia);
		mapViewer.addMouseMotionListener(mia);

		swingNodeWorldView.setContent(mapViewer);

		// add resize Listener to the stackPane
		stackPane.heightProperty().addListener(new ResizeListener(swingNode, stackPane));
		stackPane.widthProperty().addListener(new ResizeListener(swingNode, stackPane));

		swingNodeWorldView.setVisible(false);
	}

	/**
	 * Initializes the Menu Bar with all its contents.
	 */
	private void initializeMenuBar() {
		newItem.setOnAction((event) -> MenuBarManager.newAction(event));
		open.setOnAction((event) -> MenuBarManager.openAction(event));
		add.setOnAction((event) -> MenuBarManager.addAction(event));
		save.setOnAction((event) -> MenuBarManager.saveAction(event));
		saveAs.setOnAction((event) -> MenuBarManager.saveAsAction(event));
		preferences.setOnAction((event) -> MenuBarManager.preferencesAction(event));
		quit.setOnAction((event) -> MenuBarManager.quitAction(event));
		delete.setOnAction((event) -> MenuBarManager.deleteAction(event));
		undelete.setOnAction((event) -> MenuBarManager.undeleteAction(event));
		about.setOnAction((event) -> MenuBarManager.aboutAction(event));

	}

	/**
	 * Sets the handlers for the zoomin and zoomout buttons.
	 */
	private void initializeZoomButtons() {
		zoomIn.setOnAction((event) -> ButtonManager.zoomInAction(event));
		zoomOut.setOnAction((event) -> ButtonManager.zoomOutAction(event));
	}

	/**
	 * Initializes the special Toolbox for the Symbol Representation Layer.
	 */
	private void initializeSymbolRepToolbox() {
		// Hide SymbolRep Toolbox View
		//TODO symbolToolVBox.setVisible(false);
		topLeftAPane.getChildren().remove(symbolToolVBox);
		
		edgesVisibleCheckbox.selectedProperty()
				.addListener((ov, oldVal, newVal) -> ButtonManager.edgeVisibilitySwitcher(ov, oldVal, newVal));
		nodeLabelCheckbox.selectedProperty()
				.addListener((ov, oldVal, newVal) -> ButtonManager.labelVisibilitySwitcher(ov, oldVal, newVal));
		edgeWeightCheckbox.selectedProperty()
				.addListener((ov, oldVal, newVal) -> ButtonManager.edgeWeightVisibilitySwitcher(ov, oldVal, newVal));

		mapViewChoiceBox.setItems(FXCollections.observableArrayList("Default", "Road", "Satellite", "Hybrid"));
		mapViewChoiceBox.getSelectionModel().selectFirst();
		mapViewChoiceBox.getSelectionModel().selectedItemProperty()
				.addListener((ov, oldVal, newVal) -> ButtonManager.mapViewChoiceChange(ov, oldVal, newVal));
		
		
		
	}

	/**
	 * Set the Handlers for the Layer switch Buttons.
	 */
	private void initializeLayerButton() {
		underlayButton.setOnAction((event) -> ButtonManager.underlayAction(event));
		operatorButton.setOnAction((event) -> ButtonManager.operatorAction(event));
		mappingButton.setOnAction((event) -> ButtonManager.mappingAction(event));
		symbolRepButton.setOnAction((event) -> ButtonManager.symbolRepAction(event));

		ArrayList<Button> layerButtons = new ArrayList<Button>();
		layerButtons.add(underlayButton);
		layerButtons.add(operatorButton);
		layerButtons.add(mappingButton);
		layerButtons.add(symbolRepButton);
		ButtonManager.initialize(layerButtons, this, underlayButton);
	}

	/**
	 * Sets the minimum size and adds the handlers to the graph display.
	 */
	private void initializeDisplayPane() {
		ResizeListener rLis = new ResizeListener(swingNode, pane);
		pane.heightProperty().addListener(rLis);
		pane.widthProperty().addListener(rLis);
		pane.setOnScroll(GraphDisplayManager.scrollHandler);
		swingNode.setContent((JPanel) Main.getInstance().getGraphManager().getView());
		pane.setMinSize(200, 200);
	}

	/**
	 * Initialize the Toolbox.
	 */
	@SuppressWarnings("unchecked")
	private void initializeToolbox() {

		ToolboxManager.initialize(this);

		toolboxStringColumn.setCellValueFactory(new ToolboxManager.PairKeyFactory());
		toolboxObjectColumn.setCellValueFactory(new ToolboxManager.PairValueFactory());

		toolboxObjectColumn.setCellFactory((column) -> {
			return new ToolboxManager.PairValueCell();
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
	@SuppressWarnings("unchecked")
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
	
	//TODO Test Reihe zum Vorführen
	public static MetricRowData testRowData = new MetricRowData(new TestMetric());
	
	/**
	 * Initialize the metric box
	 */
	@SuppressWarnings("unchecked")
	private void initializeMetricbox() {
		MetricboxManager.initialize(this);
		
		//TODO Möglicherweise auslagern
		metricbox.setRowFactory( tv -> {
		    TableRow<MetricRowData> row = new TableRow<>();
		    row.setOnMouseClicked(event -> {
		        if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
		        	MetricRowData rowData = row.getItem();
		             System.out.println(rowData.getKey());
		        }
		    });
		    return row ;
		});

		metricBoxMetricColumn.setResizable(true);
		metricBoxValueColumn.setResizable(true);

		metricBoxMetricColumn.setCellValueFactory(new PropertyValueFactory<MetricRowData, String>("key"));
		metricBoxValueColumn.setCellValueFactory(new PropertyValueFactory<MetricRowData, String>("value"));
		metricBoxUpdateColumn.setCellValueFactory(new PropertyValueFactory<>("checked"));
		metricBoxUpdateColumn.setCellFactory(CheckBoxTableCell.forTableColumn(metricBoxUpdateColumn));
		metricBoxUpdateColumn.setEditable(true);
		
		ObservableList<MetricRowData> data =
	              FXCollections.observableArrayList(testRowData);
		
		metricbox.getColumns().setAll(metricBoxMetricColumn, metricBoxValueColumn, metricBoxUpdateColumn);
		metricbox.setItems(data);
		
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
		assert swingNode != null : "fx:id=\"swingNode\" was not injected: check your FXML file 'MainWindow.fxml'.";
		assert pane != null : "fx:id=\"pane\" was not injected: check your FXML file 'MainWindow.fxml'.";

		assert zoomIn != null : "fx:id=\"zoomIn\" was not injected: check your FXML file 'MainWindow.fxml'.";
		assert zoomOut != null : "fx:id=\"zoomOut\" was not injected: check your FXML file 'MainWindow.fxml'.";

		assert underlayButton != null : "fx:id=\"underlayButton\" was not injected: check your FXML file 'MainWindow.fxml'.";
		assert operatorButton != null : "fx:id=\"operatorButton\" was not injected: check your FXML file 'MainWindow.fxml'.";
		assert mappingButton != null : "fx:id=\"mappingButton\" was not injected: check your FXML file 'MainWindow.fxml'.";
		assert symbolRepButton != null : "fx:id=\"symbolRepButton\" was not injected: check your FXML file 'MainWindow.fxml'.";

		assert newItem != null : "fx:id=\"newItem\" was not injected: check your FXML file 'MainWindow.fxml'.";
		assert open != null : "fx:id=\"open\" was not injected: check your FXML file 'MainWindow.fxml'.";
		assert add != null : "fx:id=\"add\" was not injected: check your FXML file 'MainWindow.fxml'.";
		assert save != null : "fx:id=\"save\" was not injected: check your FXML file 'MainWindow.fxml'.";
		assert saveAs != null : "fx:id=\"saveAs\" was not injected: check your FXML file 'MainWindow.fxml'.";
		assert preferences != null : "fx:id=\"preferences\" was not injected: check your FXML file 'MainWindow.fxml'.";
		assert quit != null : "fx:id=\"quit\" was not injected: check your FXML file 'MainWindow.fxml'.";
		assert delete != null : "fx:id=\"delete\" was not injected: check your FXML file 'MainWindow.fxml'.";
		assert undelete != null : "fx:id=\"undelete\" was not injected: check your FXML file 'MainWindow.fxml'.";
		assert about != null : "fx:id=\"about\" was not injected: check your FXML file 'MainWindow.fxml'.";

		assert toolbox != null : "fx:id=\"toolbox\" was not injected: check your FXML file 'MainWindow.fxml'.";
		assert properties != null : "fx:id=\"properties\" was not injected: check your FXML file 'MainWindow.fxml'.";
		assert metricbox != null : "fx:id=\"metricbox\" was not injected: check your FXML file 'MainWindow.fxml'.";

		assert toolboxStringColumn != null : "fx:id=\"toolboxString\" was not injected: check your FXML file 'MainWindow.fxml'.";
		assert toolboxObjectColumn != null : "fx:id=\"toolboxObject\" was not injected: check your FXML file 'MainWindow.fxml'.";

		assert propertiesStringColumn != null : "fx:id=\"propertiesString\" was not injected: check your FXML file 'MainWindow.fxml'.";
		assert propertiesObjectColumn != null : "fx:id=\"propertiesObject\" was not injected: check your FXML file 'MainWindow.fxml'.";
		assert propertiesTypeColumn != null : "fx:id=\"propertiesType\" was not injected: check your FXML file 'MainWindow.fxml'.";

		assert metricBoxMetricColumn != null : "fx:id=\"metricBoxMetricColumn\" was not injected: check your FXML file 'MainWindow.fxml'.";
		assert metricBoxValueColumn != null : "fx:id=\"metricBoxValueColumn\" was not injected: check your FXML file 'MainWindow.fxml'.";
		assert metricBoxUpdateColumn != null : "fx:id=\"metricBoxUpdateColumn\" was not injected: check your FXML file 'MainWindow.fxml'.";
		
		assert topLeftAPane != null : "fx:id=\"topLeftAPane\" was not injected: check your FXML file 'MainWindow.fxml'.";
		
		assert symbolToolVBox != null : "fx:id=\"symbolToolVBox\" was not injected: check your FXML file 'MainWindow.fxml'.";
		assert edgesVisibleCheckbox != null : "fx:id=\"edgesVisibleCheckbox\" was not injected: check your FXML file 'MainWindow.fxml'.";
		assert nodeLabelCheckbox != null : "fx:id=\"nodeLabelCheckbox\" was not injected: check your FXML file 'MainWindow.fxml'.";
		assert edgeWeightCheckbox != null : "fx:id=\"egdeWeightCheckbox\" was not injected: check your FXML file 'MainWindow.fxml'.";
		assert mapViewChoiceBox != null : "fx:id=\"mapViewChoiceBox\" was not injected: check your FXML file 'MainWindow.fxml'.";

		assert stackPane != null : "fx:id=\"stackPane\" was not injected: check your FXML file 'MainWindow.fxml'.";
		assert swingNodeWorldView != null : "fx:id=\"swingNodeWorldView\" was not injected: check your FXML file 'MainWindow.fxml'.";
	}
}
