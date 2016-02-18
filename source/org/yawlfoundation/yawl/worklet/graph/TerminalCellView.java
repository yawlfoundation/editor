package org.yawlfoundation.yawl.worklet.graph;

import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.VertexView;

/**
 * @author Michael Adams
 * @date 1/02/2016
 */
public class TerminalCellView extends VertexView {

    private static final TerminalCellRenderer renderer = new TerminalCellRenderer();

    public TerminalCellView(Object vertex) {
        super(vertex);
    }

    public CellViewRenderer getRenderer() {
        return renderer;
    }

}
