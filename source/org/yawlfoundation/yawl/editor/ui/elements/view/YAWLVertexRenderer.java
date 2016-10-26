/*
 * Copyright (c) 2004-2013 The YAWL Foundation. All rights reserved.
 * The YAWL Foundation is a collaboration of individuals and
 * organisations who are committed to improving workflow technology.
 *
 * This file is part of YAWL. YAWL is free software: you can
 * redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation.
 *
 * YAWL is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General
 * Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with YAWL. If not, see <http://www.gnu.org/licenses/>.
 */

package org.yawlfoundation.yawl.editor.ui.elements.view;

import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.VertexRenderer;
import org.yawlfoundation.yawl.editor.ui.elements.model.AtomicTask;
import org.yawlfoundation.yawl.editor.ui.elements.model.YAWLTask;
import org.yawlfoundation.yawl.editor.ui.plugin.YPluginHandler;
import org.yawlfoundation.yawl.editor.ui.util.IconList;
import org.yawlfoundation.yawl.elements.YDecomposition;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;

abstract class YAWLVertexRenderer extends VertexRenderer {

    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        boolean tmp = selected;
        if (isOpaque()) {
            g2.setColor(super.getBackground());
            fillVertex(g2, getSize());
            g2.setColor(super.getForeground());
        }
        try {
            setBorder(null);
            setOpaque(false);
            selected = false;
            YPluginHandler.getInstance().preCellRender(g2, view);
            drawIcon(g2, getSize());
            drawVertex(g2, getSize());
        }
        finally {
            selected = tmp;
        }
        if (bordercolor != null) {
            g2.setStroke(new BasicStroke(1));
            g2.setColor(bordercolor);
            drawIcon(g2, getSize());
            drawVertex(g2, getSize());
        }
        if (selected) {
            g2.setStroke(GraphConstants.SELECTION_STROKE);
            g2.setColor(highlightColor);
            drawIcon(g2, getSize());
            drawVertex(g2, getSize());
        }

        g2.setStroke(new BasicStroke(1));
        if (view.getCell() instanceof YAWLTask) {
            YAWLTask task = (YAWLTask) view.getCell();
            if (task.hasCancellationSetMembers()) {
                drawCancelSetMarker(g2, getSize());
            }
            if (isAutomatedTask(task)) {
                Path2D.Double shape = drawAutomatedMarker(g2, getSize());
                if (hasCodelet(task)) {
                    drawCodeletMarker(g2, shape);
                }
            }
            if (task instanceof AtomicTask) {
                if (((AtomicTask) task).hasTimerEnabled()) {
                    drawTimerMarker(g2, getSize());
                }
            }
        }
    }

    protected void drawIcon(Graphics graphics, Dimension size) {
        if (view.getCell() instanceof YAWLTask) {
            YAWLTask task = (YAWLTask) view.getCell();
            String iconPath = task.getIconPath();
            if (iconPath != null) {
                Icon icon = IconList.getInstance().getIcon(iconPath);
                if (icon != null) {
                    icon.paintIcon(null, graphics,
                            getIconHorizontalOffset(size, icon),
                            getIconVerticalOffset(size,icon)
                    );
                }
            }
        }
    }

    protected int getIconHorizontalOffset(Dimension size, Icon icon) {
        return (size.width - icon.getIconWidth())/2;
    }

    protected int getIconVerticalOffset(Dimension size, Icon icon) {
        return (size.height - icon.getIconHeight())/2;
    }


    protected void drawCancelSetMarker(Graphics2D graphics, Dimension size) {
        Ellipse2D.Double circle = new Ellipse2D.Double(
                (3 * size.getWidth() / 4) - 2,
                1,
                size.getWidth() / 4,
                size.getHeight() / 4
        );
        graphics.setColor(Color.red);
        graphics.fill(circle);
    }

    protected void drawTimerMarker(Graphics2D g, Dimension size) {
        Ellipse2D.Double circle = new Ellipse2D.Double(1, 1,
                size.getWidth() / 4, size.getHeight() / 4);
        Line2D vHand = new Line2D.Double(
                circle.getCenterX(),
                1,
                circle.getCenterX(),
                circle.getCenterY()
        );
        Line2D hHand = new Line2D.Double(
                circle.getCenterX(),
                circle.getCenterY(),
                1 + circle.getWidth(),
                circle.getCenterY()
        );
        g.setColor(Color.white);
        g.fill(circle);
        g.setColor(Color.black);
        g.draw(circle);
        g.draw(vHand);
        g.draw(hHand);
    }

    protected Path2D.Double drawAutomatedMarker(Graphics2D g, Dimension size) {
        Path2D.Double path = getAutomatedShape(size);
        g.setColor(Color.black);
        g.draw(path);
        return path;
    }


    protected void drawCodeletMarker(Graphics2D g, Path2D.Double path) {
        g.setColor(Color.green.darker());
        g.fill(path);
    }


    private Path2D.Double getAutomatedShape(Dimension size) {
        Rectangle2D shapeRect = new Rectangle2D.Double(
                3 * size.getWidth() / 8,
                2,
                size.getWidth() / 4,
                size.getHeight() / 4 - 2
        );
        Path2D.Double path = new Path2D.Double();
        path.moveTo(shapeRect.getX(), shapeRect.getY());
        path.lineTo(shapeRect.getX(), shapeRect.getY() + shapeRect.getHeight());
        path.lineTo(shapeRect.getX() + shapeRect.getWidth(),
                shapeRect.getY() + (shapeRect.getHeight() / 2));
        path.lineTo(shapeRect.getX(), shapeRect.getY());
        path.closePath();
        return path;
    }


    private boolean isAutomatedTask(YAWLTask task) {
        YDecomposition decomp = task.getDecomposition();
        return (decomp != null) && (! decomp.requiresResourcingDecisions());
    }

    private boolean hasCodelet(YAWLTask task) {
        YDecomposition decomp = task.getDecomposition();
        return decomp != null && decomp.getCodelet() != null;
    }


    abstract protected void fillVertex(Graphics graphics, Dimension size);

    abstract protected void drawVertex(Graphics graphics, Dimension size);
}
