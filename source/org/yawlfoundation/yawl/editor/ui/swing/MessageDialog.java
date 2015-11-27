package org.yawlfoundation.yawl.editor.ui.swing;

import org.yawlfoundation.yawl.editor.ui.YAWLEditor;

import javax.swing.*;
import java.awt.*;

/**
 * @author Michael Adams
 * @date 27/11/2015
 */
public class MessageDialog {

    public static void info(String message, String title) {
        info(YAWLEditor.getInstance(), message, title);
    }


    public static void warn(String message, String title) {
        warn(YAWLEditor.getInstance(), message, title);
    }


    public static void error(String message, String title) {
        error(YAWLEditor.getInstance(), message, title);
    }


    public static void info(Component component, String message, String title) {
        JOptionPane.showMessageDialog(component, message, title,
                JOptionPane.INFORMATION_MESSAGE);
    }


    public static void warn(Component component, String message, String title) {
        JOptionPane.showMessageDialog(component, message, title,
                JOptionPane.WARNING_MESSAGE);
    }


    public static void error(Component component, String message, String title) {
        JOptionPane.showMessageDialog(component, message, title,
                JOptionPane.ERROR_MESSAGE);
    }




}
