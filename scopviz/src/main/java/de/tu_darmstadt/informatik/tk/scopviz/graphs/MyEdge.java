package de.tu_darmstadt.informatik.tk.scopviz.graphs;

import java.util.HashSet;

import org.graphstream.graph.implementations.AbstractEdge;
import org.graphstream.graph.implementations.AbstractNode;

import de.tu_darmstadt.informatik.tk.scopviz.debug.Debug;
import de.tu_darmstadt.informatik.tk.scopviz.ui.css.CSSManager;
import de.tu_darmstadt.informatik.tk.scopviz.ui.css.CSSable;

public class MyEdge extends AbstractEdge implements CSSable {
	// TODO comment
	HashSet<String> classes;
	// TODO comment
	String type = "edge";
	// TODO comment
	String css;

	public MyEdge(String id, AbstractNode source, AbstractNode target, boolean directed) {
		super(id, source, target, directed);
		updateCSS();
		classes = new HashSet<String>();
	}

	@Override
	public void addCSSClass(String c) {
		classes.add(c);
		updateCSS();
	}

	@Override
	public void removeCSSClass(String c) {
		classes.remove(c);
		updateCSS();
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
	public HashSet<String> getClasses() {
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
		Debug.out(id + ": " + type + classes + "\n" + css);
	}

	@Override
	public String getCSS() {
		return css;
	}

	@Override
	protected void attributeChanged(AttributeChangeEvent event, String attribute, Object oldValue, Object newValue) {
		super.attributeChanged(event, attribute, oldValue, newValue);
		if (attribute.equals("ui.class")) {
			if (oldValue != null && oldValue instanceof String)
				removeCSSClass((String) oldValue);
			if (newValue != null && newValue instanceof String)
				addCSSClass((String) newValue);
		}
	}
}