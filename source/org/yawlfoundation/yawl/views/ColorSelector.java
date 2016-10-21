package org.yawlfoundation.yawl.views;


import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Michael Adams
 * @date 18/10/2016
 */
public class ColorSelector {

    // Color palette based on designs developed by Cynthia Brewer
    // (http://colorbrewer2.org/).
    private static final List<Color> COLOR_LIST = Arrays.asList(
            new Color(141, 211, 199),
            new Color(255, 255, 179),
            new Color(190, 186, 218),
            new Color(251, 128, 114),
            new Color(128, 177, 211),
            new Color(253, 180, 98),
            new Color(179, 222, 105),
            new Color(252, 205, 229),
            new Color(217, 217, 217),
            new Color(188, 128, 189),
            new Color(204, 235, 197),
            new Color(255, 237, 111)
    );


    public static List<Color> get(int size) {
        if (size <= COLOR_LIST.size()) {
            return COLOR_LIST.subList(0, size);
        }
        List<Color> colors = new ArrayList<Color>(size);

        // blah
        return colors;
    }

}
