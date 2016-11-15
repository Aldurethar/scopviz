package de.tu_darmstadt.informatik.tk.scopviz;

import java.io.FileOutputStream;
import java.io.IOException;
import org.graphstream.graph.Graph;
import org.graphstream.stream.file.FileSinkGraphML;

/**
 * @version 1.0
 * @author jascha-b
 * 
 */
public class GraphMLExporter {
	/**
	 * Exports the current state of the Graph to a GraphML file.
	 * 
	 * @param g
	 *            The Graphstream-Graph to be exported
	 * @param fileName
	 *            The Location on disk the File will be saved on
	 */
	public void writeGraph(final Graph g, final String fileName) {
		FileSinkGraphML writer = new FileSinkGraphML();
		try {
			writer.writeAll(g, new FileOutputStream(fileName));
		} catch (IOException e) {
			System.out.println("cannot Acces File or invalid path");
			e.printStackTrace();
		}
	}
}
