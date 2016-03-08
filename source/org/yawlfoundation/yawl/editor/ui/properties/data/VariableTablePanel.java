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

import org.yawlfoundation.yawl.editor.core.data.YDataHandler;
import org.yawlfoundation.yawl.editor.ui.properties.dialog.component.MiniToolBar;
import org.yawlfoundation.yawl.editor.ui.specification.SpecificationModel;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.util.Collections;
import java.util.Vector;

/**
 * The basic, net-level variable table panel
 *
 * @author Michael Adams
 * @date 9/08/12
 */
public class VariableTablePanel extends JPanel
        implements ActionListener, ListSelectionListener, TableModelListener {

    protected VariableTable table;
    protected final DataVariableDialog parent;
    protected final JScrollPane scrollPane;
    protected final ScrollListener scrollListener;
    protected MiniToolBar toolbar;
    protected final TableType tableType;
    protected boolean isEditing;

    // toolbar buttons
    private JButton btnUp;
    private JButton btnDown;
    private JButton btnAdd;
    private JButton btnDel;
    private StatusPanel status;


    public VariableTablePanel(java.util.List<VariableRow> rows, TableType tableType,
                              String decompositionID, DataVariableDialog parent) {
        this.parent = parent;
        this.tableType = tableType;
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10,10,0,10));
        scrollPane = new JScrollPane(createTable(rows, tableType, decompositionID));
        scrollPane.setSize(new Dimension(tableType.getPreferredWidth(), 180));
        scrollListener = new ScrollListener();
        add(populateToolBar(), BorderLayout.SOUTH);
        add(scrollPane, BorderLayout.CENTER);
        table.getSelectionModel().addListSelectionListener(this);
        table.getModel().addTableModelListener(this);
    }


    // table selection event
    public void valueChanged(ListSelectionEvent event) {
        if (! event.getValueIsAdjusting()) {
            tableChanged(null);
        }
    }


    // table change event
    public void tableChanged(TableModelEvent e) {
        enableButtons(!isEditing);
    }


    public VariableTable getTable() { return table; }

    public void refreshTable() { table.refresh(); }


    public void showErrorStatus(String msg, java.util.List<String> more) {
        status.set("    " + msg, StatusPanel.ERROR, more);
    }


    public void showOKStatus(String msg, java.util.List<String> more) {
        status.set("    " + msg, StatusPanel.OK, more);
    }


    public void clearStatus() {
        status.clear();
     }



    public Vector<VariableScope> getScopes() {
        Vector<VariableScope> scopes = new Vector<VariableScope>();
        Collections.addAll(scopes, VariableScope.values());
        return scopes;
    }


    public void actionPerformed(ActionEvent event) {
        clearStatus();
        String action = event.getActionCommand();
        if (action.equals("Add")) {
            table.addRow();
            setEditMode(true);
        }
        else if (action.equals("Del")) {
            table.removeRow();
        }
        else if (action.equals("Up")) {
            table.moveSelectedRowUp();
        }
        else if (action.equals("Down")) {
            table.moveSelectedRowDown();
        }
    }

    public VariableRow getVariableAtRow(int row) {
        java.util.List<VariableRow> variableRows = table.getVariables();
        return row < variableRows.size() ? variableRows.get(row) : null;
    }

    public boolean isEditing() {return isEditing; }

    public void showToolBar(boolean show) { toolbar.setVisible(show); }


    public VariableTablePanel copy() {
        return new VariableTablePanel(table.getVariables(), tableType,
                table.getDecompositionID(), parent);
    }

    public DataVariableDialog getVariableDialog() { return parent; }

    private JTable createTable(java.util.List<VariableRow> rows, TableType tableType,
                               String decompositionID) {
        table = new VariableTable(tableType);
        table.setVariables(rows);
        table.setDecompositionID(decompositionID);
        VariableRowUsageEditor usageEditor = new VariableRowUsageEditor(this);
        table.setDefaultEditor(Integer.class, usageEditor);
        VariableRowStringEditor stringEditor = new VariableRowStringEditor(this);
        table.setDefaultEditor(String.class, stringEditor);
        VariableRowUsageRenderer usageRenderer = new VariableRowUsageRenderer();
        table.setDefaultRenderer(Integer.class, usageRenderer);
        VariableRowStringRenderer stringRenderer = new VariableRowStringRenderer();
        table.setDefaultRenderer(String.class, stringRenderer);
        fixSelectorColumn(table);
        if (table.getRowCount() > 0) table.selectRow(0);
        return table;
    }


    private JToolBar populateToolBar() {
        toolbar = new MiniToolBar(this);
        btnAdd = toolbar.addButton("plus", "Add", " Add ");
        btnDel = toolbar.addButton("minus", "Del", " Remove ");
        btnUp = toolbar.addButton("arrow_up", "Up", " Move up ");
        btnDown = toolbar.addButton("arrow_down", "Down", " Move down ");
        return toolbar;
    }


    protected void addStatusBar() {
        status = new StatusPanel(parent);
        toolbar.add(status);
    }


    public void setTableChanged() {
        table.getTableModel().setTableChanged(true);
        parent.enableApplyButton();
    }


    private void fixSelectorColumn(JTable table) {
        TableColumn column = table.getColumnModel().getColumn(0);
        column.setPreferredWidth(15);
        column.setMaxWidth(15);
        column.setResizable(false);
    }


    protected void enableButtons(boolean enable) {
        boolean hasRowSelected = table.getSelectedRow() > -1;
        btnAdd.setEnabled(enable);
        btnDel.setEnabled(enable && hasRowSelected);
        btnUp.setEnabled(enable && hasRowSelected);
        btnDown.setEnabled(enable && hasRowSelected);
    }



    protected YDataHandler getDataHandler() {
        return SpecificationModel.getHandler().getDataHandler();
    }



    protected void setEditMode(boolean editing) {
        if (isEditing != editing) {
            isEditing = editing;
            parent.setEditing(editing, tableType);
            enableButtons(!editing);
            setTitleIndicator(editing);
//            setScrollFreezer(editing);
            if (! editing) {
                getTable().getTableModel().setTableChanged(true);
//                scrollPane.getVerticalScrollBar().removeAdjustmentListener(scrollListener);
            }
//            else {
 //               scrollPane.getVerticalScrollBar().addAdjustmentListener(scrollListener);
//            }
        }
    }


    protected void cancelEdit() {
        isEditing = false;
        parent.setEditing(false, tableType);
        enableButtons(true);

        // if the current row has no name, it is a cancelled new row so must be removed
        VariableRow editedRow = getTable().getSelectedVariable();
        if (editedRow != null && editedRow.getName().isEmpty()) {
            getTable().removeRow();
        }
    }


    protected void setTitleIndicator(boolean editing) {
        Border border = getBorder();
        if (border instanceof TitledBorder) {
            TitledBorder titledBorder = (TitledBorder) getBorder();
            String title = titledBorder.getTitle();
            String pencil = " \u270E";
            if (editing) {
                titledBorder.setTitle(title + pencil);
            }
            else {
                titledBorder.setTitle(title.replace(pencil, ""));
            }
            repaint();
        }
    }


    // when editing, prevent the table's viewport from scrolling
    protected void setScrollFreezer(boolean freeze) {
        if (freeze) {
            scrollListener.topRow = table.rowAtPoint(
                    scrollPane.getViewport().getViewPosition()
            );
            scrollPane.getVerticalScrollBar().addAdjustmentListener(scrollListener);
        }
        else {
            scrollPane.getVerticalScrollBar().removeAdjustmentListener(scrollListener);
        }
    }


    protected void notifyUsageChange(int usage) {  }



    /**********************************************************/

    class ScrollListener implements AdjustmentListener {

        int topRow;

        // override viewport changes while frozen
        public void adjustmentValueChanged(AdjustmentEvent e) {
            scrollPane.getViewport().setViewPosition(
                    table.getCellRect(topRow, 0, true).getLocation()
            );
        }
    }

}
