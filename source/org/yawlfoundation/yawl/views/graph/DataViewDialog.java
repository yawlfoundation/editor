package org.yawlfoundation.yawl.views.graph;

import org.yawlfoundation.yawl.editor.ui.YAWLEditor;
import org.yawlfoundation.yawl.editor.ui.swing.JAlternatingRowColorTable;
import org.yawlfoundation.yawl.editor.ui.swing.MessageDialog;
import org.yawlfoundation.yawl.editor.ui.util.SplitPaneUtil;
import org.yawlfoundation.yawl.views.ontology.Triple;
import prefuse.data.Graph;
import prefuse.data.io.GraphMLReader;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Michael Adams
 * @date 4/11/16
 */
public class DataViewDialog extends JDialog implements TableModelListener {

    private java.util.List<Triple> _triples;
    private DataViewTableModel _tableModel;
    private JPanel _viewPanel;

    public DataViewDialog(java.util.List<Triple> triples) {
        super(YAWLEditor.getInstance());
        _triples = triples;
        setTitle("Task Data I/O Graph");
        setContentPane(getContent(load(triples)));
        setResizable(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
        setLocationRelativeTo(YAWLEditor.getInstance());
    }


    @Override
    public void tableChanged(TableModelEvent e) {
        filter();
    }

    private InputStream load(java.util.List<Triple> triples) {
        GraphMLWriter writer = new GraphMLWriter();
        try {
            return writer.toInputStream(triples);
        }
        catch (UnsupportedEncodingException uee) {
            MessageDialog.error("Failed to load Triples: " + uee.getMessage(),
                    "Graph Load Failure");
            return null;
        }
    }


    private JPanel getContent(InputStream is) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(0,5,0,5));
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
        SplitPaneUtil splitPaneUtil = new SplitPaneUtil();
        splitPaneUtil.setupDivider(splitPane, false);
        splitPane.setLeftComponent(getFilterPanel());
        splitPaneUtil.setDividerLocation(splitPane,0.3);
        createViewPanel();
        _viewPanel.add(getView(is));
        splitPane.setRightComponent(_viewPanel);
        panel.add(splitPane, BorderLayout.CENTER);
        panel.add(getButtonBar(), BorderLayout.SOUTH);
        return panel;
    }



    private JPanel getButtonBar() {
        JPanel panel = new JPanel();
        panel.setBorder(new EmptyBorder(10, 0, 10, 0));
        panel.add(createButton("Close", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        }));
        return panel;
    }


    private JButton createButton(String label, ActionListener listener) {
        JButton button = new JButton(label);
        button.setActionCommand(label);
        button.setMnemonic(label.charAt(0));
        button.setPreferredSize(new Dimension(70, 25));
        button.addActionListener(listener);
        return button;
    }


    private void createViewPanel() {
        _viewPanel = new JPanel();
        _viewPanel.setBackground(Color.WHITE);
        _viewPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                for (Component c: _viewPanel.getComponents()) {
                    c.setSize(_viewPanel.getSize());
                }
            }
        });
    }


    private JScrollPane getFilterPanel() {
        _tableModel = new DataViewTableModel(getVars());
        JTable table = new JAlternatingRowColorTable(_tableModel);
        _tableModel.addTableModelListener(this);
        table.getColumnModel().getColumn(0).setMaxWidth(50);
//        table.getColumnModel().getColumn(0).setPreferredWidth(150);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.add(table, BorderLayout.CENTER);
        panel.add(getSelectAllCBox(), BorderLayout.SOUTH);
        return new JScrollPane(panel);
    }


    private JCheckBox getSelectAllCBox() {
        final JCheckBox checkBox = new JCheckBox("Select All/None");
        checkBox.setSelected(true);
        checkBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                _tableModel.selectAll(checkBox.isSelected());
            }
        });
        return checkBox;
    }


    private JComponent getView(InputStream is) {
        if (is != null) {
            try {
                Graph g = new GraphMLReader().readGraph(is);
                return new RadialGraphView(g, "name");
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new JPanel();
    }


    private Set<String> getVars() {
        Set<String> vars = new HashSet<String>();
        for (Triple triple : _triples) {
            String subject = triple.getSubject();
            String object = triple.getObject();
            vars.add(subject.startsWith("TASK:") ? object : subject);
        }
        return vars;
    }


    private void filter() {
        java.util.List<Triple> filtered = new ArrayList<Triple>();
        Set<String> selected = _tableModel.getSelected();
        for (Triple triple : _triples) {
            if (selected.contains(triple.getSubject()) ||
                    selected.contains(triple.getObject())) {
                filtered.add(triple);
            }
        }
        _viewPanel.removeAll();
        if (! filtered.isEmpty()) {
            JComponent view = getView(load(filtered));
            view.setSize(_viewPanel.getSize());
            _viewPanel.add(view);
        }
        _viewPanel.invalidate();
        _viewPanel.repaint();
    }

}
