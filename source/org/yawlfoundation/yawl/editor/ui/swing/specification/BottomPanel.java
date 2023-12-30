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

package org.yawlfoundation.yawl.editor.ui.swing.specification;

import org.jgraph.event.GraphSelectionEvent;
import org.yawlfoundation.yawl.editor.ui.YAWLEditor;
import org.yawlfoundation.yawl.editor.ui.elements.model.YAWLVertex;
import org.yawlfoundation.yawl.editor.ui.net.utilities.NetCellUtilities;
import org.yawlfoundation.yawl.editor.ui.specification.pubsub.GraphState;
import org.yawlfoundation.yawl.editor.ui.specification.pubsub.GraphStateListener;
import org.yawlfoundation.yawl.editor.ui.specification.pubsub.Publisher;
import org.yawlfoundation.yawl.editor.ui.specification.validation.ValidationMessage;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;

public class BottomPanel extends JTabbedPane implements GraphStateListener {

    private static final int NOTES_PANEL_INDEX = 0;
    private static final int PROBLEM_PANEL_INDEX = 1;
    private static final int ALLOY_PANEL_INDEX = 2;

    private final NotesPanel notesPanel;
    private final ProblemPanel problemPanel;
    private final AlloyPanel alloyPanel;
    private final AlloyRACCTestResultPanel alloyRACCTestResultPanel;
    private final AlloyValidationsResultPanel alloyProblemsPanel;


    public BottomPanel() {
//        setMinimumSize(new Dimension());            // minimise pane on startup

        notesPanel = new NotesPanel();
        addTab("Notes", notesPanel);

        problemPanel = new ProblemPanel(this);
        addTab("Problems", problemPanel);
        
        alloyPanel = new AlloyPanel(this);
        addTab("Alloy", alloyPanel);

        alloyRACCTestResultPanel = new AlloyRACCTestResultPanel(this);
        addTab("RACC Test Result (Alloy)", alloyRACCTestResultPanel);

        alloyProblemsPanel = new AlloyValidationsResultPanel(this);
        addTab("Alloy Problems", alloyProblemsPanel);

        setEnabledAt(NOTES_PANEL_INDEX, false);
        setSelectedComponent(problemPanel);

        Publisher.getInstance().subscribe(this,
                Arrays.asList(GraphState.NoElementSelected,
                        GraphState.ElementsSelected,
                        GraphState.OneElementSelected));
    }

    public void graphSelectionChange(GraphState state, GraphSelectionEvent event) {
        switch (state) {
            case ElementsSelected, NoElementSelected -> {
                setEnabledAt(NOTES_PANEL_INDEX, false);
                setTitleAt(NOTES_PANEL_INDEX, "Notes");
                notesPanel.setVertex(null);
                notesPanel.setVisible(false);
                setSelectedComponent(problemPanel);
                notesPanel.repaint();
            }
            default -> {
                YAWLVertex vertex = NetCellUtilities.getVertexFromCell(
                        YAWLEditor.getNetsPane().getSelectedGraph().getSelectionCell()
                );

                if (vertex == null) {
                    return;
                }

                setEnabledAt(NOTES_PANEL_INDEX, true);
                String name = vertex.getName();
                if (name == null) name = vertex.getID();
                setTitleAt(NOTES_PANEL_INDEX, "Notes (" + name + ")");
                notesPanel.setVertex(vertex);
                selectNotesTab();
            }
        }
    }

    public void selectNotesTab() {
        setSelectedComponent(notesPanel);
        notesPanel.setPreferredSize(this.getSize());
    }
    
    public void selectAlloyTab() {
        setSelectedComponent(alloyPanel);
        alloyPanel.setPreferredSize(this.getSize());
    }

    public void selectAlloyRACCTestResults() {
        setSelectedComponent(alloyPanel);
        alloyRACCTestResultPanel.setPreferredSize(this.getSize());
    }

    public void selectProblemsTab() {
        setSelectedComponent(problemPanel);
        setTitleAt(PROBLEM_PANEL_INDEX, problemPanel.getTitle());
    }

    public void selectAlloyProblemsTab() {
        setSelectedComponent(problemPanel);
        setTitleAt(PROBLEM_PANEL_INDEX, problemPanel.getTitle());
    }
    
    public void setAlloyCode(String code) {
        alloyPanel.setText(code);
    }

    public void setAlloyRACCTestResults(String results) {
        alloyRACCTestResultPanel.setText(results);
    }

    public void setProblemList(String title, List<ValidationMessage> problems) {
        problemPanel.setProblemList(title, problems);
    }
    public void setAlloyProblemsList(String title, List<ValidationMessage> problems) {
        alloyProblemsPanel.setProblemList(title, problems);
    }
}
