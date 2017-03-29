package de.tu_darmstadt.informatik.tk.scopviz.ui;

import java.util.ArrayList;
import java.util.HashMap;

import de.tu_darmstadt.informatik.tk.scopviz.main.Main;
import de.tu_darmstadt.informatik.tk.scopviz.metrics.BasicMappingOperator;
import de.tu_darmstadt.informatik.tk.scopviz.metrics.TestOperator;
import de.tu_darmstadt.informatik.tk.scopviz.metrics.interfaces.ScopvizGraphOperator;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class OperatorManager {

	private static HashMap<String, ScopvizGraphOperator> operators = new HashMap<String, ScopvizGraphOperator>();

	/**
	 * Initializes all GraphOperators for employment
	 * 
	 * ****Central method to add a new metric***** Add line: addOperator(new
	 * YourMetric()); for using it in the Operatordialog
	 * **************************************************************
	 * 
	 */
	private static void initializeGraphOperators() {
		addOperator(new TestOperator());
		addOperator(new BasicMappingOperator());
	}

	public static void openOperatorsDialog() {
		Dialog<ArrayList<String>> addPropDialog = new Dialog<>();
		addPropDialog.setTitle("GraphOperators");

		ButtonType addButtonType = new ButtonType("invoke on current graph", ButtonData.OK_DONE);
		addPropDialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

		// create grid
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		// set DropDown Menu
		ChoiceBox<String> operatorChooser = new ChoiceBox<>();
		operatorChooser.setItems(FXCollections.observableArrayList(operators.keySet()));

		// adding elements to grid
		grid.add(new Label("Please select the operator you want to invoke on the current Gaph"), 0, 0);
		grid.add(operatorChooser, 0, 1);

		addPropDialog.getDialogPane().setContent(grid);

		Platform.runLater(() -> operatorChooser.requestFocus());

		// get new property values
		addPropDialog.setResultConverter(dialogButton -> {
			if (dialogButton == addButtonType) {
				operators.get(operatorChooser.getSelectionModel().getSelectedItem())
						.calculate(Main.getInstance().getGraphManager());
				return null;
			} else
				return null;

		});
		addPropDialog.showAndWait();

	}

	public static void addOperator(ScopvizGraphOperator op) {
		operators.put(op.getName(), op);
	}

	public static void initialize(GUIController g) {
		initializeGraphOperators();
	}
}
