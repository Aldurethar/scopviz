package de.tu_darmstadt.informatik.tk.scopviz;

import java.io.IOException;

import org.graphstream.graph.*;

/**
 * Hello world!
 *
 */
public class App {

	public static void main(String[] args) {
		
		GraphMLImporter importer = new GraphMLImporter();
		try {
			Graph g = importer.readGraph("src/main/resources/TestGraphML.txt");
			g.display();
		} catch (IOException e) {
			System.err.println("Error while reading or displaying test GraphML file!");
			e.printStackTrace();
		}
		System.out.println("Hello World!");
	}
}
