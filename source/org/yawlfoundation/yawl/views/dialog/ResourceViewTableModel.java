/*
 * Copyright (c) 2004-2013 The YAWL Foundation. All rights reserved.
 * The YAWL Foundation is a collaboration of individuals and
 * organisations who are committed to improving workflow technology.
 *
 * This file is part of YAWL. YAWL is free software: you can
 * redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation.
 *
 * YAWL is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General
 * Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with YAWL. If not, see <http://www.gnu.org/licenses/>.
 */

package org.yawlfoundation.yawl.views.dialog;

import org.yawlfoundation.yawl.editor.core.resourcing.ResourceDataSet;
import org.yawlfoundation.yawl.resourcing.resource.Role;

import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Michael Adams
 * @date 16/05/13
 */
public class ResourceViewTableModel extends AbstractTableModel {

    private List<RoleColorPair> _roleColors;

    public ResourceViewTableModel() {
        super();
    }

    public ResourceViewTableModel(Map<String, Color> roleColors) {
        this();
        setValues(roleColors);
    }

    public int getRowCount() {
        return _roleColors != null ? _roleColors.size() : 0;
    }

    public int getColumnCount() {
        return 1;
    }

    public Object getValueAt(int row, int col) {
        return _roleColors.get(row);
//        switch (col) {
//            case 0: return _roleColors.get(row).getColor();
//            case 1: return _roleColors.get(row).getRole();
//        }
//        return null;
    }

    public void setValues(Map<String, Color> roleColors) {
        _roleColors = new ArrayList<RoleColorPair>();
        for (String roleID : roleColors.keySet()) {
            Role role = ResourceDataSet.getRole(roleID);
            String roleName = role != null ? role.getName() : "unknown";
            _roleColors.add(new RoleColorPair(roleName, roleColors.get(roleID)));
        }
        Collections.sort(_roleColors);
        fireTableDataChanged();
    }

}

