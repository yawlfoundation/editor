package org.yawlfoundation.yawl.worklet.dialog;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * @author Michael Adams
 * @date 4/12/2015
 */
public class ButtonPanel extends JPanel {


    public ButtonPanel() {
        super();
        setBorder(new EmptyBorder(5,5,10,5));
    }


    public JButton addButton(String caption, ActionListener listener) {
        JButton button = new JButton(caption);
        button.setActionCommand(caption);
        button.setPreferredSize(new Dimension(90, 25));
        button.addActionListener(listener);
        return (JButton) add(button);
    }

}
