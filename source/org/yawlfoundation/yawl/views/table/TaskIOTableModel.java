/*
 * Copyright (c) 2004-2013 The YAWL Foundation. All rights reserved.
 * The YAWL Foundation is a collaboration of individuals and
 * organisations who are committed to improving workflow technology.
 *
 * This file is part of YAWL. YAWL is free software: you can
 * redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation.
 *
 * YAWL is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General
 * Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with YAWL. If not, see <http://www.gnu.org/licenses/>.
 */

package org.yawlfoundation.yawl.views.table;

import org.yawlfoundation.yawl.editor.ui.specification.SpecificationModel;
import org.yawlfoundation.yawl.views.ontology.OntologyHandler;
import org.yawlfoundation.yawl.views.ontology.Triple;
import org.yawlfoundation.yawl.views.util.FlowsComparator;

import javax.swing.table.DefaultTableModel;
import java.util.*;

public class TaskIOTableModel extends DefaultTableModel {

    private List<TaskIO> _taskIOList;
    private List<String> _varList;


    public TaskIOTableModel() {
        super();
        populate();
    }


    public int getColumnCount() {
        return _varList.size();
    }

    public String getColumnName(int col) {
        return _varList.get(col);
    }

    public Class getColumnClass(int columnIndex) {
        return String.class;
    }

    public boolean isCellEditable(int row, int column) {
        return false;
    }

    public int getRowCount() {
        return _taskIOList != null ? _taskIOList.size() : 0;
    }


    public Object getValueAt(int row, int col) {
        return _taskIOList.get(row).getValue(_varList.get(col));
    }


    public String getRowHeaderValueAt(int row) {
        return _taskIOList.get(row).getID();
    }


    public int getWidestLabel() {
        int max = 0;
        for (TaskIO tio : _taskIOList) {
            if (max < tio.getID().length()) {
                max = tio.getID().length();
            }
        }
        return max;
    }

    private void populate() {
        if (!OntologyHandler.isLoaded()) {
            OntologyHandler.load(SpecificationModel.getHandler());
        }
        List<String> netNames = getNamesByType("YNet");
        populateVars(netNames);
        populateTasks();
    }


    private void populateVars(List<String> netNames) {
        _varList = new ArrayList<String>();
        for (String netName : netNames) {
             _varList.addAll(getNetVars(netName));
        }
    }


    private List<String> getNamesByType(String object) {
        List<String> names = new ArrayList<String>();
        List<Triple> triples = OntologyHandler.swrlQuery(null,
                "http://www.w3.org/1999/02/22-rdf-syntax-ns#type", object);
        for (Triple triple : triples) {
            names.add(triple.getSubject());
        }
        return names;
    }


    private List<String> getNetVars(String netName) {
        List<String> vars = new ArrayList<String>();
        getObjects(vars, netName, "hasLocalVariable");
        getObjects(vars, netName, "hasInputParameter");
        getObjects(vars, netName, "hasOutputParameter");
        return vars;
    }


    private void getObjects(List<String> vars, String netName, String predicate) {
        List<Triple> triples = OntologyHandler.swrlQuery(netName, predicate, null);
        for (Triple triple : triples) {
            vars.add(triple.getObject());
        }
    }


    private void populateTasks() {
        _taskIOList = new ArrayList<TaskIO>();
        List<String> taskIDs = getNamesByType("YTask");
        Collections.sort(taskIDs, new FlowsComparator());
        List<Triple> readsFrom = OntologyHandler.swrlQuery("readsDataFrom");
        List<Triple> writesTo = OntologyHandler.swrlQuery("writesDataTo");
        for (String taskID : taskIDs) {
            Set<String> reads = getVars(readsFrom, taskID);
            Set<String> writes = getVars(writesTo, taskID);
            _taskIOList.add(new TaskIO(taskID, reads, writes));
        }
    }


    private Set<String> getVars(List<Triple> triples, String id) {
        Set<String> vars = new HashSet<String>();
        for (Triple triple : triples) {
            if (triple.getSubject().equals(id)) {
                vars.add(triple.getObject());
            }
        }
        return vars;
    }

}