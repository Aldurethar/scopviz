package de.tu_darmstadt.informatik.tk.scopviz.debug;

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
	 * @param s
	 *            Integer to be printed on the console
	 */
	public static void out(int s) {
		System.out.println(s);
	}
}