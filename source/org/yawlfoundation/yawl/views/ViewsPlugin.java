package org.yawlfoundation.yawl.views;

import org.yawlfoundation.yawl.editor.ui.actions.net.YAWLSelectedNetAction;
import org.yawlfoundation.yawl.editor.ui.plugin.YEditorPluginAdapter;
import org.yawlfoundation.yawl.editor.ui.specification.SpecificationModel;

import javax.swing.*;

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
        return null;
    }

    @Override
    public void closeSpecification() {
        OntologyHandler.unload();
    }

    @Override
    public void openSpecification() {
        OntologyHandler.load(SpecificationModel.getHandler());
    }
}
