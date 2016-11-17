package org.yawlfoundation.yawl.views.graph;

import org.yawlfoundation.yawl.util.XNode;
import org.yawlfoundation.yawl.views.ontology.Triple;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * @author Michael Adams
 * @date 4/11/16
 */
public class GraphMLWriter {

    private static final String NS = "http://graphml.graphdrawing.org/xmlns";


    public String toXML(List<Triple> triples) {
        XNode root = new XNode("graphml");
        root.addAttribute("xmlns", NS);
        XNode graphNode = root.addChild("graph");
        graphNode.addAttribute("edgedefault", "directed");

        graphNode.addChild(getDataSchema());

        Map<String, XNode> nodeMap = getNodeMap(getUniqueNodeNames(triples));

        graphNode.addChildren(nodeMap.values());
        graphNode.addChildren(getEdges(triples, nodeMap));

        return root.toString(true);
    }


    public InputStream toInputStream(List<Triple> triples)
            throws UnsupportedEncodingException {
        String xml = toXML(triples);
        return new ByteArrayInputStream(xml.getBytes("UTF-8"));
    }


    private XNode getDataSchema() {
        // <key id="name" for="node" attr.name="name" attr.type="string"/>
        XNode schema = new XNode("key");
        schema.addAttribute("id", "name");
        schema.addAttribute("for", "node");
        schema.addAttribute("attr.name", "name");
        schema.addAttribute("attr.type", "string");
        return schema;
    }


    private Set<String> getUniqueNodeNames(List<Triple> triples) {
        Set<String> names = new HashSet<String>();
        for (Triple triple : triples) {
            names.add(triple.getSubject());
            names.add(triple.getObject());
        }
        return names;
    }


    private Map<String, XNode> getNodeMap(Set<String> names) {
        Map<String, XNode> nodeMap = new HashMap<String, XNode>();
        int id = 1;
        for (String name : names) {
            XNode node = new XNode("node");
            node.addAttribute("id", id++);
            XNode data = node.addChild("data", name);
            data.addAttribute("key", "name");
            nodeMap.put(name, node);
        }
        return nodeMap;
    }


    private Set<XNode> getEdges(List<Triple> triples, Map<String, XNode> nodeMap) {
        Set<XNode> edges = new HashSet<XNode>();
        for (Triple triple : triples) {
            XNode source = nodeMap.get(triple.getSubject());
            XNode target = nodeMap.get(triple.getObject());

            XNode edge = new XNode("edge");
            edge.addAttribute("source", source.getAttributeValue("id"));
            edge.addAttribute("target", target.getAttributeValue("id"));
            edges.add(edge);
        }
        return edges;
    }

}

