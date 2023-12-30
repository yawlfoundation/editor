package org.yawlfoundation.yawl.analyser.testgeneration.RACC.testCase;

import java.util.ArrayList;

public class Clause implements Cloneable {
    public String expression;
    private Clause _parent;
    private ArrayList<Clause> _children;
    public LogicalOperator operator;
    private boolean _givenValue;
    private boolean isNegative;
    private final String _virtualId;

    public Clause(String expression, String virtualId) {
        this.expression = expression;
        this._virtualId = virtualId;
        this._children = new ArrayList<>();
        this.isNegative = false;
    }

    public String getVirtualId() {
        return this._virtualId;
    }

    public boolean isNegative() {
        return isNegative;
    }

    public void setNegative(boolean negative) {
        isNegative = negative;
    }

    public void setParent(Clause parent) {
        this._parent = parent;
    }

    public void addChild(Clause child) {
        child.setParent(this);
        this._children.add(child);
    }

    public void clearChildren() {
        this._children = new ArrayList<>();
    }

    public void setChildren(ArrayList<Clause> children) {
        this.clearChildren();
        for (Clause child : children) {
            this.addChild(child);
        }
    }

    public Clause getParent() {
        return this._parent;
    }

    public ArrayList<Clause> getChildren() {
        return this._children;
    }

    public boolean isRoot() {
        return this._parent == null;
    }

    public void setOperator(LogicalOperator operator) {
        this.operator = operator;
    }

    public boolean isLeaf() {
        return this._children.size() == 0;
    }

    public boolean getValue() {
        return this._givenValue;
    }

    public void setValue(boolean value) {
        this._givenValue = value;
    }

    public Clause clone() {
        try {
            Clause cloned = (Clause) super.clone();
            return this._cloneChild(cloned);
        } catch (CloneNotSupportedException ex) {
            return this;
        }
    }

    private Clause _cloneChild(Clause clause) throws CloneNotSupportedException {
        if (clause.isLeaf()) {
            Clause leafClause = new Clause(expression, clause.getVirtualId());
            leafClause.setValue(clause.getValue());
            return leafClause;
        }
        Clause cloned = new Clause(null, null);
        cloned.setValue(clause.getValue());
        cloned.operator = clause.operator;
        cloned.setParent(null);
        cloned.clearChildren();
        for (int i = 0; i < clause.getChildren().size(); i++) {
            Clause clonedChild = this._cloneChild(clause.getChildren().get(i));
            clonedChild.setParent(cloned);
            cloned.addChild(clonedChild);
        }
        return cloned;
    }
}
