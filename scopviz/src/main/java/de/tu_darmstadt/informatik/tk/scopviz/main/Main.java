package de.tu_darmstadt.informatik.tk.scopviz.main;

import de.tu_darmstadt.informatik.tk.scopviz.ui.GraphDisplayManager;
import de.tu_darmstadt.informatik.tk.scopviz.ui.handlers.MyAnimationTimer;
import javafx.animation.AnimationTimer;
import javafx.stage.Stage;

/**
 * Main Class to contain all core functionality. Built as a Singleton, use
 * getInstance() to get access to the functionality
 * 
 * @author Jan Enders (jan.enders@stud.tu-darmstadt.de)
 * @version 1.0
 *
 */
public final class Main {
	/**
	 * The Stylesheet that is given to every graph that is added to display everything correctly
	 */
	public static final String DEFAULT_STYLESHEET = 
			"node{text-alignment:at-right;} \n"
			+ "edge{text-offset: 4px,-4px;}";
	/**
	 * Singular instance of the Class, facilitates Singleton pattern.
	 */
	private static Main instance;

	/**
	 * Current mode of the application for creating new Nodes and Edges.
	 */
	private CreationMode creationMode = CreationMode.CREATE_NONE;
	/**
	 * Current mode of the application for selecting Nodes and Edges.
	 */
	private SelectionMode selectionMode = SelectionMode.SELECT_NODES;

	/**
	 * the root window of the application
	 */
	private Stage primaryStage;

	/**
	 * Private constructor to prevent initialization, facilitates Singleton
	 * pattern. Loads a Graph from a GraphML file and creates a visualizer to
	 * manage it
	 */
	private Main() {
		AnimationTimer alwaysPump = new MyAnimationTimer();
		alwaysPump.start();
	}

	/**
	 * Returns the singular instance of the Class, grants access to the
	 * Singleton. Initializes the instance when called for the first time.
	 * 
	 * @return the singular instance of the class
	 */
	public static Main getInstance() {
		if (instance == null) {
			initialize();
		}
		return instance;
	}

	/**
	 * Initializes the singular instance.
	 */
	private static void initialize() {
		instance = new Main();
	}

	/**
	 * Returns a reference to the visualizer object used by the app.
	 * 
	 * @return the visualizer in use
	 */
	public GraphManager getGraphManager() {
		return GraphDisplayManager.getGraphManager();
	}

	/**
	 * Returns a unique id for a new node not yet used by the graph.
	 * 
	 * @return a new unused id as a String
	 */
	public String getUnusedID() {
		int i = 0;
		while (true) {
			String tempID = i + "";
			if (getGraphManager().getGraph().getNode(tempID) == null
					&& getGraphManager().getGraph().getEdge(tempID) == null) {
				return (tempID);
			} else {
				i++;
			}
		}
	}

	/**
	 * @return the primaryStage
	 */
	public Stage getPrimaryStage() {
		return primaryStage;
	}

	/**
	 * @param primaryStage
	 *            the primaryStage to set
	 */
	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}

	/**
	 * @return the createModus
	 */
	public CreationMode getCreationMode() {
		return creationMode;
	}

	/**
	 * @param creationMode
	 *            the createModus to set
	 */
	public void setCreationMode(CreationMode creationMode) {
		this.creationMode = creationMode;
	}

	/**
	 * @return the selectModus
	 */
	public SelectionMode getSelectionMode() {
		return selectionMode;
	}

	/**
	 * @param selectModus
	 *            the selectModus to set
	 */
	public void setSelectionMode(SelectionMode selectModus) {
		this.selectionMode = selectModus;
	}

}
