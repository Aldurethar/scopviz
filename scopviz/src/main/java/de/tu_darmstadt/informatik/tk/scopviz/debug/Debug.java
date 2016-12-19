package de.tu_darmstadt.informatik.tk.scopviz.debug;


/**
 * Debug class to allow easy, static access to console output.
 * 
 * @author Matthias Wilhelm
 * @version 1.0
 *
 */
public class Debug {
	public static final boolean DEBUG_ENABLED = true;
	/**
	 * 
	 * @return a sample graph for the Program
	 */
	public static String getDefaultGraph(){
		String fileName = null;
		fileName = "/Example.graphml";
		return fileName;
	}
	
	/**
	 * Short form for System.out.println().
	 * 
	 * @param s
	 *            String to be printed on the console
	 */
	public static void out(String s) {
		if(DEBUG_ENABLED)
		System.out.println(s);
	}

	/**
	 * Short form for System.out.println().
	 * 
	 * @param s
	 *            Integer to be printed on the console
	 */
	public static void out(int s) {
		if(DEBUG_ENABLED)
		System.out.println(s);
	}
}