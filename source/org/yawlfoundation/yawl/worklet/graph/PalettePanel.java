package org.yawlfoundation.yawl.worklet.graph;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Enumeration;

/**
 * @author Michael Adams
 * @date 2/02/2016
 */
public class PalettePanel extends JPanel {

    private final ButtonGroup _group = new ButtonGroup();


    public PalettePanel() {
        super();
        setLayout(new GridLayout(5,3));
        setBorder(new EmptyBorder(5,5,0,5));
        addContent();
        setSelected(ExletActions.Select);
    }


    public ExletActions getSelected() {
        String cmd = _group.getSelection().getActionCommand();
        return ExletActions.valueOf(cmd);
    }


    public void setSelected(ExletActions action) {
        Enumeration<AbstractButton> e = _group.getElements();
        while (e.hasMoreElements()) {
            AbstractButton btn = e.nextElement();
            if (btn.getActionCommand().equals(action.name())) {
                btn.setSelected(true);
                break;
            }
        }
    }


    public void addPaletteListener(ActionListener listener) {
        Enumeration<AbstractButton> e = _group.getElements();
        while (e.hasMoreElements()) {
            e.nextElement().addActionListener(listener);
        }
    }


    private void addContent() {
        for (ExletActions a : ExletActions.values()) {
            JToggleButton btn = new JToggleButton(IconLoader.getIcon(a.name()));
            btn.setActionCommand(a.name());
            btn.setSize(32,32);
            btn.setOpaque(false);
            btn.setToolTipText(a.name());
            add(btn);
            _group.add(btn);
        }
    }

}
