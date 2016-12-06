package de.tu_darmstadt.informatik.tk.scopviz.main;

import org.graphstream.graph.Node;

import de.tu_darmstadt.informatik.tk.scopviz.ui.Visualizer;
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
	 * Current mode of the application for things like creating new Nodes and
	 * Edges.
	 */
	private Modus modus = Modus.NORMAL;

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
		/*
		GraphManager.addGraph("/Example.graphml");
		GraphManager.setCurrentLayer(Layer.OPERATOR);
		GraphManager.addGraph("/Example2.graphml");
		*/
		
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
	public Visualizer getVisualizer() {
		return GraphManager.getVisualizer();
	}

	/**
	 * Returns the current mode of the app.
	 * 
	 * @return the current mode
	 */
	public Modus getModus() {
		return modus;
	}

	/**
	 * Sets the mode of the app.
	 * 
	 * @param newMod
	 *            the new Mode to set
	 */
	public void setModus(Modus newMod) {
		modus = newMod;
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
			if (getVisualizer().getGraph().getNode(tempID) == null
					&& getVisualizer().getGraph().getEdge(tempID) == null) {
				return (tempID);
			} else {
				i++;
			}
		}
		// return (new Random().nextInt()+"");
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

}
