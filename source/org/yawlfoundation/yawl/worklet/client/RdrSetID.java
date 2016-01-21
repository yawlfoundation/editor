package org.yawlfoundation.yawl.worklet.client;

import org.yawlfoundation.yawl.engine.YSpecificationID;

/**
 * Simple transport for set identifiers - used by remove set dialog
 * @author Michael Adams
 * @date 21/01/2016
 */
public class RdrSetID implements Comparable<RdrSetID> {

    private YSpecificationID _specID;
    private String _processName;


    public RdrSetID(YSpecificationID specID) { _specID = specID; }

    public RdrSetID(String processName) { _processName = processName; }


    public boolean isSpecID() { return _specID != null; }

    public YSpecificationID getSpecID() { return _specID; }

    public String getProcessName() { return _processName; }

    public String getName() {
        return isSpecID() ? _specID.getUri() : _processName;
    }


    public String getIdentifier() {
        return isSpecID() ? _specID.toFullString() : _processName;
    }


    // for rendering list
    public String toString() {
        return isSpecID() ? _specID.toString() : _processName;
    }


    public int compareTo(RdrSetID other) {
        return getName().compareTo(other.getName());
    }

}
