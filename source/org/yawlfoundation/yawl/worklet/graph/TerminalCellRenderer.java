package org.yawlfoundation.yawl.worklet.graph;

import org.jgraph.graph.VertexRenderer;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * @author Michael Adams
 * @date 1/02/2016
 */
public class TerminalCellRenderer extends VertexRenderer {

    private static final Dimension SIZE = new Dimension(32,32);

    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        TerminalCell cell = (TerminalCell) view.getCell();
        Point2D point = cell.getLocation();
        if (isOpaque()) {
            g2.setColor(super.getBackground());
            g2.fillOval(0,0, SIZE.width, SIZE.height);
            g2.setColor(super.getForeground());
        }
        setBorder(null);
        setOpaque(false);
        selected = false;
        if (cell.isStart()) {
            drawStart(g2, point, SIZE);
        }
        else {
            drawEnd(g2, point, SIZE);
        }
    }


    protected void drawStart(Graphics2D g, Point2D point, Dimension size) {
        Polygon startArrow = new Polygon();
        startArrow.addPoint(Math.round(size.width/3),
                Math.round(size.height/4));

        startArrow.addPoint(Math.round(size.width/3),
                Math.round((size.height/4)*3));

        startArrow.addPoint(Math.round((size.width/4)*3),
                Math.round(size.height/2));
        g.setColor(Color.GREEN.darker());
        g.fillPolygon(startArrow);

        g.setColor(Color.black);
        g.drawPolygon(startArrow);
        g.drawOval(0, 0, size.width - 1, size.height - 1);
    }


    protected void drawEnd(Graphics2D g, Point2D point, Dimension size) {
        g.setColor(Color.RED);
        g.fillRect(Math.round(size.width/4),
                        Math.round(size.height/4),
                Math.round(size.width/2),
                Math.round(size.height/2));
        g.setColor(Color.black);
        g.drawRect(Math.round(size.width/4),
                        Math.round(size.height/4),
                Math.round(size.width/2 - 1),
                Math.round(size.height/2 - 1));
        g.drawOval(0,0, size.width - 1, size.height - 1);
    }

}
