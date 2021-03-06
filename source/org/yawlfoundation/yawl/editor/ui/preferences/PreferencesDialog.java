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

package org.yawlfoundation.yawl.editor.ui.preferences;

import com.l2fprod.common.swing.JButtonBar;
import com.l2fprod.common.swing.plaf.misc.IconPackagerButtonBarUI;
import org.yawlfoundation.yawl.editor.ui.YAWLEditor;
import org.yawlfoundation.yawl.editor.ui.util.ButtonUtil;
import org.yawlfoundation.yawl.editor.ui.util.ResourceLoader;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * @author Michael Adams
 * @date 23/09/13
 */
public class PreferencesDialog extends JDialog
        implements ActionListener, CaretListener {

    private JPanel _mainPanel;
    private JPanel _currentPanel;
    private JButton _btnApply;
    private java.util.List<PreferencePanel> _contentPanels;


    public PreferencesDialog() {
        super(YAWLEditor.getInstance());
        init();
    }

    public void actionPerformed(ActionEvent event) {
        if (! (event.getSource() instanceof JButton)) {
            caretUpdate(null);
        }
        else {
            String action = event.getActionCommand();
            if (! action.equals("Cancel")) {
                for (PreferencePanel panel : _contentPanels) {
                    panel.applyChanges();
                }
                _btnApply.setEnabled(false);
            }
            if (! action.equals("Apply")) {
                setVisible(false);
            }
        }
    }

    public void caretUpdate(CaretEvent event) {
        if (_btnApply != null) _btnApply.setEnabled(true);
    }

    private void init() {
        _contentPanels = new ArrayList<PreferencePanel>();
        setModal(true);
        setResizable(true);
        setTitle("YAWL Editor Preferences");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        add(getContent());
        setPreferredSize(new Dimension(700, 600));
        pack();
        setLocationRelativeTo(YAWLEditor.getInstance());
    }


    private JPanel getContent() {
        _mainPanel = new JPanel(new BorderLayout());
        JButtonBar toolbar = new JButtonBar(JButtonBar.VERTICAL);
        toolbar.setUI(new IconPackagerButtonBarUI());
        toolbar.setPreferredSize(new Dimension(110, 400));
        _mainPanel.add(toolbar, BorderLayout.WEST);
        populateToolbar(toolbar);
        _mainPanel.add(getButtonBar(this), BorderLayout.SOUTH);
        return _mainPanel;
    }


    private void populateToolbar(JButtonBar toolbar) {
        ButtonGroup group = new ButtonGroup();
        addConnectionPreferences(toolbar, group);
        addAnalysisPreferences(toolbar, group);
        addFilePathPreferences(toolbar, group);
        addSaveOptionsPreferences(toolbar, group);
        addDefaultPreferences(toolbar, group);
    }


    private void showContent(JPanel content) {
        if (_currentPanel != null) {
            _mainPanel.remove(_currentPanel);
        }
        _currentPanel = content;
        _mainPanel.add(content, BorderLayout.CENTER);
        _mainPanel.revalidate();
        repaint();
     }


    private void addConnectionPreferences(JButtonBar toolbar, ButtonGroup group) {
        ConnectionsPanel connectionsPanel = new ConnectionsPanel(this);
        JToggleButton button = makeButton("Connections", "connect", connectionsPanel);
        toolbar.add(button);
        group.add(button);
        _contentPanels.add(connectionsPanel);

        // make this the first one shown
        button.setSelected(true);
        showContent(connectionsPanel);
    }

    private void addAnalysisPreferences(JButtonBar toolbar, ButtonGroup group) {
        AnalysisPanel analysisPanel = new AnalysisPanel(this, this);
         JToggleButton button = makeButton("Analysis", "analyze", analysisPanel);
         toolbar.add(button);
         group.add(button);
        _contentPanels.add(analysisPanel);
    }

    private void addFilePathPreferences(JButtonBar toolbar, ButtonGroup group) {
        FilePathPanel filePathPanel = new FilePathPanel(this);
        JToggleButton button = makeButton("File Paths", "folder-open-edit", filePathPanel);
        toolbar.add(button);
        group.add(button);
        _contentPanels.add(filePathPanel);
    }

    private void addSaveOptionsPreferences(JButtonBar toolbar, ButtonGroup group) {
        FileOptionsPanel fileOptionsPanel = new FileOptionsPanel(this);
        JToggleButton button = makeButton("Save Options", "saveOptions", fileOptionsPanel);
        toolbar.add(button);
        group.add(button);
        _contentPanels.add(fileOptionsPanel);
    }

    private void addDefaultPreferences(JButtonBar toolbar, ButtonGroup group) {
        DefaultsPanel defaultsPanel = new DefaultsPanel(this);
        JToggleButton button = makeButton("Defaults", "defaults", defaultsPanel);
        toolbar.add(button);
        group.add(button);
        _contentPanels.add(defaultsPanel);
    }

    private JToggleButton makeButton(String caption, String iconName,
                                     final JPanel content) {
        JToggleButton button = new JToggleButton(caption);
        button.setIcon(ResourceLoader.getMenuIcon(iconName));
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                showContent(content);
            }
        });
        return button;
    }

    protected JPanel getButtonBar(ActionListener listener) {
        JPanel panel = new JPanel();
        panel.setBorder(new EmptyBorder(5,5,10,5));
        panel.add(ButtonUtil.createButton("Cancel", listener));
        _btnApply = ButtonUtil.createButton("Apply", listener);
        _btnApply.setEnabled(false);
        panel.add(_btnApply);
        panel.add(ButtonUtil.createButton("OK", listener));
        return panel;
    }

}
