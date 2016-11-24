package de.tu_darmstadt.informatik.tk.scopviz.main;

import java.util.Random;

import org.graphstream.graph.Graph;

import de.tu_darmstadt.informatik.tk.scopviz.io.GraphMLImporter;
import de.tu_darmstadt.informatik.tk.scopviz.ui.Visualizer;

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
	 * The Graph currently used by the Application. Only a reference, the actual
	 * graph management is done by the visualizer.
	 */
	private Graph graph;

	/**
	 * The Visualizer that manages the Graph instance and provides the View to
	 * access the Graph.
	 */
	private Visualizer visualizer;

	/**
	 * Current mode of the application for things like creating new Nodes and
	 * Edges.
	 */
	private Modus modus = Modus.NORMAL;

	/**
	 * Private constructor to prevent initialization, facilitates Singleton
	 * pattern. Loads a Graph from a GraphML file and creates a visualizer to
	 * manage it
	 */
	private Main() {
		GraphMLImporter importer = new GraphMLImporter();
		graph = importer.readGraph(Main.class.getResource("/Example.graphml"));
		visualizer = new Visualizer(graph);
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
		return visualizer;
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
		// TODO gescheite implementierung
		Random rand = new Random();
		return rand.nextInt() + "";
	}

}
