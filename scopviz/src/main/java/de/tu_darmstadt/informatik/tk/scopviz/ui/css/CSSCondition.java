package de.tu_darmstadt.informatik.tk.scopviz.ui.css;

import java.util.HashSet;
import java.util.Iterator;

class CSSCondition {
	String type;
	HashSet<String> classes = new HashSet<String>();

	public CSSCondition(String type, HashSet<String> classes) {
		this.type = type;
		this.classes = classes;
	}

	int ConditionsMetBy(CSSable suspect) {
		if (type != null && !type.equals(suspect.getType()))
			return 0;

		Iterator<String> i = classes.iterator();
		while (i.hasNext()) {
			if (!suspect.getClasses().contains(i.next()))
				return 0;
		}
		return classes.size();
	}
}
