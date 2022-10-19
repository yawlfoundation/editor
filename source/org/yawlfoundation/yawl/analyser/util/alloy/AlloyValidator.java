package org.yawlfoundation.yawl.analyser.util.alloy;

import edu.mit.csail.sdg.alloy4.A4Reporter;
import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4.ErrorAPI;
import edu.mit.csail.sdg.alloy4.XMLNode;
import edu.mit.csail.sdg.alloy4compiler.ast.Command;
import edu.mit.csail.sdg.alloy4compiler.ast.Expr;
import edu.mit.csail.sdg.alloy4compiler.ast.Module;
import edu.mit.csail.sdg.alloy4compiler.parser.CompUtil;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Options;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Solution;
import edu.mit.csail.sdg.alloy4compiler.translator.A4SolutionReader;
import edu.mit.csail.sdg.alloy4compiler.translator.TranslateAlloyToKodkod;
import org.yawlfoundation.yawl.analyser.util.alloy.descriptors.TranslationGenerator;
import org.yawlfoundation.yawl.elements.YNet;
import org.yawlfoundation.yawl.elements.YTask;
import org.yawlfoundation.yawl.util.StringUtil;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class AlloyValidator {
    private final TranslationGenerator alloyGenerator;
    private final YNet _net;

    public AlloyValidator(YNet workflow) {
        this._net = workflow;
        this.alloyGenerator = new TranslationGenerator(workflow);
    }

    public String checkOrJoinInLoop() throws Exception {
        System.out.println("check for Or-Joins in loop");
        String alloySpecification = this.alloyGenerator.generate();
        String msg = checkForCycles(alloySpecification);
        if (msg.length() == 0) {
            msg = formatXMLMessage("The net " + this._net.getID() +
                    " has no OR-joins in a cycle.", true);
        }
        return msg;
    }

    public String checkForCycles(String alloySpec) throws Exception {
        Set<YTask> ORJoins = getOrJoins();
        StringBuilder resultBuilder = new StringBuilder();
        for (YTask task : ORJoins) {
            String specBuilder = alloySpec + String.format("""
                    assert no_or_join_in_loop {
                    \tno t: task | t.join = "Or" && t.label = "%s" && t in t.^(flowsInto.nextTask)
                    }
                                    
                    check no_or_join_in_loop for %d
                    """, task.getName(), this._net.getNetTasks().size());
            resultBuilder.append(checkOrJoinForTask(specBuilder, task.getName()));
        }
        return resultBuilder.toString();
    }

    private String checkOrJoinForTask(String alloySpec, String taskLabel) throws IOException, Err {
        String outputFilename = "E:/CE/Master/Thesis/saj76/YAWL_editor/output.xml";
        String outputFilename1 = "E:/CE/Master/Thesis/saj76/YAWL_editor/output1.xml";
        String tempFilename = "E:/CE/Master/Thesis/saj76/YAWL_editor/temp.als";
        A4Reporter rep = new A4Reporter();
        File tmpAls = CompUtil.flushModelToFile(alloySpec, null);
        {
            Module world = CompUtil.parseEverything_fromString(rep, alloySpec);
            A4Options opt = new A4Options();
            opt.originalFilename = tmpAls.getAbsolutePath();
            opt.solver = A4Options.SatSolver.SAT4J;
            Command cmd = world.getAllCommands().get(world.getAllCommands().size() - 1);
            try {
                A4Solution sol = TranslateAlloyToKodkod.execute_command(rep, world.getAllReachableSigs(), cmd, opt);
                sol.writeXML(outputFilename);
                assert sol.satisfiable();
                {
                    XMLNode xmlNode = new XMLNode(new File(outputFilename));
                    String alloySourceFilename = xmlNode.iterator().next().getAttribute("filename");
                    Module ansWorld = CompUtil.parseEverything_fromFile(rep, null, alloySourceFilename);
                    A4Solution ans = A4SolutionReader.read(ansWorld.getAllReachableSigs(), xmlNode);

                    Expr e = CompUtil.parseOneExpression_fromString(ansWorld, "univ");
                    System.out.println(ans.eval(e));
                    e = CompUtil.parseOneExpression_fromString(ansWorld, "Point");
                    System.out.println(ans.eval(e));
                }
                return "";
            } catch (ErrorAPI e) {
                return formatXMLMessage(String.format("Task %s is an Or-Join and it is in loop!", taskLabel), false);
            } catch (Exception e) {
                return formatXMLMessage(String.format("Task %s is an Or-Join and it is in loop!", taskLabel), false);
            }

            // eval with existing A4Solution
        }
    }

    private Set<YTask> getOrJoins() {
        Set<YTask> ORJoins = new HashSet<YTask>();
        for (YTask task : this._net.getNetTasks()) {
            if (task.getJoinType() == YTask._OR) ORJoins.add(task);
        }
        return ORJoins;
    }


    /**
     * used for formatting xml messages.
     * Message could be a warning or observation.
     */
    private String formatXMLMessage(String msg, boolean is_observation) {
        return StringUtil.wrap(msg, is_observation ? "observation" : "warning");
    }
}