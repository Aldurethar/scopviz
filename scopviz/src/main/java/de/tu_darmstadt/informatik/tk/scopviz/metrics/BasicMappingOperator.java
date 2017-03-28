package de.tu_darmstadt.informatik.tk.scopviz.metrics;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;

import org.graphstream.graph.Node;

import de.tu_darmstadt.informatik.tk.scopviz.debug.Debug;
import de.tu_darmstadt.informatik.tk.scopviz.graphs.GraphManager;
import de.tu_darmstadt.informatik.tk.scopviz.graphs.MappingGraphManager;
import de.tu_darmstadt.informatik.tk.scopviz.main.Main;
import de.tu_darmstadt.informatik.tk.scopviz.metrics.interfaces.ScopvizGraphOperator;

public class BasicMappingOperator implements ScopvizGraphOperator {

	@Override
	public void calculate(GraphManager g) {
		//check if you are using a Mapping Graph
		MappingGraphManager map;
		if (g instanceof MappingGraphManager){
			map = (MappingGraphManager) g;
		} else {
			Debug.out("ERROR: can only invoke " + getName() + " on a Mapping Graph", 3);
			return;
		}
		
		//find the Nodes that have to be mapped and where they can be mapped to
		LinkedList<Node> operatorNodes = getOperatorNodes(map);
		LinkedList<Node> procEnNodes = getProcEnNodes(map);
		
		
		//Map the Nodes (beginning with the operatorNode with the highest Processing requirement)
		operatorNodes.sort(operatorComparator);
		Iterator<Node> procEnIterator;
		Boolean successfull;
		for (Node n: operatorNodes){
			procEnIterator = procEnNodes.iterator();
			successfull = false;
			while(procEnIterator.hasNext() && !successfull){
				successfull = map.createEdge(procEnIterator.next().getId(), n.getId());
				Debug.out(new Boolean(successfull).toString());
			}
			if(!successfull){
				Debug.out("WARNING: BasicMappingOperator could not map all Nodes");
			}
			
		}
	}

	@Override
	public String getName() {
		return "Basic Automapping";
	}
	
	protected LinkedList<Node> getProcEnNodes(GraphManager g){
		LinkedList<Node> result = new LinkedList<Node>();
		Iterator<Node> nodeIter= g.getGraph().getNodeIterator();
		while(nodeIter.hasNext()){
			Node n = nodeIter.next();
			if("procEn".equals(n.getAttribute("typeofNode"))){
				result.add(n);
			}
		}
		return result;
	}
	
	protected LinkedList<Node> getOperatorNodes(GraphManager g){
		LinkedList<Node> result = new LinkedList<Node>();
		Iterator<Node> nodeIter= g.getGraph().getNodeIterator();
		while(nodeIter.hasNext()){
			Node n = nodeIter.next();
			if("operator".equals(n.getAttribute("typeofNode"))){
				result.add(n);
			}
		}
		return result;
	}
	
	protected Comparator<Node> operatorComparator = new Comparator<Node>() {

		@Override
		public int compare(Node o1,Node o2) {
			Main m = Main.getInstance();
			
			//this does: process-need(o1) - process-need(o2)
			Double result =  m.convertAttributeTypes(o1.getAttribute("process-need"), new Double(0)) 
					- m.convertAttributeTypes(o2.getAttribute("process-need"), new Double(0));
			if(result == 0.0){
				return 0;
			}else if (result < 0.0){
				return -1;
			} else {
				return 1;
			}
		}
	};

}
