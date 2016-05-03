package org.yawlfoundation.yawl.worklet.tree;

import java.awt.*;

/**
 * Paints a tree of TreeNodes in a Graphics context
 *
 * @author Michael Adams
 * @date 1/05/2016
 */
public class TreeRenderer {

    private static final int V_SPACE = 30;
    private static final int X_OFFSET = 10;
    private static final int Y_OFFSET = 10;
    protected static final int NODE_SIZE = 16;



    public void paint(Graphics g, TreeNode node) {
        if (node != null) {
            int x = getScreenX(node);
            int y = getScreenY(node);
            g.drawRoundRect(x, y, NODE_SIZE, NODE_SIZE, 5, 5);
            if (node.isSelected()) {
                Color orig = g.getColor();
                g.setColor(Color.LIGHT_GRAY);
                g.fillRoundRect(x, y, NODE_SIZE, NODE_SIZE, 5, 5);
                g.setColor(orig);
            }

            paint(g, node.getFalseChild());
            paint(g, node.getTrueChild());

            drawArc(g, node, node.getTrueChild());
            drawArc(g, node, node.getFalseChild());
        }
    }


    public int getScreenX(TreeNode node) { return node.getX() * NODE_SIZE + X_OFFSET; }

    public int getScreenY(TreeNode node) { return node.getY() * V_SPACE + Y_OFFSET; }


    public Dimension getSize(int x, int y) {
        int width = x * NODE_SIZE + X_OFFSET;
        int height = y * V_SPACE + Y_OFFSET;
        return new Dimension(width, height);
    }


    private void drawArc(Graphics g, TreeNode parent, TreeNode child) {
        if (! (parent == null || child == null)) {
            g.drawLine(getScreenX(parent) + NODE_SIZE / 2, getScreenY(parent) + NODE_SIZE,
                    getScreenX(child) + NODE_SIZE / 2, getScreenY(child));
        }
    }

}
