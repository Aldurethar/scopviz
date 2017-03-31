package de.tu_darmstadt.informatik.tk.scopviz.ui.css;

/**
 * Stores a single CSS property value Pair. Provides setter for both.
 * 
 * @author Matthias Wilhelm
 */
class CSSDeclaration {
	/**
	 * The CSS Property name
	 */
	String property;
	/**
	 * The CSS Value
	 */
	String value;

	/**
	 * Creates a new CSSDeclaration.
	 * 
	 * @param property
	 *            CSS property
	 * @param value
	 *            CSS value
	 */
	CSSDeclaration(String property, String value) {
		this.property = property.trim();
		this.value = value.trim();
	}

	/**
	 * 
	 * @return CSS property
	 */
	String getProperty() {
		return property;
	}

	/**
	 * 
	 * @return CSS value
	 */
	String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return property + ": " + value;
	}

}
