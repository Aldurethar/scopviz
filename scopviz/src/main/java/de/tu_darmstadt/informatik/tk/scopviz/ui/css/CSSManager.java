package de.tu_darmstadt.informatik.tk.scopviz.ui.css;

import java.util.HashSet;

import de.tu_darmstadt.informatik.tk.scopviz.debug.Debug;

public class CSSManager {
	/**
	 * Du zerstörst diesen REGEX und Matthias zerstört dich
	 */
	private static final String CSS_MATCH_REGEX = "(\\s*([A-Za-z]+|[A-Za-z]*(\\.[A-Za-z_-]*)+)\\s*\\{(\\s*[A-Za-z_-]+\\s*\\:\\s*[0-9A-Za-z\\(\\)_\\#\\'\\\"-]+\\s*\\;?)+\\s*\\})+\\s*";

	static HashSet<CSSRule> rules = new HashSet<CSSRule>();

	public static void addRule(String rule) {
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
	}

	private static CSSRule extractRule(String s) {
		String[] sArray = s.trim().split("\\{");
		return new CSSRule(extractConditions(sArray[0]), parseCss(sArray[1]));
	}

	private static HashSet<CSSCondition> extractConditions(String s) {
		HashSet<CSSCondition> conditions = new HashSet<>();
		String[] sArray = s.trim().split("\\,");
		for (String cond : sArray) {
			conditions.add(extractCondition(cond));
		}
		return conditions;
	}

	private static CSSCondition extractCondition(String s) {
		HashSet<String> classes = new HashSet<String>();
		String[] sArray = s.trim().split("\\.");
		for (int i = 1; i < sArray.length; i++) {
			classes.add(sArray[i]);
		}
		return new CSSCondition(sArray[0], classes);
	}

	private static String parseCss(String s) {
		String parsedCss = "";
		String[] sArray = s.trim().split("\\;");
		for (int i = 0; i < sArray.length; i++) {
			parsedCss = parsedCss.concat(parseCssStatement(sArray[i])).concat("; ");
		}
		return parsedCss.substring(0, parsedCss.length()-1);
	}

	private static String parseCssStatement(String s) {
		String[] sArray = s.trim().split("\\:");
		return sArray[0].trim().concat(": ").concat(sArray[1].trim());
	}

}
