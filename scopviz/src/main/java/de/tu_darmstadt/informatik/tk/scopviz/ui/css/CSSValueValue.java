package de.tu_darmstadt.informatik.tk.scopviz.ui.css;

public class CSSValueValue {
	// TODO comment
	String cssValue;
	// TODO comment
	int ruleValue;

	// TODO comment
	CSSValueValue(String cssValue, int ruleValue) {
		this.cssValue = cssValue;
		this.ruleValue = ruleValue;
	}

	// TODO comment
	String getCssValue() {
		return cssValue;
	}

	// TODO comment
	int getRuleValue() {
		return ruleValue;
	}
}