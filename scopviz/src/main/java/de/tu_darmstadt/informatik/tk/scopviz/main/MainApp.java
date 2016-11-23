package de.tu_darmstadt.informatik.tk.scopviz.main;

import java.awt.Dimension;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.Iterator;
import java.util.Random;

import javax.swing.JPanel;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.ui.geom.Vector3;
import org.graphstream.ui.swingViewer.ViewPanel;
import org.graphstream.ui.swingViewer.util.GraphMetrics;
import org.graphstream.ui.view.View;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import de.tu_darmstadt.informatik.tk.scopviz.debug.Debug;
import de.tu_darmstadt.informatik.tk.scopviz.io.GraphMLImporter;
import de.tu_darmstadt.informatik.tk.scopviz.ui.GUIController;
import de.tu_darmstadt.informatik.tk.scopviz.ui.Visualizer;

/**
 * Main Class, initializes Graph, displays UI.
 * 
 * @author Jan Enders (jan.enders@stud.tu-darmstadt.de)
 * @version 1.0
 *
 */
public class MainApp extends Application {

	/**
	 * Primary Stage for the UI Scene.
	 */
	private Stage primaryStage;
	/**
	 * Root Object of the Scene Graph.
	 */
	private VBox rootLayout;
	/**
	 * Graph (Network or Operator Graph) to display.
	 */
	private Graph graph;
	/**
	 * Preferred size for the Graph Viewer.
	 */
	private final Dimension preferredViewerSize = new Dimension(425, 367);
	
	private Visualizer visualizer;
	private ViewPanel view;
	
	private static GUIController guiController;

	private enum Mod {
		NORMAL, CREATE_NODE, CREATE_EDGE, FIRST_NODE_SELECTED
	}

	private String firstNode;

	private Mod modus = Mod.NORMAL;

	/**
	 * Main Method, launches the Application.
	 * 
	 * @param args
	 *            Optional String arguments (command line flags), none
	 *            implemented
	 */
	public static void main(final String[] args) {
		launch(args);
	}

	/**
	 * Initializes the Graph by importing it from a GraphML file.
	 */
	@Override
	public void init() {
		GraphMLImporter importer = new GraphMLImporter();
		graph = importer.readGraph(MainApp.class.getResource("/Example.graphml"));
		visualizer =  new Visualizer(graph);
	}

	/**
	 * Starts the Application by initializing the UI Layout.
	 */
	@Override
	public void start(final Stage stage) {
		this.primaryStage = stage;

		initRootLayout();

	}

	/**
	 * initializes the UI Layout by loading it from a FXML file.
	 */
	public void initRootLayout() {

		// Load root layout from fxml file.
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("/NewBetterCoolerWindowTest.fxml"));
			rootLayout = (VBox) loader.load();
		} catch (IOException e) {
			System.err.println("FXML File could not be loaded. Could the Path be incorrect?");
			e.printStackTrace();
		}

		// Show the scene containing the root layout.
		Scene scene = new Scene(rootLayout);

		SwingNode swingNode = guiController.swingNode;
		Pane pane = guiController.pane;
		Button createNodeButton = guiController.createNode;
		Button createEdgeButton = guiController.createEdge;
		
		
		view = visualizer.getView();
		view.setPreferredSize(preferredViewerSize);

		swingNode.setContent((JPanel) view);
		pane.setMinSize(200, 200);
		

		ChangeListener<Number> resizeListener = new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
				view.setPreferredSize(new Dimension((int) pane.getWidth()-10, (int) pane.getHeight()-10));
				
				swingNode.setContent(view);
			}

		};

		pane.heightProperty().addListener(resizeListener);
		pane.widthProperty().addListener(resizeListener);
		
		guiController.zoomIn.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent evt){
				view.getCamera().setViewPercent(view.getCamera().getViewPercent()*0.95);
			}
		});
		
		guiController.zoomOut.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent evt){
				view.getCamera().setViewPercent(view.getCamera().getViewPercent()*1.05);
			}
		});
		
		createNodeButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				switch (modus) {
				case CREATE_NODE:
					modus = Mod.NORMAL;
					Debug.out("Modus set to Normal");
					createNodeButton.setText("Knoten hinzufügen");
					break;
				case NORMAL:
					modus = Mod.CREATE_NODE;
					Debug.out("Modus set to Create Node");
					createNodeButton.setText("Ende");
					break;
				default:
					modus = Mod.CREATE_NODE;
					Debug.out("Modus set to Create Node");
					createNodeButton.setText("Ende");
					createEdgeButton.setText("Kante hinzufügen");
					firstNode = "";
					break;
				}
			}
		});

		createEdgeButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				firstNode = "";
				switch (modus) {
				case CREATE_EDGE:
				case FIRST_NODE_SELECTED:
					modus = Mod.NORMAL;
					Debug.out("Modus set to Normal");
					createEdgeButton.setText("Kante hinzufügen");
					break;
				case NORMAL:
					modus = Mod.CREATE_EDGE;
					Debug.out("Modus set to Create Edge");
					createEdgeButton.setText("Ende");
					break;
				default:
					modus = Mod.CREATE_EDGE;
					Debug.out("Modus set to Create Edge");
					createEdgeButton.setText("Ende");
					createNodeButton.setText("Knoten hinzufügen");
					break;
				}
			}
		});

		swingNode.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				double x = event.getX();
				double trueX = (x - 45) / 3 + 100;
				double y = event.getY();
				double trueY = (y - 30) / (-3)  +200;
					Debug.out("-M (" + trueX + "/" + trueY + ")");
				if (modus == Mod.CREATE_NODE) {
					Node n = graph.addNode(getUnusedID());
					GraphMetrics gm = view.getCamera().getMetrics();
					Vector3 vc3 = gm.getSize();
					Debug.out("(x/y): " + vc3.x() + "/" + vc3.y());
					vc3.x();
					vc3.y();
					n.setAttribute("x", trueX);
					n.setAttribute("y", trueY);
					Debug.out("Created a dot on (" + trueX + "/" + trueY + ")");
				} else if (modus == Mod.CREATE_EDGE || modus == Mod.FIRST_NODE_SELECTED) {
					Iterator<Node> itr = graph.getNodeIterator();
					double d = Double.MAX_VALUE;
					String id = null;
					while (itr.hasNext()) {
						Node curN = itr.next();
						double nodeX = curN.getAttribute("x");
						double nodeY =  curN.getAttribute("y");
						double curD = Math.sqrt(Math.pow(nodeX - trueX, 2.0) + Math.pow(nodeY - trueY, 2.0));
						Debug.out("+" + curN.getId() + " (" + nodeX + "/" + nodeY + ")");
						if (curD < d) {
							d = curD;
							id = curN.getId();
						}
					}

					Debug.out(id + " pressed");

					if (id == null) {
						Debug.out("nothing selected");
						return;
					}
					switch (modus) {
					case CREATE_EDGE:
						firstNode = id;
						modus = Mod.FIRST_NODE_SELECTED;
						break;
					case FIRST_NODE_SELECTED:
						if (!id.matches(firstNode)) {
							graph.addEdge(getUnusedID(), firstNode, id);
							Debug.out("Created a edge between " + firstNode + " and " + id);
						}
						firstNode = "";
						modus = Mod.CREATE_EDGE;
						break;
					default:
						break;

					}
				}
			}

		});
		
		
		primaryStage.setMinHeight(400);
		primaryStage.setMinWidth(640);
		primaryStage.setScene(scene);
		primaryStage.show();

	}
	
		private String getUnusedID() {
		// TODO gescheite implementierung
		Random rand = new Random();
		return rand.nextInt() + "";
	}
	
	
	public static void setGUIController(GUIController toSet){
		guiController = toSet;
	}
	
	

}
