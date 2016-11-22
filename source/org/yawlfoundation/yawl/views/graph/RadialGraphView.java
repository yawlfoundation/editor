package org.yawlfoundation.yawl.views.graph;

import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.GroupAction;
import prefuse.action.ItemAction;
import prefuse.action.RepaintAction;
import prefuse.action.animate.ColorAnimator;
import prefuse.action.animate.PolarLocationAnimator;
import prefuse.action.animate.QualityControlAnimator;
import prefuse.action.animate.VisibilityAnimator;
import prefuse.action.assignment.ColorAction;
import prefuse.action.assignment.FontAction;
import prefuse.action.layout.CollapsedSubtreeLayout;
import prefuse.action.layout.graph.RadialTreeLayout;
import prefuse.activity.SlowInSlowOutPacer;
import prefuse.controls.*;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.data.Tuple;
import prefuse.data.event.TupleSetListener;
import prefuse.data.tuple.DefaultTupleSet;
import prefuse.data.tuple.TupleSet;
import prefuse.render.AbstractShapeRenderer;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.EdgeRenderer;
import prefuse.render.LabelRenderer;
import prefuse.util.ColorLib;
import prefuse.util.FontLib;
import prefuse.visual.VisualItem;
import prefuse.visual.expression.InGroupPredicate;
import prefuse.visual.sort.TreeDepthItemSorter;

import java.util.Iterator;
import java.util.logging.*;


/**
 * A node-link tree viewer
 * Modified from original code from: http://jheer.org
 */
public class RadialGraphView extends Display {

    private static final String tree = "tree";
    private static final String treeNodes = "tree.nodes";
    private static final String treeEdges = "tree.edges";
    private static final String linear = "linear";


    public RadialGraphView(Graph g, String label) {
        super(new Visualization());
        setHighQuality(true);
        setDebugLevel(Level.WARNING);

        // -- set up visualization --
        m_vis.add(tree, g);
        m_vis.setInteractive(treeEdges, null, false);

        // -- set up renderers --
        LabelRenderer nodeRenderer = new LabelRenderer(label);
        nodeRenderer.setRenderType(AbstractShapeRenderer.RENDER_TYPE_DRAW_AND_FILL);
        nodeRenderer.setHorizontalAlignment(Constants.CENTER);
        nodeRenderer.setRoundedCorner(12, 12);
        nodeRenderer.setHorizontalPadding(6);
        nodeRenderer.setVerticalPadding(3);

        EdgeRenderer edgeRenderer = new EdgeRenderer(
                Constants.EDGE_TYPE_LINE, Constants.EDGE_ARROW_FORWARD);

        DefaultRendererFactory rf = new DefaultRendererFactory(nodeRenderer);
        rf.add(new InGroupPredicate(treeEdges), edgeRenderer);
        m_vis.setRendererFactory(rf);

        // -- set up processing actions --

        // colors
        ItemAction nodeColor = new NodeColorAction(treeNodes);
        ItemAction textColor = new TextColorAction(treeNodes);
        m_vis.putAction("textColor", textColor);

        ItemAction edgeColor = new ColorAction(treeEdges,
                VisualItem.STROKECOLOR, ColorLib.rgb(160, 160, 160));
        ItemAction edgeArrowColor = new ColorAction(treeEdges,
                VisualItem.FILLCOLOR, ColorLib.rgb(160, 160, 160));

        ColorAction borderColor = new BorderColorAction(treeNodes);

        FontAction fonts = new FontAction(treeNodes,
                FontLib.getFont("Tahoma", 14));
        fonts.add("ingroup('_focus_')", FontLib.getFont("Tahoma", 16));

        // recolor
        ActionList recolor = new ActionList();
        recolor.add(textColor);
        recolor.add(edgeColor);
        recolor.add(edgeArrowColor);
        recolor.add(borderColor);
        recolor.add(nodeColor);
        m_vis.putAction("recolor", recolor);

        // repaint
        ActionList repaint = new ActionList();
        repaint.add(recolor);
        repaint.add(new RepaintAction());
        m_vis.putAction("repaint", repaint);

        // animate paint change
        ActionList animatePaint = new ActionList(400);
        animatePaint.add(new ColorAnimator(treeNodes));
        animatePaint.add(new RepaintAction());
        m_vis.putAction("animatePaint", animatePaint);

        // create the tree layout action
        RadialTreeLayout treeLayout = new RadialTreeLayout(tree);
 //       treeLayout.setAngularBounds(-Math.PI/2, Math.PI);
        m_vis.putAction("treeLayout", treeLayout);

        CollapsedSubtreeLayout subLayout = new CollapsedSubtreeLayout(tree);
        m_vis.putAction("subLayout", subLayout);

        // create the filtering and layout
        ActionList filter = new ActionList();
        filter.add(new TreeRootAction(tree));
        filter.add(fonts);
        filter.add(treeLayout);
        filter.add(subLayout);
        filter.add(textColor);
        filter.add(nodeColor);
        filter.add(edgeColor);
        m_vis.putAction("filter", filter);

        // animated transition
        ActionList animate = new ActionList(1250);
        animate.setPacingFunction(new SlowInSlowOutPacer());
        animate.add(new QualityControlAnimator());
        animate.add(new VisibilityAnimator(tree));
        animate.add(new PolarLocationAnimator(treeNodes, linear));
        animate.add(new ColorAnimator(treeNodes));
        animate.add(new RepaintAction());
        m_vis.putAction("animate", animate);
        m_vis.alwaysRunAfter("filter", "animate");

        // ------------------------------------------------

        // initialize the display
        setSize(600, 600);
        setItemSorter(new TreeDepthItemSorter());
        addControlListener(new DragControl());
        addControlListener(new ZoomToFitControl());
        addControlListener(new ZoomControl());
        addControlListener(new PanControl());
        addControlListener(new FocusControl(1, "filter"));
        addControlListener(new HoverActionControl("repaint"));

        // ------------------------------------------------

        // filter graph and perform layout
        m_vis.run("filter");

        // maintain a set of items that should be interpolated linearly
        // this isn't absolutely necessary, but makes the animations nicer
        // the PolarLocationAnimator should read this set and act accordingly
        m_vis.addFocusGroup(linear, new DefaultTupleSet());
        m_vis.getGroup(Visualization.FOCUS_ITEMS).addTupleSetListener(
                new TupleSetListener() {
                    public void tupleSetChanged(TupleSet t, Tuple[] add, Tuple[] rem) {
                        TupleSet linearInterp = m_vis.getGroup(linear);
                        if (add.length < 1) return;
                        linearInterp.clear();
                        for (Node n = (Node) add[0]; n != null; n = n.getParent())
                            linearInterp.addTuple(n);
                    }
                }
        );
        m_vis.run("repaint");
    }


    public static void setDebugLevel(Level newLvl) {
        Logger rootLogger = LogManager.getLogManager().getLogger("");
        Handler[] handlers = rootLogger.getHandlers();
        rootLogger.setLevel(newLvl);
        for (Handler h : handlers) {
            if(h instanceof FileHandler)
                h.setLevel(newLvl);
        }
    }

    // ------------------------------------------------------------------------

    /**
     * Switch the root of the tree by requesting a new spanning tree
     * at the desired root
     */
    public static class TreeRootAction extends GroupAction {
        public TreeRootAction(String graphGroup) {
            super(graphGroup);
        }

        public void run(double frac) {
            TupleSet focus = m_vis.getGroup(Visualization.FOCUS_ITEMS);
            if (focus == null || focus.getTupleCount() == 0) return;

            Graph g = (Graph) m_vis.getGroup(m_group);
            Node f = null;
            Iterator tuples = focus.tuples();
            while (tuples.hasNext() && !g.containsTuple(f = (Node) tuples.next())) {
                f = null;
            }
            if (f == null) return;
            g.getSpanningTree(f);
        }
    }

    /**
     * Set node fill colors
     */
    public static class NodeColorAction extends ColorAction {
        public NodeColorAction(String group) {
            super(group, VisualItem.FILLCOLOR, ColorLib.rgb(255, 255, 255));
            add("_hover", ColorLib.gray(220, 230));
            add("ingroup('_search_')", ColorLib.rgb(255, 190, 190));
            add("ingroup('_focus_')", ColorLib.rgb(198, 229, 229));
        }

    } // end of inner class NodeColorAction

    /**
     * Set node text colors
     */
    public static class TextColorAction extends ColorAction {
        public TextColorAction(String group) {
            super(group, VisualItem.TEXTCOLOR, ColorLib.gray(0));
            add("_hover", ColorLib.rgb(255, 0, 0));
        }
    } // end of inner class TextColorAction

    /**
     * Set the stroke color for drawing treemap node outlines. A graded
     * grayscale ramp is used, with higer nodes in the tree drawn in
     * lighter shades of gray.
     */
    public static class BorderColorAction extends ColorAction {

        public BorderColorAction(String group) {
            super(group, VisualItem.STROKECOLOR);
            add(VisualItem.STROKE, 4);
        }

        public int getColor(VisualItem item) {
            return ColorLib.rgb(0, 0, 255);
        }
    }


} // end of class RadialGraphView
