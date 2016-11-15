package de.tu_darmstadt.informatik.tk.scopviz;

import org.graphstream.graph.Graph;
import org.graphstream.stream.file.FileSinkGraphML;

import java.io.FileOutputStream;
import java.io.IOException;

import org.graphstream.*;

public class GraphMLExporter {
	public void writeGraph (Graph g, String fileName){
		FileSinkGraphML writer = new FileSinkGraphML();
		try {
			writer.writeAll(g, new FileOutputStream(fileName));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
