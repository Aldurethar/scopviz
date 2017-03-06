package de.tu_darmstadt.informatik.tk.scopviz.ui.css;

import java.util.HashSet;
import java.util.Iterator;

class CSSRule {
	HashSet<CSSCondition> conditions = new HashSet<CSSCondition>();
	String css;

	public CSSRule(HashSet<CSSCondition> conditions, String css) {
		super();
		this.conditions = conditions;
		this.css = css;
	}

	int ConditionsMetBy(CSSable suspect) {
		int result = 0;
		Iterator<CSSCondition> i = conditions.iterator();
		while (i.hasNext()) {
			CSSCondition condition = i.next();
			int r = condition.ConditionsMetBy(suspect);
			if (r > result)
				result = r;
		}

		return result;
	}

	String getCSS() {
		return css;
	}
}
