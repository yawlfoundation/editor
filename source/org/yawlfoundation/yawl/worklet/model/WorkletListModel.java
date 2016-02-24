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
import org.yawlfoundation.yawl.util.StringUtil;
import org.yawlfoundation.yawl.worklet.client.WorkletClient;
import org.yawlfoundation.yawl.worklet.support.WorkletInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Michael Adams
 * @date 21/06/13
 */
public class WorkletListModel extends AbstractResourceListModel {

    private final List<WorkletInfo> fullList;
    private List<WorkletInfo> filteredList;

    private int filterLength;


    public WorkletListModel() {
        fullList = getWorkletList();
        filteredList = new ArrayList<WorkletInfo>(fullList);
        filterLength = 0;
    }


    public int getSize() { return filteredList.size(); }


    public Object getElementAt(int i) {
        return filteredList.get(i).toString();
    }


    public void filter(String chars) {
        if (chars.length() == 0) {
            filteredList = new ArrayList<WorkletInfo>(fullList);
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


    public int[] getIndicesFor(Object o) {
        if (o != null) {
            List<String> selections = StringUtil.splitToList((String) o, ";");
            if (! selections.isEmpty()) {
                int[] indices = new int[selections.size()];
                int index = 0;
                for (int i=0; i < fullList.size(); i++) {
                    if (selections.contains(fullList.get(i).getSpecID().getKey())) {
                        indices[index++] = i;
                    }
                }
                return indices;
            }
        }
        return new int[0];
    }


    private List<WorkletInfo> filter(List<WorkletInfo> list, String chars) {
        List<WorkletInfo> filtered = new ArrayList<WorkletInfo>();
        String mask = chars.toLowerCase();
        for (WorkletInfo workletInfo : list) {
            String uri = workletInfo.getSpecID().getUri();
            if (uri.toLowerCase().contains(mask)) {
                filtered.add(workletInfo);
            }
        }
        return filtered;
    }


    private List<WorkletInfo> getWorkletList() {
        return WorkletClient.getInstance().getWorkletCache().getWorkletList();
    }
}
