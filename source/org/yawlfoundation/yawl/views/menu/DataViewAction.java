package org.yawlfoundation.yawl.views.menu;

import org.yawlfoundation.yawl.editor.ui.actions.net.YAWLSelectedNetAction;
import org.yawlfoundation.yawl.editor.ui.specification.SpecificationModel;
import org.yawlfoundation.yawl.editor.ui.swing.MessageDialog;
import org.yawlfoundation.yawl.views.graph.DataViewDialog;
import org.yawlfoundation.yawl.views.ontology.OntologyHandler;
import org.yawlfoundation.yawl.views.ontology.Triple;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

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
        if (!OntologyHandler.isLoaded()) {
            OntologyHandler.load(SpecificationModel.getHandler());
        }
        try {
            List<Triple> triples = processTriples(
                    OntologyHandler.swrlQuery("readsDataFrom"), true);
            triples.addAll(processTriples(
                    OntologyHandler.swrlQuery("writesDataTo"), false));

            if (! triples.isEmpty()) {
                new DataViewDialog(triples).setVisible(true);
            }
            else {
                MessageDialog.warn("Specification contains no roles",
                        "Resource Graph View");
            }
        }
        catch (Exception oqe) {
            oqe.printStackTrace();
        }
    }


    private List<Triple> processTriples(List<Triple> triples, boolean invert) {
        List<Triple> processed = new ArrayList<Triple>();
        for (Triple triple : triples) {
            String subject = "TASK: " +  triple.getSubject();
            Triple newTriple = new Triple(subject, triple.getPredicate(), triple.getObject());
            if (invert) newTriple.invert();
            processed.add(newTriple);
        }
        return processed;
    }


}
