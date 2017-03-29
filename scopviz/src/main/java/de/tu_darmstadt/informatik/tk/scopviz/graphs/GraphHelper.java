package de.tu_darmstadt.informatik.tk.scopviz.graphs;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import org.graphstream.algorithm.Toolkit;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Element;
import org.graphstream.graph.Node;
import org.graphstream.ui.geom.Point3;

import de.tu_darmstadt.informatik.tk.scopviz.debug.Debug;
import de.tu_darmstadt.informatik.tk.scopviz.main.Layer;
import de.tu_darmstadt.informatik.tk.scopviz.ui.OptionsManager;

public class GraphHelper {

	public static MyGraph newMerge(boolean vertical, MyGraph... sources) {

		String newID = "Composite";
		for (MyGraph g : sources) {
			newID = newID.concat("_" + g.getId());
		}
		MyGraph result = new MyGraph(newID);

		for (MyGraph g : sources) {
			result.addSubGraph(g);
			mergeInto(result, g);
		}

		return result;
	}

	// TODO better way to do scaling
	private static void mergeInto(MyGraph target, MyGraph source) {
		double targetMinX = target.getMinX();
		double targetMaxX = target.getMaxX();
		double sourceMinX = source.getMinX();
		double sourceMaxX = source.getMaxX();
		double scalingFactorX = ((targetMaxX - targetMinX + 1) / (target.getNodeCount() + 1))
				/ ((sourceMaxX - sourceMinX + 1) / (source.getNodeCount() + 1));
		double xOffset = targetMaxX - sourceMinX + 10;

		double targetMinY = target.getMinY();
		double sourceMinY = source.getMinY();
		double scalingFactorY = scalingFactorX;

		// adjust Cordinates and Attributes
		adjustSourceXCoordinates(source, scalingFactorX, xOffset, sourceMinX);
		graphAttribute(target);
		graphAttribute(source);
		adjustSourceYCoordinates(source, scalingFactorY, targetMinY, sourceMinY);

		// Copy Nodes and Edges
		HashMap<String, String> newIds = copyNodes(target, source);
		copyEdges(target, source, newIds);

	}

	private static void copyEdges(MyGraph target, MyGraph source, HashMap<String, String> newIds) {
		Random ran = new Random();
		boolean searchingForId = true;
		for (Edge e : source.getEdgeSet()) {
			// finding a new ID for the Node
			searchingForId = true;
			String newId = source.getId() + e.getId();
			while (searchingForId) {
				if (target.getEdge(newId) == null) {
					searchingForId = false;
					target.addEdge(newId, newIds.get(e.getSourceNode().getId()), newIds.get(e.getTargetNode().getId()));
					if(e.getAttribute("originalElement") == null) {
						target.getEdge(newId).addAttribute("originalElement", source.getId().concat("+#" + e.getId()));
					} else  {
						target.getEdge(newId).addAttribute("originalElement",(Object) e.getAttribute("originalElement"));
					}
				
				} else {
					newId = newId.concat(String.valueOf((char) (ran.nextInt(52) + 'a')));
				}
			}
			for (String s : e.getAttributeKeySet()) {
				target.getEdge(newId).addAttribute(s, (Object) e.getAttribute(s));
			}
		}
	}

	private static HashMap<String, String> copyNodes(MyGraph target, MyGraph source) {
		HashMap<String, String> newIds = new HashMap<>();
		Random ran = new Random();
		boolean searchingForId = true;
		for (Node n : source.getNodeSet()) {
			// finding a new ID for the Node
			searchingForId = true;
			String newId = source.getId() + n.getId();
			while (searchingForId) {
				if (target.getNode(newId) == null) {
					searchingForId = false;
					target.addNode(newId);
					newIds.put(n.getId(), newId);
					if(n.getAttribute("originalElement") == null) {
						target.getNode(newId).addAttribute("originalElement", source.getId().concat("+#" + n.getId()));
					} else  {
						target.getNode(newId).addAttribute("originalElement",(Object) n.getAttribute("originalElement"));
					}
				} else {
					newId = newId.concat(String.valueOf((char) (ran.nextInt(52) + 'a')));
				}
			}
			for (String s : n.getAttributeKeySet()) {
				target.getNode(newId).addAttribute(s, (Object) n.getAttribute(s));
			}
		}

		return newIds;
	}

	private static void adjustSourceYCoordinates(MyGraph g, double scalingFactor, double targetMinY,
			double sourceMinY) {
		for (Node n : g.getNodeSet()) {
			double d = (Double) n.getAttribute("y");
			d = d - sourceMinY;
			d = d * scalingFactor;
			d = d + targetMinY;
			n.addAttribute("y", d);
		}
	}

	private static void graphAttribute(MyGraph g) {
		for (Node n : g.getNodeSet()) {
			if (n.getAttribute("originalGraph") == null) {
				n.addAttribute("originalGraph", g.getId());
			}
		}
	}

	private static void adjustSourceXCoordinates(MyGraph g, Double scalingFactor, Double xOffset, Double SourceMinX) {
		for (Node n : g.getNodeSet()) {
			Double d = (Double) n.getAttribute("x");
			d = d - SourceMinX;
			d = d * scalingFactor;
			d = d + xOffset;
			d = d + SourceMinX;
			n.addAttribute("x", d);
		}
	}

	/**
	 * Converts the Coordinates of all Nodes into a saveable and uniform Format.
	 */
	public static void correctCoordinates(MyGraph g) {
		Point3 coords;
		Node n = null;
		Iterator<Node> allNodes = g.getNodeIterator();

		while (allNodes.hasNext()) {
			n = allNodes.next();
			if (n.hasAttribute("xyz")) {
				coords = Toolkit.nodePointPosition(n);
				n.setAttribute("x", coords.x);
				n.setAttribute("y", coords.y);
				n.removeAttribute("xyz");
			}
		}
	}

	/**
	 * Converts the weight property into a label to display on the Graph.
	 * Removes all labels if that option is set
	 */
	public static void handleEdgeWeight(MyGraph g) {
		if (!Layer.UNDERLAY.equals(g.getAttribute("layer"))) {
			return;
		}
		Edge e = null;
		Iterator<Edge> allEdges = g.getEdgeIterator();

		while (allEdges.hasNext()) {
			e = allEdges.next();
			if (!e.hasAttribute("weight")) {
				e.addAttribute("weight", OptionsManager.getDefaultWeight());
			}
			if (OptionsManager.isWeightShown()) {
				e.setAttribute("ui.label", e.getAttribute("weight").toString());
			} else {
				e.removeAttribute("ui.label");
			}
		}
	}

	/**
	 * adds default to all Nodes and converts yEd attributes to regular ones.
	 * 
	 * @param g
	 *            the graph that the attributes will be added onto
	 */
	public static void setAllDefaults(MyGraph g) {
		for (Node n : g.getNodeSet()) {
			// general defaults
			if (!n.hasAttribute("ui.label")) {
				n.addAttribute("ui.label", "");
			}
			if (!n.hasAttribute("typeofNode") || n.getAttribute("typeofNode").equals("")) {
				n.addAttribute("typeofNode", "standard");
			}

			// underlay defaults
			if (Layer.UNDERLAY.equals(g.getAttribute("layer"))) {
				if (!n.hasAttribute("typeofDevice") || n.getAttribute("typeofDevice").equals("")) {
					n.addAttribute("typeofDevice", "unknown");
				}
				if (!n.hasAttribute("lat") || n.getAttribute("long").equals("")) {
					n.addAttribute("lat", OptionsManager.getDefaultLat());
				}
				if (!n.hasAttribute("long") || n.getAttribute("long").equals("")) {
					n.addAttribute("long", OptionsManager.getDefaultLong());
				}
				if (!n.hasAttribute("process-max") || n.getAttribute("process-max").equals("")) {
					n.addAttribute("process-max", 0.0);
				}
			}

			// operator defaults
			if (Layer.OPERATOR.equals(g.getAttribute("layer"))) {
				if (!n.hasAttribute("process-need") || n.getAttribute("process-need").equals("")) {
					n.addAttribute("process-need", 0.0);
				}
			}
		}
	}
	
	public static void propagateAttribute (MyGraph g, Element n, String attribute, Object value){
		if(n.getAttribute("originalElement") == null){
			Debug.out("Debug: Attribute originalElement does not Exist");
			return;
		}
		String origGraph = n.getAttribute("originalElement").toString().split("\\+#")[0];
		String origNode = n.getAttribute("originalElement").toString().split("\\+#")[1];
		Node oldNode = null;
		Edge oldEdge = null;
		MyGraph old = null;
		Iterator<MyGraph> graphIter = g.getAllSubGraphs().iterator();
		while(graphIter.hasNext()){
			old = graphIter.next();
			if(old.getId().equals(origGraph)){
				Iterator<Node> nodeIter = old.getNodeIterator();
				while (nodeIter.hasNext()){
					oldNode = nodeIter.next();
					if(oldNode.getId().equals(origNode)){
						oldNode.addAttribute(attribute, value);
						Debug.out("Debug: propagating successfull");
						return;
					}
				}
				Iterator<Edge> edgeIter = old.getEdgeIterator();
				while (edgeIter.hasNext()){
					oldEdge = edgeIter.next();
					if(oldEdge.getId().equals(origNode)){
						oldEdge.addAttribute(attribute, value);
						Debug.out("Debug: propagating successfull");
						return;
					}
				}
				Debug.out("WARNING: could not find the specified Element " + origNode + " in the Graph " + origGraph, 2);
				return;
			}
		}
		Debug.out("WARNING: could not find the specified Graph " + origGraph, 2);
	}
}
