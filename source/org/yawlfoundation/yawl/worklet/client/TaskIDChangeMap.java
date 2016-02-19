package org.yawlfoundation.yawl.worklet.client;

import org.yawlfoundation.yawl.editor.ui.specification.SpecificationModel;
import org.yawlfoundation.yawl.engine.YSpecificationID;

import java.io.IOException;
import java.util.Map;

/**
 * @author Michael Adams
 * @date 18/02/2016
 */
public class TaskIDChangeMap {

    private Map<String, String> _changedIdentifiers;

    public TaskIDChangeMap(Map<String, String> changeMap) {
        _changedIdentifiers = changeMap;
    }


    public String getID(String oldID) {
        String newID = _changedIdentifiers.get(oldID);
        return newID != null ? newID : oldID;
    }


    public String getOldID(String newID) {
        for (String oldID : _changedIdentifiers.keySet()) {
            if (_changedIdentifiers.get(oldID).equals(newID)) {
                return oldID;
            }
        }
        return newID;
    }


    // called when a user changes a taskID
    public void add(String oldID, String newID) {

        // need to handle the case where this id has been updated
        // more than once between saves
        _changedIdentifiers.put(getOldID(oldID), newID);
    }


    public void saveChanges() {
        if (! _changedIdentifiers.isEmpty()) {
            YSpecificationID specID = SpecificationModel.getHandler().
                    getSpecification().getSpecificationID();
            try {
                if (WorkletClient.getInstance().updateRdrSetTaskIDs(specID, _changedIdentifiers)) {
                    _changedIdentifiers.clear();
                }
            }
            catch (IOException ignore) {
                //
            }
        }
    }

}
