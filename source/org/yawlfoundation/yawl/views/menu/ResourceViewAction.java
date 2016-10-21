package org.yawlfoundation.yawl.views.menu;

import org.yawlfoundation.yawl.editor.ui.YAWLEditor;
import org.yawlfoundation.yawl.editor.ui.actions.net.YAWLSelectedNetAction;
import org.yawlfoundation.yawl.editor.ui.net.NetGraph;
import org.yawlfoundation.yawl.editor.ui.net.NetGraphUI;
import org.yawlfoundation.yawl.editor.ui.specification.SpecificationModel;
import org.yawlfoundation.yawl.views.OntologyHandler;
import org.yawlfoundation.yawl.views.ResourceView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

class ResourceViewAction extends YAWLSelectedNetAction {

    private boolean selected;
    private ResourceView resourceView;

    {
        putValue(Action.SHORT_DESCRIPTION, "Resource View Overlay");
        putValue(Action.NAME, "Resource View");
        putValue(Action.LONG_DESCRIPTION, "Colours tasks by selected roles");
        putValue(Action.MNEMONIC_KEY, KeyEvent.VK_R);
    }


    public void actionPerformed(ActionEvent event) {
        NetGraph graph = YAWLEditor.getNetsPane().getSelectedGraph();
        NetGraphUI graphUI = (NetGraphUI) graph.getUI();
        selected = !selected;
        if (selected) {
            if (!OntologyHandler.isLoaded()) {
                OntologyHandler.load(SpecificationModel.getHandler());
            }
            resourceView = new ResourceView(graph);
            graphUI.addOverlay(resourceView);
        }
        else {
            graphUI.removeOverlay(resourceView);
            resourceView.close();
            resourceView = null;
        }
        YAWLEditor.getNetsPane().getSelectedGraph().repaint();
    }

    public boolean isSelected() {
        return selected;
    }

}
