package org.yawlfoundation.yawl.editor.ui.swing.specification;

import javax.swing.*;
import java.awt.*;

public class AlloyRACCTestResultPanel extends JPanel {

    private final JEditorPane editor;
    private final BottomPanel owner;

    public AlloyRACCTestResultPanel(BottomPanel owner) {
        this.owner = owner;
        editor = new NotesEditor();
        setLayout(new BorderLayout());
        add(new JScrollPane(editor), BorderLayout.CENTER);
    }

    public void showTab() {
        if (isVisible()) repaint();
        else setVisible(true);

        owner.selectAlloyRACCTestResults();
    }

    public void setText(String text) {
        editor.setText(text);
        showTab();
    }
}
