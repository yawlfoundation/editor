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

package org.yawlfoundation.yawl.worklet.dialog;

import org.yawlfoundation.yawl.editor.ui.specification.io.SpecificationReader;
import org.yawlfoundation.yawl.editor.ui.swing.AbstractDownloadDialog;
import org.yawlfoundation.yawl.editor.ui.swing.SpecificationListModel;
import org.yawlfoundation.yawl.engine.YSpecificationID;
import org.yawlfoundation.yawl.worklet.client.WorkletClient;
import org.yawlfoundation.yawl.worklet.support.WorkletInfo;

import javax.swing.*;
import java.io.IOException;

/**
 * @author Michael Adams
 * @date 27/11/15
 */
public class WorkletLoadDialog extends AbstractDownloadDialog {

    private static final WorkletClient CLIENT = WorkletClient.getInstance();

    public WorkletLoadDialog() {
        super();
        setTitle("Loaded Worklets");
    }


    @Override
    protected JList getList() {
        java.util.List<WorkletInfo> workletList = CLIENT.getWorkletCache().getWorkletList();
        return new JList(new WorkletSpecificationListModel(workletList));
    }


    @Override
    protected SpecificationReader getSpecificationReader(YSpecificationID specID,
                                                         String specXML) {
        return new SpecificationReader(specXML, null);
    }


    @Override
    protected String getSelectedSpecification(YSpecificationID specID) throws IOException {
        return WorkletClient.getInstance().getWorklet(specID);
    }


    @Override
    protected String getSourceString() { return "Worklet Service"; }


    /**************************************************************************/

    class WorkletSpecificationListModel extends SpecificationListModel {

        protected WorkletSpecificationListModel(java.util.List<WorkletInfo> items) {
            super(items);
        }

        public YSpecificationID getSelectedID(int index) {
            return ((WorkletInfo) items.get(index)).getSpecID();
        }
    }

}
