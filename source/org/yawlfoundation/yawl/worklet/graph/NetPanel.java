package org.yawlfoundation.yawl.worklet.graph;

import org.jgraph.JGraph;
import org.jgraph.event.GraphModelEvent;
import org.jgraph.event.GraphModelListener;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.GraphModel;
import org.yawlfoundation.yawl.editor.ui.properties.data.StatusPanel;
import org.yawlfoundation.yawl.editor.ui.properties.dialog.component.MiniToolBar;
import org.yawlfoundation.yawl.worklet.rdr.RdrConclusion;
import org.yawlfoundation.yawl.worklet.rdr.RdrPrimitive;
import org.yawlfoundation.yawl.worklet.rdr.RuleType;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


/**
 * @author Michael Adams
 * @date 1/02/2016
 */
public class NetPanel extends JPanel implements GraphModelListener {

    private final ExletGraph _graph;
    private final DefaultGraphModel _model;
    private final ExletNetValidator _validator;
    private final NetDialog _dialog;

    private StatusPanel _status;


    public NetPanel(NetDialog dialog, RdrConclusion conclusion, RuleType ruleType) {
        super();
        init();
        _dialog = dialog;
        _model = new DefaultGraphModel();
        add(createStatusBar(), BorderLayout.SOUTH);
        _graph = createGraph(_model);
        new NetRenderer(getPreferredSize()).render(_graph, conclusion);
        _validator = new ExletNetValidator(ruleType);
        add(new JScrollPane(_graph), BorderLayout.CENTER);
    }


    public JGraph getGraph() { return _graph; }


    public RdrConclusion getConclusion() {
        return getConclusion(_validator.getPrimitiveList(getCells()));
    }


    @Override
    public void graphChanged(GraphModelEvent e) {
        if (_validator != null) {
            String result = _validator.validate(getCells());
            boolean valid = result.equals("ok");
            if (valid) {
                _status.setOK("OK");
            }
            else {
                _status.set(result);
            }
            _dialog.setOKEnabled(valid);
            _dialog.setAlignEnabled(valid || ! result.startsWith("End not reachable"));
        }
    }


    public void alignCells() {
        RdrConclusion conclusion = getConclusion();
        if (! conclusion.isNullConclusion()) {
            _graph.getGraphLayoutCache().remove(_model.getRoots().toArray());
            new NetRenderer(getPreferredSize()).render(_graph, conclusion);
        }
    }


    public void clearCells() {
        for (AbstractNetCell cell : getCells()) {
            if (! cell.canBeRemoved()) {               // terminals
                cell.removeEdges();
            }
            else {
                _graph.getGraphLayoutCache().remove(new Object[] { cell }, true, true);
            }
        }
    }


    private void init() {
        setBorder(new EmptyBorder(5,0,0,5));
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(600, 200));
    }


    private ExletGraph createGraph(GraphModel model) {
        ExletGraph graph = new ExletGraph(model);
        graph.getGraphLayoutCache().setFactory(new NetViewFactory());
        graph.setUI(new ExletGraphUI());
        graph.setEditable(false);
        graph.setDisconnectable(false);
        graph.setAntiAliased(true);
        graph.setTolerance(5);
        graph.setAutoResizeGraph(true);
        graph.setAutoscrolls(true);
        graph.getModel().addGraphModelListener(this);
        return graph;
    }


    private MiniToolBar createStatusBar() {
        MiniToolBar toolbar = new MiniToolBar(null);
        toolbar.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED),
                new EmptyBorder(0,8,3,3)));
        toolbar.addSeparator(new Dimension(1,16));
        _status = new StatusPanel(null);
        toolbar.add(_status);
        return toolbar;
    }


    private java.util.List<AbstractNetCell> getCells() {
        java.util.List roots = _model.getRoots();
        if (roots != null) {
            java.util.List<AbstractNetCell> vertices = new ArrayList<AbstractNetCell>();
            for (Object root : roots) {
                if (root instanceof AbstractNetCell) {
                    vertices.add((AbstractNetCell) root);
                }
            }
            return vertices;
        }
        return Collections.emptyList();
    }


    private java.util.List<PrimitiveCell> getPrimitiveCells() {
        java.util.List<PrimitiveCell> cells = new ArrayList<PrimitiveCell>();
        for (AbstractNetCell aCell : getCells()) {
            if (aCell instanceof PrimitiveCell) {
                cells.add((PrimitiveCell) aCell);
            }
        }
        Collections.sort(cells, new Comparator<PrimitiveCell>() {
            public int compare(PrimitiveCell p1, PrimitiveCell p2) {
                return (int) (p1.getBounds().getX() - p2.getBounds().getX());
            }
        });
        return cells;
    }


    private RdrConclusion getConclusion(java.util.List<RdrPrimitive> primitives) {
        RdrConclusion conclusion = new RdrConclusion();
        conclusion.setPrimitives(primitives);
        return conclusion;
    }

}
