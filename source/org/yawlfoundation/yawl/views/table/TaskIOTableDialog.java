package org.yawlfoundation.yawl.views.table;

import org.yawlfoundation.yawl.editor.ui.YAWLEditor;
import org.yawlfoundation.yawl.editor.ui.swing.JAlternatingRowColorTable;
import org.yawlfoundation.yawl.editor.ui.util.ButtonUtil;
import org.yawlfoundation.yawl.views.table.header.RowHeaderTable;
import org.yawlfoundation.yawl.views.table.header.VerticalTableHeaderCellRenderer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;

/**
 * @author Michael Adams
 * @date 30/11/16
 */
public class TaskIOTableDialog extends JDialog {

    public TaskIOTableDialog() {
         super(YAWLEditor.getInstance());
         setContentPane(getContent());
         setResizable(true);
         setTitle("Task-Variable I/O Matrix");
         setDefaultCloseOperation(DISPOSE_ON_CLOSE);
         pack();
         setLocationRelativeTo(YAWLEditor.getInstance());
     }


    public JPanel getContent() {
        JPanel panel = new JPanel(new BorderLayout());
        JTable table = new JHorizontalFriendlyTable();
        table.setModel(new TaskIOTableModel());
        setHeaderRenderer(table);

        panel.add(layoutTable(table), BorderLayout.CENTER);
        panel.add(getButtonBar(), BorderLayout.SOUTH);
        return panel;
    }


    private JPanel getButtonBar() {
        JPanel panel = new JPanel();
        panel.setBorder(new EmptyBorder(10, 0, 10, 0));
        panel.add(ButtonUtil.createButton("Done", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        }));
        return panel;
    }


    private void setHeaderRenderer(JTable table) {
        TableCellRenderer headerRenderer = new VerticalTableHeaderCellRenderer();
        Enumeration<TableColumn> columns = table.getColumnModel().getColumns();
        while (columns.hasMoreElements()) {
            TableColumn col = columns.nextElement();
            col.setHeaderRenderer(headerRenderer);
            col.setMaxWidth(24);
            col.setResizable(false);
        }
    }


    private JScrollPane layoutTable(JTable table) {
        table.setRowHeight(table.getRowHeight() + 5);
        JScrollPane scrollPane = new JScrollPane(table);
        JTable rowTable = new RowHeaderTable(table);
        scrollPane.setRowHeaderView(rowTable);
        scrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER, rowTable.getTableHeader());
        return scrollPane;
    }


    class JHorizontalFriendlyTable extends JAlternatingRowColorTable {

        @Override
        public Dimension getPreferredSize() {
            if (getParent () instanceof JViewport) {
                if (getParent().getWidth() > super.getPreferredSize().width) {
                    return getMinimumSize();
                }
            }
            return super.getPreferredSize();
        }

        @Override
        public boolean getScrollableTracksViewportWidth () {
            if (autoResizeMode != AUTO_RESIZE_OFF) {
                if (getParent() instanceof JViewport) {
                    return (getParent().getWidth() > getPreferredSize().width);
                }
                return true;
            }
            return false;
        }
    }

}
