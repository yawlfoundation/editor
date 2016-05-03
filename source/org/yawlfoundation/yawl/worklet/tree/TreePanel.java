package org.yawlfoundation.yawl.worklet.tree;

import org.yawlfoundation.yawl.worklet.dialog.ViewTreeDialog;
import org.yawlfoundation.yawl.worklet.rdr.RdrNode;
import org.yawlfoundation.yawl.worklet.rdr.RdrTree;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author Michael Adams
 * @date 13/01/2016
 */
public class TreePanel extends JPanel {

    private ViewTreeDialog _parent;

    // actually a representation of the first child node of rdr tree
    private TreeNode _rootNode;

    private static final TreeRenderer RENDERER = new TreeRenderer();
    private static final TreeLayout LAYOUT = new TreeLayout();


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
        RENDERER.paint(g, _rootNode);
    }


    public Rectangle getRootNodeRect() {
        return _rootNode == null ? null :
                new Rectangle(_rootNode.getX(), _rootNode.getY(),
                        TreeRenderer.NODE_SIZE, TreeRenderer.NODE_SIZE);
    }


    public void setTree(RdrTree tree, RdrNode selection) {
        _rootNode = buildTree(tree);
        layoutNodes();
        makeInitialSelection(selection);
        repaint();
    }


    private void makeInitialSelection(RdrNode selection) {
        TreeNode toSelect = selection != null ? getNodeFor(_rootNode, selection) :
                _rootNode != null ? _rootNode : null;
        if (toSelect != null) {
            toSelect.setSelected(true);
            _parent.nodeSelected(toSelect.getRdrNode());
        }
    }


    private TreeNode getNodeFor(TreeNode node, RdrNode rdrNode) {
        TreeNode found = null;
        if (! (node == null || rdrNode == null)) {
            if (node.getRdrNode().equals(rdrNode)) {
                found = node;
            }
            else {
                found = getNodeFor(node.getFalseChild(), rdrNode);
                if (found == null) found = getNodeFor(node.getTrueChild(), rdrNode);
            }
        }
        return found;
    }


    private TreeNode buildTree(RdrTree tree) {
        RdrNode firstChild = getFirstChildNode(tree);
        if (firstChild == null) {
            return null;
        }
        return addNode(null, firstChild);
    }


    private TreeNode addNode(TreeNode parent, RdrNode rdrNode) {
        TreeNode node = null;
        if (rdrNode != null) {
            node = new TreeNode(parent, rdrNode);
            node.setTrueChild(addNode(node, rdrNode.getTrueChild()));
            node.setFalseChild(addNode(node, rdrNode.getFalseChild()));
        }
        return node;
    }


    private void layoutNodes() {
        LAYOUT.positionNodes(_rootNode);
        setPreferredSize(RENDERER.getSize(LAYOUT.getMaxX(), LAYOUT.getMaxY()));
    }


    private TreeNode setSelectedNode(MouseEvent e) {
        clearSelection(_rootNode);
        TreeNode node = getNodeAt(_rootNode, e.getX(), e.getY());
        if (node != null) {
            node.setSelected(true);
            repaint();
        }
        return node;
    }


    private TreeNode getNodeAt(TreeNode node, int x, int y) {
        TreeNode found = null;
        if (node != null) {
            int left = RENDERER.getScreenX(node);
            int right = left + TreeRenderer.NODE_SIZE;
            int top = RENDERER.getScreenY(node);
            int bottom = top + TreeRenderer.NODE_SIZE;
            if (x >= left && x <= right && y >= top && y <= bottom) {
                found = node;
            }
            else {
                found = getNodeAt(node.getFalseChild(), x, y);
                if (found == null) found = getNodeAt(node.getTrueChild(), x, y);
            }
        }
        return found;
    }


    private void clearSelection(TreeNode node) {
        if (node != null) {
            node.setSelected(false);
            clearSelection(node.getFalseChild());
            clearSelection(node.getTrueChild());
        }
    }


    private RdrNode getFirstChildNode(RdrTree tree) {
        if (tree == null) return null;
        RdrNode rootNode = tree.getRootNode();
        return rootNode != null ? rootNode.getTrueChild() : null;
    }

}
