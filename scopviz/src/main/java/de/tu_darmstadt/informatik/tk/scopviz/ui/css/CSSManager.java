package de.tu_darmstadt.informatik.tk.scopviz.ui.css;

import java.util.HashSet;

import de.tu_darmstadt.informatik.tk.scopviz.debug.Debug;

public class CSSManager {
	/**
	 * Du zerstörst diesen REGEX und Matthias zerstört dich
	 */
	private static final String CSS_MATCH_REGEX = "(\\s*([A-Za-z]+|[A-Za-z]*(\\.[A-Za-z_-]*)+)\\s*\\{\\s*[A-Za-z_-]+\\s*\\:\\s*[A-Za-z\\(\\)_\\#\\'\\\"-]+\\s*\\;?\\s*\\})+\\s*";

	static HashSet<CSSRule> rules = new HashSet<CSSRule>();

	public static void addRule(String rule) {
		if (!rule.matches(CSS_MATCH_REGEX)) {
			Debug.out("rule <<" + rule + ">> doesn't match regex");
			return;
		}

		HashSet<CSSCondition> conditions = new HashSet<>();
		String[] ruleSplit = rule.split("{");
		int i = 0;
		String front = ruleSplit[0].trim();
		while (i < ruleSplit.length - 1) {
			String type = "";
			HashSet<String> classes = new HashSet<String>();
			if (front.contains(".")) {
				String[] dots = front.split(".");
				int j = 1;
				if (front.startsWith(".")) {
					j = 0;
				} else {
					type = dots[0];
				}
				while (j < dots.length) {
					classes.add(dots[i]);
				}
			} else {
				type = front;
			}
			conditions.add(new CSSCondition(type, classes));
			i++;
		}

		// TODO , split einfügen

		String css = null;

		CSSRule e = new CSSRule(conditions, css);

		rules.add(e);
	}
}
