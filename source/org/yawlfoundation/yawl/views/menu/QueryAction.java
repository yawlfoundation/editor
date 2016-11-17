package org.yawlfoundation.yawl.views.menu;

import org.yawlfoundation.yawl.editor.ui.actions.net.YAWLSelectedNetAction;
import org.yawlfoundation.yawl.editor.ui.specification.SpecificationModel;
import org.yawlfoundation.yawl.views.ontology.OntologyHandler;
import org.yawlfoundation.yawl.views.query.QueryDialog;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

class QueryAction extends YAWLSelectedNetAction {

    {
        putValue(Action.SHORT_DESCRIPTION, "Query Ontology");
        putValue(Action.NAME, "Query the Ontology");
        putValue(Action.LONG_DESCRIPTION, "Query the populated ontology");
        putValue(Action.MNEMONIC_KEY, KeyEvent.VK_Q);
    }


    public void actionPerformed(ActionEvent event) {
        if (!OntologyHandler.isLoaded()) {
            OntologyHandler.load(SpecificationModel.getHandler());
        }
        new QueryDialog().setVisible(true);
    }

}
