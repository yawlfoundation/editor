package org.yawlfoundation.yawl.views.util;

import org.yawlfoundation.yawl.editor.ui.specification.SpecificationModel;
import org.yawlfoundation.yawl.views.ontology.OntologyHandler;
import org.yawlfoundation.yawl.views.ontology.Triple;

import java.util.*;

/**
 * @author Michael Adams
 * @date 30/11/16
 */
public class FlowsComparator implements Comparator<String> {

    private final List<Triple> _predecessorList;
    private List<Triple> _flowsIntoList;
    private Map<String, String> _netToInputCondition;


    public FlowsComparator() {
        if (!OntologyHandler.isLoaded()) {
            OntologyHandler.load(SpecificationModel.getHandler());
        }
        _predecessorList = OntologyHandler.swrlQuery("hasPredecessor");
    }


    @Override
    public int compare(String id1, String id2) {
        boolean onePrecedesTwo = hasPredecessor(id2, id1);
        boolean twoPrecedesOne = hasPredecessor(id1, id2);
        if (onePrecedesTwo && twoPrecedesOne) {
            return handleLoop(id1, id2);
        }
        if (onePrecedesTwo) {
            return -1;
        }
        if (twoPrecedesOne) {
            return 1;
        }
        return 0;
    }


    private boolean hasPredecessor(String id1, String id2) {
        for (Triple t : _predecessorList) {
            String s = t.getSubject();
            String o = t.getObject();
            if (s != null && s.equals(id1) && o != null && o.equals(id2)) {
                return true;
            }
        }
        return false;
    }


    private int handleLoop(String id1, String id2) {
        if (_flowsIntoList == null) {
            _flowsIntoList = OntologyHandler.swrlQuery("flowsInto");
            _netToInputCondition = new HashMap<String, String>();
        }
        return getShortestPath(id1) - getShortestPath(id2);
    }


    private int getShortestPath(String taskID) {
        String netID = getNetForTask(taskID);
        if (netID != null) {
            String icID = getInputCondition(netID);
            if (icID != null) {
                Set<String> sources = new HashSet<String>();
                sources.add(icID);
                return getShortestPath(sources, taskID, 1);
            }
        }
        return 0;
    }


    private int getShortestPath(Set<String> sources, String target, int count) {
        Set<String> nextSet = getFlowsInto(sources);
        if (nextSet.contains(target)) {
            return count;
        }
        return getShortestPath(nextSet, target, ++count);
    }


    private Set<String> getFlowsInto(Set<String> sourceSet) {
        Set<String> fromSet = new HashSet<String>();
        for (String source : sourceSet) {
            for (Triple t : _flowsIntoList) {
                if (t.getSubject().equals(source)) {
                    fromSet.add(t.getObject());
                }
            }
        }
        return fromSet;
    }



    private String getInputCondition(String netID) {
        String icID = _netToInputCondition.get(netID);
        if (icID == null) {
            icID = query(netID, "hasInputCondition", null);
            _netToInputCondition.put(netID, icID);
        }
        return icID;
    }


    private String getNetForTask(String id) {
        return query(null,"hasExternalNetElement", id);
    }


    private String query(String s, String p, String o) {
        List<Triple> triples = OntologyHandler.swrlQuery(s, p, o);
        if (!triples.isEmpty()) {
            if (s == null) return triples.get(0).getSubject();
            if (o == null) return triples.get(0).getObject();
        }
        return null;
    }

}
