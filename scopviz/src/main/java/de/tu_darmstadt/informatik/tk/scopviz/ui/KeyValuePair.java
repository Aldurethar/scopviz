package de.tu_darmstadt.informatik.tk.scopviz.ui;

import javafx.beans.property.SimpleStringProperty;

/**
 * Key-Value Class for TableViews
 * 
 * @author Dominik Renkel, Julian Ohl
 *
 */
public class KeyValuePair {

	private final SimpleStringProperty key;
	private final SimpleStringProperty value;

	private final Object classType;

	public KeyValuePair(String key, String value, Object classType) {
		this.key = new SimpleStringProperty(key);
		this.value = new SimpleStringProperty(value);
		this.classType = classType;
	}

	public String getKey() {
		return key.get();
	}

	public void setKey(String fName) {
		key.set(fName);
	}

	public String getValue() {
		return value.get();
	}

	public void setValue(String fName) {
		value.set(fName);
	}

	public Object getClassType() {
		return classType;
	}
}
