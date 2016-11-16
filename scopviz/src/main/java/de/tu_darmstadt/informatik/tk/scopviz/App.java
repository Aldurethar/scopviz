package de.tu_darmstadt.informatik.tk.scopviz;

import java.io.IOException;
import org.graphstream.graph.*;
import de.tu_darmstadt.informatik.tk.scopviz.io.*;

/**
 * Hello world!
 *
 */
public class App {

	public static void main(String[] args) {

		GraphMLImporter importer = new GraphMLImporter();
		Graph g = importer.readGraph("src/main/resources/Example.graphml");
		g.display(false);
		
		System.out.println("Hello World!");
	}
}
