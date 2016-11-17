package org.yawlfoundation.yawl.views.resource;

import org.yawlfoundation.yawl.editor.ui.elements.model.YAWLAtomicTask;
import org.yawlfoundation.yawl.editor.ui.elements.model.YAWLTask;
import org.yawlfoundation.yawl.editor.ui.net.NetGraph;
import org.yawlfoundation.yawl.views.ontology.Triple;
import org.yawlfoundation.yawl.views.ontology.OntologyHandler;
import org.yawlfoundation.yawl.views.ontology.OntologyQueryException;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * @author Michael Adams
 * @date 28/10/2016
 */
public class ResourceConstraintsOverlay {

    private static final String ICON_PATH = "source/org/yawlfoundation/yawl/views/icons/";
    private static final String FAM_TASK_ICON_NAME = "famTask.png";
    private static final String FOUR_EYES_ICON_NAME = "fourEyes.png";
    private NetGraph _graph;
    private Map<String, YAWLAtomicTask> _taskMap;            // [taskID, task]
    private Set<Triple> _familiarTaskSet;
    private Set<Triple> _fourEyesTaskSet;
    private Icon _famTaskIcon;
    private Icon _fourEyesIcon;


    public ResourceConstraintsOverlay(NetGraph graph, Map<String, YAWLAtomicTask> taskMap) {
        _graph = graph;
        _taskMap = taskMap;
        _familiarTaskSet = getFamiliarTasks(_graph);
        _fourEyesTaskSet = getFourEyesTasks(_graph);
    }


    public void paint(Graphics g) {
        if (hasConstraints()) overlayConstraints((Graphics2D) g, _graph.getScale());
    }


    private boolean hasConstraints() {
        return !(_familiarTaskSet.isEmpty() && _fourEyesTaskSet.isEmpty());
    }


    private void overlayConstraints(Graphics2D g, double scale) {
        Graphics2D gCopy = (Graphics2D) g.create();
        gCopy.setStroke(new BasicStroke(3 * (float) scale, BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND, 0, new float[] { 5 * (float) scale }, 0));

        gCopy.setColor(Color.GREEN.darker());
        overlayConstraints(gCopy, _familiarTaskSet, scale, FAM_TASK_ICON_NAME);

        gCopy.setColor(Color.RED.darker());
        overlayConstraints(gCopy, _fourEyesTaskSet, scale, FOUR_EYES_ICON_NAME);

        gCopy.dispose();
    }


    private Rectangle2D getTaskBounds(String taskID) {
        YAWLTask task = (YAWLTask) _taskMap.get(taskID);
        return task != null ? task.getBounds() : null;
    }


    private Point2D[] getPathPoints(Rectangle2D T1, Rectangle2D T2, double scale) {
        Point2D.Double p1, p2, pb;
        if (T1.getMaxX() <= T2.getX()) {
            if (T2.getY() <= T1.getMaxY()) {
                // b, a
                p1 = new Point2D.Double(T1.getMaxX(), T1.getY());
                p2 = new Point2D.Double(T2.getX(), T2.getY());
                pb = new Point2D.Double((p1.getX() + p2.getX()) / 2,
                        (p1.getY() + p2.getY()) / 2 - (Math.abs(p2.getX() - p1.getX()) / 4));
            }
            else {
                // d, c
                p1 = new Point2D.Double(T1.getMaxX(), T1.getMaxY());
                p2 = new Point2D.Double(T2.getX(), T2.getMaxY());
                pb = new Point2D.Double((p1.getX() + p2.getX()) / 2,
                        (p1.getY() + p2.getY()) / 2 + (Math.abs(p2.getX() - p1.getX()) / 4));
            }
        }
        else {
            if (T2.getY() <= T1.getMaxY()) {
                // a, c
                p1 = new Point2D.Double(T1.getX(), T1.getY());
                p2 = new Point2D.Double(T2.getX(), T2.getMaxY());
            }
            else {
                // c, a
                p1 = new Point2D.Double(T1.getX(), T1.getMaxY());
                p2 = new Point2D.Double(T2.getX(), T2.getY());
            }
            pb = new Point2D.Double(
                    (p1.getX() + p2.getX()) / 2 - (Math.abs(p2.getX() - p1.getX()) / 4),
                    (p1.getY() + p2.getY()) / 2);
        }

        scalePoint(p1, scale);
        scalePoint(p2, scale);

        return new Point2D[] { p1, pb, p2 };
    }


    private Path2D getConstraintPath(Point2D[] points) {
        Path2D path = new Path2D.Double();
        path.moveTo(points[0].getX(), points[0].getY());
        path.curveTo(points[0].getX(), points[0].getY(),
                points[1].getX(), points[1].getY(),
                points[2].getX(), points[2].getY());
        return path;
    }


    private void scalePoint(Point2D.Double point, double scale) {
        point.setLocation(point.getX() * scale, point.getY() * scale);
    }


    private Set<Triple> getFamiliarTasks(NetGraph graph) {
        return getConstraint(graph, "isFamiliarTaskOf");
    }


    private Set<Triple> getFourEyesTasks(NetGraph graph) {
        return getConstraint(graph, "isFourEyesTaskOf");
    }


    private Set<Triple> getConstraint(NetGraph graph, String predicate) {
        String netID = graph.getNetModel().getName() + "!";
        try {
            Set<Triple> queryResult =
                    OntologyHandler.query(predicate).getTriples();
            for (Triple triple : queryResult) {
                triple.removeSubjectPrefix(netID);
                triple.removeObjectPrefix(netID);
            }
            return queryResult;
        }
        catch (OntologyQueryException vqe) {
            return Collections.emptySet();
        }
    }


    private void overlayConstraints(Graphics2D g, Set<Triple> triples, double scale,
                                    String iconName) {
        for (Triple triple : triples) {
            Rectangle2D T1 = getTaskBounds(triple.getSubject());
            Rectangle2D T2 = getTaskBounds(triple.getObject());
            if (! (T1 == null || T2 == null)) {
                Point2D[] pathPoints = getPathPoints(T1, T2, scale);
                Path2D path = getConstraintPath(pathPoints);
                g.draw(path);

                paintIcon(g, getIconPosition(path), iconName);
            }
        }
    }


    private void paintIcon(Graphics2D g, Point2D location, String iconName) {
        if (location != null) {
            Icon icon = getIcon(iconName);
            if (icon != null) {
                int x = (int) location.getX();
                int y = (int) location.getY();
                icon.paintIcon(null, g, x, y);
            }
        }
    }


    private Icon getIcon(String iconName) {
        if (iconName.equals(FAM_TASK_ICON_NAME)) {
            return getFamTaskIcon();
        }
        if (iconName.equals(FOUR_EYES_ICON_NAME)) {
            return getFourEyesIcon();
        }
        return null;
    }


    private Icon getFamTaskIcon() {
        if (_famTaskIcon == null) {
            _famTaskIcon = loadIcon(FAM_TASK_ICON_NAME);
        }
        return _famTaskIcon;
    }


    private Icon getFourEyesIcon() {
        if (_fourEyesIcon == null) {
            _fourEyesIcon = loadIcon(FOUR_EYES_ICON_NAME);
        }
        return _fourEyesIcon;
    }


    private Icon loadIcon(String iconFileName) {
        try {
            String path = ICON_PATH + iconFileName;
            BufferedImage image = ImageIO.read(new File(path));
            if (image != null) {
                return new ImageIcon(image);
            }
        }
        catch (IOException ignore) {

        }
        return null;
    }


    // since the curved path has not yet appeared on the canvas, here it is reproduced
    // in a BufferedImage, so that the precise y-coord mid-line can be found
    private Point2D getIconPosition(Path2D path) {
        try {
            // create the Image
            Rectangle rect = path.getBounds();
            int width = rect.x + rect.width;
            int height = rect.y + rect.height;
            BufferedImage bi = new BufferedImage(width, height,
                    BufferedImage.TYPE_INT_ARGB);

            // ... and draw the path on it
            Graphics2D g = bi.createGraphics();
            g.setBackground(Color.white);
            g.setColor(Color.GREEN.darker());
            int pathRGB = g.getColor().getRGB();
            g.setStroke(new BasicStroke(3));
            g.draw(path);
            g.dispose();

            // starting at min-y, test each y at midline-x for the curve color
            int midX = rect.x + (rect.width / 2);
            for (int y = 0; y < rect.y + rect.height; y++) {
                if (y < 0) continue;
                int pixelRGB = bi.getRGB(midX, y);

                // if the current pixel is same color as curve, return this point
                if (pathRGB == pixelRGB) {
                    return new Point2D.Double(midX, y);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
