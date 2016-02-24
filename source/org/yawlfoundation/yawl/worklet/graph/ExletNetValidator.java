package org.yawlfoundation.yawl.worklet.graph;

import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultPort;
import org.yawlfoundation.yawl.worklet.client.WorkletClient;
import org.yawlfoundation.yawl.worklet.exception.ExletValidationError;
import org.yawlfoundation.yawl.worklet.exception.ExletValidator;
import org.yawlfoundation.yawl.worklet.rdr.RdrConclusion;
import org.yawlfoundation.yawl.worklet.rdr.RdrPrimitive;
import org.yawlfoundation.yawl.worklet.rdr.RuleType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Michael Adams
 * @date 11/02/2016
 */
public class ExletNetValidator {

    private final ExletValidator _exletValidator = new ExletValidator();
    private final RuleType _ruleType;

    public ExletNetValidator(RuleType ruleType) { _ruleType = ruleType; }


    public String validate(List<AbstractNetCell> cells) {
        if (! reachable(cells)) {
            return "End not reachable from start";
        }
        if (anyDetached(cells)) {
            return "Not all actions are on a path from start to end";
        }

        RdrConclusion conclusion = new RdrConclusion();
        conclusion.setPrimitives(getPrimitiveList(cells));
        List<ExletValidationError> errors = _exletValidator.validate(_ruleType, conclusion,
                getWorkletList());
        return errors.isEmpty() ? "ok" : errors.get(0).getMessage();
    }


    private boolean reachable(List<AbstractNetCell> vertices) {
        AbstractNetCell current = getStartTerminal(vertices);
        while (! (current == null || isEndTerminal(current))) {
            current = getNext(current);
        }
        return current != null;   // end reached
    }


    private TerminalCell getStartTerminal(List<AbstractNetCell> cells) {
        for (AbstractNetCell cell : cells) {
            if (isStartTerminal(cell)) {
                return (TerminalCell) cell;
            }
        }
        return null;
    }


    private AbstractNetCell getNext(AbstractNetCell cell) {
        DefaultEdge outEdge = cell.getOutgoingEdge();
        if (outEdge != null) {
            DefaultPort port = (DefaultPort) outEdge.getTarget();
            if (port != null) {
                return (AbstractNetCell) port.getParent();
            }
        }
        return null;
    }


    private boolean anyDetached(List<AbstractNetCell> cells) {
        for (AbstractNetCell cell : cells) {
            if (isDetached(cell)) {
                return true;
            }
        }
        return false;
    }


    // pre: end is reachable from start
    private boolean isDetached(AbstractNetCell cell) {
        return cell instanceof PrimitiveCell && (cell.canBeSource() || cell.canBeTarget());
    }


    private boolean isStartTerminal(AbstractNetCell cell) {
        return (cell instanceof TerminalCell) && ((TerminalCell) cell).isStart();
    }


    private boolean isEndTerminal(AbstractNetCell cell) {
        return (cell instanceof TerminalCell) && ! ((TerminalCell) cell).isStart();
    }


    // pre: start terminal exists, end is reachable
    protected List<RdrPrimitive> getPrimitiveList(List<AbstractNetCell> vertices) {
        List<RdrPrimitive> primitiveList = new ArrayList<RdrPrimitive>();
        TerminalCell start = getStartTerminal(vertices);
        AbstractNetCell current = getNext(start);
        int index = 1;
        while (! (current == null || isEndTerminal(current))) {
            PrimitiveCell primitiveCell = (PrimitiveCell) current;
            RdrPrimitive primitive = primitiveCell.getPrimitive();
            primitive.setIndex(index++);
            primitiveList.add(primitive);
            current = getNext(current);
        }
        return primitiveList;
    }


    private Set<String> getWorkletList() {
        return WorkletClient.getInstance().getWorkletCache().getKeySet();
    }

}
