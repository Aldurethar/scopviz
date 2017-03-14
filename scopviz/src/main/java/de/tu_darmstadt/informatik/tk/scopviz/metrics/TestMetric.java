package de.tu_darmstadt.informatik.tk.scopviz.metrics;

import java.util.LinkedList;

import de.tu_darmstadt.informatik.tk.scopviz.graphs.MyGraph;
import de.tu_darmstadt.informatik.tk.scopviz.metrics.interfaces.ScopvizGraphMetric;
import javafx.util.Pair;

public class TestMetric implements ScopvizGraphMetric {

	@Override
	public boolean isSetupRequired() {
		return false;
	}

	@Override
	public LinkedList<Pair<String, String>> calculate(MyGraph g) {
		LinkedList<Pair<String, String>> test =  new LinkedList<Pair<String, String>>();
		test.add(new Pair<String, String>("Hi", "test"));
		return test;
	}

	@Override
	public String getName() {
		return "Hi";
	}

	@Override
	public void setup() {
		// TODO Auto-generated method stub

	}

}
