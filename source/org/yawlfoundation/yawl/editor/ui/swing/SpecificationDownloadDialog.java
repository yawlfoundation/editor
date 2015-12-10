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

package org.yawlfoundation.yawl.editor.ui.swing;

import org.yawlfoundation.yawl.editor.core.YConnector;
import org.yawlfoundation.yawl.editor.ui.specification.io.LayoutRepository;
import org.yawlfoundation.yawl.editor.ui.specification.io.SpecificationReader;
import org.yawlfoundation.yawl.engine.YSpecificationID;
import org.yawlfoundation.yawl.engine.interfce.SpecificationData;
import org.yawlfoundation.yawl.util.StringUtil;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * @author Michael Adams
 * @date 18/10/13
 */
public class SpecificationDownloadDialog extends AbstractDownloadDialog {

    public SpecificationDownloadDialog() {
        super();
        setTitle("Available Specifications");
    }


    @Override
    protected JList getList() {
        try {
            java.util.List<YSpecificationID> idList = new ArrayList<YSpecificationID>();
            for (SpecificationData specData : YConnector.getLoadedSpecificationList()) {
                YSpecificationID specID = specData.getID();
                idList.add(specID);
            }
            sortSpecificationsList(idList);
            return new JList(new SpecificationIDListModel(idList));
        }
        catch (IOException ioe) {
            showError("Failed to get list of specifications from the YAWL Engine: ", ioe);
        }
        return new JList();
    }


    @Override
    protected SpecificationReader getSpecificationReader(YSpecificationID specID,
                                                         String specXML) {
        return new SpecificationReader(specXML, getSpecificationLayout(specID));
    }

    @Override
    protected String getSelectedSpecification(YSpecificationID specID) throws IOException {
        String specXML = YConnector.getSpecification(specID);
        if (specXML.startsWith("<fail")) {
            throw new IOException(StringUtil.unwrap(specXML));
        }
        return specXML;
    }


    @Override
    protected String getSourceString() { return "YAWL Engine"; }


    protected void sortSpecificationsList(java.util.List<YSpecificationID> list) {
        Collections.sort(list, new Comparator<YSpecificationID>() {
            public int compare(YSpecificationID o1, YSpecificationID o2) {
                return o1.toString().compareTo(o2.toString());
            }
        });
    }


    private String getSpecificationLayout(YSpecificationID specID) {
            return LayoutRepository.getInstance().get(specID);
    }


    /**************************************************************************/

    class SpecificationIDListModel extends SpecificationListModel {

        protected SpecificationIDListModel(java.util.List<YSpecificationID> items) {
            super(items);
        }

        public YSpecificationID getSelectedID(int index) {
            return (YSpecificationID) items.get(index);
        }
    }


}
