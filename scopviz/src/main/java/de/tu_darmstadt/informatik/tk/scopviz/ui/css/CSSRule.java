package de.tu_darmstadt.informatik.tk.scopviz.ui.css;

import java.util.HashSet;
import java.util.Iterator;

/**
 * Stores a single Rule containing selectors and declarations. Offers a function
 * to check whether a CSSable matches one selector.
 * 
 * @author Matthias Wilhelm
 */
class CSSRule {
	/**
	 * A Set to store all selectors for the this rule.
	 */
	HashSet<CSSSelector> selectors = new HashSet<CSSSelector>();
	/**
	 * A Set to store all declarations for the this rule.
	 */
	HashSet<CSSDeclaration> declarations = new HashSet<CSSDeclaration>();
	/**
	 * A String to store the declarations in a human readable form.
	 */
	String css;

	/**
	 * 
	 * @param selectors
	 * @param declarations
	 */
	CSSRule(HashSet<CSSSelector> selectors, HashSet<CSSDeclaration> declarations) {
		super();
		this.selectors = selectors;
		this.declarations = declarations;
		css = "";
		for (CSSDeclaration dc : declarations) {
			css = css.concat(dc.toString()).concat("; ");
		}
		css = css.trim();
	}

	/**
	 * Checks whether a CSSable matches one selector.
	 * 
	 * @param suspect
	 *            the CSSable to check
	 * @return a positive integer if the condition is met. The more difficult
	 *         the rule was to meet, the greater the integer.
	 */
	int ConditionsMetBy(CSSable suspect) {
		int result = 0;
		Iterator<CSSSelector> i = selectors.iterator();
		while (i.hasNext()) {
			CSSSelector condition = i.next();
			int r = -1;
			if (condition.ConditionsMetBy(suspect))
				r = condition.getValue();
			if (r > result)
				result = r;
		}
		return result;
	}

	/**
	 * 
	 * @return all stored declarations
	 */
	public HashSet<CSSDeclaration> getDeclarations() {
		return declarations;
	}

	@Override
	public String toString() {
		return selectors.toString().replace("[", "").replace("]", "") + " { " + css + " }";
	}

}
