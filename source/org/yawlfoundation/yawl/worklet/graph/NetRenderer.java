package org.yawlfoundation.yawl.worklet.graph;

import org.jgraph.JGraph;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;
import org.yawlfoundation.yawl.worklet.rdr.RdrConclusion;
import org.yawlfoundation.yawl.worklet.rdr.RdrPrimitive;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;


/**
 * @author Michael Adams
 * @date 1/02/2016
 */
public class NetRenderer {

    protected static final int EDGE_LENGTH = 32;
    protected static final int TERMINAL_EDGE_TO_SIDE_GAP = 20;

    private Dimension _frameSize;

    public NetRenderer(Dimension frameSize) {
        _frameSize = frameSize;
    }


    public void render(JGraph graph, RdrConclusion conclusion) {
        graph.getModel().beginUpdate();
        List<DefaultGraphCell> cells = createCells(conclusion);
        if (! conclusion.isNullConclusion()) {
            cells.addAll(createEdges(cells));
        }
        graph.getGraphLayoutCache().insert(cells.toArray());
        graph.getModel().endUpdate();
    }


    private List<DefaultGraphCell> createCells(RdrConclusion conclusion) {
        List<DefaultGraphCell> cells = new ArrayList<DefaultGraphCell>();
        Rectangle2D bounds = getStartBounds();
        int xGap = calcXGap(conclusion.getCount());
        cells.add(new TerminalCell(bounds, true));                          // start
        if (! conclusion.isNullConclusion()) {
            for (RdrPrimitive primitive : conclusion.getPrimitives()) {
                bounds = nextBounds(bounds, xGap);
                cells.add(new PrimitiveCell(bounds, primitive));
            }
            bounds = nextBounds(bounds, xGap);
        }
        else {
            bounds = getEndBounds();
        }
        cells.add(new TerminalCell(bounds, false));             // end
        return cells;
    }


    private List<DefaultGraphCell> createEdges(List<DefaultGraphCell> cells) {
        List<DefaultGraphCell> edges = new ArrayList<DefaultGraphCell>();
        for (int i=0; i < cells.size() -1; i++) {
            edges.add(createEdge(cells.get(i), cells.get(i+1)));
        }
        return edges;
    }


    private DefaultGraphCell createEdge(DefaultGraphCell source, DefaultGraphCell target) {
        DefaultEdge edge = new DefaultEdge();
        source.addPort();
        ((AbstractNetCell) source).setOutgoingEdge(edge);
        edge.setSource(source.getChildAt(source.getChildCount() -1));
        target.addPort();
        ((AbstractNetCell) target).setIncomingEdge(edge);
        edge.setTarget(target.getChildAt(target.getChildCount() -1));
        GraphConstants.setLineEnd(edge.getAttributes(), GraphConstants.ARROW_TECHNICAL);
        GraphConstants.setEndFill(edge.getAttributes(), true);
        return edge;
    }


    private Rectangle2D nextBounds(Rectangle2D bounds, double xInc) {
        return new Rectangle2D.Double(bounds.getX() + xInc,
                bounds.getY(), bounds.getWidth(), bounds.getHeight());
    }


    private int calcXGap(int primCount) {

        // canvas width - gap to border at both sides - width of cells (incl. terminals)
        // divided by number of gaps between cells = space of gap between each cell
        int space = (_frameSize.width - TERMINAL_EDGE_TO_SIDE_GAP * 2 -
                EDGE_LENGTH * (primCount + 2)) / (primCount + 1);
        return space + EDGE_LENGTH;
    }


    private Rectangle2D getStartBounds() {
        int centerFromTop = (_frameSize.height - EDGE_LENGTH) / 2;
        return new Rectangle2D.Double(TERMINAL_EDGE_TO_SIDE_GAP, centerFromTop,
                EDGE_LENGTH, EDGE_LENGTH);
    }


    private Rectangle2D getEndBounds() {
        int centerFromTop = (_frameSize.height - EDGE_LENGTH) / 2;
        int x = _frameSize.width - TERMINAL_EDGE_TO_SIDE_GAP - EDGE_LENGTH;
        return new Rectangle2D.Double(x, centerFromTop, EDGE_LENGTH, EDGE_LENGTH);
    }

}


