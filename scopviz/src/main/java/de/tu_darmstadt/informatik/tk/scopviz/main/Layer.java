package de.tu_darmstadt.informatik.tk.scopviz.main;

/**
 * Enum for the different Display Layers.
 * 
 * @author Julian Ohl
 * @version 1.0
 */
public enum Layer {
	/** Layer for the Graph representing the Network. */
	UNDERLAY,
	/** Layer for the Operator Graph. */
	OPERATOR,
	/** Layer for Mapping the Operation onto the Network. */
	MAPPING,
	/**
	 * Symbol Representation Layer, displaying the Network Graph with Images
	 * representing the corresponding Devices.
	 */
	SYMBOL
}
