package de.tu_darmstadt.informatik.tk.scopviz.ui;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;

import de.tu_darmstadt.informatik.tk.scopviz.main.Main;
import de.tu_darmstadt.informatik.tk.scopviz.main.MainApp;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Callback;
import javafx.util.Pair;

/**
 * Manager for the Properties pane and its contents.
 * 
 * @author Julian Ohl
 * @version 1.0
 *
 */
public class PropertiesManager {

	private static TableView<Pair<String, Object>> properties;
	/**
	 * Initializes the Manager by adding the List of properties to display into
	 * the properties pane.
	 * 
	 * @param properties
	 *            The list of properties to display
	 */
	public static void initializeItems(TableView<Pair<String, Object>> propertiesInput) {
		
		properties = propertiesInput;
		
		@SuppressWarnings("unchecked")
		ObservableList<Pair<String, Object>> data = FXCollections.observableArrayList(
                pair("Color", "Green"),
                pair("ID", "20102"),
                pair("x-Pos", "0"),
                pair("y-Pos", "0"),
                pair("Attribute", "Fuck this Shit")
                
        );
		
		properties.getItems().setAll(data);
	}
	
	public static void setItemsProperties(){
		Node selectedNode;
		Edge selectedEdge;
		String nid = Main.getInstance().getVisualizer().getSelectedNodeID();
		String eid = Main.getInstance().getVisualizer().getSelectedEdgeID();
		
		selectedNode = Main.getInstance().getVisualizer().getGraph().getNode(nid);
		selectedEdge = Main.getInstance().getVisualizer().getGraph().getEdge(eid);
		
		if (selectedNode == null && selectedEdge ==null){
			return;
		}
		ObservableList<Pair<String, Object>> newData = FXCollections.observableArrayList();
		if (selectedNode != null){
			for(String key : selectedNode.getAttributeKeySet()){
				
				TextField textField = new TextField(selectedNode.getAttribute(key).toString());
				
				newData.add(pair(key, textField));
			}
		} else if (selectedEdge != null){
			for(String key : selectedEdge.getAttributeKeySet()){
				
				TextField textField = new TextField(selectedEdge.getAttribute(key).toString());
				
				newData.add(pair(key, textField));
			}
		}
		
		
		properties.getItems().setAll(newData);
	}
	
	
	

	private static Pair<String, Object> pair(String name, Object textfield) {
	        return new Pair<>(name, textfield);
	    }
	 
	 public static class PairKeyFactory implements Callback<TableColumn.CellDataFeatures<Pair<String, Object>, String>, ObservableValue<String>> {
		    @Override
		    public ObservableValue<String> call(TableColumn.CellDataFeatures<Pair<String, Object>, String> data) {
		        return new ReadOnlyObjectWrapper<>(data.getValue().getKey());
		    }
		}

	 public static class PairValueFactory implements Callback<TableColumn.CellDataFeatures<Pair<String, Object>, Object>, ObservableValue<Object>> {
		    @SuppressWarnings("unchecked")
		    @Override
		    public ObservableValue<Object> call(TableColumn.CellDataFeatures<Pair<String, Object>, Object> data) {
		        Object value = data.getValue().getValue();
		        return (value instanceof ObservableValue)
		                ? (ObservableValue<Object>) value
		                : new ReadOnlyObjectWrapper<>(value);
		    }
		}
		
	 public static class PairValueCell extends TableCell<Pair<String, Object>, Object> {
		    @Override
		    protected void updateItem(Object item, boolean empty) {
		        super.updateItem(item, empty);

		        if (item != null) {
		            if (item instanceof String) {
		                setText((String) item);
		                setGraphic(null);
		            }else if (item instanceof TextField) {
		            	setText(null);
		            	setGraphic((TextField) item);
		            }else {
		                setText("N/A");
		                setGraphic(null);
		            }
		        } else {
		            setText(null);
		            setGraphic(null);
		        }
		    }
		}
}
