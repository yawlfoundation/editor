/*
 * Copyright (c) 2004-2015 The YAWL Foundation. All rights reserved.
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

package org.yawlfoundation.yawl.worklet.dialog;

import org.yawlfoundation.yawl.editor.ui.properties.dialog.component.MiniToolBar;
import org.yawlfoundation.yawl.worklet.rdr.RdrConclusion;
import org.yawlfoundation.yawl.worklet.rdr.RdrPrimitive;
import org.yawlfoundation.yawl.worklet.support.ExletValidationError;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.CellEditorListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * @author Michael Adams
 * @date 30/09/2014
 */
public class ConclusionTablePanel extends JPanel implements ActionListener {

    private ConclusionTable table;
    private MiniToolBar toolbar;


    public ConclusionTablePanel(JComboBox cbxType, CellEditorListener listener) {
        super();
        setLayout(new BorderLayout());
        setBorder(new TitledBorder("Actions"));
        table = new ConclusionTable(cbxType, listener);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setSize(new Dimension(600, 200));
        add(scrollPane, BorderLayout.CENTER);
        add(populateToolBar(), BorderLayout.SOUTH);
        setConclusion(new ArrayList<RdrPrimitive>());
    }


    public void setConclusion(java.util.List<RdrPrimitive> primitives) {
        table.setConclusion(primitives);
        table.setPreferredScrollableViewportSize(getPreferredSize());
    }


    public RdrConclusion getConclusion() {
        return table.getConclusion();
    }


    public void setVisuals(java.util.List<ExletValidationError> errors) {
        table.setVisuals(errors);
    }


    public boolean hasValidContent() {
        return table.getRowCount() > 0 && table.getBackground() != Color.PINK;
    }


    public void actionPerformed(ActionEvent event) {
        String action = event.getActionCommand();
        if (action.equals("Add")) {
            table.addRow();
        }
        else if (action.equals("Del")) {
            table.removeRow();
        }
    }


    private JToolBar populateToolBar() {
        toolbar = new MiniToolBar(this);
        toolbar.addButton("plus", "Add", " Add ");
        toolbar.addButton("minus", "Del", " Remove ");
        return toolbar;
    }

}
