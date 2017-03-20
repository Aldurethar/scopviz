package de.tu_darmstadt.informatik.tk.scopviz.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Element;
import org.graphstream.graph.Node;

import de.tu_darmstadt.informatik.tk.scopviz.debug.Debug;
import de.tu_darmstadt.informatik.tk.scopviz.graphs.GraphManager;
import de.tu_darmstadt.informatik.tk.scopviz.main.Layer;
import de.tu_darmstadt.informatik.tk.scopviz.main.Main;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;

/**
 * Manager for the Properties pane and its contents.
 * 
 * @author Julian Ohl, Dominik Renkel
 * @version 1.0
 *
 */
public final class PropertiesManager {

	/** Regex for detecting whether a String represent an Integer. */
	public static final String IS_INT = "^(-)?\\d+$";
	/** Regex for detecting whether a String represents a Boolean. */
	public static final String IS_BOOL = "^true$|^false$";
	/**
	 * Regex for detecting whether a String represents a floating point number.
	 */
	public static final String IS_FLOAT = "^[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?$";

	/** The Table of Attributes. */
	private static TableView<KeyValuePair> properties;

	/** Flag whether the name has been set. */
	public static boolean nameSet;
	/** Flag whether the value has been set. */
	public static boolean valueSet;
	
	
	public static HashSet<TableRow<KeyValuePair>> tableRows = new HashSet<TableRow<KeyValuePair>>();
	private static HashMap<String, Integer> itemOrderRules = new HashMap<String,Integer>();
	
	/**
	 * Private Constructor to prevent Instantiation.
	 */
	private PropertiesManager() {
	}

	/**
	 * Initializes the Manager by adding the List of properties to display into
	 * the properties pane.
	 * 
	 * @param propertiesInput
	 *            The list of properties to display
	 */
	public static void initializeItems(TableView<KeyValuePair> propertiesInput) {

		properties = propertiesInput;
		setItemOrderRules();

	}
	
	
	private static void setItemOrderRules(){
		
		//
		itemOrderRules.put("x", 1);
		itemOrderRules.put("y", 2);
		
		//
		itemOrderRules.put("layout.frozen", -1);
		itemOrderRules.put("ui.style", -1);
		itemOrderRules.put("ui.j2dsk", -1);
		itemOrderRules.put("ui.clicked", -1);
		itemOrderRules.put("ui.map.selected", -1);
		itemOrderRules.put("xyz", -1);
		
		//
		itemOrderRules.put("mapping", -2);
		itemOrderRules.put("mapping-parent", -2);
		itemOrderRules.put("mapping-parent-id", -2);
		itemOrderRules.put("ui.class", -2);
		
		
	}
	
	/**
	 * Update Properties of selected Node/Edge, if a any Property was changed.
	 */
	public static final EventHandler<CellEditEvent<KeyValuePair, String>> setOnEditCommitHandler = new EventHandler<CellEditEvent<KeyValuePair, String>>() {

		@Override
		public void handle(CellEditEvent<KeyValuePair, String> t) {

			KeyValuePair editedPair = t.getTableView().getItems().get(t.getTablePosition().getRow());

			Object classType = editedPair.getClassType();
			String key = editedPair.getKey();

			// handling the problem when using his own names for properties
			// needed by graphstream
			// e.g. "ui.label" as "ID", might need an extra function/structure
			// if more of these are added
			if (key.equals("ID")) {
				key = "ui.label";
			}

			String oldValue = t.getOldValue();
			String newValue = t.getNewValue();

			Element selected = getSelected();

			// Type-Check the input
			if (classType.equals(Integer.class) && newValue.matches(IS_INT)) {
				selected.changeAttribute(key, Integer.valueOf(newValue));
				editedPair.setValue(newValue);
				Debug.out("Edited integer Attribute " + key);

			} else if (classType.equals(Boolean.class) && newValue.matches(IS_BOOL)) {
				selected.changeAttribute(key, Boolean.valueOf(newValue));
				editedPair.setValue(newValue);
				Debug.out("Edited boolean Attribute " + key);

			} else if (classType.equals(Float.class) && newValue.matches(IS_FLOAT)) {
				selected.changeAttribute(key, Float.valueOf(newValue));
				editedPair.setValue(newValue);
				Debug.out("Edited float Attribute " + key);

			} else if (classType.equals(Double.class) && newValue.matches(IS_FLOAT)) {
				selected.changeAttribute(key, Double.valueOf(newValue));
				editedPair.setValue(newValue);
				Debug.out("Edited double Attribute " + key);

			} else if (classType.equals(String.class)) {
				selected.changeAttribute(key, newValue);
				editedPair.setValue(newValue);
				Debug.out("Edited String Attribute " + key);

			} else {
				editedPair.setValue(oldValue);
				t.getTableView().getItems().get(t.getTablePosition().getRow()).setKey(oldValue);
				setItemsProperties();
				Debug.out("invalid input for this attribute type");
			}

			// Unselect row after updating Property
			properties.getSelectionModel().clearSelection();
		}
	};

	/**
	 * Callback to be executed when a right click occurs on the table.
	 */
	public static Callback<TableView<KeyValuePair>, TableRow<KeyValuePair>> rightClickCallback = new Callback<TableView<KeyValuePair>, TableRow<KeyValuePair>>() {
		@Override
		public TableRow<KeyValuePair> call(TableView<KeyValuePair> tableView) {

			final TableRow<KeyValuePair> row = new TableRow<>();

			// ContextMenu on non empty rows (add & delete)
			final ContextMenu menuOnNonEmptyRows = new ContextMenu();
			final MenuItem addPropMenuItem = new MenuItem("Add..");
			final MenuItem deletePropMenuItem = new MenuItem("Delete");

			// ContextMenu on empty rows (only add)
			final ContextMenu menuOnEmptyRows = new ContextMenu();
			final MenuItem onlyAddPropMenuItem = new MenuItem("Add..");

			// add functionality
			onlyAddPropMenuItem.setOnAction((event) -> addPropFunctionality());
			addPropMenuItem.setOnAction((event) -> addPropFunctionality());

			// delete functionality
			deletePropMenuItem.setOnAction((event) -> {
				Debug.out("Remove Element");
				removeProperty(row.getItem());
				properties.getItems().remove(row.getItem());
			});

			// Disable MenuItem in symbol layer
			//TODO
			onlyAddPropMenuItem.disableProperty().bind(GraphDisplayManager.inSymbolLayerProperty()); 
			addPropMenuItem.disableProperty().bind(GraphDisplayManager.inSymbolLayerProperty()); 
			deletePropMenuItem.disableProperty().bind(GraphDisplayManager.inSymbolLayerProperty()); 
			
			// add MenuItem to ContextMenu
			menuOnEmptyRows.getItems().add(onlyAddPropMenuItem);
			menuOnNonEmptyRows.getItems().addAll(addPropMenuItem, deletePropMenuItem);
			

			// when empty row right-clicked open special menu (only add),
			// otherwise normal menu (add & delete)
			row.contextMenuProperty().bind(Bindings.when(Bindings.isNotNull(row.itemProperty()))
					.then(menuOnNonEmptyRows).otherwise(menuOnEmptyRows));

			tableRows.add(row);
			return row;
		}
	};

	/**
	 * Sets Property-TableView Elements to selected Node or Edge Properties.
	 */
	public static void setItemsProperties() {

		String nid = Main.getInstance().getGraphManager().getSelectedNodeID();
		String eid = Main.getInstance().getGraphManager().getSelectedEdgeID();

		if (nid != null) {
			Node selectedNode = Main.getInstance().getGraphManager().getGraph().getNode(nid);
			showNewDataSet(selectedNode);

		} else if (eid != null) {
			Edge selectedEdge = Main.getInstance().getGraphManager().getGraph().getEdge(eid);
			showNewDataSet(selectedEdge);

		} else {
			return;
		}
	}

	/**
	 * Add properties of selected Node or Edge to Properties TableView.
	 * 
	 * @param selected
	 *            selected Node or Edge
	 * @param newData
	 */
	public static void showNewDataSet(Element selected) {

		ObservableList<KeyValuePair> newData = FXCollections.observableArrayList();
		
		if (selected == null) {
			properties.setItems(newData);
			return;
		}
		
		// fix for concurrentModification exception
		String[] temp = new String[0];
		temp = selected.getAttributeKeySet().toArray(temp);
		for (int i = 0; i < temp.length; i++) {
			String key = temp[i];
			switch (key) {
			// filter out or change attributes added by graphstream that are of
			// no use to the user
			case "ui.label":
				if (selected instanceof Node) {
					Object actualAttribute = selected.getAttribute(key);
					// replace UI Label with ID"
					key = "ID";
					newData.add(0, new KeyValuePair(key, String.valueOf(actualAttribute), actualAttribute.getClass()));
				}
				break;
			case "weight":
				if (selected instanceof Edge
						&& Layer.UNDERLAY == Main.getInstance().getGraphManager().getGraph().getAttribute("layer")) {
					newData.add(new KeyValuePair(key, selected.getAttribute(key).toString(), Double.class));
					break;
				}
				break;
			case "process-need":
				if (selected instanceof Node
						&& Layer.OPERATOR == Main.getInstance().getGraphManager().getGraph().getAttribute("layer")) {
					newData.add(new KeyValuePair(key, selected.getAttribute(key).toString(), Double.class));
					break;
				}
				break;
			case "process-max":
				if (selected instanceof Node
						&& Layer.UNDERLAY == Main.getInstance().getGraphManager().getGraph().getAttribute("layer")) {
					newData.add(new KeyValuePair(key, selected.getAttribute(key).toString(), Double.class));
					break;
				}
			case "typeOfDevice":
				if (selected instanceof Node
						&& Layer.UNDERLAY == Main.getInstance().getGraphManager().getGraph().getAttribute("layer")) {
					newData.add(new KeyValuePair(key, selected.getAttribute(key).toString(), String.class));
				}

			default:
				Object actualAttribute = selected.getAttribute(key);
				newData.add(new KeyValuePair(key, String.valueOf(actualAttribute), actualAttribute.getClass()));
				break;
			}
		}
		
		properties.setItems(groupProperties(newData));
	}

	/**
	 * Get the selected node or edge from the GraphManager.
	 * 
	 * @return selected node or egde
	 */
	private static Element getSelected() {
		GraphManager viz = Main.getInstance().getGraphManager();

		String nid = viz.getSelectedNodeID();
		String eid = viz.getSelectedEdgeID();

		if (nid != null) {
			return viz.getGraph().getNode(nid);
		} else if (eid != null) {
			return viz.getGraph().getEdge(eid);
		} else {
			return null;
		}
	}
	
	/**
	 * Delete a given Pair from the current Node or Edge.
	 * 
	 * @param pair
	 *            selectedProperty
	 */
	private static void removeProperty(KeyValuePair pair) {

		Element selected = getSelected();

		selected.removeAttribute(pair.getKey());

	}
	
	
	public static ObservableList<KeyValuePair> groupProperties(ObservableList<KeyValuePair> data) {
		ObservableList<KeyValuePair> newData = data;
		
		for(String key: itemOrderRules.keySet()){
			
			for(int i = 0; i < newData.size();i++){
				KeyValuePair kvp = newData.get(i);
				
				if(kvp.getKey().equals(key)){
					
					if(itemOrderRules.get(kvp.getKey()) == -1){
						newData.remove(kvp);
						break;
					}
					
					else if(itemOrderRules.get(kvp.getKey()) == -2){
						
						if (!Debug.DEBUG_ENABLED){
							newData.remove(kvp);
							break;
						}
						
					}
					
					else{
						newData.remove(kvp);
						newData.add(itemOrderRules.get(kvp.getKey()),kvp);
						break;
					}
					
					
				}
				
				
			}
		}
		
			
		return newData;
	}
	
	/**TODO Auslagern
	 * contextMenu add button functionality.
	 */
	private static void addPropFunctionality() {
		Debug.out("Add Element");

		// Create new Dialog
		Dialog<ArrayList<String>> addPropDialog = new Dialog<>();
		addPropDialog.setTitle("Add Property");
		addPropDialog.setHeaderText("Choose your Property Details");

		ButtonType addButtonType = new ButtonType("Confirm", ButtonData.OK_DONE);
		addPropDialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

		// create grid
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		// create dialog elements
		TextField name = new TextField();
		name.setPromptText("Name");
		TextField value = new TextField();
		value.setPromptText("Value");

		ChoiceBox<String> type = new ChoiceBox<String>();
		type.setItems(FXCollections.observableArrayList("Integer", "Float", "String", "Boolean"));
		type.getSelectionModel().selectFirst();

		// position elements on grid
		grid.add(new Label("Property Name:"), 0, 0);
		grid.add(name, 1, 0);
		grid.add(new Label("Property Value:"), 0, 1);
		grid.add(value, 1, 1);
		grid.add(new Label("Property Type:"), 0, 2);
		grid.add(type, 1, 2);

		javafx.scene.Node confirmButton = addPropDialog.getDialogPane().lookupButton(addButtonType);
		confirmButton.setDisable(true);

		nameSet = false;
		valueSet = false;

		// hide confirm button, when textfields empty
		name.textProperty().addListener((observable, oldValue, newValue) -> {
			PropertiesManager.nameSet = true;
			if (newValue.trim().isEmpty()) {
				PropertiesManager.nameSet = false;
				confirmButton.setDisable(true);
			} else if (PropertiesManager.valueSet) {
				confirmButton.setDisable(false);
			}
		});
		value.textProperty().addListener((observable, oldValue, newValue) -> {
			PropertiesManager.valueSet = true;
			if (newValue.trim().isEmpty()) {
				PropertiesManager.valueSet = false;
				confirmButton.setDisable(true);
			} else if (PropertiesManager.nameSet) {
				confirmButton.setDisable(false);
			}
		});

		// set dialog
		addPropDialog.getDialogPane().setContent(grid);

		Platform.runLater(() -> name.requestFocus());

		// get new property values
		addPropDialog.setResultConverter(dialogButton -> {
			if (dialogButton == addButtonType) {
				ArrayList<String> tmp = new ArrayList<String>();
				tmp.add(name.getText());
				tmp.add(value.getText());
				tmp.add(type.getValue());

				return tmp;
			} else {
				return null;
			}
		});

		Optional<ArrayList<String>> result = addPropDialog.showAndWait();

		// create new Property
		result.ifPresent(t -> {
			System.out.println("Name: " + t.get(0) + ", Value: " + t.get(1) + ", Type: " + t.get(2));

			Element selected = getSelected();

			if (t.get(2).equals("Integer")) {
				selected.addAttribute(t.get(0), Integer.valueOf(t.get(1)));
			} else if (t.get(2).equals("Float")) {
				selected.addAttribute(t.get(0), Float.valueOf(t.get(1)));
			} else if (t.get(2).equals("String")) {
				selected.addAttribute(t.get(0), String.valueOf(t.get(1)));
			} else if (t.get(2).equals("Boolean")) {
				selected.addAttribute(t.get(0), Boolean.valueOf(t.get(1)));
			}

			showNewDataSet(selected);
		});
	}
}