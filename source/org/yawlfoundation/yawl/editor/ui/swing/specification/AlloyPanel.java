package org.yawlfoundation.yawl.editor.ui.swing.specification;

import org.yawlfoundation.yawl.editor.ui.elements.model.YAWLVertex;

import javax.swing.*;
import java.awt.*;

public class AlloyPanel extends JPanel {

    private final JEditorPane editor;
    private final BottomPanel owner;

    public AlloyPanel(BottomPanel owner) {
        this.owner = owner;
        editor = new NotesEditor();
        setLayout(new BorderLayout());
        add(new JScrollPane(editor), BorderLayout.CENTER);
    }

    public void showTab() {
        if (isVisible()) repaint();
        else setVisible(true);

        owner.selectAlloyTab();
    }
    
    public void setText(String text) {
        editor.setText(text);
        showTab();
    }
}
