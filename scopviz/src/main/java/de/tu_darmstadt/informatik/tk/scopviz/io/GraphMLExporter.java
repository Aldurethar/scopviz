package de.tu_darmstadt.informatik.tk.scopviz.io;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.stream.file.FileSinkGraphML;

import de.tu_darmstadt.informatik.tk.scopviz.debug.Debug;
import de.tu_darmstadt.informatik.tk.scopviz.graphs.MyGraph;
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
	public void writeGraph(final MyGraph g, final String fileName) {
		MyFileSinkGraphML writer = new MyFileSinkGraphML();
		if(g.isComposite()){
			for(int i = 0; i < g.getChildren().toArray().length; i++){
				writer.exportGraphs(g.getAllSubGraphs(), fileName);
				return;
			}
			return;
		}
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
	public void writeGraph(final MyGraph g, final Stage stage) {
		String fileName;
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Saving graph");
		try {
			fileName = fileChooser.showSaveDialog(stage).getPath();
			Main.getInstance().getGraphManager().setCurrentPath(fileName);
			if (fileName != null) {
				writeGraph(g, fileName);
			}
		} catch (NullPointerException e) {

		}
	}
}