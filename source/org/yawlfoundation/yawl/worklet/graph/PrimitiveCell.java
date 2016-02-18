package org.yawlfoundation.yawl.worklet.graph;

import org.yawlfoundation.yawl.worklet.rdr.RdrPrimitive;

import java.awt.geom.Rectangle2D;

/**
 * @author Michael Adams
 * @date 1/02/2016
 */
public class PrimitiveCell extends AbstractNetCell {


    private final RdrPrimitive _primitive;


    public PrimitiveCell(Rectangle2D bounds, RdrPrimitive primitive) {
        super(bounds);
        _primitive = primitive;
    }


    public RdrPrimitive getPrimitive() { return _primitive; }


    public boolean isCompensator() {
        return getPrimitive().getAction().equals("compensate");
    }


    public boolean canBeRemoved() { return true; }


    public String getInnerToolTipText() {
        StringBuilder s = new StringBuilder();
        s.append("&nbsp;<b>Action:</b> ");
        s.append(_primitive.getAction());
        s.append("<br> ");
        s.append("&nbsp;<b>Target:</b> ");
        s.append(_primitive.getTarget());
        s.append("&nbsp;<p>");
        return s.toString();
    }


    public String toString() {
        return "[" + this.hashCode() + "]: " + _primitive.toString();
    }

}
