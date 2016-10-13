package org.yawlfoundation.yawl.editor.ui.net;

import java.awt.*;

/**
 * Called when painting a net on the canvas, to allow overlaying of views
 *
 * @author Michael Adams
 * @date 12/10/2016
 */
public interface OverlaidView {

    void paint(Graphics g, Color canvasBackground);

}
