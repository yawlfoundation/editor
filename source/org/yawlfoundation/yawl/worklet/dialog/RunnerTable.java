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

package org.yawlfoundation.yawl.worklet.dialog;

import org.yawlfoundation.yawl.editor.ui.swing.JSingleSelectTable;
import org.yawlfoundation.yawl.worklet.selection.WorkletRunner;

/**
 * @author Michael Adams
 * @date 30/09/2014
 */
public class RunnerTable extends JSingleSelectTable {

    public RunnerTable() {
        super();
        setModel(new RunnerTableModel());
        setRowHeight(getRowHeight() + 5);
        setCellSelectionEnabled(false);
        setRowSelectionAllowed(true);
        setColumnSelectionAllowed(false);
        setFillsViewportHeight(true);            // to allow drops on empty table
    }


    public WorkletRunner getSelection() {
        return getTableModel().getRunner(getSelectedRow());
    }


    public RunnerTableModel getTableModel() {
        return (RunnerTableModel) getModel();
    }

}
