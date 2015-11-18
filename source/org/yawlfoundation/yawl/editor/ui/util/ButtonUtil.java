package org.yawlfoundation.yawl.editor.ui.util;

import javax.swing.*;
import java.awt.*;

/**
 * @author Michael Adams
 * @date 11/11/2015
 */
public class ButtonUtil {

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
            for (Component component : panel.getComponents()) {
                if (component instanceof JButton && !component.equals(widestButton)) {
                    component.setPreferredSize(widestButton.getPreferredSize());
                }
            }
        }
    }
}
