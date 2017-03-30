package de.tu_darmstadt.informatik.tk.scopviz.debug;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.XMLEvent;

import de.tu_darmstadt.informatik.tk.scopviz.io.MyFileSourceGraphML;
import de.tu_darmstadt.informatik.tk.scopviz.ui.ConsoleManager;
import javafx.application.Platform;

/**
 * Debug class to allow easy, static access to console output.
 * 
 * @author Matthias Wilhelm
 * @version 1.1
 *
 */
public final class Debug {

	private static int logLevel = 2;

	/**
	 * @return the logLevel (1 = INFORMTAION, 2 = WARNING, 3 = ERROR)
	 */
	public static int getLogLevel() {
		return logLevel;
	}

	/**
	 * All Logs with a severity smaller than the loglevel will be ignored
	 * 
	 * @param logLevel
	 *            the logLevel to set
	 */
	public static void setLogLevel(int logLevel) {
		Debug.logLevel = logLevel;
	}

	/**
	 * Private Constructor to prevent instantiation.
	 */
	private Debug() {
	}

	/**
	 * Flag that determines whether to start the program in Debug Mode, loading
	 * a Graph for testing on startup and enabling Debug output to the Console.
	 */
	public static final boolean DEBUG_ENABLED = true;

	/**
	 * Returns the Location of the File for the testing Graph.
	 * 
	 * @return a sample underlay graph for the Program
	 */
	public static String getDefaultUnderlayGraph() {
		String fileName = null;
		fileName = "/underlay1.graphml";
		return fileName;
	}

	/**
	 * Returns the Location of the File for the testing Graph.
	 * 
	 * @return a sample operator graph for the Program
	 */
	public static String getDefaultOperatorGraph() {
		String fileName = null;

		fileName = "/operatorgraph1.graphml";
		return fileName;
	}

	/**
	 * Short form for System.out.println().
	 * 
	 * @param s
	 *            String to be printed on the console
	 */
	public static void out(String s) {
		if (DEBUG_ENABLED) {
			System.out.println(s);
		}
	}

	/**
	 * Short form for System.out.println().
	 * 
	 * @param s
	 *            Object to be printed on the console
	 */
	public static void out(Object s) {
		if (DEBUG_ENABLED) {
			System.out.println("DEBUG: " + s);
		}
	}

	/**
	 * Short form for System.out.println(). Also look if a message is important
	 * enough to be printed
	 * 
	 * @param s
	 *            String to be printed on the console
	 * 
	 * @param severity
	 *            the severity of the message (1 = INFORMATION, 2 = WARNING, 3 =
	 *            ERROR)
	 */
	public static void out(String s, int severity) {
		if (severity >= logLevel) {
			if (DEBUG_ENABLED) {
				System.out.println(s);
			}

			if (severity < 3) {
				Platform.runLater(() -> ConsoleManager.addNormalText(s));
			} else {
				Platform.runLater(() -> ConsoleManager.addErrorText(s));
			}
		}
	}

	/**
	 * Short form for System.out.println().
	 * 
	 * @param s
	 *            Integer to be printed on the console
	 */
	public static void out(int s) {
		if (DEBUG_ENABLED) {
			System.out.println(s);
		}
	}

	/**
	 * Prints out an XML event.
	 * 
	 * @param e
	 *            the event to print out
	 */
	public static void out(XMLEvent e) {
		MyFileSourceGraphML t = new MyFileSourceGraphML();
		switch (e.getEventType()) {
		case XMLStreamConstants.START_ELEMENT:
			Debug.out(t.gotWhat(e.getEventType(), e.asStartElement().getName().getLocalPart()));
			break;
		case XMLStreamConstants.END_ELEMENT:
			Debug.out(t.gotWhat(e.getEventType(), e.asEndElement().getName().getLocalPart()));
			break;
		case XMLStreamConstants.ATTRIBUTE:
			Debug.out(t.gotWhat(e.getEventType(), ((Attribute) e).getName().getLocalPart()));
			break;
		default:
			Debug.out(e.toString());
		}

	}
}