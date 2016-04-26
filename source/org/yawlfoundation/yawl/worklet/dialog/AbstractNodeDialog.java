package org.yawlfoundation.yawl.worklet.dialog;

import org.yawlfoundation.yawl.editor.core.YConnector;
import org.yawlfoundation.yawl.editor.core.controlflow.YControlFlowHandlerException;
import org.yawlfoundation.yawl.editor.ui.YAWLEditor;
import org.yawlfoundation.yawl.editor.ui.elements.model.VertexContainer;
import org.yawlfoundation.yawl.editor.ui.elements.model.YAWLAtomicTask;
import org.yawlfoundation.yawl.editor.ui.net.NetGraph;
import org.yawlfoundation.yawl.editor.ui.specification.SpecificationModel;
import org.yawlfoundation.yawl.elements.YAWLServiceReference;
import org.yawlfoundation.yawl.elements.YDecomposition;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.awt.*;
import java.awt.event.ItemEvent;

/**
 * @author Michael Adams
 * @date 3/12/2015
 */
public abstract class AbstractNodeDialog extends JDialog implements TableModelListener {

    private boolean _isComboListener;

    public AbstractNodeDialog() { this(null, false); }

    public AbstractNodeDialog(Frame frame) { this(frame, false); }

    public AbstractNodeDialog(Frame frame, boolean isComboListener) {
        super(frame);
        _isComboListener = isComboListener;
        setModal(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }


    public abstract void enableButtons();


    public void tableChanged(TableModelEvent e) { enableButtons(); }


    public boolean isComboListener() { return _isComboListener; }


    public void comboChanged(ItemEvent event) { }  // see ViewRuleDialog


    protected void setBaseSize(int w, int h) {
        Dimension d = new Dimension(w, h);
        setPreferredSize(d);
        setMinimumSize(d);
    }


    protected YAWLAtomicTask getSelectedTask() {
        NetGraph selectedGraph = YAWLEditor.getNetsPane().getSelectedGraph();
        Object cell = selectedGraph.getSelectionCell();
        if (cell instanceof VertexContainer) {
            cell = ((VertexContainer) cell).getVertex();
        }
        return (cell instanceof YAWLAtomicTask) ? (YAWLAtomicTask) cell : null;
    }


    protected YAWLServiceReference getServiceReference() {
        for (YAWLServiceReference service : YConnector.getServices()) {
            String uri = service.getURI();
            if (uri != null && uri.contains("workletService")) {
                return service;
            }
        }
        return null;
    }


    protected YDecomposition getOrCreateDecomposition(YAWLAtomicTask task) {
        YDecomposition decomposition = task.getDecomposition();
        if (decomposition == null) {
            try {
                decomposition = SpecificationModel.getHandler()
                        .getControlFlowHandler().addTaskDecomposition(task.getName());
                task.setDecomposition(decomposition);
            }
            catch (YControlFlowHandlerException ycfhe) {
                //
            }
        }
        return decomposition;
    }

}
