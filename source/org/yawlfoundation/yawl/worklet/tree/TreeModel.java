package org.yawlfoundation.yawl.worklet.tree;

import org.yawlfoundation.yawl.worklet.rdr.RdrNode;
import org.yawlfoundation.yawl.worklet.rdr.RdrTree;

/**
 * @author Michael Adams
 * @date 13/01/2016
 */
public class TreeModel {

    private final RdrTree _tree;


    public TreeModel(RdrTree tree) { _tree = tree; }


    public RdrNode getRoot() { return _tree.getRootNode(); }

    public RdrNode getNextTrueNode(RdrNode node) { return node.getTrueChild(); }



}
