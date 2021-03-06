/*
 * Copyright (c) 2004-2014 The YAWL Foundation. All rights reserved.
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

package org.yawlfoundation.yawl.editor.ui.properties.data;

import org.yawlfoundation.yawl.editor.core.data.YDataHandler;

/**
 * @author Michael Adams
 * @date 28/02/2014
 */
public class TaskVariableTableModel extends NetVariableTableModel {

    public TaskVariableTableModel() {
        super();
    }


    public String getColumnName(int column) {
        return column == VALUE_COLUMN ? "Default Output Value" : super.getColumnName(column);
    }


    public void addRow() { super.addRow(YDataHandler.INPUT_OUTPUT); }


    public boolean allRowsValid() {
        if (getVariables() != null) {
            for (VariableRow row : getVariables()) {
                if (! row.isValid()) return false;
            }
        }
        return true;
    }

}
