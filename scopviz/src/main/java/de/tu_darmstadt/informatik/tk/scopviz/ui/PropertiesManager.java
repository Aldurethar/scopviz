package de.tu_darmstadt.informatik.tk.scopviz.ui;

import org.graphstream.algorithm.Toolkit;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Element;
import org.graphstream.graph.Node;

import de.tu_darmstadt.informatik.tk.scopviz.debug.Debug;
import com.sun.org.apache.bcel.internal.generic.GOTO;

import de.tu_darmstadt.informatik.tk.scopviz.main.GraphManager;
import de.tu_darmstadt.informatik.tk.scopviz.main.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;

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
			Element selected;

			String nid = viz.getSelectedNodeID();
			String eid = viz.getSelectedEdgeID();

			if (nid != null) {
				selected = viz.getGraph().getNode(nid);
			} else if (eid != null) {
				selected = viz.getGraph().getEdge(eid);
			} else
				return;
			
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
}