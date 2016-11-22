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

package org.yawlfoundation.yawl.editor.ui.properties.dialog.component;

import org.yawlfoundation.yawl.editor.ui.util.ButtonUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionListener;

/**
 * @author Michael Adams
 * @date 2/06/2014
 */
public class ButtonBar extends JPanel {

    private JButton _btnOK;
    private JButton _btnCancel;


    public ButtonBar(ActionListener listener) {
        setBorder(new EmptyBorder(10, 0, 10, 0));
        _btnCancel = ButtonUtil.createButton("Cancel", listener);
        add(_btnCancel);
        _btnOK = ButtonUtil.createButton("OK", listener);
        _btnOK.setEnabled(false);
        add(_btnOK);
    }


    public JButton getOK() { return _btnOK; }

    public JButton getCancel() { return _btnCancel; }

    public void setOKEnabled(boolean enable) { _btnOK.setEnabled(enable); }

    public boolean isOKEnabled() { return _btnOK.isEnabled(); }


    public JButton addButton(String label, ActionListener listener) {
        JButton btn = ButtonUtil.createButton(label, listener);
        add(btn);
        return btn;
    }

}
