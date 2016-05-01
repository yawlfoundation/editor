package org.yawlfoundation.yawl.worklet.tree;

/**
 * @author Michael Adams
 * @date 29/04/2016
 */
public class TreeLayout {

    private int _minX = Integer.MAX_VALUE;
    private int _maxX = Integer.MIN_VALUE;
    private int _maxY = 0;

    public void positionNodes(TreeNode root) {
        setDepths(root, 0);
        setSlots(root);
        slideTreeLeft(root);
    }


    public int getMaxX() { return _maxX; }

    public int getMaxY() { return _maxY; }


    private void setDepths(TreeNode node, int depth) {
        if (node != null) {
            node.setY(depth);
            _maxY = depth;
            setDepths(node.getFalseChild(), depth + 1);
            setDepths(node.getTrueChild(), depth + 1);
        }
    }


    private void setSlots(TreeNode node) {
        if (node != null) {
            setSlots(node.getFalseChild());
            setSlots(node.getTrueChild());

            node.setInitialX();
            if (! (node.isLeaf() || node.isFalseChild())) {
                compressTree(node);
            }

            int x = node.getX();
            if (x < _minX) _minX = x;
            if (x > _maxX) _maxX = x;
        }
    }


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
