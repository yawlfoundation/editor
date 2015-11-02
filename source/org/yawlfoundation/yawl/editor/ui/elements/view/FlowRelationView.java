package org.yawlfoundation.yawl.editor.ui.elements.view;

import org.jgraph.JGraph;
import org.jgraph.graph.CellView;
import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.EdgeRenderer;
import org.jgraph.graph.EdgeView;
import org.yawlfoundation.yawl.editor.core.util.FileUtil;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * @author Michael Adams
 * @date 2/11/2015
 */
public class FlowRelationView extends EdgeView {

    private static final FlowRelationRenderer renderer = new FlowRelationRenderer();

    public FlowRelationView(Object cell) { super(cell); }

    public CellViewRenderer getRenderer() { return renderer; }
}


class FlowRelationRenderer extends EdgeRenderer {

    private static final boolean isOSX = FileUtil.isMac();

   	@Override
    public boolean intersects(JGraph graph, CellView value, Rectangle rect) {

        // super method works for all OSes when value is a single line
        boolean isIntersect = super.intersects(graph, value, rect);

        // super method works on multipoint line for all OSes except OSX
        if (!isIntersect && isOSX && value instanceof EdgeView && graph != null &&
                ((EdgeView) value).getPointCount() > 2) {

            // so for OSX, check each line segment (pair of points) for intersection
            EdgeView edgeView = (EdgeView) value;
            rect.grow(10, 10);                        // widen tolerance
            for (int i = 0; i < edgeView.getPointCount() - 1; i += 2) {
                if (intersects(edgeView, rect, i)) {
                    return true;
                }
            }
        }
        return isIntersect;
    }


    boolean intersects(EdgeView edgeView, Rectangle rect, int index) {
        Point2D p0 = edgeView.getPoint(index);
    	Point2D p1 = edgeView.getPoint(++index);
    	return rect.intersectsLine(p0.getX(), p0.getY(), p1.getX(), p1.getY());
    }

}
