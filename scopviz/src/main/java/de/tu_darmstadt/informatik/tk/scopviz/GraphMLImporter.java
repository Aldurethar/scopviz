package de.tu_darmstadt.informatik.tk.scopviz;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.graphstream.graph.*;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.stream.file.*;

public class GraphMLImporter {

	public Graph readGraph(String fileName) {
		Graph g = new DefaultGraph("g");
		FileSource fs = new FileSourceGraphML();
		fs.addSink(g);
		try {
			fs.readAll(fileName);
		} catch (IOException e) {
			System.out.println("GraphML File doesn't exist or can't be accessed");
			e.printStackTrace();
		}
		return g;
	}
}