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

import org.jdom2.Element;
import org.yawlfoundation.yawl.editor.ui.properties.data.StatusPanel;
import org.yawlfoundation.yawl.editor.ui.properties.data.VariableRow;
import org.yawlfoundation.yawl.editor.ui.properties.dialog.component.MiniToolBar;
import org.yawlfoundation.yawl.util.JDOMUtil;
import org.yawlfoundation.yawl.util.StringUtil;
import org.yawlfoundation.yawl.util.XNode;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.EventListener;

/**
 * @author Michael Adams
 * @date 30/09/2014
 */
public class DataContextTablePanel extends JPanel implements CellEditorListener {

    private DataContextTable table;
    private StatusPanel status;


    public DataContextTablePanel(EventListener listener, DialogMode mode) {
        super();
        setLayout(new BorderLayout());
        setBorder(new TitledBorder("Data Context"));
        JScrollPane scrollPane = new JScrollPane(createTable(listener, mode));
        scrollPane.setSize(new Dimension(500, 200));
        add(scrollPane, BorderLayout.CENTER);
        add(populateToolBar(), BorderLayout.SOUTH);
        setEditable(mode != DialogMode.Viewing);
    }


    @Override
    public void editingStopped(ChangeEvent e) {
        VariableRow row = getSelectedVariable();
        if (row.isValidValue()) {
            status.clear();
        }
        else {
            status.set("Invalid value for data type: " + row.getName());
        }
    }

    @Override
    public void editingCanceled(ChangeEvent e) { }


    public void setNode(java.util.List<VariableRow> rows, XNode cornerstoneNode) {
        table.setCornerstoneNode(cornerstoneNode);
        setVariables(rows);
        setEditable(false);
    }


    public void setVariables(java.util.List<VariableRow> rows) {
        table.getTableModel().setVariables(rows);
        table.setPreferredScrollableViewportSize(getPreferredSize());
        table.updateUI();
    }


    public VariableRow getVariableAtRow(int index) {
        return table.getTableModel().getVariableAtRow(index);
    }


    public VariableRow getSelectedVariable() {
        return table.getSelectedVariable();
    }


    public Element getDataElement(String rootName) {
        XNode root = new XNode(validateXMLLabel(rootName));
        for (VariableRow row : table.getTableModel().getVariables()) {
            XNode rowNode = new XNode(JDOMUtil.encodeEscapes(row.getName()),
                    JDOMUtil.encodeEscapes(row.getValue()));
            rowNode.addAttribute("type", row.getDataType());
            root.addChild(rowNode);
        }
        return root.toElement();
    }


    public void addEventListener(EventListener listener) {
        table.getDefaultEditor(String.class).addCellEditorListener((CellEditorListener) listener);
        table.getSelectionModel().addListSelectionListener((ListSelectionListener) listener);
    }


    public boolean hasValidContent() {
        return table.hasValidContent();
    }


    public void setEditable(boolean editable) {
        table.getTableModel().setEditable(editable);
    }


    public void clearVariables() { table.setVariables(new ArrayList<VariableRow>()); }


    private DataContextTable createTable(EventListener listener, DialogMode mode) {
        table = new DataContextTable(mode);
        DataContextValueEditor editor = new DataContextValueEditor(this);
        editor.addCellEditorListener((CellEditorListener) listener);
        table.setDefaultEditor(String.class, editor);
        if (mode != DialogMode.Viewing) {
            table.getSelectionModel().addListSelectionListener(
                    (ListSelectionListener) listener);
        }
        return table;
    }


    private JToolBar populateToolBar() {
        MiniToolBar toolbar = new MiniToolBar(null);
        toolbar.addSeparator(new Dimension(16, 16));
        status = new StatusPanel(null);
        toolbar.add(status);
        return toolbar;
    }


    private String validateXMLLabel(String label) {
        if (StringUtil.isNullOrEmpty(label)) {
            return "data";
        }
        return Character.isJavaIdentifierStart(label.charAt(0)) ? label : ("_" + label);
    }

}
