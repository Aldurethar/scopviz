package de.tu_darmstadt.informatik.tk.scopviz.ui;

import java.util.LinkedList;
import java.util.*;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.ui.swingViewer.ViewPanel;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerPipe;

import de.tu_darmstadt.informatik.tk.scopviz.main.MyViewerListener;

/**
 * Interface between GUI and internal Graph representation.
 * 
 * @version 3.0.0.0
 * @author jascha-b
 *
 */
public class Visualizer {
	//The graph of this Visualizer
	Graph g;
	
	//last deleted elements for undelete
	private Node deletedNode;
	private LinkedList<Edge> deletedEdges = new LinkedList<>();
	
	//Currently selected Edge or Node at least on of these is always null
	private String selectedNodeID = null;
	//TODO figure out how to do this
	private String selectedEdgeID = null;
	
	//View Panel of the Graph
	private ViewPanel view;
	private Viewer viewer;
	private ViewerPipe fromViewer;
	
	
	public Visualizer(Graph graph){
		g=graph;
		viewer = new Viewer(g, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
		view = viewer.addDefaultView(false);
		viewer.setCloseFramePolicy(Viewer.CloseFramePolicy.EXIT);
		fromViewer = viewer.newViewerPipe();
		fromViewer.addViewerListener(new MyViewerListener(this));
		fromViewer.addSink(graph);
	}

	
	
	
	/**
	 * deletes the Node corresponding to the given ID from the Graph.
	 * The referenced Graph is  modified directly.
	 * Will throw an ElementNotFoundException, when the Node is not Found
	 * Will also remove all Edges connected to the given Node
	 * 
	 * @param g the Graph with the Node that shall be removed
	 * @param id the ID of the node that will be removed 
	 */
	public void deleteNode (final String id) {
		deletedEdges.removeAll(deletedEdges);
		deletedNode = null;
		//Edges have to be deleted first because they clear deletedNode 
		//and need the Node to still be in the Graph
		deleteEdgesOfNode(id);
		deletedNode = g.removeNode(id);
	}
	
	/**
	 * deletes the Edge corresponding to the given ID from the Graph.
	 * The referenced Graph is  modified directly.
	 * Will throw an ElementNotFoundException, when the Edge is not Found
	 * 
	 * @param g the Graph with the Edge that shall be removed
	 * @param id the ID of the Edge that will be removed 
	 */
	public void deleteEdge (final String id) {
		deletedEdges.removeAll(deletedEdges);
		deletedNode = null;
		deletedEdges.add(g.removeEdge(id));
	}
	
	/**
	 * deletes all Edges connected to the given Node
	 * The referenced Graph is modified Directly
	 * Will throw an ElementNotFoundException, when the Node is not Found
	 * 
	 * @param g the Graph containing the Node
	 * @param id the Id of the Node, whose Edges shall be removed
	 */
	public void deleteEdgesOfNode (final String id) {
		Node node = g.getNode(id); 
		deletedEdges.removeAll(deletedEdges);
		deletedNode = null;
		Edge[] temp = new Edge[0];
		temp = g.getEdgeSet().toArray(temp);
		
		for (Edge e : temp){
			if (e.getSourceNode().equals(node) || e.getTargetNode().equals(node)){
				//adds the Edge to the list of deleted Edges and remove sit from the Graph 
				deletedEdges.add(g.removeEdge(e));
			}
		} 
	 }
	
	//TODO make undeletes Graph specific
	/**
	 * Undos the last deleting operation on the given Graph
	 * Deleting operations are: deleteNode, deleteEdge and deleteEdgesOfNode
	 * only undos the last deleting operation even if that operation didn't change the Graph
	 *  
	 * @param g the Graph, whose Elements shall be undeleted
	 */
	public void undelete () {
		HashMap<String, Object> attributes =  new HashMap<String, Object>();
		if(deletedNode!=null){
			for (String s : deletedNode.getAttributeKeySet()){
				attributes.put(s, deletedNode.getAttribute(s));
			}
			g.addNode(deletedNode.getId());
			g.getNode(deletedNode.getId()).addAttributes(attributes);
		}
		
		for (Edge e : deletedEdges){
			attributes = new HashMap<String, Object>();
			for (String s : e.getAttributeKeySet()){
				attributes.put(s, e.getAttribute(s));
			}
			g.addEdge(e.getId(),(Node) e.getSourceNode(),(Node) e.getTargetNode());
			g.getEdge(e.getId()).addAttributes(attributes);
		}
	}
	
	
	/**
	 * returns a View of the Graph. The View is in the Swing Thread and the
	 * Graph in the Main thread.
	 * 
	 * 
	 * @return a View of the Graph, inheriting from JPanel
	 */
	public ViewPanel getView (){
		return view;
	}

	//TODO real javadoc
	/**
	 * @return the deletedNode
	 */
	public Node getDeletedNode() {
		return deletedNode;
	}




	/**
	 * @param deletedNode the deletedNode to set
	 */
	public void setDeletedNode(Node deletedNode) {
		this.deletedNode = deletedNode;
	}




	/**
	 * @return the deletedEdges
	 */
	public LinkedList<Edge> getDeletedEdges() {
		return deletedEdges;
	}




	/**
	 * @param deletedEdges the deletedEdges to set
	 */
	public void setDeletedEdges(LinkedList<Edge> deletedEdges) {
		this.deletedEdges = deletedEdges;
	}




	/**
	 * @return the fromViewer
	 */
	public ViewerPipe getFromViewer() {
		return fromViewer;
	}




	/**
	 * @param fromViewer the fromViewer to set
	 */
	public void setFromViewer(ViewerPipe fromViewer) {
		this.fromViewer = fromViewer;
	}




	/**
	 * @return the graph
	 */
	public Graph getGraph() {
		return g;
	}




	/**
	 * @return the viewer
	 */
	public Viewer getViewer() {
		return viewer;
	}




	/**
	 * @param view the view to set
	 */
	public void setView(ViewPanel view) {
		this.view = view;
	}
	
	//TODO javadoc
	public void pump(){
		fromViewer.pump();
	}
}
