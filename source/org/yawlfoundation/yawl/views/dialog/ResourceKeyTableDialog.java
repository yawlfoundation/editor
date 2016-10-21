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

    public ResourceKeyTableDialog(Map<String, Color> roleColorMap) {
        super(YAWLEditor.getInstance());
        add(buildTable(roleColorMap));
        setResizable(false);
        pack();
        setLocationRelativeTo(YAWLEditor.getInstance());
    }


    private JScrollPane buildTable(Map<String, Color> roleColorMap) {
        JTable table = new JTable(new ResourceViewTableModel(roleColorMap));
        table.setTableHeader(null);
        table.getColumnModel().getColumn(0).setCellRenderer(new RoleColorCellRenderer());
        JScrollPane pane = new JScrollPane(table);
        int preferredHeight = (int) Math.min(table.getPreferredSize().getHeight() + 5, 150);
        pane.setPreferredSize(new Dimension(180, preferredHeight));
        return pane;
    }

}
