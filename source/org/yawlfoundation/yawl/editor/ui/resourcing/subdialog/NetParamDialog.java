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

package org.yawlfoundation.yawl.editor.ui.resourcing.subdialog;

import org.yawlfoundation.yawl.editor.core.resourcing.DynParam;
import org.yawlfoundation.yawl.editor.ui.YAWLEditor;
import org.yawlfoundation.yawl.editor.ui.resourcing.ResourceDialog;
import org.yawlfoundation.yawl.editor.ui.util.ButtonUtil;
import org.yawlfoundation.yawl.elements.YAtomicTask;
import org.yawlfoundation.yawl.elements.data.YVariable;
import org.yawlfoundation.yawl.schema.XSDType;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * @author Michael Adams
 * @date 19/07/13
 */
public class NetParamDialog extends JDialog implements ActionListener {

    private JRadioButton _rbParticipant;
    private JRadioButton _rbRole;
    private JButton _btnOK;

    protected JComboBox<String> _varCombo;
    protected DynParam _selected;
    protected Dimension _size = new Dimension(270, 200);

    
    public NetParamDialog(ResourceDialog owner) {
        super(owner);
        setTitle("Add Net Parameter");
        initialise();
        add(getContent(owner.getTask()));
        setPreferredSize(_size);
        pack();
        setLocationRelativeTo(YAWLEditor.getInstance());
    }


    public void actionPerformed(ActionEvent event) {
        String action = event.getActionCommand();
        if (action.equals("Cancel")) {
            _selected = null;
        }
        else {
            DynParam.Refers refers = _rbParticipant.isSelected() ?
                    DynParam.Refers.Participant : DynParam.Refers.Role;
            _selected = new DynParam((String) _varCombo.getSelectedItem(), refers);
        }
        setVisible(false);
    }


    public void load(DynParam param) {
        _varCombo.setSelectedItem(param.getName());
        setSelectedButton(param);
        _selected = param;
    }


    public void removeItems(java.util.List<DynParam> params) {
        for (DynParam param : params) {
            _varCombo.removeItem(param.getName());
        }
    }


    public void loadForEdit(DynParam param) {
        load(param);
        setTitle("Edit Net Parameter");
        _varCombo.setEnabled(false);            // lock name so can't be changed
    }


    public DynParam getSelection() {
        return _selected;
    }

    protected void setDimension(Dimension size) { _size = size; }

    private void initialise() {
        setModal(true);
        setResizable(false);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    }

    protected JPanel getContent(YAtomicTask task) {
        JPanel content = new JPanel(new BorderLayout());
        content.setBorder(new EmptyBorder(5,5,5,5));
        content.add(createComboPanel(task), BorderLayout.NORTH);
        content.add(createRadioBox(), BorderLayout.CENTER);
        content.add(createButtonBar(), BorderLayout.SOUTH);
        enableOK();
        return content;
    }


    protected JPanel createComboPanel(YAtomicTask task) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(5, 5, 10, 5));
        _varCombo = new JComboBox<>();
        _varCombo.setPreferredSize(new Dimension(175, 25));
        addItems(task);
        _varCombo.setEnabled(_varCombo.getItemCount() > 0);
        panel.add(new JLabel("Parameter: "), BorderLayout.WEST);
        panel.add(_varCombo, BorderLayout.CENTER);
        panel.setSize(410, 25);
        return panel;
    }

    private JPanel createRadioBox() {
        JPanel panel = new JPanel(new GridLayout(0,1));
        panel.setBorder(new TitledBorder("Will refer to"));
        ButtonGroup btnGroup = new ButtonGroup();
        _rbParticipant = new JRadioButton("Participant");
        _rbParticipant.setSelected(true);
        _rbRole = new JRadioButton("Role");
        btnGroup.add(_rbParticipant);
        btnGroup.add(_rbRole);
        panel.add(_rbParticipant);
        panel.add(_rbRole);
        return panel;
    }


    protected JPanel createButtonBar() {
        JPanel panel = new JPanel();
        panel.setBorder(new EmptyBorder(10, 0, 10, 0));
        panel.add(ButtonUtil.createButton("Cancel", this));
        _btnOK = ButtonUtil.createButton("OK", this);
        panel.add(_btnOK);
        return panel;
     }


    private void setSelectedButton(DynParam param) {
        if (param.getRefers() == DynParam.Refers.Participant) {
            _rbParticipant.setSelected(true);
        }
        else _rbRole.setSelected(true);
    }


    private void addItems(YAtomicTask task) {

        // get all net-level input and local variables
        java.util.List<YVariable> variables = new ArrayList<YVariable>();
        variables.addAll(task.getNet().getInputParameters().values());
        variables.addAll(task.getNet().getLocalVariables().values());

        // sort on name
        Collections.sort(variables, new Comparator<YVariable>() {
            public int compare(YVariable v1, YVariable v2) {
                return v1.getPreferredName().compareTo(v2.getPreferredName());
            }
        });

        // add all those of 'string' type to the combo box
        for (YVariable variable : variables) {
            if (variable.getDataTypeName().equals(XSDType.getString(XSDType.STRING))) {
                _varCombo.addItem(variable.getPreferredName());
            }
        }
    }


    protected void enableOK() {
        _btnOK.setEnabled(_varCombo.getItemCount() > 0);
    }

}
