package de.tu_darmstadt.informatik.tk.scopviz.ui;

import java.net.URL;
import java.util.ResourceBundle;

import de.tu_darmstadt.informatik.tk.scopviz.main.MainApp;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;

public class GUIController implements Initializable{
	
	@FXML public SwingNode swingNode;
	@FXML public Pane pane;
	
	@FXML public Button zoomIn;
	@FXML public Button zoomOut;
	
	@FXML public ScrollPane layerScrollPane;
	@FXML public ScrollPane propertiesScrollPane;
	@FXML public ScrollPane metricScrollPane;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		assert swingNode != null : "fx:id=\"swingNode\" was not injected: check your FXML file 'NewBetterCoolerWindowTest.fxml'.";
		
		assert pane != null : "fx:id=\"pane\" was not injected: check your FXML file 'NewBetterCoolerWindowTest.fxml'.";
		
		assert zoomIn != null : "fx:id=\"zoomIn\" was not injected: check your FXML file 'NewBetterCoolerWindowTest.fxml'.";
		assert zoomOut != null : "fx:id=\"zoomOut\" was not injected: check your FXML file 'NewBetterCoolerWindowTest.fxml'.";
		
		assert layerScrollPane != null : "fx:id=\"layerScrollPane\" was not injected: check your FXML file 'NewBetterCoolerWindowTest.fxml'.";
		assert propertiesScrollPane != null : "fx:id=\"propertiesScrollPane\" was not injected: check your FXML file 'NewBetterCoolerWindowTest.fxml'.";
		assert metricScrollPane != null : "fx:id=\"metricSrollPane\" was not injected: check your FXML file 'NewBetterCoolerWindowTest.fxml'.";
		
		MainApp.setGUIController(this);
	}

}
