package de.tu_darmstadt.informatik.tk.scopviz.main;

import java.math.BigDecimal;
import java.math.BigInteger;

import de.tu_darmstadt.informatik.tk.scopviz.graphs.GraphManager;
import de.tu_darmstadt.informatik.tk.scopviz.io.GraphMLExporter;
import de.tu_darmstadt.informatik.tk.scopviz.ui.GraphDisplayManager;
import de.tu_darmstadt.informatik.tk.scopviz.ui.OptionsManager;
import de.tu_darmstadt.informatik.tk.scopviz.ui.handlers.MyAnimationTimer;
import javafx.animation.AnimationTimer;
import javafx.stage.Stage;

/**
 * Main Class to contain all core functionality. Built as a Singleton, use
 * getInstance() to get access to the functionality
 * 
 * @author Jan Enders (jan.enders@stud.tu-darmstadt.de)
 * @version 1.0
 *
 */
public final class Main {
	/**
	 * Singular instance of the Class, facilitates Singleton pattern.
	 */
	private static Main instance;

	/**
	 * Current mode of the application for creating new Nodes and Edges.
	 */
	private CreationMode creationMode = CreationMode.CREATE_NONE;

	/**
	 * The root window of the application.
	 */
	private Stage primaryStage;

	/**
	 * Private constructor to prevent initialization, facilitates Singleton
	 * pattern. Initializes an AnimationTimer to call all Functionality that
	 * needs to be executed every Frame.
	 */
	private Main() {
		AnimationTimer alwaysPump = new MyAnimationTimer();
		alwaysPump.start();
	}

	/**
	 * Returns the singular instance of the Class, grants access to the
	 * Singleton. Initializes the instance when called for the first time.
	 * 
	 * @return the singular instance of the class
	 */
	public static Main getInstance() {
		if (instance == null) {
			initialize();
		}
		return instance;
	}

	/**
	 * Initializes the singular instance.
	 */
	private static void initialize() {
		instance = new Main();
	}

	/**
	 * Returns a reference to the GraphManager object currently used by the app.
	 * 
	 * @return the visualizer in use
	 */
	public GraphManager getGraphManager() {
		return GraphDisplayManager.getGraphManager();
	}

	/**
	 * Returns a unique id for a new Node or Edge not yet used by the graph.
	 * 
	 * @return a new unused id as a String
	 */
	public String getUnusedID() {
		int i = 0;
		while (true) {
			String tempID = i + "";
			if (getGraphManager().getGraph().getNode(tempID) == null
					&& getGraphManager().getGraph().getEdge(tempID) == null) {
				return (tempID);
			} else {
				i++;
			}
		}
	}

	public String getUnusedID(GraphManager gm) {
		int i = 0;
		while (true) {
			String tempID = i + "";
			if (gm.getGraph().getNode(tempID) == null && gm.getGraph().getEdge(tempID) == null) {
				return (tempID);
			} else {
				i++;
			}
		}
	}

	/**
	 * Returns the primary Stage for the Application Window.
	 * 
	 * @return the primary Stage
	 */
	public Stage getPrimaryStage() {
		return primaryStage;
	}

	/**
	 * Sets the Reference to the primary Stage of the Application Window.
	 * 
	 * @param primaryStage
	 *            the primary Stage of the Window.
	 */
	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}

	/**
	 * Returns the current Creation Mode.
	 * 
	 * @return the current creationMode
	 */
	public CreationMode getCreationMode() {
		return creationMode;
	}

	/**
	 * Switches the App to a given Creation Mode.
	 * 
	 * @param creationMode
	 *            the creationMode to switch to
	 */
	public void setCreationMode(CreationMode creationMode) {
		this.creationMode = creationMode;
	}

	// TODO replace throw by something better for debug
	/**
	 * Converts a given Attribute into the type of result
	 * 
	 * @param attribute
	 *            the Attribute to be converted. supported types: byte, short,
	 *            integer, long, float, double, BigInteger, BigDecimal, String
	 * 
	 * @param result
	 *            the Attribute will be written in here after the conversion.
	 *            the supported types are the same as above except for String
	 * 
	 * @return the value of result
	 */
	// don't worry I checked all the conversions
	@SuppressWarnings("unchecked")
	public <T extends Number> T convertAttributeTypes(Object attribute, T result) {
		if (attribute == null) {
			return null;
		}
		String currentType = attribute.getClass().getSimpleName().toLowerCase();
		String targetType = result.getClass().getSimpleName().toLowerCase();
		switch (targetType) {
		case "byte":
			switch (currentType) {
			case "byte":
			case "short":
			case "integer":
			case "long":
			case "biginteger":
			case "float":
			case "double":
			case "bigdecimal":
				result = (T) new Byte(((Number) attribute).byteValue());
				break;
			case "string":
				result = (T) new Byte(new BigDecimal((String) attribute).byteValue());
				break;
			default:
				throw new IllegalArgumentException("invalid type: " + attribute.getClass());
			}
			break;
		case "short":
			switch (currentType) {
			case "byte":
			case "short":
			case "integer":
			case "long":
			case "biginteger":
			case "float":
			case "double":
			case "bigdecimal":
				result = (T) new Short(((Number) attribute).shortValue());
				break;
			case "string":
				result = (T) new Short(new BigDecimal((String) attribute).shortValue());
				break;
			default:
				throw new IllegalArgumentException("invalid type: " + attribute.getClass());
			}
			break;
		case "integer":
			switch (currentType) {
			case "byte":
			case "short":
			case "integer":
			case "long":
			case "biginteger":
			case "float":
			case "double":
			case "bigdecimal":
				result = (T) new Integer(((Number) attribute).intValue());
				break;
			case "string":
				result = (T) new Integer(new BigDecimal((String) attribute).intValue());
				break;
			default:
				throw new IllegalArgumentException("invalid type: " + attribute.getClass());
			}
			break;
		case "long":
			switch (currentType) {
			case "byte":
			case "short":
			case "integer":
			case "long":
			case "biginteger":
			case "float":
			case "double":
			case "bigdecimal":
				result = (T) new Long(((Number) attribute).longValue());
				break;
			case "string":
				result = (T) new Long(new BigDecimal((String) attribute).longValue());
				break;
			default:
				throw new IllegalArgumentException("invalid type: " + attribute.getClass());
			}
			break;
		case "biginteger":
			BigInteger integer;
			switch (currentType) {
			case "byte":
				integer = new BigInteger(Byte.toString((byte) attribute));
				break;
			case "short":
				integer = new BigInteger(Short.toString((short) attribute));
				break;
			case "integer":
				integer = new BigInteger(Integer.toString((int) attribute));
				break;
			case "long":
				integer = new BigInteger(Long.toString((long) attribute));
				break;
			case "float":
				integer = new BigInteger(Integer.toString((int) (float) attribute));
				break;
			case "double":
				integer = new BigInteger(Long.toString((long) (double) attribute));
				break;
			case "biginteger":
				integer = (BigInteger) attribute;
				break;
			case "bigdecimal":
				integer = ((BigDecimal) attribute).toBigInteger();
				break;
			case "string":
				integer = new BigDecimal((String) attribute).toBigInteger();
				break;
			default:
				throw new IllegalArgumentException("invalid type: " + attribute.getClass());
			}
			result = (T) integer;
			break;
		case "float":
			switch (currentType) {
			case "byte":
			case "short":
			case "integer":
			case "long":
			case "biginteger":
			case "float":
			case "double":
			case "bigdecimal":
				result = (T) new Float(((Number) attribute).floatValue());
				break;
			case "string":
				result = (T) new Float(new BigDecimal((String) attribute).floatValue());
				break;
			default:
				throw new IllegalArgumentException("invalid type: " + attribute.getClass());
			}
			break;
		case "double":
			switch (currentType) {
			case "byte":
			case "short":
			case "integer":
			case "long":
			case "biginteger":
			case "float":
			case "double":
			case "bigdecimal":
				result = (T) new Double(((Number) attribute).doubleValue());
				break;
			case "string":
				result = (T) new Double(new BigDecimal((String) attribute).doubleValue());
				break;
			default:
				throw new IllegalArgumentException("invalid type: " + attribute.getClass());
			}
			break;
		case "bigdecimal":
			BigDecimal decimal;
			switch (currentType) {
			case "byte":
				decimal = new BigDecimal((byte) attribute);
				break;
			case "short":
				decimal = new BigDecimal((short) attribute);
				break;
			case "integer":
				decimal = new BigDecimal((int) attribute);
				break;
			case "long":
				decimal = new BigDecimal((long) attribute);
				break;
			case "float":
				decimal = new BigDecimal((float) attribute);
				break;
			case "double":
				decimal = new BigDecimal((double) attribute);
				break;
			case "biginteger":
				decimal = new BigDecimal((BigInteger) attribute);
				break;
			case "bigdecimal":
				decimal = (BigDecimal) attribute;
				break;
			case "string":
				decimal = new BigDecimal((String) attribute);
				break;
			default:
				throw new IllegalArgumentException("invalid type: " + attribute.getClass());
			}
			result = (T) decimal;
			break;
		}
		return result;
	}

	public void closeProgram() {
		GraphMLExporter exp = new GraphMLExporter();
		exp.writeGraph(GraphDisplayManager.getGraphManager(Layer.UNDERLAY).getGraph(), "underlay-shutdown.graphml",
				false);
		exp.writeGraph(GraphDisplayManager.getGraphManager(Layer.OPERATOR).getGraph(), "operator-shutdown.graphml",
				false);
		OptionsManager.save();
		System.exit(0);
	}

}
