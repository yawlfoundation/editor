package org.yawlfoundation.yawl.worklet.graph;

import org.jgraph.graph.VertexRenderer;
import org.yawlfoundation.yawl.worklet.exception.ExletTarget;
import org.yawlfoundation.yawl.worklet.rdr.RdrPrimitive;

import javax.swing.*;
import java.awt.*;

/**
 * @author Michael Adams
 * @date 1/02/2016
 */
class PrimitiveCellRenderer extends VertexRenderer {

    private static final Dimension SIZE = new Dimension(32,32);
    private static final Color FILL_COLOUR = new Color(220, 220, 220);
    private static final Color SELECTION_COLOUR = new Color(160, 25, 25);


    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(FILL_COLOUR);
        g2.fillRect(0, 0, SIZE.width, SIZE.height);
        setBorder(null);
        setOpaque(false);
        drawIcon(g2);
        if (selected) {
            g2.setColor(SELECTION_COLOUR);
            g2.setStroke(new BasicStroke(4));
            g2.drawRect(0, 0, SIZE.width-1, SIZE.height-1);
        }
        else {
            g2.setColor(super.getForeground());
            g2.drawRect(0, 0, SIZE.width-1, SIZE.height-1);
        }
    }


    protected void drawIcon(Graphics graphics) {
        PrimitiveCell cell = (PrimitiveCell) view.getCell();
        RdrPrimitive primitive = cell.getPrimitive();
        if (primitive != null) {
            Icon icon = IconLoader.getIcon(getIconName(primitive));
            if (icon != null) {
                icon.paintIcon(null, graphics, 2, 2);
            }
        }
    }


    private String getIconName(RdrPrimitive primitive) {
        switch (primitive.getExletAction()) {
            case Compensate: return "Compensate";
            case Continue: return "Continue" + getIconSuffix(primitive.getExletTarget());
            case Complete: return "ForceComplete";
            case Fail: return "ForceFail";
            case Remove: return "Remove" + getIconSuffix(primitive.getExletTarget());
            case Restart: return "Restart";
            case Suspend: return "Suspend" + getIconSuffix(primitive.getExletTarget());
            default: return null;
        }
    }


    private String getIconSuffix(ExletTarget target) {
        switch (target) {
            case Case: return "All";
            case AllCases:
            case AncestorCases: return "AllCases";
            default: return "";  // work items
        }
    }

}
