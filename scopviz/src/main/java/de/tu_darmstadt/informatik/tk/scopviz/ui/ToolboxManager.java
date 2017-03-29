package de.tu_darmstadt.informatik.tk.scopviz.ui;

import java.util.Optional;

import org.graphstream.graph.Edge;

import de.tu_darmstadt.informatik.tk.scopviz.graphs.GraphHelper;
import de.tu_darmstadt.informatik.tk.scopviz.main.CreationMode;
import de.tu_darmstadt.informatik.tk.scopviz.main.Main;
import de.tu_darmstadt.informatik.tk.scopviz.main.MainApp;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import javafx.util.Pair;

/**
 * Manager for the Toolbox pane.
 * 
 * @author Dominik Renkel
 * @version 2.0
 *
 */
public final class ToolboxManager {

	/**
	 * GUIController reference
	 */
	private static GUIController controller;

	/**
	 * needed, so that MouseClickedEvent is not fired, when SelectionProperty
	 * was changed
	 */
	private static boolean selectedPropertyChanged = false;

	/**
	 * private constructor to prevent Instantiation.
	 */
	private ToolboxManager() {
	}

	/**
	 * Initialize Toolbox, set controller
	 * 
	 * @param guiController
	 */
	public static void initialize(GUIController guiController) {
		controller = guiController;
	}

	/**
	 * Initializes the toolbox to contain the specified list of entries.
	 * 
	 */
	public static void initializeItems() {

		setUnderlayItems();

	}

	/**
	 * Create Row Elements for underlay toolbox view
	 */
	public static void setUnderlayItems() {

		@SuppressWarnings("unchecked")
		ObservableList<Pair<Object, String>> data = FXCollections.observableArrayList(
				pair(new Image(MainApp.class
						.getResource("/de/tu_darmstadt/informatik/tk/scopviz/ui/standard_operator.png").toString()),
						"Standard"),
				pair(new Image(
						MainApp.class.getResource("/de/tu_darmstadt/informatik/tk/scopviz/ui/procEn.png").toString()),
						"ProcEn"),
				pair(new Image(
						MainApp.class.getResource("/de/tu_darmstadt/informatik/tk/scopviz/ui/source.png").toString()),
						"Source"),
				pair(new Image(
						MainApp.class.getResource("/de/tu_darmstadt/informatik/tk/scopviz/ui/sink.png").toString()),
						"Sink"),
				pair(new Image(MainApp.class.getResource("/de/tu_darmstadt/informatik/tk/scopviz/ui/undirEdge.png")
						.toString()), "Undirected"));

		controller.toolbox.getItems().setAll(data);
	}

	/**
	 * create row elements for operator toolbox view
	 */
	public static void setOperatorItems() {

		@SuppressWarnings("unchecked")
		ObservableList<Pair<Object, String>> data = FXCollections.observableArrayList(
				pair(new Image(MainApp.class
						.getResource("/de/tu_darmstadt/informatik/tk/scopviz/ui/standard_operator.png").toString()),
						"Operator"),
				pair(new Image(
						MainApp.class.getResource("/de/tu_darmstadt/informatik/tk/scopviz/ui/source.png").toString()),
						"Source"),
				pair(new Image(
						MainApp.class.getResource("/de/tu_darmstadt/informatik/tk/scopviz/ui/sink.png").toString()),
						"Sink"),
				pair(new Image(
						MainApp.class.getResource("/de/tu_darmstadt/informatik/tk/scopviz/ui/dirEdge.png").toString()),
						"Directed"));

		controller.toolbox.getItems().setAll(data);

	}

	/**
	 * create row elements for mapping toolbox view
	 */
	public static void setMappingItems() {

		@SuppressWarnings("unchecked")
		ObservableList<Pair<Object, String>> data = FXCollections.observableArrayList(pair(
				new Image(
						MainApp.class.getResource("/de/tu_darmstadt/informatik/tk/scopviz/ui/dirEdge.png").toString()),
				"Mapping Edge"));

		controller.toolbox.getItems().setAll(data);

	}

	/**
	 * fired when the selected item in the Toolbox was changed
	 * 
	 * @param ov
	 * @param oldVal
	 *            the previous selected item
	 * @param newVal
	 *            the new selected item
	 */
	public static void selectedItemChanged(ObservableValue<? extends Pair<Object, String>> ov,
			Pair<Object, String> oldVal, Pair<Object, String> newVal) {

		if (newVal != null) {

			// change creation mode based on selected item
			String rowString = newVal.getValue();

			if (rowString.equals("Standard")) {
				changeCreationMode(CreationMode.CREATE_STANDARD_NODE);

			} else if (rowString.equals("Source")) {
				changeCreationMode(CreationMode.CREATE_SOURCE_NODE);

			} else if (rowString.equals("Sink")) {
				changeCreationMode(CreationMode.CREATE_SINK_NODE);

			} else if (rowString.equals("ProcEn")) {
				changeCreationMode(CreationMode.CREATE_PROC_NODE);

			} else if (rowString.equals("Operator")) {
				changeCreationMode(CreationMode.CREATE_OPERATOR_NODE);

			} else if (rowString.equals("Directed")) {
				changeCreationMode(CreationMode.CREATE_DIRECTED_EDGE);

			} else if (rowString.equals("Undirected")) {
				changeCreationMode(CreationMode.CREATE_UNDIRECTED_EDGE);

			} else if (rowString.equals("Mapping Edge")) {
				changeCreationMode(CreationMode.CREATE_DIRECTED_EDGE);
			}

		} else {
			// selected item was an empty row
			Main.getInstance().setCreationMode(CreationMode.CREATE_NONE);
		}

		// Unselecet Rows if Creation Mode is None
		if (Main.getInstance().getCreationMode().equals(CreationMode.CREATE_NONE)) {
			controller.toolbox.getSelectionModel().clearSelection();
			Main.getInstance().getGraphManager().deselectEdgeCreationNodes();
		}

		// set this property to true, so the MouseClickedHandler doesn't also
		// fire its event
		selectedPropertyChanged = true;

	}

	/**
	 * fired when a row was clicked
	 * 
	 * @param event
	 */
	@SuppressWarnings("unchecked")
	public static void rowClickedHandler(MouseEvent event) {

		// only use the handler when the selectionProperty Listener didn't fire
		// its event
		if (selectedPropertyChanged) {
			selectedPropertyChanged = false;
			return;
		}

		// Get the clicked TableRow
		Node node = ((Node) event.getTarget()).getParent();
		TableRow<Pair<Object, String>> row = null;

		if (node instanceof TableRow) {
			row = (TableRow<Pair<Object, String>>) node;
		} else {
			// clicking on picture part
			try {
				row = (TableRow<Pair<Object, String>>) node.getParent();

			} catch (ClassCastException e) {
				// there was a dragging move one the picture part when this
				// exception is thrown -> cant get row from this action
				return;
			}
		}

		// clear selection if selected row was clicked again
		if (row.isEmpty()
				|| controller.toolbox.getSelectionModel().selectedItemProperty().get().equals(row.getItem())) {
			controller.toolbox.getSelectionModel().clearSelection();
		}
	}

	/**
	 * If currentMode already selected then deselect, otherwise set mode on
	 * currentMode
	 * 
	 * @param currentMode
	 */
	private static void changeCreationMode(CreationMode currentMode) {

		if (Main.getInstance().getCreationMode().equals(currentMode))
			Main.getInstance().setCreationMode(CreationMode.CREATE_NONE);
		else
			Main.getInstance().setCreationMode(currentMode);
	}

	/**
	 * create a pair object under given picture and name
	 * 
	 * @param picture
	 * @param name
	 * @return
	 */
	private static Pair<Object, String> pair(Object picture, String name) {
		return new Pair<>(picture, name);
	}

	/**
	 * the last edge that was created
	 */
	private static Edge lastCreatedEdge = null;

	/**
	 * opens a dialog that asks for a weight for a newly created Edge. The
	 * default value is Optionsmanager.getDefaultWeight()
	 * 
	 * @param e
	 *            the new Edge that needs a weight
	 */
	public static void createWeightDialog(Edge e) {
		if (e.equals(lastCreatedEdge)) {
			return;
		}
		lastCreatedEdge = e;
		Platform.runLater(() -> {
			TextInputDialog weightDialog = new TextInputDialog(Double.toString(OptionsManager.getDefaultWeight()));
			weightDialog.setTitle("Edge Weight");
			weightDialog.setHeaderText("Please enter the weight of the Edge");
			weightDialog.setContentText("Edge Weight");
			Optional<String> result = weightDialog.showAndWait();
			if (result.isPresent()) {
				e.addAttribute("weight", Double.parseDouble(result.get()));
				GraphHelper.propagateAttribute(Main.getInstance().getGraphManager().getGraph(), e, "weight",
						Double.parseDouble(result.get()));

			}
		});
	}

	/**
	 * Class for getting the string out of the pair elements in each row
	 *
	 */
	public static class PairKeyFactory
			implements Callback<TableColumn.CellDataFeatures<Pair<Object, String>, String>, ObservableValue<String>> {
		@Override
		public ObservableValue<String> call(TableColumn.CellDataFeatures<Pair<Object, String>, String> data) {
			return new ReadOnlyObjectWrapper<>(data.getValue().getValue());
		}
	}

	/**
	 * Class for getting the picture out of the pair elements in each row
	 *
	 */
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

	/**
	 * The actual TableCell, that renders the images of nodes and edges. (Image,
	 * String)-Cell additional support for (String, String), (Integer, String),
	 * (Boolean, String), ("N/A", String) table cells
	 *
	 */
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

	private static org.graphstream.graph.Node lastCreatedNode = null;

	public static void createProcMaxDialog(org.graphstream.graph.Node n) {
		if (n.equals(lastCreatedNode)) {
			return;
		}
		lastCreatedNode = n;
		Platform.runLater(() -> {
			TextInputDialog weightDialog = new TextInputDialog(Double.toString(OptionsManager.getDefaultWeight()));
			weightDialog.setTitle("Maximum Processing Power");
			weightDialog.setHeaderText("Please enter the maximum processing power of the Node");
			weightDialog.setContentText("processing power");
			Optional<String> result = weightDialog.showAndWait();
			org.graphstream.graph.Node actualNode = Main.getInstance().getGraphManager().getGraph().getNode(n.getId());
			if (result.isPresent()) {
				actualNode.addAttribute("process-max", Double.parseDouble(result.get()));
				GraphHelper.propagateAttribute(Main.getInstance().getGraphManager().getGraph(), actualNode,
						"process-max", Double.parseDouble(result.get()));
			}
			PropertiesManager.setItemsProperties();
		});
	}

	public static void createProcNeedDialog(org.graphstream.graph.Node n) {
		if (n.equals(lastCreatedNode)) {
			return;
		}
		lastCreatedNode = n;
		Platform.runLater(() -> {
			TextInputDialog weightDialog = new TextInputDialog(Double.toString(OptionsManager.getDefaultWeight()));
			weightDialog.setTitle("needed Processing power");
			weightDialog.setHeaderText("Please enter the amount of processing power the node needs");
			weightDialog.setContentText("neede Power");
			Optional<String> result = weightDialog.showAndWait();
			org.graphstream.graph.Node actualNode = Main.getInstance().getGraphManager().getGraph().getNode(n.getId());
			if (result.isPresent()) {
				actualNode.addAttribute("process-need", Double.parseDouble(result.get()));
				GraphHelper.propagateAttribute(Main.getInstance().getGraphManager().getGraph(), actualNode,
						"process-need", Double.parseDouble(result.get()));
			}
		});
	}
}
