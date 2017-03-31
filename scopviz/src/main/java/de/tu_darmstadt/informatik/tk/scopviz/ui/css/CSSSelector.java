package de.tu_darmstadt.informatik.tk.scopviz.ui.css;

import java.util.HashSet;
import java.util.Iterator;

/**
 * Stores a single CSSSelector consisting of a type and set of classes. Stores
 * its value. The value is calculated by multiplying the amount of classes by
 * Two and adding one if the selector has a type.
 * 
 * @author Matthias Wilhelm
 */
class CSSSelector {
	/**
	 * the stored CSS type
	 */
	String type;
	/**
	 * the stored CSS classes
	 */
	HashSet<String> classes;
	/**
	 * the stored selector value.<br/>
	 * The value is calculated by multiplying the amount of classes by Two and
	 * adding one if the selector has a type
	 */
	int value;

	/**
	 * Creates a new CSSSelector. Calculates its value.<br/>
	 * The value is calculated by multiplying the amount of classes by Two and
	 * adding one if the selector has a type
	 * 
	 * @param type
	 *            CSS type
	 * @param classes
	 *            a Set CSS classes
	 */
	CSSSelector(String type, HashSet<String> classes) {
		if (type != null && type.trim().length() > 0)
			this.type = type;
		this.classes = classes;
		value = (type != null ? 1 : 0) + classes.size() << 1;
	}

	/**
	 * Compares the suspect to its conditions.
	 * 
	 * @param suspect
	 *            the CSSable to check
	 * @return true if the CSSable contains all classes of the selector and the
	 *         type matches.
	 */
	boolean ConditionsMetBy(CSSable suspect) {
		if (type != null && !type.equals(suspect.getType()))
			return false;
		Iterator<String> i = classes.iterator();
		HashSet<String> sC = suspect.getClasses();
		while (i.hasNext()) {
			if (sC == null || !sC.contains(i.next()))
				return false;
		}
		return true;
	}

	/**
	 * The value is calculated by multiplying the amount of classes by Two and
	 * adding one if the selector has a type
	 * 
	 * @return the value of this CSS selector
	 */
	int getValue() {
		return value;
	}

	@Override
	public String toString() {
		String ret = "";
		for (String c : classes) {
			ret = ret.concat(".").concat(c);
		}
		if (type == null)
			return ret;
		return type.concat(ret);
	}
}