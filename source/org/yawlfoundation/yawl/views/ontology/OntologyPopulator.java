package org.yawlfoundation.yawl.views.ontology;

import org.apache.jena.ontology.*;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.shared.JenaException;
import org.apache.jena.util.FileManager;
import org.yawlfoundation.yawl.elements.*;
import org.yawlfoundation.yawl.elements.data.YParameter;
import org.yawlfoundation.yawl.elements.data.YVariable;
import org.yawlfoundation.yawl.views.ontology.mapping.Expression;
import org.yawlfoundation.yawl.views.ontology.mapping.Mapping;
import org.yawlfoundation.yawl.views.ontology.resourcing.TaskResources;

import java.io.InputStream;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Michael Adams
 *         - prototype by Gary Grossgarten (h-brs.de)
 */

public class OntologyPopulator {

    public static final String NAMESPACE = "http://www.semanticweb.org/yawl/ontologies/YSpecificationOntology#";

    private OntModel ontology;

    // Classes (mirroring YAWL classes)
    private OntClass yNet;
    private OntClass yDecomposition;
    private OntClass yTask;
    private OntClass yInputCondition;
    private OntClass yOutputCondition;
    private OntClass yCondition;
    private OntClass ySpecification;
    private OntClass yVariable;
    private OntClass Participant;
    private OntClass Role;

    // Introduced classes
    private OntClass mapping;
    private OntClass expression;

    //Object Properties
    private ObjectProperty decomposesTo;
    private ObjectProperty hasCompletedMapping;
    private ObjectProperty hasStartingMapping;
    private ObjectProperty hasDecomposition;
    private ObjectProperty hasExpression;
    private ObjectProperty hasInputCondition;
    private ObjectProperty hasOutputCondition;
    private ObjectProperty hasInputParameter;
    private ObjectProperty hasOutputParameter;
    private ObjectProperty hasLocalVariable;
    private ObjectProperty hasRootNet;
    private ObjectProperty hasSpecification;
    private ObjectProperty mapsTo;
    private ObjectProperty refersTo;
    private ObjectProperty flowsInto;
    private ObjectProperty hasExternalNetElement;
    private ObjectProperty isFourEyesTaskOf;
    private ObjectProperty isFamiliarTaskOf;
    private ObjectProperty hasParticipant;
    private ObjectProperty hasRole;

    /**
     * Individuals
     * #####################################
     * <p>
     * inputParameterIndividuals:
     * Every individual points to a InputParameter as long as its input only
     * <p>
     * outputParameterIndividuals:
     * Every individual points to a OutputParameter as long as its output only
     * <p>
     * yIOParameterIndividuals:
     * Every Input-/OutputVariable individual points to a Map containing the belonging Input and Output Parameter
     * Map<InputParameter, OutputParameter>
     * <p>
     * startingMappingIndividuals:
     * Every individual points to a Map containing the belonging YTask and mapping
     * Map<YTask , Map< mapsToVariable, expression query>
     * <p>
     * completedMappingIndividuals:
     * Every individual points to a Map containing the belonging YTask and mapping
     * Map<YTask , Map< expression query, mapsToVariable>
     * <p>
     * expressionIndividuals:
     * Every individual points to a map containing the refersToVariable and its respective expression query
     */

    private Individual specificationIndividual;
    private Map<Individual, YTask> taskIndividuals;
    private Map<Individual, YInputCondition> inputConditionIndividuals;
    private Map<Individual, YOutputCondition> outputConditionIndividuals;
    private Map<Individual, YCondition> conditionIndividuals;
    private Map<Individual, YDecomposition> decompositionIndividuals;
    private Map<Individual, YNet> netIndividuals;
    private Map<Individual, YParameter> inputParameterIndividuals;
    private Map<Individual, YParameter> outputParameterIndividuals;
    private Map<Individual, YParameter> ioParameterIndividuals;
    private Map<Individual, YVariable> localVariableIndividuals;
    private Map<Individual, Mapping> startingMappingIndividuals;
    private Map<Individual, Mapping> completedMappingIndividuals;
    private Map<Individual, Expression> expressionIndividuals;
    private Map<Individual, String> participantIndividuals;
    private Map<Individual, String> roleIndividuals;

    private Map<YExternalNetElement, Individual> netElementToIndividualLookup;
    private Map<YVariable, Individual> variableToIndividualLookup;


    public OntologyPopulator() {
    }

    public OntologyPopulator(String ontoFile) {
        loadOntologyModel(ontoFile);
    }

    /**
     * loads the ontology from a given path and initializes the
     * Classes and Object Properties of the respective ontology
     * for further actions
     */
    public void loadOntologyModel(String ontoFile) {

        OntModel ontoModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
        try {
            InputStream in = FileManager.get().open(ontoFile);
            try {
                ontoModel.read(in, null);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("Ontology " + ontoFile + " loaded.");
        }
        catch (JenaException je) {
            System.err.println("ERROR" + je.getMessage());
            je.printStackTrace();
            System.exit(0);
        }
        ontology = ontoModel;
        initOntClasses();
        initObjectProperties();
    }

    // initializes the Object Properties of the imported ontology
    private void initObjectProperties() {
        flowsInto = getObjectProperty("flowsInto");
        decomposesTo = getObjectProperty("decomposesTo");
        hasCompletedMapping = getObjectProperty("hasCompletedMapping");
        hasStartingMapping = getObjectProperty("hasStartingMapping");
        hasDecomposition = getObjectProperty("hasDecomposition");
        hasExpression = getObjectProperty("hasExpression");
        hasInputCondition = getObjectProperty("hasInputCondition");
        hasOutputCondition = getObjectProperty("hasOutputCondition");
        hasInputParameter = getObjectProperty("hasInputParameter");
        hasOutputParameter = getObjectProperty("hasOutputParameter");
        hasLocalVariable = getObjectProperty("hasLocalVariable");
        hasRootNet = getObjectProperty("hasRootNet");
        hasSpecification = getObjectProperty("hasSpecification");
        mapsTo = getObjectProperty("mapsTo");
        refersTo = getObjectProperty("refersTo");
        hasExternalNetElement = getObjectProperty("hasExternalNetElement");
        isFourEyesTaskOf = getObjectProperty("isFourEyesTaskOf");
        isFamiliarTaskOf = getObjectProperty("isFamiliarTaskOf");
        hasParticipant = getObjectProperty("hasParticipant");
        hasRole = getObjectProperty("hasRole");
    }

    private ObjectProperty getObjectProperty(String name) {
        return ontology.getObjectProperty(NAMESPACE + name);
    }

    // initializes the Classes of the imported ontology
    private void initOntClasses() {
        yTask = getOntClass("YTask");
        expression = getOntClass("Expression");
        mapping = getOntClass("Mapping");
        yDecomposition = getOntClass("YDecomposition");
        yNet = getOntClass("YNet");
        yInputCondition = getOntClass("YInputCondition");
        yOutputCondition = getOntClass("YOutputCondition");
        yCondition = getOntClass("YCondition");
        ySpecification = getOntClass("YSpecification");
        yVariable = getOntClass("YVariable");
        Participant = getOntClass("Participant");
        Role = getOntClass("Role");
    }

    private OntClass getOntClass(String name) {
        return ontology.getOntClass(NAMESPACE + name);
    }

    /**
     * Uses the SpecificationParser to get all the information needed, to add the individuals of the ontology classes.
     *
     * @param specificationParser is used to get the basic information to create individuals
     */
    public void addIndividuals(SpecificationParser specificationParser) {
        addYTaskIndividuals(specificationParser.getTasks());
        addYDecompositionIndividuals(specificationParser.getTaskDecompositions());
        addYNetIndividuals(specificationParser.getNets());
        addYInputConditionIndividuals(specificationParser.getInputConditions());
        addYOutputConditionIndividuals(specificationParser.getOutputConditions());
        addConditionIndividuals(specificationParser.getConditions());
        generateNetElementToIndividualLookupMap();
        addYSpecificationIndividual(specificationParser.getSpecification());
        addIOParameterIndividuals(specificationParser.getIOParameters());
        addYInputParameterIndividuals(specificationParser.getInputParameters());
        addYOutputParameterIndividuals(specificationParser.getOutputParameters());
        addLocalVariableIndividuals(specificationParser.getLocalVariables());
        generateVariableToIndividualLookupMap();
        addMappingIndividuals(specificationParser.getMappings());
        addExpressionIndividuals();
        addResourcingIndividuals(specificationParser.getTaskResources());
    }

    /**
     * Uses the SpecificationParser to get all the information needed, to add the Object Property relations to the ontology.
     *
     * @param specificationParser is used to get some object property relations (should propably be moved to ontology helper)
     */
    public void addObjectProperties(SpecificationParser specificationParser) {
        addFlowsIntoObjectProperties(specificationParser);
        addDecomposesToObjectProperties();
        addHasRootnetToObjectProperties(specificationParser);
        addHasDecompositionObjectProperties();
        addHasSpecificationObjectProperties();
        addConditionObjectProperties();
        addParameterObjectProperties();
        addHasLocalVariableObjectProperties();
        addMapsToObjectProperties();
        addMappingObjectProperties();
        addHasExpressionObjectProperties();
        addRefersToObjectProperties();
        addHasExternalNetElementObjectProperties();
        addResourcingObjectProperties(specificationParser.getTaskResources());
    }


    /**
     * can be utilized to print the ontology to the console in a certain format
     *
     * @param param is used as the format identifier
     */
    public void outputAs(String param) {
        ontology.getBaseModel().write(System.out, param);
    }

    /**
     * can be utilized to print the ontology to the console in owl format
     */
    public void output() {
        ontology.getBaseModel().write(System.out);
    }

    /**
     * can be utilized to print the ontology to a certain Writer (file export)
     *
     * @param writer is used to specify how to export the ontology
     */
    public void outputTo(Writer writer) {
        ontology.getBaseModel().write(writer);
        System.out.println("File successfully exported");
    }

    public OntModel getOntModel() {
        return ontology;
    }

    /**
     * Every decomposition belongs to a specification
     */
    private void addHasSpecificationObjectProperties() {
        for (Individual i : decompositionIndividuals.keySet()) {
            i.addProperty(hasSpecification, specificationIndividual);
        }
    }

    /**
     * adds all task individuals of the given YAWL Specification to the ontology
     *
     * @param taskSet is used to create a task individual for every ytask
     */
    public void addYTaskIndividuals(Set<YTask> taskSet) {
        taskIndividuals = new HashMap<Individual, YTask>();
        for (YTask task : taskSet) {
            taskIndividuals.put(ontology.createIndividual(
                    NamingConventions.getNameFor(task), yTask), task);
        }
    }

    /**
     * adds all task decomposition individuals of the given YAWL Specification to the ontology
     *
     * @param decompositionSet is used to create a decomposition individual for every task decomposition
     */
    public void addYDecompositionIndividuals(Set<YAWLServiceGateway> decompositionSet) {
        decompositionIndividuals = new HashMap<Individual, YDecomposition>();
        for (YDecomposition decomposition : decompositionSet) {
            decompositionIndividuals.put(ontology.createIndividual(
                    NamingConventions.getNameFor(decomposition),
                    yDecomposition), decomposition);
        }
    }


    /**
     * adds all net decomposition individuals of the given YAWL Specification to the ontology
     *
     * @param netSet is used to create a net individual for every net decomposition
     */
    public void addYNetIndividuals(Set<YNet> netSet) {
        netIndividuals = new HashMap<Individual, YNet>();
        for (YNet net : netSet) {
            netIndividuals.put(ontology.createIndividual(
                    NamingConventions.getNameFor(net), yNet), net);
        }
    }


    /**
     * adds all inputCondition individuals of the given YAWL Specification to the ontology
     *
     * @param inputConditionSet is used to create a input condition individual for every YInputCondition
     */
    public void addYInputConditionIndividuals(Set<YInputCondition> inputConditionSet) {
        inputConditionIndividuals = new HashMap<Individual, YInputCondition>();
        for (YInputCondition condition : inputConditionSet) {
            inputConditionIndividuals.put(ontology.createIndividual(
                    NamingConventions.getNameFor(condition),
                    yInputCondition), condition);
        }
    }


    /**
     * adds all inputCondition individuals of the given YAWL Specification to the ontology
     *
     * @param outputConditionSet is used to create a output condition individual for every YOutputCondition
     */
    public void addYOutputConditionIndividuals(Set<YOutputCondition> outputConditionSet) {
        outputConditionIndividuals = new HashMap<Individual, YOutputCondition>();
        for (YOutputCondition condition : outputConditionSet) {
            outputConditionIndividuals.put(ontology.createIndividual(
                    NamingConventions.getNameFor(condition),
                    yOutputCondition), condition);

        }
    }


    public void addConditionIndividuals(Set<YCondition> conditionSet) {
        conditionIndividuals = new HashMap<Individual, YCondition>();
        for (YCondition condition : conditionSet) {
            conditionIndividuals.put(ontology.createIndividual(
                    NamingConventions.getNameFor(condition),
                    yCondition), condition);

        }
    }


    /**
     * adds all inputParameter individuals of the given YAWL Specification to the ontology
     *
     * @param inputParameters is used to create a input parameter individual for every
     *                        YParameter that is input only
     */
    public void addYInputParameterIndividuals(Set<YParameter> inputParameters) {
        inputParameterIndividuals = new HashMap<Individual, YParameter>();
        for (YParameter parameter : inputParameters) {
            inputParameterIndividuals.put(ontology.createIndividual(
                    NamingConventions.getNameFor(parameter), yVariable),
                    parameter);
        }
    }

    /**
     * adds all outputParameter individuals of the given YAWL Specification to the ontology
     *
     * @param outputParameters is used to create a output parameter individual for every YParameter that is output only
     */
    public void addYOutputParameterIndividuals(Set<YParameter> outputParameters) {
        outputParameterIndividuals = new HashMap<Individual, YParameter>();
        for (YParameter parameter : outputParameters) {
            outputParameterIndividuals.put(ontology.createIndividual(
                    NamingConventions.getNameFor(parameter), yVariable),
                    parameter);
        }
    }


    public void addLocalVariableIndividuals(Set<YVariable> localVariables) {
        localVariableIndividuals = new HashMap<Individual, YVariable>();
        for (YVariable variable : localVariables) {
            localVariableIndividuals.put(ontology.createIndividual(
                    NamingConventions.getNameFor(variable), yVariable),
                    variable);
        }
    }


    public void addYSpecificationIndividual(YSpecification specification) {
        specificationIndividual = ontology.createIndividual(
                NamingConventions.getNameFor(specification), ySpecification);
    }

    /**
     * adds all expression Individuals to the ontology
     */
    private void addExpressionIndividuals() {
        expressionIndividuals = new HashMap<Individual, Expression>();
        addExpressionIndividuals(startingMappingIndividuals);
        addExpressionIndividuals(completedMappingIndividuals);
    }


    private void addExpressionIndividuals(Map<Individual, Mapping> map) {
        for (Mapping mapping : map.values()) {
            expressionIndividuals.put(ontology.createIndividual(
                    NamingConventions.getNameFor(mapping.getExpression()), expression),
                    mapping.getExpression());
        }
    }

    /**
     * adds all IO Parameter individuals to the ontology
     *
     * @param ioParameters is used to create a io parameter individual for every YParameter that is an input parameter
     *                     with a matching output parameter
     */
    private void addIOParameterIndividuals(Set<YParameter> ioParameters) {
        ioParameterIndividuals = new HashMap<Individual, YParameter>();
        for (YParameter parameter : ioParameters) {
            Individual i = ontology.createIndividual(
                    NamingConventions.getNameFor(parameter), yVariable);
            ioParameterIndividuals.put(i, parameter);
        }
    }


    /**
     * adds all mapping individuals to the ontology
     *
     * @param mappingIndividuals
     */
    private void addMappingIndividuals(Set<Mapping> mappingIndividuals) {
        startingMappingIndividuals = new HashMap<Individual, Mapping>();
        completedMappingIndividuals = new HashMap<Individual, Mapping>();
        for (Mapping mapping : mappingIndividuals) {
            if (mapping.getType() == Mapping.Type.Starting) {
                addStartingMappingIndividual(mapping);
            }
            else {
                addCompletedMappingIndividual(mapping);
            }
        }
    }


    private void addCompletedMappingIndividual(Mapping completedMapping) {
        String completed = NamingConventions.getNameFor(completedMapping);
        completedMappingIndividuals.put(ontology.createIndividual(completed, mapping),
                completedMapping);
    }


    private void addStartingMappingIndividual(Mapping startingMapping) {
        String starting = NamingConventions.getNameFor(startingMapping);
        startingMappingIndividuals.put(ontology.createIndividual(starting, mapping),
                startingMapping);
    }


    private void addResourcingIndividuals(Map<YTask, TaskResources> resourcesMap) {
        participantIndividuals = new HashMap<Individual, String>();
        roleIndividuals = new HashMap<Individual, String>();

        // get unique members to create individuals for
        Set<String> participants = new HashSet<String>();
        Set<String> roles = new HashSet<String>();
        for (TaskResources resources : resourcesMap.values()) {
            participants.addAll(resources.getParticipants());
            roles.addAll(resources.getRoles());
        }

        for (String pid : participants) {
            participantIndividuals.put(
                    ontology.createIndividual(NAMESPACE + pid, Participant), pid);
        }
        for (String rid : roles) {
            roleIndividuals.put(ontology.createIndividual(NAMESPACE + rid, Role), rid);
        }
    }


    private void addResourcingObjectProperties(Map<YTask, TaskResources> resourcesMap) {
        for (YTask task : resourcesMap.keySet()) {
            Individual iTask = getTaskIndividual(task);
            if (iTask != null) {
                TaskResources resources = resourcesMap.get(task);
                for (String pid : resources.getParticipants()) {
                    iTask.addProperty(hasParticipant, ontology.getIndividual(NAMESPACE + pid));
                }
                for (String rid : resources.getRoles()) {
                    iTask.addProperty(hasRole, ontology.getIndividual(NAMESPACE + rid));
                }
                String famTask = resources.getFamiliarTask();
                if (famTask != null) {
                    Individual iFamTask = getTaskIndividual(famTask);
                    if (iFamTask != null) {
                        iFamTask.addProperty(isFamiliarTaskOf, iTask);
                    }
                }
                String fourEyesTask = resources.getFourEyesTask();
                if (fourEyesTask != null) {
                    Individual iFourEyesTask = getTaskIndividual(fourEyesTask);
                    if (iFourEyesTask != null) {
                        iFourEyesTask.addProperty(isFourEyesTaskOf, iTask);
                    }
                }
            }
        }
    }


    /**
     * adds the Object Property relation hasCompletedMapping to all tasks that have
     * completed mappings
     */
    private void addMappingObjectProperties() {
        addMappingObjectProperties(startingMappingIndividuals, hasStartingMapping);
        addMappingObjectProperties(completedMappingIndividuals, hasCompletedMapping);
    }


    private void addMappingObjectProperties(Map<Individual, Mapping> mappingMap,
                                            ObjectProperty property) {
        for (Individual iTask : taskIndividuals.keySet()) {
            YTask yTask = taskIndividuals.get(iTask);
            for (Individual iMapping : mappingMap.keySet()) {
                Mapping mapping = mappingMap.get(iMapping);
                if (mapping.getTask().equals(yTask)) {
                    iTask.addProperty(property, iMapping);
                }
            }
        }
    }


    /**
     * adds the Object Property relation mapsTo to all mappings and their respective Parameters
     */
    private void addMapsToObjectProperties() {
        addMapsToObjectProperties(startingMappingIndividuals);
        addMapsToObjectProperties(completedMappingIndividuals);
    }


    private void addMapsToObjectProperties(Map<Individual, Mapping> map) {
        for (Individual i : map.keySet()) {
            Mapping mapping = map.get(i);
            for (YVariable variable : variableToIndividualLookup.keySet()) {
                if (mapping.mapsToVariable(variable)) {
                    i.addProperty(mapsTo, variableToIndividualLookup.get(variable));
                }
            }
        }
    }


    /**
     * adds the Object Property relation hasLocalVariable to all YNets and their respective Variables
     */
    private void addHasLocalVariableObjectProperties() {
        for (Individual i : netIndividuals.keySet()) {
            YNet net = netIndividuals.get(i);
            for (Individual iLocal : localVariableIndividuals.keySet()) {
                YVariable variable = localVariableIndividuals.get(iLocal);
                if (variable.getParentDecomposition().equals(net)) {
                    i.addProperty(hasLocalVariable, iLocal);
                }
            }
        }
    }


    private void addParameterObjectProperties() {
        addParameterObjectProperties(inputParameterIndividuals, hasInputParameter);
        addParameterObjectProperties(ioParameterIndividuals, hasInputParameter);
        addParameterObjectProperties(outputParameterIndividuals, hasOutputParameter);
        addParameterObjectProperties(ioParameterIndividuals, hasOutputParameter);
    }


    private void addParameterObjectProperties(Map<Individual, YParameter> parameters,
                                              ObjectProperty property) {
        for (Individual iParameter : parameters.keySet()) {
            YParameter parameter = parameters.get(iParameter);
            Individual iDecomposition = getDecompositionIndividual(
                    parameter.getParentDecomposition());
            if (iDecomposition != null) {
                iDecomposition.addProperty(property, iParameter);
            }
        }
    }


    private void addConditionObjectProperties() {
        addConditionObjectProperties(inputConditionIndividuals, hasInputCondition);
        addConditionObjectProperties(outputConditionIndividuals, hasOutputCondition);
    }

    /**
     * adds the Object Property relation hasOutputCondition to all nets and their respective OutputConditions
     */
    private void addConditionObjectProperties(Map<Individual, ? extends YCondition> map,
                                              ObjectProperty property) {
        for (Individual iCondition : map.keySet()) {
            YCondition condition = map.get(iCondition);
            Individual iNet = getDecompositionIndividual(condition.getNet());
            if (iNet != null) {
                iNet.addProperty(property, iCondition);
            }
        }
    }

    /**
     * adds the Object Property relation hasDecomposition to the given specification
     */
    private void addHasDecompositionObjectProperties() {
        for (Individual i : decompositionIndividuals.keySet()) {
            specificationIndividual.addProperty(hasDecomposition, i);
        }
        for (Individual i : netIndividuals.keySet()) {
            specificationIndividual.addProperty(hasDecomposition, i);
        }
    }

    private void addHasExpressionObjectProperties() {
        addHasExpressionObjectProperties(startingMappingIndividuals);
        addHasExpressionObjectProperties(completedMappingIndividuals);
    }

    /**
     * adds the Object Property relation hasExpression to the mappings and their respective expressions
     */
    private void addHasExpressionObjectProperties(Map<Individual, Mapping> map) {
        for (Individual iMapping : map.keySet()) {
            Mapping mapping = map.get(iMapping);
            for (Individual iExpression : expressionIndividuals.keySet()) {
                if (mapping.getExpression().equals(expressionIndividuals.get(iExpression))) {
                    iMapping.addProperty(hasExpression, iExpression);
                }
            }
        }
    }

    /**
     * adds the Object Property relation refersTo to the expressions and their respective variables
     */
    private void addRefersToObjectProperties() {
        for (Individual iExpression : expressionIndividuals.keySet()) {
            Expression expression = expressionIndividuals.get(iExpression);
            for (YVariable variable : variableToIndividualLookup.keySet()) {
                if (expression.refersToVariable(variable)) {
                    iExpression.addProperty(refersTo,
                            variableToIndividualLookup.get(variable));
                }
            }
        }
    }


    /**
     * adds the Object Property relation hasRootnet to the specification
     */
    private void addHasRootnetToObjectProperties(SpecificationParser specificationParser) {
        specificationIndividual.addProperty(hasRootNet, getNetIndividual(
                specificationParser.getRootNet()));
    }


    /**
     * adds the Object Property relation decomposes to all tasks and their respective decompositions
     */
    private void addDecomposesToObjectProperties() {
        for (Individual i : taskIndividuals.keySet()) {
            Individual iDecomposition = getDecompositionIndividual(
                    taskIndividuals.get(i).getDecompositionPrototype());
            if (iDecomposition != null) {
                i.addProperty(decomposesTo, iDecomposition);
            }
        }
    }


    private Individual getDecompositionIndividual(YDecomposition decomposition) {
        if (decomposition != null) {
            for (Individual i : decompositionIndividuals.keySet()) {
                if (decompositionIndividuals.get(i).equals(decomposition)) {
                    return i;
                }
            }
            return getNetIndividual((YNet) decomposition);
        }
        return null;
    }


    private Individual getNetIndividual(YNet net) {
        if (net != null) {
            for (Individual i : netIndividuals.keySet()) {
                if (netIndividuals.get(i).equals(net)) {
                    return i;
                }
            }
        }
        return null;
    }


    private Individual getTaskIndividual(YTask task) {
        for (Individual iTask : taskIndividuals.keySet()) {
            YTask taskValue = taskIndividuals.get(iTask);
            if (taskValue.equals(task)) {
                return iTask;
            }
        }
        return null;
    }


    private Individual getTaskIndividual(String taskID) {
        for (Individual iTask : taskIndividuals.keySet()) {
            YTask taskValue = taskIndividuals.get(iTask);
            if (taskValue.getID().equals(taskID)) {
                return iTask;
            }
        }
        return null;
    }


    /**
     * adds the Object Property relation flowsInto to the tasks
     */
    private void addFlowsIntoObjectProperties(SpecificationParser specificationParser) {
        Map<YExternalNetElement, Set<YExternalNetElement>> flowsMap =
                specificationParser.getFlowRelations();
        for (YExternalNetElement element : flowsMap.keySet()) {
            Individual i = netElementToIndividualLookup.get(element);
            if (i != null) {
                for (YExternalNetElement postElement : flowsMap.get(element)) {
                    Individual iPost = netElementToIndividualLookup.get(postElement);
                    if (iPost != null) {
                        i.addProperty(flowsInto, iPost);
                    }
                }
            }
        }
    }


    private void addHasExternalNetElementObjectProperties() {
        for (Individual iNet : netIndividuals.keySet()) {
            YNet yNet = netIndividuals.get(iNet);
            for (YExternalNetElement element : yNet.getNetElements().values()) {
                Individual i = netElementToIndividualLookup.get(element);
                if (i != null) {
                    iNet.addProperty(hasExternalNetElement, i);
                }
            }
        }
    }


    public Individual getSpecification() {
        return specificationIndividual;
    }


    private void generateNetElementToIndividualLookupMap() {
        netElementToIndividualLookup = new HashMap<YExternalNetElement, Individual>();
        buildNetElementToIndividualLookupMap(taskIndividuals);
        buildNetElementToIndividualLookupMap(inputConditionIndividuals);
        buildNetElementToIndividualLookupMap(outputConditionIndividuals);
        buildNetElementToIndividualLookupMap(conditionIndividuals);
    }


    private void buildNetElementToIndividualLookupMap(
            Map<Individual, ? extends YExternalNetElement> map) {
        for (Individual i : map.keySet()) {
            netElementToIndividualLookup.put(map.get(i), i);
        }
    }


    private void generateVariableToIndividualLookupMap() {
        variableToIndividualLookup = new HashMap<YVariable, Individual>();
        buildVariableToIndividualLookupMap(inputParameterIndividuals);
        buildVariableToIndividualLookupMap(outputParameterIndividuals);
        buildVariableToIndividualLookupMap(ioParameterIndividuals);
        buildVariableToIndividualLookupMap(localVariableIndividuals);
    }


    private void buildVariableToIndividualLookupMap(
            Map<Individual, ? extends YVariable> map) {
        for (Individual i : map.keySet()) {
            variableToIndividualLookup.put(map.get(i), i);
        }
    }

}
