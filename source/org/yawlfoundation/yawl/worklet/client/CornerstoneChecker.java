package org.yawlfoundation.yawl.worklet.client;

import org.jdom2.Element;
import org.yawlfoundation.yawl.engine.interfce.WorkItemRecord;
import org.yawlfoundation.yawl.worklet.rdr.RdrNode;
import org.yawlfoundation.yawl.worklet.rdr.RdrTree;

import java.io.IOException;

/**
 * @author Michael Adams
 * @date 26/10/2016
 */
public class CornerstoneChecker {


    public RdrNode check(String treeXML, RdrNode node) throws IOException {
        RdrTree rdrTree = new RdrTree();
        rdrTree.fromXML(treeXML);
        return supplementIfRequired(rdrTree, node);
    }


    private RdrNode supplementIfRequired(RdrTree rdrTree, RdrNode rdrNode) {
        if (rdrTree != null) {
            for (String condition : rdrTree.getAllConditions()) {
                if (condition.contains("(this)")) {
                    rdrNode.setCornerStone(supplementCornerstone(rdrNode.getCornerStone()));
                    break;
                }
            }
        }
        return rdrNode;
    }


    // adds dummy process info to cornerstone data for functions that require it,
    // so that new node placement succeeds correctly on the service side
    private Element supplementCornerstone(Element cornerstone) {
        Element eInfo = new Element("process_info");
        Element eThis = new Element("workItemRecord");
        Element eTimer = new Element("timerexpiry");
        Element eStatus = new Element("status");
        eTimer.setText("1");                     // any value will do
        eStatus.setText(WorkItemRecord.statusExecuting);
        eThis.addContent(eTimer);
        eThis.addContent(eStatus);
        eInfo.addContent(eThis);
        cornerstone.addContent(eInfo);
        return cornerstone;
    }

}
