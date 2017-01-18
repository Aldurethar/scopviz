package de.tu_darmstadt.informatik.tk.scopviz.main;

/**
 * Enum describing the various modes the program can be in for creating new
 * nodes, edges etc.
 * 
 * @author Matthias Wilhelm
 * @version 1.0
 *
 */
public enum CreationMode {
	/** Mode for creating Standard Underlay Nodes. */
	CREATE_STANDARD_NODE,
	/** Mode for creating Source Nodes on the Operator Graph. */
	CREATE_SOURCE_NODE,
	/** Mode for creating Sink Nodes on the Operator Graph. */
	CREATE_SINK_NODE,
	/** Mode for creating Processing Enabled Nodes on the Underlay Graph. */
	CREATE_PROC_NODE,
	/** Mode for creating Operator Nodes on the Operator Graph. */
	CREATE_OPERATOR_NODE,
	/** Mode for creating undirected Edges. */
	CREATE_UNDIRECTED_EDGE,
	/** Mode for creating directed Edges. */
	CREATE_DIRECTED_EDGE,
	/** Default Mode when not trying to create anything. */
	CREATE_NONE
}
