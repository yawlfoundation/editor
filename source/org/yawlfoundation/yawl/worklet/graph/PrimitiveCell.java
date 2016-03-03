package org.yawlfoundation.yawl.worklet.graph;

import org.yawlfoundation.yawl.worklet.exception.ExletAction;
import org.yawlfoundation.yawl.worklet.exception.ExletTarget;
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

    public ExletTarget getTarget() { return _primitive.getExletTarget(); }


    public boolean isCompensator() {
        return getPrimitive().getExletAction() == ExletAction.Compensate;
    }


    // toggle ancestor cases <--> all cases (other targets ignored)
    public void setAncestorCasesTarget(boolean isAncestorCases) {
        if (isAncestorCases && getTarget() == ExletTarget.AllCases) {
            _primitive.setExletTarget(ExletTarget.AncestorCases);
        }
        else if (!isAncestorCases && getTarget() == ExletTarget.AncestorCases) {
            _primitive.setExletTarget(ExletTarget.AllCases);
        }
    }


    public boolean shouldShowAncestorPopup() {
        switch (getTarget()) {
            case AllCases:
            case AncestorCases: return true;
            default: return false;
        }
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
