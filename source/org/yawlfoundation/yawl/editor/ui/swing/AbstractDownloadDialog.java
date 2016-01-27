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

package org.yawlfoundation.yawl.editor.ui.swing;

import org.yawlfoundation.yawl.editor.ui.YAWLEditor;
import org.yawlfoundation.yawl.editor.ui.plugin.YPluginHandler;
import org.yawlfoundation.yawl.editor.ui.properties.dialog.PropertyDialog;
import org.yawlfoundation.yawl.editor.ui.specification.io.SpecificationReader;
import org.yawlfoundation.yawl.editor.ui.specification.pubsub.Publisher;
import org.yawlfoundation.yawl.editor.ui.util.CursorUtil;
import org.yawlfoundation.yawl.engine.YSpecificationID;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;

/**
 * @author Michael Adams
 * @date 18/10/13
 */
public abstract class AbstractDownloadDialog extends PropertyDialog
        implements ActionListener, ListSelectionListener {

    protected JList listBox;

    public AbstractDownloadDialog() {
        super(YAWLEditor.getInstance());
        setResizable(true);
        setPreferredSize(new Dimension(400, 200));
        pack();
        setLocationRelativeTo(getParent());
    }


    public void actionPerformed(ActionEvent e) {
        setVisible(false);
        if (e.getActionCommand().equals("OK")) {
            download();
        }
    }


    public void valueChanged(ListSelectionEvent listSelectionEvent) {
        getOKButton().setEnabled(true);
    }


    public void setSelectionMode(int mode) {
        listBox.setSelectionMode(mode);
    }


    protected JPanel getContent() {
        JPanel content = new JPanel(new BorderLayout());
        content.add(getListPanel(), BorderLayout.CENTER);
        content.add(getButtonBar(this), BorderLayout.SOUTH);
        return content;
    }


    private JPanel getListPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(7,7,7,7));
        panel.add(createListBox(), BorderLayout.CENTER);
        return panel;
    }


    private JScrollPane createListBox() {
        listBox = getList();
        listBox.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listBox.addListSelectionListener(this);
        listBox.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent mouseEvent) {
                if (mouseEvent.getClickCount() == 2) {
                    setVisible(false);
                    download();
                }
            }
        });

        return new JScrollPane(listBox);
    }


    private void download() {
        YSpecificationID selectedID =
                ((SpecificationListModel)listBox.getModel()).getSelectedID(
                        listBox.getSelectedIndex());
        if (selectedID != null) {
            try {
                String specXML = getSelectedSpecification(selectedID);

                YStatusBar statusBar = YAWLEditor.getStatusBar();
                CursorUtil.showWaitCursor();
                Publisher.getInstance().publishFileBusyEvent();
                statusBar.setText("Downloading Specification...");
                statusBar.progressOverSeconds(4);
                YAWLEditor.getNetsPane().setVisible(false);

                SpecificationReader reader = getSpecificationReader(selectedID, specXML);
                reader.addPropertyChangeListener(new LoadCompletionListener());
                reader.execute();
            }
            catch (IOException ioe) {
                showError("Failed to get download the selected specification from" +
                        " the " + getSourceString() + ": ", ioe);
            }
        }
    }


    protected abstract JList getList();

    protected abstract SpecificationReader getSpecificationReader(YSpecificationID specID,
                                                                  String specXML);

    protected abstract String getSelectedSpecification(YSpecificationID specID)
            throws IOException;

    protected abstract String getSourceString();


    protected void showError(String message, Exception e) {
        MessageDialog.error(this, message + '\n' + e.getMessage(), "Error");
    }


    /******************************************************************************/

    class LoadCompletionListener implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent event) {
            if (event.getNewValue() == SwingWorker.StateValue.DONE) {
                YAWLEditor.getNetsPane().setVisible(true);
                YAWLEditor.getStatusBar().resetProgress();
                Publisher.getInstance().publishFileUnbusyEvent();
                CursorUtil.showDefaultCursor();
                YPluginHandler.getInstance().specificationLoaded();
            }
        }

    }

}
