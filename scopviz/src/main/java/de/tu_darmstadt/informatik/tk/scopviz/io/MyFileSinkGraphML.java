package de.tu_darmstadt.informatik.tk.scopviz.io;

import java.io.IOException;
import java.util.HashMap;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.stream.file.FileSinkGraphML;

import de.tu_darmstadt.informatik.tk.scopviz.debug.Debug;


public class MyFileSinkGraphML extends FileSinkGraphML{

	private void print(String format, Object... args) throws IOException {
		output.write(String.format(format, args));
	}

	@Override
	protected void exportGraph(Graph g){
		try {
			int attribute = 0;
			HashMap<String, String> nodeAttributes = new HashMap<String, String>();
			HashMap<String, String> edgeAttributes = new HashMap<String, String>();
			HashMap<String, String> graphAttributes = new HashMap<String, String>();
			Debug.out(g.getAttributeCount());
			for (String j : g.getAttributeKeySet()) {
				if (!graphAttributes.containsKey(j)) {
					Object gValue = g.getAttribute(j);
					String gType;

					if (gValue == null)
						continue;

					String gId = String.format("attr%04X", attribute++);

					if (gValue instanceof Boolean)
						gType = "boolean";
					else if (gValue instanceof Long)
						gType = "long";
					else if (gValue instanceof Integer)
						gType = "int";
					else if (gValue instanceof Double)
						gType = "double";
					else if (gValue instanceof Float)
						gType = "float";
					else
						gType = "string";

					graphAttributes.put(j, gId);

					print("\t<key id=\"%s\" for=\"graph\" attr.name=\"%s\" attr.type=\"%s\"/>\n",
							gId, escapeXmlString(j), gType);
				}
			}

			for (Node n : g.getEachNode()) {
				for (String k : n.getAttributeKeySet()) {
					if (!nodeAttributes.containsKey(k)) {
						Object value = n.getAttribute(k);
						String type;

						if (value == null)
							continue;

						String id = String.format("attr%04X", attribute++);

						if (value instanceof Boolean)
							type = "boolean";
						else if (value instanceof Long)
							type = "long";
						else if (value instanceof Integer)
							type = "int";
						else if (value instanceof Double)
							type = "double";
						else if (value instanceof Float)
							type = "float";
						else
							type = "string";

						nodeAttributes.put(k, id);

						print("\t<key id=\"%s\" for=\"node\" attr.name=\"%s\" attr.type=\"%s\"/>\n",
								id, escapeXmlString(k), type);
					}
				}
			}

			for (Edge n : g.getEachEdge()) {
				for (String k : n.getAttributeKeySet()) {
					if (!edgeAttributes.containsKey(k)) {
						Object value = n.getAttribute(k);
						String type;

						if (value == null)
							continue;

						String id = String.format("attr%04X", attribute++);

						if (value instanceof Boolean)
							type = "boolean";
						else if (value instanceof Long)
							type = "long";
						else if (value instanceof Integer)
							type = "int";
						else if (value instanceof Double)
							type = "double";
						else if (value instanceof Float)
							type = "float";
						else
							type = "string";

						edgeAttributes.put(k, id);
						print("\t<key id=\"%s\" for=\"edge\" attr.name=\"%s\" attr.type=\"%s\"/>\n",
								id, escapeXmlString(k), type);
					}
				}
			}

			print("\t<graph id=\"%s\" edgedefault=\"undirected\">\n", escapeXmlString(g.getId()));
			for (String k : g.getAttributeKeySet()) {
				print("\t\t\t<data key=\"%s\">%s</data>\n", graphAttributes
						.get(k), escapeXmlString(g.getAttribute(k).toString()));
			}
			
			for (Node n : g.getEachNode()) {
				print("\t\t<node id=\"%s\">\n", n.getId());
				for (String k : n.getAttributeKeySet()) {
					print("\t\t\t<data key=\"%s\">%s</data>\n", nodeAttributes
							.get(k), escapeXmlString(n.getAttribute(k).toString()));
				}
				print("\t\t</node>\n");
			}
			for (Edge e : g.getEachEdge()) {
				print(
						"\t\t<edge id=\"%s\" source=\"%s\" target=\"%s\" directed=\"%s\">\n",
						e.getId(), e.getSourceNode().getId(), e.getTargetNode()
						.getId(), e.isDirected());
				for (String k : e.getAttributeKeySet()) {
					print("\t\t\t<data key=\"%s\">%s</data>\n", edgeAttributes
							.get(k), escapeXmlString(e.getAttribute(k).toString()));
				}
				print("\t\t</edge>\n");
			}
			print("\t</graph>\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String escapeXmlString(String s){
		//why do you make me do this graphstream???
		return s
				.replace("&", "&amp;")
				.replace("<", "&lt;")
				.replace(">", "&gt;")
				.replace("\"", "&quot;")
				.replace("'", "&apos;");
	}
}
