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
import org.apache.jena.base.Sys;
import org.yawlfoundation.yawl.analyser.util.alloy.descriptors.TranslationGenerator;
import org.yawlfoundation.yawl.elements.YNet;
import org.yawlfoundation.yawl.elements.YTask;
import org.yawlfoundation.yawl.util.StringUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.Set;

public class AlloyValidator {
    private final TranslationGenerator alloyGenerator;
    private final YNet _net;

    public AlloyValidator(YNet workflow) {
        this._net = workflow;
        this.alloyGenerator = new TranslationGenerator(workflow, null);
    }

    public String checkOrJoinInLoop() throws Exception {
        System.out.println("check for Or-Joins in loop");
        String msg = checkForCycles();
        if (msg.length() == 0) {
            msg = formatXMLMessage("The net " + this._net.getID() +
                    " has no OR-joins in a cycle.", true);
        }
        return msg;
    }

    public String areAllTasksReachable() throws Exception {
        System.out.println("check whether all tasks are reachable or not!");
        String alloySpecification = this.alloyGenerator.generateDescription();
        String msg = checkAreAllTasksReachable(alloySpecification);
        if (msg.length() == 0) {
            msg = formatXMLMessage("In the net " + this._net.getID() +
                    " all tasks are reachable .", true);
        }
        return msg;
    }

    private String checkAreAllTasksReachable(String alloySpecification) throws Exception {
        StringBuilder resultBuilder = new StringBuilder();
        System.out.println(alloySpecification);
        for (YTask task : this._net.getNetTasks()) {
            String specBuilder = alloySpecification + String.format("""
                    assert is_any_state_task_is_token_in_it {
                     	all t: task | t.label = "%s" => {
                     		all s: State | t not in s.token
                     	}
                     }
                                    
                    check is_any_state_task_is_token_in_it for %d
                    """, task.getName(), this._getAssertionSize());
            try {
                checkAlloySpec(specBuilder);
            } catch (ErrorAPI e) {
                resultBuilder.append(formatXMLMessage(String.format("Task %s is not reachable!", task.getName()),
                        false));
            } catch (Exception e) {
                resultBuilder.append(formatXMLMessage(String.format("Task %s is not reachable!", task.getName()),
                        false));
            }

        }
        return resultBuilder.toString();
    }

    private int _getAssertionSize() {
        int count = 0;
        for (YTask task : this._net.getNetTasks()) {
            count += task.getPostsetElements().size();
        }
        count += this._net.getInputCondition().getPostsetElements().size();
        return count;
    }

    public String checkForCycles() throws Exception {
        Set<YTask> ORJoins = getOrJoins();
        StringBuilder resultBuilder = new StringBuilder();
        for (YTask task : ORJoins) {
            TranslationGenerator alloyGenerator = new TranslationGenerator(this._net, task.getName());
            String alloySpecification = alloyGenerator.generateDescription();
            System.out.println("-------------------------------------");
            System.out.println(alloySpecification);
            System.out.println("-------------------------------------");
            String specBuilder = alloySpecification + String.format("""
                    assert no_or_join_in_loop {
                    \tall t: task | t.label = "%s" => t not in t.^(flowsInto.nextTask)
                    }
                                    
                    check no_or_join_in_loop for %d
                    """, task.getName(), this._getAssertionSize());
            try {
                checkAlloySpec(specBuilder);
                resultBuilder.append(formatXMLMessage(String.format("Task %s is an Or-Join and it is in loop!",
                        task.getName()), false));
            } catch (ErrorAPI ex) {
                System.out.println(ex.msg);
            } catch (Exception ex) {
                System.out.println(ex.toString());
            }

        }
        return resultBuilder.toString();
    }

    private void checkAlloySpec(String alloySpec) throws IOException, Err {
        String outputFilename = "H:/Projects/saj76/YAWL_editor/output.xml";
        String outputFilename1 = "H:/Projects/saj76/YAWL_editor/output1.xml";
        String tempFilename = "H:/Projects/saj76/YAWL_editor/temp.als";
        A4Reporter rep = new A4Reporter();
//        System.out.println(alloySpec);
        File tmpAls = CompUtil.flushModelToFile(alloySpec, null);
        {
            FileWriter myWriter = new FileWriter(tempFilename, true);
            myWriter.write("-----------------------------------------------\n");
            myWriter.write(alloySpec);
            myWriter.write("\n-----------------------------------------------");
            myWriter.close();
            Module world = CompUtil.parseEverything_fromString(rep, alloySpec);
            A4Options opt = new A4Options();
            opt.originalFilename = tmpAls.getAbsolutePath();
            opt.solver = A4Options.SatSolver.SAT4J;
            Command cmd = world.getAllCommands().get(world.getAllCommands().size() - 1);
            System.out.println(cmd.label);
            System.out.println(cmd);

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
            }
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