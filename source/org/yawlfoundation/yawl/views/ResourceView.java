package org.yawlfoundation.yawl.views;

import org.yawlfoundation.yawl.editor.ui.elements.model.AtomicTask;
import org.yawlfoundation.yawl.editor.ui.elements.model.MultipleAtomicTask;
import org.yawlfoundation.yawl.editor.ui.elements.model.YAWLAtomicTask;
import org.yawlfoundation.yawl.editor.ui.elements.model.YAWLTask;
import org.yawlfoundation.yawl.editor.ui.net.NetGraph;
import org.yawlfoundation.yawl.editor.ui.net.OverlaidView;
import org.yawlfoundation.yawl.editor.ui.net.utilities.NetUtilities;
import org.yawlfoundation.yawl.views.dialog.ResourceKeyTableDialog;

import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.*;
import java.util.List;

import static org.yawlfoundation.yawl.editor.ui.elements.view.MultipleAtomicTaskView.MultipleAtomicTaskRenderer.GAP_DIVISOR;

/**
 * @author Michael Adams
 * @date 18/10/2016
 */
public class ResourceView implements OverlaidView {

    private ResourceKeyTableDialog _legendDialog;
    private NetGraph _graph;
    private Map<String, YAWLAtomicTask> _taskMap;            // [taskID, task]
    private Map<String, Set<String>> _resourceMap;           // [taskID, set of roles]
    private Map<String, Color> _colorMap;                    // [roleID, color]


    public ResourceView(NetGraph graph) {
        _graph = graph;
        _graph.getModel().addUndoableEditListener(new ChangeListener());
        _taskMap = getTaskMap(_graph);
        _resourceMap = getResourceMap(_graph);
        _colorMap = getColorMap(_resourceMap);
    }

    @Override
    public void paint(Graphics g, Color canvasBackground) {
        for (String taskID : _taskMap.keySet()) {
            YAWLAtomicTask task = _taskMap.get(taskID);
            Set<String> roles = _resourceMap.get(taskID);
            overlayTask((Graphics2D) g, task, roles, _colorMap, _graph.getScale());
        }

        if (_legendDialog == null) {
            _legendDialog = new ResourceKeyTableDialog(_colorMap);
            _legendDialog.setVisible(true);
        }
    }


    public void close() {
        if (_legendDialog != null) {
            _legendDialog.setVisible(false);
        }
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
        catch (ViewsQueryException vqe) {
            return Collections.emptyMap();
        }
    }


    private Map<String, Color> getColorMap(Map<String, Set<String>> resourceMap) {
        Set<String> roleSet = getUniqueRoles(resourceMap);
        List<Color> colorSet = ColorSelector.get(roleSet.size());
        Map<String, Color> colorMap = new HashMap<String, Color>();
        Iterator<Color> itr = colorSet.iterator();
        for (String role : roleSet) {
            colorMap.put(role, itr.next());
        }
        return colorMap;
    }


    private Set<String> getUniqueRoles(Map<String, Set<String>> resourceMap) {
        Set<String> uniqueRoles = new HashSet<String>();
        for (Set<String> roles : resourceMap.values()) {
            uniqueRoles.addAll(roles);
        }
        return uniqueRoles;
    }


    private void overlayTask(Graphics2D g, YAWLAtomicTask task, Set<String> roleSet,
                             Map<String, Color> colorMap, double scale) {

        Set<Color> colorSet = new HashSet<Color>();
        if (roleSet == null) {
            colorSet.add(Color.WHITE);
        }
        else {
            for (String role : roleSet) {
                colorSet.add(colorMap.get(role));
            }
        }
        if (task instanceof AtomicTask) {
            fillSingleInstanceTask(g, task, colorSet, scale);
        }
        else if (task instanceof MultipleAtomicTask) {
            fillMultipleInstanceTask(g, task, colorSet, scale);
        }
    }


    private void fillSingleInstanceTask(Graphics2D g, YAWLAtomicTask task,
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
    }


    protected void fillMultipleInstanceTask(Graphics2D g, YAWLAtomicTask task,
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


}
