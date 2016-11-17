package org.yawlfoundation.yawl.views.resource;

import org.yawlfoundation.yawl.editor.ui.YAWLEditor;
import org.yawlfoundation.yawl.editor.ui.net.NetGraph;
import org.yawlfoundation.yawl.editor.ui.net.NetGraphModel;
import org.yawlfoundation.yawl.editor.ui.net.NetGraphUI;
import org.yawlfoundation.yawl.editor.ui.specification.SpecificationModel;
import org.yawlfoundation.yawl.views.util.ColorSelector;
import org.yawlfoundation.yawl.views.dialog.ResourceKeyTableDialog;
import org.yawlfoundation.yawl.views.ontology.OntologyHandler;

import java.awt.*;
import java.util.*;

/**
 * @author Michael Adams
 * @date 25/10/2016
 */
public class ResourceViewHandler {

    private final Map<NetGraph, ResourceView> _views;
    private ResourceKeyTableDialog _legendDialog;
    private boolean _enabled;


    public ResourceViewHandler() {
        _views = new HashMap<NetGraph, ResourceView>();
    }


    public void enableView(boolean enable) {
        _enabled = enable;
        if (enable) {
            Map<String, Color> colorMap = addViews();
            showLegend(colorMap);
        }
        else {
            removeViews();
            hideLegend();
        }
        YAWLEditor.getNetsPane().getSelectedGraph().repaint();
    }


    public void refreshViews() {
        if (_enabled) {
            removeViews();
            OntologyHandler.update(SpecificationModel.getHandler());
            enableView(true);
        }
    }


    private Map<String, Color> addViews() {
        if (!OntologyHandler.isLoaded()) {
            OntologyHandler.load(SpecificationModel.getHandler());
        }
        for (NetGraphModel model : SpecificationModel.getNets()) {
            NetGraph graph = model.getGraph();
            ResourceView view = new ResourceView(graph);
            _views.put(graph, view);

            NetGraphUI graphUI = (NetGraphUI) graph.getUI();
            graphUI.addOverlay(view);
        }

        // combine to one set of color-role combos
        Map<String, Color> colorMap = buildColorMap();
        for (ResourceView view : _views.values()) {
            view.setColorMap(colorMap);
        }

        return colorMap;
    }


    public void removeViews() {
        for (NetGraph graph : _views.keySet()) {
            NetGraphUI graphUI = (NetGraphUI) graph.getUI();
            ResourceView view = _views.get(graph);
            graphUI.removeOverlay(view);
        }
        _views.clear();
    }


    private Map<String, Color> buildColorMap() {
        Set<String> roleSet = getUniqueRoles();
        java.util.List<Color> colorSet = ColorSelector.get(roleSet.size());
        Map<String, Color> colorMap = new HashMap<String, Color>();
        Iterator<Color> itr = colorSet.iterator();
        for (String role : roleSet) {
            colorMap.put(role, itr.next());
        }
        return colorMap;
    }


    private Set<String> getUniqueRoles() {
        Set<String> uniqueRoles = new HashSet<String>();
        for (ResourceView view : _views.values()) {
            uniqueRoles.addAll(view.getResources());
        }
        return uniqueRoles;
    }


    private void showLegend(Map<String, Color> colorMap) {
        if (!colorMap.isEmpty()) {
            if (_legendDialog == null) {
                _legendDialog = new ResourceKeyTableDialog(colorMap);
            }
            else {
                _legendDialog.setValues(colorMap);
            }
            _legendDialog.setVisible(true);
        }
    }


    private void hideLegend() {
        if (_legendDialog != null) {
            _legendDialog.setVisible(false);
        }
    }

}
