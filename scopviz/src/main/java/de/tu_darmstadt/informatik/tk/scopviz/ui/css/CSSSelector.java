package de.tu_darmstadt.informatik.tk.scopviz.ui.css;

import java.util.HashSet;
import java.util.Iterator;

class CSSSelector {
	//TODO comment
	String type;
	//TODO comment
	HashSet<String> classes;
	//TODO comment
	int value;

	//TODO comment
	CSSSelector(String type, HashSet<String> classes) {
		this.type = type;
		this.classes = classes;
		value = (type != null ? 1 : 0) + classes.size() << 1;
	}

	//TODO comment
	boolean ConditionsMetBy(CSSable suspect) {
		if (type != null && !type.equals(suspect.getType()))
			return false;
		Iterator<String> i = classes.iterator();
		while (i.hasNext()) {
			if (!suspect.getClasses().contains(i.next()))
				return false;
		}
		return true;
	}

	//TODO comment
	int getValue() {
		return value;
	}

	@Override
	public String toString() {
		String ret = "";
		for (String c : classes) {
			ret = ret.concat(".").concat(c);
		}
		if (type == null)
			return ret;
		return type.concat(ret);
	}
}