package de.tu_darmstadt.informatik.tk.scopviz.ui.css;

import java.util.HashSet;

public interface CSSable {
	/**
	 * Adds a CSS class to the object. classes already added are ignored
	 * silently. multiple classes can be separated by a '.' or ' '.
	 * 
	 * @param c
	 *            the classes to add
	 */
	public void addCSSClass(String c);

	/**
	 * Removes a CSS class from the object. classes not part of the object are
	 * ignored silently. multiple classes can be separated by a '.' or ' '.
	 * 
	 * @param c
	 *            the classes to remove
	 */
	public void removeCSSClass(String c);

	/**
	 * Toggles a CSS class from the object. multiple classes can be separated by
	 * a '.' or ' '.
	 * 
	 * @param c
	 *            the classes to remove
	 */
	public void toggleCSSClass(String c);

	/**
	 * Checks whether the given classes are part of the object. multiple classes
	 * can be separated by a '.' or ' '.
	 * 
	 * @param c
	 *            the classes to check
	 * @return return true if all classes are matched
	 */
	public boolean hasCSSClass(String c);

	/**
	 * 
	 * @return a Set of Strings containing all the previously added CSS classes
	 */
	public HashSet<String> getClasses();

	/**
	 * 
	 * @return the Type of the CSS Object
	 */
	public String getType();

	/**
	 * Updates the stored CSS String
	 */
	public void updateCSS();

	/**
	 * 
	 * @return the stored CSS String
	 */
	public String getCSS();
}
