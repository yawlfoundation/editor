package org.yawlfoundation.yawl.views.menu;

import org.yawlfoundation.yawl.editor.ui.actions.net.YAWLSelectedNetAction;
import org.yawlfoundation.yawl.views.graph.ResourceViewDialog;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

class GraphViewAction extends YAWLSelectedNetAction {

    {
        putValue(Action.SHORT_DESCRIPTION, "Resource Graph View");
        putValue(Action.NAME, "Resource Graph");
        putValue(Action.LONG_DESCRIPTION, "Role relations graph");
        putValue(Action.MNEMONIC_KEY, KeyEvent.VK_G);
    }


    public GraphViewAction() {
        super();
    }


    public void actionPerformed(ActionEvent event) {
        new ResourceViewDialog().setVisible(true);
    }

}
