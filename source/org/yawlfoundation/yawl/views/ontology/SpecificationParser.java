package org.yawlfoundation.yawl.views.ontology;

import org.yawlfoundation.yawl.editor.core.YSpecificationHandler;
import org.yawlfoundation.yawl.elements.*;
import org.yawlfoundation.yawl.elements.data.YParameter;
import org.yawlfoundation.yawl.elements.data.YVariable;
import org.yawlfoundation.yawl.views.ontology.mapping.Mapping;
import org.yawlfoundation.yawl.views.ontology.resourcing.ResourceParser;
import org.yawlfoundation.yawl.views.ontology.resourcing.TaskResources;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Michael Adams
 *         - prototype by Gary Grossgarten (h-brs.de)
 *         <p>
 *         This class is used to load a specification with YAWL's YSpecificationHandler.
 *         Uses the YAWL API to get all the information needed to create individuals in the
 *         OntologyPopulator class.
 */
public class SpecificationParser {

    private YSpecificationHandler _specificationHandler;

    private Set<YNet> _nets;
    private Set<YAWLServiceGateway> _taskDecompositions;
    private Set<YTask> _tasks;
    private Set<YInputCondition> _inputConditions;
    private Set<YOutputCondition> _outputConditions;
    private Set<YCondition> _conditions;
    private Set<Mapping> _mappings;
    private Set<YParameter> _inputParameters;
    private Set<YParameter> _outputParameters;
    private Set<YParameter> _ioParameters;
    private Set<YVariable> _localVariables;
    private Map<YTask, TaskResources> _taskResources;
    private Map<YExternalNetElement, Set<YExternalNetElement>> _flowRelations;


    public SpecificationParser(String uri) {
        _specificationHandler = new YSpecificationHandler();

        try {
            _specificationHandler.load(uri);
            populateSets();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    public SpecificationParser(YSpecificationHandler handler) {
        _specificationHandler = handler;
        populateSets();
    }


    /**
     * @return the loaded YSpecification
     */
    public YSpecification getSpecification() {
        return _specificationHandler.getSpecification();
    }


    /**
     * @return the set of all net decompositions of the loaded YSpecification
     */
    public Set<YNet> getNets() {
        return _nets;
    }


    /**
     * @return the set of all task decompositions of the loaded YSpecification
     */
    public Set<YAWLServiceGateway> getTaskDecompositions() {
        return _taskDecompositions;
    }


    /**
     * @return the root net of the loaded YSpecification
     */
    public YNet getRootNet() {
        return _specificationHandler.getControlFlowHandler().getRootNet();
    }


    /**
     * @return the set of all tasks of the loaded YSpecification
     */
    public Set<YTask> getTasks() {
        return _tasks;
    }


    /**
     * @return the set of all input conditions of the loaded YSpecification
     */
    public Set<YInputCondition> getInputConditions() {
        return _inputConditions;
    }


    /**
     * @return the set of all output conditions of the loaded YSpecification
     */
    public Set<YOutputCondition> getOutputConditions() {
        return _outputConditions;
    }


    /**
     * @return the set of all explicit conditions of the loaded YSpecification,
     * that are not input or output conditions
     */
    public Set<YCondition> getConditions() {
        return _conditions;
    }


    /**
     * @return the set of all data mappings for all tasks of the loaded YSpecification
     */
    public Set<Mapping> getMappings() {
        return _mappings;
    }


    /**
     * @return the set of all input only parameters for all decompositions of the
     * loaded YSpecification
     */
    public Set<YParameter> getInputParameters() {
        return _inputParameters;
    }


    /**
     * @return the set of all output only parameters for all decompositions of the
     * loaded YSpecification
     */
    public Set<YParameter> getOutputParameters() {
        return _outputParameters;
    }


    /**
     * @return the set of all input/output parameters for all decompositions of the
     * loaded YSpecification
     */
    public Set<YParameter> getIOParameters() {
        return _ioParameters;
    }


    /**
     * @return the set of all local variables for all nets of the loaded YSpecification
     */
    public Set<YVariable> getLocalVariables() {
        return _localVariables;
    }


    /**
     * @return a map containing all YExternalNetElements pointing to their successors
     */
    public Map<YExternalNetElement, Set<YExternalNetElement>> getFlowRelations() {
        return _flowRelations;
    }


    public Map<YTask, TaskResources> getTaskResources() {
        return _taskResources;
    }


    private void populateSets() {
        initDataStructures();
        populateSetsFromNetDecompositions();
        populateSetsFromTaskDecompositions();
        populateIOParameters();
        populateTaskResources();
    }


    private void initDataStructures() {
        _nets = _specificationHandler.getControlFlowHandler().getNets();
        _taskDecompositions = new HashSet<YAWLServiceGateway>(
                _specificationHandler.getControlFlowHandler().getTaskDecompositions());
        _tasks = new HashSet<YTask>();
        _inputConditions = new HashSet<YInputCondition>();
        _outputConditions = new HashSet<YOutputCondition>();
        _conditions = new HashSet<YCondition>();
        _mappings = new HashSet<Mapping>();
        _inputParameters = new HashSet<YParameter>();
        _outputParameters = new HashSet<YParameter>();
        _ioParameters = new HashSet<YParameter>();
        _localVariables = new HashSet<YVariable>();
        _taskResources = new HashMap<YTask, TaskResources>();
        _flowRelations = new HashMap<YExternalNetElement, Set<YExternalNetElement>>();
    }


    private void populateSetsFromNetDecompositions() {
        for (YNet net : _nets) {
            _localVariables.addAll(net.getLocalVariables().values());
            _inputParameters.addAll(net.getInputParameters().values());
            _outputParameters.addAll(net.getOutputParameters().values());

            for (YExternalNetElement element : net.getNetElements().values()) {
                if (element instanceof YTask) {
                    _tasks.add((YTask) element);
                    _mappings.addAll(initMappings((YTask) element));
                }
                else if (element instanceof YInputCondition) {
                    _inputConditions.add((YInputCondition) element);
                }
                else if (element instanceof YOutputCondition) {
                    _outputConditions.add((YOutputCondition) element);
                }
                else {  // plain condition
                    YCondition condition = (YCondition) element;
                    if (!condition.isImplicit()) {
                        _conditions.add(condition);
                    }
                }
                populateFlowRelations(element);
            }
        }
    }


    private void populateSetsFromTaskDecompositions() {
        for (YDecomposition decomposition : _taskDecompositions) {
            _inputParameters.addAll(decomposition.getInputParameters().values());
            _outputParameters.addAll(decomposition.getOutputParameters().values());
        }
    }


    private void populateFlowRelations(YExternalNetElement element) {
        Set<YExternalNetElement> postSetElements = new HashSet<YExternalNetElement>();
        for (YExternalNetElement postSetElement : element.getPostsetElements()) {

            // conflate flows with implicit conditions
            if (postSetElement instanceof YCondition &&
                    ((YCondition) postSetElement).isImplicit()) {
                postSetElements.addAll(postSetElement.getPostsetElements());
            }
            else {
                postSetElements.add(postSetElement);
            }
        }
        if (!postSetElements.isEmpty()) {
            _flowRelations.put(element, postSetElements);
        }
    }


    private void populateIOParameters() {
        Set<YParameter> outputsToRemove = new HashSet<YParameter>();
        for (YParameter input : _inputParameters) {
            for (YParameter output : _outputParameters) {
                if (isParameterMatch(input, output)) {
                    _ioParameters.add(input);
                    outputsToRemove.add(output);
                }
            }
        }
        _inputParameters.removeAll(_ioParameters);
        _outputParameters.removeAll(outputsToRemove);
    }


    private boolean isParameterMatch(YParameter input, YParameter output) {
        return input.getName().equals(output.getName()) &&
                input.getParentDecomposition().getID().equals(
                        output.getParentDecomposition().getID());
    }


    private Set<Mapping> initMappings(YTask task) {
        Set<Mapping> mappings = new HashSet<Mapping>();
        Map<String, String> startingMappings = task.getDataMappingsForTaskStarting();
        for (String mapsTo : startingMappings.keySet()) {
            mappings.add(new Mapping(task, startingMappings.get(mapsTo), mapsTo,
                    Mapping.Type.Starting));
        }
        Map<String, String> completedMappings = task.getDataMappingsForTaskCompletion();
        for (String query : completedMappings.keySet()) {
            mappings.add(new Mapping(task, query, completedMappings.get(query),
                    Mapping.Type.Completed));
        }
        return mappings;
    }


    private void populateTaskResources() {
        _taskResources = new HashMap<YTask, TaskResources>();
        ResourceParser parser = new ResourceParser();
        for (YTask task : _tasks) {
            TaskResources resources = parser.parse(task);
            if (resources != null) {
                _taskResources.put(task, resources);
            }
        }
    }

}
