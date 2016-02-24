package org.yawlfoundation.yawl.worklet.dialog;

import org.yawlfoundation.yawl.util.StringUtil;
import org.yawlfoundation.yawl.worklet.client.WorkletClient;
import org.yawlfoundation.yawl.worklet.exception.ExletAction;
import org.yawlfoundation.yawl.worklet.rdr.RdrConclusion;
import org.yawlfoundation.yawl.worklet.rdr.RdrNode;
import org.yawlfoundation.yawl.worklet.rdr.RdrPrimitive;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Michael Adams
 * @date 15/01/2016
 */
public class CompositeRulePanel extends JPanel {

    private JTextArea _textArea;

    private static final int TAB_SIZE = 3;


    public CompositeRulePanel() {
        super();
        setLayout(new BorderLayout());
        setBorder(new CompoundBorder(new EmptyBorder(0,8,0,8),
                new TitledBorder("Effective Composite Rule")));
        add(new JScrollPane(createTextArea()), BorderLayout.CENTER);
    }


    public void setCondition(RdrNode node) {
        _textArea.setText(composeCondition(getNodePath(node)));
    }


    private java.util.List<RdrNode> getNodePath(RdrNode node) {
        java.util.List<RdrNode> nodeList = new ArrayList<RdrNode>();

        // get list of nodes on path back to root
        while (node.getParent() != null) {
            nodeList.add(node);
            node = node.getParent();
        }
        Collections.reverse(nodeList);       // now first child --> node
        return nodeList;
    }


    private String composeCondition(java.util.List<RdrNode> nodeList) {
        if (nodeList == null || nodeList.isEmpty()) return "";

        RdrNode prevNode = nodeList.get(0);
        int tabs = 1;
        StringBuilder s = new StringBuilder();
        s.append(getLine(prevNode, "IF "));
        for (int i=1; i < nodeList.size(); i++) {
            RdrNode node = nodeList.get(i);
            if (node == prevNode.getTrueChild()) {
                s.append(getIndent(tabs++));
                s.append(getLine(node, "EXCEPT IF "));
            }
            else {              // false child
                s.append(getIndent(--tabs));
                s.append(getLine(node, "ELSE IF "));
            }
            prevNode = node;
        }
        return s.toString();
    }


    private String getLine(RdrNode node, String starter) {
        StringBuilder s = new StringBuilder();
        s.append(starter).append(node.getCondition()).append(" THEN ")
                .append(getConclusionLine(node.getConclusion())).append('\n');
        return s.toString();
    }


    private String getConclusionLine(RdrConclusion conclusion) {
        StringBuilder s = new StringBuilder();
        for (RdrPrimitive primitive : conclusion.getPrimitives()) {
            if (s.length() > 0) s.append(" -> ");
            s.append(primitive.getAction()).append(" ")
                    .append(getTarget(primitive));
        }
        return s.toString();
    }


    private String getIndent(int tabCount) {
        if (tabCount < 1) return "";
        char[] tabs = new char[tabCount * TAB_SIZE];
        for (int i=0; i<(tabCount * TAB_SIZE); i++) {
            tabs[i] = ' ';
        }
        return new String(tabs);
    }


    private JTextArea createTextArea() {
        _textArea = new JTextArea(5, 20);
        _textArea.setEditable(false);
        _textArea.setBackground(Color.WHITE);
        return _textArea;
    }


    private String getTarget(RdrPrimitive primitive) {
        return ExletAction.fromString(primitive.getAction()).isWorkletAction() ?
                StringUtil.join(WorkletClient.getInstance().getWorkletCache()
                        .getURIsForTarget(primitive.getTarget()), ';') :
                primitive.getTarget();
    }

}
