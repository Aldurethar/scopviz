package de.tu_darmstadt.informatik.tk.scopviz.ui.css;

class CSSDeclaration {
	// TODO comment
	String property;
	// TODO comment
	String value;

	// TODO comment
	CSSDeclaration(String property, String value) {
		this.property = property.trim();
		this.value = value.trim();
	}

	// TODO comment
	String getProperty() {
		return property;
	}

	// TODO comment
	void setProperty(String property) {
		this.property = property;
	}

	// TODO comment
	String getValue() {
		return value;
	}

	// TODO comment
	void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return property + ": " + value;
	}

}
