package org.yawlfoundation.yawl.views.ontology;

import org.yawlfoundation.yawl.elements.YDecomposition;
import org.yawlfoundation.yawl.elements.YExternalNetElement;
import org.yawlfoundation.yawl.elements.YNet;
import org.yawlfoundation.yawl.elements.YSpecification;
import org.yawlfoundation.yawl.elements.data.YVariable;
import org.yawlfoundation.yawl.views.ontology.mapping.Expression;
import org.yawlfoundation.yawl.views.ontology.mapping.Mapping;
import org.yawlfoundation.yawl.views.ontology.mapping.StartingExpression;

import java.util.Set;

/**
 * @author Michael Adams
 *         - prototype by Gary Grossgarten (h-brs.de)
 *         <p>
 *         This class is used to get unique names for individuals used in the ontology
 */
public class NamingConventions {

    private static final char SEP = '!';


    public static String getNameFor(YSpecification ySpecification) {
        return getPrefix() + ySpecification.getURI();
    }


    public static String getNameFor(YDecomposition decomposition) {
        return getPrefix() + decomposition.getID();
    }


    public static String getNameFor(YNet yNet) {
        return getPrefix() + yNet.getID();
    }


    public static String getNameFor(YVariable yVariable) {
        return getNameFor(yVariable.getParentDecomposition()) + SEP + yVariable.getName();
    }


    public static String getNameFor(Mapping mapping) {
        String netID = mapping.getTask().getNet().getID();
        String taskID = mapping.getTask().getID();
        String source = mapping.getType() == Mapping.Type.Starting ? netID : taskID;
        String target = mapping.getType() == Mapping.Type.Starting ? taskID : netID;

        return getPrefix() + "Mapping" + SEP + source + SEP +
                getFirstRefersTo(mapping.getExpression()) + SEP +
                target + SEP + mapping.getMapsTo();
    }


    public static String getNameFor(YExternalNetElement element) {
        return getPrefix() + element.getNet().getID() + SEP + element.getID();
    }


    public static String getNameFor(Expression expression) {
        String type = expression instanceof StartingExpression ? "Input" : "Output";
        return getPrefix() + type + SEP + expression.getDecomposition().getID() + SEP +
                getFirstRefersTo(expression) + SEP + expression.getTask().getID();
    }


    private static String getFirstRefersTo(Expression expression) {
        Set<String> refersTo = expression.getRefersTo();
        return refersTo.isEmpty() ? "null" : refersTo.iterator().next();
    }


    private static String getPrefix() {
        return OntologyPopulator.NAMESPACE;
    }

}
