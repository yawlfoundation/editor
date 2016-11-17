package org.yawlfoundation.yawl.views.menu;

import org.yawlfoundation.yawl.editor.core.resourcing.ResourceDataSet;
import org.yawlfoundation.yawl.editor.ui.actions.net.YAWLSelectedNetAction;
import org.yawlfoundation.yawl.editor.ui.specification.SpecificationModel;
import org.yawlfoundation.yawl.editor.ui.swing.MessageDialog;
import org.yawlfoundation.yawl.resourcing.resource.Role;
import org.yawlfoundation.yawl.views.graph.GraphMLWriter;
import org.yawlfoundation.yawl.views.graph.GraphViewDialog;
import org.yawlfoundation.yawl.views.ontology.OntologyHandler;
import org.yawlfoundation.yawl.views.ontology.Triple;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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
        if (!OntologyHandler.isLoaded()) {
            OntologyHandler.load(SpecificationModel.getHandler());
        }
        try {
            String pred = "passesDataTo";
            List<Triple> triples = processTriples(OntologyHandler.swrlQuery(pred));

            if (! triples.isEmpty()) {
                GraphMLWriter writer = new GraphMLWriter();
                InputStream is = writer.toInputStream(triples);
                new GraphViewDialog(is).setVisible(true);
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


    private List<Triple> processTriples(List<Triple> triples) {
        List<Triple> processed = new ArrayList<Triple>();
        for (Triple triple : triples) {
            triple.invert();
            String subjectRole = getRoleName(triple.getSubject());
            String objectRole = getRoleName(triple.getObject());
            processed.add(new Triple(subjectRole, triple.getPredicate(), objectRole));
        }
        return processed;
    }


    private String getRoleName(String roleID) {
        Role role = ResourceDataSet.getRole(roleID);
        return role != null ? role.getName() : roleID;
    }

}
