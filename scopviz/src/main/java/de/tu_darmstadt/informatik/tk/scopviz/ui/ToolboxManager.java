package de.tu_darmstadt.informatik.tk.scopviz.ui;

import de.tu_darmstadt.informatik.tk.scopviz.main.CreateModus;
import de.tu_darmstadt.informatik.tk.scopviz.main.Main;
import de.tu_darmstadt.informatik.tk.scopviz.main.MainApp;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import javafx.util.Pair;

/**
 * Manager for the Toolbox pane. Der Jascha soll mal sein Ding da reinmergen
 * 
 * @author Dominik Renkel
 * @version 0.9
 *
 */
public class ToolboxManager {
	
	/**
	 * GUIController reference
	 */
	private static GUIController controller;
	
	/**
	 * Initialize Toolbox, set controller
	 * @param guiController
	 */
	public static void initialize(GUIController guiController){
		controller = guiController;
	}

	/**
	 * Initializes the toolbox to contain the specified list of entries.
	 * 
	 * @param toolbox
	 *            the list of entries to add to the toolbox
	 */
	public static void initializeItems(TableView<Pair<Object, String>> toolbox) {

		@SuppressWarnings("unchecked")
		ObservableList<Pair<Object, String>> data = FXCollections.observableArrayList(
				pair(new Image(MainApp.class.getResource("/png/node.png").toString()), "Standard"),
				pair(new Image(MainApp.class.getResource("/png/source.png").toString()), "Source"),
				pair(new Image(MainApp.class.getResource("/png/sink.png").toString()), "Sink"),
				pair(new Image(MainApp.class.getResource("/png/enProc.png").toString()), "EnProc"), 
				pair("", ""),
				pair(new Image(MainApp.class.getResource("/png/dirEdge.png").toString()), "Directed"),
				pair(new Image(MainApp.class.getResource("/png/undirEdge.png").toString()), "Undirected"));

		toolbox.getItems().setAll(data);
	}
	
	/**
	 * Handler for TableRows
	 */
	public static EventHandler<MouseEvent> rowClickedHandler = new EventHandler<MouseEvent>(){

		@SuppressWarnings("unchecked")
		@Override
		public void handle(MouseEvent event) {
			
			// Get TbaleRow
			Node node = ((Node) event.getTarget()).getParent();
            TableRow<Pair<Object, String>> row;
            
            if (node instanceof TableRow) {
                row = (TableRow<Pair<Object, String>>) node;
            } else {
                // clicking on text part
                row = (TableRow<Pair<Object, String>>) node.getParent();
            }
			
            Main main = Main.getInstance();
            
            // Set CreateModus based on pressed TableRow
			if (! row.isEmpty()) {
				
				String rowString = row.getItem().getValue();
				
	            if(rowString.equals("Standard")){
	            	if(main.getCreateModus().equals(CreateModus.CREATE_STANDARD_NODE))
	            		main.setCreateModus(CreateModus.CREATE_NONE);
	            	else
	            		main.setCreateModus(CreateModus.CREATE_STANDARD_NODE);
	            	
	            }else if(rowString.equals("Source")){
	            	if(main.getCreateModus().equals(CreateModus.CREATE_SOURCE_NODE))
	            		main.setCreateModus(CreateModus.CREATE_NONE);
	            	else
	            		main.setCreateModus(CreateModus.CREATE_SOURCE_NODE);
		            
	            }else if(rowString.equals("Sink")){
	            	if(main.getCreateModus().equals(CreateModus.CREATE_SINK_NODE))
	            		main.setCreateModus(CreateModus.CREATE_NONE);
	            	else
	            		main.setCreateModus(CreateModus.CREATE_SINK_NODE);
	            	
	            }else if(rowString.equals("EnProc")){
	            	if(main.getCreateModus().equals(CreateModus.CREATE_PROC_NODE))
	            		main.setCreateModus(CreateModus.CREATE_NONE);
	            	else
	            		main.setCreateModus(CreateModus.CREATE_PROC_NODE);
	            	
	            }else if(rowString.equals("Directed")){
	            	if(main.getCreateModus().equals(CreateModus.CREATE_DIRECTED_EDGE))
	            		main.setCreateModus(CreateModus.CREATE_NONE);
	            	else
	            		main.setCreateModus(CreateModus.CREATE_DIRECTED_EDGE);
	            	
	            }else if(rowString.equals("Undirected")){
	            	if(main.getCreateModus().equals(CreateModus.CREATE_UNDIRECTED_EDGE))
	            		main.setCreateModus(CreateModus.CREATE_NONE);
	            	else
	            		main.setCreateModus(CreateModus.CREATE_UNDIRECTED_EDGE);
	            }
	        }
			
			controller.createModusText.setText(Main.getInstance().getCreateModus().toString());
			
		}
		
	};

	private static Pair<Object, String> pair(Object picture, String name) {
		return new Pair<>(picture, name);
	}

	public static class PairKeyFactory
			implements Callback<TableColumn.CellDataFeatures<Pair<Object, String>, String>, ObservableValue<String>> {
		@Override
		public ObservableValue<String> call(TableColumn.CellDataFeatures<Pair<Object, String>, String> data) {
			return new ReadOnlyObjectWrapper<>(data.getValue().getValue());
		}
	}

	public static class PairValueFactory
			implements Callback<TableColumn.CellDataFeatures<Pair<Object, String>, Object>, ObservableValue<Object>> {
		@SuppressWarnings("unchecked")
		@Override
		public ObservableValue<Object> call(TableColumn.CellDataFeatures<Pair<Object, String>, Object> data) {
			Object value = data.getValue().getKey();
			return (value instanceof ObservableValue) ? (ObservableValue<Object>) value
					: new ReadOnlyObjectWrapper<>(value);
		}
	}

	public static class PairValueCell extends TableCell<Pair<Object, String>, Object> {
		@Override
		protected void updateItem(Object item, boolean empty) {
			super.updateItem(item, empty);

			if (item != null) {
				if (item instanceof String) {
					setText((String) item);
					setGraphic(null);
				} else if (item instanceof Integer) {
					setText(Integer.toString((Integer) item));
					setGraphic(null);
				} else if (item instanceof Boolean) {
					CheckBox checkBox = new CheckBox();
					checkBox.setSelected((boolean) item);
					setGraphic(checkBox);
				} else if (item instanceof Image) {
					setText(null);
					ImageView imageView = new ImageView((Image) item);
					imageView.setFitWidth(20);
					imageView.setFitHeight(20);
					imageView.setPreserveRatio(true);
					imageView.setSmooth(true);
					setGraphic(imageView);
				} else {
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
