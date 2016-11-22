package org.yawlfoundation.yawl.editor.ui.util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * @author Michael Adams
 * @date 11/11/2015
 */
public class ButtonUtil {

    private static final int PREFERRED_HEIGHT = 25;

    public static void setEqualWidths(JPanel panel) {
        Component widestButton = null;
        for (Component component : panel.getComponents()) {
            if (component instanceof JButton) {
                if (widestButton == null ||
                        component.getPreferredSize().getWidth() >
                                widestButton.getPreferredSize().getWidth()) {
                    widestButton = component;
                }
            }
        }

        if (widestButton != null) {
            Dimension correctedSize = new Dimension(
                    widestButton.getPreferredSize().width, PREFERRED_HEIGHT);
            for (Component component : panel.getComponents()) {
                if (component instanceof JButton) {
                    component.setPreferredSize(correctedSize);
                }
            }
        }
    }


    public static JButton createButton(String label, ActionListener listener) {
        JButton button = new JButton(label);
        button.setActionCommand(label);
        button.setMnemonic(label.charAt(0));
        button.setPreferredSize(new Dimension(70, PREFERRED_HEIGHT));
        button.addActionListener(listener);
        return button;
    }

}
