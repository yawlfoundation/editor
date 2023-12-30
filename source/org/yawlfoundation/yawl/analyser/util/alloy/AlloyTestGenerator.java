package org.yawlfoundation.yawl.analyser.util.alloy;

import edu.mit.csail.sdg.alloy4.A4Reporter;
import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4.XMLNode;
import edu.mit.csail.sdg.alloy4compiler.ast.Command;
import edu.mit.csail.sdg.alloy4compiler.ast.Expr;
import edu.mit.csail.sdg.alloy4compiler.ast.Module;
import edu.mit.csail.sdg.alloy4compiler.parser.CompUtil;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Options;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Solution;
import edu.mit.csail.sdg.alloy4compiler.translator.A4SolutionReader;
import edu.mit.csail.sdg.alloy4compiler.translator.TranslateAlloyToKodkod;
import org.apache.jena.base.Sys;
import org.yawlfoundation.yawl.analyser.testgeneration.RACC.exceptions.NotValidLeafClauseIdException;
import org.yawlfoundation.yawl.analyser.testgeneration.RACC.testCase.VariableAssignment;
import org.yawlfoundation.yawl.analyser.util.alloy.descriptors.InputDescriptor;
import org.yawlfoundation.yawl.analyser.util.alloy.descriptors.NotNoneSplitOutputDescriptor;
import org.yawlfoundation.yawl.analyser.util.alloy.descriptors.OutputDescriptor;
import org.yawlfoundation.yawl.analyser.util.alloy.descriptors.TranslationGenerator;
import org.yawlfoundation.yawl.elements.YNet;
import org.yawlfoundation.yawl.elements.YTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class AlloyTestGenerator {
    private final TranslationGenerator translator;
    private final YNet workflow;

    public AlloyTestGenerator(YNet workflow) {
        this.translator = new TranslationGenerator(workflow, null);
        this.workflow = workflow;
    }

    public String testWithMutations(YNet net) {
        try {
            StringBuilder resultBuilder = new StringBuilder();
            TranslationGenerator translationGenerator = new TranslationGenerator(this.workflow, null);
            String alloyDescription = translationGenerator.generateDescription();
            ArrayList<RACCTestSpecification> testDescriptions = this.addTestSpecifications();
            String alloyPredDescription = translationGenerator.generatePredDescription();
            String filePath = new File("").getAbsolutePath();
            String tempFilename = filePath.concat("/temp_mutation_test.txt");
            System.out.println(tempFilename);
            String content = new Scanner(new File(tempFilename)).useDelimiter("\\Z").next();
            System.out.println(content);
            String[] result = content.split("----------");
            for (String testSpec : result) {
                System.out.println(testSpec);
                String fullDescription = alloyDescription + "\n\n" + testSpec
                        + "\n\n" + alloyPredDescription;
                try {

                    checkAlloySpec(fullDescription);
                    resultBuilder.append("Test passed \n");
                } catch (Exception ex) {
                    resultBuilder.append("Oops! Test failed\n ");
                }
            }
            return resultBuilder.toString();
        } catch (Exception | NotValidLeafClauseIdException e) {
            e.printStackTrace();
        }

        return "";
    }

    public String test(YNet net) {
        try {
            StringBuilder resultBuilder = new StringBuilder();
            TranslationGenerator translationGenerator = new TranslationGenerator(this.workflow, null);
            String alloyDescription = translationGenerator.generateDescription();
            ArrayList<RACCTestSpecification> testDescriptions = this.addTestSpecifications();
            String alloyPredDescription = translationGenerator.generatePredDescription();
            System.out.println(testDescriptions.size());
            for (RACCTestSpecification testDescription : testDescriptions) {
                String fullDescription = alloyDescription + "\n\n" + testDescription.getTestDescription()
                        + "\n\n" + alloyPredDescription;
                try {

                    checkAlloySpec(fullDescription);
                    resultBuilder.append(String.format("Test passed with %s in flow %s -> %s!\n",
                            testDescription.getTestAssignments(), testDescription.getSourceTaskName(),
                            testDescription.getDestinationTaskName()));
                } catch (Exception ex) {
                    resultBuilder.append(String.format("Oops! Test failed with %s in flow %s -> %s!\n",
                            testDescription.getTestAssignments(), testDescription.getSourceTaskName(),
                            testDescription.getDestinationTaskName()));
                }
            }
            return resultBuilder.toString();
        } catch (Exception | NotValidLeafClauseIdException e) {
            e.printStackTrace();
        }
        return "";
    }

    private void checkAlloySpec(String alloySpec) throws IOException, Err {
        String filePath = new File("").getAbsolutePath();
        String outputFilename = filePath.concat("/output_test.xml");
//        String outputFilename1 = "H:/Projects/saj76/YAWL_editoy/output1_test.xml";
        String tempFilename = filePath.concat("/temp_test.als");
        System.out.println(tempFilename);

        A4Reporter rep = new A4Reporter();
//        System.out.println(alloySpec);
        File tmpAls = CompUtil.flushModelToFile(alloySpec, null);
        {
            FileWriter myWriter = new FileWriter(tempFilename, true);
            myWriter.write("-----------------------------------------------\n");
            myWriter.write(alloySpec);
            myWriter.write("\n-----------------------------------------------");
            myWriter.close();
//            Module world = CompUtil.parseEverything_fromString(rep, alloySpec);
//            A4Options opt = new A4Options();
//            opt.originalFilename = tmpAls.getAbsolutePath();
//            opt.solver = A4Options.SatSolver.SAT4J;
//            Command cmd = world.getAllCommands().get(world.getAllCommands().size() - 1);
//            System.out.println(cmd.label);
//            System.out.println(cmd);
//
//            A4Solution sol = TranslateAlloyToKodkod.execute_command(rep, world.getAllReachableSigs(), cmd, opt);
//            sol.writeXML(outputFilename);
//            assert sol.satisfiable();
//            {
//                XMLNode xmlNode = new XMLNode(new File(outputFilename));
//                String alloySourceFilename = xmlNode.iterator().next().getAttribute("filename");
//                Module ansWorld = CompUtil.parseEverything_fromFile(rep, null, alloySourceFilename);
//                A4Solution ans = A4SolutionReader.read(ansWorld.getAllReachableSigs(), xmlNode);
//
//                Expr e = CompUtil.parseOneExpression_fromString(ansWorld, "univ");
//                System.out.println(ans.eval(e));
//            }
        }
//        boolean isDeleted = (new File(tempFilename)).delete();
    }

    public ArrayList<RACCTestSpecification> addTestSpecifications() throws NotValidLeafClauseIdException {
        ArrayList<RACCTestSpecification> mainDescription = new ArrayList<>();
        for (int i = 0; i < this.workflow.getNetTasks().size(); i++) {
            YTask currentTask = this.workflow.getNetTasks().get(i);
            OutputDescriptor outputDescriptor = this.translator.outputDescriptorFactory(currentTask);
            InputDescriptor inputDescriptor = this.translator.inputDescriptorFactory(currentTask);
            if (outputDescriptor instanceof NotNoneSplitOutputDescriptor notNoneSplitOutputDescriptor) {
                HashMap<String, HashMap<String, HashMap<Boolean, ArrayList<VariableAssignment>>>> outputsTestCases = notNoneSplitOutputDescriptor.getTestCases();
                for (Map.Entry<String, HashMap<String, HashMap<Boolean, ArrayList<VariableAssignment>>>> outputTestCasesMap : outputsTestCases.entrySet()) {
                    String outputTaskName = outputTestCasesMap.getKey();
                    HashMap<String, HashMap<Boolean, ArrayList<VariableAssignment>>> outputTestCases = outputTestCasesMap.getValue();
                    for (Map.Entry<String, HashMap<Boolean, ArrayList<VariableAssignment>>> variableTestCasesMap : outputTestCases.entrySet()) {
                        String variableName = variableTestCasesMap.getKey();
                        HashMap<Boolean, ArrayList<VariableAssignment>> variableTestCases = variableTestCasesMap.getValue();
                        mainDescription.addAll(getTestSpecifications(variableTestCases, outputTaskName, currentTask.getName()));
                    }
                }
            }
        }
        return mainDescription;
    }

    private ArrayList<RACCTestSpecification> getTestSpecifications(HashMap<Boolean, ArrayList<VariableAssignment>> variableTestCases, String nextTaskName, String currentTaskName) {
        ArrayList<RACCTestSpecification> result = new ArrayList<>();
        for (Map.Entry<Boolean, ArrayList<VariableAssignment>> variableTestCase : variableTestCases.entrySet()) {
            ArrayList<String> assignmentStr = new ArrayList<>();
            StringBuilder resultBuilder = new StringBuilder(String.format("""
                    fact test {
                                	all t, t': task | t.label = "%s" && t'.label = "%s" => {
                                    one s: State |  t in s.token &&
                                    
                    """, currentTaskName, nextTaskName));
            ArrayList<String> assignments = new ArrayList<>();
            boolean expectedValue = variableTestCase.getKey();
            for (VariableAssignment assignment : variableTestCase.getValue()) {
                assignments.add(String.format("s.%s", assignment.toString()));
                assignmentStr.add(assignment.toString());
            }
            resultBuilder.append(String.join(" && ", assignments));
            if (assignments.size() == 0) {
                ArrayList<RACCTestSpecification> trueResult = new ArrayList<>();
                RACCTestSpecification ts = new RACCTestSpecification(currentTaskName, nextTaskName, String.format("""
                                fact test {
                                	all t, t': task | t.label = "%s" && t'.label = "%s" => {
                                    one s: State |  t in s.token && t' in s.next.token && 1 = 1}
                                }""",
                        currentTaskName, nextTaskName));
                ts.setTestAssignments("any input.");
                trueResult.add(ts);
                return trueResult;
            }
            String closure = expectedValue ? " && t' in s.next.token\n \t}\n}" : " && t' not in s.next.token\n \t}\n}";
            resultBuilder.append(closure);
            RACCTestSpecification ts = new RACCTestSpecification(currentTaskName, nextTaskName, resultBuilder.toString());
            ts.setTestAssignments(String.format("input of %s", String.join(", ", assignmentStr)));
            result.add(ts);
        }
        return result;
    }

    private String getTestDescriptions(HashMap<String, HashMap<String, ArrayList<ArrayList<String>>>> testCases) {
        return null;
    }

}
