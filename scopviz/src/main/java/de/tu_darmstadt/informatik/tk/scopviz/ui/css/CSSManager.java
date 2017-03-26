package de.tu_darmstadt.informatik.tk.scopviz.ui.css;

import java.util.HashMap;
import java.util.HashSet;

import de.tu_darmstadt.informatik.tk.scopviz.debug.Debug;

public class CSSManager {
	/**
	 * Du zerstörst diesen REGEX und Matthias zerstört dich
	 */
	// TODO comment
	private static final String CSS_MATCH_REGEX = "(\\s*([A-Za-z]+|[A-Za-z]*(\\.[A-Za-z_-]*)+)\\s*\\{(\\s*[A-Za-z_-]+\\s*\\:\\s*[0-9A-Za-z\\(\\)_\\#\\'\\\"-]+\\s*\\;?)+\\s*\\})+\\s*";

	// TODO comment
	static HashSet<CSSRule> rules = new HashSet<CSSRule>();
	// TODO comment
	private static HashSet<CSSable> cssAbles = new HashSet<CSSable>();

	// TODO comment
	public static void addRule(String rule) {
		addRule(rule, true);
	}

	// TODO comment
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

	// TODO comment
	public static void addCSSAble(CSSable ca) {
		cssAbles.add(ca);
	}

	// TODO comment
	public static void removeCSSAble(CSSable ca) {
		cssAbles.remove(ca);
	}

	// TODO comment
	public static String getCSS(CSSable ca) {
		// TODO implement
		// <Property, <CSSValue, RuleValue>>
		HashMap<String, CSSValueValue> cssDeclarations = new HashMap<>();
		for (CSSRule r : rules) {
			int ruleValue = r.ConditionsMetBy(ca);
			HashSet<CSSDeclaration> declarations = r.getDeclarations();
			for (CSSDeclaration d : declarations) {
				String property = d.getProperty();
				String value = d.getValue();
				if (!cssDeclarations.containsKey(property) || ruleValue >= cssDeclarations.get(property).getRuleValue())
					cssDeclarations.put(property, new CSSValueValue(value, ruleValue));
			}
		}
		String result = "";

		for (String key : cssDeclarations.keySet()) {
			result = result.concat(key).concat(": ").concat(cssDeclarations.get(key).getCssValue()).concat("; ");
		}
		return result.trim();
	}

	// TODO comment
	private static void updateCSSAble() {
		for (CSSable ca : cssAbles)
			ca.updateCSS();
	}

	// TODO comment
	private static CSSRule extractRule(String s) {
		String[] sArray = s.trim().split("\\{");
		return new CSSRule(extractSelectors(sArray[0]), parseCss(sArray[1]));
	}

	// TODO comment
	private static HashSet<CSSSelector> extractSelectors(String s) {
		HashSet<CSSSelector> selectors = new HashSet<>();
		String[] sArray = s.trim().split("\\,");
		for (String selecteor : sArray) {
			selectors.add(extractSelector(selecteor));
		}
		return selectors;
	}

	// TODO comment
	private static CSSSelector extractSelector(String s) {
		HashSet<String> classes = new HashSet<String>();
		String[] sArray = s.trim().split("\\.");
		for (int i = 1; i < sArray.length; i++) {
			classes.add(sArray[i]);
		}
		return new CSSSelector(sArray[0], classes);
	}

	// TODO comment
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

	// TODO comment
	private static CSSDeclaration parseCssStatement(String s) {
		String[] sArray = s.trim().split("\\:");
		return new CSSDeclaration(sArray[0], sArray[1]);
	}

}
