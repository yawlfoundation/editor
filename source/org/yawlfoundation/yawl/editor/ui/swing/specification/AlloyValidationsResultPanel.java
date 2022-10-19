package org.yawlfoundation.yawl.editor.ui.swing.specification;

public class AlloyValidationsResultPanel extends ProblemPanel {
    private final BottomPanel owner;

    public AlloyValidationsResultPanel(BottomPanel owner) {
        super(owner);
        this.owner = owner;
    }

    public void showTab() {
        if (isVisible()) repaint();
        else setVisible(true);

        owner.selectAlloyProblemsTab();
    }
}