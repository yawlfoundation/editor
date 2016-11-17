package org.yawlfoundation.yawl.views.menu;

import org.yawlfoundation.yawl.editor.ui.actions.net.YAWLSelectedNetAction;
import org.yawlfoundation.yawl.editor.ui.specification.SpecificationModel;
import org.yawlfoundation.yawl.views.ontology.OntologyHandler;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

class SaveOntologyAction extends YAWLSelectedNetAction {

    {
        putValue(Action.SHORT_DESCRIPTION, "Save Ontology");
        putValue(Action.NAME, "Save to OWL File");
        putValue(Action.LONG_DESCRIPTION, "Save the populated ontology to OWL file");
        putValue(Action.MNEMONIC_KEY, KeyEvent.VK_S);
    }


    public void actionPerformed(ActionEvent event) {
        if (!OntologyHandler.isLoaded()) {
            OntologyHandler.load(SpecificationModel.getHandler());
        }

        OntologyHandler.save();
    }

}
