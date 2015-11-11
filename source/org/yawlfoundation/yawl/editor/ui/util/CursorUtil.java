package org.yawlfoundation.yawl.editor.ui.util;

import org.yawlfoundation.yawl.editor.ui.YAWLEditor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;

/**
 * @author Michael Adams
 * @date 11/11/2015
 */
public class CursorUtil {

    private static final MouseAdapter _mouseAdapter =  new MouseAdapter() {};

    private CursorUtil() {}

    /** Sets cursor for specified component to Wait cursor */
    public static void showWaitCursor() {
      RootPaneContainer root = YAWLEditor.getInstance();
      root.getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      root.getGlassPane().addMouseListener(_mouseAdapter);
      root.getGlassPane().setVisible(true);
    }

    /** Sets cursor for specified component to normal cursor */
    public static void showDefaultCursor() {
      RootPaneContainer root = YAWLEditor.getInstance();
      root.getGlassPane().setCursor(Cursor.getDefaultCursor());
      root.getGlassPane().removeMouseListener(_mouseAdapter);
      root.getGlassPane().setVisible(false);
    }

}
