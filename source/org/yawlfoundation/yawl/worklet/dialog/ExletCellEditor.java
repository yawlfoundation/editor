/*
 * Copyright (c) 2004-2015 The YAWL Foundation. All rights reserved.
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

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

/**
 * @author Michael Adams
 * @date 30/09/2014
 */
public abstract class ExletCellEditor extends DefaultCellEditor {

    protected JComboBox _combo;


    public ExletCellEditor(CellEditorListener listener) {
        super(new JTextField());
        addCellEditorListener(listener);
        setClickCountToStart(1);
    }


    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row,
                                                 int column) {
        ((ConclusionTable) table).editingStarted();
        return super.getTableCellEditorComponent(table, value, isSelected, row, column);
    }


    protected JComboBox newComboInstance(JTable table, Object value) {
        _combo = buildCombo((ConclusionTable) table, value);
        return _combo;
    }


    protected JComboBox buildCombo(ConclusionTable table, Object value) {
        final JComboBox combo = new JComboBox(getItemsForContext(table));
        combo.setSelectedItem(value);

        combo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fireEditingStopped();
            }
        });

        combo.addPopupMenuListener(new PopupMenuListener() {
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) { }

            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) { }

            public void popupMenuCanceled(PopupMenuEvent e) {
                fireEditingCanceled();
            }
        });

        combo.setRenderer(new ListCellRenderer() {
            DefaultListCellRenderer renderer = new DefaultListCellRenderer();

            public Component getListCellRendererComponent(JList jList, Object o,
                                                          int i, boolean b, boolean b1) {
                JLabel label = (JLabel) renderer.getListCellRendererComponent(jList, o, i, b, b1);
                label.setText(o.toString());
                return label;
            }
        });

        return combo;
    }


    protected abstract Vector<?> getItemsForContext(ConclusionTable table);

}
