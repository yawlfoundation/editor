package org.yawlfoundation.yawl.views.util;


import java.awt.*;
import java.util.List;
import java.util.*;

/**
 * @author Michael Adams
 * @date 18/10/2016
 */
public class ColorSelector {

    // Static color palette based on a set from http://colorbrewer2.org/
    private static final List<Color> STATIC_LIST = Arrays.asList(
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

    // in case we need more than 12
    private static List<Color> _colors = new ArrayList<Color>(STATIC_LIST);


    // returns a list of colors of the requested size
    public static List<Color> get(int size) {
        return size <= _colors.size() ? _colors.subList(0, size) : generate(_colors, size);
    }


    public static List<Color> get(int size, List<Color> exceptionSet) {
        List<Color> colorList = new ArrayList<>(size);
        for (Color candidate : _colors) {
            if (!isSimilarToExisting(exceptionSet, candidate)) {
                colorList.add(candidate);
                if (colorList.size() == size) {
                    return colorList;
                }
            }
        }
        List<Color> combinedList = new ArrayList<>(exceptionSet);
        combinedList.addAll(colorList);
        int leftToGet = size - colorList.size();
        for (int i = 0; i < leftToGet; i++) {
             Color color = nextColor(combinedList);
             colorList.add(color);
             combinedList.add(color);
         }
         return colorList;
    }


    private static List<Color> generate(List<Color> colors, int size) {
        for (int i = colors.size(); i <= size; i++) {
            colors.add(nextColor(colors));
        }
        return colors;
    }


    private static Color nextColor(List<Color> colors) {
        Random random = new Random();
        Color next = null;
        boolean similar = true;     // init the loop
        while (similar) {

            // create a pastel color
            float hue = random.nextFloat();
            float luminance = 0.9f;
            float saturation = (random.nextInt(2000) + 1000) / 10000f;    // 0.1f to 0.3f
            next = Color.getHSBColor(hue, saturation, luminance);

            similar = isSimilarToExisting(colors, next);
        }
        return next;
    }


    // simple check to ensure not too similar to existing color
    private static boolean isSimilarToExisting(List<Color> colors, Color c) {
        for (Color existing : colors) {
            int red = Math.abs(c.getRed() - existing.getRed());
            int green = Math.abs(c.getGreen() - existing.getGreen());
            int blue = Math.abs(c.getBlue() - existing.getBlue());
            int threshold = 15;
            if (red < threshold && green < threshold && blue < threshold) {
                return true;
            }
        }
        return false;
    }

}
