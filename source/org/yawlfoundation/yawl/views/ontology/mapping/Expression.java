package org.yawlfoundation.yawl.views.ontology.mapping;

import org.yawlfoundation.yawl.elements.YDecomposition;
import org.yawlfoundation.yawl.elements.YTask;
import org.yawlfoundation.yawl.elements.data.YVariable;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Michael Adams
 * @date 29/08/2016
 */
public abstract class Expression {

    private YTask _task;
    private String _query;


    public Expression(YTask task, String query) {
        _task = task;
        _query = query;
    }


    public abstract Set<String> getRefersTo();

    public abstract YDecomposition getDecomposition();


    protected Set<String> getRefersTo(Set<String> varNames) {

        // a complex data mapping may refer to more than one variable
        Set<String> refersTo = new HashSet<String>();

        String prefix = "/" + getDecomposition().getID() + "/";
        for (String varName : varNames) {
            String xPath = prefix + varName + "/";
            if (_query.contains(xPath) || _query.endsWith(xPath) ||
                    _query.endsWith(prefix + varName)) {
                refersTo.add(varName);
            }
        }
        return refersTo;
    }


    public YTask getTask() {
        return _task;
    }


    public boolean refersToVariable(YVariable variable) {
        return variable != null && getRefersTo().contains(variable.getName()) &&
                getDecomposition().equals(variable.getParentDecomposition());
    }

}
