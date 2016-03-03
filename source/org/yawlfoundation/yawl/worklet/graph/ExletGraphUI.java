package org.yawlfoundation.yawl.worklet.graph;

import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphConstants;
import org.jgraph.plaf.basic.BasicGraphUI;
import org.yawlfoundation.yawl.editor.ui.net.NetOverlay;
import org.yawlfoundation.yawl.editor.ui.resourcing.subdialog.ListDialog;
import org.yawlfoundation.yawl.util.StringUtil;
import org.yawlfoundation.yawl.worklet.exception.ExletTarget;
import org.yawlfoundation.yawl.worklet.model.WorkletListModel;
import org.yawlfoundation.yawl.worklet.rdr.RdrPrimitive;
import org.yawlfoundation.yawl.worklet.support.WorkletInfo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * @author Michael Adams
 * @date 4/02/2016
 */
public class ExletGraphUI extends BasicGraphUI implements ActionListener {

    private ExletActions _paletteSelection = ExletActions.Select;
    private final NetOverlay _overlay = new NetOverlay();


    public ExletGraphUI() {
        super();
    }


    protected MouseListener createMouseListener() { return new NetMouseHandler(); }

    protected KeyListener createKeyListener() { return new NetKeyHandler(); }


    // from PalettePanel
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        _paletteSelection = ExletActions.valueOf(cmd);
    }


    // draws or removes potential flows
    protected void paintOverlay(Graphics g) {
        _overlay.paint(g, graph.getBackground());
    }


    /*******************************************************************************/

    public class NetMouseHandler extends MouseHandler {

        private AbstractNetCell source = null;

        public NetMouseHandler() { super(); }

         public void mouseClicked(MouseEvent e) {
             if (e.getClickCount() == 2 && !e.isConsumed()) {
                 editCompensator(e);
             }
             else if (SwingUtilities.isRightMouseButton(e)) {
                 showAncestorCasesPopup(e);
             }
             else {
                 switch (_paletteSelection) {
                     case Select:
                         super.mouseClicked(e);
                         break;
                     case Arc:
                         break;
                     case Compensate:
                         if (canInsertAt(e.getX(), e.getY())) {
                             insertCompensation(e);
                         }
                         break;
                     default:
                         if (canInsertAt(e.getX(), e.getY())) {
                             insertPrimitive(e.getX(), e.getY());
                         }
                         break;
                 }
             }
        }


        public void mousePressed(MouseEvent e) {
            if (_paletteSelection == ExletActions.Arc) {
                Object cell = getCellAt(e.getX(), e.getY());
                if (cell instanceof AbstractNetCell) {
                    source = (AbstractNetCell) cell;
                }
            }
            super.mousePressed(e);
        }


        public void mouseDragged(MouseEvent e) {
            if (_paletteSelection == ExletActions.Arc && source != null) {
                Point2D.Double point = new Point2D.Double(e.getX(), e.getY());
                _overlay.setLine(new Line2D.Double(source.getCentre(), point));
                graph.repaint();
            }
            else super.mouseDragged(e);
        }


        public void mouseReleased(MouseEvent e) {
            if (_paletteSelection == ExletActions.Arc && source != null) {
                Object cell = getCellAt(e.getX(), e.getY());
                if (cell instanceof AbstractNetCell) {
                    AbstractNetCell target = (AbstractNetCell) cell;
                    if (canConnect(source, target)) {
                        insertEdge(source, target);
                    }
                }
                graph.repaint();
            }
            super.mouseReleased(e);

            _overlay.clear();
            source = null;
        }


        private void insertCompensation(MouseEvent e) {
            String target = showWorkletListDialog(e.getLocationOnScreen(), null);
            if (target != null) {
                insertPrimitive(e.getX(), e.getY(), target);
            }
        }


        private void insertPrimitive(int x, int y) {
            insertPrimitive(x, y, _paletteSelection.getTarget());
        }


        private void insertPrimitive(int x, int y, String target) {
            int index = getPrimitiveCount() + 1;
            RdrPrimitive p = new RdrPrimitive(index, _paletteSelection.getAction(),
                    target);
            int edgeLength = NetRenderer.EDGE_LENGTH;
            int centX = x - edgeLength / 2;
            int centY = y - edgeLength / 2;
            Rectangle2D bounds = new Rectangle2D.Double(centX, centY, edgeLength, edgeLength);
            PrimitiveCell cell = new PrimitiveCell(bounds, p);
            graphLayoutCache.insert(cell);
        }


        private void insertEdge(AbstractNetCell source, AbstractNetCell target) {
            DefaultEdge edge = new DefaultEdge();
            source.addPort();
            edge.setSource(source.getChildAt(source.getChildCount() -1));
            source.setOutgoingEdge(edge);
            target.addPort();
            edge.setTarget(target.getChildAt(target.getChildCount() -1));
            target.setIncomingEdge(edge);

            GraphConstants.setLineEnd(edge.getAttributes(), GraphConstants.ARROW_TECHNICAL);
            GraphConstants.setEndFill(edge.getAttributes(), true);
            graphLayoutCache.insert(edge);
        }


        private int getPrimitiveCount() {
            Object[] cells = graphLayoutCache.getCells(false, true, false, false);
            return cells != null ? cells.length : 0;
        }


        private boolean canConnect(AbstractNetCell source, AbstractNetCell target) {
            return ! ((source instanceof TerminalCell) && target instanceof TerminalCell)
                    && source != target && source.canBeSource() && target.canBeTarget();
        }


        private boolean canInsertAt(int x, int y) {
            return ! (getCellAt(x, y) instanceof AbstractNetCell);
        }


        private void editCompensator(MouseEvent e) {
            Object cell = getCellAt(e.getX(), e.getY());
            if (cell instanceof PrimitiveCell) {
                PrimitiveCell p = (PrimitiveCell) cell;
                if (p.isCompensator()) {
                    String target = showWorkletListDialog(
                            e.getLocationOnScreen(), p.getPrimitive().getTarget());
                    if (target != null) {
                        p.getPrimitive().setTarget(target);
                    }
                }
            }
        }


        private void showAncestorCasesPopup(MouseEvent e) {
            Object cell = getCellAt(e.getX(), e.getY());
            if (cell instanceof PrimitiveCell) {
                PrimitiveCell pCell = (PrimitiveCell) cell;
                if (pCell.shouldShowAncestorPopup()) {
                    new AncestorCasesPopup(pCell, e);
                }
            }
        }


        private String showWorkletListDialog(Point location, String targets) {
            ListDialog dialog = new ListDialog(null, new WorkletListModel(), "Worklets");
            dialog.setResizable(true);
            dialog.setPreferredSize(new Dimension(550, 500));
            dialog.pack();
            dialog.setLocation(location);
            if (targets != null) {
                dialog.setSelections(targets);
            }
            dialog.setVisible(true);
            java.util.List<String> selections = new ArrayList<String>();
            for (Object o : dialog.getSelections()) {
                 selections.add(((WorkletInfo) o).getSpecID().getKey());
            }
            return selections.isEmpty() ? null : StringUtil.join(selections, ';');
        }


        private Object getCellAt(int x, int y) {
            return graph.getFirstCellForLocation(x, y);
        }

    }


    /*******************************************************************************/

    public class NetKeyHandler extends KeyHandler {

        public NetKeyHandler() { super(); }

        public void keyPressed(KeyEvent e) {
            super.keyPressed(e);
            if (e.getKeyCode() == KeyEvent.VK_DELETE ||
                    e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                Object[] cells = graph.getSelectionCells();
                if (cells != null) {
                    for (Object cell : cells) {
                        if (cell instanceof AbstractNetCell) {
                            AbstractNetCell netCell = (AbstractNetCell) cell;
                            if (netCell.canBeRemoved()) {
                                removeEdgeFromSource(netCell.getIncomingEdge());
                                removeEdgeFromTarget(netCell.getOutgoingEdge());
                                graphLayoutCache.remove(new Object[] { cell }, true, true);
                            }
                        }
                        else if (cell instanceof DefaultEdge) {
                            DefaultEdge edge = (DefaultEdge) cell;
                            removeEdgeFromSource(edge);
                            removeEdgeFromTarget(edge);
                            graphLayoutCache.remove(new Object[] { cell }, true, true);
                        }
                    }
                }
            }
        }


        private void removeEdgeFromSource(DefaultEdge edge) {
            if (edge != null) {
                DefaultPort port = (DefaultPort) edge.getSource();
                if (port != null) {
                    port.removeEdge(edge);
                    AbstractNetCell source = (AbstractNetCell) port.getParent();
                    if (source != null) source.setOutgoingEdge(null);
                }
            }
        }


        private void removeEdgeFromTarget(DefaultEdge edge) {
            if (edge != null) {
                DefaultPort port = (DefaultPort) edge.getTarget();
                if (port != null) {
                    port.removeEdge(edge);
                    AbstractNetCell target = (AbstractNetCell) port.getParent();
                    if (target != null) target.setIncomingEdge(null);
                }
            }
        }

    }


    /*******************************************************************************/

    class AncestorCasesPopup extends JPopupMenu {

        public AncestorCasesPopup(final PrimitiveCell pCell, MouseEvent e) {
            final JCheckBoxMenuItem item = new JCheckBoxMenuItem("Ancestor cases only");
            item.setSelected(pCell.getTarget() == ExletTarget.AncestorCases);

            item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    pCell.setAncestorCasesTarget(item.isSelected());
                    fireCellChanged(pCell);                    // to revalidate exlet
                }
            });

            add(item);
            show(e.getComponent(), e.getX(), e.getY());
        }


        public void fireCellChanged(Object cell) {
            ((DefaultGraphModel) graph.getModel()).cellsChanged(new Object[]{ cell });
        }
    }

}
