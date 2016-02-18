package org.yawlfoundation.yawl.worklet.graph;

import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * @author Michael Adams
 * @date 1/02/2016
 */
public abstract class AbstractNetCell extends DefaultGraphCell {

    protected DefaultEdge _inEdge;
    protected DefaultEdge _outEdge;

    protected AbstractNetCell(Rectangle2D bounds) {
        super();
        setBounds(bounds);
        GraphConstants.setSizeable(getAttributes(), false);
        GraphConstants.setSelectable(getAttributes(), true);
    }


    protected Point2D getLocation() {
        Rectangle2D bounds = getBounds();
        return new Point2D.Double(bounds.getX(), bounds.getY());
    }


    public Point2D getCentre() {
        Rectangle2D bounds = getBounds();
        return new Point2D.Double(bounds.getCenterX(), bounds.getCenterY());
    }


    public Rectangle2D getBounds() {
        return GraphConstants.getBounds(getAttributes());
    }


    public void setBounds(Rectangle2D bounds) {
        GraphConstants.setBounds(getAttributes(), bounds);
    }


    protected void setIncomingEdge(DefaultEdge edge) { _inEdge = edge; }

    protected DefaultEdge getIncomingEdge() { return _inEdge; }


    protected void setOutgoingEdge(DefaultEdge edge) { _outEdge = edge; }

    protected DefaultEdge getOutgoingEdge() { return _outEdge; }


    public void removeEdges() {
        setIncomingEdge(null);
        setOutgoingEdge(null);
    }


    public String getToolTipText() {
        return "<html><body>" + getInnerToolTipText() + "</body></html>";
    }


    public boolean canBeSource() { return getOutgoingEdge() == null; }

    public boolean canBeTarget() { return getIncomingEdge() == null; }


    public abstract boolean canBeRemoved() ;

    protected abstract String getInnerToolTipText();

}
