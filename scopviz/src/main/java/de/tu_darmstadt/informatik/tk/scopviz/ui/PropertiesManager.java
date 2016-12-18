package de.tu_darmstadt.informatik.tk.scopviz.ui;

import java.util.ArrayList;
import java.util.Optional;

import org.graphstream.algorithm.Toolkit;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Element;
import org.graphstream.graph.Node;

import de.tu_darmstadt.informatik.tk.scopviz.debug.Debug;
import com.sun.org.apache.bcel.internal.generic.GOTO;

import de.tu_darmstadt.informatik.tk.scopviz.main.GraphManager;
import de.tu_darmstadt.informatik.tk.scopviz.main.Main;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.util.Callback;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

/**
 * Manager for the Properties pane and its contents.
 * 
 * @author Julian Ohl, Dominik Renkel
 * @version 1.0
 *
 */
public class PropertiesManager {

	public static final String IS_INT = "^(-)?\\d+$";
	public static final String IS_BOOL = "^true$|^false$";
	public static final String IS_FLOAT = "^[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?$";

	private static TableView<KeyValuePair> properties;
	
	public static boolean nameSet;
	public static boolean valueSet;

	/**
	 * Initializes the Manager by adding the List of properties to display into
	 * the properties pane.
	 * 
	 * @param properties
	 *            The list of properties to display
	 */
	public static void initializeItems(TableView<KeyValuePair> propertiesInput) {

		properties = propertiesInput;

	}

	/**
	 * Update Properties of selected Node/Edge, if a any Property was changed
	 */
	public static final EventHandler<CellEditEvent<KeyValuePair, String>> setOnEditCommitHandler = new EventHandler<CellEditEvent<KeyValuePair, String>>() {

		@Override
		public void handle(CellEditEvent<KeyValuePair, String> t) {

			KeyValuePair editedPair = t.getTableView().getItems().get(t.getTablePosition().getRow());

			Object classType = editedPair.getClassType();
			String key = editedPair.getKey();
			String oldValue = t.getOldValue();
			String newValue = t.getNewValue();
			
			GraphManager viz = Main.getInstance().getGraphManager();
			Element selected =getSelected();

			String nid = viz.getSelectedNodeID();
			String eid = viz.getSelectedEdgeID();

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
				selected.changeAttribute(key, Float.valueOf(newValue));
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
		}
	};
	
	public static Callback<TableView<KeyValuePair>, TableRow<KeyValuePair>> rightClickCallback = new Callback<TableView<KeyValuePair>, TableRow<KeyValuePair>>() {  
        @Override  
        public TableRow<KeyValuePair> call(TableView<KeyValuePair> tableView) {  
        	
            final TableRow<KeyValuePair> row = new TableRow<>(); 
            
            final ContextMenu menuOnNonEmptyRows = new ContextMenu();
            final ContextMenu menuOnEmptyRows = new ContextMenu();
            
            final MenuItem onlyAddPropMenuItem = new MenuItem("Add..");
            final MenuItem addPropMenuItem = new MenuItem("Add..");
    		final MenuItem deletePropMenuItem = new MenuItem("Delete");
            
    		
    		onlyAddPropMenuItem.setOnAction(addPropHandler);
    		
    		addPropMenuItem.setOnAction(addPropHandler);
    		
            deletePropMenuItem.setOnAction(new EventHandler<ActionEvent>() {  
                @Override  
                public void handle(ActionEvent event) { 
                	Debug.out("Remove Element");
                	removeProperty(row.getItem());
                    properties.getItems().remove(row.getItem());  
                }  
            });
            
            menuOnEmptyRows.getItems().add(onlyAddPropMenuItem);
            menuOnNonEmptyRows.getItems().addAll(addPropMenuItem, deletePropMenuItem);
            
            row.contextMenuProperty().bind(
            	      Bindings.when(Bindings.isNotNull(row.itemProperty()))
            	      .then(menuOnNonEmptyRows)
            	      .otherwise(menuOnEmptyRows));	      
            
            return row;  
        }  
    };

	/**
	 * Sets Property-TableView Elements to selected Node or Edge Properties
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

		} else
			return;
	}

	/**
	 * Add properties of selected Node or Edge to Properties TableView
	 * 
	 * @param selected
	 *            selected Node or Edge
	 * @param newData
	 */
	private static void showNewDataSet(Element selected) {

		ObservableList<KeyValuePair> newData = FXCollections.observableArrayList();

		for (String key : selected.getAttributeKeySet()) {

			if (key.equals("xyz") && selected instanceof Node) {

				double[] pos = Toolkit.nodePosition((Node) selected);

				newData.add(new KeyValuePair("x", String.valueOf(pos[0]), double.class));
				newData.add(new KeyValuePair("y", String.valueOf(pos[1]), double.class));
				newData.add(new KeyValuePair("z", String.valueOf(pos[2]), double.class));

			} else {
				Object actualAttribute = selected.getAttribute(key);

				newData.add(new KeyValuePair(key, String.valueOf(actualAttribute), actualAttribute.getClass()));
			}

		}

		properties.setItems(newData);
	}
	
	/**
	 * Get the selceted node or edge 
	 * @return selected node or egde
	 */
	private static Element getSelected(){
		GraphManager viz = Main.getInstance().getGraphManager();
		
		String nid = viz.getSelectedNodeID();
		String eid = viz.getSelectedEdgeID();

		if (nid != null) {
			return viz.getGraph().getNode(nid);
		} else if (eid != null) {
			return viz.getGraph().getEdge(eid);
		} else
			return null;
	}
	
	/**
	 * Delete a given Pair from the current Node or Edge
	 * @param pair selectedProperty
	 */
	private static void removeProperty(KeyValuePair pair){
		
		Element selected = getSelected();
		
		selected.removeAttribute(pair.getKey());
		
		// Caution VERY BAD CODE! BY JULIAN 
		Debug.out(String.valueOf(properties.getPrefWidth())); 
	}
	
	/**
	 * MenuItem Handler, clicked on MenuItem
	 */
	private static EventHandler<ActionEvent> addPropHandler = new EventHandler<ActionEvent>() {  
        @Override  
        public void handle(ActionEvent event) {  
            Debug.out("Add Element"); 
            addProperty();
        }  
    };
	
    /**
     * Add a new property to the selected node or edge
     */
	private static void addProperty(){
		
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
			if(newValue.trim().isEmpty()){
				PropertiesManager.nameSet = false;
				confirmButton.setDisable(true);
			}else if(PropertiesManager.valueSet){
				confirmButton.setDisable(false);
			}
		});
		value.textProperty().addListener((observable, oldValue, newValue) -> {
			PropertiesManager.valueSet = true;
			if(newValue.trim().isEmpty()){
				PropertiesManager.valueSet = false;
				confirmButton.setDisable(true);
			}else if(PropertiesManager.nameSet){
				confirmButton.setDisable(false);
			}
		});
		
		
		// set dialog
		addPropDialog.getDialogPane().setContent(grid);
		
		Platform.runLater(() -> name.requestFocus()); 
		
		
		// get new property values
		addPropDialog.setResultConverter(dialogButton -> { 
			if(dialogButton == addButtonType){
				ArrayList<String> tmp = new ArrayList<String>();
				tmp.add(name.getText());
				tmp.add(value.getText());
				tmp.add(type.getValue());
				
				return tmp;
			}else 
				return null;
			
		});
		
		Optional<ArrayList<String>> result = addPropDialog.showAndWait();
		
		
		// create new Property
		result.ifPresent(t -> {
			System.out.println("Name: "+ t.get(0)+ ", Value: "+ t.get(1)+ ", Type: "+t.get(2));
			
			Element selected = getSelected();
			
			if(t.get(2).equals("Integer")){
				selected.addAttribute(t.get(0), Integer.valueOf(t.get(1)));
			}else if(t.get(2).equals("Float")){
				selected.addAttribute(t.get(0), Float.valueOf(t.get(1)));
			}else if(t.get(2).equals("String")){
				selected.addAttribute(t.get(0), String.valueOf(t.get(1)));
			}else if(t.get(2).equals("Boolean")){
				selected.addAttribute(t.get(0), Boolean.valueOf(t.get(1)));
			}
			
			showNewDataSet(selected);
		});
	}
}