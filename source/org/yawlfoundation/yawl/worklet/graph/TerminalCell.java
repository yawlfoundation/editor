package org.yawlfoundation.yawl.worklet.graph;

import java.awt.geom.Rectangle2D;

/**
 * @author Michael Adams
 * @date 1/02/2016
 */
public class TerminalCell extends AbstractNetCell {

    private final boolean _isStart;

    public TerminalCell(Rectangle2D bounds, boolean isStart) {
        super(bounds);
        _isStart = isStart;
    }


    public boolean isStart() { return _isStart; }


    public boolean canBeSource() { return _isStart && super.canBeSource(); }

    public boolean canBeTarget() { return ! _isStart && super.canBeTarget(); }

    public boolean canBeRemoved() { return false; }


    public String getInnerToolTipText() {
        return _isStart ? "Start" : "End";
    }


    public String toString() {
        return "[" + this.hashCode() + "]: " +  getInnerToolTipText();
    }

}
