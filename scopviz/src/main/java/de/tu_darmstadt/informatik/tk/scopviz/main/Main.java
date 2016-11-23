package de.tu_darmstadt.informatik.tk.scopviz.main;

import java.util.Random;

import org.graphstream.graph.Graph;

import de.tu_darmstadt.informatik.tk.scopviz.io.GraphMLImporter;
import de.tu_darmstadt.informatik.tk.scopviz.ui.Visualizer;

public class Main {

	private static Main instance;

	private Graph graph;
	private Visualizer visualizer;
	
	private Modus modus = Modus.NORMAL;
	
	private Main() {
		GraphMLImporter importer = new GraphMLImporter();
		graph = importer.readGraph(Main.class.getResource("/Example.graphml"));
		visualizer = new Visualizer (graph);
	}

	public static Main getInstance() {
		if (instance == null) {
			initialize();
		}
		return instance;
	}

	private static void initialize() {
		instance = new Main();	
		
		//TODO: initialize EVERYTHING!
	}
	
	public Visualizer getVisualizer(){
		return visualizer;
	}
	
	public Modus getModus(){
		return modus;
	}
	
	public void setModus(Modus newMod){
		modus = newMod;
	}
	
	public String getUnusedID() {
		// TODO gescheite implementierung
		Random rand = new Random();
		return rand.nextInt() + "";
	}

}
