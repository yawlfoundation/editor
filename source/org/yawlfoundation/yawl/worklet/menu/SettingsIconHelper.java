package org.yawlfoundation.yawl.worklet.menu;

import org.yawlfoundation.yawl.worklet.client.WorkletClient;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

/**
 * @author Michael Adams
 * @date 5/05/2016
 */
public class SettingsIconHelper {

    private static JMenuItem _item;
    private static JButton _button;
    private static boolean _connectionStatus;

    private static final ImageIcon VALID_ICON = getWorkletMenuIcon("gear");
    private static final ImageIcon INVALID_ICON = getWorkletMenuIcon("gearError");


    static {
        _connectionStatus = true;
        startHeartbeat();
    }


    static void setItem(JMenu menu) {
        _item = menu.getItem(menu.getItemCount() - 1);
    }


    static void setButton(JToolBar toolBar) {
        _button = (JButton) toolBar.getComponent(toolBar.getComponentCount() - 1);
    }


    public static void checkConnection() {
        new ConnectionChecker().execute();
    }


    private static ImageIcon getWorkletMenuIcon(String name) {
        URL url = SettingsIconHelper.class.getResource("icon/" + name + ".png");
        return url != null ? new ImageIcon(url) : null;
    }


    // start a timer to check the service connection every 10 seconds
    private static void startHeartbeat() {
        Timer timer = new Timer(10000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkConnection();
            }
        });
        timer.setInitialDelay(500);
        timer.start();
    }


    /********************************************************************************/

    static class ConnectionChecker extends SwingWorker<Void, Integer> {

        protected Void doInBackground() throws Exception {
            try {
                WorkletClient.getInstance().connect();
                setIcon(true);
            }
            catch (Exception e) {
                setIcon(false);
            }
            return null;
        }


        // change icon when there has been a change in connection status
        private void setIcon(boolean isConnected) {
            if (_connectionStatus != isConnected) {
                ImageIcon icon = isConnected ? VALID_ICON : INVALID_ICON;
                _item.setIcon(icon);
                _button.setIcon(icon);
                _connectionStatus = isConnected;
            }
        }

    }

}
