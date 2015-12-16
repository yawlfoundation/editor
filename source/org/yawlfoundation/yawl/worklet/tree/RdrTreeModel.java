package org.yawlfoundation.yawl.worklet.tree;

import org.yawlfoundation.yawl.worklet.rdr.RdrTreeSet;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import java.util.Enumeration;

/**
 * @author Michael Adams
 * @date 15/12/2015
 */
public class RdrTreeModel extends DefaultTreeModel {

    private RdrTreeSet _treeSet;

    public RdrTreeModel(RdrTreeSet treeSet) {
        super(new TreeNode() {
            @Override
            public TreeNode getChildAt(int childIndex) {
                return null;
            }

            @Override
            public int getChildCount() {
                return 0;
            }

            @Override
            public TreeNode getParent() {
                return null;
            }

            @Override
            public int getIndex(TreeNode node) {
                return 0;
            }

            @Override
            public boolean getAllowsChildren() {
                return false;
            }

            @Override
            public boolean isLeaf() {
                return false;
            }

            @Override
            public Enumeration children() {
                return null;
            }
        });
    }
}
