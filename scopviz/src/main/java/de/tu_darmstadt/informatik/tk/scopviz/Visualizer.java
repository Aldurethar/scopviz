package de.tu_darmstadt.informatik.tk.scopviz;

import org.graphstream.graph.*;
import org.graphstream.ui.view.*;

/**
 * Interface between GUI and internal Graph representation.
 * @version 1.0.0.0
 * @author jascha-b
 *
 */
public class Visualizer {
  
  /**
   * returns a View of the Graph. 
   * The View is in the Swing Thread and the Graph in the Main thread.
   * @param g the Graph that the view is based on
   * @return a View of the Graph, inheriting from JPanel
   */
  public View getView (final Graph g){
    Viewer viewer = new Viewer(g, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
    View view = viewer.addDefaultView(false);
    return view;
  }
}
