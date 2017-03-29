package de.tu_darmstadt.informatik.tk.scopviz.graphs;

import java.util.Set;

import org.graphstream.graph.implementations.AbstractEdge;
import org.graphstream.graph.implementations.AbstractNode;

import de.tu_darmstadt.informatik.tk.scopviz.ui.css.CSSManager;
import de.tu_darmstadt.informatik.tk.scopviz.ui.css.CSSable;

public class MyEdge extends AbstractEdge implements CSSable {
	// TODO comment
	Set<String> classes;
	// TODO comment
	String type = "edge";
	// TODO comment
	String css;

	public MyEdge(String id, AbstractNode source, AbstractNode target, boolean directed) {
		super(id, source, target, directed);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void addCSSClass(String c) {
		classes.add(c);
	}

	@Override
	public void removeCSSClass(String c) {
		classes.remove(c);
	}

	@Override
	public void toggleCSSClass(String c) {
		if (hasCSSClass(c))
			removeCSSClass(c);
		else
			addCSSClass(c);
	}

	@Override
	public boolean hasCSSClass(String c) {
		return classes.contains(c);
	}

	@Override
	public Set<String> getClasses() {
		return classes;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public void updateCSS() {
		css = CSSManager.getCSS(this);
		addAttribute("ui.style", css);
	}

	@Override
	public String getCSS() {
		return css;
	}
}