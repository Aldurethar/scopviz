package de.tu_darmstadt.informatik.tk.scopviz.ui;

import javafx.beans.property.SimpleStringProperty;

/**
 * Key-Value Class for TableViews
 * 
 * @author Dominik Renkel, Julian Ohl
 *
 */
public class KeyValuePair {

	/** The Key. */
	private final SimpleStringProperty key;
	/** The Value */
	private final SimpleStringProperty value;

	/** The Class type. */
	private final Object classType;

	private final SimpleStringProperty classTypeAsString;

	/**
	 * Creates a new KeyValuePair from a given Key and Value with a given Class
	 * Type
	 */
	public KeyValuePair(String key, String value, Object classType) {
		this.key = new SimpleStringProperty(key);
		this.value = new SimpleStringProperty(value);
		this.classType = classType;
		this.classTypeAsString = new SimpleStringProperty(((Class<?>) classType).getSimpleName());
	}

	/**
	 * Returns the Key.
	 * 
	 * @return the Key.
	 */
	public String getKey() {
		return key.get();
	}

	/**
	 * Sets the Key.
	 * 
	 * @param fName
	 *            the new Key to set
	 */
	public void setKey(String fName) {
		key.set(fName);
	}

	/**
	 * Returns the Value.
	 * 
	 * @return the Value
	 */
	public String getValue() {
		return value.get();
	}

	/**
	 * Sets the Value.
	 * 
	 * @param fName
	 *            the new Value to set
	 */
	public void setValue(String fName) {
		value.set(fName);
	}

	/**
	 * Returns the classTypeAsString.
	 * 
	 * @return the classTypeAsString
	 */
	public String getClassTypeAsString() {
		return classTypeAsString.get();
	}

	/**
	 * Sets the classTypeAsString.
	 * 
	 * @param fName
	 *            the new classTypeAsString to set
	 */
	public void setClassTypeAsString(String fName) {
		classTypeAsString.set(fName);
	}

	/**
	 * Returns the Class Type.
	 * 
	 * @return the Class Type
	 */
	public Object getClassType() {
		return classType;
	}
}
