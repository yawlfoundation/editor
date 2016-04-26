/*
 * Copyright (c) 2004-2013 The YAWL Foundation. All rights reserved.
 * The YAWL Foundation is a collaboration of individuals and
 * organisations who are committed to improving workflow technology.
 *
 * This file is part of YAWL. YAWL is free software: you can
 * redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation.
 *
 * YAWL is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General
 * Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with YAWL. If not, see <http://www.gnu.org/licenses/>.
 */

package org.yawlfoundation.yawl.editor.ui.properties.data;

import org.yawlfoundation.yawl.editor.core.data.BindingReference;
import org.yawlfoundation.yawl.editor.core.data.YDataHandlerException;
import org.yawlfoundation.yawl.editor.ui.properties.data.binding.references.BindingReferencesDialog;
import org.yawlfoundation.yawl.editor.ui.specification.SpecificationModel;
import org.yawlfoundation.yawl.editor.ui.swing.MessageDialog;
import org.yawlfoundation.yawl.elements.YNet;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The basic, net-level variable table panel
 *
 * @author Michael Adams
 * @date 9/08/12
 */
public class NetVariableTablePanel extends VariableTablePanel
        implements ActionListener, ListSelectionListener, TableModelListener {

    // toolbar button
    private JButton btnBindingRefs;

    public NetVariableTablePanel(java.util.List<VariableRow> rows,
                                 String decompositionID, DataVariableDialog parent) {
        super(rows, TableType.Net, decompositionID, parent);
        populateToolBar();
        enableButtons(true);
    }


    public void actionPerformed(ActionEvent event) {
        super.actionPerformed(event);
        String action = event.getActionCommand();
        if (action.equals("BindingRefs")) {
            showBindingReferencesDialog(table.getSelectedVariable().getName());
        }
    }


    public NetVariableTablePanel copy() {
        return new NetVariableTablePanel(table.getVariables(),
                table.getDecompositionID(), parent);
    }


    private void populateToolBar() {
        btnBindingRefs = toolbar.addButton("bindingref", "BindingRefs", " Find References ");
        addStatusBar();
    }


    protected void enableButtons(boolean enable) {
        super.enableButtons(enable);
        boolean hasRowSelected = table.getSelectedRow() > -1;
        btnBindingRefs.setEnabled(enable && hasRowSelected);
    }


    private void showBindingReferencesDialog(String netVarName) {
        try {
            java.util.List<BindingReference> references =
                    getDataHandler().getBindingReferences(parent.getNet(), netVarName);
            new BindingReferencesDialog(parent, references, netVarName).setVisible(true);
        }
        catch (YDataHandlerException ydhe) {
            MessageDialog.error(this, "Error: " + ydhe.getMessage(),
                    "Get Binding References Error");
        }
    }


    private boolean isRootNet(YNet net) {
        return SpecificationModel.getHandler().getControlFlowHandler()
                .getRootNet().equals(net);
    }

}
