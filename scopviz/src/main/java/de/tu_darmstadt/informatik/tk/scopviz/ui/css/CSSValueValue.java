package de.tu_darmstadt.informatik.tk.scopviz.ui.css;

/**
 * Value Value class. Provides a constructor and two getters for the values.
 * 
 * @author Matthias Wilhelm
 */
public class CSSValueValue {
	/**
	 * the CSS value
	 */
	String cssValue;
	/**
	 * the rule value
	 */
	int ruleValue;

	/**
	 * Creates a new CSSValueValue pair.
	 * 
	 * @param cssValue
	 *            the CSS value to store
	 * @param ruleValue
	 *            the rule value to store
	 */
	CSSValueValue(String cssValue, int ruleValue) {
		this.cssValue = cssValue;
		this.ruleValue = ruleValue;
	}

	/**
	 * 
	 * @return the stored CSS value
	 */
	String getCssValue() {
		return cssValue;
	}

	/**
	 * 
	 * @return the stored rule value
	 */
	int getRuleValue() {
		return ruleValue;
	}
}