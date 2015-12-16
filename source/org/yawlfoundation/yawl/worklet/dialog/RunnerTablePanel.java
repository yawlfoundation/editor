/*
 * Copyright (c) 2004-2014 The YAWL Foundation. All rights reserved.
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

import org.yawlfoundation.yawl.editor.ui.properties.data.StatusPanel;
import org.yawlfoundation.yawl.editor.ui.properties.dialog.component.MiniToolBar;
import org.yawlfoundation.yawl.worklet.selection.WorkletRunner;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.EventListener;

/**
 * @author Michael Adams
 * @date 30/09/2014
 */
public class RunnerTablePanel extends JPanel {

    private RunnerTable table;
    private StatusPanel status;


    public RunnerTablePanel(EventListener listener) {
        super();
        setLayout(new BorderLayout());
        setBorder(new TitledBorder("Executing Worklet Instances"));
        JScrollPane scrollPane = new JScrollPane(createTable(listener));
        scrollPane.setSize(new Dimension(600, 300));
        add(scrollPane, BorderLayout.CENTER);
        add(populateToolBar(), BorderLayout.SOUTH);
    }


    public void setRows(java.util.List<WorkletRunner> rows) {
        table.getTableModel().setRows(rows);
        table.setPreferredScrollableViewportSize(getPreferredSize());
        table.updateUI();
    }


    public WorkletRunner getSelection() {
        return table.getSelection();
    }


    public void showMsg(String msg) { status.set(msg); }


    private RunnerTable createTable(EventListener listener) {
        table = new RunnerTable();
        table.getSelectionModel().addListSelectionListener((ListSelectionListener) listener);
        return table;
    }


    private JToolBar populateToolBar() {
        MiniToolBar toolbar = new MiniToolBar(null);
        toolbar.addSeparator(new Dimension(16, 16));
        status = new StatusPanel(null);
        toolbar.add(status);
        return toolbar;
    }

}
