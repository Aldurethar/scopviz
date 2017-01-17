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
	 * Singular instance of the Class, facilitates Singleton pattern.
	 */
	private static Main instance;

	/**
	 * Current mode of the application for creating new Nodes and Edges.
	 */
	private CreationMode creationMode = CreationMode.CREATE_NONE;

	/**
	 * The root window of the application
	 */
	private Stage primaryStage;

	/**
	 * Private constructor to prevent initialization, facilitates Singleton
	 * pattern. Initializes an AnimationTimer to call all Functionality that
	 * needs to be executed every Frame.
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
	 * Returns a reference to the GraphManager object currently used by the app.
	 * 
	 * @return the visualizer in use
	 */
	public GraphManager getGraphManager() {
		return GraphDisplayManager.getGraphManager();
	}

	/**
	 * Returns a unique id for a new Node or Edge not yet used by the graph.
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
	 * Returns the primary Stage for the Application Window.
	 * 
	 * @return the primary Stage
	 */
	public Stage getPrimaryStage() {
		return primaryStage;
	}

	/**
	 * Sets the Reference to the primary Stage of the Application Window.
	 * 
	 * @param primaryStage
	 *            the primary Stage of the Window.
	 */
	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}

	/**
	 * Returns the current Creation Mode.
	 * 
	 * @return the current creationMode
	 */
	public CreationMode getCreationMode() {
		return creationMode;
	}

	/**
	 * Switches the App to a given Creation Mode.
	 * 
	 * @param creationMode
	 *            the creationMode to switch to
	 */
	public void setCreationMode(CreationMode creationMode) {
		this.creationMode = creationMode;
	}

}
