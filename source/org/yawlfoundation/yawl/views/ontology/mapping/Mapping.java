package org.yawlfoundation.yawl.views.ontology.mapping;

import org.yawlfoundation.yawl.elements.YDecomposition;
import org.yawlfoundation.yawl.elements.YTask;
import org.yawlfoundation.yawl.elements.data.YVariable;

/**
 * @author Michael Adams
 * @date 29/08/2016
 */
public class Mapping {

    private YTask _task;
    private Expression _expression;
    private String _mapsTo;


    public Mapping(YTask task, String query, String mapsTo, Type type) {
        _task = task;
        _mapsTo = mapsTo;
        _expression = type == Type.Starting ? new StartingExpression(task, query) :
                new CompletedExpression(task, query);
    }

    public YTask getTask() {
        return _task;
    }

    public String getMapsTo() {
        return _mapsTo;
    }

    public Expression getExpression() {
        return _expression;
    }

    public Type getType() {
        return _expression instanceof StartingExpression ? Type.Starting : Type.Completed;
    }

    public String getTypeString() {
        return getType().name();
    }

    public boolean mapsToVariable(YVariable variable) {
        return variable != null && variable.getName().equals(_mapsTo) &&
                getMapsToDecomposition().equals(variable.getParentDecomposition());
    }

    private YDecomposition getMapsToDecomposition() {
        return getType() == Type.Starting ? _task.getDecompositionPrototype() :
                _task.getNet();
    }


    public enum Type {Starting, Completed}

}
