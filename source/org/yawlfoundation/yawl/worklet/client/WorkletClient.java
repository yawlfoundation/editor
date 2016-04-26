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
import org.yawlfoundation.yawl.worklet.rdrutil.RdrResult;
import org.yawlfoundation.yawl.worklet.selection.WorkletRunner;
import org.yawlfoundation.yawl.worklet.settings.SettingsStore;
import org.yawlfoundation.yawl.worklet.support.WorkletGatewayClient;
import org.yawlfoundation.yawl.worklet.support.WorkletInfo;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * @author Michael Adams
 * @date 6/03/15
 */
public class WorkletClient extends YConnection {

    private static final ClientCache CACHE = new ClientCache();
    private static final WorkletInfoCache _workletInfoCache = new WorkletInfoCache();
    private static final WorkletClient INSTANCE = new WorkletClient();
    private static final String URL_FILE_PATH = "/workletService/gateway";

    private String _userid;
    private String _password;
    private String _connectionError;
    private WorkletGatewayClient _client;
    private XNodeParser _xNodeParser;
    private TaskIDChangeMap _taskIdChanges;


    private WorkletClient() {
        super();
        _userid = SettingsStore.getServiceUserId();
        _password = SettingsStore.getServicePassword();
        setURL();
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
        setURL(host, port);
        boolean test = isConnected();

        // restore current
        setUserID(id);
        setPassword(pw);
        setURL(url);

        return test;
    }


    @Override
    protected void init() {
        _client = new WorkletGatewayClient(getURL().toExternalForm());
    }

    @Override
    protected String getURLFilePath() { return URL_FILE_PATH; }

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


    private void setURL() {
        String host = SettingsStore.getServiceHost();
        int port = SettingsStore.getServicePort();
        try {
            setURL(host, port);
        }
        catch (Exception e) {
            //
        }
    }


    public void refreshSettings() {
        setUserID(SettingsStore.getServiceUserId());
        setPassword(SettingsStore.getServicePassword());
        setURL();
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
            _connectionError = null;
            if (! client.successful(_handle)) {
                _connectionError = StringUtil.unwrap(_handle);
                _handle = null;
                return false;
            }
        }
        return true;
    }


    public boolean isConnected() { return isConnected(_client); }


    public boolean isConnected(Interface_Client client) {
        boolean connected = false;
        try {
            if (_handle != null) {
                String response = _client.checkConnection(_handle);
                connected = response != null && response.equalsIgnoreCase("true");
            }
            if (! connected) {
                _handle = null;
                connected = connect(client);
            }
        }
        catch (IOException ioe) {
            connected = false;
        }
        return connected;
    }


    public void connect() throws IOException {
        if (!isConnected()) {
            String errMsg = "Unable to connect to Worklet Service";
            if (_connectionError != null) errMsg += ": " + _connectionError;
            throw new IOException(errMsg);
        }
    }


    public boolean successful(String msg) { return _client.successful(msg); }


    public void clearCache() { CACHE.clearAll(); }


    public WorkletInfoCache getWorkletCache() { return _workletInfoCache; }


    public void setTaskIdChangeMap(TaskIDChangeMap map) { _taskIdChanges = map; }

    public TaskIDChangeMap getTaskIdChangeMap() { return _taskIdChanges; }


    public String getUpdatedTaskID(String taskID) {
        return _taskIdChanges != null ? _taskIdChanges.getID(taskID) : taskID;
    }

    public String getOldTaskID(String taskID) {
        return _taskIdChanges != null ? _taskIdChanges.getOldID(taskID) : taskID;
    }


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
        return getWorkletInfoList(_client.getWorkletInfoList(_handle));
    }


    public boolean addRule(YSpecificationID specID, String taskID, RuleType rule,
                          RdrNode node) throws IOException {
        connect();
        check(_client.addNode(specID, getOldTaskID(taskID), rule, node, _handle));
        return true;
    }


    public RdrResult removeRule(YSpecificationID specID, String taskID, RuleType rule,
                                RdrNode node) throws IOException {
        connect();
        String result = _client.removeNode(specID, getOldTaskID(taskID), rule,
                node.getNodeId(), _handle);
        check(result);
        return getRdrResult(result);
    }


    public boolean addRuleSet(YSpecificationID specID, String xml) throws IOException {
        connect();
        check(_client.addRdrSet(specID, xml, _handle));
        return true;
    }


    public boolean addRuleSet(String processName, String xml) throws IOException {
        connect();
        check(_client.addRdrSet(processName, xml, _handle));
        return true;
    }


    public boolean addWorklet(YSpecification worklet) throws IOException {
        return addWorklet(worklet.getSpecificationID(), YMarshal.marshal(worklet));
    }


    public boolean addWorklet(YSpecificationID specID, String workletXML)
            throws IOException {
        connect();
        check(_client.addWorklet(specID, workletXML, _handle));
        _workletInfoCache.invalidate();
        return true;
    }


    // selection worklets
    public String replaceWorklet(String itemID) throws IOException {
        connect();
        String casesStarted = _client.replaceWorklet(itemID, _handle);
        check(casesStarted);
        return casesStarted;
    }


    // exception worklets
    public String replaceWorklet(String caseID, String itemID, RuleType ruleType)
            throws IOException {
        connect();
        String casesStarted = _client.replaceWorklet(caseID, itemID, ruleType, _handle);
        check(casesStarted);
        return casesStarted;
    }


    public String getWorklet(YSpecificationID specID) throws IOException {
        connect();
        String workletXML = _client.getWorklet(specID, _handle);
        check(workletXML);
        return workletXML;
    }


    public List<WorkletInfo> getOrphanedWorklets() throws IOException {
        connect();
        return getWorkletInfoList(_client.getOrphanedWorklets(_handle));
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


    public List<RdrSetID> getRdrSetIDs() throws IOException {
        connect();
        XNode root = toXNode(_client.getRdrSetIDs(_handle));
        List<RdrSetID> idList = new ArrayList<RdrSetID>();
        for (XNode node : root.getChildren()) {
            RdrSetID rdrSetID;
            String id = node.getText();
            if (id.contains(":")) {
                YSpecificationID specID = new YSpecificationID();
                specID.fromFullString(id);
                rdrSetID = new RdrSetID(specID);
            }
            else {
                rdrSetID = new RdrSetID(id);                      // process name
            }
            idList.add(rdrSetID);
        }
        Collections.sort(idList);

        return idList;
    }


    public void removeRdrSet(RdrSetID rdrSetID) throws IOException {
        connect();
        check(_client.removeRdrSet(rdrSetID.getIdentifier(), _handle));
    }


    public void removeWorklet(String specKey) throws IOException {
        connect();
        check(_client.removeWorklet(specKey, _handle));
    }


    public List<String> loadFile(String path, String type) throws IOException {
        connect();
        String result = _client.loadFile(path, type, _handle);
        if (successful(result)) {
            return Collections.emptyList();
        }
        else {
            List<String> errors = new ArrayList<String>();
            XNode root = getXNodeParser().parse(result);
            for (XNode errorNode : root.getChild("errors").getChildren()) {
                 errors.add(errorNode.getText());
            }
            return errors;
        }
    }


    public boolean updateRdrSetTaskIDs(YSpecificationID specID, Map<String, String> updates)
            throws IOException {
        connect();
        return successful(_client.updateRdrSetTaskIDs(specID, updates, _handle));
    }


    // only used by replace dialog, so we know the engine has the spec loaded
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


    // only used by replace dialog, so we know the engine has the spec loaded
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


    private List<WorkletInfo> getWorkletInfoList(String xml) throws IOException {
        XNode root = toXNode(xml);
        List<WorkletInfo> workletList = new ArrayList<WorkletInfo>();
        for (XNode node : root.getChildren()) {
            workletList.add(new WorkletInfo(node));
        }
        Collections.sort(workletList);
        return workletList;
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


    private RdrResult getRdrResult(String value) {
        try {
            return RdrResult.valueOf(value);
        }
        catch (IllegalArgumentException iae) {
            return RdrResult.Unknown;
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
