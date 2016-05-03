package org.yawlfoundation.yawl.worklet.tree;

import org.yawlfoundation.yawl.worklet.rdr.RdrNode;

public class TreeNode {
    int _x;
    int _y;
    int _column;
    TreeNode _parent;
    TreeNode _trueChild;
    TreeNode _falseChild;
    RdrNode _rdrNode;
    boolean _selected;


    public TreeNode(TreeNode parent, RdrNode rdrNode) {
        _parent = parent;
        _rdrNode = rdrNode;
        _column = -1;
    }

    public int getX() { return _x; }

    public void setX(int x) { _x = x; }


    public int getY() { return _y; }

    public void setY(int y) { _y = y; }


    public void setSelected(boolean b) { _selected = b; }

    public boolean isSelected() { return _selected; }


    public RdrNode getRdrNode() { return _rdrNode; }


    public void setTrueChild(TreeNode next) { _trueChild = next; }

    public TreeNode getTrueChild() { return _trueChild; }


    public void setFalseChild(TreeNode next) { _falseChild = next; }

    public TreeNode getFalseChild() { return _falseChild; }


    public boolean isLeaf() {
        return _trueChild == null && _falseChild == null;
    }

    public boolean isFalseChild() {
        return _parent != null && _parent.getFalseChild() == this;
    }

    public boolean isTrueChild() {
        return _parent != null && _parent.getTrueChild() == this;
    }


    /**
     * Sets the initial x coordinate for this node. Every leaf node has a node-width's
     * space between it and its sibling. Every pair has a minimum one node-width's
     * space between is true node and the following pair's false node (whether or
     * not those nodes actually exist)
     */
    public void setInitialX() {
        if (isLeaf()) {

            // double the ordinal column value to add intra and inter spacings
            setX(getColumn() * 2);
        }
        else {

            // this node has one or two child nodes, so centre it above them
            TreeNode falseChild = getFalseChild();
            TreeNode trueChild = getTrueChild();

            // if this node only has a true child, place it one column to the left
            if (falseChild == null) {
                setX(trueChild.getX() - 1);
            }

            // if this node only has a false child, place it one column to the right
            else if (trueChild == null) {
                setX(falseChild.getX() + 1);
            }

            // node has two children, so centre the node above them
            else {
                setX((falseChild.getX() + trueChild.getX()) / 2);
            }
        }
    }


    /**
     * Moves this node and all its children to the left or right by the value passed.
     * @param moveBy if >0, moves the branch right by that number of columns; if <0,
     *               move the branch left by that number of columns.
     */
    public void moveBranch(int moveBy) {
        _x += moveBy;
        if (getFalseChild() != null) getFalseChild().moveBranch(moveBy);
        if (getTrueChild() != null) getTrueChild().moveBranch(moveBy);
    }


    /**
     * Determines this node's ordinal column position within its row, 0 being the
     * left-most node on this row, n-1 being the right-most, where n is the maximum
     * number of nodes that can exist on this row. Note: the node's position is not
     * dependent on whether nodes exist in every column (i.e. gaps are counted)
     * @return this node's ordinal column position
     */
    private int getColumn() {
        if (_column < 0) {         // not yet set

            // if root node, its position is 0 (only position on top row)
            // else its position is double its parent's for the false child
            // or double its parent's + 1 for the true child
            _column = _parent == null ? 0 : _parent.getColumn() * 2;
            if (isTrueChild()) _column++;
        }
        return _column;
    }

}
