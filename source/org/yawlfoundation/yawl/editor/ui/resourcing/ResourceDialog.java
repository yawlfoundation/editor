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

import org.yawlfoundation.yawl.editor.core.resourcing.YResourceHandler;
import org.yawlfoundation.yawl.editor.ui.YAWLEditor;
import org.yawlfoundation.yawl.editor.ui.elements.model.YAWLTask;
import org.yawlfoundation.yawl.editor.ui.plugin.YPluginHandler;
import org.yawlfoundation.yawl.editor.ui.resourcing.panel.PrimaryResourcesPanel;
import org.yawlfoundation.yawl.editor.ui.resourcing.panel.SecondaryResourcesPanel;
import org.yawlfoundation.yawl.editor.ui.resourcing.panel.TaskPrivilegesPanel;
import org.yawlfoundation.yawl.editor.ui.specification.SpecificationModel;
import org.yawlfoundation.yawl.editor.ui.util.ButtonUtil;
import org.yawlfoundation.yawl.elements.YAtomicTask;
import org.yawlfoundation.yawl.elements.YNet;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * @author Michael Adams
 * @date 2/08/12
 */
public class ResourceDialog extends JDialog
        implements ActionListener, ItemListener, TableModelListener {

    private final YAtomicTask task;
    private PrimaryResourcesPanel primaryResourcesPanel;
    private SecondaryResourcesPanel secondaryResourcesPanel;
    private TaskPrivilegesPanel taskPrivilegesPanel;
    private JButton btnApply;
    private YNet net;


    public ResourceDialog(YNet net, YAWLTask task) {
        super();
        initialise(net);
        this.task = getTask(task.getID());
        setTitle("Resources for Task " + task.getID());
        add(getContent());
        setPreferredSize(new Dimension(780, 700));
        setMinimumSize(new Dimension(630, 635));
        pack();
        setLocationRelativeTo(YAWLEditor.getInstance());
    }


    public void actionPerformed(ActionEvent event) {
        String action = event.getActionCommand();
        if (event.getSource() instanceof JComboBox) {
            enableApplyButton(true);
        }
        else {
            if (action.equals("Refresh")) {
                SpecificationModel.getHandler().getResourceHandler().resetCache();
            }

            // if OK or Apply clicked, and there is data changes to save
            else if (!action.equals("Cancel") && btnApply.isEnabled()) {
                updateTaskResources();
                enableApplyButton(false);
            }

            if (action.equals("OK") || action.equals("Cancel")) {
                setVisible(false);
            }
        }
    }

    public void tableChanged(TableModelEvent tableModelEvent) {
        enableApplyButton(true);
    }

    public void itemStateChanged(ItemEvent e) {
        enableApplyButton(true);
    }

    public YAtomicTask getTask() { return task; }

    public String getInteractionString() {
        return primaryResourcesPanel.getInteractionString();
    }


    private void enableApplyButton(boolean enable) {
        if (btnApply != null) btnApply.setEnabled(enable);
    }

    private YAtomicTask getTask(String name) {
        return (YAtomicTask) net.getNetElement(name);
    }


    private void initialise(YNet net) {
        this.net = net;
        setModal(true);
        setResizable(true);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    }


    private JPanel getContent() {
        primaryResourcesPanel = new PrimaryResourcesPanel(net, task, this);
        secondaryResourcesPanel = new SecondaryResourcesPanel(net, task, this);
        taskPrivilegesPanel = new TaskPrivilegesPanel(net, task, this);

        JTabbedPane pane = new JTabbedPane();
        pane.addTab("Primary Resources", primaryResourcesPanel);
        pane.addTab("Secondary Resources", secondaryResourcesPanel);
        pane.addTab("Task Privileges", taskPrivilegesPanel);

        JPanel content = new JPanel(new BorderLayout());
        content.add(pane, BorderLayout.CENTER);
        content.add(createButtonBar(), BorderLayout.SOUTH);
        return content;
    }


    private JPanel createButtonBar() {
        JPanel innerPanel = new JPanel();
        innerPanel.setBorder(new EmptyBorder(10,0,10,0));
        innerPanel.add(ButtonUtil.createButton("Cancel", this));
        btnApply = ButtonUtil.createButton("Apply", this);
        btnApply.setEnabled(false);
        innerPanel.add(btnApply);
        innerPanel.add(ButtonUtil.createButton("OK", this));
        
        JPanel refreshPanel = new JPanel();
        JButton btnRefresh = ButtonUtil.createButton("refresh","Refresh", 25, this);
        btnRefresh.setBorderPainted(false);
        btnRefresh.setToolTipText("Refresh resource data");
        refreshPanel.add(btnRefresh);

        JPanel outerPanel = new JPanel(new BorderLayout());
        outerPanel.add(refreshPanel, BorderLayout.EAST);
        outerPanel.add(innerPanel, BorderLayout.CENTER);
        return outerPanel;
    }


    private void updateTaskResources() {
        primaryResourcesPanel.save();
        secondaryResourcesPanel.save();
        taskPrivilegesPanel.save();
        finaliseUpdate();
    }


    protected void finaliseUpdate() {
        YResourceHandler resHandler = SpecificationModel.getHandler().getResourceHandler();
        resHandler.getOrCreateTaskResources(net.getID(), task.getID()).setTaskXML();
        YPluginHandler.getInstance().resourcingChanged(task);
    }

}


