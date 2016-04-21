package org.yawlfoundation.yawl.worklet.graph;

import org.yawlfoundation.yawl.editor.ui.YAWLEditor;
import org.yawlfoundation.yawl.editor.ui.properties.dialog.component.ButtonBar;
import org.yawlfoundation.yawl.worklet.rdr.RdrConclusion;
import org.yawlfoundation.yawl.worklet.rdr.RuleType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Michael Adams
 * @date 1/02/2016
 */
public class NetDialog extends JDialog implements ActionListener {

    private ButtonBar _btnBar;
    private NetPanel _netPanel;
    private JButton _btnAlign;
    private RdrConclusion _conclusion;


    public NetDialog(RdrConclusion conclusion, RuleType ruleType) {
        super(YAWLEditor.getInstance());
        setTitle("Rules Action Editor: " + ruleType.toLongString());
        setModal(true);
        setResizable(true);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        add(getContent(conclusion, ruleType));
        pack();
        setLocationRelativeTo(YAWLEditor.getInstance());
        _netPanel.graphChanged(null);                              // initial validation
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if (cmd.equals("Cancel")) {
            setVisible(false);
        }
        else if (cmd.equals("Clear")) {
            _netPanel.clearCells();
        }
        else if (cmd.equals("Align")) {
            _netPanel.alignCells();
        }
        else if (cmd.equals("OK")) {
            _conclusion = _netPanel.getConclusion();
            setVisible(false);
        }
    }


    public RdrConclusion getConclusion() { return _conclusion; }


    public void setOKEnabled(boolean enable) { _btnBar.setOKEnabled(enable); }

    public void setAlignEnabled(boolean enable) { _btnAlign.setEnabled(enable); }



    private JPanel getContent(RdrConclusion conclusion, RuleType ruleType) {
        JPanel panel = new JPanel(new BorderLayout());
        _netPanel = new NetPanel(this, conclusion, ruleType);
        panel.add(getPalettePanel(), BorderLayout.WEST);
        panel.add(getButtonBar(), BorderLayout.SOUTH);
        panel.add(_netPanel, BorderLayout.CENTER);
        return panel;
    }


    private PalettePanel getPalettePanel() {
        ExletGraphUI graphUI = (ExletGraphUI) _netPanel.getGraph().getUI();
        PalettePanel palettePanel = new PalettePanel();
        palettePanel.addPaletteListener(graphUI);
        return palettePanel;
    }

    private ButtonBar getButtonBar() {
        _btnBar = new ButtonBar(this);
        _btnAlign = _btnBar.addButton("Align", this);
        JButton btnClear = _btnBar.addButton("Clear", this);
        btnClear.setMnemonic('r');
        return _btnBar;
    }

}
