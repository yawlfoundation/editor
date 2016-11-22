package org.yawlfoundation.yawl.views.graph;

import org.yawlfoundation.yawl.editor.ui.YAWLEditor;
import org.yawlfoundation.yawl.editor.ui.swing.JAlternatingRowColorTable;
import org.yawlfoundation.yawl.editor.ui.swing.MessageDialog;
import org.yawlfoundation.yawl.editor.ui.util.ButtonUtil;
import org.yawlfoundation.yawl.editor.ui.util.SplitPaneUtil;
import org.yawlfoundation.yawl.views.ontology.Triple;
import prefuse.data.Graph;
import prefuse.data.io.GraphMLReader;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
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
import java.util.Set;

/**
 * @author Michael Adams
 * @date 4/11/16
 */
public abstract class AbstractViewDialog extends JDialog implements TableModelListener {

    private java.util.List<Triple> _triples;
    private JPanel _viewPanel;


    public AbstractViewDialog() {
        super(YAWLEditor.getInstance());
        loadTriples();
        setContentPane(getContent(toInputStream(_triples)));
        setResizable(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
        setLocationRelativeTo(YAWLEditor.getInstance());
    }


    abstract JPanel getFilterPanel();

    abstract void filter(SelectionTableModel model);

    abstract boolean isFilterMatch(Triple triple, Set<String> selected);

    abstract boolean loadTriples();


    public void setTriples(java.util.List<Triple> triples) {
        if (_triples != null) refreshView(triples);
        _triples = triples;
    }


    @Override
    public void tableChanged(TableModelEvent e) {
        filter((SelectionTableModel) e.getSource());
    }


    protected java.util.List<Triple> getTriples() { return _triples; }


    protected void refreshView(java.util.List<Triple> triples) {
        _viewPanel.removeAll();
        if (! triples.isEmpty()) {
            JComponent view = getView(toInputStream(triples));
            _viewPanel.add(view, BorderLayout.CENTER);
        }
        _viewPanel.invalidate();
        _viewPanel.repaint();
    }


    protected InputStream toInputStream(java.util.List<Triple> triples) {
        if (triples == null) {
            return null;
        }
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
        panel.add(ButtonUtil.createButton("Close", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        }));
        return panel;
    }


    private void createViewPanel() {
        _viewPanel = new JPanel(new BorderLayout());
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


    protected JComponent getView(InputStream is) {
        if (is != null) {
            try {
                Graph g = new GraphMLReader().readGraph(is);
                return new RadialGraphView(g, "name");
            }
            catch (Exception e) {
                // e.printStackTrace();
            }
        }
        return new JPanel();
    }


    protected java.util.List<Triple> filter(java.util.List<Triple> triples,
                                            SelectionTableModel model) {
        Set<String> selected = model.getSelected();
        if (model.getRowCount() == selected.size()) {    // all selected
            return triples;
        }

        java.util.List<Triple> filtered = new ArrayList<Triple>();
        for (Triple triple : getTriples()) {
            if (isFilterMatch(triple, selected)) {
                filtered.add(triple);
            }
        }
        return filtered;
    }



    protected JTable createTable(Set<String> values) {
        JTable table = new JAlternatingRowColorTable(new SelectionTableModel(values));
        table.getModel().addTableModelListener(this);
        table.getColumnModel().getColumn(0).setMaxWidth(50);
        table.setTableHeader(null);
        return table;
    }


    protected SelectionTableModel getTableModel(JTable table) {
        return (SelectionTableModel) table.getModel();
    }


    protected JPanel getTablePanel(JTable table, String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder(title));
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(getSelectAllCBox(table), BorderLayout.SOUTH);
        return panel;
    }


    private JCheckBox getSelectAllCBox(final JTable table) {
        final JCheckBox checkBox = new JCheckBox("Select All/None");
        checkBox.setSelected(true);
        checkBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getTableModel(table).selectAll(checkBox.isSelected());
            }
        });
        return checkBox;
    }

}
