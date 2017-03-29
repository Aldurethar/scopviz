package de.tu_darmstadt.informatik.tk.scopviz.metrics;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.stream.Collectors;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;

import de.tu_darmstadt.informatik.tk.scopviz.debug.Debug;
import de.tu_darmstadt.informatik.tk.scopviz.graphs.MappingGraphManager;
import de.tu_darmstadt.informatik.tk.scopviz.graphs.MyGraph;
import de.tu_darmstadt.informatik.tk.scopviz.main.Main;
import de.tu_darmstadt.informatik.tk.scopviz.metrics.interfaces.ScopvizGraphMetric;
import javafx.stage.FileChooser;
import javafx.util.Pair;

/**
 * Class to compute the placement cost Metric. Placement Cost is defined as the
 * sum of the costs of individual mapping placements. These Costs are specified
 * in operator node - underlay node pairs and are loaded from an external file
 * during setup.
 * 
 * @author Jan Enders
 * @version 1.0
 *
 */
public class PlacementCostMetric implements ScopvizGraphMetric {

	/**
	 * The prefix used to mark the line containing all the relevant operator
	 * node IDs.
	 */
	private static final String OPERATOR_ID_PREFIX = "operatorIDs:";
	/**
	 * The Prefix used to mark the line containing all the relevant underlay
	 * node IDs.
	 */
	private static final String UNDERLAY_ID_PREFIX = "underlayIDs:";

	/** The text to display in case of an error during computation. */
	private static final Pair<String, String> ERROR_MESSAGE = new Pair<String, String>("Error",
			"ERROR: check Debug logs");
	/** The text to display if the Setup has not yet been done. */
	private static final Pair<String, String> SETUP_NEEDED = new Pair<String, String>("Setup required!",
			"Setup required!");

	/** The Cost Matrix. */
	private double[][] costs;
	/** The operator node IDs specified in the cost data. */
	private LinkedList<String> operatorIDs = new LinkedList<String>();
	/** The underlay node IDs specified in the cost data. */
	private LinkedList<String> underlayIDs = new LinkedList<String>();
	/** Whether or not the Setup has been done yet. */
	private boolean setupDone = false;
	/** Flag for when an error occurs during computation */
	private boolean error = false;

	@Override
	public boolean isSetupRequired() {
		return true;
	}

	@Override
	public String getName() {
		return "Placement Cost";
	}

	@Override
	public void setup() {
		// Pick File from File Chooser
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select Placement Cost Data");
		File file = fileChooser.showOpenDialog(Main.getInstance().getPrimaryStage());
		if (file != null) {
			String fileName = file.getPath();
			try {
				// Read File Line by Line
				BufferedReader reader = new BufferedReader(new FileReader(fileName));
				String line;
				int lineCounter = 0;
				while ((line = reader.readLine()) != null) {
					lineCounter = processLine(line, lineCounter);
				}
				reader.close();

				setupDone = true;
			} catch (Exception e) {
				Debug.out("ERROR while trying to read File!");
				error = true;
			}
		}
	}

	@Override
	public LinkedList<Pair<String, String>> calculate(MyGraph g) {
		LinkedList<Pair<String, String>> results = new LinkedList<Pair<String, String>>();
		double placementCostSum = 0;

		// If an Error occurred, show error message
		if (error) {
			results.add(ERROR_MESSAGE);
			error = false;
		}
		// If Setup has not yet been successfully done, show error message
		if (!setupDone) {
			results.add(SETUP_NEEDED);
		} else {
			LinkedList<Edge> mappingEdges = new LinkedList<Edge>(g.getEdgeSet().stream()
					.filter(e -> (((Boolean) e.getAttribute(MappingGraphManager.ATTRIBUTE_KEY_MAPPING)) == true))
					.collect(Collectors.toList()));
			for (Edge e : mappingEdges) {
				placementCostSum += placementCost(e.getNode0(), e.getNode1());
			}
			results.add(new Pair<String, String>("Overall Cost", "" + placementCostSum));
		}
		return results;
	}

	/**
	 * Processes a single line of the input Data.
	 * 
	 * @param line
	 *            The line to process
	 * @param lineCounter
	 *            The counter of actual lines that has been read so far
	 * @return the possibly increased line counter
	 */
	private int processLine(String line, int lineCounter) {
		// Discard Comments and empty lines
		if (line.startsWith("%") || line.equals("")) {
			return lineCounter;
		}

		// Read Operator Node ID list
		if (line.startsWith(OPERATOR_ID_PREFIX)) {
			operatorIDs = new LinkedList<String>();
			String data = line.substring(OPERATOR_ID_PREFIX.length()).trim();
			String[] opIDs = data.split(",");
			for (String s : opIDs) {
				operatorIDs.add(s.trim());
			}

			// Read Underlay Node ID list
		} else if (line.startsWith(UNDERLAY_ID_PREFIX)) {
			underlayIDs = new LinkedList<String>();
			String data = line.substring(UNDERLAY_ID_PREFIX.length()).trim();
			String[] ulIDs = data.split(",");
			for (String s : ulIDs) {
				underlayIDs.add(s.trim());
			}

		} else {
			// When both lists have been read, create cost array with correct
			// size
			if (lineCounter == 2) {
				costs = new double[operatorIDs.size()][underlayIDs.size()];
			}

			// Parse comma separated data
			int dataRow = lineCounter - 2;
			String[] data = line.split(",");
			double[] convertedData = Arrays.asList(data).stream().mapToDouble(s -> Double.parseDouble(s)).toArray();
			costs[dataRow] = Arrays.copyOf(convertedData, costs[dataRow].length);
		}
		return lineCounter + 1;
	}

	/**
	 * Fethes the placement cost of a specific Coupling of Operator node and
	 * Underlay Node.
	 * 
	 * @param operator
	 *            The Operator node
	 * @param target
	 *            The Underlay node it is mapped to
	 * @return The placement cost
	 */
	private double placementCost(Node operator, Node target) {
		if (costs == null) {
			Debug.out(
					"Tried to read Costs from nonexistant Cost Matrix. Please run Setup before trying to Compute the Metric!");
			error = true;
			return 0;
		}
		String operatorID = operator.getId().substring(MappingGraphManager.OPERATOR.length()).trim();
		String underlayID = target.getId().substring(MappingGraphManager.UNDERLAY.length()).trim();
		int x = operatorIDs.indexOf(operatorID);
		int y = underlayIDs.indexOf(underlayID);
		if (x < 0 || y < 0) {
			Debug.out("Either operator node ID " + operatorID + " or underlay node ID " + underlayID
					+ " do not exist in the cost data!");
			error = true;
			return 0;
		}
		return costs[x][y];
	}
}