package de.tu_darmstadt.informatik.tk.scopviz.main;

import java.util.Iterator;

import org.graphstream.algorithm.Toolkit;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.view.Camera;

/**
 * This class contains helpful static functions.
 * 
 * @author Matthias Wilhelm
 * @version 1.0
 */
public final class EdgeSelectionHelper {

	/**
	 * Private Constructor to prevent instantiation. private
	 * EdgeSelectionHelper(){}
	 * 
	 * /** Width in pixels for which the edge selection triggers.
	 */
	private static final int EDGE_SELECTION_WIDTH = 5;

	/**
	 * Precalculates pi / 2.
	 */
	private static final double HALF_PI = Math.PI / 2;

	private EdgeSelectionHelper() {
	}

	// TODO optional: only update if view has changed
	/**
	 * Returns the closest Edge in the current Graph to a given position. It
	 * allows for an inaccuracy of around {@value #EDGE_SELECTION_WIDTH}px
	 * 
	 * @param pos
	 *            The position. Expects a <b>Point3</b>, but only uses the x and
	 *            y coordinates
	 * @return the closest Edge if a valid Edge exists, returns <b>null</b>
	 *         otherwise
	 */
	public static Edge getClosestEdge(Point3 pos) {
		Camera cam = Main.getInstance().getGraphManager().getView().getCamera();

		// gets to points within a fixed distance of each other and calculates
		// the gu of those points
		Point3 min = cam.transformPxToGu(0, 0);
		Point3 max = cam.transformPxToGu(EDGE_SELECTION_WIDTH, EDGE_SELECTION_WIDTH);

		// calculates the approximate distance by taken the maximum of the
		// absolute Distances of the x's or y's
		double dist = Math.max(Math.abs(max.x - min.x), Math.abs(max.y - min.y));

		// calls the actual calculation
		return getClosestEdge(pos, dist);
	}

	/**
	 * Returns the closest Edge in the current Graph to a given position within
	 * the given maxDistance.
	 * 
	 * @param pos
	 *            The position. Expects a <b>Point3</b>, but only uses the x and
	 *            y coordinates
	 * @param maxDistance
	 *            the maximum distance (in gu) the edge may be away from the
	 *            position to be considered as a valid edge
	 * @return the closest Edge if a valid Edge exists, returns <b>null</b>
	 *         otherwise
	 */
	public static Edge getClosestEdge(Point3 pos, double maxDistance) {
		double x0 = pos.x;
		double y0 = pos.y;

		// keeps track of the current smallest distance to an edge;
		// initializes to the given maxDistance
		double dist = maxDistance;

		// keeps track of the current closest edge
		Edge result = null;

		GraphManager gm = Main.getInstance().getGraphManager();

		// Iterates over every edge, calculates the distance and updates the
		// best edge and corresponding distance
		for (Iterator<Edge> iterator = gm.getGraph().getEdgeIterator(); iterator.hasNext();) {
			Edge edge = (Edge) iterator.next();

			// Get the positions of the nodes of the currently selected edge.
			double[] n1 = Toolkit.nodePosition(edge.getNode0());
			double[] n2 = Toolkit.nodePosition(edge.getNode1());

			// Extract the x and y values of the positions of the nodes
			double x1 = n1[0];
			double y1 = n1[1];
			double x2 = n2[0];
			double y2 = n2[1];

			// Calculate the distance between the nodes
			// Naming convention is view node 0 as A, node 1 as B and the point
			// as C in a triangle
			double c = distance(x1, y1, x2, y2); // Distance Node0 Node1

			// Calculate the distance between the edge and the point
			double cdist = Math.abs((y2 - y1) * x0 - (x2 - x1) * y0 + x2 * y1 - y2 * x1) / c;

			// Check if the edge is closer than the currently stored one
			if (cdist < dist) {

				// Calculate the distances from each node to the point
				double a = distance(x0, y0, x2, y2); // Distance Point Node0
				double b = distance(x0, y0, x1, y1); // Distance Point Node1

				// Precalculate the squares of the distances for later use
				double b2 = b * b;
				double a2 = a * a;
				double c2 = c * c;

				// Calculates the inner angles off the triangle
				double alpha = Math.acos((b2 + c2 - a2) / (2 * b * c));
				double beta = Math.acos((a2 + c2 - b2) / (2 * a * c));

				// Check if the point is actually visually next to the edge by
				// checking if both inner angles are less than 90°
				if (alpha <= HALF_PI && beta <= HALF_PI) {
					dist = cdist;
					result = edge;
				}
			}
		}
		return result;
	}

	/**
	 * Calculates the distance between two given Nodes.
	 * 
	 * @param a
	 *            Node 1
	 * @param b
	 *            Node 2
	 * @return the distance between the two Nodes as a double
	 */
	public static double distance(Node a, Node b) {
		double[] n1 = Toolkit.nodePosition(a);
		double[] n2 = Toolkit.nodePosition(b);

		return distance(n1[0], n1[1], n2[0], n2[1]);
	}

	/**
	 * Calculates the distance between a x,y position and a Node.
	 * 
	 * @param x0
	 *            x cord of the position
	 * @param y0
	 *            y cord of the position
	 * @param a
	 *            the Node
	 * @return the distance between the position and the Node as a double
	 */
	public static double distance(double x0, double y0, Node a) {
		double[] n1 = Toolkit.nodePosition(a);

		return distance(x0, y0, n1[0], n1[1]);
	}

	/**
	 * Calculates the distance between two x,y positions.
	 * 
	 * @param x0
	 *            x cord of the first position
	 * @param y0
	 *            y cord of the first position
	 * @param x1
	 *            x cord of the second position
	 * @param y1
	 *            y cord of the second position
	 * @return the distance between the two positions as a double
	 */
	public static double distance(double x0, double y0, double x1, double y1) {
		return Math.sqrt(Math.pow(y0 - y1, 2) + Math.pow(x0 - x1, 2));
	}

}
