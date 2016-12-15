package de.tu_darmstadt.informatik.tk.scopviz.debug;

import org.graphstream.graph.Edge;

/**
 * Debug class to allow easy, static access to console output.
 * 
 * @author Matthias Wilhelm
 * @version 1.0
 *
 */
public class Debug {

	/**
	 * Short form for System.out.println().
	 * 
	 * @param s
	 *            String to be printed on the console
	 */
	public static void out(String s) {
		System.out.println(s);
	}

	/**
	 * Short form for System.out.println().
	 * 
	 * @param i
	 *            Integer to be printed on the console
	 */
	public static void out(int i) {
		System.out.println(i);
	}

	/**
	 * Short form for System.out.println().
	 * 
	 * @param e
	 *            Edge to be printed on the console
	 */
	public static void out(Edge e) {
		if (e != null) {
			System.out.println("Edge: " + e.toString());
		} else {

			System.out.println("Edge: null");
		}
	}
}