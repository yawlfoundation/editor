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

package org.yawlfoundation.yawl.worklet.client;

import org.yawlfoundation.yawl.editor.core.YConnector;
import org.yawlfoundation.yawl.editor.core.connection.YConnection;
import org.yawlfoundation.yawl.elements.YSpecification;
import org.yawlfoundation.yawl.engine.YSpecificationID;
import org.yawlfoundation.yawl.engine.interfce.Interface_Client;
import org.yawlfoundation.yawl.engine.interfce.Marshaller;
import org.yawlfoundation.yawl.engine.interfce.SpecificationData;
import org.yawlfoundation.yawl.engine.interfce.TaskInformation;
import org.yawlfoundation.yawl.engine.interfce.interfaceB.InterfaceB_EnvironmentBasedClient;
import org.yawlfoundation.yawl.unmarshal.YMarshal;
import org.yawlfoundation.yawl.util.StringUtil;
import org.yawlfoundation.yawl.util.XNode;
import org.yawlfoundation.yawl.util.XNodeParser;
import org.yawlfoundation.yawl.worklet.rdr.RdrNode;
import org.yawlfoundation.yawl.worklet.rdr.RuleType;
import org.yawlfoundation.yawl.worklet.selection.WorkletRunner;
import org.yawlfoundation.yawl.worklet.settings.SettingsStore;
import org.yawlfoundation.yawl.worklet.support.WorkletGatewayClient;
import org.yawlfoundation.yawl.worklet.support.WorkletInfo;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Michael Adams
 * @date 6/03/15
 */
public class WorkletClient extends YConnection {

    private static final String DEFAULT_USERID = "editor";
    private static final String DEFAULT_PASSWORD = "yEditor";

    private static final ClientCache CACHE = new ClientCache();
    private static final WorkletClient INSTANCE = new WorkletClient();

    private String _userid = DEFAULT_USERID;
    private String _password = DEFAULT_PASSWORD;
    private WorkletGatewayClient _client;
    private XNodeParser _xNodeParser;


    private WorkletClient() {
        super();
        String host = SettingsStore.getServiceHost();
        int port = SettingsStore.getServicePort();
        try {
            setURL(new URL("http", host, port, "workletService/gateway"));
        }
        catch (Exception e) {
            //
        }
    }


    public static WorkletClient getInstance() { return INSTANCE; }


    // to test connection params
    public boolean testConnection(String host, int port, String userId, String password)
            throws MalformedURLException {

        // save current
        String id = _userid;
        String pw = _password;
        URL url = getURL();

        // set test params & check
        setUserID(userId);
        setPassword(password);
        setURL(new URL("http", host, port, "workletService/gateway"));
        boolean test = isConnected();

        // restore current
        setUserID(id);
        setPassword(pw);
        setURL(url);

        return test;
    }


    @Override
    protected void init() { _client = new WorkletGatewayClient(); }

    @Override
    protected String getURLFilePath() {
        return null;
    }

    @Override
    protected Interface_Client getClient() { return _client; }



    public void setUserID(String id) {
        if (id != null) {
            _userid = id;
            disconnect();
        }
    }


    public void setPassword(String pw) {
        if (pw != null) {
            _password = pw;
            disconnect();
        }
    }


    public void disconnect() {
        if (_handle != null) {
            try {
                _client.disconnect(_handle);
            }
            catch (IOException ioe) {
                //
            }
        }
        _handle = null;
    }


    protected boolean connect(Interface_Client client) throws IOException {
        super.connect(client);
        if (_handle == null) {
            _handle = _client.connect(_userid, _password);
            if (! client.successful(_handle)) {
                _handle = null;
                return false;
            }
        }
        return true;
    }


    public boolean isConnected() { return isConnected(_client); }


    public boolean isConnected(Interface_Client client) {
        try {
            if (_handle == null) {
                return connect(client);
            }
            else {
                boolean success = _client.successful(_client.checkConnection(_handle));
                if (! success) _handle = null;
                return success;
            }
        }
        catch (IOException ioe) {
            return false;
        }
    }


    public void connect() throws IOException {
        if (!isConnected()) {
            throw new IOException("Unable to connect to Worklet Service");
        }
    }


    public boolean successful(String msg) { return _client.successful(msg); }


    /********************************************************************************/

    public List<WorkletRunner> getRunningWorkletList() throws IOException {
        connect();
        XNode node = toXNode(_client.getRunningWorklets(_handle));
        List<WorkletRunner> runners = new ArrayList<WorkletRunner>();
        for (XNode child : node.getChildren()) {
            runners.add(new WorkletRunner(child));
        }
        Collections.sort(runners, new RunnerCaseIdComparator());
        return runners;
    }


    public List<WorkletInfo> getWorkletInfoList() throws IOException {
        connect();
        XNode root = toXNode(_client.getWorkletInfoList(_handle));
        List<WorkletInfo> workletList = new ArrayList<WorkletInfo>();
        for (XNode node : root.getChildren()) {
            workletList.add(new WorkletInfo(node));
        }
        Collections.sort(workletList);
        return workletList;
    }


    public boolean addRule(YSpecificationID specID, String taskID, RuleType rule,
                          RdrNode node) throws IOException {
        connect();
        check(_client.addNode(specID, taskID, rule, node, _handle));
        return true;
    }


    public boolean addRuleSet(YSpecificationID specID, String xml) throws IOException {
        connect();
        check(_client.addRdrSet(specID, xml, _handle));
        return true;
    }


    public boolean addWorklet(YSpecification worklet) throws IOException {
        return addWorklet(worklet.getSpecificationID(), YMarshal.marshal(worklet));
    }


    public boolean addWorklet(YSpecificationID specID, String workletXML)
            throws IOException {
        connect();
        check(_client.addWorklet(specID, workletXML, _handle));
        return true;
    }


    public String getWorklet(YSpecificationID specID) throws IOException {
        connect();
        String workletXML = _client.getWorklet(specID, _handle);
        check(workletXML);
        return workletXML;
    }


    public RdrNode getRdrNode(long nodeId) throws IOException {
        RdrNode node = CACHE.getNode(nodeId);
        if (node == null) {
            connect();
            node = toRdrNode(_client.getNode(nodeId, _handle));
            CACHE.add(node);
        }
        return node;
    }


    public String getRdrSet(YSpecificationID specID) throws IOException {
        connect();
        String setXML = _client.getRdrSet(specID, _handle);
        check(setXML);
        return setXML;
    }


    public TaskInformation getTaskInfo(YSpecificationID specID, String taskID)
            throws IOException {
        TaskInformation taskInfo = CACHE.getTaskInfo(specID, taskID);
        if (taskInfo == null) {
            InterfaceB_EnvironmentBasedClient ibClient = YConnector.getInterfaceBClient();
            String handle = ibClient.connect(_userid, _password);
            String taskInfoASXML = ibClient.getTaskInformationStr(specID, taskID, handle);
            check(taskInfoASXML);
            taskInfo = ibClient.parseTaskInformation(taskInfoASXML);
            CACHE.add(taskInfo);
        }
        return taskInfo;
    }


    public SpecificationData getSpecificationInfo(YSpecificationID specID)
            throws IOException {
        SpecificationData specData = CACHE.getSpecData(specID);
        if (specData == null) {
            InterfaceB_EnvironmentBasedClient ibClient = YConnector.getInterfaceBClient();
            String handle = ibClient.connect(_userid, _password);
            String specificationXML = ibClient.getSpecification(specID, handle);
            check(specificationXML);
            String wrapped = StringUtil.wrap(specificationXML, "speclist");
            List<SpecificationData> specList =
                    Marshaller.unmarshalSpecificationSummary(wrapped);
            specData = specList.get(0);
            CACHE.add(specData);
        }
        return specData;
    }


    private XNodeParser getXNodeParser() {
        if (_xNodeParser == null) {
            _xNodeParser = new XNodeParser();
        }
        return _xNodeParser;
    }


    private XNode toXNode(String result) throws IOException {
        check(result);
        XNode root = getXNodeParser().parse(result);
        if (root == null) {
            throw new IOException("Malformed result string:" + result);
        }
        return root;
    }


    private RdrNode toRdrNode(String nodeXML) throws IOException {
        check(nodeXML);
        return new RdrNode(nodeXML);
    }


    private void check(String xml) throws IOException {
        if (!successful(xml)) {
            if (StringUtil.isNullOrEmpty(xml)) xml = "General Engine Error";
            while (xml.trim().startsWith("<")) xml = StringUtil.unwrap(xml);
            throw new IOException(xml);
        }
    }



    /**************************************************************************/

    class RunnerCaseIdComparator implements Comparator<WorkletRunner> {

         public int compare(WorkletRunner s1, WorkletRunner s2) {
             if (s1 == null) return -1;
             if (s2 == null) return 1;
             int c1 = StringUtil.strToInt(s1.getCaseID(), 0);
             int c2 = StringUtil.strToInt(s2.getCaseID(), 0);
             return c1 - c2;
        }
    }

}
