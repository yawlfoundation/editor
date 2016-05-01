package org.yawlfoundation.yawl.worklet.tree;

import org.yawlfoundation.yawl.worklet.rdr.RdrNode;

public class TreeNode {
    int _x;
    int _y;
    TreeNode _parent;
    TreeNode _trueChild;
    TreeNode _falseChild;
    RdrNode _rdrNode;
    boolean _selected;


    public TreeNode(TreeNode parent, RdrNode rdrNode) {
        _parent = parent;
        _rdrNode = rdrNode;
    }

    public int getX() { return _x; }

    public void setX(int x) { _x = x; }


    public int getY() { return _y; }

    public void setY(int y) { _y = y; }


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



    public void setInitialX() {
        if (isLeaf()) {
            String trace = tracePath();
            setX(trace != null ? Integer.parseInt(trace, 2) * 2 : 0);
        }
        else {
            TreeNode falseChild = getFalseChild();
            TreeNode trueChild = getTrueChild();
            if (falseChild == null) {
                setX(trueChild.getX() - 1);
            }
            else if (trueChild == null) {
                setX(falseChild.getX() + 1);
            }
            else {
                setX((falseChild.getX() + trueChild.getX()) / 2);
            }
        }
    }


    public void moveBranch(int moveBy) {
        _x = _x + moveBy;
        if (getFalseChild() != null) getFalseChild().moveBranch(moveBy);
        if (getTrueChild() != null) getTrueChild().moveBranch(moveBy);
    }


    public void setSelected(boolean b) { _selected = b; }

    public boolean isSelected() { return _selected; }


    private String tracePath() {
        if (_parent != null) {
            return _parent.tracePath() + ( isTrueChild() ? "1" : "0");
        }
        return "0";
    }

}
