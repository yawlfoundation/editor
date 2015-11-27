/*
 * Copyright (c) 2004-2015 The YAWL Foundation. All rights reserved.
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

package org.yawlfoundation.yawl.worklet.model;

import org.yawlfoundation.yawl.editor.ui.resourcing.listmodel.AbstractResourceListModel;
import org.yawlfoundation.yawl.engine.YSpecificationID;
import org.yawlfoundation.yawl.worklet.client.WorkletClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Michael Adams
 * @date 21/06/13
 */
public class WorkletListModel extends AbstractResourceListModel {

    private final List<YSpecificationID> fullList;
    private List<YSpecificationID> filteredList;

    private int filterLength;


    public WorkletListModel() {
        fullList = getWorkletList();
        filteredList = new ArrayList<YSpecificationID>(fullList);
        filterLength = 0;
    }


    public int getSize() { return filteredList.size(); }


    public Object getElementAt(int i) {
        return filteredList.get(i).getUri();
    }


    public void filter(String chars) {
        if (chars.length() == 0) {
            filteredList = new ArrayList<YSpecificationID>(fullList);
        }
        else if (chars.length() > filterLength) {
            filteredList = filter(filteredList, chars);
        }
        else if (chars.length() < filterLength) {
            filteredList = filter(fullList, chars);
        }

        filterLength = chars.length();
        fireContentsChanged(this, 0, filteredList.size());
    }


    public List<Object> getSelections(int[] selectedIndices) {
        List<Object> selections = new ArrayList<Object>();
        for (int index : selectedIndices) {
                selections.add(filteredList.get(index));
        }
        return selections;
    }


    private List<YSpecificationID> filter(List<YSpecificationID> list, String chars) {
        List<YSpecificationID> filtered = new ArrayList<YSpecificationID>();
        String mask = chars.toLowerCase();
        for (YSpecificationID specID : list) {
            String uri = specID.getUri();
            if (uri.toLowerCase().contains(mask)) {
                filtered.add(specID);
            }
        }
        return filtered;
    }


    private List<YSpecificationID> getWorkletList() {
        try {
            return new WorkletClient().getWorkletIdList();
        }
        catch (IOException ioe) {
            return Collections.emptyList();
        }
    }
}
