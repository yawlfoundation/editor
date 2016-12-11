package org.yawlfoundation.yawl.editor.core.connection;

import org.yawlfoundation.yawl.engine.YSpecificationID;
import org.yawlfoundation.yawl.resourcing.rsInterface.WorkQueueGatewayClient;
import org.yawlfoundation.yawl.resourcing.rsInterface.WorkQueueGatewayClientAdapter;
import org.yawlfoundation.yawl.util.StringUtil;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Michael Adams
 * @date 11/12/16
 */
public class YWorkQueueConnection extends YConnection {

    private WorkQueueGatewayClientAdapter _adapter;

    // default URL parts
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 8080;
    private static final String GATEWAY_PATH = "/resourceService/workqueuegateway";


    // Constructors
    public YWorkQueueConnection() { }

    public YWorkQueueConnection(String host, int port) throws MalformedURLException {
        super();
        setURL(new URL("http", host, port, GATEWAY_PATH));
    }

    public YWorkQueueConnection(String urlStr) { super(urlStr); }

    public YWorkQueueConnection(URL url) { super(url); }


    /**
     * Opens a connection to the resource service. Usually not required to be called
     * directly, as connections are managed internally.
     * @return true if the connection is successfully opened.
     * @throws IOException if there's a problem connection to the service.
     */
    public boolean connect() throws IOException {
        return super.connect(getClient());
    }


    /**
     * Checks that a connection is open. Usually not required to be called
     * directly, as connections are managed internally.
     * @return true if the connection is open.
     */
    public boolean isConnected() {
        return super.isConnected(getClient());
    }


    /**
     * Unloads a specification from the engine.
     * Note: this method is used, rather than the InterfaceAClient one, so that the
     * resource service can perform other cleanup tasks it requires when a
     * specification is removed.
     * @param specID the specification to remove
     * @return success result
     * @throws IOException
     */
    public boolean unloadSpecification(YSpecificationID specID) throws IOException {
        if (isConnected()) {
            String result = _adapter.unloadSpecification(specID, _handle);
            if (! _adapter.successful(result)) {
                throw new IOException(StringUtil.unwrap(result));
            }
            return true;
        }
        throw new IOException("Cannot connect to YAWL Engine");
    }


    /**
     * Called from the super class to do any initialisation tasks
     */
    protected void init() {
        _adapter = new WorkQueueGatewayClientAdapter(_url.toExternalForm());
    }

    protected String getURLFilePath() {
        return GATEWAY_PATH;
    }

    @Override
    public WorkQueueGatewayClient getClient() {
        return _adapter.getClient();
    }

}
