package org.yawlfoundation.yawl.editor.ui.util;

/**
 * @author Michael Adams
 * @date 19/10/2015
 */
public class Pauser {

    private static final int WAIT_TIME = 50;
    private static final Object LOCK = new Object();

    public static synchronized void pause(long milliseconds) {
        long now = System.currentTimeMillis();
        long finishTime = now + milliseconds;
        synchronized (LOCK) {
            while (now < finishTime) {
                try {
                    LOCK.wait(WAIT_TIME);
                }
                catch (InterruptedException ignore) {
                    // fall through
                }
                now = System.currentTimeMillis();
            }
        }
    }

}
