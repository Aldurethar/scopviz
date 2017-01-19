package de.tu_darmstadt.informatik.tk.scopviz.debug;

import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.XMLEvent;

import de.tu_darmstadt.informatik.tk.scopviz.io.MyFileSourceGraphML;

/**
 * Debug class to allow easy, static access to console output.
 * 
 * @author Matthias Wilhelm
 * @version 1.0
 *
 */
public class Debug {

	/**
	 * Flag that determines whether to start the program in Debug Mode, loading
	 * a Graph for testing on startup and enabling Debug output to the Console
	 */
	public static final boolean DEBUG_ENABLED = true;

	/**
	 * Returns the Location of the File for the testing Graph.
	 * 
	 * @return a sample graph for the Program
	 */
	public static String getDefaultGraph() {
		String fileName = null;
		fileName = "/Example.graphml";
		return fileName;
	}

	/**
	 * Returns the Location of the File for the testing Graph.
	 * 
	 * @return a sample graph for the Program
	 */
	public static String getDefaultGraph2() {
		String fileName = null;
		fileName = "/Example2.graphml";
		return fileName;
	}
	
	/**
	 * Returns the Location of the File for the testing SymbolGraph.
	 * 
	 * @return a sample symbol graph for the Program
	 */
	public static String getDefaultSymbolGraph() {
		String fileName = null;
		fileName = "/ExampleSymbol.graphml";
		return fileName;
	}

	/**
	 * Short form for System.out.println().
	 * 
	 * @param s
	 *            String to be printed on the console
	 */
	public static void out(String s) {
		if (DEBUG_ENABLED)
			System.out.println(s);
	}

	/**
	 * Short form for System.out.println().
	 * 
	 * @param s
	 *            Integer to be printed on the console
	 */
	public static void out(int s) {
		if (DEBUG_ENABLED)
			System.out.println(s);
	}

	public static void out(XMLEvent e) {
		MyFileSourceGraphML t = new MyFileSourceGraphML();
		switch (e.getEventType()) {
		case XMLEvent.START_ELEMENT:
			Debug.out(t.gotWhat(e.getEventType(), e.asStartElement().getName().getLocalPart()));
			break;
		case XMLEvent.END_ELEMENT:
			Debug.out(t.gotWhat(e.getEventType(), e.asEndElement().getName().getLocalPart()));
			break;
		case XMLEvent.ATTRIBUTE:
			Debug.out(t.gotWhat(e.getEventType(), ((Attribute) e).getName().getLocalPart()));
			break;
		default:
			Debug.out(e.toString());
		}

	}
}