package de.tu_darmstadt.informatik.tk.scopviz;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.graphstream.graph.*;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.stream.file.*;

public class GraphMLImporter {

	public Graph readGraph(String fileName) throws IOException{
		Graph g = new DefaultGraph("g");
		FileSource fs = new FileSourceGraphML();
		fs.addSink(g);
		fs.readAll(fileName);
		/*while (fs.nextEvents()) {
			// Optionally some code here ...
		}
		if(fs!=null){
			//fs.end();
		}
		fs.removeSink(g);*/
		return g;
	}
}