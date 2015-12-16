package org.yawlfoundation.yawl.worklet.client;

import org.yawlfoundation.yawl.engine.YSpecificationID;
import org.yawlfoundation.yawl.engine.interfce.SpecificationData;
import org.yawlfoundation.yawl.engine.interfce.TaskInformation;
import org.yawlfoundation.yawl.worklet.rdr.RdrNode;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Michael Adams
 * @date 16/12/2015
 */
public class ClientCache {

    private final Map<Long, RdrNode> _nodeMap = new HashMap<Long, RdrNode>();
    private final Map<String, TaskInformation> _taskInfoMap =
            new HashMap<String, TaskInformation>();
    private final Map<String, SpecificationData> _specDataMap =
            new HashMap<String, SpecificationData>();


    protected void clearAll() {
        clearNodes();
        clearSpecData();
        clearTaskInfo();
    }



    protected RdrNode getNode(long id) { return _nodeMap.get(id); }

    protected RdrNode add(RdrNode node) {
        return _nodeMap.put(node.getNodeId(), node);
    }

    protected RdrNode remove(RdrNode node) {
        return _nodeMap.remove(node.getNodeId());
    }

    protected void clearNodes() { _nodeMap.clear(); }



    protected SpecificationData getSpecData(YSpecificationID specID) {
        return _specDataMap.get(specID.toKeyString());
    }

    protected SpecificationData add(SpecificationData specData) {
        return _specDataMap.put(specData.getID().toKeyString(), specData);
    }

    protected SpecificationData remove(SpecificationData specData) {
        return _specDataMap.remove(specData.getID().toKeyString());
    }

    protected void clearSpecData() { _specDataMap.clear(); }



    protected TaskInformation getTaskInfo(YSpecificationID specID, String taskID) {
        return _taskInfoMap.get(getTaskInfoKey(specID, taskID));
    }

    protected TaskInformation add(TaskInformation taskInfo) {
        String key = getTaskInfoKey(taskInfo.getSpecificationID(), taskInfo.getTaskID());
        return _taskInfoMap.put(key, taskInfo);
    }

    protected TaskInformation remove(TaskInformation taskInfo) {
        String key = getTaskInfoKey(taskInfo.getSpecificationID(), taskInfo.getTaskID());
        return _taskInfoMap.remove(key);
    }

    protected void clearTaskInfo() { _taskInfoMap.clear(); }

    private String getTaskInfoKey(YSpecificationID specID, String taskID) {
        return specID.toKeyString() + ":" + taskID;
    }

}
