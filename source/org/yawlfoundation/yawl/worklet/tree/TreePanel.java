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

    private TreeNode _rootNode;             // actually the first child node of rdr tree
    private Set<TreeNode> _nodeSet;
    private ViewTreeDialog _parent;

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
                TreeNode node = setSelectedNode(e);
                if (node != null) _parent.nodeSelected(node.getRdrNode());
            }
        });
    }


    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (_nodeSet != null) {
            for (TreeNode node : _nodeSet) {
                int x = getScreenX(node);
                int y = getScreenY(node);
                g.drawRoundRect(x, y, NODE_SIZE, NODE_SIZE, 5, 5);
                drawArc(g, node, node.getTrueChild());
                drawArc(g, node, node.getFalseChild());
                if (node.isSelected()) {
                    Color orig = g.getColor();
                    g.setColor(Color.LIGHT_GRAY);
                    g.fillRoundRect(x, y, NODE_SIZE, NODE_SIZE, 5, 5);
                    g.setColor(orig);
                }
            }
        }
    }


    public Rectangle getRootNodeRect() {
        return _rootNode == null ? null :
                new Rectangle(_rootNode.getX(), _rootNode.getY(), NODE_SIZE, NODE_SIZE);
    }


    public void setTree(RdrTree tree, RdrNode selection) {
        _nodeSet = composeSet(tree);
        layoutNodes();

        TreeNode toSelect = selection != null ? getNodeFor(selection) :
                _rootNode != null ? _rootNode : null;
        if (toSelect != null) {
            toSelect.setSelected(true);
            _parent.nodeSelected(toSelect.getRdrNode());
        }
        repaint();
    }


    private TreeNode getNodeFor(RdrNode rdrNode) {
        for (TreeNode node : _nodeSet) {
            if (node.getRdrNode().equals(rdrNode)) {
                return node;
            }
        }
        return null;
    }


    private Set<TreeNode> composeSet(RdrTree tree) {
        RdrNode firstChild = getFirstChildNode(tree);
        if (firstChild == null) {
            return Collections.emptySet();
        }
        Set<TreeNode> nodeSet = new HashSet<TreeNode>();
        _rootNode = addNode(nodeSet, null, firstChild);
        return nodeSet;
    }


    private TreeNode addNode(Set<TreeNode> nodeSet, TreeNode parent, RdrNode rdrNode) {
        TreeNode node = null;
        if (rdrNode != null) {
            node = new TreeNode(parent, rdrNode);
            nodeSet.add(node);
            node.setTrueChild(addNode(nodeSet, node, rdrNode.getTrueChild()));
            node.setFalseChild(addNode(nodeSet, node, rdrNode.getFalseChild()));
        }
        return node;
    }


    private void layoutNodes() {
        TreeLayout layout = new TreeLayout();
        layout.positionNodes(_rootNode);
        int width = layout.getMaxX() * NODE_SIZE + X_OFFSET;
        int height = layout.getMaxY() * V_SPACE + Y_OFFSET;
        setPreferredSize(new Dimension(width, height));
    }


    private void drawArc(Graphics g, TreeNode parent, TreeNode child) {
        if (! (parent == null || child == null)) {
            g.drawLine(getScreenX(parent) + NODE_SIZE / 2, getScreenY(parent) + NODE_SIZE,
                    getScreenX(child) + NODE_SIZE / 2, getScreenY(child));
        }
    }


    private TreeNode setSelectedNode(MouseEvent e) {
        clearSelection();
        TreeNode node = getNodeAt(e.getX(), e.getY());
        if (node != null) {
            node.setSelected(true);
            repaint();
        }
        return node;
    }


    private TreeNode getNodeAt(int x, int y) {
        for (TreeNode node : _nodeSet) {
            int left = getScreenX(node);
            int right = left + NODE_SIZE;
            int top = getScreenY(node);
            int bottom = top + NODE_SIZE;
             if (x >= left && x <= right && y >= top && y <= bottom) {
                 return node;
             }
        }
        return null;
    }


    private void clearSelection() {
        for (TreeNode node : _nodeSet) {
            node.setSelected(false);
        }
    }


    private RdrNode getFirstChildNode(RdrTree tree) {
        if (tree == null) return null;
        RdrNode rootNode = tree.getRootNode();
        return rootNode != null ? rootNode.getTrueChild() : null;
    }


    private int getScreenX(TreeNode node) {
        return node.getX() * NODE_SIZE + X_OFFSET;
    }


    private int getScreenY(TreeNode node) {
        return node.getY() * V_SPACE + Y_OFFSET;
    }

}
