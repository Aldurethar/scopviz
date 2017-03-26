package de.tu_darmstadt.informatik.tk.scopviz.ui.css;

import java.util.HashSet;
import java.util.Iterator;

class CSSRule {
	// TODO comment
	HashSet<CSSSelector> selectors = new HashSet<CSSSelector>();
	// TODO comment
	HashSet<CSSDeclaration> declarations = new HashSet<CSSDeclaration>();
	// TODO comment
	String css;

	// TODO comment
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

	// TODO comment
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

	// TODO comment
	public HashSet<CSSSelector> getSelectors() {
		return selectors;
	}

	// TODO comment
	public HashSet<CSSDeclaration> getDeclarations() {
		return declarations;
	}

	// TODO comment
	String getCSS() {
		return css;
	}

	@Override
	public String toString() {
		return selectors.toString().replace("[", "").replace("]", "") + " { " + css + " }";
	}

}
