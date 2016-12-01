package org.yawlfoundation.yawl.views.menu;

import org.yawlfoundation.yawl.editor.ui.actions.net.YAWLSelectedNetAction;
import org.yawlfoundation.yawl.views.table.TaskIOTableDialog;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

class TaskIOAction extends YAWLSelectedNetAction {

    {
        putValue(Action.SHORT_DESCRIPTION, "Task IO Table");
        putValue(Action.NAME, "TaskIO");
        putValue(Action.LONG_DESCRIPTION, "Show Task Data IO matrix");
        putValue(Action.MNEMONIC_KEY, KeyEvent.VK_M);
    }


    public void actionPerformed(ActionEvent event) {
        new TaskIOTableDialog().setVisible(true);
    }

}
