package org.yawlfoundation.yawl.views;

import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;

/**
 * @author Michael Adams
 * @date 20/10/2016
 */
public class ChangeListener implements UndoableEditListener {

    public ChangeListener() {
    }


    @Override
    public void undoableEditHappened(UndoableEditEvent e) {
        System.out.println("change happened");
    }
}
