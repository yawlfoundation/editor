package org.yawlfoundation.yawl.worklet.graph;

import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.VertexView;

/**
 * @author Michael Adams
 * @date 1/02/2016
 */
public class PrimitiveCellView extends VertexView {

    private static final PrimitiveCellRenderer renderer = new PrimitiveCellRenderer();

    public PrimitiveCellView(Object vertex) {
        super(vertex);
    }

    public CellViewRenderer getRenderer() {
        return renderer;
    }

}
