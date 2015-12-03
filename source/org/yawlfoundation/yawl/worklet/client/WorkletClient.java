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

import org.yawlfoundation.yawl.editor.core.connection.YConnection;
import org.yawlfoundation.yawl.elements.YSpecification;
import org.yawlfoundation.yawl.engine.YSpecificationID;
import org.yawlfoundation.yawl.engine.interfce.Interface_Client;
import org.yawlfoundation.yawl.unmarshal.YMarshal;
import org.yawlfoundation.yawl.util.StringUtil;
import org.yawlfoundation.yawl.util.XNode;
import org.yawlfoundation.yawl.util.XNodeParser;
import org.yawlfoundation.yawl.worklet.rdr.RdrNode;
import org.yawlfoundation.yawl.worklet.rdr.RuleType;
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

    private static final String DEFAULT_USERID = "editor";
    private static final String DEFAULT_PASSWORD = "yEditor";

    private String _userid = DEFAULT_USERID;
    private String _password = DEFAULT_PASSWORD;
    private WorkletGatewayClient _client;
    private XNodeParser _xNodeParser;


    public WorkletClient() {
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


    // to test connection params
    public WorkletClient(String host, int port, String userId, String password)
            throws MalformedURLException {
        super();
        setUserID(userId);
        setPassword(password);
        setURL(new URL("http", host, port, "workletService/gateway"));
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


    public boolean isConnected() { return isConnected(_client); }


    public boolean successful(String msg) { return _client.successful(msg); }


    /********************************************************************************/

    public Vector<String> getWorkletList() throws IOException {
        if (isConnected()) {
            String xml = _client.getWorkletNames(_handle);
            if (_client.successful(xml)) {
                XNode node = getXNodeParser().parse(xml);
                if (node != null) {
                    Vector<String> names = new Vector<String>();
                    for (XNode child : node.getChildren()) {
                        names.add(child.getText());
                    }
                    Collections.sort(names);
                    return names;
                }
                else throw new IOException("Malformed data returned from service");
            }
            else throw new IOException(StringUtil.unwrap(xml));
        }
        else throw new IOException("Unable to connect to Worklet Service");
    }


    public List<WorkletInfo> getWorkletInfoList() throws IOException {
        if (isConnected()) {
            XNode root = toXNode(_client.getWorkletInfoList(_handle));
            List<WorkletInfo> workletList = new ArrayList<WorkletInfo>();
            for (XNode node : root.getChildren()) {
                workletList.add(new WorkletInfo(node));
            }
            Collections.sort(workletList);
            return workletList;
        }
        else throw new IOException("Unable to connect to Worklet Service");
    }


    public String addRule(YSpecificationID specID, String taskID, RuleType rule,
                          RdrNode node) throws IOException {
        if (isConnected()) {
            return _client.addNode(specID, taskID, rule, node, _handle);
        }
        throw new IOException("Unable to connect to Worklet Service");
    }


    public boolean addRuleSet(YSpecificationID specID, String xml) throws IOException {
        if (isConnected()) {
            String msg = _client.addRdrSet(specID, xml, _handle);
            if (! successful(msg)) {
                throw new IOException(StringUtil.unwrap(msg));
            }
            return true;
        }
        throw new IOException("Unable to connect to Worklet Service");
    }


    public boolean addWorklet(YSpecification worklet) throws IOException {
        return addWorklet(worklet.getSpecificationID(), YMarshal.marshal(worklet));
    }


    public boolean addWorklet(YSpecificationID specID, String workletXML)
            throws IOException {
        if (isConnected()) {
            String msg = _client.addWorklet(specID, workletXML, _handle);
            if (! successful(msg)) {
                throw new IOException(StringUtil.unwrap(msg));
            }
            return true;
        }
        throw new IOException("Unable to connect to Worklet Service");
    }


    public String getWorklet(YSpecificationID specID) throws IOException {
        if (isConnected()) {
            return _client.getWorklet(specID, _handle);
        }
        throw new IOException("Unable to connect to Worklet Service");
    }


    public String getRdrSet(YSpecificationID specID) throws IOException {
        if (isConnected()) {
            return _client.getRdrSet(specID, _handle);
        }
        throw new IOException("Unable to connect to Worklet Service");
    }


    private XNodeParser getXNodeParser() {
        if (_xNodeParser == null) {
            _xNodeParser = new XNodeParser();
        }
        return _xNodeParser;
    }


    private XNode toXNode(String result) throws IOException {
        if (!successful(result)) {
            throw new IOException(StringUtil.unwrap(result));
        }
        XNode root = getXNodeParser().parse(result);
        if (root == null) {
            throw new IOException("Malformed result string:" + result);
        }
        return root;
    }



    /**************************************************************************/

    class SpecIdComparator implements Comparator<YSpecificationID> {

         public int compare(YSpecificationID s1, YSpecificationID s2) {
             if (s1 == null) return -1;
             if (s2 == null) return 1;
             return s1.getUri().compareToIgnoreCase(s2.getUri());
        }
    }

}
