package org.yawlfoundation.yawl.views.ontology.mapping;

import org.yawlfoundation.yawl.elements.YDecomposition;
import org.yawlfoundation.yawl.elements.YTask;

import java.util.Set;

/**
 * @author Michael Adams
 * @date 29/08/2016
 */
public class CompletedExpression extends Expression {

    public CompletedExpression(YTask task, String query) {
        super(task, query);
    }


    @Override
    public Set<String> getRefersTo() {

        // only the task output vars can be referenced by completion mappings
        return super.getRefersTo(getDecomposition().getOutputParameterNames());
    }


    @Override
    public YDecomposition getDecomposition() {
        return getTask().getDecompositionPrototype();
    }

}
