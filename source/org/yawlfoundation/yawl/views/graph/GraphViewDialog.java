package org.yawlfoundation.yawl.views.graph;

import org.yawlfoundation.yawl.editor.ui.YAWLEditor;
import prefuse.data.Graph;
import prefuse.data.io.GraphMLReader;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;

/**
 * @author Michael Adams
 * @date 4/11/16
 */
public class GraphViewDialog extends JDialog {

    public GraphViewDialog(InputStream is) {
        super(YAWLEditor.getInstance());
        setContentPane(getContent(is));
        setResizable(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
        setLocationRelativeTo(YAWLEditor.getInstance());
    }


    private JPanel getContent(InputStream is) {
        JPanel panel = new JPanel(new BorderLayout());
        try {
            Graph g = new GraphMLReader().readGraph(is);
            RadialGraphView view = new RadialGraphView(g, "name");
            panel.add(view, BorderLayout.CENTER);
            panel.add(getButtonBar(), BorderLayout.SOUTH);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
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


}
