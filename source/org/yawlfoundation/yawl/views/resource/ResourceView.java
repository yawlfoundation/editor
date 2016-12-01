package org.yawlfoundation.yawl.views.resource;

import org.yawlfoundation.yawl.editor.ui.elements.model.AtomicTask;
import org.yawlfoundation.yawl.editor.ui.elements.model.MultipleAtomicTask;
import org.yawlfoundation.yawl.editor.ui.elements.model.YAWLAtomicTask;
import org.yawlfoundation.yawl.editor.ui.elements.model.YAWLTask;
import org.yawlfoundation.yawl.editor.ui.net.NetGraph;
import org.yawlfoundation.yawl.editor.ui.net.OverlaidView;
import org.yawlfoundation.yawl.editor.ui.net.utilities.NetUtilities;
import org.yawlfoundation.yawl.elements.YAWLServiceGateway;
import org.yawlfoundation.yawl.elements.YAWLServiceReference;
import org.yawlfoundation.yawl.elements.YDecomposition;
import org.yawlfoundation.yawl.views.ontology.OntologyHandler;
import org.yawlfoundation.yawl.views.ontology.OntologyQueryException;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.*;

import static org.yawlfoundation.yawl.editor.ui.elements.view.MultipleAtomicTaskView.MultipleAtomicTaskRenderer.GAP_DIVISOR;

/**
 * @author Michael Adams
 * @date 18/10/2016
 */
public class ResourceView implements OverlaidView {

    private NetGraph _graph;
    private ResourceConstraintsOverlay _constraintsOverlay;
    private Map<String, YAWLAtomicTask> _taskMap;            // [taskID, task]
    private Map<String, Set<String>> _resourceMap;           // [taskID, set of roles]
    private Map<String, Color> _colorMap;                    // [roleID, color]
    private boolean _showConstraints;
    private boolean _showServices;


    public ResourceView(NetGraph graph) {
        _graph = graph;
        // _graph.getModel().addUndoableEditListener(new ChangeListener());
        _taskMap = getTaskMap(_graph);
        _resourceMap = getResourceMap(_graph);
        _constraintsOverlay = new ResourceConstraintsOverlay(graph, _taskMap);
    }


    @Override
    public void paint(Graphics g, Color canvasBackground) {
        for (String taskID : _taskMap.keySet()) {
            YAWLAtomicTask task = _taskMap.get(taskID);
            Set<String> roles = _resourceMap.get(taskID);
            overlayTask((Graphics2D) g, task, roles, _colorMap, _graph.getScale());
        }
        if (_showConstraints) {
            _constraintsOverlay.paint(g);
        }
    }


    public Set<String> getResources() {
        Set<String> resourceSet = new HashSet<String>();
        for (Set<String> resources : _resourceMap.values()) {
            resourceSet.addAll(resources);
        }
        return resourceSet;
    }

    public void setColorMap(Map<String, Color> colorMap) {
        _colorMap = colorMap;
    }

    public void setShowConstraints(boolean show) {
        _showConstraints = show;
    }


    public void setShowServices(boolean show) {
        _showServices = show;
    }


    private Map<String, YAWLAtomicTask> getTaskMap(NetGraph graph) {
        Map<String, YAWLAtomicTask> taskMap = new HashMap<String, YAWLAtomicTask>();
        for (YAWLAtomicTask task : NetUtilities.getAtomicTasks(graph.getNetModel())) {
            taskMap.put(task.getID(), task);
        }
        return taskMap;
    }


    private Map<String, Set<String>> getResourceMap(NetGraph graph) {
        String netID = graph.getNetModel().getName() + "!";
        try {
            Map<String, Set<String>> filteredResult = new HashMap<String, Set<String>>();
            Map<String, Set<String>> queryResult = OntologyHandler.query("hasRole")
                    .getSubjectObjectPairs();
            for (String id : queryResult.keySet()) {
                if (id.startsWith(netID)) {
                    String taskID = id.replace(netID, "");
                    filteredResult.put(taskID, queryResult.get(id));
                }
            }
            return filteredResult;
        }
        catch (OntologyQueryException vqe) {
            return Collections.emptyMap();
        }
    }


    private void overlayTask(Graphics2D g, YAWLAtomicTask task, Set<String> roleSet,
                             Map<String, Color> colorMap, double scale) {
        Set<Color> colorSet = new HashSet<Color>();

        YAWLServiceReference service = getService(task);
        if (service != null)  {             // special case - overrides resourcing
            if (_showServices) {
                writeService(g, service, task, scale);
                return;                      // no more to do
            }
            else {
                colorSet.add(Color.WHITE);
            }
        }
        else if (roleSet == null) {
            colorSet.add(Color.WHITE);
        }
        else {
            for (String role : roleSet) {
                colorSet.add(colorMap.get(role));
            }
        }
        Rectangle2D boundingBox = null;
        if (task instanceof AtomicTask) {
            boundingBox = fillSingleInstanceTask(g, task, colorSet, scale);
        }
        else if (task instanceof MultipleAtomicTask) {
            boundingBox = fillMultipleInstanceTask(g, task, colorSet, scale);
        }
        if (boundingBox != null) {
            redrawIndicators(g, (YAWLTask) task, boundingBox, scale);
        }
    }


    private Rectangle2D fillSingleInstanceTask(Graphics2D g, YAWLAtomicTask task,
                                               Set<Color> colorSet, double scale) {
        Rectangle2D boundingRect = getBoundingRect((YAWLTask) task, scale);
        double sectorWidth = boundingRect.getWidth() / colorSet.size();

        double xSector = boundingRect.getX();
        for (Color color : colorSet) {
            Rectangle2D sector = new Rectangle2D.Double(
                    xSector,
                    boundingRect.getY(),
                    sectorWidth,
                    boundingRect.getHeight()
            );

            g.setColor(color);
            g.fill(sector);
            xSector += sectorWidth;
        }

        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke((float) scale));
        g.draw(boundingRect);

        return boundingRect;
    }


    protected Rectangle2D fillMultipleInstanceTask(Graphics2D g, YAWLAtomicTask task,
                                                   Set<Color> colorSet, double scale) {
        Rectangle2D boundingRect = getBoundingRect((YAWLTask) task, scale);
        double horizontalGap = boundingRect.getWidth() / GAP_DIVISOR;
        double verticalGap = boundingRect.getHeight() / GAP_DIVISOR;
        double sectorWidth = (boundingRect.getWidth() - horizontalGap) / colorSet.size();

        double xSector = boundingRect.getX();
        for (Color color : colorSet) {
            Rectangle2D topRightRect = new Rectangle2D.Double(
                    xSector + horizontalGap,
                    boundingRect.getY(),
                    sectorWidth,
                    boundingRect.getHeight() - verticalGap
            );

            g.setColor(color);
            g.fill(topRightRect);
            xSector += sectorWidth;
        }

        xSector = boundingRect.getX();
        for (Color color : colorSet) {
            Rectangle2D bottomLeftRect = new Rectangle2D.Double(
                    xSector,
                    boundingRect.getY() + verticalGap,
                    sectorWidth,
                    boundingRect.getHeight() - verticalGap
            );

            g.setColor(color);
            g.fill(bottomLeftRect);
            xSector += sectorWidth;
        }

        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke((float) scale));

        Path2D.Double path = new Path2D.Double();
        path.moveTo(boundingRect.getX() + horizontalGap, boundingRect.getY() + verticalGap);
        path.lineTo(boundingRect.getX() + horizontalGap, boundingRect.getY());
        path.lineTo(boundingRect.getX() + boundingRect.getWidth(), boundingRect.getY());
        path.lineTo(boundingRect.getX() + boundingRect.getWidth(),
                boundingRect.getY() + boundingRect.getHeight() - verticalGap);
        path.lineTo(boundingRect.getX() + boundingRect.getWidth() - horizontalGap,
                boundingRect.getY() + boundingRect.getHeight() - verticalGap);
        g.draw(path);

        g.draw(new Rectangle2D.Double(
                boundingRect.getX(),
                boundingRect.getY() + verticalGap,
                boundingRect.getWidth() - horizontalGap,
                boundingRect.getHeight() - verticalGap
        ));

        return boundingRect;
    }


    private Rectangle2D getBoundingRect(YAWLTask task, double scale) {
        Dimension size = YAWLTask.getVertexSize();
        return new Rectangle2D.Double(
                task.getBounds().getX() * scale,
                task.getBounds().getY() * scale,
                size.width * scale - 1,
                size.height * scale - 1
        );
    }


    private void redrawIndicators(Graphics2D g, YAWLTask task, Rectangle2D boundingBox,
                                  double scale) {
        if (task.hasCancellationSetMembers()) {
            drawCancelSetMarker(g, boundingBox, scale);
        }
        if (isAutomatedTask(task)) {
            drawAutomatedMarker(g, boundingBox, scale);
            if (hasCodelet(task)) {
                drawCodeletMarker(g, boundingBox, scale);
            }
        }
        if (task instanceof AtomicTask && ((AtomicTask) task).hasTimerEnabled()) {
            drawTimerMarker(g, boundingBox, scale);
        }
    }


    protected void drawCancelSetMarker(Graphics2D g, Rectangle2D boundingBox, double scale) {
        Ellipse2D.Double circle = new Ellipse2D.Double(
                boundingBox.getX() + (3 * boundingBox.getWidth() / 4) - scale,
                boundingBox.getY() + scale,
                boundingBox.getWidth() / 4,
                boundingBox.getHeight() / 4
        );
        g.setColor(Color.red);
        g.fill(circle);
    }


    protected void drawTimerMarker(Graphics2D g, Rectangle2D boundingBox, double scale) {
        Ellipse2D.Double circle = new Ellipse2D.Double(
                boundingBox.getX() + scale,
                boundingBox.getY() + scale,
                boundingBox.getWidth() / 4,
                boundingBox.getHeight() / 4
        );
        Line2D vHand = new Line2D.Double(
                circle.getCenterX(),
                boundingBox.getY(),
                circle.getCenterX(),
                circle.getCenterY()
        );
        Line2D hHand = new Line2D.Double(
                circle.getCenterX(),
                circle.getCenterY(),
                boundingBox.getX() + circle.getWidth(),
                circle.getCenterY()
        );
        g.setColor(Color.white);
        g.fill(circle);
        g.setColor(Color.black);
        g.draw(circle);
        g.draw(vHand);
        g.draw(hHand);
    }


    protected void drawAutomatedMarker(Graphics2D g, Rectangle2D boundingBox, double scale) {
        Path2D.Double path = getAutomatedShape(boundingBox, scale);
        g.setColor(Color.black);
        g.draw(path);
    }

    protected void drawCodeletMarker(Graphics2D g, Rectangle2D boundingBox, double scale) {
        Path2D.Double path = getAutomatedShape(boundingBox, scale);
        g.setColor(Color.green.darker());
        g.fill(path);
    }


    private Path2D.Double getAutomatedShape(Rectangle2D taskRect, double scale) {
        Rectangle2D shapeRect = new Rectangle2D.Double(
                taskRect.getX() + (3 * taskRect.getWidth() / 8),
                taskRect.getY() + (2 * scale),
                taskRect.getWidth() / 4,
                taskRect.getHeight() / 4 - (2 * scale)
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
        return (decomp != null) && (!decomp.requiresResourcingDecisions());
    }


    private boolean hasCodelet(YAWLTask task) {
        YDecomposition decomp = task.getDecomposition();
        return decomp != null && decomp.getCodelet() != null;
    }


    private YAWLServiceReference getService(YAWLAtomicTask task) {
        YAWLServiceGateway gateway = (YAWLServiceGateway) task.getDecomposition();
        return gateway != null ? gateway.getYawlService() : null;
    }


    private void writeService(Graphics2D g, YAWLServiceReference service,
                              YAWLAtomicTask task, double scale) {
        Rectangle2D boundingRect = getBoundingRect((YAWLTask) task, scale);
        String id = service.getServiceID();
        if (id != null) {
            String name = id.substring(0, id.lastIndexOf('/'));  // -'/ib'
            name = name.substring(name.lastIndexOf('/') + 1);   // - 'http:...'
            name = name.substring(0, name.lastIndexOf("Service"));
            Color color = g.getColor();
            g.setColor(Color.BLACK);
            g.fill(boundingRect);
            g.setColor(Color.WHITE);
            Font font = g.getFont();
            g.setFont(font.deriveFont((float) (6 * scale)));
            g.drawString(name, (float) boundingRect.getX() + 5,
                    (float) boundingRect.getMaxY() - 10);
            g.setFont(font);
            g.setColor(color);
        }
    }

}
