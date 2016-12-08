package org.yawlfoundation.yawl.views.menu;

import org.yawlfoundation.yawl.editor.ui.actions.net.YAWLSelectedNetAction;
import org.yawlfoundation.yawl.editor.ui.specification.SpecificationModel;
import org.yawlfoundation.yawl.views.dialog.UserRulesDialog;
import org.yawlfoundation.yawl.views.ontology.OntologyHandler;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

class UserRulesAction extends YAWLSelectedNetAction {

    {
        putValue(Action.SHORT_DESCRIPTION, "Edit User Rules");
        putValue(Action.NAME, "Edit User Rules");
        putValue(Action.LONG_DESCRIPTION, "Edit User Defined Rules");
        putValue(Action.MNEMONIC_KEY, KeyEvent.VK_U);
    }


    public void actionPerformed(ActionEvent event) {
        if (!OntologyHandler.isLoaded()) {
            OntologyHandler.load(SpecificationModel.getHandler());
        }
        new UserRulesDialog().setVisible(true);
    }

}
