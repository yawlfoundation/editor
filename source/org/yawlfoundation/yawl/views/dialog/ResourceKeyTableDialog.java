package org.yawlfoundation.yawl.views.dialog;

import org.yawlfoundation.yawl.editor.ui.YAWLEditor;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

/**
 * @author Michael Adams
 * @date 19/10/2016
 */
public class ResourceKeyTableDialog extends JDialog {

    private JTable _table;
    private JScrollPane _pane;

    public ResourceKeyTableDialog(Map<String, Color> roleColorMap) {
        super(YAWLEditor.getInstance());
        add(buildTable(roleColorMap));
        setResizable(false);
        pack();
        setLocationRelativeTo(YAWLEditor.getInstance());
    }


    public void setValues(Map<String, Color> roleColorMap) {
        ((ResourceViewTableModel) _table.getModel()).setValues(roleColorMap);
        resize();
    }


    private JScrollPane buildTable(Map<String, Color> roleColorMap) {
        _table = new JTable(new ResourceViewTableModel(roleColorMap));
        _table.setTableHeader(null);
        _table.getColumnModel().getColumn(0).setCellRenderer(new RoleColorCellRenderer());
        _pane = new JScrollPane(_table);
        setSize();
        return _pane;
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
