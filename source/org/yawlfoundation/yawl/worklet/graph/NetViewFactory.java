package org.yawlfoundation.yawl.worklet.graph;

import org.jgraph.graph.CellView;
import org.jgraph.graph.DefaultCellViewFactory;
import org.jgraph.graph.GraphModel;

/**
 * @author Michael Adams
 * @date 1/02/2016
 */
public class NetViewFactory extends DefaultCellViewFactory {

    public CellView createView(GraphModel model, Object cell) {
        if (cell instanceof PrimitiveCell) {
            return new PrimitiveCellView(cell);
        }
        if (cell instanceof TerminalCell) {
            return new TerminalCellView(cell);
        }
        return super.createView(model, cell);
    }

}
