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

package org.yawlfoundation.yawl.worklet.menu;

import org.jgraph.event.GraphSelectionEvent;
import org.yawlfoundation.yawl.editor.core.validation.Validator;
import org.yawlfoundation.yawl.editor.ui.YAWLEditor;
import org.yawlfoundation.yawl.editor.ui.actions.YAWLBaseAction;
import org.yawlfoundation.yawl.editor.ui.actions.net.YAWLSelectedNetAction;
import org.yawlfoundation.yawl.editor.ui.actions.specification.YAWLSpecificationAction;
import org.yawlfoundation.yawl.editor.ui.elements.model.AtomicTask;
import org.yawlfoundation.yawl.editor.ui.elements.model.VertexContainer;
import org.yawlfoundation.yawl.editor.ui.elements.model.YAWLVertex;
import org.yawlfoundation.yawl.editor.ui.specification.SpecificationModel;
import org.yawlfoundation.yawl.editor.ui.specification.io.SpecificationWriter;
import org.yawlfoundation.yawl.editor.ui.specification.pubsub.*;
import org.yawlfoundation.yawl.editor.ui.specification.validation.SpecificationValidator;
import org.yawlfoundation.yawl.editor.ui.specification.validation.ValidationMessage;
import org.yawlfoundation.yawl.editor.ui.specification.validation.ValidationResultsParser;
import org.yawlfoundation.yawl.editor.ui.swing.MessageDialog;
import org.yawlfoundation.yawl.editor.ui.swing.menu.YAWLMenuItem;
import org.yawlfoundation.yawl.elements.YSpecification;
import org.yawlfoundation.yawl.engine.YSpecificationID;
import org.yawlfoundation.yawl.util.StringUtil;
import org.yawlfoundation.yawl.worklet.client.WorkletClient;
import org.yawlfoundation.yawl.worklet.dialog.AddRuleDialog;
import org.yawlfoundation.yawl.worklet.dialog.WorkletLoadDialog;
import org.yawlfoundation.yawl.worklet.settings.SettingsDialog;
import org.yawlfoundation.yawl.worklet.settings.SettingsStore;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

/**
 * @author Michael Adams
 * @date 29/09/2014
 */
public class WorkletMenu extends JMenu implements FileStateListener, GraphStateListener {

    private AtomicTask selectedTask;


    public WorkletMenu() {
        super("Worklet Mgt");
        addMenuItems();
        setIcon(getWorkletMenuIcon("worklet"));
        setMnemonic('W');
        setEnabled(false);
        Publisher.getInstance().subscribe(this);                     // file state
        Publisher.getInstance().subscribe(this,                      // graph state
              Arrays.asList(GraphState.NoElementSelected,
                      GraphState.ElementsSelected,
                      GraphState.OneElementSelected));
    }


    public void specificationFileStateChange(FileState state) {
        setEnabled(state == FileState.Open);
    }

    public void graphSelectionChange(GraphState state, GraphSelectionEvent event) {
        if (event == null) return;
        YAWLVertex vertex = null;
        switch(state) {
            case NoElementSelected: {
                vertex = null;
                break;
            }
            case OneElementSelected:
            case ElementsSelected: {
                Object cell = event.getCell();
                if (cell instanceof VertexContainer) {
                    vertex = ((VertexContainer) cell).getVertex();
                }
                else if (cell instanceof YAWLVertex) {
                    vertex = (YAWLVertex) cell;
                }
            }
        }
        setTask(vertex);
    }


    // only enable if task is assigned to the worklet service
    private void setTask(YAWLVertex vertex) {
        selectedTask = (vertex instanceof AtomicTask) ? (AtomicTask) vertex : null;
    }


    private void addMenuItems() {
        add(new YAWLMenuItem(new LoadWorkletAction()));
        add(new YAWLMenuItem(new SaveWorkletAction()));
        addSeparator();
        add(new YAWLMenuItem(new AddRuleAction()));
        add(new YAWLMenuItem(new AddRuleSetAction()));
        add(new YAWLMenuItem(new ViewRuleSetAction()));
        addSeparator();
        add(new YAWLMenuItem(new RemoveOrphanWorkletsAction()));
        add(new YAWLMenuItem(new RemoveRuleSetAction()));
        addSeparator();
        add(new YAWLMenuItem(new SettingsAction()));
    }


    private ImageIcon getWorkletMenuIcon(String name) {
        URL url = this.getClass().getResource("icon/" + name + ".png");
        return url != null ? new ImageIcon(url) : null;
    }


    /******************************************************************************/

    class LoadWorkletAction extends YAWLSpecificationAction {

        {
            putValue(Action.SHORT_DESCRIPTION, "Load Worklet");
            putValue(Action.NAME, "Load");
            putValue(Action.LONG_DESCRIPTION, "Load a stored Worklet into the editor");
            putValue(Action.SMALL_ICON, getWorkletMenuIcon("load"));
            putValue(Action.MNEMONIC_KEY, KeyEvent.VK_L);
        }

        public void actionPerformed(ActionEvent event) {
            new WorkletLoadDialog().setVisible(true);
        }

    }


    /******************************************************************************/

    class SaveWorkletAction extends YAWLSelectedNetAction {

        {
            putValue(Action.SHORT_DESCRIPTION, "Save as Worklet");
            putValue(Action.NAME, "Save");
            putValue(Action.LONG_DESCRIPTION, "Save the current specification as a Worklet");
            putValue(Action.SMALL_ICON, getWorkletMenuIcon("save"));
            putValue(Action.MNEMONIC_KEY, KeyEvent.VK_W);
        }

        public void actionPerformed(ActionEvent event) {
            YSpecification spec = new SpecificationWriter().cleanSpecification();
            if (isValidSpecification(spec)) {
               try {
                   new WorkletClient().addWorklet(spec);
               }
               catch (IOException ioe) {
                   MessageDialog.error(ioe.getMessage(), "Worklet Add Error");
               }
            }
            else {
                MessageDialog.error("Could not save worklet because it is not valid.\n" +
                        "Please review the problems in the Validate window below",
                        "Invalid Specification");
            }
        }


        private boolean isValidSpecification(YSpecification specification) {
            List<String> errors = new SpecificationValidator().getValidationResults(
                    specification, Validator.ERROR_MESSAGES);
            List<ValidationMessage> messages = new ValidationResultsParser().parse(errors);
            YAWLEditor.getInstance().showProblemList("Validation Results", messages);
            return errors.isEmpty();
        }
    }


    /******************************************************************************/

    class RemoveOrphanWorkletsAction extends YAWLBaseAction {

        {
            putValue(Action.SHORT_DESCRIPTION, "Remove Orphan Worklets");
            putValue(Action.NAME, "Remove Orphans");
            putValue(Action.LONG_DESCRIPTION, "Remove Worklets not referred to by any rule set");
            putValue(Action.SMALL_ICON, getWorkletMenuIcon("cleanse"));
            putValue(Action.MNEMONIC_KEY, KeyEvent.VK_O);
        }

        public void actionPerformed(ActionEvent event) {
            new WorkletLoadDialog().setVisible(true);
        }



    }


    /******************************************************************************/

    class AddRuleAction extends YAWLSelectedNetAction {

        {
            putValue(Action.SHORT_DESCRIPTION, "Add Rule");
            putValue(Action.NAME, "Add Rule");
            putValue(Action.LONG_DESCRIPTION, "Add Rule to Worklet Rule Set");
            putValue(Action.SMALL_ICON, getWorkletMenuIcon("add"));
            putValue(Action.MNEMONIC_KEY, KeyEvent.VK_A);
        }

        public void actionPerformed(ActionEvent event) {
            new AddRuleDialog(selectedTask).setVisible(true);
        }

    }


    /******************************************************************************/

    class AddRuleSetAction extends YAWLSelectedNetAction {

        {
            putValue(Action.SHORT_DESCRIPTION, "Add Rule Set");
            putValue(Action.NAME, "Add Rule Set");
            putValue(Action.LONG_DESCRIPTION, "Add a Rule Set from file");
            putValue(Action.SMALL_ICON, getWorkletMenuIcon("addSet"));
            putValue(Action.MNEMONIC_KEY, KeyEvent.VK_D);
        }

        public void actionPerformed(ActionEvent event) {
            WorkletClient client = new WorkletClient();
            YSpecificationID specID = SpecificationModel.getHandler()
                    .getSpecification().getSpecificationID();
            try {
                if (client.getRdrSet(specID) != null) {
                    MessageDialog.error(
                            "There is an existing rule set for the current specification",
                            "Unable to Add Rule Set");
                }
                else {
                    File xrsFile = getSelectedFile();
                    if (xrsFile != null) {
                        String xml = StringUtil.fileToString(xrsFile);
                        if (xml != null) {
                            client.addRuleSet(specID, xml);
                            MessageDialog.info("Rule set loaded successfully", "Success");
                        }
                    }
                    else {
                        MessageDialog.error("Error loading rule set from file",
                                "Load Rule Set Error");
                    }
                }
            }
            catch (IOException ioe) {
                MessageDialog.error(ioe.getMessage(), "Load Rule Set Error");
            }
        }


        private File getSelectedFile() {
            JFileChooser chooser = new JFileChooser(SettingsStore.getLastRuleSetPath());
            chooser.setDialogTitle("Load Worklet Rule Set");
            chooser.setAcceptAllFileFilterUsed(false);

            chooser.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    return f.isDirectory() || f.getName().toLowerCase().endsWith(".xrs");
                }

                @Override
                public String getDescription() {
                    return "Worklet Rule Set files (XRS)";
                }
            });

            int response = chooser.showDialog(YAWLEditor.getInstance(), "Open");
            if (response != JFileChooser.CANCEL_OPTION) {
                File file = chooser.getSelectedFile();
                if (file != null && file.isFile() && file.exists()) {
                    SettingsStore.setLastRuleSetPath(file.getAbsolutePath());
                    return file;
                }
            }
            return null;
        }

    }


    /******************************************************************************/

    class ViewRuleSetAction extends YAWLSelectedNetAction {

        {
            putValue(Action.SHORT_DESCRIPTION, "View Rule Set");
            putValue(Action.NAME, "View Rule Set");
            putValue(Action.LONG_DESCRIPTION, "View the Rule Set for the current specification");
            putValue(Action.SMALL_ICON, getWorkletMenuIcon("view"));
            putValue(Action.MNEMONIC_KEY, KeyEvent.VK_V);
        }

        public void actionPerformed(ActionEvent event) {
            new AddRuleDialog(selectedTask).setVisible(true);
        }

    }


    /******************************************************************************/

    class RemoveRuleSetAction extends YAWLSelectedNetAction {

        {
            putValue(Action.SHORT_DESCRIPTION, "Remove Rule Set");
            putValue(Action.NAME, "Remove Rule Set");
            putValue(Action.LONG_DESCRIPTION, "Remove an existing Rule Set from the Worklet Service");
            putValue(Action.SMALL_ICON, getWorkletMenuIcon("remove"));
            putValue(Action.MNEMONIC_KEY, KeyEvent.VK_R);
        }

        public void actionPerformed(ActionEvent event) {
            new AddRuleDialog(selectedTask).setVisible(true);
        }

    }


    /******************************************************************************/

    class SettingsAction extends YAWLBaseAction {

        {
            putValue(Action.SHORT_DESCRIPTION, "Settings");
            putValue(Action.NAME, "Settings");
            putValue(Action.LONG_DESCRIPTION, "Worklet Service Connection Settings");
            putValue(Action.SMALL_ICON, getWorkletMenuIcon("settings"));
            putValue(Action.MNEMONIC_KEY, KeyEvent.VK_S);
        }

        public void actionPerformed(ActionEvent event) {
            new SettingsDialog().setVisible(true);
        }

    }


}
