package org.yawlfoundation.yawl.worklet.graph;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Michael Adams
 * @date 2/02/2016
 */
public class IconLoader {

    public static Icon getIcon(String iconName) {
        InputStream is = IconLoader.class.getResourceAsStream("icon/" + iconName + ".png");
        if (is != null) {
            try {
                return  new ImageIcon(ImageIO.read(is));
            }
            catch (IOException ignore) {
                //
            }
        }
        return null;
    }

}
