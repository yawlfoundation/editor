package org.yawlfoundation.yawl.views.dialog;

import org.yawlfoundation.yawl.editor.ui.YAWLEditor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Map;

/**
 * @author Michael Adams
 * @date 19/10/2016
 */
public class ResourceKeyTableDialog extends JDialog {

    private JTable _table;
    private JScrollPane _pane;
    private JCheckBox _cbxConstraints;
    private JCheckBox _cbxServices;


    public ResourceKeyTableDialog(Map<String, Color> roleColorMap, ActionListener listener) {
        super(YAWLEditor.getInstance());
        add(getContent(roleColorMap, listener));
        setResizable(true);
        pack();
        setLocationRelativeTo(YAWLEditor.getInstance());
    }


    public void setValues(Map<String, Color> roleColorMap) {
        ((ResourceViewTableModel) _table.getModel()).setValues(roleColorMap);
        resize();
    }

    public boolean isConstraintsSelected() { return _cbxConstraints.isSelected(); }

    public boolean isServicesSelected() { return _cbxServices.isSelected(); }


    private JPanel getContent(Map<String, Color> roleColorMap, ActionListener listener) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(getTablePane(roleColorMap), BorderLayout.CENTER);
        panel.add(getOptionsPanel(listener), BorderLayout.SOUTH);
        return panel;
    }


    private JScrollPane getTablePane(Map<String, Color> roleColorMap) {
        _table = new JTable(new ResourceViewTableModel(roleColorMap));
        _table.setTableHeader(null);
        _table.getColumnModel().getColumn(0).setCellRenderer(new RoleColorCellRenderer());
        _pane = new JScrollPane(_table);
        setSize();
        return _pane;
    }


    private JPanel getOptionsPanel(ActionListener listener) {
        JPanel panel = new JPanel(new GridLayout(0,1));
        panel.setBorder(new EmptyBorder(3,5,3,5));
        _cbxConstraints = new JCheckBox("Show Constraints");
        _cbxConstraints.setActionCommand("constraints");
        _cbxConstraints.addActionListener(listener);
        _cbxServices = new JCheckBox("Show Services");
        _cbxServices.setActionCommand("services");
        _cbxServices.addActionListener(listener);
        panel.add(_cbxConstraints);
        panel.add(_cbxServices);
        return panel;
    }


    private void setSize() {
        int preferredHeight = (int) Math.min(_table.getPreferredSize().getHeight() + 5, 150);
        _pane.setPreferredSize(new Dimension(180, preferredHeight));
    }


    private void resize() {
        setSize();
        pack();
    }

}
