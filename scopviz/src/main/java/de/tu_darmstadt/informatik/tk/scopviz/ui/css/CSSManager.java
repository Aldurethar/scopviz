package de.tu_darmstadt.informatik.tk.scopviz.ui.css;

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
		return null;
	}

	// TODO comment
	private static void updateCSSAble() {
		for (CSSable ca : cssAbles)
			ca.updateCSS();
	}

	// TODO comment
	private static CSSRule extractRule(String s) {
		String[] sArray = s.trim().split("\\{");
		return new CSSRule(extractConditions(sArray[0]), parseCss(sArray[1]));
	}

	// TODO comment
	private static HashSet<CSSCondition> extractConditions(String s) {
		HashSet<CSSCondition> conditions = new HashSet<>();
		String[] sArray = s.trim().split("\\,");
		for (String cond : sArray) {
			conditions.add(extractCondition(cond));
		}
		return conditions;
	}

	// TODO comment
	private static CSSCondition extractCondition(String s) {
		HashSet<String> classes = new HashSet<String>();
		String[] sArray = s.trim().split("\\.");
		for (int i = 1; i < sArray.length; i++) {
			classes.add(sArray[i]);
		}
		return new CSSCondition(sArray[0], classes);
	}

	// TODO comment
	private static String parseCss(String s) {
		String parsedCss = "";
		String[] sArray = s.trim().split("\\;");
		for (int i = 0; i < sArray.length; i++) {
			parsedCss = parsedCss.concat(parseCssStatement(sArray[i])).concat("; ");
		}
		return parsedCss.substring(0, parsedCss.length() - 1);
	}

	// TODO comment
	private static String parseCssStatement(String s) {
		String[] sArray = s.trim().split("\\:");
		return sArray[0].trim().concat(": ").concat(sArray[1].trim());
	}

}
