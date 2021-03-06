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

package org.yawlfoundation.yawl.editor.ui.resourcing;

import org.yawlfoundation.yawl.editor.ui.resourcing.tablemodel.AbstractResourceTableModel;
import org.yawlfoundation.yawl.editor.ui.swing.JSingleSelectTable;

import javax.swing.*;
import java.awt.*;

/**
* @author Michael Adams
* @date 3/08/12
*/
public class ResourceTable extends JSingleSelectTable {


    public ResourceTable(AbstractResourceTableModel model) {
        super();
        init(model);
    }


    public ResourceTable(AbstractResourceTableModel model, int rows) {
        super(rows);
        init(model);
    }


    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        setBackground(enabled ? Color.WHITE :
                UIManager.getDefaults().getColor("TextArea.inactiveBackground"));
        ((AbstractResourceTableModel) getModel()).setEnabled(enabled);
    }


    private void init(AbstractResourceTableModel model) {
        setModel(model);
        setRowHeight(getRowHeight() + 5);
        setColumnSelectionAllowed(false);
        setTableHeader(null);
        setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);  // override
        setFillsViewportHeight(true);            // to allow drops on empty table
    }

}
