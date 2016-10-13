package org.yawlfoundation.yawl.views.ontology.mapping;

import org.yawlfoundation.yawl.elements.YDecomposition;
import org.yawlfoundation.yawl.elements.YNet;
import org.yawlfoundation.yawl.elements.YTask;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Michael Adams
 * @date 29/08/2016
 */
public class StartingExpression extends Expression {

    public StartingExpression(YTask task, String query) {
        super(task, query);
    }


    @Override
    public Set<String> getRefersTo() {

        // get union of all net variable names
        Set<String> varNames = new HashSet<String>(getDecomposition().getInputParameterNames());
        varNames.addAll(getDecomposition().getOutputParameterNames());
        varNames.addAll(((YNet) getDecomposition()).getLocalVariables().keySet());

        return super.getRefersTo(varNames);
    }


    @Override
    public YDecomposition getDecomposition() {
        return getTask().getNet();
    }

}
