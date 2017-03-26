package de.tu_darmstadt.informatik.tk.scopviz.graphs;

import java.util.Set;

import org.graphstream.graph.implementations.AbstractEdge;
import org.graphstream.graph.implementations.AbstractNode;

import de.tu_darmstadt.informatik.tk.scopviz.ui.css.CSSable;

public class MyEdge extends AbstractEdge implements CSSable {

	protected MyEdge(String id, AbstractNode source, AbstractNode target, boolean directed) {
		super(id, source, target, directed);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void addCSSClass(String c) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeCSSClass(String c) {
		// TODO Auto-generated method stub

	}

	@Override
	public void toggleCSSClass(String c) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean hasCSSClass(String c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Set<String> getClasses() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateCSS() {
		// TODO Auto-generated method stub

	}

	@Override
	public String getCSS() {
		// TODO Auto-generated method stub
		return null;
	}

}
