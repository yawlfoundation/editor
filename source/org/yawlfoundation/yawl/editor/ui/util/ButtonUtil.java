package org.yawlfoundation.yawl.editor.ui.util;

import javax.swing.*;
import java.awt.*;

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
}
