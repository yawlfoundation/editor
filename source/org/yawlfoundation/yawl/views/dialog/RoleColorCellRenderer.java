package org.yawlfoundation.yawl.views.dialog;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * @author Michael Adams
 * @date 20/10/2016
 */
public class RoleColorCellRenderer extends DefaultTableCellRenderer {

    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int col) {

        JLabel cell = (JLabel) super.getTableCellRendererComponent(table, value,
                isSelected, hasFocus, row, col);

        RoleColorPair roleColor = (RoleColorPair) value;
        cell.setBackground(roleColor.getColor());
        cell.setForeground(getContrastingForeground(roleColor.getColor()));
        cell.setText(roleColor.getRole());

        return cell;
    }


    // based on http://stackoverflow.com/questions/1855884/
    // determine-font-color-based-on-background-color
    private Color getContrastingForeground(Color background) {
        int d = 0;

        // Determine the perceptive luminance
        double luminance = 1 - (0.299 * background.getRed() +
                0.587 * background.getGreen() + 0.114 * background.getBlue()) / 255;

        return luminance < 0.5 ? Color.BLACK : Color.WHITE;
    }

}
