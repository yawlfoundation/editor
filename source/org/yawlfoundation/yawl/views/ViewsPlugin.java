package org.yawlfoundation.yawl.views;

import org.yawlfoundation.yawl.editor.ui.actions.net.YAWLSelectedNetAction;
import org.yawlfoundation.yawl.editor.ui.elements.model.YAWLAtomicTask;
import org.yawlfoundation.yawl.editor.ui.elements.model.YAWLVertex;
import org.yawlfoundation.yawl.editor.ui.net.NetGraphModel;
import org.yawlfoundation.yawl.editor.ui.plugin.YEditorPluginAdapter;
import org.yawlfoundation.yawl.elements.YAtomicTask;
import org.yawlfoundation.yawl.views.menu.MenuBuilder;

import javax.swing.*;
import java.util.Set;

/**
 * @author Michael Adams
 * @date 12/10/2016
 */
public class ViewsPlugin extends YEditorPluginAdapter {

    private final MenuBuilder _menuBuilder = new MenuBuilder();

    public String getName() {
        return "Process Views";
    }

    public String getDescription() {
        return "Provides various configurable views over a process specification";
    }

    public YAWLSelectedNetAction getPluginMenuAction() {
        return null;
    }

    public JMenu getPluginMenu() {
        return _menuBuilder.getMenu();
    }

    @Override
    public JToolBar getToolbar() {
        return _menuBuilder.getToolBar();
    }


    @Override
    public void closeSpecification() {
        OntologyHandler.unload();
    }

    @Override
    public void netElementAdded(NetGraphModel model, YAWLVertex element) {
        if (element instanceof YAWLAtomicTask) {
            _menuBuilder.getViewHandler().refreshViews();
        }
    }

    @Override
    public void netElementsRemoved(NetGraphModel model, Set<Object> cellsAndTheirEdges) {
        for (Object o : cellsAndTheirEdges) {
            if (o instanceof YAWLAtomicTask) {
                _menuBuilder.getViewHandler().refreshViews();
                break;
            }
        }
    }

    @Override
    public void resourcingChanged(YAtomicTask task) {
        _menuBuilder.getViewHandler().refreshViews();
    }
}
