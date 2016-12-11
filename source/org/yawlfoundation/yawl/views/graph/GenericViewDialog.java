package org.yawlfoundation.yawl.views.graph;

import org.yawlfoundation.yawl.editor.ui.YAWLEditor;
import org.yawlfoundation.yawl.editor.ui.swing.MessageDialog;
import org.yawlfoundation.yawl.editor.ui.util.ButtonUtil;
import org.yawlfoundation.yawl.views.ontology.Triple;
import prefuse.data.Graph;
import prefuse.data.io.GraphMLReader;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * @author Michael Adams
 * @date 4/11/16
 */
public class GenericViewDialog extends JDialog {

    private java.util.List<Triple> _triples;
    private JPanel _viewPanel;


    public GenericViewDialog(java.util.List<Triple> triples) {
        super(YAWLEditor.getInstance());
        _triples = triples;
        setContentPane(getContent(toInputStream(_triples)));
        setResizable(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
        setLocationRelativeTo(YAWLEditor.getInstance());
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
        createViewPanel();
        _viewPanel.add(getView(is));
        panel.add(_viewPanel, BorderLayout.CENTER);
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

}
