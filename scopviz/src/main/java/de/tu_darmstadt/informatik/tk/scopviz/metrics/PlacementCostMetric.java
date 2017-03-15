package de.tu_darmstadt.informatik.tk.scopviz.metrics;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;

import de.tu_darmstadt.informatik.tk.scopviz.debug.Debug;
import de.tu_darmstadt.informatik.tk.scopviz.graphs.MappingGraphManager;
import de.tu_darmstadt.informatik.tk.scopviz.graphs.MyGraph;
import de.tu_darmstadt.informatik.tk.scopviz.main.Main;
import de.tu_darmstadt.informatik.tk.scopviz.metrics.interfaces.ScopvizGraphMetric;
import javafx.stage.FileChooser;
import javafx.util.Pair;

public class PlacementCostMetric implements ScopvizGraphMetric {

	/** The prefix used to mark the line containing all the relevant node IDs. */
	private static final String NODE_ID_PREFIX = "nodeIDs:";
	/** The Prefix used to mark the line containing all the relevant device Types. */ 
	private static final String DEVICE_TYPE_PREFIX = "deviceTypes:";

	/** The text to display in case of an error during computation. */
	private static final Pair<String, String> ERROR_MESSAGE = new Pair<String, String>("Error", "check Debug logs");
	/** The text to display if the Setup has not yet been done. */
	private static final Pair<String, String> SETUP_NEEDED = new Pair<String, String>("Setup required!", "");

	/** The Cost Matrix. */
	private double[][] costs;
	/** The node IDs specified in the cost data. */
	private LinkedList<String> nodeIDs = new LinkedList<String>();
	/** The device types specified in the cost data. */
	private LinkedList<String> deviceTypes = new LinkedList<String>();
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
		String fileName = fileChooser.showOpenDialog(Main.getInstance().getPrimaryStage()).getPath();
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
		} catch (IOException e) {
			Debug.out("ERROR while trying to read File!");
			error = true;
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
			for (Node n : g.getNodeSet()) {
				if (n.getId().startsWith(MappingGraphManager.OPERATOR)) {
					for (Edge e : n.getEdgeSet()) {
						String edgeTargetParent = e.getNode1()
								.getAttribute(MappingGraphManager.ATTRIBUTE_KEY_MAPPING_PARENT);
						if (edgeTargetParent == MappingGraphManager.UNDERLAY) {
							placementCostSum += placementCost(n, e.getNode1());
						}
					}
				}
			}
			results.add(new Pair<String, String>("Overall Cost", "" + placementCostSum));
		}
		return results;
	}

	/**
	 * Processes a single line of the input Data.
	 * 
	 * @param line The line to process
	 * @param lineCounter The counter of actual lines that has been read so far
	 * @return the possibly increased line counter
	 */
	private int processLine(String line, int lineCounter) {
		// Discard Comments and empty lines
		if (line.startsWith("%") || line.equals("")) {
			return lineCounter;
		}

		// Read Node ID list
		if (line.startsWith(NODE_ID_PREFIX)) {
			String data = line.substring(NODE_ID_PREFIX.length()).trim();
			nodeIDs = new LinkedList<String>(Arrays.asList(data.split(",")));
			
		// Read Device Type list
		} else if (line.startsWith(DEVICE_TYPE_PREFIX)) {
			String data = line.substring(DEVICE_TYPE_PREFIX.length()).trim();
			deviceTypes = new LinkedList<String>(Arrays.asList(data.split(",")));
			
		} else {
			// When both lists have been read, create cost array with correct size
			if (lineCounter == 2) {
				costs = new double[nodeIDs.size()][deviceTypes.size()];
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
	 * Fethes the placement cost of a specific Coupling of Operator node and Device type.
	 * 
	 * @param operator The Operator node
	 * @param target The Underlay node it is mapped to
	 * @return The placement cost
	 */
	public double placementCost(Node operator, Node target) {
		if (costs == null) {
			Debug.out(
					"Tried to read Costs from nonexistant Cost Matrix. Please run Setup before trying to Compute the Metric!");
			error = true;
			return 0;
		}
		String operatorID = operator.getId();
		String targetType = target.getAttribute("typeofDevice");
		int x = nodeIDs.indexOf(operatorID);
		int y = deviceTypes.indexOf(targetType);
		if (x < 0 || y < 0) {
			Debug.out(
					"Either node ID" + operatorID + " or device type " + targetType + "do not exist in the cost data!");
			error = true;
			return 0;
		}
		return costs[x][y];
	}
}