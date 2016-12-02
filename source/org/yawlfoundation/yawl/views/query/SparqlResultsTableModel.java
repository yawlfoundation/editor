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

package org.yawlfoundation.yawl.views.query;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Resource;

import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;

public class SparqlResultsTableModel extends DefaultTableModel {

    private List<ResultRow> _rows;
    private List<String> _colNames;
    private boolean _suppressNamespaces;


    public SparqlResultsTableModel() {
        super();
    }

    public void setValues(ResultSet resultSet) {
        _colNames = resultSet.getResultVars();
        _rows = new ArrayList<ResultRow>();
        for (; resultSet.hasNext(); ) {
            QuerySolution soln = resultSet.nextSolution();
            ResultRow row = new ResultRow();
            for (String colName : _colNames) {
                Resource resource = soln.getResource(colName);
                if (resource != null) {
                    row.add(soln.getResource(colName).toString());
                }
            }
            _rows.add(row);
        }
        fireTableStructureChanged();
    }


    public void clear() {
        _colNames = null;
        _rows = null;
        fireTableStructureChanged();
    }


    public void setSuppressNamespaces(boolean suppress) {
        _suppressNamespaces = suppress;
        fireTableDataChanged();
    }


    public int getColumnCount() {
        return _colNames != null ? _colNames.size() : 0;
    }

    public String getColumnName(int index) {
        return _colNames != null ? _colNames.get(index) : null;
    }

    public Class getColumnClass(int columnIndex) {
        return String.class;
    }

    public boolean isCellEditable(int row, int column) {
        return false;
    }

    public int getRowCount() {
        return _rows != null ? _rows.size() : 0;
    }


    public Object getValueAt(int row, int col) {
        return _rows != null ? _rows.get(row).getValue(col) : null;
    }


    /***********************************************************************/

    class ResultRow {

        List<String> values = new ArrayList<String>();

        ResultRow() { }

        void add(String value) { values.add(value); }

        String getValue(int index) {
            if (index < values.size()) {
                String value = values.get(index);
                if (_suppressNamespaces) {
                    int endOfNS = value.indexOf('#');
                    if (endOfNS > -1) {
                        value = value.substring(endOfNS + 1);
                    }
                }
                return value;
            }
            return null;
        }
    }

}