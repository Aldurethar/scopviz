package de.tu_darmstadt.informatik.tk.scopviz.ui.css;

import java.util.HashSet;
import java.util.Iterator;

class CSSRule {
	//TODO comment
	HashSet<CSSCondition> conditions = new HashSet<CSSCondition>();
	//TODO comment
	String css;

	//TODO comment
	public CSSRule(HashSet<CSSCondition> conditions, String css) {
		super();
		this.conditions = conditions;
		this.css = css.trim();
	}

	//TODO comment
	int ConditionsMetBy(CSSable suspect) {
		int result = 0;
		Iterator<CSSCondition> i = conditions.iterator();
		while (i.hasNext()) {
			CSSCondition condition = i.next();
			int r=-1;
			if (condition.ConditionsMetBy(suspect))
			 r = condition.getValue();
			if (r > result)
				result = r;
		}

		return result;
	}
	
	

	//TODO comment
	String getCSS() {
		return css;
	}

	@Override
	public String toString() {
		return conditions.toString().replace("[", "").replace("]", "") + " { " + css + " }";
	}

}
