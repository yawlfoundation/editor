package org.yawlfoundation.yawl.worklet.graph;

import org.jgraph.JGraph;
import org.jgraph.graph.GraphModel;

import javax.swing.*;
import java.awt.event.MouseEvent;

/**
 * @author Michael Adams
 * @date 18/02/2016
 */
public class ExletGraph extends JGraph {

    public ExletGraph() { super(); }

    public ExletGraph(GraphModel model) {
        super(model);
        ToolTipManager.sharedInstance().registerComponent(this);
    }


    public String getToolTipText(MouseEvent event) {
        Object cell = getFirstCellForLocation(event.getX(), event.getY());
        return (cell instanceof AbstractNetCell) ?
                ((AbstractNetCell) cell).getToolTipText() : null;
    }
}
