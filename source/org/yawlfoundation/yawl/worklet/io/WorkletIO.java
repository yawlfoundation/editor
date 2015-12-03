package org.yawlfoundation.yawl.worklet.io;

import org.yawlfoundation.yawl.editor.ui.specification.SpecificationModel;
import org.yawlfoundation.yawl.elements.YSpecification;
import org.yawlfoundation.yawl.engine.YSpecificationID;
import org.yawlfoundation.yawl.util.StringUtil;
import org.yawlfoundation.yawl.worklet.client.WorkletClient;

import java.io.IOException;

/**
 * @author Michael Adams
 * @date 25/11/2015
 */
public class WorkletIO {

    private final WorkletClient CLIENT;


    public WorkletIO() { CLIENT = new WorkletClient(); }


    public boolean put() throws IOException {
        YSpecification currentSpec = SpecificationModel.getHandler().getSpecification();
        return currentSpec != null && CLIENT.addWorklet(currentSpec);
    }


    public String get(YSpecificationID specID) throws IOException {
        String xml = CLIENT.getWorklet(specID);
        if (CLIENT.successful(xml)) {
            return xml;
        }
        throw new IOException(StringUtil.unwrap(xml));   // error message
    }


    public boolean loadRules(YSpecificationID specID, String xml) throws IOException {
        return CLIENT.addRuleSet(specID, xml);
    }

}
