package org.yawlfoundation.yawl.views.table;

import java.util.Set;

/**
 * @author Michael Adams
 * @date 1/12/16
 */
public class TaskIO {

    private String _id;
    private Set<String> _reads;
    private Set<String> _writes;

    public TaskIO(String id, Set<String> reads, Set<String> writes) {
        _id = id;
        _reads = reads;
        _writes = writes;
    }


    public String getID() { return _id; }


    public String getValue(String varName) {
        StringBuilder sb = new StringBuilder();
        if (_reads.contains(varName)) sb.append("R");
        if (_writes.contains(varName)) sb.append("W");
        return sb.toString();
    }

}
