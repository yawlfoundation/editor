package org.yawlfoundation.yawl.views.menu;

import org.yawlfoundation.yawl.editor.ui.actions.net.YAWLSelectedNetAction;
import org.yawlfoundation.yawl.editor.ui.specification.SpecificationModel;
import org.yawlfoundation.yawl.views.graph.GraphMLWriter;
import org.yawlfoundation.yawl.views.graph.GraphViewDialog;
import org.yawlfoundation.yawl.views.ontology.OntologyHandler;
import org.yawlfoundation.yawl.views.ontology.Triple;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Set;

class GraphViewAction extends YAWLSelectedNetAction {


    {
        putValue(Action.SHORT_DESCRIPTION, "Resource Graph View");
        putValue(Action.NAME, "Resource View");
        putValue(Action.LONG_DESCRIPTION, "Role relations graph");
        putValue(Action.MNEMONIC_KEY, KeyEvent.VK_G);
    }

    public GraphViewAction() {
        super();
    }


    public void actionPerformed(ActionEvent event) {
        if (!OntologyHandler.isLoaded()) {
            OntologyHandler.load(SpecificationModel.getHandler());
        }
        try {
            Set<Triple> triples = OntologyHandler.query("hasRole").getTriples();
            GraphMLWriter writer = new GraphMLWriter();

            GraphViewDialog dialog =
                    new GraphViewDialog(writer.toInputStream(triples));
            dialog.setVisible(true);
        }
        catch (Exception oqe) {
            oqe.printStackTrace();
        }

    }


}
