package de.tu_darmstadt.informatik.tk.scopviz.ui.css;

import java.util.HashMap;
import java.util.HashSet;

import de.tu_darmstadt.informatik.tk.scopviz.debug.Debug;

/**
 * Manages CSSables. Offers Functions to store rules and CSSables, to remove
 * CSSables, to compare a given CSSable with all rules and to update the CSS for
 * all stored CSSables.
 * 
 * @author Matthias Wilhelm
 */
public class CSSManager {
	/**
	 * REGEX to match CSS
	 */
	private static final String CSS_MATCH_REGEX = "(\\s*([A-Za-z]+|[A-Za-z]*(\\.[A-Za-z_-]*)+\\s*\\,?)*\\s*\\{(\\s*[A-Za-z_-]+\\s*\\:\\s*[0-9A-Za-z\\(\\)\\_\\#\\'\\\"\\,\\s-]+\\s*\\;?)+\\s*\\})+\\s*";

	/**
	 * A Set to store all rules.
	 */
	static HashSet<CSSRule> rules = new HashSet<CSSRule>();
	/**
	 * A Set to store references to all CSSable interfaces
	 */
	private static HashSet<CSSable> cssAbles = new HashSet<CSSable>();

	/**
	 * Add a new Rule to the Set. Doesn't check whether the rule is useful.
	 * Prevents storing the same rule twice silently. Multiple rules are
	 * recognized, if separated by whitespace only.<br/>
	 * Updates all stored CSSabled afterwards.
	 * 
	 * @param rule
	 *            the rule to add
	 * 
	 */
	public static void addRule(String rule) {
		addRule(rule, true);
	}

	/**
	 * Add a new Rule to the Set. Doesn't check whether the rule is useful.
	 * Prevents storing the same rule twice silently. Multiple rules are
	 * recognized, if separated by whitespace only.
	 * 
	 * @param rule
	 *            the rule to add
	 * @param updateCSSable
	 *            Updates all stored CSSabled afterwards, if true
	 */
	public static void addRule(String rule, boolean updateCSSable) {
		if (!rule.matches(CSS_MATCH_REGEX)) {
			Debug.out("rule << " + rule + " >> doesn't match regex");
			return;
		}

		String[] sArray = rule.trim().split("\\}");
		for (String s : sArray) {
			CSSRule newRule = extractRule(s);
			rules.add(newRule);
			Debug.out("<< " + newRule.toString() + " >> added.");
		}
		if (updateCSSable)
			updateCSSAble();
	}

	/**
	 * Stores a reference to the CSSable. Storing the reference allows this
	 * Manager to update the CSS for the CSSables
	 * 
	 * @param ca
	 *            the CSSable to store
	 */
	public static void addCSSAble(CSSable ca) {
		cssAbles.add(ca);
	}

	/**
	 * Removes the reference to the CSSable. It will no longer get its CSS
	 * updated by this Manager.
	 * 
	 * @param ca
	 *            the CSSable to remove
	 */
	public static void removeCSSAble(CSSable ca) {
		cssAbles.remove(ca);
	}

	/**
	 * Returns the best match of CSS declarations for the given CSSable
	 * 
	 * @param ca
	 *            the CSSable
	 * @return a String containing all CSS declarations
	 */
	public static String getCSS(CSSable ca) {
		// <Property, <CSSValue, RuleValue>>
		HashMap<String, CSSValueValue> cssDeclarations = new HashMap<>();
		for (CSSRule r : rules) {
			int ruleValue = r.ConditionsMetBy(ca);
			if (ruleValue > 0) {
				HashSet<CSSDeclaration> declarations = r.getDeclarations();
				for (CSSDeclaration d : declarations) {
					String property = d.getProperty();
					String value = d.getValue();
					if (!cssDeclarations.containsKey(property)
							|| ruleValue >= cssDeclarations.get(property).getRuleValue())
						cssDeclarations.put(property, new CSSValueValue(value, ruleValue));
				}
			}
		}
		String result = "";

		for (String key : cssDeclarations.keySet()) {
			result = result.concat(key).concat(": ").concat(cssDeclarations.get(key).getCssValue()).concat("; ");
		}
		return result.trim();
	}

	/**
	 * Iterates over every CSSable and calls its updateCSS function.
	 */
	private static void updateCSSAble() {
		for (CSSable ca : cssAbles)
			ca.updateCSS();
	}

	/**
	 * Converts a String into a Rule. Doesn't check for correct CSS. Check
	 * should be handled beforehand.<br/>
	 * String is expected to be in following form:<br/>
	 * "selectors{declarations"
	 * 
	 * @param s
	 *            the rule as String
	 * @return the rule as CSSRule
	 */
	private static CSSRule extractRule(String s) {
		String[] sArray = s.trim().split("\\{");
		return new CSSRule(extractSelectors(sArray[0]), parseCss(sArray[1]));
	}

	/**
	 * Converts a String into selectors. Doesn't check for correct CSS. Check
	 * should be handled beforehand.<br/>
	 * String is expected to be in following form:<br/>
	 * "selector(,selector)*"
	 * 
	 * @param s
	 *            the selectors as String
	 * @return the selectors in a HashSet
	 */
	private static HashSet<CSSSelector> extractSelectors(String s) {
		HashSet<CSSSelector> selectors = new HashSet<>();
		String[] sArray = s.trim().split("\\,");
		for (String selecteor : sArray) {
			selectors.add(extractSelector(selecteor));
		}
		return selectors;
	}

	/**
	 * Converts a String into a selector. Doesn't check for correct CSS. Check
	 * should be handled beforehand.<br/>
	 * String is expected to be in one of the following forms:<br/>
	 * "type(.class)*"<br/>
	 * ".class(.class)*"
	 * 
	 * @param s
	 *            the selector as String
	 * @return the selector as CSSSelector
	 */
	private static CSSSelector extractSelector(String s) {
		HashSet<String> classes = new HashSet<String>();
		String[] sArray = s.trim().split("\\.");
		for (int i = 1; i < sArray.length; i++) {
			classes.add(sArray[i]);
		}
		return new CSSSelector(sArray[0], classes);
	}

	/**
	 * Converts a String into declarations. Doesn't check for correct CSS. Check
	 * should be handled beforehand.<br/>
	 * String is expected to be in following form:<br/>
	 * "declaration(;declaration)*"
	 * 
	 * @param s
	 *            the declarations as String
	 * @return the declarations in a HashSet
	 */
	private static HashSet<CSSDeclaration> parseCss(String s) {
		HashSet<CSSDeclaration> declarations = new HashSet<CSSDeclaration>();
		String[] sArray = s.trim().split("\\;");
		for (int i = 0; i < sArray.length; i++) {
			CSSDeclaration cssDeclaration = parseCssStatement(sArray[i]);
			String property = cssDeclaration.getProperty();
			for (CSSDeclaration cd : declarations) {
				if (property.equals(cd.getProperty())) {
					declarations.remove(cd);
					break;
				}
			}
			declarations.add(cssDeclaration);
		}
		return declarations;
	}

	/**
	 * Converts a String into a declaration. Doesn't check for correct CSS.
	 * Check should be handled beforehand.<br/>
	 * String is expected to be in following form:<br/>
	 * "property:value"
	 * 
	 * @param s
	 *            the declaration as String
	 * @return the declaration as CSSDeclaration
	 */
	private static CSSDeclaration parseCssStatement(String s) {
		String[] sArray = s.trim().split("\\:");
		return new CSSDeclaration(sArray[0], sArray[1]);
	}

}
