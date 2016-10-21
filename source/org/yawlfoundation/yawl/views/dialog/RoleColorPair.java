package org.yawlfoundation.yawl.views.dialog;

import java.awt.*;

/*************************************************************/

class RoleColorPair implements Comparable<RoleColorPair> {
    private String roleName;
    private Color color;

    RoleColorPair(String r, Color c) {
        roleName = r;
        color = c;
    }

    public String getRole() {
        return roleName;
    }

    public Color getColor() {
        return color;
    }


    @Override
    public int compareTo(RoleColorPair other) {
        return getRole().compareTo(other.getRole());
    }
}
