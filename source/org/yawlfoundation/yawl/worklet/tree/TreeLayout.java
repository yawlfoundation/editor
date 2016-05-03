package org.yawlfoundation.yawl.worklet.tree;

/**
 * @author Michael Adams
 * @date 29/04/2016
 */
public class TreeLayout {

    // track final tree dimensions
    private int _minX;
    private int _maxX;
    private int _maxY;


    public void positionNodes(TreeNode root) {
        initDimensions();
        positionNodes(root, 0);
        slideTreeLeft(root);
    }


    public int getMaxX() { return _maxX; }

    public int getMaxY() { return _maxY; }


    private void initDimensions() {
        _minX = Integer.MAX_VALUE;
        _maxX = Integer.MIN_VALUE;
        _maxY = 0;
    }


    /**
     * Set the depth (y coordinate) and initial column (x coordinate) of a node
     * @param node the node to position
     * @param depth the row ordinal for the node
     */
    private void positionNodes(TreeNode node, int depth) {
        if (node != null) {

            // set the node's row, and save the widest row value
            node.setY(depth);
            _maxY = depth;

            // do a post order traversal of the tree (handle leaf nodes first)
            positionNodes(node.getFalseChild(), depth + 1);
            positionNodes(node.getTrueChild(), depth + 1);

            // set the node's ordinal column, and if it's a true branch move it
            // left as far as possible (if any) without overlapping other branches
            node.setInitialX();
            if (! (node.isLeaf() || node.isFalseChild())) {
                compressTree(node);
            }

            // save the minimum and maximum column values
            updateWidth(node.getX());
        }
    }


    private void updateWidth(int x) {
        if (x < _minX) _minX = x;
        if (x > _maxX) _maxX = x;
    }


    /**
     * Move entire tree left, so that the left-most node is in column 0
     * @param node
     */
    private void slideTreeLeft(TreeNode node) {
        if (_minX > 0) {
            node.moveBranch(_minX * -1);
            _maxX -= _minX;
            _minX = 0;
        }
    }


    private void compressTree(TreeNode node) {
        if (! (node.getTrueChild() == null || node.getFalseChild() == null)) {

            // get max slot of false branch
            int maxFalseSlot = getMaxX(node.getFalseChild());

            // get min slot of true branch
            int minTrueSlot = getMinX(node.getTrueChild());

            int diff = minTrueSlot - maxFalseSlot - 2;
            if (diff > 0) {
                node.getTrueChild().moveBranch(diff * -1);
            }
        }
    }


    public int getMinX(TreeNode node) {
        int min = node.getX();
        if (node.getFalseChild() != null) {
            min = Math.min(min, getMinX(node.getFalseChild()));
        }
        else if (node.getTrueChild() != null) {
            min = Math.min(min, getMinX(node.getTrueChild()));
        }
        return min;
    }


    public int getMaxX(TreeNode node) {
        int max = node.getX();
        if (node.getTrueChild() != null) {
            max = Math.max(max, getMaxX(node.getTrueChild()));
        }
        else if (node.getFalseChild() != null) {
            max = Math.max(max, getMaxX(node.getFalseChild()));
        }
        return max;
    }

}
