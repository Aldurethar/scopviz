package de.tu_darmstadt.informatik.tk.scopviz.io;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.stream.file.FileSinkGraphML;

import de.tu_darmstadt.informatik.tk.scopviz.main.Main;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * Exporter to write a given Graph object to a GraphML file on disk.
 * 
 * @author Jascha Bohne
 * @version 1.0
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
		clearAttributes(g);
		try {
			writer.writeAll(g, new FileOutputStream(fileName));
		} catch (IOException e) {
			System.out.println("cannot Acces File or invalid path");
			e.printStackTrace();
		}
	}

	/**
	 * Exports the current state of the Graph to a GraphML file. Opens a
	 * FileSaveDialog
	 * 
	 * @param g
	 *            The Graphstream-Graph to be exported
	 * @param stage
	 *            The parent window of the save Window
	 */
	public void writeGraph(final Graph g, final Stage stage) {
		clearAttributes(g);
		String fileName;
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Saving graph");
		try {
			fileName = fileChooser.showSaveDialog(stage).getPath();
			Main.getInstance().getGraphManager().setCurrentPath(fileName);
			writeGraph(g, fileName);
		} catch (NullPointerException e) {

		}
	}

	private void clearAttributes(Graph g) {
		Iterator<? extends Edge> edges = g.getEdgeIterator();
		while (edges.hasNext()) {
			Edge e = edges.next();
			e.removeAttribute("ui.j2dsk");
		}
		Iterator<? extends Node> nodes = g.getNodeIterator();
		while (nodes.hasNext()) {
			Node n = nodes.next();
			n.removeAttribute("ui.j2dsk");
		}
	}
}
