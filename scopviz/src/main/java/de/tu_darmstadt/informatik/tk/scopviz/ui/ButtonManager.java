package de.tu_darmstadt.informatik.tk.scopviz.ui;

import java.util.Iterator;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.ui.geom.Vector3;
import org.graphstream.ui.swingViewer.util.GraphMetrics;

import de.tu_darmstadt.informatik.tk.scopviz.debug.Debug;
import de.tu_darmstadt.informatik.tk.scopviz.main.Main;
import de.tu_darmstadt.informatik.tk.scopviz.main.Modus;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

public class ButtonManager {

	private static GUIController guiController;

	public static void initialize(GUIController guiCon) {
		guiController = guiCon;
	}

	public static EventHandler<ActionEvent> createNodeHandler = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent arg0) {
			switch (Main.getInstance().getModus()) {
			case CREATE_NODE:
				Main.getInstance().setModus(Modus.NORMAL);
				Debug.out("Modus set to Normal");
				guiController.createNode.setText("Knoten hinzufügen");
				break;
			case NORMAL:
				Main.getInstance().setModus(Modus.CREATE_NODE);
				Debug.out("Modus set to Create Node");
				guiController.createNode.setText("Ende");
				break;
			default:
				Main.getInstance().setModus(Modus.CREATE_NODE);
				Debug.out("Modus set to Create Node");
				guiController.createNode.setText("Ende");
				guiController.createEdge.setText("Kante hinzufügen");
				Main.getInstance().getVisualizer().deselect();
				break;
			}
		}
	};

	public static EventHandler<ActionEvent> createEdgeHandler = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent arg0) {
			Main.getInstance().getVisualizer().deselect();
			switch (Main.getInstance().getModus()) {
			case CREATE_EDGE:
			case FIRST_NODE_SELECTED:
				Main.getInstance().setModus(Modus.NORMAL);
				Debug.out("Modus set to Normal");
				guiController.createEdge.setText("Kante hinzufügen");
				break;
			case NORMAL:
				Main.getInstance().setModus(Modus.CREATE_EDGE);
				Debug.out("Modus set to Create Edge");
				guiController.createEdge.setText("Ende");
				break;
			default:
				Main.getInstance().setModus(Modus.CREATE_EDGE);
				Debug.out("Modus set to Create Edge");
				guiController.createEdge.setText("Ende");
				guiController.createNode.setText("Knoten hinzufügen");
				break;
			}
		}
	};

	public static EventHandler<MouseEvent> clickedHandler = new EventHandler<MouseEvent>() {

		// TODO: make this not terrible
		@Override
		public void handle(MouseEvent event) {
			Visualizer visualizer = Main.getInstance().getVisualizer();
			Modus currentMod = Main.getInstance().getModus();
			Graph graph = visualizer.getGraph();
			double x = event.getX();
			double trueX = (x - 45) / 3 + 100;
			double y = event.getY();
			double trueY = (y - 30) / (-3) + 200;
			Debug.out("-M (" + trueX + "/" + trueY + ")");
			if (currentMod == Modus.CREATE_NODE) {
				Node n = graph.addNode(Main.getInstance().getUnusedID());
				GraphMetrics gm = visualizer.getView().getCamera().getMetrics();
				Vector3 vc3 = gm.getSize();
				Debug.out("(x/y): " + vc3.x() + "/" + vc3.y());
				vc3.x();
				vc3.y();
				n.setAttribute("x", trueX);
				n.setAttribute("y", trueY);
				Debug.out("Created a dot on (" + trueX + "/" + trueY + ")");
			} else if (currentMod == Modus.CREATE_EDGE || currentMod == Modus.FIRST_NODE_SELECTED) {
				Iterator<Node> itr = graph.getNodeIterator();
				double d = Double.MAX_VALUE;
				String id = null;
				while (itr.hasNext()) {
					Node curN = itr.next();
					double nodeX = curN.getAttribute("x");
					double nodeY = curN.getAttribute("y");
					double curD = Math.sqrt(Math.pow(nodeX - trueX, 2.0) + Math.pow(nodeY - trueY, 2.0));
					Debug.out("+" + curN.getId() + " (" + nodeX + "/" + nodeY + ")");
					if (curD < d) {
						d = curD;
						id = curN.getId();
					}
				}

				Debug.out(id + " pressed");

				if (id == null) {
					Debug.out("nothing selected");
					return;
				}
				switch (currentMod) {
				case CREATE_EDGE:
					visualizer.setSelectedNodeID(id);
					Main.getInstance().setModus(Modus.FIRST_NODE_SELECTED);
					break;
				case FIRST_NODE_SELECTED:
					if (!id.matches(visualizer.getSelectedNodeID())) {
						graph.addEdge(Main.getInstance().getUnusedID(), visualizer.getSelectedNodeID(), id);
						Debug.out("Created a edge between " + visualizer.getSelectedNodeID() + " and " + id);
					}
					visualizer.deselect();
					Main.getInstance().setModus(Modus.CREATE_EDGE);
					break;
				default:
					break;

				}
			}
		}

	};
}
