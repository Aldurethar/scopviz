package de.tu_darmstadt.informatik.tk.scopviz.ui;

import org.graphstream.algorithm.Toolkit;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Element;
import org.graphstream.graph.Node;

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
	public static EventHandler<CellEditEvent<KeyValuePair, String>> setOnEditCommitHandler = new EventHandler<CellEditEvent<KeyValuePair, String>>() {

		@Override
		public void handle(CellEditEvent<KeyValuePair, String> t) {

			KeyValuePair editedPair = t.getTableView().getItems().get(t.getTablePosition().getRow());

			Object classType = editedPair.getClassType();
			String key = editedPair.getKey();

			editedPair.setValue(t.getNewValue());

			Visualizer viz = Main.getInstance().getVisualizer();
			Element selected;

			String nid = viz.getSelectedNodeID();
			String eid = viz.getSelectedEdgeID();

			if (nid != null) {
				selected = viz.getGraph().getNode(nid);
			} else if (eid != null) {
				selected = viz.getGraph().getEdge(eid);
			} else
				return;

			if (classType.equals(Integer.class)) {
				selected.changeAttribute(key, Integer.valueOf(editedPair.getValue()));

			} else if (classType.equals(Boolean.class)) {
				selected.changeAttribute(key, Boolean.valueOf(editedPair.getValue()));

			} else if (classType.equals(Float.class)) {
				selected.changeAttribute(key, Float.valueOf(editedPair.getValue()));

			} else if (classType.equals(Double.class)) {
				selected.changeAttribute(key, Double.valueOf(editedPair.getValue()));

			} else if (classType.equals(String.class)) {
				selected.changeAttribute(key, editedPair.getValue());

			}
		}
	};

	/**
	 * Sets Property-TableView Elements to selected Node or Edge Properties
	 */
	public static void setItemsProperties() {

		String nid = Main.getInstance().getVisualizer().getSelectedNodeID();
		String eid = Main.getInstance().getVisualizer().getSelectedEdgeID();

		if (nid != null) {
			Node selectedNode = Main.getInstance().getVisualizer().getGraph().getNode(nid);
			showNewDataSet(selectedNode);

		} else if (eid != null) {
			Edge selectedEdge = Main.getInstance().getVisualizer().getGraph().getEdge(eid);
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