package org.yawlfoundation.yawl.worklet.tree;

import org.yawlfoundation.yawl.worklet.dialog.ViewTreeDialog;
import org.yawlfoundation.yawl.worklet.rdr.RdrNode;
import org.yawlfoundation.yawl.worklet.rdr.RdrTree;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Michael Adams
 * @date 13/01/2016
 */
public class TreePanel extends JPanel {

    private Node _rootNode;
    private Set<Node> _nodeSet;
    private ViewTreeDialog _parent;

    private static final int H_SPACE = 20;
    private static final int V_SPACE = 30;
    private static final int X_OFFSET = 10;
    private static final int Y_OFFSET = 10;
    private static final int NODE_SIZE = 16;


    public TreePanel(ViewTreeDialog parent) {
        super();
        setBackground(Color.WHITE);
        _parent = parent;

        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                Node node = setSelectedNode(e);
                if (node != null) _parent.nodeSelected(node.getRdrNode());
            }
        });
    }


    public Dimension getPreferredSize() {
        return new Dimension(150, 300); //_preferredSize;
    }


    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (_nodeSet != null) {
            for (Node node : _nodeSet) {
                g.drawRoundRect(node.getX(), node.getY(), NODE_SIZE, NODE_SIZE, 5, 5);
                drawArc(g, node, node.getNextTrue());
                drawArc(g, node, node.getNextFalse());
                if (node.isSelected()) {
                    Color orig = g.getColor();
                    g.setColor(Color.LIGHT_GRAY);
                    g.fillRoundRect(node.getX(), node.getY(), NODE_SIZE, NODE_SIZE, 5, 5);
                    g.setColor(orig);
                }
            }
        }
    }


    public Rectangle getRootNodeRect() {
        return new Rectangle(_rootNode.getX(), _rootNode.getY(), NODE_SIZE, NODE_SIZE);
    }


    public void setTree(RdrTree tree, RdrNode selection) {
        _nodeSet = composeSet(tree);
        setPreferredSize(_nodeSet);

        Node toSelect = selection != null ? getNodeFor(selection) :
                _rootNode != null ? _rootNode.getNextTrue() : null;
        if (toSelect != null) {
            toSelect.setSelected(true);
            _parent.nodeSelected(toSelect.getRdrNode());
        }
        repaint();
    }


    private Node getNodeFor(RdrNode rdrNode) {
        for (Node node : _nodeSet) {
            if (node.getRdrNode().equals(rdrNode)) {
                return node;
            }
        }
        return null;
    }


    private Set<Node> composeSet(RdrTree tree) {
        if (tree == null) {
            return Collections.emptySet();
        }
        Set<Node> nodeSet = new HashSet<Node>();
        _rootNode = addNode(nodeSet, tree.getRootNode(), X_OFFSET, Y_OFFSET);
        recalibrateLocations(nodeSet);
        return nodeSet;
    }


    private Node addNode(Set<Node> nodeSet, RdrNode rdrNode, int x, int y) {
        Node node = null;
        if (rdrNode != null) {
            node = new Node(x, y, rdrNode);
            nodeSet.add(node);
            node.setNextTrue(addNode(nodeSet, rdrNode.getTrueChild(),
                    x + H_SPACE, y + V_SPACE));
            node.setNextFalse(addNode(nodeSet, rdrNode.getFalseChild(),
                    x - H_SPACE, y + V_SPACE));
        }
        return node;
    }


    private void recalibrateLocations(Set<Node> nodeSet) {

        // first pass: find min X (will be <= 0)
        int minX = Integer.MAX_VALUE;
        for (Node node : nodeSet) {
             if (node.getX() < minX) minX = node.getX();
        }

        // second pass: recalibrate
        for (Node node : nodeSet) {
             node.incX(-minX + Y_OFFSET);
        }
    }


    private void drawArc(Graphics g, Node parent, Node child) {
        if (! (parent == null || child == null)) {
            g.drawLine(parent.getX() + NODE_SIZE / 2, parent.getY() + NODE_SIZE,
                    child.getX() + NODE_SIZE / 2, child.getY());
        }
    }


    private Node setSelectedNode(MouseEvent e) {
        clearSelection();
        Node node = getNodeAt(e.getX(), e.getY());
        if (node != null) {
            node.setSelected(true);
            repaint();
        }
        return node;
    }


    private Node getNodeAt(int x, int y) {
        for (Node node : _nodeSet) {
            int left = node.getX();
            int right = left + NODE_SIZE;
            int top = node.getY();
            int bottom = top + NODE_SIZE;
             if (x >= left && x <= right && y >= top && y <= bottom) {
                 return node;
             }
        }
        return null;
    }


    private void clearSelection() {
        for (Node node : _nodeSet) {
            node.setSelected(false);
        }
    }


    private Dimension setPreferredSize(Set<Node> nodeSet) {
        int maxX = 0;
        int maxY = 0;
        for (Node node : nodeSet) {
            maxX = Math.max(node.getX(), maxX);
            maxY = Math.max(node.getY(), maxY);
        }
        return new Dimension(maxX + NODE_SIZE + 20, maxY + NODE_SIZE + 20);
    }


    /************************************************************************/

    class Node {
        int _x;
        int _y;
        RdrNode _node;
        Node _nextTrue;
        Node _nextFalse;
        boolean _selected;

        Node (int x, int y, RdrNode n) { _x = x; _y = y; _node = n; }

        int getX() { return _x; }

        void incX(int inc) { _x += inc; }

        int getY() { return _y; }

        RdrNode getRdrNode() { return _node; }

        void setNextTrue(Node next) { _nextTrue = next; }

        Node getNextTrue() { return _nextTrue; }

        void setNextFalse(Node next) { _nextFalse = next; }

        Node getNextFalse() { return _nextFalse; }

        void setSelected(boolean b) { _selected = b; }

        boolean isSelected() { return _selected; }

    }

}
