package org.yawlfoundation.yawl.views;

import org.yawlfoundation.yawl.editor.ui.actions.net.YAWLSelectedNetAction;
import org.yawlfoundation.yawl.editor.ui.elements.model.YAWLVertex;
import org.yawlfoundation.yawl.editor.ui.net.NetGraphModel;
import org.yawlfoundation.yawl.editor.ui.plugin.YEditorPluginAdapter;
import org.yawlfoundation.yawl.editor.ui.specification.SpecificationModel;
import org.yawlfoundation.yawl.views.menu.MenuBuilder;

import javax.swing.*;
import java.util.Set;

/**
 * @author Michael Adams
 * @date 12/10/2016
 */
public class ViewsPlugin extends YEditorPluginAdapter {

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
        return new MenuBuilder().getMenu();
    }

    @Override
    public JToolBar getToolbar() {
        return new MenuBuilder().getToolBar();
    }


    @Override
    public void closeSpecification() {
        OntologyHandler.unload();
    }

    @Override
    public void netElementAdded(NetGraphModel model, YAWLVertex element) {
        //    OntologyHandler.update(SpecificationModel.getHandler());
    }

    @Override
    public void netElementsRemoved(NetGraphModel model, Set<Object> cellsAndTheirEdges) {
        //    OntologyHandler.update(SpecificationModel.getHandler());
    }

    @Override
    public void specificationChanged() {
        OntologyHandler.update(SpecificationModel.getHandler());
    }
}
