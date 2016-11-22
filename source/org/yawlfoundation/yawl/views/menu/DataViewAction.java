package org.yawlfoundation.yawl.views.menu;

import org.yawlfoundation.yawl.editor.ui.actions.net.YAWLSelectedNetAction;
import org.yawlfoundation.yawl.views.graph.DataViewDialog;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

class DataViewAction extends YAWLSelectedNetAction {


    {
        putValue(Action.SHORT_DESCRIPTION, "Task I/O View");
        putValue(Action.NAME, "Task I/O Graph");
        putValue(Action.LONG_DESCRIPTION, "Task Variable I/O graph");
        putValue(Action.MNEMONIC_KEY, KeyEvent.VK_T);
    }

    public DataViewAction() {
        super();
    }


    public void actionPerformed(ActionEvent event) {
        new DataViewDialog().setVisible(true);
     }

}
